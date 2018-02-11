package org.tron.network

import org.tron.core.Sha256Hash
import org.tron.network.message.Message

trait Node {
  def setNodeDelegate(nodeDel: NodeDelegate): Unit
  def broadcast(msg: Message): Unit
  def listen(): Unit
  def connectToP2PNetWork(): Unit
  def syncFrom(blockHash: Sha256Hash): Unit
  def close(): Unit
}
