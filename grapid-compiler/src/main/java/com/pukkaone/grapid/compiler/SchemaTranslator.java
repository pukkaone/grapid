package com.pukkaone.grapid.compiler;

import com.github.pukkaone.grapid.core.ArgumentDirective;
import com.github.pukkaone.grapid.core.DataFetcherUtils;
import com.github.pukkaone.grapid.core.GraphQLObject;
import com.github.pukkaone.grapid.core.GraphQLObjectDataFetcher;
import com.github.pukkaone.grapid.core.TieDirective;
import com.github.pukkaone.grapid.core.Version;
import com.github.pukkaone.grapid.core.VersionExecutor;
import com.github.pukkaone.grapid.core.apichange.ChangeDescription;
import com.github.pukkaone.grapid.core.apichange.VersionChanges;
import com.github.pukkaone.grapid.core.apichange.VersionHistory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import graphql.GraphQLException;
import graphql.language.ArrayValue;
import graphql.language.Description;
import graphql.language.EnumTypeDefinition;
import graphql.language.FieldDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import graphql.language.Type;
import graphql.schema.DataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.lang.model.element.Modifier;

/**
 * Translates GraphQL schema files to Java source files.
 */
public class SchemaTranslator {

  private static final String SERVICE_SUFFIX = "Service";
  private static final String TIE_SUFFIX = "Tie";
  private static final ClassName DATA_FETCHER = ClassName.get(DataFetcher.class);
  private static final ClassName LIST = ClassName.get(List.class);
  private static final ParameterizedTypeName ENUM = ParameterizedTypeName.get(
      ClassName.get(Enum.class), WildcardTypeName.subtypeOf(Object.class));
  private static final ParameterizedTypeName FUNCTION_STRING_ENUM = ParameterizedTypeName.get(
      ClassName.get(Function.class), ClassName.get(String.class), ENUM);
  private static final ParameterizedTypeName MAP_CLASS_TO_STRING =
      ParameterizedTypeName.get(
          ClassName.get(Map.class),
          ParameterizedTypeName.get(
              ClassName.get(Class.class), WildcardTypeName.subtypeOf(Object.class)),
          FUNCTION_STRING_ENUM);
  private static final ParameterizedTypeName MAP_CLASS_TO_FUNCTION_GRAPHQLOBJECT =
      ParameterizedTypeName.get(
          ClassName.get(Map.class),
          ParameterizedTypeName.get(
              ClassName.get(Class.class), WildcardTypeName.subtypeOf(Object.class)),
          ParameterizedTypeName.get(Function.class, GraphQLObject.class, GraphQLObject.class));

  private String servicePackageName;
  private String tiePackageName;
  private String typePackageName;
  private String version;
  private boolean lastVersion;
  private Path outputDirectory;
  private TypeTranslator typeTranslator;
  private ClassName versionsClass;

  private FieldTranslator fieldTranslator = new FieldTranslator();
  private TypeDefinitionRegistry typeDefinitionRegistry;
  private Map<String, ClassName> enumTypeToClassMap = new HashMap<>();
  private Map<String, ClassName> inputTypeToClassMap = new HashMap<>();
  private Map<String, ClassName> objectTypeToClassMap = new HashMap<>();

  /**
   * Constructor.
   *
   * @param packagePrefix
   *     base Java package of generated Java source code
   * @param version
   *     API version
   * @param lastVersion
   *     true if API version is last one
   * @param outputDirectory
   *     directory where Java source files will be written
   */
  public SchemaTranslator(
      String packagePrefix, String version, boolean lastVersion, Path outputDirectory) {

    this.servicePackageName = packagePrefix;
    this.tiePackageName = packagePrefix + '.' + version;
    this.typePackageName = packagePrefix + '.' + version + ".type";
    this.version = version;
    this.lastVersion = lastVersion;
    this.outputDirectory = outputDirectory;
    this.typeTranslator = new TypeTranslator(typePackageName);
    this.versionsClass = ClassName.get(packagePrefix, "Versions");
  }

