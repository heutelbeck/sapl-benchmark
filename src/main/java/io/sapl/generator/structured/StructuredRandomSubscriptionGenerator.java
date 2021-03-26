package io.sapl.generator.structured;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.domain.model.DomainDataContainer;
import io.sapl.generator.SubscriptionGenerator;
import io.sapl.domain.model.DomainSubject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class StructuredRandomSubscriptionGenerator implements SubscriptionGenerator {

    private static final JsonNode EMPTY_NODE = JsonNodeFactory.instance.objectNode();

    private final DomainDataContainer domainDataContainer;
    private final double emptySubNodeProbability;
    private final double emptySubProbability;

    @Override
    public List<AuthorizationSubscription> generateSubscriptions(int numberOfSubscriptions) {
        List<AuthorizationSubscription> subscriptions = new LinkedList<>();
        for (int i = 0; i < numberOfSubscriptions; i++) {
            AuthorizationSubscription sub = createStructuredRandomSubscription();

            subscriptions.add(sub);
            log.trace("generated sub: {}", sub);
        }
        return subscriptions;
    }

    public AuthorizationSubscription createStructuredRandomSubscription() {
        double roll = roll();
        if (roll >= emptySubProbability) {
            log.trace("dice rolled {} - higher than {} -> EMPTY SUB", roll, emptySubProbability);
            return SubscriptionGenerator.createEmptySubscription();
        }
        log.trace("dice rolled {} - lower than {}", roll, emptySubProbability);
        return SubscriptionGenerator.createSubscription(getRandomSub(), getRandomAction(), getRandomResource());
    }


    private double roll() {
        return domainDataContainer.getPolicyUtil().roll();
    }

    private int roll(int supremum) {
        return domainDataContainer.getPolicyUtil().roll(supremum);
    }

    public <T> T getRandomElement(List<T> list) {
        return list.get(roll(list.size()));
    }

    private JsonNode getRandomResource() {
        double roll = roll();
        if (roll >= emptySubNodeProbability) {
            log.trace("dice rolled {} - higher than {} -> EMPTY RESOURCE", roll, emptySubNodeProbability);
            return EMPTY_NODE;
        }
        log.trace("dice rolled {} - lower than {}", roll, emptySubNodeProbability);
        return JsonNodeFactory.instance
                .textNode(getRandomElement(domainDataContainer.getDomainResources()).getResourceName());
    }

    private JsonNode getRandomAction() {
        double roll = roll();
        if (roll >= emptySubNodeProbability) {
            log.trace("dice rolled {} - higher than {} -> EMPTY ACTION", roll, emptySubNodeProbability);
            return EMPTY_NODE;
        }
        log.trace("dice rolled {} - lower than {}", roll, emptySubNodeProbability);
        return JsonNodeFactory.instance.textNode(getRandomElement(domainDataContainer.getDomainActions()));
    }

    private JsonNode getRandomSub() {
        double roll = roll();
        if (roll >= emptySubNodeProbability) {
            log.trace("dice rolled {} - higher than {} -> EMPTY SUBJECT", roll, emptySubNodeProbability);
            return EMPTY_NODE;
        }
        log.trace("dice rolled {} - lower than {}", roll, emptySubNodeProbability);
        DomainSubject domainSubject = getRandomElement(domainDataContainer.getDomainSubjects());
        return buildSubjectNode(domainSubject);
    }

    private JsonNode buildSubjectNode(DomainSubject domainSubject) {
        ObjectNode subject = JsonNodeFactory.instance.objectNode();
        ArrayNode authorityNode = JsonNodeFactory.instance.arrayNode();
        for (String subjectAuthority : domainSubject.getSubjectAuthorities()) {
            authorityNode.add(subjectAuthority);
        }
        subject.set("authorities", authorityNode);
        subject.put("name", domainSubject.getSubjectName());

        return subject;
    }


}
