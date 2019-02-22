package com.github.pukkaone.grapid.core;

import com.github.pukkaone.grapid.core.scalar.CustomGraphQLScalarTypes;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Adapts GraphQL request to service method.
 */
public class GraphQLExecutor {

  private GraphQL graphQL;

  /**
   * Constructor.
   *
   * @param schemaFilePattern
   *     pattern to match schema file names
   * @param runtimeWiring
   *     specification for wiring GraphQL types to data fetchers.
   */
  public GraphQLExecutor(String schemaFilePattern, RuntimeWiring runtimeWiring) {
    var typeDefinitionRegistry = parseSchemaFiles(schemaFilePattern);
    var graphQLSchema = new SchemaGenerator().makeExecutableSchema(
        typeDefinitionRegistry, runtimeWiring);
    graphQL = GraphQL.newGraphQL(graphQLSchema).build();
  }

  /**
   * Constructor.
   *
   * @param version
   *     API version
   * @param runtimeWiring
   *     specification for wiring GraphQL types to data fetchers.
   */
  public GraphQLExecutor(Version version, RuntimeWiring runtimeWiring) {
    this("classpath*:/graphql/" + version + "/**/*.graphql", runtimeWiring);
  }

  private TypeDefinitionRegistry parseSchemaFiles(String schemaFilePattern) {
    var typeDefinitionRegistry = new TypeDefinitionRegistry();

    for (var scalarType : CustomGraphQLScalarTypes.INSTANCE.getScalarTypes()) {
      typeDefinitionRegistry.add(scalarType.getDefinition());
    }

    var schemaParser = new SchemaParser();
    typeDefinitionRegistry.merge(schemaParser.parse(ArgumentDirective.DEFINITION));
    typeDefinitionRegistry.merge(schemaParser.parse(TieDirective.DEFINITION));

    var patternResolver = new PathMatchingResourcePatternResolver();
    Resource[] resources;
    try {
      resources = patternResolver.getResources(schemaFilePattern);
    } catch (IOException e) {
      throw new IllegalStateException(
          "Cannot find GraphQL files matching pattern " + schemaFilePattern);
    }

    for (var resource : resources) {
      try (var reader = new InputStreamReader(resource.getInputStream())) {
        typeDefinitionRegistry.merge(schemaParser.parse(reader));
      } catch (IOException e) {
        throw new IllegalStateException("Cannot read resource " + resource);
      }
    }

    return typeDefinitionRegistry;
  }

  /**
   * Executes GraphQL request.
   *
   * @param graphqlRequest
   *     GraphQL request
   * @return GraphQL result map
   */
  public Map<String, Object> execute(GraphQLRequest graphqlRequest) {
    var executionInput = ExecutionInput.newExecutionInput()
        .query(graphqlRequest.getQuery())
        .operationName(graphqlRequest.getOperationName())
        .variables(graphqlRequest.getVariables())
        .build();
    return graphQL.execute(executionInput)
        .toSpecification();
  }
}
