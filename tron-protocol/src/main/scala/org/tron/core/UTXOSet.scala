package org.tron.core

import org.tron.protos.core.TronTXOutput.TXOutput
import org.tron.protos.core.TronTXOutputs.TXOutputs
import org.tron.storage.LevelDbDataSourceImpl
import org.tron.utils.ByteArray
import org.tron.utils.ByteStringUtils._

class UTXOSet(
  val txDB: LevelDbDataSourceImpl,
  val blockchain: Blockchain) {

  def reindex(): Unit = {
    txDB.resetDB()

    blockchain.findUTXO().foreach { case (key, value) =>
      txDB.put(ByteArray.fromHexString(key), value.toByteArray)
    }
  }

  def getBalance(key: PublicKey) = {
    findUTXO(key).map(_.value).sum
  }

  def findSpendableOutputs(pubKey: PublicKey, amount: Long): SpendableOutputs = {
    val unspentOutputs = scala.collection.mutable.Map[String, Array[Long]]()

    var accumulated = 0L
    val keySet = txDB.allKeys
    for (key <- keySet) {
      val txOutputsData = txDB.get(key).get
      val txOutputs = TXOutputs.parseFrom(txOutputsData)
      val len = txOutputs.outputs.size
      for (i <- 0 until len) {
        val txOutput = txOutputs.outputs(i)
        if (pubKey.hex == ByteArray.toHexString(txOutput.pubKeyHash.toByteArray) && accumulated < amount) {
          accumulated += txOutput.value
          val v = unspentOutputs.getOrElse(ByteArray.toHexString(key), Array[Long]())
          unspentOutputs.put(ByteArray.toHexString(key), v :+ i.toLong)
        }
      }
    }

    SpendableOutputs(accumulated, unspentOutputs.toMap)
  }

  def findUTXO(pubKeyHash: PublicKey): Set[TXOutput] = {

    txDB
      // Take all keys
      .allKeys
      // Retrieve data for each key
      .flatMap(key => txDB.get(key))
      // Find all outputs
      .flatMap { txData =>
        TXOutputs.parseFrom(txData).outputs.filter(txOutput => {
          val txOutputHex = txOutput.pubKeyHash.hex
          pubKeyHash.hex == txOutputHex
        })
      }
  }
}