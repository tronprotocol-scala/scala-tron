/*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *//*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron.storage

import java.io.{File, IOException}
import java.util.concurrent.locks.ReentrantReadWriteLock

import org.fusesource.leveldbjni.JniDBFactory.factory
import org.iq80.leveldb._
import org.tron.utils.FileUtil

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Try}

class LevelDbDataSourceImpl(dbFolder: File, name: String = "default") extends DataSource[Array[Byte], Array[Byte]] {

  val resetDbLock = new ReentrantReadWriteLock

  @volatile
  private var db: Option[DB] = None

  val options = {
    val dbOptions = new Options
    dbOptions.createIfMissing(true)
    dbOptions.compressionType(CompressionType.NONE)
    dbOptions.blockSize(10 * 1024 * 1024)
    dbOptions.writeBufferSize(10 * 1024 * 1024)
    dbOptions.cacheSize(0)
    dbOptions.paranoidChecks(true)
    dbOptions.verifyChecksums(true)
    dbOptions.maxOpenFiles(32)
    dbOptions
  }

  private def database: DB = {
    db.getOrElse {
      buildDb() match {
        case Right(newDb) =>
          db = Some(newDb)
          newDb
        case Left(e) =>
          throw e
      }
    }
  }

  def openDb() = {
    val path = new File(dbFolder.getAbsolutePath, name)
    path.mkdirs()
    factory.open(path, options)
  }

  def repairDb(): Unit = {
    factory.repair(dbFolder, options)
  }

  def buildDb(): Either[Throwable, DB] = {
    Try(openDb()).recoverWith {
      case e: IOException if e.getMessage.contains("Corruption:") =>
        repairDb()
        Try(openDb())
      case e =>
        Failure(e)
    }.toEither
  }

  def resetDB() = Future.successful {
    close()
    FileUtil.recursiveDelete(new File(dbFolder.getAbsolutePath, name).getAbsolutePath)
    buildDb() match {
      case Right(newDb) =>
        db = Some(newDb)
      case Left(e) =>
        throw e
    }
  }

  def destroyDB(fileLocation: File): Unit = {
    withWriteLock {
      factory.destroy(fileLocation, new Options)
    }
  }

  def get(key: Array[Byte]) = Future.successful {
    withReadLock {
      Option(database.get(key))
    }
  }

  def put(key: Array[Byte], value: Array[Byte]) = Future.successful {
    withReadLock {
      database.put(key, value)
    }
  }

  def delete(key: Array[Byte]) = Future.successful {
    withReadLock {
      database.delete(key)
    }
  }

  def allKeys = Future.successful {
    withReadLock {
      val iterator = database.iterator
      try {
        iterator.seekToFirst()
        val result = mutable.HashSet[Array[Byte]]()
        while (iterator.hasNext) {
          result.add(iterator.peekNext.getKey)
          iterator.next
        }
        result.toSet
      } catch {
        case e: IOException =>
          throw new RuntimeException(e)
      } finally {
        if (iterator != null) {
          iterator.close()
        }
      }
    }
  }

  def close(): Unit = {
    withWriteLock {
      if (db.nonEmpty) {
        database.close()
        db = None
      }
    }
  }

  def withReadLock[T](action: => T): T = {
    resetDbLock.readLock.lock()
    try action
    finally resetDbLock.readLock.unlock()
  }

  def withWriteLock[T](action: => T): T = {
    resetDbLock.writeLock.lock()
    try action
    finally resetDbLock.writeLock.unlock()
  }
}
