(ns structbiol.dssp
    (:use [clojure.java.io :only [reader]]
          [clojure.string :only [trim split]]))

(defrecord DSSP [ surface-area number-of-residues
		 number-of-chains ss-total
		 ss-intrachain ss-interchain
		 residues ])

(defn coords [residue]
    [(:x-ca residue) (:y-ca residue) (:z-ca residue)])

(defn get-chain [chain-id dssp]
    (filter #(= (% :chain) chain-id) (:residues dssp)))

(defn chain-idenfifiers
  "Given takes a DSSP. record and tells you the chain identifiers: A,B,C etc."
  [dssp]
  (filter (fn [x] (false? (nil? x)))
	  (set (map #(:chain %) (:residues dssp)))))

(defn get-residue-attribute
    "For a collection of residues, get the requested attribute."
  [attr dssp-record]    
  (map #( attr % ) (:residues dssp-record))) 

(defn remove-bad-residues
  "Given a DSSP. record, return a new record with the nils and
undefined residues removed - all chains are processed at once"
  [dssp]
  (assoc dssp :residues
      (filter #(not (= (:aa %) "X")) (:residues dssp))))

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
	       (re-find #"TOTAL NUMBER OF RESIDUES" current-line) 
	       (recur (rest content)
		      (merge headers (parse-residues-header current-line))
		      residues
		      in-residues)
	       
	       (re-find #"ACCESSIBLE SURFACE OF PROTEIN" current-line)
	       (recur (rest content)
		      (merge headers {:surface-area (Float/parseFloat
						     (first
						      (split (trim current-line) #"\s")))})
		      residues
		      in-residues)
	       
	       (re-find #"  \#  RESIDUE AA" current-line)
	       (recur (rest content) headers residues true)
	       
	       (re-find #".*!\*" current-line)
	       (recur (rest content) headers residues in-residues)
	       
	       (true? in-residues) 
	       (recur (rest content) 
		      headers
		      (cons (parse-residue-line current-line) residues) 
		      in-residues)
	       
	       :else (recur (rest content)
			    headers 
			    residues
			    in-residues))))))
    (catch Exception e (throw e))))