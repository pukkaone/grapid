package com.github.pukkaone.grapid.core.scalar;

import com.google.auto.service.AutoService;
import graphql.Scalars;
import graphql.language.ScalarTypeDefinition;
import graphql.schema.GraphQLScalarType;

/**
 * {@link Character Char} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class CharScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  @Override
  public GraphQLScalarType build() {
    return GraphQLScalarType.newScalar(Scalars.GraphQLChar)
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(Scalars.GraphQLChar.getName())
            .build())
        .build();
  }
}
