package org.tron.core

case class SpendableOutputs(
  amount: Long,
  unspentOutputs: Map[String, Array[Long]])