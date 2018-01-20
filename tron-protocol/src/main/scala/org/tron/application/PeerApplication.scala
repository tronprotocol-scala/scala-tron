package org.tron.application

import org.tron.peer.Peer

trait PeerApplication {

  this: Application =>

  val peer: Peer
}
