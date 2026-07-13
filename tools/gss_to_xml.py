#!/usr/bin/env python3
"""
Convert Bluetooth SIG GSS (GATT Specification Supplement) YAML characteristic
definitions into the sputnikdev bluetooth-gatt-parser XML dialect.

The SIG retired the machine-readable characteristic XML repository (~2019) in
favour of the GSS, whose field structure lives in a semi-structured YAML: field
types are explicit, but scaling/unit/presence details are encoded as a rigid
mini-grammar inside `description` prose. This script parses that grammar:

    type: uint16                        -> <Format>uint16</Format>
    Represented values: M = 1, d = -2, b = 0
                                        -> <Multiplier>/<DecimalExponent>/<BinaryExponent>
    Unit: org.bluetooth.unit.foo        -> <Unit>org.bluetooth.unit.foo</Unit>
    (also "Base Unit:")
    Minimum: 0.0  / Maximum: 1022.0     -> <Minimum>/<Maximum>
    type: uint16[n] / uint8[N]          -> repeats="0" (to end) / repeats="N"
    type: boolean[8] + fields bit table -> <BitField><Bit>...
    "Present if bit N of Flags ..."     -> <Requirement>CN</Requirement> on Flags enum
    "Refer to X characteristic ..."     -> <Reference>org.bluetooth.characteristic.x</Reference>

Where a field's scaling/unit cannot be extracted, an XML comment
`<!-- REVIEW: ... -->` is emitted next to it and the file is reported as needing
review. Nothing is fabricated: a missing exponent is omitted, not guessed.

Usage:
    gss_to_xml.py --uuids char_uuids.yaml --out <resdir> <id-or-file> [<id-or-file> ...]
    gss_to_xml.py --uuids char_uuids.yaml --out <resdir> --stdin < list-of-ids
GSS files are fetched from the SIG public repo unless a local path is given.
"""
import argparse, os, re, sys, urllib.request
from xml.sax.saxutils import escape
import yaml

GSS_RAW = "https://bitbucket.org/bluetooth-SIG/public/raw/main/gss/{}.yaml"

# ---- prose grammar -----------------------------------------------------------

# M = 1, d = -2, b = 0   (tolerate spaces around the minus: "d = - 2")
_MDB = re.compile(r"M\s*=\s*(-?\s*\d+).*?d\s*=\s*(-?\s*\d+).*?b\s*=\s*(-?\s*\d+)",
                  re.IGNORECASE | re.DOTALL)
_UNIT = re.compile(r"(?:Base\s+Unit|Unit)\s*:\s*(org\.bluetooth\.unit\.[\w.\-]+)",
                   re.IGNORECASE)
_MIN = re.compile(r"Minimum\s*:\s*(-?\d+(?:\.\d+)?)", re.IGNORECASE)
_MAX = re.compile(r"Maximum\s*:\s*(-?\d+(?:\.\d+)?)", re.IGNORECASE)
# "Present if bit N of Flags field is set to 1" (or 0). The polarity matters:
# set-to-1 maps cleanly to a Requirement on that bit; set-to-0 (present when the
# bit is CLEAR) has no direct single-enum equivalent and is left for review.
_PRESENT_BIT = re.compile(
    r"Present if bit\s+(\d+)\s+of\s+(?:the\s+)?(?:[\w ]*?)Flags[^.]*?set to\s+([01])",
    re.IGNORECASE)
_REFER = re.compile(r"Refer to (?:the\s+)?(.+?)\s+characteristic", re.IGNORECASE)
# type suffix: uint16[n]  ->  ('uint16', 'n');  uint8[4] -> ('uint8', '4')
_ARRAY = re.compile(r"^(.*?)\[(\w+)\]$")


def _num(s):
    return s.replace(" ", "")


def strip_array(typ):
    """Return (base_type, repeats) where repeats is None|0|N. 0 == repeat to end."""
    m = _ARRAY.match(typ.strip())
    if not m:
        return typ.strip(), None
    base, count = m.group(1), m.group(2)
    if count.isdigit():
        return base, int(count)
    return base, 0  # symbolic (n, k, ...) => to end of data


def refer_to_id(text):
    """'Electric Current' -> org.bluetooth.characteristic.electric_current"""
    slug = re.sub(r"[^\w]+", "_", text.strip().lower()).strip("_")
    return "org.bluetooth.characteristic." + slug


