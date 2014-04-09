package sample.cluster.simple

import akka.actor.{Deploy, Props, ActorLogging, Actor}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import sample.cluster.simple.ClusterTestProtocol.{Result, Work, Batch}
import akka.cluster.ClusterScope
import akka.cluster.routing.{ClusterRouterPoolSettings, ClusterRouterPool}
import akka.routing._
import scala.util.Random
import akka.routing.RoundRobinPool
import akka.routing.Routees

class TheFatController extends Actor with ActorLogging {
  override def receive: Actor.Receive = {
    case Batch(size) => {
      log.info(s"Generating a batch of size $size")
      for (i <- 1 to size) workerRouter ! Work(i)
      workerRouter ! GetRoutees
      context.system.scheduler.scheduleOnce(10.seconds, self, Batch(Random.nextInt(10)))
    }
    case Routees(routees) => {
      val groupedByNode = routees.groupBy {
        _ match {
          case ActorRefRoutee(ref) =>
            ref.path.address.port
        }
      }
      if (groupedByNode.isEmpty) {
        println("Worker router has no routees.")
      }
      else {
        groupedByNode.foreach { case (port, routees) =>
          println(s"Node with port $port has ${routees.size} routees.")
        }
      }
    }

  }

  context.system.scheduler.scheduleOnce(0.seconds, self, Batch(Random.nextInt(10)))

  private val workerRouter = {
    context.actorOf(ClusterRouterPool(
      RoundRobinPool(10),
      ClusterRouterPoolSettings(
        totalInstances = 30,
        maxInstancesPerNode = 10,
        allowLocalRoutees = true,
        useRole = None)
    ).props(Props[Worker]), name = "worker-router")
  }

}

object ClusterTestProtocol {
  case class Batch(size: Int)
  case class Work(id: Int)
  case class Result(id: Int)
}
