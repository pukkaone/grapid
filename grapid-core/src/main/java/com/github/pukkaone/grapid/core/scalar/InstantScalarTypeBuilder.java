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
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

/**
 * {@link Instant} custom GraphQL scalar type.
 */
@AutoService(GraphQLScalarTypeBuilder.class)
public class InstantScalarTypeBuilder implements GraphQLScalarTypeBuilder {

  private static final DateTimeFormatter INSTANT_FORMATTER =
      new DateTimeFormatterBuilder().appendInstant(3).toFormatter();

  private static class InstantCoercing implements Coercing<Instant, String> {
    @Override
    public String serialize(Object input) throws CoercingSerializeException {
      if (input instanceof Instant) {
        try {
          return INSTANT_FORMATTER.format((Instant) input);
        } catch (DateTimeException e) {
          throw new CoercingSerializeException("Cannot format Instant " + input, e);
        }
      } else {
        throw new CoercingSerializeException(
            "Expected value of type Instant but was " + input.getClass());
      }
    }

    @Override
    public Instant parseValue(Object input) throws CoercingParseValueException {
      if (input instanceof Instant) {
        return (Instant) input;
      } else if (input instanceof String) {
        try {
          return Instant.parse(input.toString());
        } catch (DateTimeParseException e) {
          throw new CoercingParseValueException("Cannot parse [" + input + "] to Instant", e);
        }
      } else {
        throw new CoercingParseValueException(
            "Expected input value of type String but was " + input.getClass());
      }
    }

    @Override
    public Instant parseLiteral(Object input) throws CoercingParseLiteralException {
      if (input instanceof StringValue) {
        try {
          return Instant.parse(((StringValue) input).getValue());
        } catch (DateTimeParseException e) {
          throw new CoercingParseLiteralException("Cannot parse [" + input + "] to Instant", e);
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
        .name(Instant.class.getSimpleName())
        .description(Instant.class.getName())
        .definition(ScalarTypeDefinition.newScalarTypeDefinition()
            .name(Instant.class.getSimpleName())
            .build())
        .coercing(new InstantCoercing())
        .build();
  }
}