# ---- field emission ----------------------------------------------------------

def emit_scalar_field(field, review):
    name = _clean_name(field.get("field", ""))
    typ = (field.get("type") or "").strip()
    desc = field.get("description") or ""

    # struct reference ("Refer to X characteristic")
    if typ == "struct":
        rm = _REFER.search(desc)
        if rm:
            return ('    <Field name="%s">\n      <Reference>%s</Reference>\n    </Field>\n'
                    % (escape(name), escape(refer_to_id(rm.group(1)))))
        review.append("struct field '%s' without resolvable reference" % name)
        return ('    <Field name="%s">\n      <Format>struct</Format>\n'
                '      <!-- REVIEW: unresolved struct reference -->\n    </Field>\n' % escape(name))

    base, repeats = strip_array(typ)
    parts = ['    <Field name="%s"%s>' % (
        escape(name), '' if repeats is None else ' repeats="%d"' % repeats)]
    parts.append('      <Format>%s</Format>' % escape(base))

    mdb = _MDB.search(desc)
    if mdb:
        mult, dexp, bexp = (_num(mdb.group(1)), _num(mdb.group(2)), _num(mdb.group(3)))
        if mult != "1":
            parts.append('      <Multiplier>%s</Multiplier>' % mult)
        if dexp != "0":
            parts.append('      <DecimalExponent>%s</DecimalExponent>' % dexp)
        if bexp != "0":
            parts.append('      <BinaryExponent>%s</BinaryExponent>' % bexp)
    unit = _UNIT.search(desc)
    if unit:
        parts.append('      <Unit>%s</Unit>' % escape(unit.group(1)))
    mn = _MIN.search(desc)
    if mn:
        parts.append('      <Minimum>%s</Minimum>' % mn.group(1))
    mx = _MAX.search(desc)
    if mx:
        parts.append('      <Maximum>%s</Maximum>' % mx.group(1))

    # presence condition -> Requirement (the referenced Flags enum carries the label)
    pb = _PRESENT_BIT.search(desc)
    if pb:
        bit, polarity = pb.group(1), pb.group(2)
        if polarity == "1":
            parts.append('      <Requirement>C%s</Requirement>' % bit)
        else:
            # present when the bit is CLEAR: no single-enum requirement expresses this
            review.append("field '%s' present-if-bit%s-clear needs manual requirement" % (name, bit))
            parts.append('      <!-- REVIEW: present only when Flags bit %s == 0 -->' % bit)
    elif re.search(r"Present if", desc, re.IGNORECASE):
        review.append("field '%s' has an unparsed presence condition" % name)
        parts.append('      <!-- REVIEW: unparsed presence condition -->')

    # Only flag a genuine gap: the prose defines a scaling (M/d/b) but no unit came
    # through. Dimensionless enums/booleans/counters legitimately carry neither, so
    # don't cry wolf on those.
    if mdb and not unit:
        review.append("numeric field '%s' has scaling but no unit" % name)
        parts.append('      <!-- REVIEW: scaling present but no unit found in prose -->')
    parts.append('    </Field>')
    return "\n".join(parts) + "\n"


def emit_flags_field(field, fields_meta, review):
    """boolean[8]/Flags -> <BitField> using the fields: bit table."""
    name = _clean_name(field.get("field", ""))
    typ = (field.get("type") or "").strip()
    base, _ = strip_array(typ)
    # width in bits: boolean[8] => 8; uint8 => 8
    m = _ARRAY.match(typ.strip())
    if m and m.group(2).isdigit():
        width = int(m.group(2))
    elif base.startswith("uint"):
        width = int(base[4:] or 8)
    else:
        width = 8
    meta = fields_meta.get(name.lower())
    if not meta:
        review.append("flags field '%s' has no bit table" % name)
        return ('    <Field name="%s">\n      <Format>%dbit</Format>\n'
                '      <!-- REVIEW: flags field without bit definitions -->\n    </Field>\n'
                % (escape(name), width))
    lines = ['    <Field name="%s">' % escape(name), '      <BitField>']
    for v in meta.get("values", []):
        bit = str(v.get("bit", "")).strip()
        if "–" in bit or "-" in bit or not bit.isdigit():
            continue  # RFU / ranges
        lines.append('        <Bit index="%s" size="1" name="C%s">' % (bit, bit))
        lines.append('          <Enumerations>')
        lines.append('            <Enumeration key="1" requires="C%s"/>' % bit)
        lines.append('          </Enumerations>')
        lines.append('        </Bit>')
    lines.append('      </BitField>')
    lines.append('    </Field>')
    return "\n".join(lines) + "\n"


