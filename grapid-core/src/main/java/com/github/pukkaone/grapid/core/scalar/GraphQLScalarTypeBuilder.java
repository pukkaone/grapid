package com.github.pukkaone.grapid.core.scalar;

import graphql.schema.GraphQLScalarType;

/**
 * {@link java.util.ServiceLoader ServiceLoader} interface to build GraphQL scalar type.
 */
public interface GraphQLScalarTypeBuilder {

  /**
   * Builds GraphQL scalar type.
   *
   * @return GraphQL scalar type
   */
  GraphQLScalarType build();
}
