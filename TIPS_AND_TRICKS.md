# Tips & Tricks - GitBook to Dash Converter

## 🎯 Best Practices

### 1. Struktura SUMMARY.md

```markdown
# Summary

* [Introduction](README.md)
* [Getting Started](getting-started.md)
* [API Reference](api/README.md)
    * [Core API](api/core.md)
    * [Utils](api/utils.md)
* [Examples](examples/README.md)
    * [Basic Example](examples/basic.md)
```

**Tip:** Používejte odsazení (4 mezery) pro podkapitoly - skript je automaticky detekuje jako "Guide" místo "Section".

### 2. Pojmenování souborů

✅ **Doporučeno:**
- `getting-started.md` (kebab-case)
- `api-reference.md`
- `quick_guide.md` (snake_case)

❌ **Nedoporučeno:**
- `Getting Started.md` (mezery v názvu)
- `API Reference & Tips.md` (speciální znaky)

### 3. Prázdné sekční stránky

Je v pořádku mít stránky jen s nadpisem:

```markdown
# API Reference
```

Dash používá tyto stránky jako organizační strukturu v menu.

### 4. README.md jako úvod

`README.md` se automaticky stane `index.html` (Introduction page).

**Tip:** Dejte do README.md přehled celé dokumentace, aby měla vstupní stránka hodnotu.

## 🔧 Přizpůsobení

### Změna typu položek v indexu

Skript automaticky určuje typy:
- **Section** - kapitoly na první úrovni odsazení
- **Guide** - všechny ostatní stránky

Pokud chcete jiné typy (Class, Function, Method), upravte `parse-summary` funkci:

```clojure
(let [entry-type (cond
                   (str/includes? title "API") "Function"
                   (str/includes? title "Class") "Class"
                   (< indent 4) "Section"
                   :else "Guide")]
  ...)
```

### Vlastní ikona pro docset

Přidejte `icon.png` (16x16) a `icon@2x.png` (32x32) do:
```
MyDocset.docset/
└── icon.png
└── icon@2x.png
```

## 🐛 Debugging

### Zobrazit co Honkit vygeneroval

```bash
# Spusťte build manuálně
cd ~/my-gitbook-project
honkit build

# Prohlédněte si výstup
open _book/index.html
```

### Zkontrolovat SQLite index

```bash
sqlite3 MyDocset.docset/Contents/Resources/docSet.dsidx \
  "SELECT * FROM searchIndex;"
```

### Ověřit odkazy

```bash
# Najít všechny odkazy na "./"
grep -r 'href="./"' MyDocset.docset/Contents/Resources/Documents/
# Mělo by vrátit nic (všechny by měly být opravené na "index.html")
```

## 🚀 Performance tipy

### Velké projekty (>100 stránek)

Honkit build může trvat déle. Použijte:

```bash
# Přeskočit regeneraci při opakovaném spuštění
honkit build --log=info
```

### Zmenšení velikosti docsetu

1. Odstraňte nepotřebné assety z GitBook téma
2. Minimalizujte obrázky před buildem
3. Komprimujte archiv s lepší kompresí:

```bash
tar --exclude='.DS_Store' -czf MyDocs.tgz MyDocs.docset
# nebo pro maximální kompresi:
tar --exclude='.DS_Store' -cJf MyDocs.tar.xz MyDocs.docset
```

## 📦 Sdílení docsetů

### Vytvoření XML feedu pro Dash

Pokud chcete sdílet docset veřejně, vytvořte XML feed:

```xml
<entry>
    <version>1.0</version>
    <url>https://example.com/MyDocs.tgz</url>
</entry>
```

Více info: https://kapeli.com/docsets#submittingcontributed

### GitHub Release

```bash
# Tag verze
git tag v1.0.0
git push --tags

# Nahrajte .tgz jako GitHub Release asset
gh release create v1.0.0 MyDocs.tgz --title "MyDocs v1.0.0"
```

## 🎨 Stylování

### Vlastní CSS pro Dash

Přidejte do vašeho `book.json`:

```json
{
  "styles": {
    "website": "styles/website.css"
  }
}
```

V `styles/website.css`:

```css
/* Optimalizace pro Dash WebKit viewer */
body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  line-height: 1.6;
}

code {
  background: #f4f4f4;
  padding: 2px 6px;
  border-radius: 3px;
}

pre code {
  display: block;
  padding: 12px;
  overflow-x: auto;
}
```

## 🔍 Pokročilé použití

### Batch konverze více projektů

```bash
#!/bin/bash
for dir in ~/docs/*/; do
  if [ -f "$dir/SUMMARY.md" ]; then
    echo "Converting $dir"
    cd "$dir"
    bb ~/bin/gitbook-to-dash.bb
  fi
done
```

### CI/CD integrace

```yaml
# .github/workflows/docset.yml
name: Build Docset
on: [push]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: DeLaGuardo/setup-clojure@master
        with:
          bb: latest
      - run: npm install -g honkit
      - run: bb gitbook-to-dash.bb --name "My Docs"
      - uses: actions/upload-artifact@v2
        with:
          name: docset
          path: "*.tgz"
```

## 📚 Užitečné odkazy

- [Honkit dokumentace](https://github.com/honkit/honkit)
- [Dash Docset format](https://kapeli.com/docsets)
- [Babashka](https://github.com/babashka/babashka)
- [GitBook format](https://docs.gitbook.com/)

---

**Pro otázky a návrhy vytvořte issue na GitHubu!**
