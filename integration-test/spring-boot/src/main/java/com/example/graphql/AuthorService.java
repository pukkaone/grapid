package com.example.graphql;

import com.example.graphql.v2019_01_01.type.Author;
import com.example.graphql.v2019_01_01.type.AuthorInput;
import com.example.graphql.v2019_01_01.type.Book;
import com.example.repository.AuthorRepository;
import com.example.repository.BookRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implements author operations.
 */
@RequiredArgsConstructor
@Service
@SuppressWarnings("checkstyle:JavadocMethod")
public class AuthorService {

  private final AuthorRepository authorRepository;
  private final BookRepository bookRepository;

  public Book book(String bookId) {
    return bookRepository.findById(bookId);
  }

  public List<Book> books(String authorId) {
    return bookRepository.findByAuthorId(authorId);
  }

  public Author createAuthor(AuthorInput authorInput) {
    return authorRepository.createAuthor(authorInput);
  }

  public List<Author> createAuthors(List<AuthorInput> authorInputs) {
    return authorInputs.stream()
        .map(this::createAuthor)
        .collect(Collectors.toList());
  }

  public Author echoNullableAuthor(AuthorInput authorInput) {
    if (authorInput == null) {
      return null;
    }

    return createAuthor(authorInput);
  }

  public List<Author> echoNullableListAuthor(List<AuthorInput> authorInputs) {
    if (authorInputs == null) {
      return List.of();
    }

    return createAuthors(authorInputs);
  }
}
