(ns structbiol.core
    (:gen-class)
    (:use [structbiol.dssp]
          [structbiol.pdb]
          [clojure.contrib.math :only [expt sqrt]]))
    
(defn -main [& args]
  (def chain
       (get-chain "B" (remove-bad-residues (read-dssp "resources/1hnn.dssp"))))
    
  (def d (read-dssp "resources/1dot.dssp"))
  (def chunked (chuck-chain-by-structure "A" d))
	
  (doall (for [i (sort (keys chunked))]
  (println i (count (chunked i)) ((first (chunked i)) :ss))))
	  
  (fetch-dssp-file "s1hnn")
  (fetch-dssp-file "3chy")
  
  (flush))
    
    
