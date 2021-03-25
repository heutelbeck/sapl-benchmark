package io.sapl.benchmark;

import io.sapl.benchmark.index.IndexBenchmarkCommand;
import picocli.CommandLine;

public class BenchmarkMain {
    public static void main(String... args) {
        System.exit(new CommandLine(new IndexBenchmarkCommand()).execute(args));
    }
}
