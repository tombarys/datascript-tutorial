# GitBook to Dash Docset Converter

Babashka skript pro automatickou konverzi GitBook dokumentace do Dash docset formátu.

## Požadavky

1. **Babashka** - [nainstalujte zde](https://github.com/babashka/babashka#installation)
   ```bash
   brew install borkdude/brew/babashka
   ```

2. **Honkit** - GitBook alternativa
   ```bash
   npm install -g honkit
   ```

3. **SQLite** - obvykle již nainstalováno na macOS

## Požadavky na zdrojovou dokumentaci

Váš GitBook projekt musí obsahovat:
- ✅ `SUMMARY.md` - obsah dokumentace (menu struktura)
- ✅ `README.md` - úvodní stránka
- ✅ `*.md` soubory odkazované v SUMMARY.md
- ⚠️ `book.json` - volitelné (může být prázdný `{}`)

## Použití

### Základní použití (v adresáři s GitBook dokumentací)

```bash
bb gitbook-to-dash.bb
```

Automaticky:
- Zjistí název z názvu adresáře
- Vytvoří docset v aktuálním adresáři
- Vytvoří `.tgz` archiv

### S vlastními parametry

```bash
bb gitbook-to-dash.bb \
  --name "My Documentation" \
  --identifier mydocs \
  --dir ./my-gitbook-project \
  --output ./output
```

## Parametry

| Parametr | Krátká forma | Popis | Default |
|----------|--------------|-------|---------|
| `--name` | `-n` | Název docsetu | Název adresáře |
| `--identifier` | `-i` | Bundle identifier | Sanitizovaný název |
| `--dir` | `-d` | Zdrojový adresář | Aktuální adresář |
| `--output` | `-o` | Výstupní adresář | Aktuální adresář |
| `--help` | `-h` | Zobrazit nápovědu | - |

## Příklady

### Konverze dokumentace v aktuálním adresáři

```bash
cd ~/my-gitbook-project
bb ~/path/to/gitbook-to-dash.bb
```

### Konverze s vlastním názvem

```bash
bb gitbook-to-dash.bb --name "DataScript Tutorial"
```

### Konverze projektu v jiném adresáři

```bash
bb gitbook-to-dash.bb \
  --dir ~/Documents/my-docs \
  --output ~/Desktop \
  --name "My API Docs"
```

## Co skript dělá

1. ✅ **Kontrola prerekvizit** - ověří přítomnost honkit a SQLite
2. ✅ **Kontrola zdrojových souborů** - ověří SUMMARY.md a README.md
3. ✅ **Build dokumentace** - spustí `honkit build`
4. ✅ **Parsování SUMMARY.md** - extrahuje strukturu menu
5. ✅ **Vytvoření docset struktury** - adresáře a Info.plist
6. ✅ **Kopírování souborů** - přesune vygenerované HTML
7. ✅ **Oprava odkazů** - změní `href="./"` na `href="index.html"`
8. ✅ **Vytvoření search indexu** - SQLite databáze pro Dash
9. ✅ **Vytvoření archivu** - `.tgz` pro snadné sdílení

## Výstup

Po dokončení najdete:

```
output-directory/
├── MyDocumentation.docset/    # Docset pro Dash
│   └── Contents/
│       ├── Info.plist
│       └── Resources/
│           ├── docSet.dsidx   # Search index
│           └── Documents/      # HTML soubory
└── MyDocumentation.tgz        # Komprimovaný archiv
```

## Instalace do Dash

### Způsob 1: Drag & Drop
1. Otevřete Dash
2. Přetáhněte `MyDocumentation.docset` do okna Dash

### Způsob 2: Z archivu
1. Extrahujte `MyDocumentation.tgz`
2. Přetáhněte `.docset` do Dash

## Řešení problémů

### "honkit not found"
```bash
npm install -g honkit
```

### "SUMMARY.md not found"
Ujistěte se, že jste ve správném adresáři, nebo použijte `--dir`

### "honkit build failed"
Zkontrolujte:
- Existují všechny `.md` soubory odkazované v SUMMARY.md?
- Je `book.json` validní JSON? (zkuste prázdný `{}`)

### Menu v Dash nefunguje
Skript automaticky opravuje odkazy. Pokud problém přetrvává:
1. Odstraňte starý docset z Dash
2. Reinstalujte nový docset

## Příklad: Konverze DataScript Tutorial

```bash
cd ~/Dev/datascript-tutorial
bb gitbook-to-dash.bb --name "DataScript Tutorial"
```

Výsledek:
```
✅ SUCCESS!
Docset created: DataScript Tutorial.docset
Archive created: DataScript Tutorial.tgz (847 KB)
Location: /Users/tomas/Dev/datascript-tutorial
```

## Licence

Public Domain - použijte jak potřebujete

## Autor

Vytvořeno s pomocí GitHub Copilot CLI
