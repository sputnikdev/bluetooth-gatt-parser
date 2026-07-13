#!/usr/bin/env bash
# Refresh bundled GATT characteristic specs from the Bluetooth SIG GSS.
#
# The SIG retired the machine-readable characteristic XML (~2019); field
# structure now lives in the GSS YAML at bitbucket.org/bluetooth-SIG/public.
# This script:
#   1. downloads the current characteristic UUID directory,
#   2. lists every GSS characteristic we don't already bundle (the "gap"),
#   3. converts each gap entry to our XML dialect via gss_to_xml.py,
#      writing straight into src/main/resources/gatt/characteristic.
#
# Converted files carry `approved="No"`; scalars are high fidelity, complex
# (conditional/struct) ones get <!-- REVIEW --> markers — grep for REVIEW after
# running and hand-audit those before trusting their decode.
#
# Usage:  tools/refresh_gss.sh            # convert the whole gap
#         tools/refresh_gss.sh <id> ...   # convert just the named ids
set -euo pipefail
cd "$(dirname "$0")/.."
RES="src/main/resources/gatt/characteristic"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

BASE="https://bitbucket.org/bluetooth-SIG/public/raw/main"
API="https://api.bitbucket.org/2.0/repositories/bluetooth-SIG/public/src/main/gss/?pagelen=100"

echo "Fetching characteristic UUID directory..."
curl -fsSL "$BASE/assigned_numbers/uuids/characteristic_uuids.yaml" -o "$WORK/char_uuids.yaml"

if [ "$#" -gt 0 ]; then
    printf '%s\n' "$@" > "$WORK/gap.txt"
else
    echo "Computing gap (GSS characteristics not already bundled)..."
    python3 - "$API" "$RES" > "$WORK/gap.txt" <<'PY'
import json, os, sys, urllib.request
api, res = sys.argv[1], sys.argv[2]
url, ids = api, []
while url:
    d = json.load(urllib.request.urlopen(url, timeout=30))
    for v in d["values"]:
        p = v["path"]
        if p.startswith("gss/org.bluetooth.characteristic") and p.endswith(".yaml"):
            ids.append(os.path.basename(p)[:-5])
    url = d.get("next")
have = {f[:-4].lower() for f in os.listdir(res) if f.endswith(".xml")}
for i in sorted(set(ids)):
    if i.lower() not in have:
        print(i)
PY
fi

echo "Converting $(wc -l < "$WORK/gap.txt") characteristic(s)..."
python3 "$(dirname "$0")/gss_to_xml.py" --uuids "$WORK/char_uuids.yaml" --out "$RES" --stdin < "$WORK/gap.txt"

echo
echo "Done. Rebuild to regenerate the registry:"
echo "  JAVA_HOME=<jdk> MAVEN_OPTS=-Djava.security.manager=allow mvn clean test"
echo "Then review any files containing '<!-- REVIEW':"
echo "  grep -rl REVIEW $RES"
