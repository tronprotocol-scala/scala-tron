package org.tron.core

import java.math.BigInteger

import com.google.protobuf.ByteString
import org.spongycastle.util.{Arrays, BigIntegers}
import org.tron.crypto.Hash.sha3
import org.tron.protos.core.TronBlock.Block
import org.tron.protos.core.TronBlockHeader.BlockHeader
import org.tron.protos.core.TronTransaction.Transaction
import org.tron.utils.ByteArray

object BlockUtils {

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

    val genesisBlock = Block()
      .addTransactions(coinbase)

    val blockHeader = BlockHeader(
      difficulty = ByteString.copyFrom(ByteArray.fromHexString("2001")),
      hash = ByteString.copyFrom(sha3(prepareData(genesisBlock))))

    genesisBlock.withBlockHeader(blockHeader)
  }

  def newGenesisBlock(transactions: List[Transaction]): Block = {

    val genesisBlock = Block()
      .withTransactions(transactions)

    val blockHeader = BlockHeader(
      difficulty = ByteString.copyFrom(ByteArray.fromHexString("2001")),
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
      .blockHeader.get
      .withHash(ByteString.EMPTY)
      .withNonce(ByteString.EMPTY)

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
    * getData print string of the block
    *
    * @param block { @link Block} block
    * @return String format string of the block
    */
  def toPrintString(block: Block): String = ???

  /**
    * getData mine value
    *
    * @param block { @link Block} block
    * @return byte[] mine value
    */
  def getMineValue(block: Block): Array[Byte] = {
    val concat = Arrays.concatenate(prepareData(block), block.getBlockHeader.nonce.toByteArray)
    sha3(concat)
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
      lastHash <- Option(blockchain.blockDB.getData(Constant.LAST_HASH))
      value <- Option(blockchain.blockDB.getData(lastHash))
    } yield Block.parseFrom(value).getBlockHeader.number + 1

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