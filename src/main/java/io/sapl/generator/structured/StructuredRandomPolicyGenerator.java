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

import io.sapl.domain.model.DomainDataContainer;
import io.sapl.generator.PolicyGenerator;
import io.sapl.generator.PolicyUtil;
import io.sapl.domain.model.DomainActions;
import io.sapl.domain.model.DomainPolicy;
import io.sapl.domain.model.DomainPolicy.DomainPolicyObligation;
import io.sapl.domain.model.DomainResource;
import io.sapl.domain.model.DomainRole;
import io.sapl.domain.model.DomainSubject;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
@RequiredArgsConstructor
public class StructuredRandomPolicyGenerator implements PolicyGenerator {

    private static final String TAB_STRING = "\t\t";
    private final DomainDataContainer domainDataContainer;
    private final PolicyUtil policyUtil;

    @Override
    public void generatePolicies(Path policyFolder) throws Exception {
        generateDomainPolicies(policyFolder.toString());
    }

    public void generateDomainPoliciesWithSeed(long newSeed, Path policyPath) {
        policyUtil.reseedDice(newSeed);
        generateDomainPolicies(policyPath.toString());
    }


    public void generateDomainPolicies(String policyPath) {
        List<DomainPolicy> domainPolicies = generatePolicies();

        log.info("policies TOTAL: {}", domainPolicies.size());

        policyUtil.printDomainPoliciesLimited(domainPolicies);
        policyUtil.writeDomainPoliciesToFilesystem(domainPolicies, policyPath);
    }

    public StringBuilder generateEmptyPolicy(String policyName, boolean permit) {
        return new StringBuilder().append("policy \"").append(policyName).append("\"").append(System.lineSeparator())
                .append(permit ? "permit " : "deny ");
    }

    public StringBuilder generateGeneralBasePolicy(String policyName, Collection<DomainRole> roles) {
        StringBuilder policyBuilder = new StringBuilder().append("policy \"").append(policyName).append('\"')
                .append(System.lineSeparator()).append("permit ");

        addRolesToPolicy(policyBuilder, roles, true);

        return policyBuilder;
    }

    private StringBuilder generateGeneralBasePolicyWithActions(String policyName, Collection<String> actions,
                                                               Collection<DomainRole> roles) {
        StringBuilder policyBuilder = generateGeneralBasePolicy(policyName, roles);
        addActionsToPolicy(policyBuilder, actions);

        return policyBuilder;
    }

    public StringBuilder generateBasePolicy(String policyName, Iterable<DomainResource> resources) {
        StringBuilder policyBuilder = new StringBuilder().append("policy \"").append(policyName).append("\"")
                .append(System.lineSeparator()).append("permit ");

        boolean first = true;
        policyBuilder.append('(');
        for (DomainResource resource : resources) {
            if (first)
                first = false;
            else
                policyBuilder.append(" | ");
            policyBuilder.append(String.format("resource == \"%s\"", resource.getResourceName()));
        }
        policyBuilder.append(')');

        return policyBuilder;
    }

    private StringBuilder generateBasePolicyWithActions(String policyName, Collection<DomainResource> resources,
                                                        Collection<String> actions, Collection<DomainRole> roles) {
        StringBuilder policyBuilder = generateBasePolicy(policyName, resources);
        addRolesToPolicy(policyBuilder, roles, resources.isEmpty());
        addActionsToPolicy(policyBuilder, actions);

        return policyBuilder;
    }

    public void addRolesToPolicy(StringBuilder policyBuilder, Collection<DomainRole> roles, boolean emptyPermit) {
        if (roles.isEmpty())
            return;

        policyBuilder.append(System.lineSeparator()).append(TAB_STRING);
        if (!emptyPermit)
            policyBuilder.append(" & ");
        policyBuilder.append('(');

        boolean firstRole = true;
        for (DomainRole role : roles) {
            if (firstRole)
                firstRole = false;
            else
                policyBuilder.append(" | ");
            policyBuilder.append(String.format("(\"%s\" in subject.authorities)", role.getRoleName()));
        }
        policyBuilder.append(')');
    }

