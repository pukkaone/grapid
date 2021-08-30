package com.github.pukkaone.grapid.core.scalar;

import com.google.auto.service.AutoService;
import graphql.language.ScalarTypeDefinition;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;

/**
 * {@link Long} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class LongScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  @Override
  public GraphQLScalarType build() {
    return GraphQLScalarType.newScalar(ExtendedScalars.GraphQLLong)
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(ExtendedScalars.GraphQLLong.getName())
            .build())
        .build();
  }
}
