# GDELT : A Cassandra resilient architecture

The GDELT Project monitors the world's broadcast, print, and web news from nearly every corner of every country in over 100 languages and identifies the people, locations, organizations, themes, sources, emotions, counts, quotes, images and events driving our global society every second of every day, creating a free open platform for computing on the entire world. With new files uploaded every 15 minutes, GDELT data bases contain more than 500 Gb of zipped data for the single year 2018.

In order to be able to work with a large amount of data, we have chosen to work with the following architecture :
- NoSQL : Cassandra
- AWS : EMR to transfer the data to Cassandra, and EC2 for the resiliency for the requests
- Visualization : A Zeppelin Notebook

## 1. The data

Description of the data Mentions and Events : http://data.gdeltproject.org/documentation/GDELT-Event_Codebook-V2.0.pdf
Description of the Graph of Events GKG : http://data.gdeltproject.org/documentation/GDELT-Global_Knowledge_Graph_Codebook-V2.1.pdf

![alt text](data.png)

A event is defined as an action that an actor (Actor1) takes on another actor (Actor2). A mention is an article or any source that talks about an event. The GKG database reflects the events that took place in the world, ordered by theme, type of event and location.

The conceptual model of the data is the following :
![alt text](concept.png)

## 2. Architecture

The architecture we have chosen is the following :
![alt text](archi.png)

The ZIP files are extracted from the GDELT website :
```
def fileDownloader(urlOfFileToDownload: String, fileName: String) = {
    val url = new URL(urlOfFileToDownload)
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(5000)
    connection.setReadTimeout(5000)
    connection.connect()

    if (connection.getResponseCode >= 400)
        println("error")
    else
        url #> new File(fileName) !!
}

fileDownloader("http://data.gdeltproject.org/gdeltv2/masterfilelist.txt", "/tmp/masterfilelist.txt") // save the list file to the Spark Master
fileDownloader("http://data.gdeltproject.org/gdeltv2/masterfilelist-translation.txt", "/tmp/masterfilelist_translation.txt") //same for Translation file

awsClient.putObject("fabien-mael-telecom-gdelt2018", "masterfilelist.txt", new File("/tmp/masterfilelist.txt") )
awsClient.putObject("fabien-mael-telecom-gdelt2018", "masterfilelist_translation.txt", new File( "/tmp/masterfilelist_translation.txt") )

val list_csv = spark.read.format("csv").option("delimiter", " ").
                    csv("s3a://fabien-mael-telecom-gdelt2018/masterfilelist.txt").
                    withColumnRenamed("_c0","size").
                    withColumnRenamed("_c1","hash").
                    withColumnRenamed("_c2","url")
val list_2018_tot = list_csv.where(col("url").like("%/2018%"))
list_2018_tot.select("url").repartition(100).foreach( r=> {
            val URL = r.getAs[String](0)
            val fileName = r.getAs[String](0).split("/").last
            val dir = "/mnt/tmp/"
            val localFileName = dir + fileName
            fileDownloader(URL,  localFileName)
            val localFile = new File(localFileName)
            AwsClient.s3.putObject("fabien-mael-telecom-gdelt2018", fileName, localFile )
            localFile.delete()
})

```
We duplicate this task for the translation data. Then, we need to create four data frames : 
- Mentions in english
- Events in english
- Mentions translated
- Events translated

This is done the following way :
```
val mentionsRDD_trans = sc.binaryFiles("s3a://fabien-mael-telecom-gdelt2018/201801*translation.mentions.CSV.zip"). // charger quelques fichers via une regex
   flatMap {  // decompresser les fichiers
       case (name: String, content: PortableDataStream) =>
          val zis = new ZipInputStream(content.open)
          Stream.continually(zis.getNextEntry).
                takeWhile{ case null => zis.close(); false
                       case _ => true }.
                flatMap { _ =>
                    val br = new BufferedReader(new InputStreamReader(zis))
                    Stream.continually(br.readLine()).takeWhile(_ != null)
                }
    }
val mentionsDF_trans = mentionsRDD_trans.map(x => x.split("\t")).map(row => row.mkString(";")).map(x => x.split(";")).toDF()
```