def is_flags(field):
    name = (field.get("field") or "").strip().lower()
    typ = (field.get("type") or "").strip().lower()
    return name == "flags" or typ.startswith("boolean[")


# ---- characteristic ----------------------------------------------------------

def convert(gss, ident, uuid, review):
    c = gss["characteristic"]
    name = c.get("name", ident)
    structure = c.get("structure", []) or []
    fields_meta = {f.get("name", "").strip().lower(): f for f in (c.get("fields") or [])}

    body = ['<?xml version="1.0" encoding="UTF-8"?>',
            '<Characteristic xsi:noNamespaceSchemaLocation="http://schemas.bluetooth.org/Documents/characteristic.xsd"',
            '                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"',
            '                name="%s" type="%s" uuid="%s" approved="No">' % (
                escape(_clean_name(name)), ident, uuid),
            '  <Value>']
    for f in structure:
        if is_flags(f):
            body.append(emit_flags_field(f, fields_meta, review).rstrip("\n"))
        else:
            body.append(emit_scalar_field(f, review).rstrip("\n"))
    body.append('  </Value>')
    body.append('</Characteristic>')
    return "\n".join(body) + "\n"


def _clean_name(name):
    # GSS uses LaTeX bits like CO\textsubscript{2}; flatten to plain text.
    name = re.sub(r"\\textsubscript\{(\w+)\}", r"\1", name)
    name = re.sub(r"\\textellipsis", "...", name)
    return name.strip()


# ---- driver ------------------------------------------------------------------

def load_uuid_map(path):
    with open(path) as f:
        data = yaml.safe_load(f)
    m = {}
    for e in data.get("uuids", []):
        ident = e.get("id")
        uuid = e.get("uuid")
        if ident and uuid is not None:
            m[ident] = ("%04X" % uuid) if isinstance(uuid, int) else str(uuid)
    return m


def load_gss(spec):
    if os.path.exists(spec):
        with open(spec) as f:
            return yaml.safe_load(f)
    ident = spec if spec.startswith("org.bluetooth") else "org.bluetooth.characteristic." + spec
    data = urllib.request.urlopen(GSS_RAW.format(ident), timeout=20).read()
    return yaml.safe_load(data)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--uuids", required=True, help="characteristic_uuids.yaml")
    ap.add_argument("--out", required=True, help="target characteristic resource dir")
    ap.add_argument("--stdin", action="store_true", help="read ids from stdin (one per line)")
    ap.add_argument("specs", nargs="*")
    args = ap.parse_args()

    uuid_map = load_uuid_map(args.uuids)
    specs = list(args.specs)
    if args.stdin:
        specs += [l.strip() for l in sys.stdin if l.strip()]

    written, flagged, failed = [], [], []
    for spec in specs:
        try:
            gss = load_gss(spec)
            ident = gss["characteristic"]["identifier"]
            uuid = uuid_map.get(ident)
            if not uuid:
                failed.append((spec, "no uuid in directory for %s" % ident)); continue
            review = []
            xml = convert(gss, ident, uuid, review)
            out = os.path.join(args.out, ident + ".xml")
            with open(out, "w") as f:
                f.write(xml)
            written.append((ident, uuid, len(review)))
            if review:
                flagged.append((ident, review))
        except Exception as e:
            failed.append((spec, repr(e)))

    print("== written: %d (%d need review), failed: %d ==" % (
        len(written), len(flagged), len(failed)))
    for ident, uuid, n in written:
        tag = "  (%d REVIEW)" % n if n else ""
        print("  OK  %-60s %s%s" % (ident, uuid, tag))
    for spec, err in failed:
        print("  FAIL %-58s %s" % (spec, err))
    if flagged:
        print("\n-- review detail --")
        for ident, items in flagged:
            print("  %s" % ident)
            for it in items:
                print("      - %s" % it)
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
