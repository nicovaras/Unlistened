(ns player.core
  (:gen-class))
(use 'clj-audio.core)
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (-> (java.io.File. ".") .getAbsolutePath))
  (-> (->stream "a.mp3") decode play))
