package org.tron.network.message

import org.tron.protos.Tron.Transaction

object TransactionMessage extends MessageReader {
  def fromBytes(data: Array[Byte]): Message = {
    TransactionMessage(Transaction.parseFrom(data))
  }
}

case class TransactionMessage(transaction: Transaction) extends Message {
  val messageType = MessageTypes.TRX

  def toBytes: Array[Byte] = transaction.toByteArray
}