    public void addActionsToPolicy(StringBuilder policyBuilder, Collection<String> actions) {
        if (actions.isEmpty())
            return;

        policyBuilder.append(System.lineSeparator()).append(TAB_STRING).append(" & ").append('(');
        boolean firstAction = true;
        for (String action : actions) {
            if (firstAction)
                firstAction = false;
            else
                policyBuilder.append(" | ");
            policyBuilder.append(String.format("action == \"%s\"", action));
        }
        policyBuilder.append(')');
    }

    private void addObligationToPolicy(StringBuilder policyBuilder, DomainPolicyObligation obligation) {
        policyBuilder.append(System.lineSeparator()).append("obligation").append(System.lineSeparator())
                .append(TAB_STRING).append(obligation.getObligation());
    }

    public List<DomainPolicy> generatePolicies() {

        List<DomainRole> allRoles = List.copyOf(domainDataContainer.getDomainRoles());
        log.debug("allRolesCount:{}", allRoles.size());
        List<DomainResource> allResources = List.copyOf(domainDataContainer.getDomainResources());
        log.debug("allResources:{}", allResources.size());
        List<DomainSubject> allSubjects = List.copyOf(domainDataContainer.getDomainSubjects());
        log.debug("allSubjects:{}", allSubjects.size());

        List<DomainResource> unrestrictedResources = allResources.stream().filter(DomainResource::isUnrestricted)
                .collect(Collectors.toList());
        List<DomainResource> restrictedResources = new ArrayList<>(allResources);
        restrictedResources.removeAll(unrestrictedResources);
        log.debug("generated {} resources (unrestricted={})", allResources.size(), unrestrictedResources.size());

        int newPolicyCount = 0;
        List<DomainPolicy> allPolicies = new ArrayList<>(generateSubjectSpecificPolicies(allSubjects));
        newPolicyCount = allPolicies.size();
        log.debug("generated {} subject specific policies", newPolicyCount);

        allPolicies.addAll(generatePoliciesForGeneralAccessRoles(allRoles));
        newPolicyCount = allPolicies.size() - newPolicyCount;
        log.debug("generated {} policies for general access roles", newPolicyCount);

        allPolicies.addAll(generatePoliciesForUnrestrictedResources(unrestrictedResources));
        newPolicyCount = allPolicies.size() - newPolicyCount;
        log.debug("generated {} policies for unrestricted resources", newPolicyCount);

        allPolicies.addAll(generatePoliciesForRestrictedResources(restrictedResources, allRoles));
        newPolicyCount = allPolicies.size() - newPolicyCount;
        log.debug("generated {} policies for restricted resources", newPolicyCount);

        return allPolicies;
    }

    private List<DomainPolicy> generatePoliciesForRestrictedResources(Iterable<DomainResource> restrictedResources,
                                                                      Collection<DomainRole> allRoles) {
        List<DomainPolicy> policies = new ArrayList<>();

        List<DomainRole> rolesWithRestrictedAccess = allRoles.stream()
                .filter(role -> !role.isGeneralUnrestrictedAccess()).collect(Collectors.toList());

        for (DomainResource resource : restrictedResources) {
            collectAccessingRoles(rolesWithRestrictedAccess, resource);

            if (!resource.getFullAccessRoles().isEmpty())
                handleFullAccessRoles(policies, resource);

            if (!resource.getReadAccessRoles().isEmpty())
                handleReadAccessRoles(policies, resource);

            if (!resource.getCustomAccessRoles().isEmpty())
                handleCustomAccessRoles(policies, resource);

            resource.clearResourceAccessRoles();
        }

        return policies;
    }

    private void handleCustomAccessRoles(List<DomainPolicy> policies, DomainResource resource) {
        for (DomainRole customRole : resource.getCustomAccessRoles()) {
            String policyName = resource.getResourceName() + "_custom_" + customRole.getRoleName();

            if (resource.isExtensionRequired()) {
                policyName += "_extended";
            }
            StringBuilder policyBuilder = generateBasePolicyWithActions(policyName, Collections.singletonList(resource),
                    DomainActions.generateCustomActionList(domainDataContainer), Collections.singletonList(customRole));

            if (resource.isExtensionRequired()) {
                addObligationToPolicy(policyBuilder, PolicyUtil.LOG_OBLIGATION);
            }

            policies.add(new DomainPolicy(policyName, policyBuilder.toString(), policyName));
        }
    }

