(ns player.core
  (:import java.io.File)
  (:gen-class))
(use 'clj-audio.core)
(use 'seesaw.core)
(require '[clojure.string :as str])
(require 'clojure.tools.trace)

(def listened-songs #{})

(defn mp3-paths [d]
  (filter
    (fn [x] (re-matches #".*mp3" (str x)))
    (reduce
     (fn [x f]
       (if (.isDirectory f)
         (concat x (mp3-paths f))
         (concat x [(-> f .getCanonicalPath)])))
     []
     (-> d .listFiles))))

(defn split-path [path]
  (last (str/split path #"/" )))

(defn  mp3-data []
  (reduce
    (fn [x f] (merge x f))
    {}
    (map (fn [x] {(symbol (split-path x)){:path x :played false }})
         (mp3-paths (File. "."))))
  )

(def f (frame :title "Mp3 Player"))
(def b-play (button :text "Play"))
(def b-stop (button :text "Stop"))
(def b-prev (button :text "Prev"))
(def b-next (button :text "Next"))
(def lb (listbox :model (keys (mp3-data))))
(def lbl-remaining (label :text (str "Total " (-> lb .getModel .getSize))))

(defn set-label-text []
  (config! lbl-remaining :text (str (-> lb .getSelectedIndex inc) "/" (-> lb .getModel .getSize))))

(defn filtered-songs []
    (filter
      (fn [x] (not (contains? listened-songs x)))
      (keys (mp3-data))))

(defn update-lists []
  (let [model (javax.swing.DefaultListModel.)
        items (filtered-songs)]
    (println items)
    (doseq [item items]
      (.addElement model item))
    (.setModel lb model)))


(defn  start-playing []
  (let [current-song (symbol (selection lb))]
  (set-label-text)
  (.start (Thread.
    #(->
      (->stream (:path (current-song (mp3-data)))) decode play )))
  (def listened-songs (conj listened-songs current-song))
  (update-lists)
  (println listened-songs)
    ))
; .(clojure.tools.trace/dotrace [mp3-data] (mp3-data))

(defn display [content]
  (config! f :content content)
  content)

(defn setup-gui []
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

  (def split (left-right-split
               (top-bottom-split (scrollable lb) lbl-remaining :divider-location 9/10)
               (grid-panel :columns 2 :items [b-play b-stop b-prev b-next])
                               :divider-location 1/3))
  (display split)
  (native!)
  (-> f pack! show!))

(defn -main
  [& args]
  (setup-gui))

(-main)



