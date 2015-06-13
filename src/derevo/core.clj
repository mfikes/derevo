(ns derevo.core
  (require [clojure.java.io :as jio]))

(defn process-javascript-lines [lines]
  (remove nil?
    (pmap #(second (re-matches #".*require\(\'(.*)\'\).*" %)) lines)))

(defn strip-js-extension [filename]
  (subs filename 0 (- (count filename) 3)))

(defn process-javascript-file [directory file]
  (with-open [rdr (jio/reader file)]
    {:file     (subs (.getCanonicalPath file) (count directory))
     :provides [(strip-js-extension (.getName file))]
     :requires (vec (doall (process-javascript-lines (line-seq rdr))))
     :module-type :commonjs}))

(defn process-directory [directory]
  (vec (pmap (partial process-javascript-file directory)
         (filter #(.endsWith (.getName %) ".js") (file-seq (jio/as-file directory))))))