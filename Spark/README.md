1. Setting up Spark masters (link to ZK, Adding Cassandra JAR dependencies)
2. Setting up Spark workers (master IPs, setting up crontab to launch worker at startup)

Rename **spark-env.sh.template** : Change IP addresses to node public IP
Rename **spark-default.sh** : Modify .jar exports (datastax connector), Cassandra IPS, master IPS

wget jars : Cassandra connection dependencies
```
sudo wget http://central.maven.org/maven2/com/twitter/jsr166e/1.1.0/jsr166e-1.1.0.jar;
sudo wget http://central.maven.org/maven2/com/datastax/spark/spark-cassandra-connector_2.11/2.4.0/spark-cassandra-connector_2.11-2.4.0.jar;
```

#### Set the Cassandra connection for Spark interpreter in Zeppelin

```
spark.cassandra.connection.host :: IPCasandra1,IPCassandra2
```
