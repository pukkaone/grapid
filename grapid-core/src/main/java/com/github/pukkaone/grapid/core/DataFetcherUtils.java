package com.github.pukkaone.grapid.core;

import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Data fetcher convenience methods.
 */
@SuppressWarnings("unchecked")
public class DataFetcherUtils {

  /**
   * Converts map from argument to input object.
   *
   * @param environment
   *     data fetching environment
   * @param argumentName
   *     argument name
   * @param transform
   *     function that transforms map to input object
   * @param <T>
   *     input type
   * @return input object, or null if argument is absent
   */
  public static <T> T toInput(
      DataFetchingEnvironment environment,
      String argumentName,
      Function<Map<String, Object>, T> transform) {

    var map = (Map<String, Object>) environment.getArgument(argumentName);
    if (map == null) {
      return null;
    }

    return transform.apply(map);
  }

  /**
   * Converts list of maps from argument to list of input objects.
   *
   * @param environment
   *     data fetching environment
   * @param argumentName
   *     argument name
   * @param transform
   *     function that transforms map to input object
   * @param <T>
   *     input type
   * @return list of input objects, or null if argument is absent
   */
  public static <T> List<T> toInputList(
      DataFetchingEnvironment environment,
      String argumentName,
      Function<Map<String, Object>, T> transform) {

    var list = (List<Map<String, Object>>) environment.getArgument(argumentName);
    if (list == null) {
      return null;
    }

    return list.stream()
        .map(transform)
        .collect(Collectors.toList());
  }
}
