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
package io.sapl.generator.structured;

import io.sapl.generator.PolicyUtil;
import io.sapl.generator.model.DomainActions;
import io.sapl.generator.model.DomainResource;
import io.sapl.generator.model.DomainRole;
import io.sapl.generator.model.DomainSubject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class DomainDataContainer {

    @Getter
    private final StructuredRandomConfiguration configuration;

    @Getter
    private final PolicyUtil policyUtil;

    private final int numberOfGeneratedSubscriptions;

    private List<DomainRole> domainRoles = new LinkedList<>();
    private List<DomainResource> domainResources = new LinkedList<>();
    private List<DomainSubject> domainSubjects = new LinkedList<>();
    private List<String> domainActions = new LinkedList<>();

    public DomainDataContainer(StructuredRandomConfiguration configuration, PolicyUtil policyUtil) {
        this.configuration = configuration;
        // there should be more subscriptions than executions of the benchmark to avoid
        // a subscription being used twice
        this.numberOfGeneratedSubscriptions = configuration.getNumberOfBenchmarkRuns()
                * configuration.getSubscriptionGenerationFactor() + 100;
        this.policyUtil = policyUtil;
        generate();
    }


    private void generate() {
        this.domainRoles = generateRoles();
        this.domainResources = generateResources();
        this.domainSubjects = generateSubjects(this.domainRoles);
        this.domainActions = DomainActions.generateActionListByCount(configuration.getNumberOfActions());

        if (domainRoles.isEmpty())
            throw new RuntimeException("no roles were generated");
        if (domainResources.isEmpty())
            throw new RuntimeException("no resources were generated");
        if (domainSubjects.isEmpty())
            throw new RuntimeException("no subjects were generated");
        if (domainActions.isEmpty())
            throw new RuntimeException("no actions were generated");

        log.debug("generated {} roles", this.domainRoles.size());
        log.debug("generated {} resources", this.domainResources.size());
        log.debug("generated {} subjects", this.domainSubjects.size());
        log.debug("generated {} actions", this.domainActions.size());
    }

    public List<DomainRole> getDomainRoles() {
        return List.copyOf(domainRoles);
    }

    public List<DomainResource> getDomainResources() {
        return List.copyOf(domainResources);
    }

    public List<DomainSubject> getDomainSubjects() {
        return List.copyOf(domainSubjects);
    }

    public List<String> getDomainActions() {
        return List.copyOf(domainActions);
    }


    private List<DomainRole> generateRoles() {
        List<DomainRole> roles = new ArrayList<>(configuration.getNumberOfGeneralRoles());

        for (int i = 0; i < configuration.getNumberOfGeneralRoles(); i++) {
            roles.add(new DomainRole(String.format("role.%03d", PolicyUtil.getNextRoleCount()),
                    policyUtil.rollIsLowerThanProbability(configuration.getProbabilityOfGeneralFullAccessRole()),
                    policyUtil.rollIsLowerThanProbability(configuration.getProbabilityOfGeneralReadAccessRole()),
                    policyUtil.rollIsLowerThanProbability(configuration.getProbabilityOfGeneralCustomAccessRole()),
                    policyUtil.rollIsLowerThanProbability(configuration.getProbabilityOfExtendedRole())));
        }
        return roles;
    }

    private List<DomainResource> generateResources() {
        List<DomainResource> resources = new ArrayList<>(configuration.getNumberOfGeneralResources());

        for (int i = 0; i < configuration.getNumberOfGeneralResources(); i++) {
            resources.add(new DomainResource(String.format("resource.%03d", PolicyUtil.getNextResourceCount()),
                    policyUtil.rollIsLowerThanProbability(configuration.getProbabilityOfUnrestrictedResource()),
                    policyUtil.rollIsLowerThanProbability(configuration.getProbabilityOfExtendedRole())));
        }
        return resources;
    }

    private List<DomainSubject> generateSubjects(List<DomainRole> allRoles) {
        List<DomainSubject> subjects = new ArrayList<>(configuration.getNumberOfSubjects());

        for (int i = 0; i < configuration.getNumberOfSubjects(); i++) {
            DomainSubject domainSubject = new DomainSubject(String.format("subject.%03d", i));

            // assign subject random roles
            for (int j = 0; j < policyUtil.getDice().nextInt(configuration.getLimitOfSubjectRoles()) + 1; j++) {
                DomainRole randomRole = policyUtil.getRandomElement(allRoles);
                domainSubject.getSubjectAuthorities().add(randomRole.getRoleName());
            }
            subjects.add(domainSubject);
        }
        return subjects;
    }

}
