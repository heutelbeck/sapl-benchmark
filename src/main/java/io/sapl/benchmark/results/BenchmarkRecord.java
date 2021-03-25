package io.sapl.benchmark.results;

import lombok.Value;

@Value
public class BenchmarkRecord {

    int number;

    String name;

    double timePreparation;

    double timeDuration;

    String request;

    String response;
}
