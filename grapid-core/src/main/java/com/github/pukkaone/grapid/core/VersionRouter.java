package com.github.pukkaone.grapid.core;

import java.util.Collection;
import java.util.Map;

/**
 * Forwards GraphQL request to the request executor for a specified API version.
 */
public class VersionRouter {

  private GraphQLExecutor[] executors;

  /**
   * Constructor.
   *
   * @param versionExecutors
   *     version executors
   */
  public VersionRouter(Collection<VersionExecutor> versionExecutors) {
    int maxOrdinal = versionExecutors.stream()
        .mapToInt(versionExecutor -> versionExecutor.getVersion().getOrdinal())
        .max()
        .orElseThrow(() -> new IllegalArgumentException("No versions found"));
    executors = new GraphQLExecutor[maxOrdinal + 1];

    for (var versionExecutor : versionExecutors) {
      var executor = new GraphQLExecutor(
          versionExecutor.getVersion(), versionExecutor.getRuntimeWiring());
      executors[versionExecutor.getVersion().getOrdinal()] = executor;
    }
  }

  /**
   * Forwards GraphQL request to the request executor for a specified API version.
   *
   * @param version
   *     API version
   * @param graphqlRequest
   *     GraphQL request
   * @return GraphQL result map
   */
  public Map<String, Object> execute(Version version, GraphQLRequest graphqlRequest) {
    var executor = executors[version.getOrdinal()];
    return executor.execute(graphqlRequest);
  }
}
