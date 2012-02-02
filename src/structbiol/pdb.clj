(ns structbiol.pdb
  (:use [clojure.string   :only [trim split]]
	[structbiol.utils :only [create-directory fetch-url-content]]))

(defn fetch-pdb-structure [id]
  (fetch-url-content (str "http://www.rcsb.org/pdb/files/" id ".pdb") "pdb" id))

(defn fetch-pdb-sequence [id]
  (fetch-url-content 
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

(defn parse-dbref
  "Parse a dbref string - returns a hashmap"
  [s]
  {:id-code            (trim (.substring s 7 11))           
   :chain              (trim (.substring s 12 13))            
   :seq-start          (Integer/parseInt (trim (.substring s 15 18)))
   :insertion-start    (let [ x (trim (.substring s 19 20))]
			 (if (= "" x) nil (Integer/parseInt x)))
   :seq_end            (Integer/parseInt (trim (.substring s 20 24)))
   :insertion-end      (let [x (trim (.substring s 25 26))]
			 (if (= "" x) nil (Integer/parseInt x)))
   :sequence-db-name   (trim (.substring s 26 32))
   :sequence-accession (trim (.substring s 33 39))
   :sequence-db-code   (trim (.substring s 42 52))
   :db-seq-start       (Integer/parseInt (trim (.substring s 55 61)))
   :db-seq-end         (Integer/parseInt (trim (.substring s 62 67)))})