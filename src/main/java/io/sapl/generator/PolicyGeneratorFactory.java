package io.sapl.generator;

import com.google.common.base.Strings;
import io.sapl.benchmark.BenchmarkConfiguration;
import io.sapl.generator.random.FullyRandomPolicyGenerator;
import io.sapl.generator.structured.DomainDataContainer;
import io.sapl.generator.structured.StructuredRandomPolicyGenerator;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
@UtilityClass
public class PolicyGeneratorFactory {


    public PolicyGenerator policyGeneratorByType(BenchmarkConfiguration configuration, Path policyDirectoryPath) {
        switch (configuration.getBenchmarkType()) {

            case FULLY_RANDOM:
                var fullyRandomConfigurationConfiguration =
                        Strings.isNullOrEmpty(configuration.getConfigurationFile())
                                ? ConfigurationFactory.fullyRandomDefault()
                                : ConfigurationFactory
                                .parseFullyRandomConfigurationFile(configuration.getConfigurationFile());


                return new FullyRandomPolicyGenerator(fullyRandomConfigurationConfiguration, null);
            case STRUCTURED_RANDOM:
                //fall through
            default:
                var structuredRandomConfiguration =
                        Strings.isNullOrEmpty(configuration.getConfigurationFile())
                                ? ConfigurationFactory.structuredRandomDefault()
                                : ConfigurationFactory
                                .parseStructuredRandomConfigurationFile(configuration.getConfigurationFile());


                log.info("Generating benchmark domain...");
                var domainUtil = new PolicyUtil(configuration.isPerformCleanBenchmark(), configuration.getSeed());
                var domainDataContainer = new DomainDataContainer(structuredRandomConfiguration, domainUtil);

                return new StructuredRandomPolicyGenerator(domainDataContainer, domainUtil);
        }
    }

}
