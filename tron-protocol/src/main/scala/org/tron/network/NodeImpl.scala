package org.tron.network

import java.util
import java.util.List

import akka.actor.Actor
import akka.stream.ActorMaterializer
import org.tron.core.Sha256Hash
import org.tron.network.NodeImpl.Advertise
import org.tron.network.message._
import org.tron.network.peer.PeerConnection
import org.tron.protos.Tron.Inventory
import org.tron.protos.Tron.Inventory.InventoryType

import scala.concurrent.duration._
import scala.collection.mutable

object NodeImpl {
  case class Advertise()
}

class NodeImpl(
  nodeDelegate: NodeDelegate,
  gossipNode: LocalNode) extends Actor {

  var syncMap = Map[Sha256Hash, PeerConnection]()

  /**
    * Peers from which data is being fetched
    */
  var fetchMap = Map[Sha256Hash, PeerConnection]()

  /**
    * Transactions which needs to be advertised to other peers
    */
  val trxToAdvertise = mutable.ListBuffer[Sha256Hash]()

  /**
    * Blocks which need to be advertised to other peers
    */
  val blockToAdvertise = mutable.ListBuffer[Sha256Hash]()

  /**
    * Periodically sends a message to start advertising messages
    */
  val advertisePoller = context.system.scheduler.schedule(3.seconds, 1.second, self, Advertise())(context.dispatcher)

  def connect(): Unit = {

    implicit val materializer = ActorMaterializer()(context)

    gossipNode
      .subscribe()
      .runForeach(onPeerMessage)
  }

  /**
    * On receiving a message from another peer
    */
  def onPeerMessage(peerMessage: PeerMessage) = {
    val PeerMessage(peer, message) = peerMessage

    message match {
      case message: BlockMessage =>
        handleBlockMessage(peer, message)

      case message: TransactionMessage =>
        handleTransactionMessage(peer, message)

      case message: SyncBlockChainMessage =>
        handleSyncBlockChainMessage(peer, message)

      case message: BlockInventoryMessage =>
        handleBlockInventoryMessage(peer, message)

      case message: FetchInvDataMessage =>
        handleFetchDataMessage(peer, message)
    }
  }

  /**
    * Handles a block message from a peer
    *
    * The peer can be updated with the latest seen block
    */
  def handleBlockMessage(peer: PeerConnection, message: BlockMessage) = {
    peer.lastSeenBlock = Some(message.block)
    nodeDelegate.handleBlock(message.block)
  }

  /**
    * Handles incoming transaction
    *
    * @param peer    from which the transaction came
    * @param message the message containing the transaction
    */
  def handleTransactionMessage(peer: PeerConnection, message: TransactionMessage) = {
    nodeDelegate.handleTransation(message.transaction)
  }

  /**
    * Handle blockchain synchronisation request
    */
  def handleSyncBlockChainMessage(peer: PeerConnection, message: SyncBlockChainMessage) = {
    val blockIds = nodeDelegate.getBlockHashes(message.hashes)
    peer.send(BlockInventoryMessage.fromHashes(blockIds))
  }

  /**
    * Handles block inventory message send by other peers
    * Request the full blocks from the peer who send it
    *
    * @param peer    from where to fetch the blocks
    * @param message message containing the block hashes
    */
  def handleBlockInventoryMessage(peer: PeerConnection, message: BlockInventoryMessage) = {
    val blockIds = nodeDelegate.getBlockHashes(message.hashes)
    val fetchMsg = FetchInvDataMessage.fromHashes(blockIds, InventoryType.BLOCK)
    fetchMap += fetchMsg.hash -> peer
    peer.send(fetchMsg)
  }

  /**
    * Handles a fetch message
    * Respond with the requested data
    *
    * @param peer    connection to which to send the fetch request
    * @param message data request
    */
  def handleFetchDataMessage(peer: PeerConnection, message: FetchInvDataMessage) = {
    message.hashes.foreach { hash =>
      peer.send(nodeDelegate.getData(hash, message.fetchType))
    }
  }

  /**
    * Syncs from the given hash
    */
  def syncFrom(hash: Sha256Hash) = {
    val hashList = nodeDelegate.getBlockChainSummary(hash, 100)

    val inventory = Inventory(
      `type` = Inventory.InventoryType.BLOCK,
      ids = hashList.map(_.getByteString))

    val syncBlockChainMessage = SyncBlockChainMessage(inventory)

    // Make sure there are other peers to sync from
    if (gossipNode.peers.nonEmpty) {
      syncMap.get(syncBlockChainMessage.hash).foreach { peer =>
        peer.send(syncBlockChainMessage)
      }
    }
  }

  /**
    * Broadcasts message to other peers
    */
  def broadcast(msg: Message): Unit = {
    msg match {
      case block: BlockMessage =>
        blockToAdvertise.append(block.hash)
        advertise()

      case transaction: TransactionMessage =>
        trxToAdvertise.append(transaction.hash)
        advertise()
    }

  }

  /**
    * Advertises blocks and transactions to other peers
    */
  def advertise() = {
    if (blockToAdvertise.nonEmpty) {
      val inventoryMessage = BlockInventoryMessage.fromHashes(blockToAdvertise.toList)
      gossipNode.syncablePeers
        .foreach(_.send(inventoryMessage))

      blockToAdvertise.clear()
    }

    if (trxToAdvertise.nonEmpty) {
      val inventoryMessage = TransactionInventoryMessage.fromHashes(trxToAdvertise.toList)
      gossipNode.syncablePeers
        .foreach(_.send(inventoryMessage))

      trxToAdvertise.clear()
    }
  }

  override def postStop(): Unit = {
    advertisePoller.cancel()
  }

  def receive = {
    case Advertise() =>
      advertise()
  }
}