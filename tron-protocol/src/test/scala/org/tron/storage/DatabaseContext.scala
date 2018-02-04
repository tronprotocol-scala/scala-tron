package org.tron.storage

import org.specs2.execute.{AsResult, Result}
import org.specs2.specification.ForEach
import org.tron.DefaultDB
import org.tron.application.Module
import org.tron.application.{AppFactory, Module}

trait DatabaseContext extends ForEach[DefaultDB] {

  val module = AppFactory.buildInjector
  val dbFactory = module.getInstance(classOf[DbFactory])

  def foreach[R: AsResult](f: DefaultDB => R): Result = {
    val db = openDb
    try AsResult(f(db))
    finally closeDb(db)
  }

  // create and close a transaction
  def openDb: DefaultDB = dbFactory.build("test")

  def closeDb(t: DefaultDB): Unit = t.close()
}