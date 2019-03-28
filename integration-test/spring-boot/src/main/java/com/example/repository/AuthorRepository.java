package com.example.repository;

import com.example.graphql.v2019_01_01.type.Author;
import com.example.graphql.v2019_01_01.type.AuthorInput;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Author data access operations.
 */
@Repository
public class AuthorRepository {

  /**
   * Creates author.
   *
   * @param authorInput
   *     author input data
   * @return author
   */
  public Author createAuthor(AuthorInput authorInput) {
    return Author.builder()
        .id(UUID.randomUUID().toString())
        .name(authorInput.getName())
        .build();
  }
}
