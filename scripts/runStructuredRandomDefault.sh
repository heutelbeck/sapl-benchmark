#!/bin/bash

echo "#################################"
echo "# STRUCTURED_RANDOM, CANONICAL  #"
echo "#################################"

TIMESTAMP=$(date '+%Y-%m-%d_%H-%M-%S')

java -jar target/sapl-benchmark-index-2.0.0-SNAPSHOT.jar --benchmark=STRUCTURED_RANDOM \
  --file=src/main/resources/examples/structured_random/default_structured_random.yml \
  --index=CANONICAL --output=/tmp/sapl-benchmark/ --prefix="CANONICAL-$TIMESTAMP" \
  --runs=100 --circles=1 --delete=false

echo "#################################"
echo "#   STRUCTURED_RANDOM, NAIVE    #"
echo "#################################"

TIMESTAMP=$(date '+%Y-%m-%d_%H-%M-%S')

java -jar target/sapl-benchmark-index-2.0.0-SNAPSHOT.jar --benchmark=STRUCTURED_RANDOM \
  --file=src/main/resources/examples/structured_random/default_structured_random.yml \
  --index=NAIVE --output=/tmp/sapl-benchmark/ --prefix="NAIVE-$TIMESTAMP" \
  --runs=100 --circles=1 --delete=false