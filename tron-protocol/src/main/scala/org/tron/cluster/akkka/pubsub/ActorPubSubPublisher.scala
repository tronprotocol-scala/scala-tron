package org.tron.cluster.akkka.pubsub

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish

@Singleton
class ActorPubSubPublisher @Inject()(system: ActorSystem) {

  private val mediator = DistributedPubSub(system).mediator

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
    */
  def publishToGroup(eventName: String, message: Any) = {
    mediator ! Publish(
      topic = eventName,
      msg = message)
  }
}
