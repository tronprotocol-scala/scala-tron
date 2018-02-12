package org.tron.utxo

case class SpendableOutputs(
  amount: Long,
  unspentOutputs: Map[String, Array[Long]])