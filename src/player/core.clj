(ns player.core
  (:import java.io.File)
  (:gen-class))
(use 'clj-audio.core)
(use 'seesaw.core)

(def f (frame :title "Mp3 Player"))


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

(defn start-window []
  (native!)
  (-> f pack! show!))

(defn display [content]
  (config! f :content content)
  content)

(defn -main
  [& args]
  (start-window)
  (def lb (listbox :model (mp3-paths (File. "."))))
  (def b-play (button :text "Play"))
  (def b-stop (button :text "Stop"))
  (listen b-play :action (fn [e] (.start(Thread. #(-> (->stream (selection lb)) decode play )))))
  (listen b-stop :action (fn [e] (stop)))
  (def split (left-right-split (scrollable lb) (grid-panel :columns 1
              :items [b-play b-stop]) :divider-location 1/3))
  (display split)
)
;; (-main)
