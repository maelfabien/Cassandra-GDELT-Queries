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
