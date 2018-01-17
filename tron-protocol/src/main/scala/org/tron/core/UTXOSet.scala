package org.tron.core

import com.google.protobuf.InvalidProtocolBufferException
import org.tron.protos.core.TronTXOutputs
import org.tron.protos.core.TronTXOutputs.TXOutputs
import org.tron.storage.LevelDbDataSourceImpl
import org.tron.utils.ByteArray
import org.tron.utils.ByteStringUtils._

class UTXOSet(
  val txDB: LevelDbDataSourceImpl,
  val blockchain: Blockchain) {

  def reindex(): Unit = {
    txDB.resetDB()

    for {
      (key, value) <- blockchain.findUTXO()
      txOutput <- value.outputs
    } {
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
      try {
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
      } catch {
        case e: InvalidProtocolBufferException =>
          e.printStackTrace()
      }
    }

    SpendableOutputs(accumulated, unspentOutputs.toMap)
  }

  def findUTXO(pubKeyHash: PublicKey) = {

    txDB
      // Take all keys
      .allKeys
      // Retrieve data for each key
      .map(key => txDB.get(key).get)
      // Find all outputs
      .flatMap { txData =>
        TXOutputs.parseFrom(txData).outputs.filter(txOutput => {
          val txOutputHex = txOutput.pubKeyHash.hex
          pubKeyHash.hex == txOutputHex
        })
      }
      .toArray
  }
}