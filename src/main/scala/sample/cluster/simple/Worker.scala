package sample.cluster.simple

import akka.actor.{PoisonPill, Actor, ActorLogging}
import sample.cluster.simple.ClusterTestProtocol.{Work, Batch}

class Worker extends Actor with ActorLogging {

  override def receive: Actor.Receive = {
    case Work(id) => {
      log.info(s"Working on $id")
      self ! PoisonPill
    }
  }
}
