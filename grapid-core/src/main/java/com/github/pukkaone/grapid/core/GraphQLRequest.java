package com.github.pukkaone.grapid.core;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GraphQL request body.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class GraphQLRequest {

  private String query;
  private String operationName;
  private Map<String, Object> variables;
}
