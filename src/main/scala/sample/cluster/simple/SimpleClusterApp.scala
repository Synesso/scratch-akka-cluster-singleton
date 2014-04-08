package sample.cluster.simple

import com.typesafe.config.ConfigFactory
import akka.actor.{PoisonPill, ActorSystem, Props}
import akka.contrib.pattern.ClusterSingletonManager

object SimpleClusterApp {
  def main(args: Array[String]): Unit = {
    startup(args.headOption.getOrElse("0"))
  }

  def startup(port: String): Unit = {
    // we mark the seed nodes with a special role, and will require those to join before we spin up the singleton
    val maybeSeed = if (port startsWith "255") "roles = [seed]" else ""

    // Override the configuration of the port
    val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.parseString(
        s"""
           |akka.cluster {
           |  $maybeSeed
           |  role {
           |    seed.min-nr-of-members = 2
           |  }
           |}
           |""".stripMargin)).
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