  private ClassName toServiceClassName(ObjectTypeDefinition objectType, String defaultName) {
    return ClassName.get(servicePackageName, TieDirective.getService(objectType, defaultName));
  }

  private ClassName toServiceTieClassName(ClassName serviceClass) {
    return ClassName.get(tiePackageName, serviceClass.simpleName() + TIE_SUFFIX);
  }

  private ClassName toTypeClassName(String className) {
    return ClassName.get(typePackageName, className);
  }

  private void translateEnumTypeDefinition(EnumTypeDefinition enumType) {
    var className = toTypeClassName(enumType.getName());
    enumTypeToClassMap.put(enumType.getName(), className);

    var enumBuilder = TypeSpec.enumBuilder(className)
        .addAnnotation(CodeGeneratorUtils.GENERATED)
        .addModifiers(Modifier.PUBLIC);
    if (enumType.getDescription() != null) {
      enumBuilder.addJavadoc(enumType.getDescription().getContent() + '\n');
    }

    for (var enumValue : enumType.getEnumValueDefinitions()) {
      if (enumValue.getDescription() != null) {
        enumBuilder.addEnumConstant(
            enumValue.getName(),
            TypeSpec.anonymousClassBuilder("")
                .addJavadoc(enumValue.getDescription().getContent() + '\n')
                .build());
      } else {
        enumBuilder.addEnumConstant(enumValue.getName());
      }
    }

    writeJavaFileToTypePackage(enumBuilder.build());
  }

  private void translateFields(ObjectTypeDefinition objectType) {
    var serviceClass = toServiceClassName(objectType, objectType.getName() + SERVICE_SUFFIX);
    fieldTranslator.translateFields(objectType, serviceClass);
  }

  private void translateObjectType(ObjectTypeDefinition objectType) {
    translateFields(objectType);

    typeDefinitionRegistry.objectTypeExtensions()
        .getOrDefault(objectType.getName(), List.of())
        .forEach(this::translateFields);
  }

  private void writeJavaFile(String packageName, TypeSpec typeSpec) {
    var javaFile = JavaFile.builder(packageName, typeSpec)
        .build();
    try {
      javaFile.writeTo(outputDirectory);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot write file", e);
    }
  }

  private void writeJavaFileToTiePackage(TypeSpec typeSpec) {
    writeJavaFile(tiePackageName, typeSpec);
  }

  private void writeJavaFileToTypePackage(TypeSpec typeSpec) {
    writeJavaFile(typePackageName, typeSpec);
  }

  private boolean isListType(com.squareup.javapoet.TypeName javaType) {
    if (javaType instanceof ParameterizedTypeName) {
      return ((ParameterizedTypeName) javaType).rawType.equals(LIST);
    }

    return false;
  }

  private boolean isEnumType(Type type) {
    return typeDefinitionRegistry.getType(type, EnumTypeDefinition.class).isPresent();
  }

  private boolean isInputType(Type type) {
    return typeDefinitionRegistry.getType(type, InputObjectTypeDefinition.class).isPresent();
  }

  private CodeBlock upgrade(String kind, String list, CodeBlock expression) {
    if (lastVersion) {
      return expression;
    }

    return CodeBlock.builder()
        .add("versionHistory.upgrade$L$L(", kind, list)
        .add(expression)
        .add(", $T.$L)", versionsClass, version)
        .build();
  }

  private CodeBlock generateArgument(InputValueDefinition parameter) {
    com.squareup.javapoet.TypeName javaType = typeTranslator.toJavaType(parameter.getType());

    if (isEnumType(parameter.getType()) && !isListType(javaType)) {
      return upgrade(
          "Enum",
          "",
          CodeBlock.of(
              "$T.valueOf(environment.getArgument($S))",
              javaType,
              parameter.getName()));

    } else if (isInputType(parameter.getType())) {
      if (isListType(javaType)) {
        return upgrade(
            "Input",
            "List",
            CodeBlock.of(
                "$T.toInputList(environment, $S, $T::new)",
                ClassName.get(DataFetcherUtils.class),
                parameter.getName(),
                ((ParameterizedTypeName) javaType).typeArguments.get(0)));
      } else {
        return upgrade(
            "Input",
            "",
            CodeBlock.of(
                "$T.toInput(environment, $S, $T::new)",
                ClassName.get(DataFetcherUtils.class),
                parameter.getName(),
                javaType));
      }
    }

    return CodeBlock.of("environment.getArgument($S)", parameter.getName());
  }

