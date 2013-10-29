(use 'clojure.xml)
(use 'clojure.java.io)

(defn one-component-to-element-map
  [component]
  (let [e-name (name (first component))
        the-attrs (second component)
        other-stuff (rest (rest component))]
    {:tag e-name
     :attrs the-attrs
     :content (if (empty? other-stuff)
                nil
                (vec (map one-component-to-element-map other-stuff)))}))

(defmacro spring-integration
  [& body]
  `(emit-element {:tag "beans"
                  :attrs {:top "level"}
                  :content ~(vec (map one-component-to-element-map body))}))

(to-xml
  (beans
    (channel {:id "foo"})
    (transformer {:input-channel "foo" :output-channel (str "b" "a" "r")})
    (channel {:id "bar"})
    (chain {:input-channel "bar" :output-channel "baz"}
      (filter {:expression "#{headers['allowThisMessage']}"})
      (header-enricher {}
        (reply-channel {:value "nullChannel"}))
      (transformer {:ref "bob"}))))