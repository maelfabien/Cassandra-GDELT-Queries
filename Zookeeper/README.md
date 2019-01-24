# Configuring zookeeper to achieve Spark resilience

In our architecture, we used **3 Zookeeper instances** to secure the Spark master instances. Zookeeper may be installed on its own on a node, or together with Spark/Cassandra on a worker node. *See architecture*. It is important that each zookeeper node is aware of other zookeeper instances so that they form a quorum of 3. Replicated ZK instances are important for a resilient cluster, here we use a quorum of 3 nodes.

When Spark masters are made resilient, a leader is elected at startup. If one master fails, a new leader is elected in a transparent way, and workers are automatically linked to the new leader.

## Zookeeper configuration and startup
Installing Zookeeper on each node of the quorum :

```
wget https://www-eu.apache.org/dist/zookeeper/zookeeper-3.4.13/zookeeper-3.4.13.tar.gz;
tar xf zookeeper-3.4.13.tar.gz
```

Editing the **zoo.cfg** with the quorum addresses and server port :

```
server.1=<PUBLIC.IP.1>:2891:3881
server.2=<PUBLIC.IP.2>:2892:3882
server.3=<PUBLIC.IP.3>:2893:3883
```

Each node has its own port :

```
clientPort=2181 # For node 1
``` 

Finally ZK server is launched :

```
bin/zkServer.sh start
```

## Making Spark use Zookeeper

Spark masters must be launched in ZK recovery mode, with reference to the ZK servers. This goes in spark-env.sh for master nodes.

```
export SPARK_MASTER_OPTS="-Dspark.deploy.recoveryMode=ZOOKEEPER -Dspark.deploy.zookeeper.url=ip1:port1,ip2:port2,ip3:port3"
```
