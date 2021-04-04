package io.sapl.benchmark;

import io.sapl.benchmark.index.IndexType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BenchmarkParameters {

    //    String name;

    //    long seed;

    IndexType indexType;

    BenchmarkType benchmarkType;

    int runsPerCase;

    int benchmarkIterations;

    String outputPath;

    String filePrefix;

    String configurationFile;

    boolean deletePoliciesAfterBenchmark;

}
