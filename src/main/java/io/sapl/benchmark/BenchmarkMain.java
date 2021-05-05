package io.sapl.benchmark;

import io.sapl.benchmark.index.IndexBenchmarkCommand;
import picocli.CommandLine;
import reactor.tools.agent.ReactorDebugAgent;

public class BenchmarkMain {
    public static void main(String... args) {
//        ReactorDebugAgent.init();
        System.exit(new CommandLine(new IndexBenchmarkCommand()).execute(args));
    }
}
