package io.sapl.generator.random;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.generator.SubscriptionGenerator;
import io.sapl.generator.structured.DomainDataContainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FullyRandomSubscriptionGenerator implements SubscriptionGenerator {

    private final DomainDataContainer domainDataContainer;
    private final FullyRandomConfiguration configuration;

    @Override
    public List<AuthorizationSubscription> generateSubscriptions(int numberOfSubscriptions) {
        List<AuthorizationSubscription> subscriptions = new LinkedList<>();
        for (int i = 0; i < numberOfSubscriptions; i++) {
            AuthorizationSubscription sub = createFullyRandomSubscription();

            subscriptions.add(sub);
            log.trace("generated sub: {}", sub);
        }
        return subscriptions;
    }


    public AuthorizationSubscription createFullyRandomSubscription() {
        ObjectNode resource = JsonNodeFactory.instance.objectNode();
        for (String var : getVariables()) {
            resource = resource.put(var, (roll() >= configuration.getPolicyCharacteristics().getFalseProbability()));
        }
        return new AuthorizationSubscription(NullNode.getInstance(), NullNode.getInstance(), resource,
                NullNode.getInstance());
    }

    public Collection<String> getVariables() {
        HashSet<String> variables = new HashSet<>();
        for (int i = 0; i < configuration.getPolicyCharacteristics().getVariablePoolCount(); i++) {
            variables.add("x" + i);
        }
        return variables;
    }

    private double roll() {
        return domainDataContainer.getPolicyUtil().roll();
    }


}
