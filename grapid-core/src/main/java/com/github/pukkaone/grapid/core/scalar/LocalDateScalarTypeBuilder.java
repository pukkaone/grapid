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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * {@link LocalDate} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class LocalDateScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  private static class LocalDateCoercing implements Coercing<LocalDate, String> {
    @Override
    public String serialize(Object input) throws CoercingSerializeException {
      if (input instanceof LocalDate) {
        try {
          return ((LocalDate) input).toString();
        } catch (DateTimeException e) {
          throw new CoercingSerializeException("Cannot format LocalDate " + input, e);
        }
      } else {
        throw new CoercingSerializeException(
            "Expected value of type LocalDate but was " + input.getClass());
      }
    }

    @Override
    public LocalDate parseValue(Object input) throws CoercingParseValueException {
      if (input instanceof LocalDate) {
        return (LocalDate) input;
      } else if (input instanceof String) {
        try {
          return LocalDate.parse(input.toString());
        } catch (DateTimeParseException e) {
          throw new CoercingParseValueException("Cannot parse [" + input + "] to LocalDate", e);
        }
      } else {
        throw new CoercingParseValueException(
            "Expected input value of type String but was " + input.getClass());
      }
    }

    @Override
    public LocalDate parseLiteral(Object input) throws CoercingParseLiteralException {
      if (input instanceof StringValue) {
        try {
          return LocalDate.parse(((StringValue) input).getValue());
        } catch (DateTimeParseException e) {
          throw new CoercingParseLiteralException("Cannot parse [" + input + "] to LocalDate", e);
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
        .name(LocalDate.class.getSimpleName())
        .description(
            "Date without time zone as used in human communication. " +
            "Value is a string formatted as yyyy-MM-dd")
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(LocalDate.class.getSimpleName())
            .build())
        .coercing(new LocalDateScalarTypeBuilder.LocalDateCoercing())
        .build();
  }
}
