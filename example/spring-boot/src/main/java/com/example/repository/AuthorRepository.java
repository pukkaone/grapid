package com.example.repository;

import com.example.graphql.v2019_01_01.type.Author;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorRepository {

  public Author createAuthor(String name) {
    return Author.builder()
        .id(UUID.randomUUID().toString())
        .name(name)
        .build();
  }

  public Author findById(String id) {
    return Author.builder()
        .id(id)
        .name("NAME")
        .build();
  }
}
