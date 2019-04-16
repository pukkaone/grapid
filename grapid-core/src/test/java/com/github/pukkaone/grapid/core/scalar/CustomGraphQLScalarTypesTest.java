package com.github.pukkaone.grapid.core.scalar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link CustomGraphQLScalarTypes}.
 */
class CustomGraphQLScalarTypesTest {

  static Stream<Arguments> should_translate_scalar_type_to_java_type() {
    return Stream.of(
        arguments(Instant.class.getSimpleName(), Instant.class),
        arguments(LocalDate.class.getSimpleName(), LocalDate.class),
        arguments(LocalDateTime.class.getSimpleName(), LocalDateTime.class),
        arguments(LocalTime.class.getSimpleName(), LocalTime.class));
  }

  @MethodSource
  @ParameterizedTest
  void should_translate_scalar_type_to_java_type(String scalarType, Class<?> expectedJavaType) {
    var actualJavaType = CustomGraphQLScalarTypes.INSTANCE.toJavaType(scalarType);
    assertThat(actualJavaType).isPresent();
    assertThat(actualJavaType.get()).isEqualTo(expectedJavaType);
  }
}
