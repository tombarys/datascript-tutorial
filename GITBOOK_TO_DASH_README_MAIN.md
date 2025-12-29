# 📚 GitBook to Dash Docset Converter

Automatický nástroj pro konverzi GitBook dokumentace do Dash docset formátu napsaný v Babashka (Clojure).

![License: Public Domain](https://img.shields.io/badge/license-Public%20Domain-blue.svg)
![Babashka](https://img.shields.io/badge/babashka-1.0+-green.svg)

## ✨ Vlastnosti

- ✅ **Automatická konverze** - jeden příkaz pro celý proces
- ✅ **Zero konfigurace** - funguje out-of-the-box
- ✅ **Rychlé** - Babashka start instantly
- ✅ **SQLite index** - plně funkční vyhledávání v Dash
- ✅ **Oprava odkazů** - automaticky opravuje GitBook odkazy pro Dash
- ✅ **Menu zachováno** - původní tree menu funguje v Dash
- ✅ **Přenositelné** - jediný soubor, snadno kopírovatelný

## 🚀 Quick Start

### 1. Instalace

```bash
# Prerekvizity
brew install borkdude/brew/babashka
npm install -g honkit
```

### 2. Použití

```bash
# Zkopírujte skript do vašeho GitBook projektu
cp gitbook-to-dash.bb ~/my-docs/

# Spusťte konverzi
cd ~/my-docs
bb gitbook-to-dash.bb --name "My Documentation"
```

### 3. Instalace do Dash

Přetáhněte vygenerovaný `.docset` do Dash aplikace. Hotovo! 🎉

## 📖 Dokumentace

- **[Quick Start Guide](QUICKSTART.md)** - Začněte zde
- **[Kompletní dokumentace](GITBOOK_TO_DASH_README.md)** - Všechny parametry a možnosti
- **[Tips & Tricks](TIPS_AND_TRICKS.md)** - Pokročilé použití a optimalizace

## 💡 Příklady

### Základní použití

```bash
cd ~/my-gitbook-project
bb gitbook-to-dash.bb
# → Automaticky detekuje název z adresáře
```

### S parametry

```bash
bb gitbook-to-dash.bb \
  --name "React Guide" \
  --identifier react-guide \
  --dir ~/work/react-docs \
  --output ~/Desktop
```

### Globální instalace

```bash
# Nainstalujte do PATH
mkdir -p ~/.local/bin
cp gitbook-to-dash.bb ~/.local/bin/gitbook-to-dash
chmod +x ~/.local/bin/gitbook-to-dash

# Přidejte do ~/.zshrc
export PATH="${HOME}/.local/bin:${PATH}"

# Použití odkudkoliv
cd ~/any-project
gitbook-to-dash --name "My Docs"
```

## 📋 Požadavky na projekt

Váš GitBook projekt musí obsahovat:

```
my-gitbook-project/
├── README.md          ✅ Povinné
├── SUMMARY.md         ✅ Povinné
├── book.json          ⚠️  Volitelné (může být prázdný {})
└── *.md              ✅ Kapitoly odkazované v SUMMARY.md
```

## 🎯 Co skript dělá

1. ✅ Zkontroluje prerekvizity (honkit, sqlite3)
2. ✅ Ověří přítomnost SUMMARY.md a README.md
3. ✅ Spustí `honkit build`
4. ✅ Vytvoří Dash docset strukturu
5. ✅ Vygeneruje Info.plist
6. ✅ Zkopíruje HTML soubory
7. ✅ Opraví odkazy (`./` → `index.html`)
8. ✅ Vytvoří SQLite search index
9. ✅ Zabalí do `.tgz` archivu

## 📦 Výstup

```
output-dir/
├── MyDocumentation.docset/
│   └── Contents/
│       ├── Info.plist
│       └── Resources/
│           ├── docSet.dsidx      # SQLite index
│           └── Documents/         # HTML soubory
│               ├── index.html
│               ├── chapter1.html
│               └── ...
└── MyDocumentation.tgz           # Archiv pro sdílení
```

## 🛠️ Parametry

| Parametr | Popis | Default |
|----------|-------|---------|
| `--name NAME` | Název docsetu | Název adresáře |
| `--identifier ID` | Bundle identifier | Lowercase název |
| `--dir DIR` | Zdrojový adresář | `.` |
| `--output DIR` | Výstupní adresář | `.` |
| `--help` | Zobrazit nápovědu | - |

## 🧪 Testováno na

- ✅ DataScript Tutorial (28 stránek)
- ✅ Malé projekty (5-10 stránek)
- ✅ Velké projekty (100+ stránek)
- ✅ Vnořené kapitoly (3+ úrovně)
- ✅ Markdown s code bloky
- ✅ Obrázky a assety

## 🐛 Řešení problémů

### "honkit not found"
```bash
npm install -g honkit
```

### "bb: command not found"
```bash
brew install borkdude/brew/babashka
```

### "SUMMARY.md not found"
Ujistěte se, že jste ve správném adresáři, nebo použijte `--dir`

### Menu v Dash nefunguje
1. Odstraňte starý docset z Dash
2. Reinstalujte nový docset
3. Zkontrolujte, že SUMMARY.md má správný formát

Více v **[Tips & Tricks](TIPS_AND_TRICKS.md)**

## 🤝 Příspěvky

Příspěvky jsou vítány! Vytvořte issue nebo pull request.

## 📜 Licence

Public Domain - použijte jak potřebujete.

## 🙏 Poděkování

- [Honkit](https://github.com/honkit/honkit) - Modern GitBook fork
- [Babashka](https://github.com/babashka/babashka) - Fast Clojure scripting
- [Dash](https://kapeli.com/dash) - API Documentation Browser

## 📬 Kontakt

Máte dotaz nebo nápad? Vytvořte issue!

---

**Made with ❤️ and Babashka**
