package com.example.graphql;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;

/**
 * Tests GraphQL server receives input.
 */
class InputIntegrationTest extends IntegrationTestSupport {

  private static final String NAME = "NAME";

  @Test
  void should_receive_input() throws Exception {
    var mutation = String.format(
        "mutation { createAuthor(authorInput: { name: \"%s\" }) { name } }", NAME);

    sendQueryVersion2019(mutation)
        .andExpect(jsonPath("$.data.createAuthor.name").value(NAME));
  }

  @Test
  void should_receive_input_list() throws Exception {
    var mutation = String.format(
        "mutation { createAuthors(authorInputs: [{ name: \"%s\" }]) { name } }", NAME);

    var listPath = "$.data.createAuthors";
    sendQueryVersion2019(mutation)
        .andExpect(jsonPath(listPath).isArray())
        .andExpect(jsonPath(listPath, hasSize(1)))
        .andExpect(jsonPath(listPath + "[0].name").value(NAME));
  }

  @Test
  void should_receive_nullable_input() throws Exception {
    sendQueryVersion2019("query { echoNullableAuthor(authorInput: null) { name } }")
        .andExpect(jsonPath("$.data.echoNullableAuthor").value(nullValue()));
  }

  @Test
  void should_receive_nullable_input_list() throws Exception {
    var listPath = "$.data.echoNullableListAuthor";
    sendQueryVersion2019("query { echoNullableListAuthor(authorInputs: null) { name } }")
        .andExpect(jsonPath(listPath).isArray())
        .andExpect(jsonPath(listPath).isEmpty());
  }
}
