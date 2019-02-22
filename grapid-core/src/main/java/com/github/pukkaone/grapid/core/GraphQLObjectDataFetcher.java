package com.github.pukkaone.grapid.core;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

/**
 * Gets value from field of {@link GraphQLObject}.
 *
 * @param <V>
 *     value type
 */
public class GraphQLObjectDataFetcher<V> implements DataFetcher<V> {

  public static final GraphQLObjectDataFetcher<?> INSTANCE = new GraphQLObjectDataFetcher<>();

  @Override
  @SuppressWarnings("unchecked")
  public V get(DataFetchingEnvironment environment) {
    String fieldName = environment.getField().getName();
    return (V) ((GraphQLObject) environment.getSource()).readFieldValue(fieldName);
  }
}
