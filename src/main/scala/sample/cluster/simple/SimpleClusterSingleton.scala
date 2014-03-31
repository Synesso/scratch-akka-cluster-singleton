package sample.cluster.simple

import akka.actor.{ActorLogging, Actor}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

class SimpleClusterSingleton extends Actor with ActorLogging with ClusterTestProtocol {
  override def receive: Actor.Receive = {
    case Ping => log.info("Pong!")
  }

  context.system.scheduler.schedule(0.seconds, 10.seconds, self, Ping)
}

trait ClusterTestProtocol {
  case object Ping
}