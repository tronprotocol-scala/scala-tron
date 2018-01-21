package org.tron.peer

object Sync {

  case class GetBlocks(
    headers: List[Array[Byte]])

}
