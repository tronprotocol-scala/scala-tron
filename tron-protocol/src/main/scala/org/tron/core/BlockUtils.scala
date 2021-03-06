package org.tron
package core

import java.math.BigInteger

import com.google.protobuf.ByteString
import org.spongycastle.util.BigIntegers
import org.tron.awaitResult
import org.tron.blockchain.Blockchain
import org.tron.crypto.Hash.sha3
import org.tron.protos.Tron._
import org.tron.utils.ByteArrayUtils

object BlockUtils {

  implicit class BlockImplicits(block: Block) {
    def hash: Hash = Sha256Hash.of(block.toByteArray)
    def parentHash: Hash = Sha256Hash.wrap(this.block.getBlockHeader.parentHash)
  }

  /**
    * getData a new block
    *
    * @return { @link Block} block
    */
  def newBlock(
    transactions: List[Transaction],
    parentHash: ByteString,
    difficulty: ByteString,
    number: Long): Block = {

    val block = Block()
      .addAllTransactions(transactions)

    val blockHeaderBuilder = BlockHeader(
      parentHash = parentHash,
      difficulty = difficulty,
      number = number,
      timestamp = System.currentTimeMillis(),
      hash = ByteString.copyFrom(sha3(prepareData(block))))

    block.withBlockHeader(blockHeaderBuilder)
  }

  /**
    * new genesis block
    *
    * @return { @link Block} block
    */
  def newGenesisBlock(coinbase: Transaction): Block = {

    var blockHeader = BlockHeader(
      difficulty = ByteString.copyFrom(ByteArrayUtils.fromHexString("2001")))

    val genesisBlock = Block()
      .addTransactions(coinbase)
      .withBlockHeader(blockHeader)

    blockHeader = blockHeader.withHash(ByteString.copyFrom(sha3(prepareData(genesisBlock))))

    genesisBlock.withBlockHeader(blockHeader)
  }

  def newGenesisBlock(transactions: List[Transaction]): Block = {

    val genesisBlock = Block()
      .withTransactions(transactions)

    val blockHeader = BlockHeader(
      difficulty = ByteString.copyFrom(ByteArrayUtils.fromHexString("2001")),
      hash = ByteString.copyFrom(sha3(prepareData(genesisBlock))))

    genesisBlock.withBlockHeader(blockHeader)
  }

  /**
    * getData prepare data of the block
    *
    * @param block { @link Block} block
    * @return byte[] data
    */
  def prepareData(block: Block): Array[Byte] = {
    val blockHeader = block
      .blockHeader.map(_
        .withHash(ByteString.EMPTY)
        .withNonce(ByteString.EMPTY))
      .getOrElse(BlockHeader())

    block
      .withBlockHeader(blockHeader)
      .toByteArray
  }

  /**
    * the proof block
    *
    * @param block { @link Block} block
    * @return boolean is it the proof block
    */
  def isValidate(block: Block): Boolean = ???

  /**
    * getData mine value
    *
    * @param block { @link Block} block
    * @return byte[] mine value
    */
  def getMineValue(block: Block): Array[Byte] = {
    sha3(prepareData(block) ++ block.getBlockHeader.nonce.toByteArray)
  }

  /**
    * getData Verified boundary
    *
    * @param block { @link Block} block
    * @return byte[] boundary
    */
  def getPowBoundary(block: Block): Array[Byte] = {
    BigIntegers.asUnsignedByteArray(32,
      BigInteger.ONE.shiftLeft(256).divide(new BigInteger(1, block.getBlockHeader.difficulty.toByteArray)))
  }

  /**
    * getData increase number + 1
    *
    * @return long number
    */
  def getIncreaseNumber(blockchain: Blockchain): Long = {
    val nextNumber = for {
      lastHash <- awaitResult(blockchain.blockDB.get(Constant.LAST_HASH))
      value <- awaitResult(blockchain.blockDB.get(lastHash))
      blockHeader <- Block.parseFrom(value).blockHeader
    } yield blockHeader.number + 1

    nextNumber.getOrElse(0)
  }

  /**
    * Whether the hash of the judge block is equal to the hash of the parent
    */

  // block
  def isParentOf(block1: Block, block2: Block): Boolean = {
    block1.getBlockHeader.parentHash == block2.getBlockHeader.hash
  }
}