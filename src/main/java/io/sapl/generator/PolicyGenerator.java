package io.sapl.generator;

import java.nio.file.Path;

public interface PolicyGenerator {

    void generatePolicies(Path policyFolder) throws Exception;


}
