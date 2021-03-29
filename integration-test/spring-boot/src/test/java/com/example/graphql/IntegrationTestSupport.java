package com.example.graphql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pukkaone.grapid.core.GraphQLRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Common integration test support methods.
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class IntegrationTestSupport {

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected MockMvc mockMvc;

  /**
   * Sends GraphQL query to oldest API version.
   *
   * @param query
   *     GraphQL query
   */
  protected ResultActions sendQueryVersion2018(String query) throws Exception {
    var request = new GraphQLRequest();
    request.setQuery(query);

    return mockMvc.perform(
        post("/graphql/v2018_12_31")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isOk());
  }

  /**
   * Sends GraphQL query to latest API version.
   *
   * @param query
   *     GraphQL query
   */
  protected ResultActions sendQueryVersion2019(String query) throws Exception {
    var request = new GraphQLRequest();
    request.setQuery(query);

    return mockMvc.perform(
        post("/graphql/v2019_01_01")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isOk());
  }
}
