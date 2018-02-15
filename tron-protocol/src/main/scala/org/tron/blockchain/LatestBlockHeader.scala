package org.tron.blockchain

import com.google.protobuf.ByteString

case class LatestBlockHeader(
  timestamp: Long,
  number: Long,
  hash: ByteString)
