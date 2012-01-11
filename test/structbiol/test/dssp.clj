(ns structbiol.test.dssp
  (:use [structbiol.dssp]
        [clojure.test]
        midje.sweet)
  (:require structbiol.dssp)
  (:import [structbiol.dssp DSSP]))
        
(def test-atom "  516  767 B D  B <   -Q  471   0E 114     -3,-1.5   -45,-0.2   -45,-0.3    -1,-0.1  -0.926  55.6-110.0-170.6-167.5   19.6   18.5   17.5")

(def test-residue-header 
    "  530  4  0  0  0 TOTAL NUMBER OF RESIDUES, NUMBER OF CHAINS, NUMBER OF SS-BRIDGES(TOTAL,INTRACHAIN,INTERCHAIN)")


(fact (sort (keys (parse-atom-line test-atom))) => (sort [
							  :chain :residue :aa 
							  :ss :asa :x-ca :y-ca
							  :z-ca :kappa :alpha :phi :psi] ))

(deftest test-residue-header-parser 
  (is (= (parse-residues-header test-residue-header) {:number-of-residues 530
						      :number-of-chains 4
						      :ss-total 0
						      :ss-intrachain 0
						      :ss-interchain 0
						      })))

(deftest read-throws
  (is (thrown? Exception (read-dssp "not a file"))))

(deftest test-read-dssp
  (is (= (sort (keys (read-dssp "resources/1hnn.dssp"))) (sort [:number-of-residues
								:number-of-chains
								:ss-total
								:ss-intrachain
								:ss-interchain
								:atoms
								:surface-area]))))

(deftest test-chain-identifiers
  (def dssp (read-dssp "resources/1hnn.dssp"))
  (is (= (chain-idenfifiers dssp) '("A" "B"))))




