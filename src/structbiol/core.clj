(ns structbiol.core
  (:gen-class)
  (:use [structbiol.dssp]
	[structbiol.pdb]
	[clojure.contrib.math :only [expt sqrt]])
  (:import (java.io File)))

(defn -main [& args]
  (def dssp-dir (File. "/Users/biomunky/Dropbox/postdoc/data-analysis/dssp"))
  (doall (for [f (.listFiles dssp-dir)]
	   (let [d-file (read-dssp (str (.getAbsoluteFile f)))
		 cs     (chains d-file)]
	     (if (= 1 (count cs))
	       (do (println "Chunking" (.getName f))
		   (write-chunks-to-file (first cs) (remove-bad-residues d-file) (.getName f)))))))
  (flush))
