#!/bin/bash

java -jar target/sapl-benchmark-index-2.0.0-SNAPSHOT.jar --benchmark=FULLY_RANDOM \
  --custom=src/main/resources/examples/fully_random/default_fully_random.yml \
  --index=NAIVE --output=/tmp/sapl-benchmark/ --prefix='sapl-bench-2021_03_26-'