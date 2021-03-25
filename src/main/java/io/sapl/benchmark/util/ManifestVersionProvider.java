package io.sapl.benchmark.util;

import io.sapl.benchmark.index.IndexBenchmarkCommand;
import picocli.CommandLine.IVersionProvider;

public class ManifestVersionProvider implements IVersionProvider {
	public String[] getVersion() {
		var version = IndexBenchmarkCommand.class.getPackage().getImplementationVersion() == null
				? "Unknown, not running from JAR"
				: IndexBenchmarkCommand.class.getPackage().getImplementationVersion();
		return new String[] { version };
	}
}
