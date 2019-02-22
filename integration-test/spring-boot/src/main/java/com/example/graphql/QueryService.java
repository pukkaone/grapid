package com.example.graphql;

import com.example.graphql.v2019_01_01.type.Version;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Implements root query operations.
 */
@Service
@SuppressWarnings("checkstyle:JavadocMethod")
public class QueryService {

  public List<Version> versions() {
    return List.of();
  }
}
