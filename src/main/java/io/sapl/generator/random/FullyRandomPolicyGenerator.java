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
package io.sapl.generator.random;

import io.sapl.generator.PolicyGenerator;
import io.sapl.generator.PolicyUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Data
@Slf4j
@RequiredArgsConstructor
public class FullyRandomPolicyGenerator implements PolicyGenerator {

    private static final int DEFAULT_BUFFER = 50;

    private final FullyRandomCase testcase;
    private final PolicyUtil policyUtil;

    @Override
    public void generatePolicies(Path folder) throws FileNotFoundException, UnsupportedEncodingException {
        log.info("Generate {} polices in folder: {}", testcase.getPolicyCount(), folder);
        for (int i = 0; i < testcase.getPolicyCount(); i++) {
            String name = "p_" + i;
            var filename = folder + File.pathSeparator + name + ".sapl";
            log.trace("write policy to: {}", filename);
            var policy = generatePolicyString(name);
            log.trace("policy: {}", policy);
            try (PrintWriter writer = new PrintWriter(filename, StandardCharsets.UTF_8.name())) {
                writer.println(policy);
            }
        }

    }

    private String generatePolicyString(String name) {
        final int numberOfVariables = testcase.getLogicalVariableCount();
        final int numberOfConnectors = numberOfVariables - 1;
        final int poolSize = testcase.getVariablePoolCount();

        final double negationChance = testcase.getNegationProbability();
        final double bracketChance = testcase.getBracketProbability();
        final double conjunctionChance = testcase.getConjunctionProbability();

        StringBuilder statement = new StringBuilder(DEFAULT_BUFFER).append("policy \"").append(name).append("\"")
                .append(System.lineSeparator()).append("permit ");

        int open = 0;
        for (int j = 0; j < numberOfVariables; ++j) {
            if (policyUtil.roll() <= negationChance) {
                statement.append('!');
            }
            while (policyUtil.roll() <= bracketChance) {
                statement.append('(');
                ++open;
            }
            statement.append(getIdentifier(policyUtil.roll(poolSize)));
            double chance = 1.0 / (numberOfVariables - j);
            while (open > 0 && policyUtil.roll() < chance) {
                statement.append(')');
                --open;
            }
            if (j < numberOfConnectors) {
                if (policyUtil.roll() <= conjunctionChance) {
                    statement.append(" & ");
                } else {
                    statement.append(" | ");
                }
            }
        }

        return statement.toString();
    }


    private static String getIdentifier(int index) {
        return "resource.x" + index;
    }


}
