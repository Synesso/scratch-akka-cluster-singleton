package sample.cluster.simple

import akka.actor.{ActorRef, ActorLogging, Actor}
import sample.cluster.simple.ClusterTestProtocol.{SendReport, Report, Result}

class WorkloadCounter extends Actor with ActorLogging {

  def receive = metrics(Map.empty[ActorRef, Int])

  def metrics(counts: Map[ActorRef, Int]): Receive = {
    case Result(_) => {
      context.become(metrics(counts.updated(sender, counts.getOrElse(sender, 0) + 1)))
    }
    case SendReport => sender ! Report(counts)
  }
}
