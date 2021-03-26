package io.sapl.generator;

import io.sapl.benchmark.BenchmarkCase;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public abstract class BenchmarkConfiguration<T extends BenchmarkCase> {

    public abstract List<T> getCases();
}
