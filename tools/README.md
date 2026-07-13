# tools

Utilities for maintaining the bundled GATT specification set under
`src/main/resources/gatt/`.

## Background

The library ships one XML file per GATT characteristic/service. These originate
from the Bluetooth SIG's old machine-readable characteristic XML repository,
which the SIG **retired around 2019**. Since then the SIG publishes:

- **UUID directory** — `assigned_numbers/uuids/*.yaml` (UUID ↔ name ↔ id only)
- **GSS** — `gss/*.yaml`, the *GATT Specification Supplement*, which carries the
  field structure but encodes scaling/unit/presence as a rigid mini-grammar
  inside prose `description` fields.

Both live in the public repo `bitbucket.org/bluetooth-SIG/public` (also mirrored
to GitHub). No newer machine-readable **field-structure XML** exists anywhere;
our XML dialect remains the most complete open structured GATT field database.

## gss_to_xml.py

Converts a GSS YAML characteristic into our XML dialect. Parses the prose
grammar:

| GSS | our XML |
| --- | --- |
| `type: uint16` | `<Format>uint16</Format>` |
| `Represented values: M = 1, d = -2, b = 0` | `<Multiplier>`/`<DecimalExponent>`/`<BinaryExponent>` |
| `Unit:` / `Base Unit: org.bluetooth.unit.*` | `<Unit>` |
| `Minimum:` / `Maximum:` | `<Minimum>` / `<Maximum>` |
| `type: uint16[n]` / `uint8[N]` | `repeats="0"` (to end) / `repeats="N"` |
| `Flags` + `fields:` bit table | `<BitField><Bit>…` |
| `Present if bit N … set to 1` | `<Requirement>CN</Requirement>` |
| `Refer to X characteristic` | `<Reference>org.bluetooth.characteristic.x</Reference>` |

Scalars convert at high fidelity and decode real bytes. Cases the prose can't
express unambiguously (present-if-bit-**clear**, unresolved structs, missing
units) get an inline `<!-- REVIEW … -->` marker instead of a fabricated value,
and are listed in the run summary. Always `grep -rl REVIEW` and hand-audit those.

```bash
python3 tools/gss_to_xml.py --uuids char_uuids.yaml \
        --out src/main/resources/gatt/characteristic voltage power co2_concentration
```

## refresh_gss.sh

One-shot updater: downloads the UUID directory, computes the set of GSS
characteristics we don't yet bundle, and converts them in place.

```bash
tools/refresh_gss.sh              # convert the whole gap
tools/refresh_gss.sh voltage co2_concentration   # convert specific ids
```

After running, rebuild to regenerate the registry (the `type` attribute of each
file must equal its filename or the registry generator fails):

```bash
JAVA_HOME=<jdk> MAVEN_OPTS=-Djava.security.manager=allow mvn clean test
```

> **Build note:** the Groovy registry generator relies on `SecurityManager`
> (removed in JDK 18), hence `MAVEN_OPTS=-Djava.security.manager=allow`. Builds
> on JDK 17/21.
