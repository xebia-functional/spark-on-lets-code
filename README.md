[![Build Status](https://travis-ci.org/47deg/spark-on-lets-code.svg?branch=master)](https://travis-ci.org/47deg/spark-on-lets-code)
[![Codacy Badge](https://api.codacy.com/project/badge/a7ac855c47cc46ea80b6c69907415f5c)](https://www.codacy.com/app/47deg/spark-on-lets-code)

# Spark On

This small spark project provides the sample code which we've talked about in `Spark On` blog post series at [47D Blog](http://www.47deg.com/blog/tags/sparkonletscode).

## App Requirements

* Twitter Credentials to connect to the Twitter API. Read more about it [here](https://dev.twitter.com/overview/documentation).
* In this README.md file you will see the IP address `192.168.99.100`. If you are using [docker-machine](https://docs.docker.com/machine/), `docker-machine ip <machine-name>` command should returns the specific host’s IP address. You must replace `192.168.99.100` for the IP address in your case.
* The whole infrastructure has been tested on a Apple Macbook Pro (2,7 GHz Intel Core i5, 16 GB 1867 MHz DDR3).

To start off, we need to define a few environment variables in this [config file](https://github.com/47deg/spark-on-lets-code/blob/master/scripts/sparkOn.env#L5).

## Deploy Docker Infrastructure

### Start Cluster

We've defined a bash script to deploy the whole cluster dependencies, including the Spark Streaming Application, which means, we can run it in this way:

    scripts/deploy.sh

By default, the infrastructure deployed will be:

- Spark Cluster:
    - 1 Spark Master
    - 2 Spark Worker nodes
- Cassandra Cluster:
    - 2 Cassandra Docker Containers
    - 1 Docker Container with [DataStax Opscenter](http://www.datastax.com/products/datastax-enterprise-visual-admin)
- Kafka Cluster:
    - 1 docker node zookeper
    - 3 docker containers running as kafka brokers
- Hadoop HDFS Cluster:
    - 1 docker container running as namenode
    - 1 docker container running as datanode
- 1 Docker container for our Streaming App

### Scaling Out Services

For instance, to increase the Spark Workers available:

    docker-compose scale spark_worker=5

### Start the Streaming

If everything is functioning correctly, we can start the Twitter Streaming as follows:

    curl -X "POST" "http://192.168.99.100:9090/twitter-streaming" \
      -H "Content-Type: application/json" \
      -d $'{
      "recreateDatabaseSchema": true,
      "filters": [
        "lambda",
        "scala",
        "akka",
        "spray",
        "play2",
        "playframework",
        "spark",
        "java",
        "python",
        "cassandra",
        "bigdata",
        "47 Degrees",
        "47Degrees",
        "47Deg",
        "programming",
        "chicharrones",
        "cat",
        "dog"
      ]
    }'

### Connect to the Web Socket

For instance, you could use [Simple WebSocket Client](https://goo.gl/8Jw6K) for Google Chrome, opening the connection in this URL ws://192.168.99.100:9090/trending-topics .

### Stop Cluster

We can stop the streaming gracefully, before stopping the cluster:

    curl -X "DELETE" "http://192.168.99.100:9090/twitter-streaming"

And then, from the shell:

    cd scripts
    docker-compose stop
    docker-compose rm

# HTTP Application API - FORMAT: 1A

## Spark Streaming Status Endpoint [/twitter-streaming]

Starts, stops and fetch the Spark Streaming Context status in the application. Note: once you have stopped the context you can not start again.

### Get Streaming Status [GET]

+ Response 200 (application/json)

        {
            "message": "The streaming has been created, but not been started yet"
        }

### Start Streaming [POST]

This action allows you to stop the Spark Streaming Context.

+ Response 200 (application/json)

        {
            "message": "Started"
        }

+ Response 400

### Stop Streaming [DELETE]

This action allows you to start the Spark Streaming Context.

+ Response 200 (application/json)

        {
            "message": "The streaming has been stopped"
        }

+ Response 400

## WS Filtered Twitter Word Tracks [WS /trending-topics]

Open a websocket in order to show each new filtered track word is found.

#License

Copyright (C) 2015 47 Degrees, LLC [http://47deg.com](http://47deg.com) [hello@47deg.com](mailto:hello@47deg.com)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
