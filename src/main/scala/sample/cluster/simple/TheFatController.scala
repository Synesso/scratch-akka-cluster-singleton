package sample.cluster.simple

import akka.actor.{Deploy, Props, ActorLogging, Actor}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import sample.cluster.simple.ClusterTestProtocol.{Work, Batch}
import akka.cluster.ClusterScope
import akka.cluster.routing.{ClusterRouterPoolSettings, ClusterRouterPool}
import akka.routing.{RoundRobinPool, BroadcastPool}
import scala.util.Random

class TheFatController extends Actor with ActorLogging {
  override def receive: Actor.Receive = {
    case Batch(size) => {
      log.info(s"Generating a batch of size $size")
      for (i <- 1 to size) workerRouter ! Work(i)
      context.system.scheduler.scheduleOnce(10.seconds, self, Batch(Random.nextInt(10)))
    }
  }

  context.system.scheduler.scheduleOnce(0.seconds, self, Batch(Random.nextInt(10)))

  private val workerRouter = {
    context.actorOf(ClusterRouterPool(
      RoundRobinPool(10),
      ClusterRouterPoolSettings(
        totalInstances = 10,
        maxInstancesPerNode = 5,
        allowLocalRoutees = true,
        useRole = None)
    ).props(Props[Worker]), name = "worker-router")
  }
  
}

object ClusterTestProtocol {
  case class Batch(size: Int)
  case class Work(id: Int)
}