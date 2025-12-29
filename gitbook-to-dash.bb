#!/usr/bin/env bb

(require '[clojure.java.io :as io]
         '[clojure.string :as str]
         '[clojure.java.shell :refer [sh]])

(def usage "
GitBook to Dash Docset Converter
=================================

Usage:
  bb gitbook-to-dash.bb [options]

Options:
  -n, --name NAME        Docset name (default: from directory name)
  -i, --identifier ID    Bundle identifier (default: lowercase name)
  -d, --dir DIR          Source directory (default: current directory)
  -o, --output DIR       Output directory (default: current directory)
  -h, --help            Show this help

Prerequisites:
  - honkit (npm install -g honkit)
  - SUMMARY.md and README.md in source directory
  - book.json (optional)

Example:
  bb gitbook-to-dash.bb --name \"My Documentation\" --identifier mydocs
")

(defn parse-args [args]
  (loop [args args
         opts {:dir "."
               :output "."
               :name nil
               :identifier nil}]
    (if (empty? args)
      opts
      (let [arg (first args)]
        (case arg
          ("-h" "--help") (do (println usage) (System/exit 0))
          ("-n" "--name") (recur (drop 2 args) (assoc opts :name (second args)))
          ("-i" "--identifier") (recur (drop 2 args) (assoc opts :identifier (second args)))
          ("-d" "--dir") (recur (drop 2 args) (assoc opts :dir (second args)))
          ("-o" "--output") (recur (drop 2 args) (assoc opts :output (second args)))
          (do
            (println "Unknown option:" arg)
            (println usage)
            (System/exit 1)))))))

(defn derive-name [dir]
  (-> (io/file dir)
      (.getAbsolutePath)
      (io/file)
      (.getName)
      (str/replace #"-" " ")
      (str/split #" ")
      (->> (map str/capitalize)
           (str/join " "))))

(defn sanitize-identifier [name]
  (-> name
      str/lower-case
      (str/replace #"[^a-z0-9]+" "-")
      (str/replace #"-+$" "")
      (str/replace #"^-+" "")))

(defn check-prerequisites []
  (println "Checking prerequisites...")
  
  ;; Check for honkit
  (let [honkit-check (sh "which" "honkit")]
    (when-not (zero? (:exit honkit-check))
      (println "ERROR: honkit not found. Install it with: npm install -g honkit")
      (System/exit 1)))
  
  (println "✓ honkit found"))

(defn check-source-files [dir]
  (println (str "Checking source files in: " dir))
  
  (let [summary-file (io/file dir "SUMMARY.md")
        readme-file (io/file dir "README.md")]
    
    (when-not (.exists summary-file)
      (println "ERROR: SUMMARY.md not found in" dir)
      (System/exit 1))
    
    (when-not (.exists readme-file)
      (println "ERROR: README.md not found in" dir)
      (System/exit 1))
    
    (println "✓ SUMMARY.md and README.md found")))

(defn build-honkit [dir]
  (println "\nBuilding documentation with Honkit...")
  
  (let [result (sh "honkit" "build" :dir dir)]
    (when-not (zero? (:exit result))
      (println "ERROR: honkit build failed")
      (println (:err result))
      (System/exit 1))
    
    (println "✓ Honkit build completed")))

(defn parse-summary [summary-file]
  (println "Parsing SUMMARY.md...")
  
  (let [content (slurp summary-file)
        lines (str/split-lines content)
        entries (atom [])]
    
    (doseq [line lines]
      (when-let [[_ title path] (re-find #"\[([^\]]+)\]\(([^\)]+)\)" line)]
        (let [indent (count (re-find #"^\s*" line))
              entry-type (if (< indent 4) "Section" "Guide")]
          (swap! entries conj {:name title
                               :type entry-type
                               :path path}))))
    
    (println (str "✓ Found " (count @entries) " entries"))
    @entries))

(defn create-docset-structure [output-dir docset-name]
  (println "\nCreating docset structure...")
  
  (let [docset-dir (io/file output-dir (str docset-name ".docset"))
        contents-dir (io/file docset-dir "Contents")
        resources-dir (io/file contents-dir "Resources")
        docs-dir (io/file resources-dir "Documents")]
    
    (.mkdirs docs-dir)
    
    (println (str "✓ Created: " (.getPath docset-dir)))
    
    {:docset-dir docset-dir
     :contents-dir contents-dir
     :resources-dir resources-dir
     :docs-dir docs-dir}))

(defn create-info-plist [contents-dir docset-name identifier]
  (println "Creating Info.plist...")
  
  (let [plist-content (format "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">
<plist version=\"1.0\">
<dict>
	<key>CFBundleIdentifier</key>
	<string>%s</string>
	<key>CFBundleName</key>
	<string>%s</string>
	<key>DocSetPlatformFamily</key>
	<string>%s</string>
	<key>isDashDocset</key>
	<true/>
	<key>dashIndexFilePath</key>
	<string>index.html</string>
	<key>DashDocSetFamily</key>
	<string>dashtoc</string>
</dict>
</plist>
" identifier docset-name (str/lower-case identifier))]
    
    (spit (io/file contents-dir "Info.plist") plist-content)
    (println "✓ Info.plist created")))

(defn copy-built-docs [book-dir docs-dir]
  (println "Copying built documentation...")
  
  (let [result (sh "cp" "-r" (str book-dir "/_book/") (str (.getPath docs-dir) "/"))]
    (when-not (zero? (:exit result))
      (println "ERROR: Failed to copy _book directory")
      (System/exit 1)))
  
  ;; Move files from _book subdirectory to docs-dir
  (let [book-subdir (io/file docs-dir "_book")]
    (when (.exists book-subdir)
      (doseq [file (.listFiles book-subdir)]
        (let [dest (io/file docs-dir (.getName file))]
          (sh "mv" (.getPath file) (.getPath dest))))
      (.delete book-subdir)))
  
  (println "✓ Documentation copied"))

(defn fix-links [docs-dir]
  (println "Fixing links...")
  
  (let [html-files (filter #(str/ends-with? (.getName %) ".html")
                          (file-seq docs-dir))
        count (atom 0)]
    
    (doseq [file html-files]
      (let [content (slurp file)
            fixed-content (str/replace content #"href=\"\./\"" "href=\"index.html\"")]
        (when-not (= content fixed-content)
          (spit file fixed-content)
          (swap! count inc))))
    
    (println (str "✓ Fixed links in " @count " files"))))

(defn create-search-index [resources-dir entries]
  (println "Creating search index...")
  
  (let [db-file (io/file resources-dir "docSet.dsidx")]
    
    ;; Delete existing database
    (when (.exists db-file)
      (.delete db-file))
    
    ;; Create SQLite database
    (let [create-table "CREATE TABLE searchIndex(id INTEGER PRIMARY KEY, name TEXT, type TEXT, path TEXT);"
          create-index "CREATE UNIQUE INDEX anchor ON searchIndex (name, type, path);"]
      
      (sh "sqlite3" (.getPath db-file) create-table)
      (sh "sqlite3" (.getPath db-file) create-index)
      
      ;; Insert entries
      (doseq [{:keys [name type path]} entries]
        (let [sql (format "INSERT OR IGNORE INTO searchIndex(name, type, path) VALUES ('%s', '%s', '%s');"
                         (str/replace name "'" "''")
                         type
                         path)]
          (sh "sqlite3" (.getPath db-file) sql))))
    
    (println (str "✓ Search index created with " (count entries) " entries"))))

(defn create-archive [output-dir docset-name]
  (println "\nCreating archive...")
  
  (let [docset-dir (str docset-name ".docset")
        archive-name (str docset-name ".tgz")
        result (sh "tar" "--exclude=.DS_Store" "-czf" archive-name docset-dir
                  :dir output-dir)]
    
    (when-not (zero? (:exit result))
      (println "ERROR: Failed to create archive")
      (System/exit 1))
    
    (let [archive-file (io/file output-dir archive-name)
          size-kb (int (/ (.length archive-file) 1024))]
      (println (format "✓ Archive created: %s (%d KB)" archive-name size-kb)))))

(defn print-summary [output-dir docset-name]
  (println "\n" (str/join "" (repeat 50 "=")))
  (println "✅ SUCCESS!")
  (println (str/join "" (repeat 50 "=")))
  (println)
  (println (str "Docset created: " docset-name ".docset"))
  (println (str "Archive created: " docset-name ".tgz"))
  (println (str "Location: " output-dir))
  (println)
  (println "To install in Dash:")
  (println (str "1. Open Dash"))
  (println (str "2. Drag & drop '" docset-name ".docset' into Dash"))
  (println)
  (println "Or use the archive:")
  (println (str "1. Extract " docset-name ".tgz"))
  (println (str "2. Drag & drop the extracted .docset into Dash"))
  (println))

(defn main [& args]
  (let [opts (parse-args args)
        source-dir (io/file (:dir opts))
        output-dir (io/file (:output opts))
        docset-name (or (:name opts) (derive-name (:dir opts)))
        identifier (or (:identifier opts) (sanitize-identifier docset-name))]
    
    (println "╔════════════════════════════════════════════════╗")
    (println "║   GitBook to Dash Docset Converter            ║")
    (println "╚════════════════════════════════════════════════╝")
    (println)
    (println (str "Docset name: " docset-name))
    (println (str "Identifier: " identifier))
    (println (str "Source: " (.getPath source-dir)))
    (println (str "Output: " (.getPath output-dir)))
    (println)
    
    ;; Validate
    (check-prerequisites)
    (check-source-files source-dir)
    
    ;; Build with Honkit
    (build-honkit source-dir)
    
    ;; Parse SUMMARY.md
    (let [entries (parse-summary (io/file source-dir "SUMMARY.md"))]
      
      ;; Create docset structure
      (let [{:keys [docset-dir contents-dir resources-dir docs-dir]} 
            (create-docset-structure output-dir docset-name)]
        
        ;; Create Info.plist
        (create-info-plist contents-dir docset-name identifier)
        
        ;; Copy built docs
        (copy-built-docs source-dir docs-dir)
        
        ;; Fix links
        (fix-links docs-dir)
        
        ;; Create search index
        (create-search-index resources-dir entries)
        
        ;; Create archive
        (create-archive output-dir docset-name)
        
        ;; Print summary
        (print-summary output-dir docset-name)))))

;; Run
(apply main *command-line-args*)