    private void handleReadAccessRoles(List<DomainPolicy> policies, DomainResource resource) {
        String policyName = resource.getResourceName() + "_read_roles";
        StringBuilder policyBuilder = generateBasePolicyWithActions(policyName, Collections.singletonList(resource),
                DomainActions.READ_ONLY.getActionList(), resource.getReadAccessRoles());

        List<DomainRole> extendedRoles = resource.getReadAccessRoles().stream().filter(DomainRole::isExtensionRequired)
                .collect(Collectors.toList());
        if (resource.isExtensionRequired()) {
            policyName += "_extended";
            addObligationToPolicy(policyBuilder, PolicyUtil.LOG_OBLIGATION);

            policies.add(new DomainPolicy(policyName, policyBuilder.toString(), policyName));
        } else if (!extendedRoles.isEmpty()) {
            for (DomainRole extendedRole : extendedRoles) {

                StringBuilder rolePolicyBuilder = new StringBuilder(policyBuilder);
                addObligationToPolicy(rolePolicyBuilder, PolicyUtil.LOG_OBLIGATION);

                String rolePolicyName = resource.getResourceName() + "_read_" + extendedRole.getRoleName()
                        + "_extended";

                policies.add(new DomainPolicy(rolePolicyName, rolePolicyBuilder.toString(), rolePolicyName));
            }
        }

    }

    private void handleFullAccessRoles(List<DomainPolicy> policies, DomainResource resource) {
        String policyName = resource.getResourceName() + "_unrestricted-roles";

        List<DomainRole> fullAccessRoles = resource.getFullAccessRoles();
        List<DomainRole> extendedFullAccessRoles = fullAccessRoles.stream().filter(DomainRole::isExtensionRequired)
                .collect(Collectors.toList());

        if (resource.isExtensionRequired()) {
            policyName += "_extended";
            StringBuilder extendedPolicyBuilder = generateBasePolicy(policyName, Collections.singletonList(resource));
            addRolesToPolicy(extendedPolicyBuilder, fullAccessRoles, false);
            addObligationToPolicy(extendedPolicyBuilder, PolicyUtil.LOG_OBLIGATION);
            policies.add(new DomainPolicy(policyName, extendedPolicyBuilder.toString(), policyName));
        } else {
            // handle extended roles
            if (!extendedFullAccessRoles.isEmpty()) {
                for (DomainRole extendedRole : extendedFullAccessRoles) {
                    String rolePolicyName = resource.getResourceName() + "_unrestricted_" + extendedRole.getRoleName();
                    StringBuilder rolePolicyBuilder = generateBasePolicy(rolePolicyName,
                            Collections.singletonList(resource));
                    addRolesToPolicy(rolePolicyBuilder, Collections.singletonList(extendedRole), false);
                    addObligationToPolicy(rolePolicyBuilder, PolicyUtil.LOG_OBLIGATION);

                    policies.add(new DomainPolicy(rolePolicyName, rolePolicyBuilder.toString(), rolePolicyName));
                }
                // prevent double handling of extended roles for resource
                fullAccessRoles.removeAll(extendedFullAccessRoles);
            }
            // handle roles without extension
            if (!fullAccessRoles.isEmpty()) {
                StringBuilder policyBuilder = generateBasePolicy(policyName, Collections.singletonList(resource));
                addRolesToPolicy(policyBuilder, fullAccessRoles, false);
                policies.add(new DomainPolicy(policyName, policyBuilder.toString(), policyName));
            }
        }
    }

