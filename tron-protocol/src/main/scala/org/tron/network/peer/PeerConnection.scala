package org.tron.network.peer

import io.scalecube.cluster.{Cluster, Member}
import org.tron.network.message.Message
import org.tron.protos.Tron.Block

case class PeerConnection(cluster: Cluster, member: Member) {

  var lastSeenBlock: Option[Block] = None
  var needSyncFrom: Boolean = false

  def send(message: Message) = {

  }

}
