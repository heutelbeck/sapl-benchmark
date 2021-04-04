package io.sapl.generator.random;

import io.sapl.benchmark.BenchmarkCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullyRandomCase extends BenchmarkCase {

    //    String name;

    //    long seed;

    int policyCount;

    int logicalVariableCount;

    int variablePoolCount;

    double bracketProbability;

    double conjunctionProbability;

    double negationProbability;

    double falseProbability;

}
