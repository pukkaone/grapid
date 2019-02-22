package com.github.pukkaone.grapid.core.scalar;

import graphql.schema.GraphQLScalarType;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Custom GraphQL scalar type collection.
 */
public class CustomGraphQLScalarTypes {

  /** custom scalar types instance. */
  public static final CustomGraphQLScalarTypes INSTANCE = new CustomGraphQLScalarTypes();

  private Map<String, GraphQLScalarType> nameToScalarTypeMap;

  private CustomGraphQLScalarTypes() {
    nameToScalarTypeMap = ServiceLoader.load(GraphQLScalarTypeBuilder.class)
        .stream()
        .map(ServiceLoader.Provider::get)
        .map(GraphQLScalarTypeBuilder::build)
        .collect(Collectors.toMap(GraphQLScalarType::getName, Function.identity()));
  }

  /**
   * Finds custom GraphQL scalar type by name.
   *
   * @param name
   *         to find
   * @return scalar type, or empty if not found
   */
  public Optional<GraphQLScalarType> findByName(String name) {
    return Optional.ofNullable(nameToScalarTypeMap.get(name));
  }

  /**
   * Gets custom GraphQL scalar types.
   *
   * @return scalar types
   */
  public Collection<GraphQLScalarType> getScalarTypes() {
    return nameToScalarTypeMap.values();
  }
}
