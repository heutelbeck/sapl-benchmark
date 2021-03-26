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
package io.sapl.generator;

import io.sapl.domain.model.DomainPolicy;
import io.sapl.domain.model.DomainPolicy.DomainPolicyBody;
import io.sapl.domain.model.DomainPolicy.DomainPolicyObligation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class PolicyUtil {

    private static final AtomicInteger policyCounter = new AtomicInteger();
    private static final AtomicInteger roleCounter = new AtomicInteger();
    private static final AtomicInteger resourceCounter = new AtomicInteger();
    private static final AtomicInteger appendixCounter = new AtomicInteger();

    private final boolean cleanDirectory;

    public static final DomainPolicyObligation LOG_OBLIGATION = new DomainPolicyObligation("\"logging:log_access\"");
    public static final DomainPolicyBody TREATING_BODY = new DomainPolicyBody(
            "subject in resource.<patient.treating>;");
    public static final DomainPolicyBody RELATIVE_BODY = new DomainPolicyBody(
            "subject in resource.<patient.relatives>;");
    public static final DomainPolicyBody OWN_DATA_BODY = new DomainPolicyBody("subject.id == resource.patient;");

    @Getter
    private Random dice;

    public PolicyUtil(boolean cleanDirectory, long seed) {
        this.cleanDirectory = cleanDirectory;
        this.dice = new Random(seed);
    }

    public void writeDomainPoliciesToFilesystem(List<DomainPolicy> domainPolicies, String policyPath) {
        log.info("writing policies to folder: {}", policyPath);

        File policyDir = new File(policyPath);
        boolean directoryCreated = policyDir.mkdir();
        if (!directoryCreated)
            throw new RuntimeException("policy directory could not be created");

        logDirectorySize("before clean fileCount:{}", policyDir);
        if (cleanDirectory)
            cleanPolicyDirectory(policyPath);

        logDirectorySize("after clean fileCount:{}", policyDir);

        for (DomainPolicy domainPolicy : domainPolicies) {
            writePolicyToFile(domainPolicy, policyPath);
        }

        logDirectorySize("after write policy fileCount:{}", policyDir);
    }

    private void logDirectorySize(String message, File policyDir) {
        var files = policyDir.listFiles();
        log.debug(message, files == null ? "ERROR" : files.length);
    }

    public void cleanPolicyDirectory(String policyPath) {
        log.info("removing existing policies in output directory");
        try {
            FileUtils.cleanDirectory(new File(policyPath));
        } catch (IOException e) {
            log.error("error while cleaning the directory", e);
        }
    }

    public void printDomainPoliciesLimited(List<DomainPolicy> domainPolicies) {
        log.trace("#################### POLICIES ####################");
        for (DomainPolicy domainPolicy : domainPolicies) {
            log.trace(
                    "{}--------------------------------------------------{}{}{}--------------------------------------------------",
                    System.lineSeparator(), System.lineSeparator(), domainPolicy.getPolicyContent(),
                    System.lineSeparator());
        }
    }

    public void writePolicyToFile(DomainPolicy policy, String policyPath) {
        String policyFileName = String.format("%s/%03d_%s.sapl", policyPath, PolicyUtil.getNextPolicyCount(),
                policy.getFileName());
        log.trace("writing policy file: {}", policyFileName);

        try (PrintWriter writer = new PrintWriter(policyFileName, StandardCharsets.UTF_8.name())) {
            writer.println(policy.getPolicyContent());
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            log.error("writing policy file failed", e);
        }
    }

    public static int getNextPolicyCount() {
        return policyCounter.getAndIncrement();
    }

    public static int getNextRoleCount() {
        return roleCounter.getAndIncrement();
    }

    public static int getNextResourceCount() {
        return resourceCounter.getAndIncrement();
    }

    public static int getNextAppendixCount() {
        return appendixCounter.getAndIncrement();
    }

    public static String sanitizeFileName(String fileName) {
        return fileName.toLowerCase().replaceAll("\\.", "-").replaceAll("[\\[\\]]", "").replace(", ", "-");
    }

    public static String getIOrDefault(List<String> list, int i, String defaultStr) {
        try {
            return list.get(i);
        } catch (Exception e) {
            return defaultStr;
        }
    }

    public void reseedDice(long seed) {
        this.dice = new Random(seed);
    }

    public double roll() {
        return dice.nextDouble();
    }

    public int roll(int supremum) {
        return dice.nextInt(supremum);
    }

    public  <T> T getRandomElement(List<T> list) {
        return list.get(dice.nextInt(list.size()));
    }

    public boolean rollIsLowerThanProbability(double probability) {
        return roll() < probability;
    }

}
