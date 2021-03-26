package io.sapl.benchmark.index;

import io.sapl.benchmark.BenchmarkCase;
import io.sapl.benchmark.BenchmarkExecutor;
import io.sapl.benchmark.BenchmarkParameters;
import io.sapl.benchmark.BenchmarkType;
import io.sapl.benchmark.results.BenchmarkRecord;
import io.sapl.benchmark.results.BenchmarkResultContainer;
import io.sapl.benchmark.results.BenchmarkResultWriter;
import io.sapl.benchmark.util.ManifestVersionProvider;
import io.sapl.generator.BenchmarkConfiguration;
import io.sapl.generator.ConfigurationFactory;
import io.sapl.generator.GeneratorFactory;
import io.sapl.generator.PolicyUtil;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@ToString
@Command(name = "index", mixinStandardHelpOptions = true, versionProvider = ManifestVersionProvider.class, description = "Performs a benchmark on the PRP indexing data structures.")
public class IndexBenchmarkCommand implements Callable<Integer> {

    @Option(names = {"-i", "--index"}, description = "Type of the index used for the benchmark.")
    private String indexType = "";

    @Option(names = {"-b", "--benchmark"}, description = "Type of the benchmark.")
    private String benchmarkType = "";

    @Option(names = {"-o", "--output"}, description = "Path to the output directory for benchmark results.")
    private String outputPath = ".";

    @Option(names = {"-p", "--prefix"}, description = "Prefix string for output files.")
    private String filePrefix = "";

    @Option(names = {"-c",
            "--custom"}, description = "Configuration file for custom benchmark. By default the benchmark runs the pre-configured benchmark suite. If this option is specified, the benchmark configured here is run.")
    private String benchmarkConfigurationFile = "";

    @Override
    public Integer call() throws Exception {
        log.info("Running an index benchmark... {}", this);

        var tempDirectory = prepareTempDirectory();
        var outputDirectory = Path.of(outputPath);
        log.info("Results will be written to: {}", outputDirectory.toAbsolutePath());

        var parameters = BenchmarkParameters.builder()
                .seed(0L)
                .indexType(IndexType.valueOf(indexType))
                .benchmarkType(BenchmarkType.valueOf(benchmarkType))
                .benchmarkRuns(300)
                .benchmarkIterations(1)
                .outputPath(outputPath)
                .filePrefix(filePrefix)
                .configurationFile(benchmarkConfigurationFile)
                .build();
        log.info("Benchmark parameters: {}", parameters);


        BenchmarkConfiguration<BenchmarkCase> configuration = ConfigurationFactory.parseConfigurationFile(parameters);

        var resultWriter = new BenchmarkResultWriter(parameters.getOutputPath(), parameters.getIndexType());
        var resultContainer = new BenchmarkResultContainer(parameters);


        for (BenchmarkCase benchmarkCase : configuration.getCases()) {
            var policyUtil = new PolicyUtil(parameters.isPerformCleanBenchmark(), benchmarkCase.getSeed());

            log.info("Generating policies ...");

            var generator = GeneratorFactory.policyGeneratorByType(parameters, benchmarkCase, policyUtil);
            generator.generatePolicies(tempDirectory);


            log.info("Running benchmark ...");

            var executor = new BenchmarkExecutor(policyUtil);
            List<BenchmarkRecord> results = executor.runBenchmark(parameters, benchmarkCase);
            resultWriter.addResultsForCaseToContainer(resultContainer, benchmarkCase, results);

            double[] times = new double[results.size()];
            resultWriter.writeDetailsChart(results, times, benchmarkCase.getName());
            resultWriter.addSeriesToOverviewChart(times, benchmarkCase.getName());

        }

        log.info("Benchmark completed. Writing results ...");

        resultWriter.writeFinalResults(resultContainer);

        return 0;
    }


    private Path prepareTempDirectory() throws IOException {
        var tempDirectory = Files.createTempDirectory("sapl-benchmark-");
        log.info("Created temporary directory for storing generated policies: {}", tempDirectory.toAbsolutePath());
        return tempDirectory;
    }

    private void deleteTempDirectory(Path tempDirectory) throws IOException {
        log.info("Delete temporary directory and its contents: {}", tempDirectory);
        FileUtils.deleteDirectory(tempDirectory.toFile());
    }

}
