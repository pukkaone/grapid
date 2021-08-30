package com.github.pukkaone.grapid.core.scalar;

import com.google.auto.service.AutoService;
import graphql.language.ScalarTypeDefinition;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;

/**
 * {@link Character Char} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class CharScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  @Override
  public GraphQLScalarType build() {
    return GraphQLScalarType.newScalar(ExtendedScalars.GraphQLChar)
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(ExtendedScalars.GraphQLChar.getName())
            .build())
        .build();
  }
}
