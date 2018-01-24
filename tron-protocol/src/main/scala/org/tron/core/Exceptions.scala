package org.tron.core


sealed trait Exception

case class TransactionException(message: String) extends Exception

case object InsufficientFunds extends Exception
