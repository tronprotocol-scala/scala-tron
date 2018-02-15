package org.tron.cluster

import javax.inject.Inject

import akka.actor.ActorSystem
import org.tron.cluster.pubsub.ActorPubSubPublisher
import org.tron.protos.Tron.Transaction
import org.tron.utxo.TransactionStrategy

class ClusterTransactionStrategy @Inject() (system: ActorSystem) extends TransactionStrategy {

  val publisher = new ActorPubSubPublisher(system)

  def newTransaction(transaction: Transaction): Unit = {
    publisher.publish("transaction", transaction)
  }
}
