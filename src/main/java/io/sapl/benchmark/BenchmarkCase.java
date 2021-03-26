package io.sapl.benchmark;

import lombok.Data;

import java.nio.file.Path;

@Data
public class BenchmarkCase {

    String name;

    long seed;

    Path policyFolderPath;

    int numberOfRuns;

    int numberOfIterations;
}
