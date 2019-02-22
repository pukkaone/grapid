package com.example.repository;

import com.example.graphql.v2019_01_01.type.Author;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorRepository {

  public Author createAuthor(String name) {
    var author = new Author();
    author.setId(UUID.randomUUID().toString());
    author.setName(name);
    return author;
  }

  public Author findById(String id) {
    var author = new Author();
    author.setId(id);
    author.setName("NAME");
    return author;
  }
}
