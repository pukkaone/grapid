package com.example.graphql;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;

/**
 * Tests validation directives.
 */
class ValidationDirectiveIntegrationTest extends IntegrationTestSupport {

  @Test
  void when_input_string_too_short_then_return_error() throws Exception {
    var mutation = "mutation { createAuthor(authorInput: { name: \"\" }) { name } }";

    sendQueryVersion2019(mutation).andExpect(
        jsonPath("$.errors[0].message").value(
            "/createAuthor/authorInput/name size must be between 1 and 2147483647"));
  }
}
