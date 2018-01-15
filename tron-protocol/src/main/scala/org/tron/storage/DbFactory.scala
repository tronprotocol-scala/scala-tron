package org.tron.storage

import java.io.File
import java.nio.file.Path

import org.tron.core.{BlockUtils, Constant, TransactionUtils}

class DbFactory(databaseFolder: Path) {

  def exists(name: String) = {
    val dbFile = new File(databaseFolder.toFile, name)
    dbFile.exists()
  }

  def build(name: String) = {
    val db = new LevelDbDataSourceImpl(databaseFolder.toFile, name)
    db.initDB()
    db
  }

  /**
    * Get or create a new database
    * @param name database name
    * @param account ugly hack
    * @return
    */
  def buildOrCreate(name: String, account: String) = {
    if (exists(name)) {
      build(name)
    } else {
      val blockDB = build(name)

      val transactions = TransactionUtils.newCoinbaseTransaction(account, Constant.GENESIS_COINBASE_DATA)

      val genesisBlock = BlockUtils.newGenesisBlock(transactions)


      blockDB.put(genesisBlock.blockHeader.get.hash.toByteArray, genesisBlock.toByteArray)
      val lastHash = genesisBlock.blockHeader.get.hash.toByteArray
      blockDB.put(Constant.LAST_HASH, lastHash)
      blockDB
    }
  }
}
