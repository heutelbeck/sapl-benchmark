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
package io.sapl.domain.model;

import io.sapl.generator.PolicyUtil;
import io.sapl.generator.structured.StructuredRandomCase;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class DomainDataContainer {

    @Getter
    private final StructuredRandomCase structuredRandomCase;

    @Getter
    private final PolicyUtil policyUtil;

    @Getter
    private final int numberOfGeneratedSubscriptions;

    private List<DomainRole> domainRoles = new LinkedList<>();
    private List<DomainResource> domainResources = new LinkedList<>();
    private List<DomainSubject> domainSubjects = new LinkedList<>();
    private List<String> domainActions = new LinkedList<>();

    public DomainDataContainer(StructuredRandomCase structuredRandomCase, PolicyUtil policyUtil) {
        this.structuredRandomCase = structuredRandomCase;
        // there should be more subscriptions than executions of the benchmark to avoid
        // a subscription being used twice
        this.numberOfGeneratedSubscriptions = structuredRandomCase.getNumberOfRuns()
                * this.structuredRandomCase.getSubscriptionGenerationFactor() + 100;
        this.policyUtil = policyUtil;
        generate();
    }


    private void generate() {
        this.domainRoles = generateRoles();
        this.domainResources = generateResources();
        this.domainSubjects = generateSubjects(this.domainRoles);
        this.domainActions = DomainActions.generateActionListByCount(structuredRandomCase.getNumberOfActions());

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
        List<DomainRole> roles = new ArrayList<>(structuredRandomCase.getNumberOfGeneralRoles());

        for (int i = 0; i < structuredRandomCase.getNumberOfGeneralRoles(); i++) {
            roles.add(new DomainRole(String.format("role.%03d", PolicyUtil.getNextRoleCount()),
                    policyUtil.rollIsLowerThanProbability(structuredRandomCase.getProbabilityOfGeneralFullAccessRole()),
                    policyUtil.rollIsLowerThanProbability(structuredRandomCase.getProbabilityOfGeneralReadAccessRole()),
                    policyUtil.rollIsLowerThanProbability(structuredRandomCase.getProbabilityOfGeneralCustomAccessRole()),
                    policyUtil.rollIsLowerThanProbability(structuredRandomCase.getProbabilityOfExtendedRole())));
        }
        return roles;
    }

    private List<DomainResource> generateResources() {
        List<DomainResource> resources = new ArrayList<>(structuredRandomCase.getNumberOfGeneralResources());

        for (int i = 0; i < structuredRandomCase.getNumberOfGeneralResources(); i++) {
            resources.add(new DomainResource(String.format("resource.%03d", PolicyUtil.getNextResourceCount()),
                    policyUtil.rollIsLowerThanProbability(structuredRandomCase.getProbabilityOfUnrestrictedResource()),
                    policyUtil.rollIsLowerThanProbability(structuredRandomCase.getProbabilityOfExtendedRole())));
        }
        return resources;
    }

    private List<DomainSubject> generateSubjects(List<DomainRole> allRoles) {
        List<DomainSubject> subjects = new ArrayList<>(structuredRandomCase.getNumberOfSubjects());

        for (int i = 0; i < structuredRandomCase.getNumberOfSubjects(); i++) {
            DomainSubject domainSubject = new DomainSubject(String.format("subject.%03d", i));

            // assign subject random roles
            for (int j = 0; j < policyUtil.getDice().nextInt(structuredRandomCase.getLimitOfSubjectRoles()) + 1; j++) {
                DomainRole randomRole = policyUtil.getRandomElement(allRoles);
                domainSubject.getSubjectAuthorities().add(randomRole.getRoleName());
            }
            subjects.add(domainSubject);
        }
        return subjects;
    }

}
