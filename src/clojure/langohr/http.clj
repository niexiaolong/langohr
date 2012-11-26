(ns langohr.http
  (:refer-clojure :exclude [get])
  (:require [clj-http.client :as http]
            [cheshire.core   :as json]
            [clojure.string  :as s])
  (:import java.net.URLEncoder))

;;
;; Implementation
;;

;; a good default for now. RabbitMQ 3.0 will redirect
;; from port 55672 to 15672.
(def ^:dynamic *endpoint* "http://127.0.0.1:55672")

(def ^:dynamic *username* "guest")
(def ^:dynamic *password* "guest")


;;
;; Implementation
;;

(def ^:const throw-exceptions false)

(def ^{:const true} slash    "/")

(defn url-with-path
  [& segments]
  (str *endpoint* slash (s/join slash segments)))


(defn post
  [^String uri &{:keys [body] :as options}]
  (io! (:body (http/post uri (merge options {:accept :json :basic-auth [*username* *password*] :body (json/encode body)}))) true))

(defn put
  [^String uri &{:keys [body] :as options}]
  (io! (:body (http/put uri (merge options {:accept :json :basic-auth [*username* *password*] :body (json/encode body) :throw-exceptions throw-exceptions}))) true))

(defn get
  ([^String uri]
     (io! (json/decode (:body (http/get uri {:accept :json :basic-auth [*username* *password*] :throw-exceptions throw-exceptions})) true)))
  ([^String uri &{:as options}]
     (io! (json/decode (:body (http/get uri (merge options {:accept :json :basic-auth [*username* *password*] :throw-exceptions throw-exceptions}))) true))))

(defn head
  [^String uri]
  (io! (http/head uri {:accept :json :basic-auth [*username* *password*] :throw-exceptions throw-exceptions})))

(defn delete
  ([^String uri]
     (io! (:body (http/delete uri {:accept :json :basic-auth [*username* *password*] :throw-exceptions throw-exceptions})) true))
  ([^String uri &{:keys [body] :as options}]
     (io! (:body (http/delete uri (merge options {:accept :json :basic-auth [*username* *password*] :body (json/encode body) :throw-exceptions throw-exceptions}))) true)))



;;
;; API
;;

(defn connect!
  [^String endpoint ^String username ^String password]
  (alter-var-root (var *endpoint*) (constantly endpoint))
  (alter-var-root (var *username*) (constantly username))
  (alter-var-root (var *password*) (constantly password)))


(defn get-overview
  []
  (get (url-with-path "/api/overview")))


(defn list-nodes
  []
  (get (url-with-path "/api/nodes")))

(defn get-node
  [^String node]
  (get (url-with-path (str "/api/nodes/" node))))


(defn list-extensions
  []
  (get (url-with-path "/api/extensions")))


(defn list-definitions
  []
  (get (url-with-path "/api/definitions")))

(defn list-connections
  []
  (get (url-with-path "/api/connections")))

(defn get-connection
  [^String id]
  (get (url-with-path (str "/api/nodes/" id))))

(defn list-channels
  []
  (get (url-with-path "/api/channels")))

(defn list-exchanges
  ([]
     (get (url-with-path "/api/exchanges")))
  ([^String vhost]
     (get (url-with-path (str "/api/exchanges/" (URLEncoder/encode vhost))))))

(defn get-exchange
  [^String vhost ^String exchange]
  (get (url-with-path (format "/api/exchanges/%s/%s" (URLEncoder/encode vhost) (URLEncoder/encode exchange)))))

(defn declare-exchange
  [^String vhost ^String exchange & {:as properties}]
  (post (url-with-path (format "/api/exchanges/%s/%s" (URLEncoder/encode vhost) (URLEncoder/encode exchange)))))

(defn delete-exchange
  [^String vhost ^String exchange]
  (delete (url-with-path (format "/api/exchanges/%s/%s" (URLEncoder/encode vhost) (URLEncoder/encode exchange)))))

(defn list-bindings-for-which-exchange-is-the-source
  [^String vhost ^String name]
  )

(defn list-bindings-for-which-exchange-is-the-destination
  [^String vhost ^String name]
  )

(defn publish
  [^String vhost ^String exchange]
  )

(defn list-queues
  ([]
     )
  ([^String vhost]
     ))

(defn get-queue
  [^String vhost ^String name]
  )

(defn declare-queue
  [^String vhost ^String name properties]
  )

(defn delete-queue
  [^String vhost ^String name]
  )

(defn purge-queue
  [^String vhost ^String name]
  )

(defn list-bindings
  [^String vhost ^String queue]
  )

(defn get-message
  [^String vhost ^String queue]
  )

(defn list-bindings
  ([]
     )
  ([^String vhost]
     ))

(defn bind
  [^String vhost ^String exchange ^String queue]
  )

(defn list-vhosts
  []
  )

(defn get-vhost
  [^String vhost]
  )

(defn list-permissions
  [^String vhost]
  )

(defn get-permissions
  [^String vhost ^String username]
  )

(defn aliveness-test
  [^String vhost]
  )
