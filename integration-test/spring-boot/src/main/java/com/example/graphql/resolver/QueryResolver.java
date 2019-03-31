package com.example.graphql.resolver;

import com.example.graphql.v2019_01_01.type.Version;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Implements root query operations.
 */
@Component
public class QueryResolver {

  public List<Version> versions() {
    return List.of();
  }
}
