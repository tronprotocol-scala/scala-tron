package org.tron.core

import org.tron.protos.core.TronTransaction.Transaction

trait TransactionStrategy {

  def newTransaction(transaction: Transaction): Unit

}
