package io.sapl.generator;

import io.sapl.benchmark.BenchmarkParameters;
import io.sapl.generator.random.FullyRandomConfiguration;
import io.sapl.generator.random.FullyRandomPolicyGenerator;
import io.sapl.generator.random.FullyRandomSubscriptionGenerator;
import io.sapl.generator.structured.DomainDataContainer;
import io.sapl.generator.structured.StructuredRandomConfiguration;
import io.sapl.generator.structured.StructuredRandomPolicyGenerator;
import io.sapl.generator.structured.StructuredRandomSubscriptionGenerator;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class GeneratorFactory {

    public PolicyGenerator policyGeneratorByType(BenchmarkParameters parameters, GeneralConfiguration configuration,
                                                 PolicyUtil policyUtil) {
        switch (parameters.getBenchmarkType()) {
            case FULLY_RANDOM:

                return new FullyRandomPolicyGenerator((FullyRandomConfiguration) configuration, null);
            case STRUCTURED_RANDOM:
                //fall through
            default:

                log.info("Generating benchmark domain...");
                var domainDataContainer =
                        new DomainDataContainer((StructuredRandomConfiguration) configuration, policyUtil);

                return new StructuredRandomPolicyGenerator(domainDataContainer, policyUtil);
        }
    }

    //TODO
    public SubscriptionGenerator subscriptionGeneratorByType(BenchmarkParameters parameters,
                                                             GeneralConfiguration configuration) {
        switch (parameters.getBenchmarkType()) {
            case FULLY_RANDOM:

                return new FullyRandomSubscriptionGenerator(null, (FullyRandomConfiguration) configuration);
            case STRUCTURED_RANDOM:
                //fall through
            default:
                return new StructuredRandomSubscriptionGenerator(null, 0, 0);
        }
    }

}
