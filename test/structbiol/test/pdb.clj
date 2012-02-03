(ns structbiol.test.pdb
  (:use [clojure.test]
        [structbiol.pdb]
        midje.sweet))

(def atom-string "ATOM    209  CA ACYS A  48      26.161  41.522  -0.504  0.50 41.32           C")

(def sample-atom {:chain "A" 
                  :serial-number 209 
                  :alternate-location false 
                  :x-coord 26.161 
                  :z-coord -0.504 
                  :residue-type "CYS" 
                  :sequence-number 48 
                  :type "CA"
                  :y-coord 41.522 
                  :occupancy 0.5 
                  :temperature 41.32})

(def seqres-string "SEQRES   4 A    3  ASN GLY PRO")

(def sample-seqres {:chain "A"
		    :number-of-residues 3
		    :residues ["ASN" "GLY" "PRO"]})

(def dbref-string "DBREF  1HNN A    1   282  UNP    P11086   PNMT_HUMAN       1    282")

(def sample-dbref
     {:id-code            "1HNN"   
      :chain              "A"  
      :seq-start          1  
      :insertion-start    nil  
      :seq_end            282    
      :insertion-end      nil      
      :sequence-db-name   "UNP"
      :sequence-accession "P11086"
      :sequence-db-code   "PNMT_HUMAN"    
      :db-seq-start       1
      :db-seq-end         282})

(deftest test-parse-dbref
  (is (= (parse-dbref dbref-string) sample-dbref)))

(deftest test-parse-atom
  (is (= (parse-atom atom-string) sample-atom)))

(deftest test-parse-seqres
  (is (= (parse-seqres seqres-string) sample-seqres)))