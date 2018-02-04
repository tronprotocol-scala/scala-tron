package org.tron
package core

import org.tron.protos.Tron.{TXOutput, TXOutputs}
import org.tron.utils.ByteArrayUtils
import org.tron.utils.ByteStringUtils._

class UTXOSet(
               val txDB: DefaultDB,
               val blockchain: Blockchain) {

  def reindex(): Unit = {
    awaitResult(txDB.resetDB())

    blockchain.findUTXO().foreach { case (key, value) =>
      txDB.put(ByteArrayUtils.fromHexString(key), value.toByteArray)
    }
  }

  def getBalance(key: Key) = {
    findUTXO(key.address).map(_.value).sum
  }

  def getBalance(address: Address) = {
    findUTXO(address).map(_.value).sum
  }

  def findSpendableOutputs(address: Address, amount: Long): SpendableOutputs = {
    val unspentOutputs = scala.collection.mutable.Map[String, Array[Long]]()

    var accumulated = 0L
    val keySet = awaitResult(txDB.allKeys)
    for (key <- keySet) {
      val txOutputsData = awaitResult(txDB.get(key)).get
      val txOutputs = TXOutputs.parseFrom(txOutputsData)
      val len = txOutputs.outputs.size
      for (i <- 0 until len) {
        val txOutput = txOutputs.outputs(i)
        if (accumulated < amount && address.hex == txOutput.pubKeyHash.hex) {
          accumulated += txOutput.value
          val v = unspentOutputs.getOrElse(ByteArrayUtils.toHexString(key), Array[Long]())
          unspentOutputs.put(ByteArrayUtils.toHexString(key), v :+ i.toLong)
        }
      }
    }

    SpendableOutputs(accumulated, unspentOutputs.toMap)
  }

  def findUTXO(address: Address): Set[TXOutput] = {

    awaitResult(txDB.allKeys)
      // Retrieve data for each key
      .flatMap(key => {
        val result = awaitResult(txDB.get(key))
        result
      })
      // Find all outputs
      .flatMap { txData =>
        TXOutputs.parseFrom(txData).outputs.filter(txOutput => {
          val tXOutputHex = txOutput.pubKeyHash.hex
          address.hex == tXOutputHex
        })
      }.toSet
  }
}