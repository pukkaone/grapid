package com.github.pukkaone.grapid.web.version;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pukkaone.grapid.core.GraphQLExecutor;
import com.github.pukkaone.grapid.core.GraphQLObjectDataFetcher;
import com.github.pukkaone.grapid.core.GraphQLRequest;
import com.github.pukkaone.grapid.core.scalar.CustomGraphQLScalarTypes;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implements GraphQL query for API versions.
 */
@RequestMapping(
    value = "${graphql.url:/graphql}",
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
public class VersionController {

  private ObjectMapper objectMapper;
  private GraphQLExecutor executor;

  /**
   * Constructor.
   *
   * @param objectMapper
   *     JSON object mapper
   * @param versionService
   *     version service
   */
  public VersionController(ObjectMapper objectMapper, VersionService versionService) {
    this.objectMapper = objectMapper;
    this.executor = new GraphQLExecutor(
        "classpath*:/grapid/version/Version.graphqls", getRuntimeWiring(versionService));
  }

  private static RuntimeWiring getRuntimeWiring(VersionService versionService) {
    var builder = RuntimeWiring.newRuntimeWiring();
    for (var scalarType : CustomGraphQLScalarTypes.INSTANCE.getScalarTypes()) {
      builder.scalar(scalarType);
    }

    return builder
        .type(
            TypeRuntimeWiring.newTypeWiring(Change.class.getSimpleName())
                .defaultDataFetcher(GraphQLObjectDataFetcher.INSTANCE))
        .type(
            TypeRuntimeWiring.newTypeWiring(Version.class.getSimpleName())
                .defaultDataFetcher(GraphQLObjectDataFetcher.INSTANCE))
        .type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("versions", environment -> versionService.versions()))
        .build();
  }

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

  /**
   * Executes operation defined in the GraphQL schema.
   *
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
      @RequestParam("query") String query,
      @RequestParam(value = "operationName", required = false) String operationName,
      @RequestParam(value = "variables", required = false) String variables) {

    GraphQLRequest graphqlRequest = new GraphQLRequest(
        query, operationName, toMap(variables));
    return executor.execute(graphqlRequest);
  }

  /**
   * Executes operation defined in the GraphQL schema.
   *
   * @param graphqlRequest
   *     JSON-formatted request body
   * @return GraphQL response
   */
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public Object query(@RequestBody GraphQLRequest graphqlRequest) {
    if (graphqlRequest.getQuery() == null) {
      graphqlRequest.setQuery("");
    }

    if (graphqlRequest.getVariables() == null) {
      graphqlRequest.setVariables(Map.of());
    }

    return executor.execute(graphqlRequest);
  }
}
