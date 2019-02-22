package com.example.graphql;

import com.example.graphql.v2019_01_01.type.Author;
import com.example.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthorService {

  private final AuthorRepository authorRepository;

  public Author createAuthor(String name) {
    return authorRepository.createAuthor(name);
  }

  public Author author(String id) {
    return authorRepository.findById(id);
  }
}
