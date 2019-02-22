package com.example.graphql;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.example.graphql.IntegrationTestSupport;
import org.junit.jupiter.api.Test;

/**
 * Tests GraphQL non-root object type fields are tied to Java service methods.
 */
class ServiceTieIntegrationTest extends IntegrationTestSupport {

  @Test
  void should_tie_field_having_input_value_to_service() throws Exception {
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
  void should_tie_field_having_directive_to_service() throws Exception {
    var mutation =
        "mutation { createAuthor(authorInput: { name: \"NAME\" }) { books { title } } }";

    var listPath = "$.data.createAuthor.books";
    sendQueryVersion2019(mutation)
        .andExpect(jsonPath(listPath).isArray())
        .andExpect(jsonPath(listPath, hasSize(1)))
        .andExpect(jsonPath(listPath + "[0].title").value("TITLE"));
  }
}
