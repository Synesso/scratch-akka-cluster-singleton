package sample.cluster.simple

import akka.actor.{PoisonPill, Actor, ActorLogging}
import sample.cluster.simple.ClusterTestProtocol.Ping

class PongingActor extends Actor with ActorLogging {
  override def receive: Actor.Receive = {
    case Ping => {
      log.info("Pong!")
      self ! PoisonPill
    }
  }
}
