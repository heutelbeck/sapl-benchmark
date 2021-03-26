package io.sapl.generator;

import io.sapl.benchmark.BenchmarkCase;
import io.sapl.benchmark.BenchmarkParameters;
import io.sapl.domain.model.DomainDataContainer;
import io.sapl.generator.random.FullyRandomCase;
import io.sapl.generator.random.FullyRandomPolicyGenerator;
import io.sapl.generator.random.FullyRandomSubscriptionGenerator;
import io.sapl.generator.structured.StructuredRandomCase;
import io.sapl.generator.structured.StructuredRandomPolicyGenerator;
import io.sapl.generator.structured.StructuredRandomSubscriptionGenerator;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class GeneratorFactory {

    private DomainDataContainer domainDataContainer;


    public PolicyGenerator policyGeneratorByType(BenchmarkParameters parameters, BenchmarkCase benchmarkCase, PolicyUtil policyUtil) {
        switch (parameters.getBenchmarkType()) {
            case FULLY_RANDOM:

                return new FullyRandomPolicyGenerator((FullyRandomCase) benchmarkCase, policyUtil);
            case STRUCTURED_RANDOM:
                //fall through
            default:
                log.info("Generating benchmark domain...");
                domainDataContainer =
                        new DomainDataContainer((StructuredRandomCase) benchmarkCase, policyUtil);

                return new StructuredRandomPolicyGenerator(domainDataContainer, policyUtil);
        }
    }

    //TODO
    public SubscriptionGenerator subscriptionGeneratorByType(BenchmarkParameters parameters, BenchmarkCase benchmarkCase, PolicyUtil policyUtil) {
        switch (parameters.getBenchmarkType()) {
            case FULLY_RANDOM:
                var randomCase = (FullyRandomCase) benchmarkCase;
                return new FullyRandomSubscriptionGenerator(policyUtil, randomCase);
            case STRUCTURED_RANDOM:
                //fall through
            default:
                var structuredCase = (StructuredRandomCase) benchmarkCase;
                return new StructuredRandomSubscriptionGenerator(domainDataContainer, structuredCase
                        .getProbabilityEmptySubscriptionNode(), structuredCase.getProbabilityEmptySubscription());
        }
    }

}
