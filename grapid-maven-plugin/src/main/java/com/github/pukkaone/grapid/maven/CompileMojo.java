package com.github.pukkaone.grapid.maven;

import com.pukkaone.grapid.compiler.VersionsTranslator;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

/**
 * Translates GraphQL schema files to Java source files.
 */
@Mojo(
    name = "compile",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    threadSafe = true)
public class CompileMojo extends AbstractMojo {

  /**
   * If true, then do not execute.
   */
  @Parameter(property = "grapid.skip", defaultValue = "false")
  private boolean skip;

  /**
   * Directory containing API version subdirectories, which contain GraphQL schema files.
   */
  @Parameter(defaultValue = "${project.basedir}/src/main/resources/graphql")
  private File sourceDirectory;

  /**
   * Ant-style patterns to match GraphQL schema file names under the source directory.
   */
  @Parameter(defaultValue = "**/*.graphql")
  private List<String> includes;

  /**
   * Directory where Java source files will be written.
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/grapid")
  private File outputDirectory;

  /**
   * Generate Java package hierarchy under this parent Java package.
   */
  @Parameter(required = true)
  private String packagePrefix;

  @Parameter(property = "project", readonly = true, required = true)
  private MavenProject project;

  private VersionsTranslator versionsTranslator;

  private String[] findSchemaFiles(Path versionDirectory) {
    var files = new FileSet();
    files.setDirectory(versionDirectory.toString());
    files.setFollowSymlinks(false);
    for (var include : includes) {
      files.addInclude(include);
    }

    var fileSetManager = new FileSetManager();
    return fileSetManager.getIncludedFiles(files);
  }

  private String translateVersionSchemaFiles(Path versionDirectory) {
    String[] schemaFileNames = findSchemaFiles(versionDirectory);
    getLog().info("From directory " + versionDirectory);
    getLog().info("Compiling schema files " + Arrays.asList(schemaFileNames));

    var schemaFiles = Stream.of(schemaFileNames)
        .map(versionDirectory::resolve)
        .collect(Collectors.toList());

    return versionsTranslator.translateVersion(versionDirectory, schemaFiles);
  }

  @Override
  public void execute() {
    versionsTranslator = new VersionsTranslator(
        packagePrefix,
        outputDirectory.toPath(),
        message -> getLog().warn(message));

    versionsTranslator.findVersionDirectories(sourceDirectory.toPath())
        .forEach(this::translateVersionSchemaFiles);

    versionsTranslator.translateVersions();

    project.addCompileSourceRoot(outputDirectory.getPath());
  }
}
