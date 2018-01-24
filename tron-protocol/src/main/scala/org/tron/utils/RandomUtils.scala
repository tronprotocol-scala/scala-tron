package org.tron.utils

object RandomUtils {

  /**
    * Random number within range
    */
  def nextInt(start: Int, end: Int) = {
    val rnd = new scala.util.Random
    val range = start to end
    range(rnd.nextInt(range length))
  }

  def alphanumeric(a: Int) =  scala.util.Random.alphanumeric.take(a).mkString


  /**
    * Generate a random GUID string
    *
    * @return random generated GUID as string
    */
  def uuid = java.util.UUID.randomUUID


  /**
    * Retrieves a random item
    */
  def getRandomItem[T](items: Seq[T]): T = {
    items(nextInt(0, items.size - 1))
  }
}
