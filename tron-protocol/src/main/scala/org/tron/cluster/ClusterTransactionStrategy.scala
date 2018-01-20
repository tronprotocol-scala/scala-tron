package org.tron.cluster

import akka.actor.ActorSystem
import org.tron.cluster.pubsub.ActorPubSubPublisher
import org.tron.core.TransactionStrategy
import org.tron.protos.core.TronTransaction.Transaction

class ClusterTransactionStrategy(system: ActorSystem) extends TransactionStrategy {

  val publisher = new ActorPubSubPublisher(system)

  def newTransaction(transaction: Transaction): Unit = {
    publisher.publish("transaction", transaction)
  }
}
