package io.sapl.benchmark.index;

import io.sapl.benchmark.BenchmarkCase;
import io.sapl.benchmark.BenchmarkExecutor;
import io.sapl.benchmark.BenchmarkParameters;
import io.sapl.benchmark.BenchmarkType;
import io.sapl.benchmark.results.BenchmarkResultContainer;
import io.sapl.benchmark.results.BenchmarkResultWriter;
import io.sapl.benchmark.util.ManifestVersionProvider;
import io.sapl.generator.BenchmarkConfiguration;
import io.sapl.generator.ConfigurationFactory;
import io.sapl.generator.GeneratorFactory;
import io.sapl.generator.PolicyAnalyzer;
import io.sapl.generator.PolicyUtil;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Slf4j
@ToString
@Command(name = "index", mixinStandardHelpOptions = true, versionProvider = ManifestVersionProvider.class, description = "Performs a benchmark on the PRP indexing data structures.")
public class IndexBenchmarkCommand implements Callable<Integer> {

    @Option(names = {"-i", "--index"}, description = "Type of the index used for the benchmark.")
    private IndexType indexType = IndexType.CANONICAL;

    @Option(names = {"-b", "--benchmark"}, description = "Type of the benchmark.")
    private BenchmarkType benchmarkType = BenchmarkType.STRUCTURED_RANDOM;

    @Option(names = {"-o", "--output"}, description = "Path to the output directory for benchmark results.")
    private String outputPath = ".";

    @Option(names = {"-p", "--prefix"}, description = "Prefix string for output files.")
    private String filePrefix = "";

    @Option(names = {"-f", "--file"}, description = "Configuration file for custom benchmark. By default the benchmark runs the pre-configured benchmark suite. If this option is specified, the benchmark configured here is run.")
    private String benchmarkConfigurationFile = "";

    @Option(names = {"-r", "--runs"}, description = "The number of repetitions executed for every case defined in the benchmark.")
    private int numberOfRunsPerCase = 1;

    @Option(names = {"-c", "--circles"}, description = "The benchmark will be repeated up to the number specified in this parameter.")
    private int numberOfBenchmarkIterations = 1;

    @Option(names = {"-d", "--delete"}, description = "Perform a clean benchmark where all policies are deleted after the results were produced.")
    private boolean deletePoliciesAfterBenchmark = false;


    @Override
    public Integer call() throws Exception {
        log.info("Running an index benchmark... {}", this);

        var tempDirectory = prepareTempDirectory();
        var outputDirectory = Path.of(outputPath);
        log.info("Results will be written to: {}", outputDirectory.toAbsolutePath());

        var parameters = BenchmarkParameters.builder()
//                .seed(0L)
                .indexType(indexType)
                .benchmarkType(benchmarkType)
                .runsPerCase(numberOfRunsPerCase)
                .benchmarkIterations(numberOfBenchmarkIterations)
                .outputPath(outputPath)
                .filePrefix(filePrefix)
                .configurationFile(benchmarkConfigurationFile)
                .deletePoliciesAfterBenchmark(deletePoliciesAfterBenchmark)
                .build();
        log.info("Benchmark parameters: {}", parameters);

        BenchmarkConfiguration<BenchmarkCase> configuration = ConfigurationFactory.parseConfigurationFile(parameters);

        var resultWriter = new BenchmarkResultWriter(parameters);
        var resultContainer = new BenchmarkResultContainer(parameters);

        for (BenchmarkCase benchmarkCase : configuration.getCases()) {
            log.info("######################################################");
            log.info("      EXECUTING CASE: {}      ", benchmarkCase.getName());
            log.info("######################################################");

            var policyUtil = new PolicyUtil(parameters.isDeletePoliciesAfterBenchmark(), benchmarkCase.getSeed());

            log.info("Generating policies...");
            var generator = GeneratorFactory.policyGeneratorByType(parameters, benchmarkCase, policyUtil);
            generator.generatePolicies(tempDirectory);
            benchmarkCase.setPolicyFolderPath(tempDirectory);

            log.info("Analyzing policies...");
            var characteristics = new PolicyAnalyzer(benchmarkCase.getPolicyFolderPath()).analyzeSaplDocuments();

            log.info("Running benchmark...");
            var executor = new BenchmarkExecutor(policyUtil);
            var results = executor.runBenchmark(parameters, benchmarkCase);
            resultWriter.addResultsForCaseToContainer(resultContainer, benchmarkCase, results, characteristics);


            log.info("Writing results...");
            double[] times = new double[results.size()];
            resultWriter.writeDetailsChart(results, times, benchmarkCase.getName());
            resultWriter.addSeriesToOverviewChart(times, benchmarkCase.getName());

        }

        log.info("Benchmark completed. Writing final results...");
        resultWriter.writeFinalResults(resultContainer);

        if (parameters.isDeletePoliciesAfterBenchmark()) {
            log.info("Deleting generated policies...");
            deleteTempDirectory(tempDirectory);
        }

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
