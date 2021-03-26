#!/bin/bash

java -jar target/sapl-benchmark-index-2.0.0-SNAPSHOT.jar --benchmark=FULLY_RANDOM \
  --file=src/main/resources/examples/fully_random/default_fully_random.yml \
  --index=CANONICAL --output=/tmp/sapl-benchmark/ --prefix='sapl-bench-2021_03_26-' \
  --runs=1 --circles=1 --delete=false