package com.pukkaone.grapid.compiler;

import com.github.pukkaone.grapid.core.ArgumentDirective;
import com.squareup.javapoet.ClassName;
import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Translates field in GraphQL object type to method in Java service class.
 */
public class FieldTranslator {

  public static final String MUTATION = "Mutation";
  public static final String QUERY = "Query";

  private Map<String, Map<ClassName, ServiceDefinition>> objectTypeToServiceClassToDefinitionMap =
      new HashMap<>();

  private Map<ClassName, ServiceDefinition> findByObjectTypeName(String objectTypeName) {
    return objectTypeToServiceClassToDefinitionMap.computeIfAbsent(
          objectTypeName, key -> new HashMap<>());
  }

  /**
   * Checks if a method of a Java service class is invoked to yield the GraphQL field value.
   *
   * @param objectType
   *         GraphQL object type
   * @param field
   *         GraphQL field
   * @return true if a method of a Java service class is invoked to yield the field value
   */
  public static boolean isServiceTied(ObjectTypeDefinition objectType, FieldDefinition field) {
    String objectTypeName = objectType.getName();
    if (MUTATION.equals(objectTypeName) || QUERY.equals(objectTypeName)) {
      return true;
    }

    if (!field.getInputValueDefinitions().isEmpty()) {
      return true;
    }

    return field.getDirectives()
        .stream()
        .anyMatch(directive -> directive.getName().equals(ArgumentDirective.NAME));
  }

  /**
   * Translates fields in GraphQL object type to methods in Java service class.
   *
   * @param objectType
   *     GraphQL object type
   * @param serviceClass
   *     Java service class
   */
  public void translateFields(ObjectTypeDefinition objectType, ClassName serviceClass) {
    var serviceClassToDefinitionMap = findByObjectTypeName(objectType.getName());

    var serviceDefinition = serviceClassToDefinitionMap.computeIfAbsent(
        serviceClass, ServiceDefinition::new);
    for (var field : objectType.getFieldDefinitions()) {
      if (isServiceTied(objectType, field)) {
        serviceDefinition.getFields().add(field);
      }
    }
  }

  /**
   * Gets service methods translated from a GraphQL object type.
   *
   * @param objectTypeName
   *     GraphQL object type
   * @return service methods
   */
  public Collection<ServiceDefinition> getServiceDefinitions(String objectTypeName) {
    return findByObjectTypeName(objectTypeName).values();
  }

  /**
   * Gets service methods translated from all GraphQL object types.
   *
   * @return service methods
   */
  public Collection<ServiceDefinition> getServiceDefinitions() {
    var serviceClassToDefinitionMap = objectTypeToServiceClassToDefinitionMap.values()
        .stream()
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(
            Map.Entry::getKey, Map.Entry::getValue, ServiceDefinition::merge));
    return serviceClassToDefinitionMap.values();
  }
}
