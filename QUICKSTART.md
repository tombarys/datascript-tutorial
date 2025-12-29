# GitBook to Dash - Quick Start

## 1. Instalace prerekvizit

```bash
# Babashka
brew install borkdude/brew/babashka

# Honkit
npm install -g honkit
```

## 2. Kopírování skriptu do projektu

```bash
# Zkopírujte gitbook-to-dash.bb do vašeho GitBook projektu
cp /path/to/gitbook-to-dash.bb ~/my-gitbook-project/
cd ~/my-gitbook-project
```

## 3. Spuštění

```bash
# Základní použití
bb gitbook-to-dash.bb

# S vlastním názvem
bb gitbook-to-dash.bb --name "My Documentation"

# S všemi parametry
bb gitbook-to-dash.bb \
  --name "My API Docs" \
  --identifier my-api-docs \
  --dir . \
  --output .
```

## 4. Instalace do Dash

1. Najděte vygenerovaný `.docset` soubor
2. Přetáhněte ho do Dash aplikace
3. Hotovo!

---

## Alternativní instalace (globální)

```bash
# Zkopírujte do ~/.local/bin
mkdir -p ~/.local/bin
cp gitbook-to-dash.bb ~/.local/bin/gitbook-to-dash
chmod +x ~/.local/bin/gitbook-to-dash

# Přidejte do PATH (do ~/.zshrc nebo ~/.bashrc)
export PATH="${HOME}/.local/bin:${PATH}"

# Nyní můžete používat odkudkoliv
cd ~/any-gitbook-project
gitbook-to-dash --name "My Docs"
```

---

## Testování na DataScript Tutorial

```bash
cd ~/Dev/datascript-tutorial
bb gitbook-to-dash.bb --name "DataScript Tutorial"
# ✅ SUCCESS!
# Docset created: DataScript Tutorial.docset
# Archive created: DataScript Tutorial.tgz (853 KB)
```

---

## Co když nemám Babashka?

Můžete použít Python verzi (vyžaduje Python 3 + pip):

```bash
pip3 install beautifulsoup4

# Použijte tento příkaz místo bb skriptu:
python3 << 'EOF'
# ... Python kód zde ...
EOF
```

Ale Babashka instalace je jednodušší a rychlejší! 🚀
