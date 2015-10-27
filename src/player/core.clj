(ns player.core
  (:import java.io.File)
  (:gen-class))
(use 'clj-audio.core)

(defn mp3-paths [d]
  (filter
    (fn [x] (re-matches #".*mp3" (str x)))
    (reduce
     (fn [x f]
       (if (.isDirectory f)
         (concat x (mp3-paths f))
         (concat x [(.getCanonicalPath f)])))
     []
     (.listFiles d))))


(defn -main
  [& args]
  (let [first-mp3 ( first (mp3-paths (File. ".")))]
    (println first-mp3)
    (-> (->stream first-mp3) decode play)
  ))