    private void collectAccessingRoles(List<DomainRole> rolesWithRestrictedAccess, DomainResource resource) {
        for (DomainRole role : rolesWithRestrictedAccess) {
            boolean fullAccessOnResource = policyUtil
                    .rollIsLowerThanProbability(domainDataContainer.getStructuredRandomCase()
                            .getProbabilityFullAccessOnResource());
            if (fullAccessOnResource) {
                resource.getFullAccessRoles().add(role);
                continue;
            }
            boolean readAccessOnResource = policyUtil
                    .rollIsLowerThanProbability(domainDataContainer.getStructuredRandomCase()
                            .getProbabilityReadAccessOnResource());
            if (readAccessOnResource) {
                resource.getReadAccessRoles().add(role);
                continue;
            }
            resource.getCustomAccessRoles().add(role);
        }
    }

    private List<DomainPolicy> generatePoliciesForUnrestrictedResources(List<DomainResource> unrestrictedResources) {
        if (unrestrictedResources.isEmpty())
            return Collections.emptyList();

        List<DomainPolicy> policies = new ArrayList<>();

        policies.add(new DomainPolicy("unrestricted-resources",
                generateBasePolicy("unrestricted-resources", unrestrictedResources).toString(),
                "unrestricted-resources"));

        return policies;
    }

    private List<DomainPolicy> generatePoliciesForGeneralAccessRoles(Collection<DomainRole> allRoles) {
        List<DomainPolicy> policies = new ArrayList<>();

        List<DomainRole> unrestrictedRoles = allRoles.stream().filter(DomainRole::isGeneralUnrestrictedAccess)
                .collect(Collectors.toList());
        List<DomainRole> unrestrictedExtensionRoles = unrestrictedRoles.stream().filter(DomainRole::isExtensionRequired)
                .collect(Collectors.toList());
        unrestrictedRoles.removeAll(unrestrictedExtensionRoles);

        if (!unrestrictedRoles.isEmpty())
            policies.add(new DomainPolicy("general unrestricted roles",
                    generateGeneralBasePolicy("general unrestricted roles", unrestrictedRoles).toString(),
                    "general_unrestricted_roles"));
        // TODO extendedRoles

        List<DomainRole> readRoles = allRoles.stream().filter(DomainRole::isGeneralReadAccess)
                .collect(Collectors.toList());
        List<DomainRole> readExtensionRoles = readRoles.stream().filter(DomainRole::isExtensionRequired)
                .collect(Collectors.toList());
        readRoles.removeAll(readExtensionRoles);

        List<DomainRole> customRoles = allRoles.stream().filter(DomainRole::isGeneralCustomAccess)
                .collect(Collectors.toList());
        List<DomainRole> customExtensionRoles = customRoles.stream().filter(DomainRole::isExtensionRequired)
                .collect(Collectors.toList());
        customRoles.removeAll(customExtensionRoles);

        if (!customRoles.isEmpty())
            policies.addAll(customRoles.stream().map(customRole -> new DomainPolicy(
                    "general_custom_" + customRole.getRoleName(),
                    generateGeneralBasePolicyWithActions("general_custom_" + customRole.getRoleName(),
                            DomainActions.generateCustomActionList(domainDataContainer), Collections
                                    .singletonList(customRole))
                            .toString(),
                    "general_custom_role_" + customRole.getRoleName())).collect(Collectors.toList()));

        return policies;
    }

    private List<DomainPolicy> generateSubjectSpecificPolicies(Iterable<DomainSubject> allSubjects) {
        List<DomainPolicy> policies = new ArrayList<>();

        for (DomainSubject subject : allSubjects) {
            String subjectName = subject.getSubjectName();
            for (int i = 0; i < domainDataContainer.getStructuredRandomCase().getNumberOfRolesPerSubject(); i++) {
                String policyName = String.format("policy %d for %s", i, subjectName);

                StringBuilder policyBuilder = generateEmptyPolicy(policyName, true);
                policyBuilder.append(String.format("(resource == \"%s\")", subjectName)).append(System.lineSeparator())
                        .append(TAB_STRING).append(" & ")
                        .append(String.format("(\"%s\" == subject.name)", subjectName));

                policies.add(new DomainPolicy(policyName, policyBuilder.toString(), subjectName));
            }
        }

        return policies;
    }


}
