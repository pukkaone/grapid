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
 * Translates field in GraphQL object type to method in Java class.
 */
public class FieldTranslator {

  public static final String MUTATION = "Mutation";
  public static final String QUERY = "Query";

  private Map<String, Map<ClassName, ResolverDefinition>> objectTypeToResolverClassToDefinitionMap =
      new HashMap<>();

  private Map<ClassName, ResolverDefinition> findByObjectTypeName(String objectTypeName) {
    return objectTypeToResolverClassToDefinitionMap.computeIfAbsent(
          objectTypeName, key -> new HashMap<>());
  }

  /**
   * Checks if a method of a Java class is invoked to yield the GraphQL field value.
   *
   * @param objectType
   *         GraphQL object type
   * @param field
   *         GraphQL field
   * @return true if a method of a Java class is invoked to yield the field value
   */
  public static boolean isResolvedByMethod(ObjectTypeDefinition objectType, FieldDefinition field) {
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
   * Translates fields in GraphQL object type to methods in Java class.
   *
   * @param objectType
   *     GraphQL object type
   * @param resolverClass
   *     Java class defining methods to yield field value
   */
  public void translateFields(ObjectTypeDefinition objectType, ClassName resolverClass) {
    var resolverClassToDefinitionMap = findByObjectTypeName(objectType.getName());

    var resolverDefinition = resolverClassToDefinitionMap.computeIfAbsent(
        resolverClass, ResolverDefinition::new);
    for (var field : objectType.getFieldDefinitions()) {
      if (isResolvedByMethod(objectType, field)) {
        resolverDefinition.getFields().add(field);
      }
    }
  }

  /**
   * Gets Java methods translated from a GraphQL object type.
   *
   * @param objectTypeName
   *     GraphQL object type
   * @return Java methods
   */
  public Collection<ResolverDefinition> getResolverDefinitions(String objectTypeName) {
    return findByObjectTypeName(objectTypeName).values();
  }

  /**
   * Gets Java methods translated from all GraphQL object types.
   *
   * @return Java methods
   */
  public Collection<ResolverDefinition> getResolverDefinitions() {
    var resolverClassToDefinitionMap = objectTypeToResolverClassToDefinitionMap.values()
        .stream()
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(
            Map.Entry::getKey, Map.Entry::getValue, ResolverDefinition::merge));
    return resolverClassToDefinitionMap.values();
  }
}
