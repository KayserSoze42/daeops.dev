package dev.daeops.core.base.actor

import scala.util.Try
import dev.daeops.core.base.Position

sealed trait Action {
  def id: String
}

object Action {
  final case class Move(id: String, to: Position) extends Action
  final case class Work(id: String, task: String) extends Action
  final case class Stop(id: String) extends Action
}

sealed trait Outcome {
  object Outcome {
    final case class Ok(actionId: String, msg: String) extends Outcome
    final case class Err(actionId: String, reason: String) extends Outcome

  }

  class Factory {
    def act(action: Action): Outcome = action match {
      case Action.Move(id, pos) if isValidPosition(pos) =>
        Outcome.Ok(id, s"moved to $pos")
      case Action.Work(id, task) if canWork(id) =>
        Outcome.Ok(id, s"started work on $task")
      case Action.Stop(id) =>
        Outcome.Ok(id, s"stopped current action")
      case _ => Outcome.Err(action.id, "invalid action for current state")
    }

    private def isValidPosition(pos: Position): Boolean = {
      pos.x >= 0 && pos.y >= 0 && pos.z >= 0
    }

    private def canWork(id: String): Boolean = {
      true
    }

  }

}