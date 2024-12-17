package dev.daeops.core.base.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import dev.daeops.core.base.{
  ActorMessage,
  ActorState,
  BaseCommand,
  Position,
  ActorStatus
}

sealed trait RegistryCommand
object RegistryCommand {
  case class Track[S <: ActorState](ref: ActorRef[ActorMessage], state: S) extends RegistryCommand
  case class Remove(id: String) extends RegistryCommand
  case class Move(id: String, to: Position) extends RegistryCommand
  case class UpdateStatus(id: String, status: ActorStatus) extends RegistryCommand
  case class StateChanged(id: String, state: ActorState) extends RegistryCommand
  case class Query(pos: Position, range: Double, replyTo: ActorRef[Set[ActorState]]) extends RegistryCommand
}

object ActorRegistry {
  private case class State(
                          refs: Map[String, ActorRef[ActorMessage]],
                          states: Map[String, ActorState]
                          ) {
    def track(ref: ActorRef[ActorMessage], state: ActorState): State =
      copy(refs + (state.id -> ref), states + (state.id -> state))

    def untrack(id: String): State =
      copy(refs - id, states - id)

    def updateState(id: String, state: ActorState): State =
      copy(states = states + (id -> state))
  }

  def apply(): Behavior[RegistryCommand] = handle(State(Map.empty, Map.empty))

  private def handle(s: State): Behavior[RegistryCommand] = Behaviors.receive { (ctx, cmd) =>

    cmd match {

      case RegistryCommand.Track(ref, state) =>
        ctx.log.debug(s"Tracking actor ${state.id}")
        handle(s.track(ref, state))

      case RegistryCommand.Remove(id) =>
        ctx.log.debug(s"Removing actor $id")
        s.refs.get(id).foreach(_ ! BaseCommand.Stop(id))
        handle(s.untrack(id))

      case RegistryCommand.Move(id, to) =>
        s.refs.get(id).foreach(_ ! BaseCommand.Move(id, to))
        Behaviors.same

      case RegistryCommand.UpdateStatus(id, status) =>
        s.refs.get(id).foreach(_ ! BaseCommand.UpdateStatus(id, status))
        Behaviors.same

      case RegistryCommand.StateChanged(id, newState) =>
        handle(s.updateState(id, newState))

      case RegistryCommand.Query(pos, range, replyTo) =>
        val inRange = s.states.values.filter { state =>
          val dx = pos.x - state.position.x
          val dy = pos.y - state.position.y
          val dz = pos.z - state.position.z
          Math.sqrt(dx * dx + dy * dy + dz * dz) <= range
        }.toSet
        replyTo ! inRange
        Behaviors.same
    }

  }

}
