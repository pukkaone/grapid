package com.github.pukkaone.grapid.core.scalar;

import com.google.auto.service.AutoService;
import graphql.Scalars;
import graphql.language.ScalarTypeDefinition;
import graphql.schema.GraphQLScalarType;

/**
 * {@link Byte} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class ByteScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  @Override
  public GraphQLScalarType build() {
    return GraphQLScalarType.newScalar(Scalars.GraphQLByte)
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(Scalars.GraphQLByte.getName())
            .build())
        .build();
  }
}
