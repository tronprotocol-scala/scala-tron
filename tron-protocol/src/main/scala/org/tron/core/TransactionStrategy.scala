package org.tron.core

import org.tron.protos.Tron.Transaction

trait TransactionStrategy {

  def newTransaction(transaction: Transaction): Unit

}
