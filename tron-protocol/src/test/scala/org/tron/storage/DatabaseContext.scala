package org.tron.storage

import org.specs2.execute.{AsResult, Result}
import org.specs2.specification.ForEach
import org.tron.BlockChainDb
import org.tron.application.Module

trait DatabaseContext extends ForEach[BlockChainDb] {

  val module = new Module()
  val dbFactory: DbFactory = module.buildDbFactory()

  def foreach[R: AsResult](f: BlockChainDb => R): Result = {
    val db = openDb
    try AsResult(f(db))
    finally closeDb(db)
  }

  // create and close a transaction
  def openDb: BlockChainDb = dbFactory.build("test")

  def closeDb(t: BlockChainDb): Unit = t.close()
}