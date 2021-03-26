package io.sapl.benchmark;

import io.sapl.benchmark.index.IndexType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
public class BenchmarkParameters {

    String name;

    long seed;

    IndexType indexType;

    BenchmarkType benchmarkType;

    int benchmarkRuns;

    int benchmarkIterations;

    String outputPath;

    String filePrefix;

    String configurationFile;

    boolean performCleanBenchmark;

    public String getName() {
        return String.format("Bench_%d_%s", seed, indexType);
    }

}
