package com.github.pukkaone.grapid.core.scalar;

import com.google.auto.service.AutoService;
import graphql.language.ScalarTypeDefinition;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;

/**
 * {@link Short} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class ShortScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  @Override
  public GraphQLScalarType build() {
    return GraphQLScalarType.newScalar(ExtendedScalars.GraphQLShort)
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(ExtendedScalars.GraphQLShort.getName())
            .build())
        .build();
  }
}
