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

(defn start-playing []
  (.start(Thread. #(-> (->stream (selection lb)) decode play ))))

(defn setup-gui []
  (native!)
  (-> f pack! show!)
  (def lb (listbox :model (mp3-paths (File. "."))))
  (def b-play (button :text "Play"))
  (def b-stop (button :text "Stop"))
  (def b-prev (button :text "Prev"))
  (def b-next (button :text "Next"))
  (listen b-play :action (fn [e]
                           (stop)
                           (start-playing)))
  (listen b-stop :action (fn [e] (stop)))
  (listen b-prev :action (fn [e]
    (stop)
    (.setSelectedIndex lb (mod (dec (-> lb .getSelectedIndex)) (-> lb .getModel .getSize)))
    (start-playing)))
  (listen b-next :action (fn [e]
    (stop)
    (.setSelectedIndex lb (mod (inc (-> lb .getSelectedIndex)) (-> lb .getModel .getSize)))
    (start-playing)))

  (def split (left-right-split (scrollable lb) (grid-panel :columns 2
              :items [b-play b-stop b-prev b-next]) :divider-location 1/3))
  (display split))

(defn display [content]
  (config! f :content content)
  content)

(defn -main
  [& args]
  (setup-gui))

(-main)



