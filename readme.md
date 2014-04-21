# Running this example

In separate terminals execute

    sbt "run-main sample.cluster.simple.SimpleClusterApp 2551"
    sbt "run-main sample.cluster.simple.SimpleClusterApp 2552"

and as many other instances as you like with

    sbt "run-main sample.cluster.simple.SimpleClusterApp"
    
After the leader node is established, the cluster singleton will start. Its work will be balanced among the active nodes via a router. Explore what happens when you kill the leader node process.
