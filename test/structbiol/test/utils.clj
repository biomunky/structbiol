(ns structbiol.test.utils
  (:use [clojure.java.io :only [delete-file]]
	[clojure.test]
        [structbiol.utils]
        midje.sweet))

;;create-directory
(deftest test-create-directory
  (is (= (create-directory "/tmp/test123") true))
  (is (= (create-directory "/afaketmpdir") false))
  (is (= (create-directory "/afaketmpdir/test123") false)))

;;fetch-url-content
(deftest test-fetch-url-content
  (is (= (fetch-url-content "http://www.google.com" "/tmp" "google") true))
  (is (= (fetch-url-content "http://.google.com"    "/tmp" "elgoog") false))
  (delete-file "/tmp/google"))