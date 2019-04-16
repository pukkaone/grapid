package com.github.pukkaone.grapid.core.scalar;

import com.google.auto.service.AutoService;
import graphql.language.ScalarTypeDefinition;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * {@link LocalTime} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class LocalTimeScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  private static class LocalTimeCoercing implements Coercing<LocalTime, String> {
    @Override
    public String serialize(Object input) throws CoercingSerializeException {
      if (input instanceof LocalTime) {
        try {
          return ((LocalTime) input).toString();
        } catch (DateTimeException e) {
          throw new CoercingSerializeException("Cannot format LocalTime " + input, e);
        }
      } else {
        throw new CoercingSerializeException(
            "Expected value of type LocalDate but was " + input.getClass());
      }
    }

    @Override
    public LocalTime parseValue(Object input) throws CoercingParseValueException {
      if (input instanceof LocalTime) {
        return (LocalTime) input;
      } else if (input instanceof String) {
        try {
          return LocalTime.parse(input.toString());
        } catch (DateTimeParseException e) {
          throw new CoercingParseValueException("Cannot parse [" + input + "] to LocalTime", e);
        }
      } else {
        throw new CoercingParseValueException(
            "Expected input value of type String but was " + input.getClass());
      }
    }

    @Override
    public LocalTime parseLiteral(Object input) throws CoercingParseLiteralException {
      if (input instanceof StringValue) {
        try {
          return LocalTime.parse(((StringValue) input).getValue());
        } catch (DateTimeParseException e) {
          throw new CoercingParseLiteralException(
              "Cannot parse [" + input + "] to LocalTime", e);
        }
      } else {
        throw new CoercingParseLiteralException(
            "Expected literal of type StringValue but was " + input.getClass());
      }
    }
  }

  @Override
  public GraphQLScalarType build() {
    return GraphQLScalarType.newScalar()
        .name(LocalTime.class.getSimpleName())
        .description(
            "Time without time zone as used in human communication. " +
            "Value is a string formatted as HH:mm:ss")
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(LocalTime.class.getSimpleName())
            .build())
        .coercing(new LocalTimeScalarTypeBuilder.LocalTimeCoercing())
        .build();
  }
}
