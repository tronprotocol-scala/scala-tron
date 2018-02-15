package org.tron.network.message

class MessageDeserializer {

  val registry = Map[MessageTypes, MessageReader](
    MessageTypes.BLOCK -> BlockMessage,
    MessageTypes.TRX -> TransactionMessage,
    MessageTypes.INVENTORY -> InventoryMessage,
    MessageTypes.SYNC_BLOCK_CHAIN -> SyncBlockChainMessage,
    MessageTypes.TRX_INVENTORY -> TransactionInventoryMessage,
    MessageTypes.BLOCK_INVENTORY -> BlockInventoryMessage,
  )

  def deserialize(messageTypes: MessageTypes, data: Array[Byte]): Message = {
    registry(messageTypes).fromBytes(data)
  }

}