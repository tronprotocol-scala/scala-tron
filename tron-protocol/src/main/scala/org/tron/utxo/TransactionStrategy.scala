package org.tron.utxo

import org.tron.protos.Tron.Transaction

trait TransactionStrategy {

  def newTransaction(transaction: Transaction): Unit

}
