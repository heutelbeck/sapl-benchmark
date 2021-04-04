package io.sapl.benchmark;

import lombok.Data;

import java.nio.file.Path;

@Data
public class BenchmarkCase {

    protected String name;

    protected long seed;

    Path policyFolderPath;

    int numberOfRuns;

    int numberOfIterations;
}
