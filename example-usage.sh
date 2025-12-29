#!/bin/bash
# Příklad: Použití gitbook-to-dash.bb na různých projektech

echo "=== Příklad 1: Základní použití ==="
echo ""
echo "cd ~/my-gitbook-project"
echo "bb gitbook-to-dash.bb"
echo ""

echo "=== Příklad 2: S vlastním názvem ==="
echo ""
echo "cd ~/clojure-docs"
echo "bb gitbook-to-dash.bb --name \"Clojure Guide\""
echo ""

echo "=== Příklad 3: Vzdálený projekt ==="
echo ""
echo "bb gitbook-to-dash.bb \\"
echo "  --name \"React Documentation\" \\"
echo "  --identifier react-docs \\"
echo "  --dir ~/work/react-gitbook \\"
echo "  --output ~/Desktop"
echo ""

echo "=== Příklad 4: Automatická detekce názvu ==="
echo ""
echo "cd ~/awesome-api-docs"
echo "bb gitbook-to-dash.bb"
echo "# → Vytvoří 'Awesome Api Docs.docset'"
echo ""

echo "=== Příklad 5: Testování na DataScript Tutorial ==="
echo ""
cd /Users/tomas/Dev/datascript-tutorial
echo "cd $(pwd)"
echo "bb gitbook-to-dash.bb --name \"DataScript Tutorial\""
echo ""
echo "Spouštím..."
echo ""

# Skutečné spuštění
bb gitbook-to-dash.bb --name "DataScript Tutorial Test" --output /tmp
