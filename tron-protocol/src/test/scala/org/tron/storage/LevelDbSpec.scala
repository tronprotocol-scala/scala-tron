package org.tron
package storage

import org.specs2.mutable.Specification
import org.tron.utils.ByteArrayUtils


class LevelDbSpec extends Specification with DatabaseContext {

  sequential

  "Level DB" should {

    "put and get data" in { db: LevelDbDataSourceImpl =>
      val key = "000134yyyhy".getBytes

      val value = "50000".getBytes

      awaitResult(db.put(key, value))

      val storedValue = awaitResult(db.get(key)).get
      val s = ByteArrayUtils.toString(storedValue)

      ByteArrayUtils.toString(value) must equalTo(s)
    }

    "put data" in { dataSource: LevelDbDataSourceImpl =>
      val key1 = "000134yyyhy"
      val key = key1.getBytes

      val value1 = "50000"
      val value = value1.getBytes

      awaitResult(dataSource.put(key, value))

      awaitResult(dataSource.get(key)) must beSome
      awaitResult(dataSource.allKeys).size must equalTo(1)
    }

    "reset data" in { dataSource: LevelDbDataSourceImpl =>
      awaitResult(dataSource.resetDB())
      ok
    }
  }
}