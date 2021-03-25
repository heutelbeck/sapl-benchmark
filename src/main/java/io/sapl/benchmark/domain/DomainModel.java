package io.sapl.benchmark.domain;

import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.api.pdp.AuthorizationSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
public class DomainModel {

    private final Random random;
    private final List<JsonNode> subjects;
    private final List<JsonNode> actions;
    private final List<JsonNode> resources;


    public JsonNode getRandomSubject() {
        return null;
    }

    public JsonNode getRandomAction() {
        return null;
    }

    public JsonNode getRandomResource() {
        return null;
    }

    public AuthorizationSubscription getRandomSubscription() {
        return AuthorizationSubscription.of(getRandomSubject(), getRandomAction(), getRandomResource());
    }

}
