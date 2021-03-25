package io.sapl.benchmark;

import io.sapl.benchmark.index.IndexType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BenchmarkConfiguration {

    String name;

    long seed = 0L;

    IndexType indexType;

    BenchmarkType benchmarkType;

    int numberOfBenchmarkRuns = 300;

    String outputPath = ".";

    String filePrefix = "";

    String configurationFile = "";

    boolean performCleanBenchmark = false;

    public String getName() {
        return String.format("Bench_%d_%s", seed, indexType);
    }
}
