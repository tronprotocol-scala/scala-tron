package org.tron.cluster

import javax.inject.Inject

import akka.actor.ActorSystem
import org.tron.cluster.pubsub.ActorPubSubPublisher
import org.tron.core.TransactionStrategy
import org.tron.protos.Tron.Transaction

class ClusterTransactionStrategy @Inject() (system: ActorSystem) extends TransactionStrategy {

  val publisher = new ActorPubSubPublisher(system)

  def newTransaction(transaction: Transaction): Unit = {
    publisher.publish("transaction", transaction)
  }
}
