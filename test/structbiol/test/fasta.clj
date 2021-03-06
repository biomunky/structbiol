(ns structbiol.test.fasta
    (:use [structbiol.fasta]
        [clojure.test]
        midje.sweet))

(def sample-fasta
     {">1HNN:A|PDBID|CHAIN|SEQUENCE", "MSGADRSPNAGAAPDSAPGQAAVASAYQRFEPRAYLRNNYAPPRGDLCNPNGVGPWKLRCLAQTFATGEVSGRTLIDIGSGPTVYQLLSACSHFEDITMTDFLEVNRQELGRWLQEEPGAFNWSMYSQHACLIEGKGECWQDKERQLRARVKRVLPIDVHQPQPLGAGSPAPLPADALVSAFCLEAVSPDLASFQRALDHITTLLRPGGHLLLIGALEESWYLAGEARLTVVPVSEEEVREALVRSGYKVRDLRTYIMPAHLQTGVDDVKGVFFAWAQKVGL"
      ">1HNN:B|PDBID|CHAIN|SEQUENCE", "MSGADRSPNAGAAPDSAPGQAAVASAYQRFEPRAYLRNNYAPPRGDLCNPNGVGPWKLRCLAQTFATGEVSGRTLIDIGSGPTVYQLLSACSHFEDITMTDFLEVNRQELGRWLQEEPGAFNWSMYSQHACLIEGKGECWQDKERQLRARVKRVLPIDVHQPQPLGAGSPAPLPADALVSAFCLEAVSPDLASFQRALDHITTLLRPGGHLLLIGALEESWYLAGEARLTVVPVSEEEVREALVRSGYKVRDLRTYIMPAHLQTGVDDVKGVFFAWAQKVGL"})

(deftest test-read-fasta
  (is (= (read-fasta-file "resources/1hnn.fasta") sample-fasta )))
