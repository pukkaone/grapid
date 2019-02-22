package com.pukkaone.grapid.compiler;

import com.github.pukkaone.grapid.core.Version;
import com.github.pukkaone.grapid.core.VersionComparator;
import com.github.pukkaone.grapid.core.VersionFactory;
import com.github.pukkaone.grapid.core.apichange.VersionChanges;
import com.github.pukkaone.grapid.core.apichange.VersionHistory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import lombok.RequiredArgsConstructor;

/**
 * Translates API versions to Java source files.
 */
@RequiredArgsConstructor
public class VersionsTranslator {

  private final String packagePrefix;
  private final Path outputDirectory;
  private final Consumer<String> logger;

  private List<Path> versionDirectories;
  private String lastVersion;
  private SchemaTranslator previousVersionSchema;

  private static String extractVersion(Path directory) {
    return directory.getFileName().toString();
  }

  private boolean isVersion(Path directory) {
    var version = extractVersion(directory);
    var include = SourceVersion.isName(version);
    if (!include) {
      logger.accept(
          "Ignoring version [" + version + "] because it is not a valid Java identifier");
    }

    return include;
  }

  /**
   * Finds version directories.
   *
   * @param sourceDirectory
   *     directory containing version subdirectories
   * @return version directories
   */
  public List<Path> findVersionDirectories(Path sourceDirectory) {
    try {
      versionDirectories = Files.list(sourceDirectory)
          .filter(path -> Files.isDirectory(path))
          .filter(this::isVersion)
          .sorted(Comparator.comparing(VersionsTranslator::extractVersion, new VersionComparator()))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new IllegalStateException("Cannot find in directory " + sourceDirectory, e);
    }

    lastVersion = extractVersion(versionDirectories.get(versionDirectories.size() - 1));
    return versionDirectories;
  }

  /**
   * Translates GraphQL schema for an API version.
   *
   * @param versionDirectory
   *     version directory
   * @param schemaFiles
   *     GraphQL schema files
   * @return API version
   */
  public String translateVersion(Path versionDirectory, Collection<Path> schemaFiles) {
    var version = extractVersion(versionDirectory);
    var schemaTranslator = new SchemaTranslator(
        packagePrefix, version, version.equals(lastVersion), outputDirectory);
    schemaTranslator.translateSchemaFiles(schemaFiles, previousVersionSchema);
    previousVersionSchema = schemaTranslator;
    return version;
  }

  private List<CodeBlock> generateEntries(Collection<String> versions) {
    return versions.stream()
        .map(version -> CodeBlock.of("Map.entry($S, $L)", version, version))
        .collect(Collectors.toList());
  }

  private void writeJavaFile(TypeSpec.Builder classBuilder) {
    var javaFile = JavaFile.builder(packagePrefix, classBuilder.build())
        .build();
    try {
      javaFile.writeTo(outputDirectory);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot write file", e);
    }
  }

  private void generateVersions() {
    var className = ClassName.get(packagePrefix, "Versions");
    var classBuilder = TypeSpec.classBuilder(className)
        .addAnnotation(CodeGeneratorUtils.GENERATED)
        .addAnnotation(CodeGeneratorUtils.generateNamedAnnotation(className))
        .addAnnotation(CodeGeneratorUtils.SINGLETON)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(VersionFactory.class);

    var sortedVersions = versionDirectories.stream()
        .map(VersionsTranslator::extractVersion)
        .collect(Collectors.toList());

    int ordinal = 0;
    for (var version : sortedVersions) {
      classBuilder.addField(FieldSpec.builder(
          Version.class, version, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
          .initializer("new $T($S, $L)", Version.class, version, ordinal++)
          .build());
    }

    classBuilder.addField(FieldSpec.builder(
        ParameterizedTypeName.get(Map.class, String.class, Version.class),
        "stringToVersionMap",
        Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer(CodeBlock.builder()
            .add("Map.ofEntries(\n")
            .add("$>$>")
            .add(CodeBlock.join(generateEntries(sortedVersions), ",\n"))
            .add("$<$<")
            .add(")")
            .build())
        .build());

    classBuilder.addMethod(MethodSpec.methodBuilder("getVersion")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(Version.class)
        .addParameter(String.class, "input")
        .addStatement("return stringToVersionMap.get(input)")
        .build());

    writeJavaFile(classBuilder);
  }

  private void generateVersionHistoryTie() {
    var className = ClassName.get(packagePrefix, "VersionHistoryTie");
    var classBuilder = TypeSpec.classBuilder(className)
        .addAnnotation(CodeGeneratorUtils.GENERATED)
        .addAnnotation(CodeGeneratorUtils.generateNamedAnnotation(className))
        .addAnnotation(CodeGeneratorUtils.SINGLETON)
        .addModifiers(Modifier.PUBLIC)
        .superclass(VersionHistory.class)
        .addMethod(MethodSpec.constructorBuilder()
            .addAnnotation(CodeGeneratorUtils.INJECT)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(
                ParameterizedTypeName.get(Collection.class, VersionChanges.class),
                "versionChanges")
            .addStatement("super(versionChanges)")
            .build());

    writeJavaFile(classBuilder);
  }

  /**
   * Translates API versions to Java source files.
   */
  public void translateVersions() {
    generateVersions();
    generateVersionHistoryTie();
  }
}
