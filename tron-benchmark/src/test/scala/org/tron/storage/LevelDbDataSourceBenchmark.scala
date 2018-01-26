package org.tron
package storage

import java.io.File

import akka.actor.ActorSystem
import org.scalameter.api._
import org.tron.utils.ByteArrayUtils
import redis.RedisClient

object LevelDbDataSourceBenchmark extends Bench.LocalTime {


  val sizes = Gen.range("records")(100, 1000, 100)

  val ranges = for {
    size <- sizes
  } yield 0 until size

  performance of "LevelDB Database" in {
    measure method "put" in {

      var database: LevelDbDataSourceImpl = null
      using(ranges) beforeTests {
        database = new LevelDbDataSourceImpl(new File("tron-data/benchmark"), "test-1")
        awaitResult(database.resetDB())
      } afterTests {
        database.close()
      } in { r =>
        for (i <- r) {
          awaitResult(database.put(ByteArrayUtils.fromString("record-" + i), ByteArrayUtils.fromString("data")))
        }
      }
    }
  }

  performance of "Redis Database" in {
    measure method "put" in {

      var database: RedisDbDataSourceImpl = null
      var system: ActorSystem = null
      using(ranges) beforeTests {
        implicit val actorSystem = ActorSystem()
        system = actorSystem
        database = new RedisDbDataSourceImpl(RedisClient(), "test-1")
      } afterTests {
        awaitResult(system.terminate())
      } in { r =>
        for (i <- r) {
          awaitResult(database.put(ByteArrayUtils.fromString("record-" + i), ByteArrayUtils.fromString("data")))
        }
      }
    }
  }
}