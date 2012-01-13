(ns structbiol.core
    (:gen-class)
    (:use [structbiol.dssp]
          [structbiol.pdb]
          [clojure.contrib.math :only [expt sqrt]]))
    
(defn -main [& args]
  (def chain
      (get-chain "B" (remove-bad-residues (read-dssp "resources/1hnn.dssp"))))
    
  ;(doall (for [r chain]
  ;    (println (:residue r) (coords r))))
  
  
  (defn euclidean [ [x1 y1 z1] [x2 y2 z2] ]
      (sqrt (+ (expt (- x1 x2) 2) (expt (- y1 y2) 2) (expt (- z1 z2) 2) )))
  
  (defn in-contact? [d cutoff]
      (if (<= d cutoff) true false))
  
  ; would do contact map
  ;(doall (for [i chain 
  ;             j chain 
  ;             :when (and (<= (:residue i) (:residue j))
  ;                   (not (= (:residue i) (:residue j))))]               
  ;                (let [x 0]
  ;                (in-contact? (euclidean (coords i) (coords j)) 8.0) x
  ;                )))

  (fetch-pdb-structure "1hnn")
  (fetch-pdb-sequence  "1hnn")
  
  (flush)
  )
    
    
