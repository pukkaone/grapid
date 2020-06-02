package com.github.pukkaone.grapid.core.scalar;

import com.google.auto.service.AutoService;
import graphql.Scalars;
import graphql.language.ScalarTypeDefinition;
import graphql.schema.GraphQLScalarType;

/**
 * {@link Short} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class ShortScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  @Override
  public GraphQLScalarType build() {
    return GraphQLScalarType.newScalar(Scalars.GraphQLShort)
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(Scalars.GraphQLShort.getName())
            .build())
        .build();
  }
}
