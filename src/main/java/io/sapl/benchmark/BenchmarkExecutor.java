/*
 * Copyright © 2017-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.benchmark;

import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.benchmark.index.IndexFactory;
import io.sapl.benchmark.results.BenchmarkRecord;
import io.sapl.generator.GeneratorFactory;
import io.sapl.generator.PolicyAnalyzer;
import io.sapl.generator.PolicyUtil;
import io.sapl.generator.SubscriptionGenerator;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.functions.FunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.interpreter.pip.AttributeContext;
import io.sapl.prp.PolicyRetrievalResult;
import io.sapl.prp.index.ImmutableParsedDocumentIndex;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class BenchmarkExecutor {

    private static final double MILLION = 1000000.0D;

    private static final Map<String, JsonNode> VARIABLES = Collections.emptyMap();
    private static final AttributeContext ATTRIBUTE_CONTEXT = new AnnotationAttributeContext();
    private static final FunctionContext FUNCTION_CONTEXT = new AnnotationFunctionContext();

    private final PolicyUtil policyUtil;

    public List<BenchmarkRecord> runBenchmark(BenchmarkParameters parameters, BenchmarkCase benchmarkCase) {

        List<BenchmarkRecord> results = new LinkedList<>();

        var subscriptionGenerator = GeneratorFactory.subscriptionGeneratorByType(parameters, benchmarkCase, policyUtil);

        log.info("running benchmark with config={}, runs={}", benchmarkCase.getName(), parameters.getBenchmarkRuns());

        try {
            log.debug("init index");
            // create PRP
            long begin = System.nanoTime();
            log.info("Load generated polices into index...");
            var initializedIndex = IndexFactory.indexByTypeForDocumentsIn(parameters.getIndexType(),
                    benchmarkCase.getPolicyFolderPath().toString());

            double timePreparation = nanoToMs(System.nanoTime() - begin);

            // warm up
            warmUp(initializedIndex);

            // generate AuthorizationSubscription
            List<AuthorizationSubscription> subscriptions =
                    subscriptionGenerator.generateSubscriptions(parameters.getBenchmarkRuns());

            for (int j = 0; j < parameters.getBenchmarkRuns(); j++) {

                AuthorizationSubscription request = policyUtil.getRandomElement(subscriptions);

                long start = System.nanoTime();
                EvaluationContext subscriptionScopedEvaluationCtx = new EvaluationContext(ATTRIBUTE_CONTEXT,
                        FUNCTION_CONTEXT, VARIABLES).forAuthorizationSubscription(request);
                PolicyRetrievalResult result = initializedIndex.retrievePolicies(subscriptionScopedEvaluationCtx)
                        .block();

                if (result == null)
                    throw new IllegalStateException("Policy retrieval returned 'null' result.");

                long end = System.nanoTime();

                double timeRetrieve = nanoToMs(end - start);

                // Objects.requireNonNull(result);
                AuthorizationDecision decision = AuthorizationDecision.INDETERMINATE;
                // AuthorizationDecision decision = DOCUMENTS_COMBINATOR
                // .combineMatchingDocuments(result.getMatchingDocuments(), false,
                // request, ATTRIBUTE_CONTEXT, FUNCTION_CONTEXT, VARIABLES).blockFirst();
                // Objects.requireNonNull(decision);
                results.add(new BenchmarkRecord(j, benchmarkCase.getName(), timePreparation, timeRetrieve,
                        "AuthorizationSubscription", buildResponseStringForResult(result, decision)));

                log.debug("Total : {}ms", timeRetrieve);
            }

            // log.debug("destroy index");
            // documentIndex.destroyIndex();

        } catch (Exception e) {
            log.error("Error running test", e);
        }

        return results;
    }

    private void warmUp(ImmutableParsedDocumentIndex documentIndex) {
        EvaluationContext emptySubscriptionScopedEvaluationCtx = new EvaluationContext(ATTRIBUTE_CONTEXT,
                FUNCTION_CONTEXT, VARIABLES)
                .forAuthorizationSubscription(SubscriptionGenerator.createEmptySubscription());
        try {
            for (int i = 0; i < 10; i++) {
                documentIndex.retrievePolicies(emptySubscriptionScopedEvaluationCtx);
            }
        } catch (Exception ignored) {
            log.error("error during warm-up", ignored);
        }
    }

    private String buildResponseStringForResult(@NonNull PolicyRetrievalResult policyRetrievalResult,
                                                @NonNull AuthorizationDecision decision) {
        return String.format("PolicyRetrievalResult(decision=%s, matchingDocumentsCount=%d, errorsInTarget=%b)",
                decision.getDecision(), policyRetrievalResult.getMatchingDocuments().size(),
                policyRetrievalResult.isErrorsInTarget());
    }


    private double nanoToMs(long nanoseconds) {
        return nanoseconds / MILLION;
    }

}
