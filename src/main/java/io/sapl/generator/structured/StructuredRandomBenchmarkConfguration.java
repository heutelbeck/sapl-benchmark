package io.sapl.generator.structured;

import io.sapl.generator.BenchmarkConfiguration;
import io.sapl.generator.structured.StructuredRandomCase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StructuredRandomBenchmarkConfguration extends BenchmarkConfiguration<StructuredRandomCase> {

    private String benchmarkIdentifier;

    private List<StructuredRandomCase> cases;
}
