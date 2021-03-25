package io.sapl.generator;

import lombok.Data;

import java.nio.file.Path;

@Data
public class GeneralConfiguration {

    protected String name;

    protected long seed;

    protected Path policyFolderPath;

}
