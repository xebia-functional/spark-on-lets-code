#!/bin/bash

sbt ";project api;docker"

cd scripts && docker-compose up -d

sleep 60

docker exec -t namenode /usr/local/hadoop/bin/hadoop fs -mkdir /checkpoint

# Scaling out Spark:
docker-compose scale spark_worker=2
