package io.sapl.generator;

import io.sapl.api.pdp.AuthorizationSubscription;

import java.util.List;

public interface SubscriptionGenerator {


    List<AuthorizationSubscription> generateSubscriptions(int numberOfSubscriptions);

}