  private Stream<CodeBlock> generateDirectiveArguments(FieldDefinition field) {
    List<CodeBlock> arguments = new ArrayList<>();

    var directive = field.getDirective(ArgumentDirective.NAME);
    if (directive != null) {
      var nameArgument = directive.getArgument("name");
      var valueArgument = directive.getArgument("value");
      if (nameArgument != null && valueArgument != null) {
        var value = ((StringValue) valueArgument.getValue()).getValue();
        arguments.add(CodeBlock.of(value));
      }

      var moreArgument = directive.getArgument("more");
      if (moreArgument != null) {
        ((ArrayValue) moreArgument.getValue()).getValues()
            .stream()
            .flatMap(element -> ((ObjectValue) element).getObjectFields().stream())
            .filter(objectField -> objectField.getName().equals("value"))
            .map(objectField -> ((StringValue) objectField.getValue()).getValue())
            .forEach(value -> arguments.add(CodeBlock.of(value)));
      }
    }

    return arguments.stream();
  }

  private CodeBlock generateArguments(FieldDefinition field) {
    var normalArguments = field.getInputValueDefinitions()
        .stream()
        .map(this::generateArgument);
    return Stream.concat(normalArguments, generateDirectiveArguments(field))
        .collect(CodeBlock.joining(",\n", "\n", ""));
  }

  private CodeBlock generateDataFetcherExpression(FieldDefinition field) {
    var expression = CodeBlock.builder()
        .add("service.$L(", field.getName())
        .indent().indent()
        .add(generateArguments(field))
        .add(")")
        .unindent().unindent()
        .build();
    if (lastVersion) {
      return expression;
    }

    com.squareup.javapoet.TypeName javaType = typeTranslator.toJavaType(field.getType());
    var kind = isEnumType(field.getType()) ? "Enum" : "Object";
    var list = isListType(javaType) ? "List" : "";
    return CodeBlock.builder()
        .add("versionHistory.downgrade$L$L(\n", kind, list)
        .indent().indent()
        .add(expression)
        .add(",\n")
        .add("$T.$L)", versionsClass, version)
        .unindent().unindent()
        .build();
  }

  private FieldSpec generateDataFetcher(FieldDefinition field) {
    var fieldType = ParameterizedTypeName.get(
        DATA_FETCHER, typeTranslator.toJavaType(field.getType()).box());
    return FieldSpec.builder(fieldType, field.getName(), Modifier.PUBLIC)
        .initializer(CodeBlock.builder()
            .add("environment -> ")
            .add(generateDataFetcherExpression(field))
            .build())
        .build();
  }

  private void generateServiceTie(ServiceDefinition service) {
    if (service.getFields().isEmpty()) {
      return;
    }

    var serviceTieClass = toServiceTieClassName(service.getServiceClass());
    var classBuilder = TypeSpec.classBuilder(serviceTieClass)
        .addAnnotation(CodeGeneratorUtils.GENERATED)
        .addAnnotation(CodeGeneratorUtils.generateNamedAnnotation(serviceTieClass))
        .addAnnotation(CodeGeneratorUtils.SINGLETON)
        .addModifiers(Modifier.PUBLIC)
        .addField(FieldSpec.builder(service.getServiceClass(), "service", Modifier.PRIVATE)
            .addAnnotation(CodeGeneratorUtils.INJECT)
            .build());

    if (!lastVersion) {
      classBuilder.addField(
          FieldSpec.builder(VersionHistory.class, "versionHistory", Modifier.PRIVATE)
              .addAnnotation(CodeGeneratorUtils.INJECT)
              .build());
    }

    for (var field : service.getFields()) {
      classBuilder.addField(generateDataFetcher(field));
    }

    writeJavaFileToTiePackage(classBuilder.build());
  }

