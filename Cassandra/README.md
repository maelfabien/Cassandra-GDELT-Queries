# Cassandra (C*)

Here we will see how we set a C* ring of 5 nodes to store GDELT data.
1. Setup and Launch
2. Settings and tuning
3. Creating keyspace and tables

## Setup 

C* setup is straightforward, the complexity resides in the parameter tuning of the database. C* nodes are easy to install :

```console
wget https://www-eu.apache.org/dist/cassandra/3.11.3/apache-cassandra-3.11.3-bin.tar.gz;
tar xf apache-cassandra-3.11.3-bin.tar.gz;
rm apache-cassandra-3.11.3-bin.tar.gz;
```
Launching a C* node is also quite simple :
```console
./bin/cassandra
```

## Cassandra.yaml settings

In our project, we used the same C* settings template for each node. With the right cassandra.yaml setings, 
a starting node automatically joins the ring.

We chose to setup a 5 nodes ring with a replication factor of 3.
Having 5 nodes means each node manages 3/5th of the data, which is a good practise for performance. 
On top of this, we can tolerate the loss of 2 C* nodes.
The **Murmur3Partitioner** uses tokens to assign equal portions of data to each node
and evenly distribute data from all the tables throughout the ring.

For the networking part we used this template adapted to each node :
```yaml
cluster_name: 'Test Cluster'
listen_address: your_server_ip
rpc_address: your_server_ip
seed_provider:
  - class_name: org.apache.cassandra.locator.SimpleSeedProvider
    parameters:
         - seeds: "ip1,ip2,...ipN"
endpoint_snitch: EC2Snitch
```

One interesting point is that nodes must all have the same hardware configuration, 
otherwise it seems that they organise themselves in separate groups.

Other settings :
- Comment every line in *cassandra-rackdc.properties* so that every node of the ring belongs to the same rack and datacenter.
- Security policies in AllowAll.
- Increase **write/read_request_timeout_in_ms** to absord heavy load read/write and limit timeout failures : from 2000 to 5000ms.
- **EC2Snitch** for simple cluster deployments on Amazon EC2 where all nodes in the cluster are within a single region.

## Data management
We created a single keyspace to store every tables.

```CQL
CREATE KEYSPACE gdelt_datas WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 3};
USE gdelt_datas;
```

For each request we broke down into up to 4 tables to create "views" of the data.

```sql
CREATE TABLE q_gkg(
  date_format int,
  category text,
  count int,
  subject text,
  country text,
  latitude double,
  longitude double,
PRIMARY KEY (category, subject, country));
```

## Useful tools
- nodetool [status, describecluster...]
- nodetool cfstats -- <keyspace>.<table> (keyspace/table information)
- cqlsh
- Zeppelin Cassandra interpreter

## Sources
- http://diego-pacheco.blogspot.com/2017/12/deploy-setup-cassandra-3x-cluster-on-ec2.html
- https://aws.amazon.com/fr/blogs/big-data/best-practices-for-running-apache-cassandra-on-amazon-ec2/
- https://hackernoon.com/how-to-create-simple-cassandra-cluster-on-aws-8407e4d60384
- https://techblog.bozho.net/setting-cassandra-cluster-aws/
- http://www.doanduyhai.com/blog/?p=13216

