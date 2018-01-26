package org.tron.storage

import org.tron.BlockChainDb

trait DbFactory {

  def exists(name: String): Boolean

  def build(name: String): BlockChainDb
}
