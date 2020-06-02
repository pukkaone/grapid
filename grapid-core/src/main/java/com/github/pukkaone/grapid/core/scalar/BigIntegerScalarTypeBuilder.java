package com.github.pukkaone.grapid.core.scalar;

import com.google.auto.service.AutoService;
import graphql.Scalars;
import graphql.language.ScalarTypeDefinition;
import graphql.schema.GraphQLScalarType;

/**
 * {@link java.math.BigInteger BigInteger} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class BigIntegerScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  @Override
  public GraphQLScalarType build() {
    return GraphQLScalarType.newScalar(Scalars.GraphQLBigInteger)
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(Scalars.GraphQLBigInteger.getName())
            .build())
        .build();
  }
}
