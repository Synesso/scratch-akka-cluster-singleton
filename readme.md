# Running this example

Edit src/main/resources/application.conf, correcting the ip addresses in akka.remote.netty.tcp.* and akka.cluster.seed-nodes

In separate terminals execute

    sbt "run-main sample.cluster.simple.SimpleClusterApp 2551"
    sbt "run-main sample.cluster.simple.SimpleClusterApp 2552"

and as many other instances as you like with

    sbt "run-main sample.cluster.simple.SimpleClusterApp"
