(ns structbiol.pdb
  (:use [clojure.java.io :only [reader writer]]
	[clojure.string :only [trim split join]])
  (:import (java.io File)))

(defn- create-directory [name]
  (let [f (File. name)]
    (cond
     (.isDirectory f) (println "Found" name "in current directory")
     :else (try
	     (do (println "Created" name "in current directory")
		 (.mkdir (File. name)))
	     (catch Exception e (throw "Failed to create" name "in your workspace"))))))

(defn- fetch-resource [url output-dir id]
  (cond
   (.isFile (File. (str output-dir "/" id))) (println "You've already got" (str output-dir "/" id))
   :else
   (do (create-directory output-dir)
       (with-open [r (reader url)]
	 (with-open [wtr (writer (str output-dir "/" id))]
	   (.write wtr (join "\n" (line-seq r))))))))

(defn fetch-pdb-structure [id]
  (fetch-resource (str "http://www.rcsb.org/pdb/files/" id ".pdb") "pdb" id))

(defn fetch-pdb-sequence [id]
  (fetch-resource 
   (str "http://www.rcsb.org/pdb/download/downloadFile.do?fileFormat=FASTA&compression=NO&structureId=" id)
   "fasta" id))

(defn parse-atom
    "Takes a pdb atom string and returns a parsed
    atom hashmap" 
    [s]
    {:serial-number      (Integer/parseInt (trim (.substring s 7  11)))
     :type               (trim (.substring s 13 16))
     :alternate-location (if (= "" (trim (.substring s 16 17))) true false)
     :residue-type       (trim (.substring s 17 20))
     :chain              (trim (.substring s 21 22))
     :sequence-number    (Integer/parseInt (trim (.substring s 22 26)))
     :x-coord            (Double/parseDouble (trim (.substring s 30 38)))        
     :y-coord            (Double/parseDouble (trim (.substring s 38 46)))       
     :z-coord            (Double/parseDouble (trim (.substring s 46 54)))        
     :occupancy          (Double/parseDouble (trim (.substring s 54 60)))            
     :temperature        (Double/parseDouble (trim (.substring s 60 66)))})

(defn parse-seqres
    "Takes a seqres string and returns a parsed
    seqres hashmap"
    [s]
    {:chain  (trim (.substring s 11 12))
     :number-of-residues (Integer/parseInt (trim (.substring s 13 17)))
     :residues (split (trim (.substring s 19, (count s))) #"\s+")})
    
    
    
    
    