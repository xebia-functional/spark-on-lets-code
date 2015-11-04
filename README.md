[![Build Status](https://travis-ci.org/47deg/spark-on-lets-code.svg?branch=master)](https://travis-ci.org/47deg/spark-on-lets-code)
[![Codacy Badge](https://api.codacy.com/project/badge/a7ac855c47cc46ea80b6c69907415f5c)](https://www.codacy.com/app/47deg/spark-on-lets-code)

# Spark On

This small spark project provides the sample code which we've talked about in `Spark On` blog post series at [47D Blog](http://www.47deg.com/blog).

## App Requirements

* Twitter Credentials to connect to the Twitter API. Read more about it [here](https://dev.twitter.com/overview/documentation).

## Deploy Docker Infrastructure

## Start Cluster

Deploy in a docker cluster

    sh scripts/deploy.sh

By default, the infrastructure deployed will be:

- 2 Cassandra Docker Containers
- 1 Docker Container [DataStax Opscenter](http://www.datastax.com/products/datastax-enterprise-visual-admin)
- 1 node 

## Scaling Out Services

For instance, to increase the Spark Workers available:

    docker-compose scale spark_worker=5

## Stop Cluster

    docker-compose stop

## Remove Cluster

    docker-compose rm

## Running with SBT or Activator

    ./activator -Dtwitter.credentials.consumerKey="{CONSUMER_KEY}" \
    -Dtwitter.credentials.consumerSecret="{CONSUMER_SECRET}" \
    -Dtwitter.credentials.accessToken="{ACCESS_TOKEN}" \
    -Dtwitter.credentials.accessTokenSecret="{ACCESS_TOKEN_SECRET}" \
    "run"

Or

    sbt -Dtwitter.credentials.consumerKey="{CONSUMER_KEY}" \
    -Dtwitter.credentials.consumerSecret="{CONSUMER_SECRET}" \
    -Dtwitter.credentials.accessToken="{ACCESS_TOKEN}" \
    -Dtwitter.credentials.accessTokenSecret="{ACCESS_TOKEN_SECRET}" \
    "run"

## Running fat JAR

First, we need to generate the fat jar with the [sbt assembly plugin](https://github.com/sbt/sbt-assembly):

	sbt assembly

and then:

    java -Dtwitter.credentials.consumerKey="{CONSUMER_KEY}" \
    -Dtwitter.credentials.consumerSecret="{CONSUMER_SECRET}" \
    -Dtwitter.credentials.accessToken="{ACCESS_TOKEN}" \
    -Dtwitter.credentials.accessTokenSecret="{ACCESS_TOKEN_SECRET}" \
    -cp /path/to/spark-on-lets-code/target/scala-2.11/sparkon-1.0.0.jar \
    com.fortysevendeg.sparkon.api.http.Boot

On the other hand, if you have set up the environment variables *CONSUMER_KEY*, *CONSUMER_SECRET*, *ACCESS_TOKEN* and *ACCESS_TOKEN_SECRET*:

    java -cp /path/to/spark-on-lets-code/target/scala-2.11/sparkon-1.0.0.jar \
    com.fortysevendeg.sparkon.api.http.Boot

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
