package sample.cluster.simple

import com.typesafe.config.ConfigFactory
import akka.actor.{PoisonPill, ActorSystem, Props}
import akka.contrib.pattern.ClusterSingletonManager

object SimpleClusterApp {
  def main(args: Array[String]): Unit = {
    startup(args.headOption.getOrElse("0"))
  }

  def startup(port: String): Unit = {
    // Override the configuration of the port
    val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.load())

    // Create an Akka system
    val system = ActorSystem("ClusterSystem", config)
    // Create an actor that handles cluster domain events
    system.actorOf(Props[SimpleClusterListener], name = "clusterListener")

    val clusterSingletonProperties = ClusterSingletonManager.props(
      singletonProps = Props(classOf[SimpleClusterSingleton]),
      singletonName = "pinger-ponger",
      terminationMessage = PoisonPill,
      role = None
    )

    system.actorOf(clusterSingletonProperties, "clusterSingleton")
  }

}

