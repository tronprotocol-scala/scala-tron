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

class LevelDbDataSourceImpl(dbFolder: File, name: String = "default") extends DataSource[Array[Byte], Array[Byte]] {

  var database: DB = null
  var alive = false
  val resetDbLock = new ReentrantReadWriteLock

  def buildOptions = {
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

  def initDB(): Unit = {
    resetDbLock.writeLock.lock()
    try {
      if (alive)
        return

      val dbOptions = buildOptions

      try {
        try {

          val path = new File(dbFolder.getAbsolutePath, name)
          path.mkdirs()
          database = factory.open(path, dbOptions)
        }
        catch {
          case e: IOException =>
            if (e.getMessage.contains("Corruption:")) {
              factory.repair(dbFolder, dbOptions)
              database = factory.open(dbFolder, dbOptions)
            } else {
              throw e
            }
        }
        alive = true
      } catch {
        case ioe: IOException =>
          throw new RuntimeException("Can't initialize database", ioe)
      }
    } finally {
      resetDbLock.writeLock.unlock()
    }
  }

  def resetDB(): Unit = {
    close()
    FileUtil.recursiveDelete(new File(dbFolder.getAbsolutePath, name).getAbsolutePath)
    initDB()
  }

  def destroyDB(fileLocation: File): Unit = {
    resetDbLock.writeLock.lock()
    try factory.destroy(fileLocation, new Options)
    finally resetDbLock.writeLock.unlock()
  }

  def get(key: Array[Byte]): Option[Array[Byte]] = {
    resetDbLock.readLock.lock()
    try {
      Option(database.get(key))
    } finally resetDbLock.readLock.unlock()
  }

  def put(key: Array[Byte], value: Array[Byte]): Unit = {
    resetDbLock.readLock.lock()
    try database.put(key, value)
    finally resetDbLock.readLock.unlock()
  }

  def delete(key: Array[Byte]): Unit = {
    resetDbLock.readLock.lock()
    try database.delete(key)
    finally resetDbLock.readLock.unlock()
  }

  def allKeys: Set[Array[Byte]] = {
    resetDbLock.readLock.lock()
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
      resetDbLock.readLock.unlock()
      if (iterator != null) {
        iterator.close()
      }
    }
  }

  def close(): Unit = {
    resetDbLock.writeLock.lock()
    if (!alive)
      return

    try {
      database.close()
      alive = false
    } finally resetDbLock.writeLock.unlock()
  }
}