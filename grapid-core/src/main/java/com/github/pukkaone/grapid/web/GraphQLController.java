package com.github.pukkaone.grapid.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pukkaone.grapid.core.GraphQLRequest;
import com.github.pukkaone.grapid.core.Version;
import com.github.pukkaone.grapid.core.VersionFactory;
import com.github.pukkaone.grapid.core.VersionRouter;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Executes operation defined in the GraphQL schema identified by an API version.
 */
@RequestMapping(
    value = "${graphql.url:/graphql}/{version}",
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@RestController
public class GraphQLController {

  private final ObjectMapper objectMapper;
  private final VersionFactory versionFactory;
  private final RequestVersion requestVersion;
  private final VersionRouter versionRouter;

  @SuppressWarnings("unchecked")
  private Map<String, Object> toMap(String json) {
    if (json == null) {
      return Map.of();
    }

    try {
      return objectMapper.readValue(json, Map.class);
    } catch (IOException e) {
      throw new IllegalStateException(
          "Cannot parse variables query parameter as JSON map", e);
    }
  }

  private Object execute(String versionString, GraphQLRequest graphqlRequest) {
    Version version = versionFactory.getVersion(versionString);
    if (version == null) {
      throw new ResourceNotFoundException("Version [" + versionString + "] not found");
    }

    requestVersion.setVersion(version);
    return versionRouter.execute(version, graphqlRequest);
  }

  /**
   * Executes operation defined in the GraphQL schema identified by an API version.
   *
   * @param version
   *     API version
   * @param query
   *     GraphQL query
   * @param operationName
   *     operation name to apply to request
   * @param variables
   *     names and values formatted as a JSON object
   * @return GraphQL response
   */
  @RequestMapping(method = RequestMethod.GET)
  public Object query(
      @PathVariable("version") String version,
      @RequestParam("query") String query,
      @RequestParam(value = "operationName", required = false) String operationName,
      @RequestParam(value = "variables", required = false) String variables) {

    GraphQLRequest graphqlRequest = new GraphQLRequest(
        query, operationName, toMap(variables));
    return execute(version, graphqlRequest);
  }

  /**
   * Executes operation defined in the GraphQL schema identified by an API version.
   *
   * @param version
   *     API version
   * @param graphqlRequest
   *     JSON-formatted request body
   * @return GraphQL response
   */
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public Object query(
      @PathVariable("version") String version,
      @RequestBody GraphQLRequest graphqlRequest) {

    if (graphqlRequest.getQuery() == null) {
      graphqlRequest.setQuery("");
    }

    if (graphqlRequest.getVariables() == null) {
      graphqlRequest.setVariables(Map.of());
    }

    return execute(version, graphqlRequest);
  }
}
