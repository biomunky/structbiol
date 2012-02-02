(ns structbiol.fasta
  (:use [clojure.java.io :only [reader]]))
    
(defn read-fasta-file [filename]
  (with-open [rdr (reader filename)]
    (loop [content (line-seq rdr) current-header nil sequences {} ]
      (if (empty? content) sequences
	  (let [line (first content)]
	    (cond
	     (re-find #"^>.*" line ) (recur (rest content)
					    												line
					    												sequences)
	     :else (recur (rest content)
			  						current-header
			  						(assoc sequences current-header (str (sequences current-header nil) line)))))))))

