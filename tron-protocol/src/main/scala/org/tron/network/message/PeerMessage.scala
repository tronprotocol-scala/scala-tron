package org.tron.network.message

import org.tron.network.peer.PeerConnection

case class PeerMessage(peer: PeerConnection, message: Message)
