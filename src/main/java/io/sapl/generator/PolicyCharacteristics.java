package io.sapl.generator;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PolicyCharacteristics {

    private int policyCount;

    private int logicalVariableCount;

    private int variablePoolCount;

    private double bracketProbability;

    private double conjunctionProbability;

    private double negationProbability;

    private double falseProbability;

}
