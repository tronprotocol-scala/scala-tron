package org.tron.core

import org.tron.BlockChainDb
import org.tron.protos.core.TronTXOutput.TXOutput
import org.tron.protos.core.TronTXOutputs.TXOutputs
import org.tron.utils.ByteArrayUtils
import org.tron.utils.ByteStringUtils._

import scala.concurrent.Await
import scala.concurrent.duration._

class UTXOSet(
   val txDB: BlockChainDb,
   val blockchain: Blockchain) {

  def reindex(): Unit = {
    txDB.resetDB()

    blockchain.findUTXO().foreach { case (key, value) =>
      txDB.put(ByteArrayUtils.fromHexString(key), value.toByteArray)
    }
  }

  def getBalance(key: Key) = {
    findUTXO(key.addressHex).map(_.value).sum
  }

  def getBalance(address: String) = {
    findUTXO(address).map(_.value).sum
  }

  def findSpendableOutputs(pubKey: Key, amount: Long): SpendableOutputs = {
    val unspentOutputs = scala.collection.mutable.Map[String, Array[Long]]()

    var accumulated = 0L
    val keySet = Await.result(txDB.allKeys, 5 seconds).map(key => ByteArrayUtils.fromString(key))
    for (key <- keySet) {
      val txOutputsData = Await.result(txDB.get(Array()), 5 seconds).get
      val txOutputs = TXOutputs.parseFrom(txOutputsData)
      val len = txOutputs.outputs.size
      for (i <- 0 until len) {
        val txOutput = txOutputs.outputs(i)
        if (accumulated < amount && pubKey.addressHex == txOutput.pubKeyHash.hex) {
          accumulated += txOutput.value
          val v = unspentOutputs.getOrElse(ByteArrayUtils.toHexString(key), Array[Long]())
          unspentOutputs.put(ByteArrayUtils.toHexString(key), v :+ i.toLong)
        }
      }
    }

    SpendableOutputs(accumulated, unspentOutputs.toMap)
  }

  def findUTXO(address: String): Set[TXOutput] = {
    Await.result(txDB.allKeys, 5 seconds)
      .flatMap(key => Await.result(txDB.get(ByteArrayUtils.fromString(key)), 5 seconds))
      .flatMap { txData =>
        TXOutputs.parseFrom(txData).outputs.filter(txOutput => {
          val tXOutputHex = txOutput.pubKeyHash.hex
          address == tXOutputHex
        })
      }.toSet
  }
}