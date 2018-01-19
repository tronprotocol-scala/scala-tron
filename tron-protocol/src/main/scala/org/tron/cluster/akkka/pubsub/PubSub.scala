package org.tron.cluster.akkka.pubsub

import akka.actor.ActorSystem
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish

object PubSub {

  def publishTo(topic: String, msg: Any)(implicit actorSystem: ActorSystem): Unit = {
    DistributedPubSub(actorSystem).mediator ! Publish(topic, msg)
  }

  def publishToGroup(topic: String, msg: Any)(implicit actorSystem: ActorSystem): Unit = {
    DistributedPubSub(actorSystem).mediator ! Publish(topic, msg, sendOneMessageToEachGroup = true)
  }

}
