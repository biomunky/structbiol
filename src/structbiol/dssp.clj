(ns structbiol.dssp
  (:use [clojure.java.io :only [reader writer]]
	[clojure.string :only [trim split join]]
	[structbiol.utils :only [create-directory]])
  (:import (java.io File)))

(defrecord DSSP [ surface-area number-of-residues
		 number-of-chains ss-total
		 ss-intrachain ss-interchain
		 residues ])

(defn coords [residue]
  [(:x-ca residue) (:y-ca residue) (:z-ca residue)])

(defn get-chain [chain-id dssp]
  (filter #(= (% :chain) chain-id) (:residues dssp)))

(defn chunk-chain-by-structure [chain-id dssp]
  (loop [chain (get-chain chain-id dssp) substructures {} counter 0]
    (if (empty? chain) substructures
	(let [residue (first chain)]
	  (cond 
	   (= (residue :ss) (get (first (substructures counter nil)) :ss))
	   (recur 
	    (rest chain) 
	    (assoc substructures counter 
		   (conj (substructures counter) residue)) 
	    counter)
	   (not (= (residue :ss) (get (first (substructures counter nil)) :ss)))
	   (recur (rest chain)
		  (assoc substructures (inc counter) [residue] )
		  (inc counter))	   
	   :else 
	   (recur (rest chain) substructures (inc counter)))))))

(defn write-chunks-to-file [chain-id dssp filename]
  (def output-dir "chunks/")
  (create-directory output-dir)
  (def structure-chunks (chunk-chain-by-structure chain-id dssp))
  (with-open [wtr (writer (str output-dir filename))]
    (.write wtr "chunkID, ssType, #elements\n" )
    (doall (for [[chunk-id residues] structure-chunks]
	     (.write wtr (str chunk-id " " (:ss (first residues)) " " (count residues)  "\n"))))))

(defn chains
  "Given takes a DSSP. record and tells you the chain identifiers: A,B,C etc."
  [dssp]
  (filter (fn [x] (false? (nil? x)))
	  (set (map #(:chain %) (:residues dssp)))))

(defn get-residue-attribute
  "For a collection of residues, get the requested attribute."
  [attr dssp-record]    
  (map #( attr % ) (:residues dssp-record))) 

(defn remove-bad-residues
  "Remove missing residues and those that are not one of the 'real' residues. Not done on parsing because I may want them later"
  [dssp]
  (assoc dssp :residues
	 (filter #(or (not (nil? (:ss %))) (= (:aa %) "X")) (:residues dssp))))	
	
(defn parse-residue-line
  "Pass an residue line (string) from DSSP output and get back a hashmap of the important data.
If passsed something else you will get an error or crap"
  [s]
  (if (re-find #"!|\*" s) nil
      {:residue (Integer/parseInt (trim (.substring s 5 10)))
       :chain   (trim (.substring s 11 12))
       :aa      (trim (.substring s 13 14))
       :ss      (let [ s (trim (.substring s 16 17)) ]
		  (if (= "" s) "-" s))
       :asa     (Integer/parseInt (trim (.substring s 35 38)))
       :kappa   (Float/parseFloat (trim (.substring s 92 97)))
       :alpha   (Float/parseFloat (trim (.substring s 97 103)))
       :phi     (Float/parseFloat (trim (.substring s 104 109)))
       :psi     (Float/parseFloat (trim (.substring s 109 115)))
       :x-ca    (Float/parseFloat (trim (.substring s 116 122)))
       :y-ca    (Float/parseFloat (trim (.substring s 124 129)))
       :z-ca    (Float/parseFloat (trim (.substring s 130 136)))}))

(defn parse-residues-header [s]
  (zipmap
   [:number-of-residues :number-of-chains :ss-total :ss-intrachain :ss-interchain]
   (map #(Integer/parseInt %) (take 5 (split (trim s) #"\s+" 6)))))

(defn read-dssp [filename]
  (try
    (with-open [rdr (reader filename)]
      (loop [content (line-seq rdr) headers {} residues [] in-residues false]
	(if (empty? content)
	  (DSSP. (:surface-area headers)
		 (:number-of-residues headers)
		 (:number-of-chains headers)
		 (:ss-total headers)
		 (:ss-intrachain headers)
		 (:ss-interchain headers)
		 (reverse residues))
	  (let [current-line (first content)]
	    (cond
	     (re-find #"TOTAL NUMBER OF RESIDUES" current-line) (recur (rest content)
								       (merge headers (parse-residues-header current-line))
								       residues
								       in-residues)
	     (re-find #"ACCESSIBLE SURFACE OF PROTEIN" current-line) (recur (rest content)
									    (merge headers {:surface-area (Float/parseFloat
													   (first (split (trim current-line) #"\s")))})
									    residues
									    in-residues)
	     (re-find #"  \#  RESIDUE AA" current-line) (recur (rest content) headers residues true)
	     (re-find #".*!\*" current-line) (recur (rest content) headers residues in-residues)
	     (true? in-residues) (recur (rest content) 
					headers
					(cons (parse-residue-line current-line) residues) 
					in-residues)
	     :else (recur (rest content)
			  headers 
			  residues
			  in-residues))))))
    (catch Exception e (throw e))))

(defn fetch-dssp-file [id]
  (create-directory "dssp")
  (if (.isFile (File. (str "dssp/" id ".dssp")))
    (println "Found" id "in dssp/")
    (try
      (println "Fetching" id "from EBI dssp repository")
      (let  [url (str "ftp://ftp.ebi.ac.uk/pub/databases/dssp/" id ".dssp.gz")
	     con    (-> url java.net.URL. .openConnection)
	     in     (java.io.BufferedInputStream. (.getInputStream con))
	     out    (java.io.BufferedOutputStream. 
		     (java.io.FileOutputStream. (str "dssp/" id ".dssp.gz")))
	     buffer (make-array Byte/TYPE 1024)]
	(loop [g (.read in buffer)
	       r 0]
	  (if-not (= g -1)
	    (do
	      (.write out buffer 0 g)
	      (recur (.read in buffer) (+ r g)))))
	(.close in)
	(.close out))
      (catch Exception e (println "Couldn't get the data for" id )))))