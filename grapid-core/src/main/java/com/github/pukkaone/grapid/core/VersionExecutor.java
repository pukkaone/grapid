package com.github.pukkaone.grapid.core;

import com.github.pukkaone.grapid.core.scalar.CustomGraphQLScalarTypes;
import graphql.schema.idl.RuntimeWiring;

/**
 * Associates an API version with the executor which will process requests for that API version.
 */
public abstract class VersionExecutor {

  /**
   * Gets API version.
   *
   * @return API version
   */
  public abstract Version getVersion();

  /**
   * Adds type wirings to runtime wiring.
   *
   * @param builder
   *     runtime wiring builder
   */
  protected abstract void addTypes(RuntimeWiring.Builder builder);

  /**
   * Gets specification for wiring GraphQL types to data fetchers.
   *
   * @return wiring
   */
  public RuntimeWiring.Builder getRuntimeWiring() {
    var builder = RuntimeWiring.newRuntimeWiring();
    for (var scalarType : CustomGraphQLScalarTypes.INSTANCE.getScalarTypes()) {
      builder.scalar(scalarType);
    }

    addTypes(builder);
    return builder;
  }
}
