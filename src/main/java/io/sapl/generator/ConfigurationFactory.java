package io.sapl.generator;

import io.sapl.generator.random.FullyRandomConfiguration;
import io.sapl.generator.structured.StructuredRandomConfiguration;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class ConfigurationFactory {

    public List<GeneralConfiguration> parseConfigurationFile(String configurationFile, Path path){
        //TODO
        return Collections.emptyList();
    }

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
