(ns structbiol.core
    (:gen-class)
    (:use [structbiol.dssp]))
    
(defn -main [& args]
  (def dssp (read-dssp "resources/1hnn.dssp"))


  (def dssp-clean (remove-bad-atoms dssp))

  (println (get-atom-attribute :aa dssp-clean))
  (println (get-atom-attribute :ss dssp-clean))
  
  (flush)
  )
    
    
