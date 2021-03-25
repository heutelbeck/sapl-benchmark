package io.sapl.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.sapl.api.pdp.AuthorizationSubscription;

import java.util.List;

public interface SubscriptionGenerator {


    List<AuthorizationSubscription> generateSubscriptions(int numberOfSubscriptions);

    static AuthorizationSubscription createEmptySubscription() {
        JsonNode emptyNode = JsonNodeFactory.instance.objectNode();
        return createSubscription(emptyNode, emptyNode, emptyNode);
    }

    static AuthorizationSubscription createSubscription(JsonNode subject, JsonNode action, JsonNode resource) {
        return new AuthorizationSubscription(subject, action, resource, JsonNodeFactory.instance.objectNode());
    }


}
