package sample.cluster.simple

import akka.actor.{Deploy, Props, ActorLogging, Actor}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import sample.cluster.simple.ClusterTestProtocol.Ping
import akka.cluster.ClusterScope

class SimpleClusterSingleton extends Actor with ActorLogging {
  override def receive: Actor.Receive = {
    case Ping => {
      log.info("Ping?")
      (1 to 10) foreach {_ =>
        val ponger = context.actorOf(Props[PongingActor].withDeploy(Deploy(scope = ClusterScope)))
        ponger ! Ping
      }
    }
  }

  context.system.scheduler.schedule(0.seconds, 10.seconds, self, Ping)
}

object ClusterTestProtocol {
  case object Ping
}