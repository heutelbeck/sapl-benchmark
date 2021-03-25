package io.sapl.generator.random;

import io.sapl.benchmark.PolicyCharacteristics;
import lombok.Data;

@Data
public class FullyRandomConfiguration {

    private String name = "";

    private long seed = 0L;

    private PolicyCharacteristics policyCharacteristics;

    //    public void updateName() {
    //        this.name = String.format("\"%dp, %dv, %dvp\"", policyCharacteristics.policyCount,
    //                policyCharacteristics.logicalVariableCount, policyCharacteristics.variablePoolCount);
    //    }
}
