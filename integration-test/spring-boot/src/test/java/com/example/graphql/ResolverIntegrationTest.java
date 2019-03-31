package com.example.graphql;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;

/**
 * Tests GraphQL non-root object type fields are resolved by Java methods.
 */
class ResolverIntegrationTest extends IntegrationTestSupport {

  @Test
  void when_field_has_input_value_then_invoke_method() throws Exception {
    var mutation =
        "mutation {" +
          "createAuthor(authorInput: { name: \"NAME\" }) {" +
            "book(id: \"BOOK-1\") {" +
              "title" +
            "}" +
          "}" +
        "}";

    sendQueryVersion2019(mutation)
        .andExpect(jsonPath("$.data.createAuthor.book.title").value("TITLE"));
  }

  @Test
  void when_field_has_directive_then_invoke_method() throws Exception {
    var mutation =
        "mutation { createAuthor(authorInput: { name: \"NAME\" }) { books { title } } }";

    var listPath = "$.data.createAuthor.books";
    sendQueryVersion2019(mutation)
        .andExpect(jsonPath(listPath).isArray())
        .andExpect(jsonPath(listPath, hasSize(1)))
        .andExpect(jsonPath(listPath + "[0].title").value("TITLE"));
  }
}
