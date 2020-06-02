package com.github.pukkaone.grapid.core.scalar;

import com.google.auto.service.AutoService;
import graphql.Scalars;
import graphql.language.ScalarTypeDefinition;
import graphql.schema.GraphQLScalarType;

/**
 * {@link java.math.BigDecimal BigDecimal} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class BigDecimalScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  @Override
  public GraphQLScalarType build() {
    return GraphQLScalarType.newScalar(Scalars.GraphQLBigDecimal)
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(Scalars.GraphQLBigDecimal.getName())
            .build())
        .build();
  }
}
