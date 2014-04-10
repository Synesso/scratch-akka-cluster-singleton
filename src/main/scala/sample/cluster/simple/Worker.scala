package sample.cluster.simple

import akka.actor._
import sample.cluster.simple.ClusterTestProtocol.{Result, Work, Batch}
import sample.cluster.simple.ClusterTestProtocol.Result
import sample.cluster.simple.ClusterTestProtocol.Work

class Worker(counter: ActorRef) extends Actor with ActorLogging {

  override def receive: Actor.Receive = {
    case Work(id) => {
      val result = Result(id)
      log.info(s"$result")
      sender ! result
      counter ! result
    }
  }
}
