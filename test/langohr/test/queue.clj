(set! *warn-on-reflection* true)

(ns langohr.test.queue
  (:import (com.rabbitmq.client Connection Channel AMQP AMQP$Queue$DeclareOk AMQP$Queue$BindOk))
  (:use [clojure.test] [langohr.core :as lhc] [langohr.queue :as lhq]))

;;
;; queue.declare
;;

(defonce ^Connection *conn* (lhc/connect))


(deftest t-declare-a-server-named-queue-with-default-attributes
  (let [^Channel              channel    (lhc/create-channel *conn*)
        ^AMQP$Queue$DeclareOk declare-ok (lhq/declare channel)]
    (is (lhc/open? *conn*))
    (is (lhc/open? channel))
    (is (instance? AMQP$Queue$DeclareOk declare-ok))
    (is (re-seq #"^amq\.gen-(.+)" (.getQueue declare-ok)))
    (is (= 0 (.getConsumerCount declare-ok)))
    (is (= 0 (.getMessageCount declare-ok)))))


(deftest t-declare-a-client-named-queue-with-default-attributes
  (let  [^Channel              channel    (lhc/create-channel *conn*)
         ^String               queue-name "langohr.tests.queues.client-named-with-default-attributes"
         ^AMQP$Queue$DeclareOk declare-ok (lhq/declare channel queue-name)]
    (is (= (.getQueue declare-ok) queue-name))))


(deftest t-declare-a-non-durable-exclusive-auto-deleted-client-named-queue
  (let  [^Channel              channel    (lhc/create-channel *conn*)
         ^String               queue-name "langohr.tests.queues.client-named.non-durable.exclusive.auto-deleted"
         ^AMQP$Queue$DeclareOk declare-ok (lhq/declare channel queue-name { :durable false, :exclusive true, :auto-delete true })]
    (is (= (.getQueue declare-ok) queue-name))))


(deftest t-declare-a-durable-non-exclusive-non-auto-deleted-client-named-queue
  (let  [^Channel              channel    (lhc/create-channel *conn*)
         ^String               queue-name "langohr.tests.queues.client-named.durable.non-exclusive.non-auto-deleted"
         ^AMQP$Queue$DeclareOk declare-ok (lhq/declare channel queue-name { :durable true, :exclusive false, :auto-delete false })]
    (is (= (.getQueue declare-ok) queue-name))))



;;
;; queue.bind
;;

(deftest t-bind-a-server-named-queue-to-amq-fanout
  (let [^Channel              channel  (lhc/create-channel *conn*)
        ^AMQP$Queue$DeclareOk declare-ok (lhq/declare channel)
        queue    (.getQueue declare-ok)
        exchange "amq.fanout"
        bind-ok  (lhq/bind channel queue exchange)]
    (is (instance? AMQP$Queue$BindOk bind-ok))))


(deftest t-bind-a-client-named-queue-to-amq-fanout
  (let [channel  (lhc/create-channel *conn*)
        queue    "langohr.tests.queues.client-named.non-durable.exclusive.auto-deleted"
        exchange "amq.fanout"
        bind-ok  (do
                   (lhq/declare channel queue)
                   (lhq/bind    channel queue exchange))]
        (is (instance? AMQP$Queue$BindOk bind-ok))))
