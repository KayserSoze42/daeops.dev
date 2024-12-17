package dev.daeops.core.base

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

sealed trait ActorMessage {

  def id: String

}

trait ActorState {

  def id: String
  def position: Position
  def status: ActorStatus

}

case class Position(x: Double, y: Double, z: Double)

sealed trait ActorStatus
object ActorStatus {
  case object Active extends ActorStatus
  case object Inactive extends ActorStatus
  case object Maintenance extends ActorStatus
  case object Error extends ActorStatus
}

trait BaseActor[S <: ActorState, M <: ActorMessage] {

  def apply(state: S): Behavior[M] = Behaviors.setup { context =>
    Behaviors.receiveMessage { msg =>
      handleMessage(msg) match {
        case Right(newState) => updated(newState)
        case Left(error) => Behaviors.same
      }
    }
  }

  private def updated(state: S): Behavior[M] = apply(state)
  protected def handleMessage(message: M): Either[String, S]
}

// fixd her
object BaseCommand {

  case class Move(id: String, to: Position) extends ActorMessage
  case class Stop(id: String) extends ActorMessage
  case class UpdateStatus(id: String, status: ActorStatus) extends ActorMessage
  case class GetState(id: String, replyTo: ActorRef[ActorState]) extends ActorMessage

}
// hardly know her
trait BaseCommand {

}