  private static TypeSpec.Builder generateSimpleDataClassConstructors(
      ClassName className, Description description) {

    var classBuilder = TypeSpec.classBuilder(className)
        .addAnnotation(CodeGeneratorUtils.GENERATED)
        .addModifiers(Modifier.PUBLIC)
        .superclass(GraphQLObject.class);
    if (description != null) {
      classBuilder.addJavadoc(description.getContent() + '\n');
    }

    var noArgConstructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .build();
    classBuilder.addMethod(noArgConstructor);

    var copyConstructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(GraphQLObject.class, "source")
        .addStatement("super(source)")
        .build();
    classBuilder.addMethod(copyConstructor);

    return classBuilder;
  }

  private static String capitalize(String input) {
    return Character.toUpperCase(input.charAt(0)) + input.substring(1);
  }

  private void generateGetterSetter(
      Description description, Type fieldType, String fieldName, TypeSpec.Builder classBuilder) {

    var javaType = typeTranslator.toJavaType(fieldType);

    var getterPrefix = TypeName.BOOLEAN.equals(javaType) ? "is" : "get";
    var getterBuilder = MethodSpec.methodBuilder(getterPrefix + capitalize(fieldName))
        .addModifiers(Modifier.PUBLIC)
        .returns(javaType);
    if (description != null) {
      getterBuilder.addJavadoc(description.getContent() + '\n');
    }
    if (isInputType(fieldType)) {
      getterBuilder.addStatement("return super.readFieldValue($S, $T.class)", fieldName, javaType);
    } else {
      getterBuilder.addStatement("return super.readFieldValue($S)", fieldName);
    }

    classBuilder.addMethod(getterBuilder.build());

    var setter = MethodSpec.methodBuilder("set" + capitalize(fieldName))
        .addModifiers(Modifier.PUBLIC)
        .returns(void.class)
        .addParameter(javaType, "value")
        .addStatement("super.putFieldValue($S, value)", fieldName)
        .build();
    classBuilder.addMethod(setter);
  }

  private void generateGetterSetter(FieldDefinition field, TypeSpec.Builder classBuilder) {
    generateGetterSetter(field.getDescription(), field.getType(), field.getName(), classBuilder);
  }

  private void generateGetterSetter(InputValueDefinition field, TypeSpec.Builder classBuilder) {
    generateGetterSetter(field.getDescription(), field.getType(), field.getName(), classBuilder);
  }

  private void translateInputTypeDefinition(InputObjectTypeDefinition inputType) {
    var className = toTypeClassName(inputType.getName());
    inputTypeToClassMap.put(inputType.getName(), className);

    var classBuilder = generateSimpleDataClassConstructors(className, inputType.getDescription());

    var mapConstructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterizedTypeName.get(Map.class, String.class, Object.class), "map")
        .addStatement("super(map)")
        .build();
    classBuilder.addMethod(mapConstructor);

    for (var field : inputType.getInputValueDefinitions()) {
      generateGetterSetter(field, classBuilder);
    }

    var inputTypeExtensions = typeDefinitionRegistry.inputObjectTypeExtensions()
        .getOrDefault(inputType.getName(), List.of());
    for (var inputTypeExtension : inputTypeExtensions) {
      for (var field : inputTypeExtension.getInputValueDefinitions()) {
        generateGetterSetter(field, classBuilder);
      }
    }

