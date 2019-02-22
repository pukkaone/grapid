package com.example.graphql;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests GraphQL server sends and receives scalars.
 */
class ScalarIntegrationTest extends IntegrationTestSupport {

  static Stream<Arguments> should_send_and_receive_non_string_scalar() {
    return Stream.of(
        arguments("BigDecimal", new BigDecimal("123456.78")),
        arguments("BigInteger", new BigDecimal("234567")),
        arguments("Boolean", true),
        arguments("Float", 3.14),
        arguments("Int", 456789),
        arguments("Long", 567890L),
        arguments("Short", 6789));
  }

  @MethodSource
  @ParameterizedTest
  void should_send_and_receive_non_string_scalar(String scalarType, Object value) throws Exception {
    var query = String.format("query { echo%s(value: %s) }", scalarType, value);

    sendQueryVersion2019(query)
        .andExpect(jsonPath("$.data.echo" + scalarType).value(value));
  }

  static Stream<Arguments> should_send_and_receive_string_scalar() {
    return Stream.of(
        arguments("Char", "C"),
        arguments("ID", "id-123"),
        arguments("Instant", "2018-12-31T23:59:59.999Z"),
        arguments("LocalDate", "2018-12-31"),
        arguments("LocalDateTime", "2018-12-31T23:59:59"),
        arguments("LocalTime", "23:59:59"),
        arguments("String", "string-456"));
  }

  @MethodSource
  @ParameterizedTest
  void should_send_and_receive_string_scalar(String scalarType, Object value) throws Exception {
    var query = String.format("query { echo%s(value: \"%s\") }", scalarType, value);

    sendQueryVersion2019(query)
        .andExpect(jsonPath("$.data.echo" + scalarType).value(value));
  }

  @MethodSource("should_send_and_receive_non_string_scalar")
  @ParameterizedTest
  void should_send_and_receive_non_string_list(String scalarType, Object value) throws Exception {
    var query = String.format("query { echoList%s(value: %s) }", scalarType, value);

    var listPath = "$.data.echoList" + scalarType;
    sendQueryVersion2019(query)
        .andExpect(jsonPath(listPath).isArray())
        .andExpect(jsonPath(listPath, hasSize(1)))
        .andExpect(jsonPath(listPath + "[0]").value(value));
  }

  @MethodSource("should_send_and_receive_string_scalar")
  @ParameterizedTest
  void should_send_and_receive_string_list(String scalarType, Object value) throws Exception {
    var query = String.format("query { echoList%s(value: \"%s\") }", scalarType, value);

    var listPath = "$.data.echoList" + scalarType;
    sendQueryVersion2019(query)
        .andExpect(jsonPath(listPath).isArray())
        .andExpect(jsonPath(listPath, hasSize(1)))
        .andExpect(jsonPath(listPath + "[0]").value(value));
  }

  @Test
  void should_send_and_receive_nullable_scalar() throws Exception {
    sendQueryVersion2019("query { echoNullableInt(value: null) }")
        .andExpect(jsonPath("$.data.echoNullableInt").value(nullValue()));
  }
}
