package com.github.pukkaone.grapid.core.scalar;

import graphql.schema.GraphQLScalarType;
import java.lang.reflect.ParameterizedType;
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

  private static Class<?> toJavaType(GraphQLScalarType scalarType) {
    var coercing = scalarType.getCoercing();
    var typeArgument = ((ParameterizedType) coercing.getClass().getGenericInterfaces()[0])
        .getActualTypeArguments()[0];
    return (Class<?>) typeArgument;
  }

  /**
   * Translates custom GraphQL scalar type to Java type.
   *
   * @param scalarType
   *         GraphQL scalar type
   * @return Java type, or empty if not found
   */
  public Optional<Class<?>> toJavaType(String scalarType) {
    return Optional.ofNullable(nameToScalarTypeMap.get(scalarType))
        .map(CustomGraphQLScalarTypes::toJavaType);
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
