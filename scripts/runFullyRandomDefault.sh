#!/bin/bash

echo "#################################"
echo "#    FULLY_RANDOM, CANONICAL    #"
echo "#################################"

TIMESTAMP=$(date '+%Y-%m-%d_%H-%M-%S')

java -jar target/sapl-benchmark-index-2.0.0-SNAPSHOT.jar --benchmark=FULLY_RANDOM \
  --file=src/main/resources/examples/fully_random/default_fully_random.yml \
  --index=CANONICAL --output=/tmp/sapl-benchmark/ --prefix="CANONICAL-$TIMESTAMP" \
  --runs=100 --circles=1 --delete=false


echo "#################################"
echo "#     FULLY_RANDOM, NAIVE       #"
echo "#################################"

TIMESTAMP=$(date '+%Y-%m-%d_%H-%M-%S')

java -jar target/sapl-benchmark-index-2.0.0-SNAPSHOT.jar --benchmark=FULLY_RANDOM \
  --file=src/main/resources/examples/fully_random/default_fully_random.yml \
  --index=NAIVE --output=/tmp/sapl-benchmark/ --prefix="NAIVE-$TIMESTAMP" \
  --runs=100 --circles=1 --delete=false