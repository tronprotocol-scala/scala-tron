package org.tron.network.peer

import org.tron.network.message.Message
import org.tron.protos.Tron.Block

class PeerConnection() {

  var lastSeenBlock: Option[Block] = None
  var needSyncFrom: Boolean = false

  def send(message: Message) = {

  }

}
