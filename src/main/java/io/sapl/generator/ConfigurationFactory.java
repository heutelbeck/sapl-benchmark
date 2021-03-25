package io.sapl.generator;

import io.sapl.generator.random.FullyRandomConfiguration;
import io.sapl.generator.structured.StructuredRandomConfiguration;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class ConfigurationFactory {

    public FullyRandomConfiguration fullyRandomDefault() {
        return new FullyRandomConfiguration();
    }

    public FullyRandomConfiguration parseFullyRandomConfigurationFile(String configurationFile) {
        return new FullyRandomConfiguration();
    }

    public StructuredRandomConfiguration structuredRandomDefault() {
        return new StructuredRandomConfiguration();
    }

    public StructuredRandomConfiguration parseStructuredRandomConfigurationFile(String configurationFile) {
        return new StructuredRandomConfiguration();
    }


}
