package org.tron.cluster.pubsub

import akka.NotUsed
import akka.actor.Actor
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, Unsubscribe}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}

/**
  * Adds Cluster Publish / Subscribe methods to the actor
  */
trait ActorPubSub {

  this: Actor =>

  private val mediator = DistributedPubSub(context.system).mediator

  /**
    * Subscribe to the given topic
    *
    * @param topic to subscribe to
    */
  def subscribeTo(topic: String) = {
    mediator ! Subscribe(topic, self)
  }

  /**
    * Unsubscribes to the given topic name
    * Note that destroying the actor automatically unsubscribes
    *
    * @param topic topic to unsubscribe to
    */
  def unsubscribe(topic: String) = {
    mediator ! Unsubscribe(topic, self)
  }

  /**
    * Publishes an event for the given name
    *
    * @param eventName name of the even to publish
    * @param message payload
    */
  def publish(eventName: String, message: Any) = {
    mediator ! Publish(eventName, message)
  }

  /**
    * Publishes an event for the given name
    * to only 1 member in the group
    *
    * @param eventName name of the even to publish
    * @param message payload
    */
  def publishOneToGroup(eventName: String, message: Any) = {
    mediator ! Publish(
      topic = eventName,
      msg = message,
      sendOneMessageToEachGroup = true)
  }

  /**
    * Subscribes to an event and
    * returns a Akka Stream
    */
  def subscribeToStream(eventName: String)(flow: Flow[Any, Any, NotUsed] => Flow[Any, Any, NotUsed]) = {

    implicit val materializer = ActorMaterializer()(context)
    val ref = Source.actorRef(1000, OverflowStrategy.dropHead)
      .via(flow(Flow[Any]))
      .toMat(Sink.foreach(m => self ! m))(Keep.left)
      .run()
    mediator ! Subscribe(eventName, ref)
  }

}
