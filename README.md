[![Build Status](https://magnum.travis-ci.com/47deg/spark-on-lets-code.svg?token=x4DpWRL5qXeuK6kxqVSP&branch=master)](https://magnum.travis-ci.com/47deg/spark-on-lets-code)

# SparkOn

This small spark project provides the sample code which we've talked about in [this article](http://www.47deg.com/blog/spark-on-lets-code-part-1), in the 47 Degrees blog.

## App Requirements

* Cassandra

If you have not set up `Cassandra` locally, you might use docker and the `spotify/cassandra`, for instance, running a container in this way:

    docker run -d -p 9042:9042 -p 9160:9160 spotify/cassandra
    
* Twitter Credentials to connect to the Twitter API. Read more about it [here](https://dev.twitter.com/overview/documentation).

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

#License

Copyright (C) 2015 47 Degrees, LLC [http://47deg.com](http://47deg.com) [hello@47deg.com](mailto:hello@47deg.com)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
