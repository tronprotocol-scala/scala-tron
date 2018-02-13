package org.tron
package db

import com.google.protobuf.ByteString
import org.tron.blockchain.LatestBlockHeader
import org.tron.utils.ByteArrayUtils

class DynamicPropertiesStore(val dbSource: DefaultDB) extends TronDatabase {

  private val LATEST_BLOCK_HEADER_TIMESTAMP = "latest_block_header_timestamp".getBytes
  private val LATEST_BLOCK_HEADER_NUMBER = "latest_block_header_number".getBytes
  private val LATEST_BLOCK_HEADER_HASH = "latest_block_header_hash".getBytes


  def latestBlockHeaderTimestamp: Option[Long] = {
    awaitResult(dbSource.get(LATEST_BLOCK_HEADER_TIMESTAMP)).flatMap {
      case x if x.isEmpty => None
      case x => Some(ByteArrayUtils.toLong(x))
    }
  }

  def latestBlockHeaderTimestamp_=(timestamp: Long): Unit = {
    awaitResult(dbSource.put(LATEST_BLOCK_HEADER_TIMESTAMP, ByteArrayUtils.fromLong(timestamp)))
  }

  def latestBlockHeaderNumber: Option[Long] = {
    awaitResult(dbSource.get(LATEST_BLOCK_HEADER_NUMBER)).flatMap {
      case x if x.isEmpty => None
      case x => Some(ByteArrayUtils.toLong(x))
    }
  }

  def latestBlockHeaderNumber_=(number: Long): Unit = {
    awaitResult(dbSource.put(LATEST_BLOCK_HEADER_NUMBER, ByteArrayUtils.fromLong(number)))
  }

  def latestBlockHeaderHash: Option[ByteString] = {
    awaitResult(dbSource.get(LATEST_BLOCK_HEADER_HASH)).flatMap {
      case x if x.isEmpty => None
      case x => Some(ByteString.copyFrom(x))
    }
  }

  def latestBlockHeaderHash_=(number: ByteString): Unit = {
    awaitResult(dbSource.put(LATEST_BLOCK_HEADER_HASH, number.toByteArray))
  }

  def latestBlockHeader = for {
    timestamp <- latestBlockHeaderTimestamp
    number    <- latestBlockHeaderNumber
    hash      <- latestBlockHeaderHash
  } yield LatestBlockHeader(timestamp, number, hash)

  def latestBlockHeader_(header: LatestBlockHeader) = {
    latestBlockHeaderHash = header.hash
    latestBlockHeaderNumber = header.number
    latestBlockHeaderTimestamp = header.timestamp
  }

}
