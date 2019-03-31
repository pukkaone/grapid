package com.example.graphql.resolver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Implements scalar operations.
 */
@Component
public class ScalarResolver {

  public BigDecimal echoBigDecimal(BigDecimal value) {
    return value;
  }

  public BigInteger echoBigInteger(BigInteger value) {
    return value;
  }

  public boolean echoBoolean(boolean value) {
    return value;
  }

  public char echoChar(char value) {
    return value;
  }

  public double echoFloat(double value) {
    return value;
  }

  public String echoID(String value) {
    return value;
  }

  public Instant echoInstant(Instant value) {
    return value;
  }

  public int echoInt(int value) {
    return value;
  }

  public LocalDate echoLocalDate(LocalDate value) {
    return value;
  }

  public LocalDateTime echoLocalDateTime(LocalDateTime value) {
    return value;
  }

  public LocalTime echoLocalTime(LocalTime value) {
    return value;
  }

  public long echoLong(long value) {
    return value;
  }

  public Integer echoNullableInt(Integer value) {
    return value;
  }

  public short echoShort(short value) {
    return value;
  }

  public String echoString(String value) {
    return value;
  }

  public List<BigDecimal> echoListBigDecimal(List<BigDecimal> value) {
    return value;
  }

  public List<BigInteger> echoListBigInteger(List<BigInteger> value) {
    return value;
  }

  public List<Boolean> echoListBoolean(List<Boolean> value) {
    return value;
  }

  public List<Character> echoListChar(List<Character> value) {
    return value;
  }

  public List<Double> echoListFloat(List<Double> value) {
    return value;
  }

  public List<String> echoListID(List<String> value) {
    return value;
  }

  public List<Instant> echoListInstant(List<Instant> value) {
    return value;
  }

  public List<Integer> echoListInt(List<Integer> value) {
    return value;
  }

  public List<LocalDate> echoListLocalDate(List<LocalDate> value) {
    return value;
  }

  public List<LocalDateTime> echoListLocalDateTime(List<LocalDateTime> value) {
    return value;
  }

  public List<LocalTime> echoListLocalTime(List<LocalTime> value) {
    return value;
  }

  public List<Long> echoListLong(List<Long> value) {
    return value;
  }

  public List<Short> echoListShort(List<Short> value) {
    return value;
  }

  public List<String> echoListString(List<String> value) {
    return value;
  }
}
