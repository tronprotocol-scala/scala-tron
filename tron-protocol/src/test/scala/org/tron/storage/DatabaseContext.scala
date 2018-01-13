package org.tron.storage

import org.specs2.execute.{AsResult, Result}
import org.specs2.specification.ForEach
import org.tron.application.Module

trait DatabaseContext extends ForEach[LevelDbDataSourceImpl] {

  val module = new Module()
  val dbFactory = module.buildDbFactory()

  def foreach[R: AsResult](f: LevelDbDataSourceImpl => R): Result = {
    val db = openDb
    try AsResult(f(db))
    finally closeDb(db)
  }

  // create and close a transaction
  def openDb: LevelDbDataSourceImpl = dbFactory.build("test")

  def closeDb(t: LevelDbDataSourceImpl) = t.close()
}
