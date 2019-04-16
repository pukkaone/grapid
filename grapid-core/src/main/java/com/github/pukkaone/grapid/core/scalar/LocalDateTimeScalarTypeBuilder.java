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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * {@link LocalDateTime} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class LocalDateTimeScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  private static class LocalDateTimeCoercing implements Coercing<LocalDateTime, String> {
    @Override
    public String serialize(Object input) throws CoercingSerializeException {
      if (input instanceof LocalDateTime) {
        try {
          return ((LocalDateTime) input).toString();
        } catch (DateTimeException e) {
          throw new CoercingSerializeException("Cannot format LocalDateTime " + input, e);
        }
      } else {
        throw new CoercingSerializeException(
            "Expected value of type LocalDate but was " + input.getClass());
      }
    }

    @Override
    public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
      if (input instanceof LocalDateTime) {
        return (LocalDateTime) input;
      } else if (input instanceof String) {
        try {
          return LocalDateTime.parse(input.toString());
        } catch (DateTimeParseException e) {
          throw new CoercingParseValueException("Cannot parse [" + input + "] to LocalDateTime", e);
        }
      } else {
        throw new CoercingParseValueException(
            "Expected input value of type String but was " + input.getClass());
      }
    }

    @Override
    public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
      if (input instanceof StringValue) {
        try {
          return LocalDateTime.parse(((StringValue) input).getValue());
        } catch (DateTimeParseException e) {
          throw new CoercingParseLiteralException(
              "Cannot parse [" + input + "] to LocalDateTime", e);
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
        .name(LocalDateTime.class.getSimpleName())
        .description(
            "Date and time without time zone as used in human communication. " +
            "Value is a string formatted as yyyy-MM-dd'T'HH:mm:ss")
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(LocalDateTime.class.getSimpleName())
            .build())
        .coercing(new LocalDateTimeScalarTypeBuilder.LocalDateTimeCoercing())
        .build();
  }
}
