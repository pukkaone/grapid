package com.example.graphql;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.example.graphql.v2019_01_01.type.Meal;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Tests GraphQL server responds to different API versions.
 */
class VersionIntegrationTest extends IntegrationTestSupport {

  private static final String TITLE = "TITLE";
  private static final BigDecimal PRICE = new BigDecimal("12.34");

  @Test
  void should_downgrade_enum_value() throws Exception {
    sendQueryVersion2018("query { bestMeal }")
        .andExpect(jsonPath("$.data.bestMeal").value(Meal.BREAKFAST.toString()));
  }

  @Test
  void should_upgrade_input_and_downgrade_object() throws Exception {
    var mutation =
        "mutation { " +
          "createBook(bookInput: { " +
            "title: \"TITLE\" " +
            "price: 12.34 " +
          "}) { " +
            "title " +
            "price " +
          "} " +
        "}";

    sendQueryVersion2018(mutation)
        .andExpect(jsonPath("$.data.createBook.title").value(TITLE))
        .andExpect(jsonPath("$.data.createBook.price").value(PRICE));
  }

  @Test
  void should_upgrade_input_list_and_downgrade_object_list() throws Exception {
    var mutation =
        "mutation { " +
          "createBooks(bookInputs: [{ " +
            "title: \"TITLE\" " +
            "price: 12.34 " +
          "}]) { " +
            "title " +
            "price " +
          "} " +
        "}";

    var listPath = "$.data.createBooks";
    sendQueryVersion2018(mutation)
        .andExpect(jsonPath(listPath).isArray())
        .andExpect(jsonPath(listPath, hasSize(1)))
        .andExpect(jsonPath(listPath + "[0].title").value(TITLE))
        .andExpect(jsonPath(listPath + "[0].price").value(PRICE));
  }
}
