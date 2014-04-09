package sample.cluster.simple

import akka.actor.{Kill, PoisonPill, Actor, ActorLogging}
import sample.cluster.simple.ClusterTestProtocol.{Result, Work, Batch}

class Worker extends Actor with ActorLogging {

  override def receive: Actor.Receive = {
    case Work(id) => {
      log.info(s"${Result(id)}")
      sender ! Result(id)
    }
  }
}
