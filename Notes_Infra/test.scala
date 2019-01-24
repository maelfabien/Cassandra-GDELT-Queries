import com.datastax.spark.connector._
import org.apache.spark.sql.cassandra._

val rdd = sc.cassandraTable("temperature", "stats")
println(rdd.count)




./spark-shell --jars jars/hadoop-aws-2.7.3.jar,jars/aws-java-sdk-1.7.4.jar


./spark-shell --jars jars/aws-java-sdk-1.7.4.jar


