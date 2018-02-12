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
package org.tron
package db

import java.io.File

import org.tron.protos.Tron.Transaction
import org.tron.storage.LevelDbDataSourceImpl

import scala.collection.mutable

class BlockStore(dbFolder: File) {


  private val blockDbDataSource = new LevelDbDataSourceImpl(dbFolder, "block")
  private val unSpendCache = new LevelDbDataSourceImpl(dbFolder, "trx")
  private val pendingTrans = mutable.ListBuffer[Transaction]()

  def pushTransactions(trx: Transaction): Unit = {
    pendingTrans += trx
  }

  /**
    * Generate Block
    */
  def generateBlock(): Unit = {}

  /**
    * save a block
    *
    * @param blockHash
    * @param blockData
    */
  def saveBlock(blockHash: Array[Byte], blockData: Array[Byte]): Unit = {
    blockDbDataSource.put(blockHash, blockData)
  }

  /**
    * find a block by it's hash
    *
    * @param blockHash
    * @return
    */
  def findBlockByHash(blockHash: Array[Byte]): Array[Byte] = {
    awaitResult(blockDbDataSource.get(blockHash)).get
  }

  /**
    * deleteData a block
    *
    * @param blockHash
    */
  def deleteBlock(blockHash: Array[Byte]): Unit = {
    blockDbDataSource.delete(blockHash)
  }

  def getUnspend(key: Array[Byte]): Unit = {

  }

  def isBlockIncluded(hash: Hash): Boolean = ???

  def getBlockNumByHash(lastKnownHash: Hash): Long = ???

  def headBlockNum: Long = ???

  def getBlockHashByNum(num: Long): Hash = ???


  /** *
    * resetDB the database
    */
  def reset(): Unit = blockDbDataSource.resetDB()

  def close(): Unit = blockDbDataSource.close()

}
