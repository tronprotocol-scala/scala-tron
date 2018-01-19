package org.tron.cluster.akkka.pubsub

import akka.actor.ActorSystem
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

object PubSubStreamFactory {

  /**
    * Subscribe to topic
    */
  def subscribeTo(topic: String)(implicit actorSystem: ActorSystem): Source[Any, Unit] = {
    Source.actorRef[Any](0, OverflowStrategy.dropHead)
      .mapMaterializedValue { actorRef =>
        DistributedPubSub(actorSystem).mediator ! Subscribe(topic, actorRef)
      }
  }

  /**
    * Subscribes to a group
    */
  def subscribeToGroup(topic: String, group: String)(implicit actorSystem: ActorSystem): Source[Any, Unit] = {
    Source.actorRef[Any](0, OverflowStrategy.dropHead)
      .mapMaterializedValue { actorRef =>
        DistributedPubSub(actorSystem).mediator ! Subscribe(topic, Some(group), actorRef)
      }
  }

  def publishTo(topic: String)(implicit actorSystem: ActorSystem): Sink[Any, _] = {
    Sink.lazyInit[Any, Any](_ => {
      val mediator = DistributedPubSub(actorSystem).mediator
      Future.successful(Sink.foreach(m => mediator ! Publish(topic, m)))
    }, () => ())
  }

  def publishToGroup(topic: String)(implicit actorSystem: ActorSystem): Sink[Any, _] = {
    Sink.lazyInit[Any, Any](_ => {
      val mediator = DistributedPubSub(actorSystem).mediator
      Future.successful(Sink.foreach(m => mediator ! Publish(topic, m, sendOneMessageToEachGroup = true)))
    }, () => ())
  }

}
