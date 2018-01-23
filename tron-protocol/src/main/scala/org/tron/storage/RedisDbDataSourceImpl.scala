package org.tron.storage

import java.io.File
import java.net.InetSocketAddress

import akka.actor.Props
import akka.io.Tcp.Message
import redis.RedisClient
import redis.actors.RedisSubscriberActor
import redis.api.pubsub
import redis.api.pubsub.PMessage

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class RedisDbDataSourceImpl(dbFolder: File, name: String = "default") extends DataSource[Array[Byte], Array[Byte]] {

  override def initDB(): Unit = {
    implicit val akkaSystem = akka.actor.ActorSystem()
    val redis = RedisClient()

    // publish after 2 seconds every 2 or 5 seconds
    akkaSystem.scheduler.schedule(2 seconds, 2 seconds)(redis.publish("time", System.currentTimeMillis()))
    akkaSystem.scheduler.schedule(2 seconds, 5 seconds)(redis.publish("pattern.match", "pattern value"))
    akkaSystem.scheduler.scheduleOnce(20 seconds)(akkaSystem.terminate())

    val channels = Seq("time")
    val patterns = Seq("pattern.*")

    akkaSystem.actorOf(Props(classOf[SubscribeActor], channels, patterns).withDispatcher("rediscala.rediscala-client-worker-dispatcher"))

  }

  override def put(key: Array[Byte], value: Array[Byte]): Unit = ???

  override def get(key: Array[Byte]): Option[Array[Byte]] = ???

  override def delete(key: Array[Byte]): Unit = ???

  override def close(): Unit = ???

  override def resetDB(): Unit = ???

  override def allKeys: Set[Array[Byte]] = ???
}

class SubscribeActor(channels: Seq[String] = Nil, patterns: Seq[String] = Nil)
  extends RedisSubscriberActor(
    new InetSocketAddress("localhost", 6379),
    channels,
    patterns,
    onConnectStatus = connected => { println(s"connected: $connected")}
  ) {

  override def onMessage(m: pubsub.Message): Unit = {
    println(s"message received: $m")
  }

  override def onPMessage(pm: PMessage): Unit = {
    println(s"pattern message received: $pm")
  }
}
