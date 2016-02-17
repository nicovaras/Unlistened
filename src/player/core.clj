(ns player.core
  (:import java.io.File)
  (:gen-class))
(use 'clj-audio.core)
(use 'seesaw.core)
(use 'seesaw.chooser)
(require '[clojure.string :as str])
(require 'clojure.tools.trace)

(def listened-songs #{})
(def folder (atom {}))
(defn get-folder []
  (@folder :folder))
(defn update-folder [val]
  (swap! folder assoc :folder val))
(update-folder ".")

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
         (mp3-paths (File. (get-folder)))))
  )

(def f (frame :title "Mp3 Player" :on-close :exit))
(def b-play (button :text "Play"))
(def b-stop (button :text "Stop"))
(def b-prev (button :text "Prev"))
(def b-next (button :text "Next"))
(def b-random (button :text "Random"))
(def b-folder (button :text "Open dir"))
(def lb (listbox :model (keys (mp3-data))))
(def lb-listened (listbox))
(def lbl-remaining (label :text (str "Total " (-> lb .getModel .getSize))))
(.setSelectedIndex lb 0)

(defn set-label-text []
  (config! lbl-remaining :text (str (-> lb .getSelectedIndex inc) "/" (-> lb .getModel .getSize))))

(defn filtered-songs []
    (filter
      (fn [x] (not (contains? listened-songs x)))
      (keys (mp3-data))))

(defn update-lists []
  (let [model (javax.swing.DefaultListModel.)
        items (filtered-songs)]
    (doseq [item items]
      (.addElement model item))
    (.setModel lb model))

  (let [model (javax.swing.DefaultListModel.)]
    (doseq [item listened-songs]
      (.addElement model item))
  (.setModel lb-listened model)))


(defn  start-playing []
  (let [current-song (symbol (selection lb))]
  (set-label-text)
  (.start (Thread.
    #(->
      (->stream (:path (current-song (mp3-data)))) decode play )))
  (def listened-songs (conj listened-songs current-song))
  (update-lists)
  (println listened-songs)
  (.setSelectedIndex lb 0)
    ))
; .(clojure.tools.trace/dotrace [mp3-data] (mp3-data))

(defn display [content]
  (config! f :content content)
  content)

(defn setup-gui []
  (listen b-folder :action
    (fn [e]
      (update-folder
        (.getAbsolutePath (choose-file :selection-mode :dirs-only)))
      (let [model (javax.swing.DefaultListModel.)]
        (doseq [item (keys (mp3-data))]
          (.addElement model item))
        (.setModel lb model)
      )
    )
  )


  (listen b-play :action (fn [e]
                           (stop)
                           (start-playing)))
  (listen b-random :action (fn [e]
                            (.setSelectedIndex lb  (rand-int (-> lb .getModel .getSize))) ))
  (listen b-stop :action (fn [e] (stop)))
  (listen b-prev :action (fn [e]
    (stop)
    (.setSelectedIndex lb (mod (dec (-> lb .getSelectedIndex)) (-> lb .getModel .getSize)))
    (start-playing)))
  (listen b-next :action (fn [e]
    (stop)
    (.setSelectedIndex lb (mod (inc (-> lb .getSelectedIndex)) (-> lb .getModel .getSize)))
    (start-playing)))

  (listen lb-listened :mouse-clicked (fn [e]
    (println listened-songs)
    (def listened-songs
      (disj listened-songs (-> lb-listened .getSelectedValue) ))
    (println listened-songs)
    (update-lists)))

  (def split (left-right-split
               (top-bottom-split (top-bottom-split (scrollable lb) (scrollable lb-listened)) lbl-remaining :divider-location 9/10)
               (grid-panel :columns 3 :items [ b-play b-stop b-prev b-next b-random b-folder])
                               :divider-location 1/3))
  (display split)
  (native!)
  (-> f pack! show!))

(defn -main
  [& args]
  (setup-gui))

; (-main)



