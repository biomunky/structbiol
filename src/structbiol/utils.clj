(ns structbiol.utils
  (:use [clojure.java.io  :only [reader writer]]
	[clojure.string   :only [trim join]])
  (:import (java.io File)))

(defn create-directory [name]
  "Try to create a directory in . named 'name'. If already exists print a message. If it can't it throws"
  (let [f (File. name)]
    (cond
     (.isDirectory f) true
     :else (try
		 			 (.mkdir (File. name))
	     (catch Exception e (throw "Failed to create" name))))))

(defn fetch-url-content [url output-dir outputname]
  "Given a url, output directory and the name of the output file attempt to download data.
	DOES NOT WORK FOR ZIPPED RESOURCES."
  (cond
   (.isFile (File. (str output-dir "/" outputname))) 
			(do (println "You've already got" (str output-dir "/" outputname)) true)
   :else
   (do (try
				(create-directory output-dir)
       	(with-open [r (reader url) wtr (writer (str output-dir "/" outputname))]
	   	 	(.write wtr (join "\n" (line-seq r))) true)
				(catch Exception e (do (println "Failed to fetch" url) false))))))


