package io.sapl.benchmark;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class BenchmarkCase {

    String name;

    long seed;

    Path policyFolderPath;

    int numberOfRuns;

    int numberOfIterations;
}
