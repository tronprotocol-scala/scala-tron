
  def toString(byteArray: Array[Byte]): String = {
    if (byteArray == null) ""
    else new String(byteArray)
  }

  def fromString(string: String): Array[Byte] = {
    if (string == null) new Array[Byte](0)
    else string.getBytes
  }

  def reverseBytes(bytes: Array[Byte]): Array[Byte] = { // We could use the XOR trick here but it's easier to understand if we don't. If we find this is really a
    // performance issue the matter can be revisited.
    bytes.reverse
  }
}