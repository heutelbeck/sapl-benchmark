package io.sapl.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.sapl.benchmark.BenchmarkCase;
import io.sapl.benchmark.BenchmarkParameters;
import io.sapl.benchmark.BenchmarkType;
import io.sapl.generator.random.FullyRandomBenchmarkConfguration;
import io.sapl.generator.structured.StructuredRandomBenchmarkConfguration;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
@UtilityClass
public class ConfigurationFactory {

    private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public BenchmarkConfiguration<BenchmarkCase> parseConfigurationFile(BenchmarkParameters parameters) throws Exception {
        var configurationFileClass = parameters.getBenchmarkType() == BenchmarkType.FULLY_RANDOM
                ? FullyRandomBenchmarkConfguration.class
                : StructuredRandomBenchmarkConfguration.class;

        BenchmarkConfiguration<BenchmarkCase> configuration = (BenchmarkConfiguration<BenchmarkCase>) mapper
                .readValue(new File(parameters.getConfigurationFile()), configurationFileClass);

        log.info("found {} cases in configuration file", configuration.getCases().size());

        return configuration;
    }

}
