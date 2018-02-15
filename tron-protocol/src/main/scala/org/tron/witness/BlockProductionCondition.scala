package org.tron.witness

object BlockProductionCondition extends Enumeration {
  type BlockProductionCondition = Value

  val PRODUCED, // Successfully generated block
  NOT_SYNCED,
  NOT_MY_TURN, // It isn't my turn
  NOT_TIME_YET, // Not yet arrived
  NO_PRIVATE_KEY,
  LOW_PARTICIPATION,
  LAG,
  CONSECUTIVE,
  EXCEPTION_PRODUCING_BLOCK = Value
}