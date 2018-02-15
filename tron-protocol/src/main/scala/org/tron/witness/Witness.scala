package org.tron.witness

import akka.actor.Actor
import org.joda.time.DateTime
import org.tron.network.Node
import org.tron.witness.Witness.RunProduction

import scala.concurrent.duration._

object Witness {
  case class RunProduction()
}

class Witness(node: Node) extends Actor {

  val interval = 1.second

  import context.dispatcher

  val productionSchedule = context.system.scheduler.schedule(interval, interval, self, RunProduction())

  def firstSlotTime: DateTime = DateTime.now

  def getSlotAtTime(when: DateTime) = {
    val firstSlotFime = firstSlotTime
    (when.getMillis - firstSlotFime.getMillis) / interval.toMillis + 1
  }

  def tryProduceBlock() = {

  }

  override def postStop(): Unit = {
    productionSchedule.cancel()
  }

  def receive = {
    case RunProduction() =>
      tryProduceBlock()
  }
}
