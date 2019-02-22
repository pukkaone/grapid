package com.example.graphql;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.example.graphql.v2019_01_01.type.Meal;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests GraphQL server sends and receives enum.
 */
class EnumIntegrationTest extends IntegrationTestSupport {

  @Test
  void should_send_and_receive_enum() throws Exception {
    var meal = Meal.BREAKFAST;
    var query = String.format("query { echoEnum(value: %s) }", meal);

    sendQueryVersion2019(query)
        .andExpect(jsonPath("$.data.echoEnum").value(meal.toString()));
  }

  @Test
  void should_send_and_receive_enum_list() throws Exception {
    var meals = List.of(Meal.BREAKFAST);
    var query = String.format("query { echoListEnum(value: %s) }", meals);

    var listPath = "$.data.echoListEnum";
    sendQueryVersion2019(query)
        .andExpect(jsonPath(listPath).isArray())
        .andExpect(jsonPath(listPath, hasSize(1)))
        .andExpect(jsonPath(listPath + "[0]").value(meals.get(0).toString()));
  }
}
