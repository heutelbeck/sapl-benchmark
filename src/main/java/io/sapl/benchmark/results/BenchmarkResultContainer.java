package io.sapl.benchmark.results;

import io.sapl.benchmark.BenchmarkParameters;
import io.sapl.generator.PolicyCharacteristics;
import io.sapl.benchmark.index.IndexType;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
public class BenchmarkResultContainer {

    final String benchmarkId;
    final long benchmarkTimestamp;
    final String runtimeInfo;

    final IndexType indexType;
    final int runs;
    final int iterations;

    List<BenchmarkRecord> data = new LinkedList<>();
    List<Double> minValues = new LinkedList<>();
    List<Double> maxValues = new LinkedList<>();
    List<Double> avgValues = new LinkedList<>();
    List<Double> mdnValues = new LinkedList<>();
    List<String> identifier = new LinkedList<>();
    List<PolicyCharacteristics> characteristics = new LinkedList<>();
    List<Long> seeds = new LinkedList<>();

    List<BenchmarkAggregate> aggregateData = new LinkedList<>();


    public BenchmarkResultContainer(BenchmarkParameters configuration) {
        this.benchmarkId = UUID.randomUUID().toString();
        this.benchmarkTimestamp = System.currentTimeMillis();
        this.runtimeInfo = String.format("%s_%s", System.getProperty("java.vendor"),
                System.getProperty("java.version"));
        this.indexType = configuration.getIndexType();
        this.runs = configuration.getRunsPerCase();
        this.iterations = configuration.getBenchmarkIterations();
    }
}
