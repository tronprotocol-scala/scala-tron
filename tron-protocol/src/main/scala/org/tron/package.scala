package org

import org.tron.core.Sha256Hash
import org.tron.storage.DataSource

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._

package object tron {

  /**
    * Blockchain DataSource
    */
  type DefaultDB = DataSource[Array[Byte], Array[Byte]]

  /**
    * Transaction Identifier
    */
  type TXID = Array[Byte]

  /**
    * Default Hash Type
    */
  type Hash = Sha256Hash

  def awaitResult[T](awaitable: Awaitable[T]): T = Await.result(awaitable, Duration.Inf)

  def awaitResult[T](awaitable: Awaitable[T], seconds: Int): T = Await.result(awaitable, seconds.seconds)
}
