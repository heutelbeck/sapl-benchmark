package io.sapl.benchmark.results;

import lombok.Value;

@Value
public class BenchmarkAggregate {

    String name;

    double min;

    double max;

    double avg;

    double mdn;

    long seed;

    int policyCount;

    int variableCount;

    int runs;

    int iterations;
}