    writeJavaFileToTypePackage(classBuilder.build());
  }

  private void generateSimpleDataClass(ObjectTypeDefinition objectType) {
    var className = toTypeClassName(objectType.getName());
    objectTypeToClassMap.put(objectType.getName(), className);

    var classBuilder = generateSimpleDataClassConstructors(className, objectType.getDescription());

    boolean hasProperty = false;
    for (var field : objectType.getFieldDefinitions()) {
      if (!FieldTranslator.isServiceTied(objectType, field)) {
        generateGetterSetter(field, classBuilder);
        hasProperty = true;
      }
    }

    var objectTypeExtensions = typeDefinitionRegistry.objectTypeExtensions()
        .getOrDefault(objectType.getName(), List.of());
    for (var objectTypeExtension : objectTypeExtensions) {
      for (var field : objectTypeExtension.getFieldDefinitions()) {
        if (!FieldTranslator.isServiceTied(objectType, field)) {
          generateGetterSetter(field, classBuilder);
          hasProperty = true;
        }
      }
    }

    if (hasProperty) {
      writeJavaFileToTypePackage(classBuilder.build());
    }
  }

  private void translateObjectTypeDefinition(ObjectTypeDefinition objectType) {
    switch (objectType.getName()) {
      case FieldTranslator.MUTATION:
      case FieldTranslator.QUERY:
        translateObjectType(objectType);
        break;
      default:
        translateObjectType(objectType);
        generateSimpleDataClass(objectType);
    }
  }

  private List<CodeBlock> generateServiceTieRuntimeWiring(ObjectTypeDefinition rootType) {
    List<CodeBlock> dataFetcherInvocations = new ArrayList<>();
    for (var service : fieldTranslator.getServiceDefinitions(rootType.getName())) {
      var serviceTieClass = toServiceTieClassName(service.getServiceClass());
      for (var field : service.getFields()) {
        dataFetcherInvocations.add(CodeBlock.of(
            ".dataFetcher($S, the$T.$N)",
            field.getName(),
            serviceTieClass,
            field.getName()));
      }
    }

    return dataFetcherInvocations;
  }

  private CodeBlock generateSimpleDataRuntimeWiring() {
    return CodeBlock.of(".defaultDataFetcher($T.INSTANCE)", GraphQLObjectDataFetcher.class);
  }

  private CodeBlock generateTypeRuntimeWiring(ObjectTypeDefinition objectType) {
    var codeBlockBuilder = CodeBlock.builder()
        .add("$[")
        .add("$T.newTypeWiring($S)\n", TypeRuntimeWiring.class, objectType.getName());

    List<CodeBlock> dataFetcherInvocations = new ArrayList<>();
    switch (objectType.getName()) {
      case FieldTranslator.MUTATION:
      case FieldTranslator.QUERY:
        dataFetcherInvocations.addAll(generateServiceTieRuntimeWiring(objectType));
        break;
      default:
        dataFetcherInvocations.addAll(generateServiceTieRuntimeWiring(objectType));
        dataFetcherInvocations.add(generateSimpleDataRuntimeWiring());
    }

    return codeBlockBuilder.add(CodeBlock.join(dataFetcherInvocations, "\n"))
        .add("$]")
        .build();
  }

  private CodeBlock generateTypeInvocation(ObjectTypeDefinition objectType) {
    return CodeBlock.builder()
        .add(".type(\n")
        .indent().indent()
        .add(generateTypeRuntimeWiring(objectType))
        .add(")")
        .unindent().unindent()
        .build();
  }

  private void generateVersionExecutorTie() {
    var className = ClassName.get(tiePackageName, "VersionExecutorTie");
    var classBuilder = TypeSpec.classBuilder(className)
        .addAnnotation(CodeGeneratorUtils.GENERATED)
        .addAnnotation(CodeGeneratorUtils.generateNamedAnnotation(className))
        .addAnnotation(CodeGeneratorUtils.SINGLETON)
        .addModifiers(Modifier.PUBLIC)
        .superclass(VersionExecutor.class)
        .addMethod(MethodSpec.methodBuilder("getVersion")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(Version.class)
            .addStatement("return $T.$N", versionsClass, version)
            .build());

    for (var service : fieldTranslator.getServiceDefinitions()) {
      if (service.getFields().isEmpty()) {
        continue;
      }

      ClassName serviceTieClass = toServiceTieClassName(service.getServiceClass());
      classBuilder.addField(FieldSpec.builder(
          serviceTieClass, "the" + serviceTieClass.simpleName(), Modifier.PRIVATE)
          .addAnnotation(CodeGeneratorUtils.INJECT)
          .build());
    }

    var methodBuilder = MethodSpec.methodBuilder("addTypes")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PROTECTED)
        .returns(void.class)
        .addParameter(RuntimeWiring.Builder.class, "builder")
        .addCode("builder\n")
        .addCode("$>$>");

    var typeInvocations = typeDefinitionRegistry.types()
        .values()
        .stream()
        .filter(typeDefinition -> typeDefinition instanceof ObjectTypeDefinition)
        .map(typeDefinition -> generateTypeInvocation((ObjectTypeDefinition) typeDefinition))
        .collect(CodeBlock.joining("\n"));
    methodBuilder.addCode(typeInvocations)
        .addCode(";\n$<$<");
    classBuilder.addMethod(methodBuilder.build());

    writeJavaFileToTiePackage(classBuilder.build());
  }

  private List<CodeBlock> generateEnumClassToTransformerMapEntries(
      Map<String, ClassName> sourceTypeToClassMap, Map<String, ClassName> targetTypeToClassMap) {

    List<CodeBlock> entryCodeBlocks = new ArrayList<>();
    for (var entry : sourceTypeToClassMap.entrySet()) {
      var targetClass = targetTypeToClassMap.get(entry.getKey());
      if (targetClass != null) {
        entryCodeBlocks.add(
            CodeBlock.of("Map.entry($T.class, $T::valueOf)", entry.getValue(), targetClass));
      }
    }

    return entryCodeBlocks;
  }

  private List<CodeBlock> generateEnumClassToUpgraderMapEntries(
      SchemaTranslator previousVersionSchema) {

    return (previousVersionSchema == null)
        ? List.of()
        : generateEnumClassToTransformerMapEntries(
              previousVersionSchema.enumTypeToClassMap, enumTypeToClassMap);
  }

  private List<CodeBlock> generateEnumClassToDowngraderMapEntries(
      SchemaTranslator previousVersionSchema) {

    return (previousVersionSchema == null)
        ? List.of()
        : generateEnumClassToTransformerMapEntries(
              enumTypeToClassMap, previousVersionSchema.enumTypeToClassMap);
  }

  private List<CodeBlock> generateClassToTransformerMapEntries(
      Map<String, ClassName> sourceTypeToClassMap, Map<String, ClassName> targetTypeToClassMap) {

    List<CodeBlock> entryCodeBlocks = new ArrayList<>();
    for (var entry : sourceTypeToClassMap.entrySet()) {
      var targetClass = targetTypeToClassMap.get(entry.getKey());
      if (targetClass != null) {
        entryCodeBlocks.add(
            CodeBlock.of("Map.entry($T.class, $T::new)", entry.getValue(), targetClass));
      }
    }

    return entryCodeBlocks;
  }

  private List<CodeBlock> generateInputClassToUpgraderMapEntries(
      SchemaTranslator previousVersionSchema) {

    return (previousVersionSchema == null)
        ? List.of()
        : generateClassToTransformerMapEntries(
              previousVersionSchema.inputTypeToClassMap, inputTypeToClassMap);
  }

  private List<CodeBlock> generateObjectClassToDowngraderMapEntries(
      SchemaTranslator previousVersionSchema) {

    return (previousVersionSchema == null)
        ? List.of()
        : generateClassToTransformerMapEntries(
              objectTypeToClassMap, previousVersionSchema.objectTypeToClassMap);
  }

  private void generateVersionChangesTie(SchemaTranslator previousVersionSchema) {
    var className = ClassName.get(tiePackageName, "VersionChangesTie");
    var classBuilder = TypeSpec.classBuilder(className)
        .addAnnotation(CodeGeneratorUtils.GENERATED)
        .addAnnotation(CodeGeneratorUtils.generateNamedAnnotation(className))
        .addAnnotation(CodeGeneratorUtils.SINGLETON)
        .addModifiers(Modifier.PUBLIC)
        .superclass(VersionChanges.class);

    classBuilder.addField(FieldSpec.builder(
        MAP_CLASS_TO_STRING,
        "enumToUpgraderMap",
        Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer(CodeBlock.builder()
            .add("Map.ofEntries(\n")
            .add("$>$>")
            .add(CodeBlock.join(
                generateEnumClassToUpgraderMapEntries(previousVersionSchema), ",\n"))
            .add("$<$<")
            .add(")")
            .build())
        .build());

    classBuilder.addField(FieldSpec.builder(
        MAP_CLASS_TO_STRING,
        "enumToDowngraderMap",
        Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer(CodeBlock.builder()
            .add("Map.ofEntries(\n")
            .add("$>$>")
            .add(CodeBlock.join(
                generateEnumClassToDowngraderMapEntries(previousVersionSchema), ",\n"))
            .add("$<$<")
            .add(")")
            .build())
        .build());

    classBuilder.addField(FieldSpec.builder(
        MAP_CLASS_TO_FUNCTION_GRAPHQLOBJECT,
        "inputToUpgraderMap",
        Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer(CodeBlock.builder()
            .add("Map.ofEntries(\n")
            .add("$>$>")
            .add(CodeBlock.join(
                generateInputClassToUpgraderMapEntries(previousVersionSchema), ",\n"))
            .add("$<$<")
            .add(")")
            .build())
        .build());

    classBuilder.addField(FieldSpec.builder(
        MAP_CLASS_TO_FUNCTION_GRAPHQLOBJECT,
        "objectToDowngraderMap",
        Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer(CodeBlock.builder()
            .add("Map.ofEntries(\n")
            .add("$>$>")
            .add(CodeBlock.join(
                generateObjectClassToDowngraderMapEntries(previousVersionSchema), ",\n"))
            .add("$<$<")
            .add(")")
            .build())
        .build());

    classBuilder.addMethod(MethodSpec.constructorBuilder()
        .addAnnotation(CodeGeneratorUtils.INJECT)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(
            ParameterizedTypeName.get(
                ClassName.get(Collection.class),
                ParameterizedTypeName.get(
                    ClassName.get(ChangeDescription.class),
                    WildcardTypeName.subtypeOf(Object.class))),
            "changes")
        .addStatement(
            "super(\n" +
                "$T.$N,\n" +
                "changes,\n" +
                "enumToUpgraderMap,\n" +
                "enumToDowngraderMap,\n" +
                "inputToUpgraderMap,\n" +
                "objectToDowngraderMap)",
            versionsClass,
            version)
        .build());

    writeJavaFileToTiePackage(classBuilder.build());
  }

  /**
   * Translates GraphQL schema files to Java source files.
   *
   * @param schemaFiles
   *     GraphQL schema files
   * @param previousVersionSchema
   *     schema of previous version, or null if translating first version
   */
  public void translateSchemaFiles(
      Collection<Path> schemaFiles, SchemaTranslator previousVersionSchema) {

    typeDefinitionRegistry = new TypeDefinitionRegistry();
    var schemaParser = new SchemaParser();
    for (var schemaFile : schemaFiles) {
      try {
        typeDefinitionRegistry.merge(schemaParser.parse(schemaFile.toFile()));
      } catch (GraphQLException e) {
        throw new IllegalStateException("Cannot parse file " + schemaFile, e);
      }
    }

    for (var typeDefinition : typeDefinitionRegistry.types().values()) {
      if (typeDefinition instanceof EnumTypeDefinition) {
        translateEnumTypeDefinition((EnumTypeDefinition) typeDefinition);
      } else if (typeDefinition instanceof InputObjectTypeDefinition) {
        translateInputTypeDefinition((InputObjectTypeDefinition) typeDefinition);
      } else if (typeDefinition instanceof ObjectTypeDefinition) {
        translateObjectTypeDefinition((ObjectTypeDefinition) typeDefinition);
      }
    }

    fieldTranslator.getServiceDefinitions().forEach(this::generateServiceTie);
    generateVersionExecutorTie();
    generateVersionChangesTie(previousVersionSchema);
  }
}
