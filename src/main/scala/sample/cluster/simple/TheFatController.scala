package sample.cluster.simple

import akka.actor._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import sample.cluster.simple.ClusterTestProtocol._
import akka.cluster.ClusterScope
import akka.cluster.routing.{ClusterRouterPoolSettings, ClusterRouterPool}
import akka.routing._
import scala.util.Random
import akka.routing.RoundRobinPool
import akka.routing.Routees
import akka.cluster.routing.ClusterRouterPool
import akka.routing.RoundRobinPool
import akka.routing.Routees
import akka.routing.ActorRefRoutee
import sample.cluster.simple.ClusterTestProtocol.Work
import akka.cluster.routing.ClusterRouterPool
import akka.routing.RoundRobinPool
import akka.routing.Routees
import akka.routing.ActorRefRoutee
import sample.cluster.simple.ClusterTestProtocol.Batch

class TheFatController extends Actor with ActorLogging {
  val counter = context.actorOf(Props[WorkloadCounter], "workload-counter")

  override def receive: Actor.Receive = {

    case Batch(size) => {
      log.info(s"Generating a batch of size $size")
      for (i <- 1 to size) workerRouter ! Work(i)
      context.system.scheduler.scheduleOnce(10.seconds, self, batch)
    }

    case Report(counts: Map[ActorRef, Int]) => {
      counts.map{case (k,v) =>
        val address: Option[String] = for {
          host <- k.path.address.host
          port <- k.path.address.port
        } yield {
          s"$host:$port/"
        }
        (address.getOrElse("local/") + k.path.name, v)
      }.map{case (k,v) => s"$k -> $v"}.toSeq.sorted.foreach(log.info)
    }
  }

  context.system.scheduler.scheduleOnce(3.seconds, self, batch)
  context.system.scheduler.schedule(0.seconds, 10.seconds, counter, SendReport)

  private val workerRouter = {
    context.actorOf(ClusterRouterPool(
      RoundRobinPool(10),
      ClusterRouterPoolSettings(
        totalInstances = 30,
        maxInstancesPerNode = 10,
        allowLocalRoutees = true,
        useRole = None)
    ).props(Props(classOf[Worker], counter)), name = "worker-router")
  }

  private def batch = Batch(Random.nextInt(5) + 5)

}

object ClusterTestProtocol {
  case class Batch(size: Int)
  case class Work(id: Int)
  case class Result(id: Int)
  case object SendReport
  case class Report(counts: Map[ActorRef, Int])

}
