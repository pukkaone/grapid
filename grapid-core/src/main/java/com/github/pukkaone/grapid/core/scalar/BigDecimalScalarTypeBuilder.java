package com.github.pukkaone.grapid.core.scalar;

import com.google.auto.service.AutoService;
import graphql.language.ScalarTypeDefinition;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;

/**
 * {@link java.math.BigDecimal BigDecimal} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class BigDecimalScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  @Override
  public GraphQLScalarType build() {
    return GraphQLScalarType.newScalar(ExtendedScalars.GraphQLBigDecimal)
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(ExtendedScalars.GraphQLBigDecimal.getName())
            .build())
        .build();
  }
}
