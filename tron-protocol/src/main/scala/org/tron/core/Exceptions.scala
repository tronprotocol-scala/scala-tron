package org.tron.core

object Exceptions {

  case class TransactionException(message: String) extends Exception(message)

}
