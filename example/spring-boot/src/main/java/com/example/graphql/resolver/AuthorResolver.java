package com.example.graphql.resolver;

import com.example.graphql.v2019_01_01.type.Author;
import com.example.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorResolver {

  private final AuthorRepository authorRepository;

  public Author createAuthor(String name) {
    return authorRepository.createAuthor(name);
  }

  public Author author(String id) {
    return authorRepository.findById(id);
  }
}
