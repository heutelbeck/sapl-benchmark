package io.sapl.generator.random;

import io.sapl.generator.BenchmarkConfiguration;
import io.sapl.generator.random.FullyRandomCase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FullyRandomBenchmarkConfguration extends BenchmarkConfiguration<FullyRandomCase> {

    private String benchmarkIdentifier;

    private List<FullyRandomCase> cases;

}
