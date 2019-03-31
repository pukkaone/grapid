package com.example.graphql.resolver;

import com.example.graphql.v2019_01_01.type.Book;
import com.example.graphql.v2019_01_01.type.BookInput;
import com.example.repository.BookRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implements book operations.
 */
@Component
@RequiredArgsConstructor
public class BookResolver {

  private final BookRepository bookRepository;

  public Book createBook(BookInput bookInput) {
    return bookRepository.createBook(bookInput);
  }

  public List<Book> createBooks(List<BookInput> bookInputs) {
    return bookInputs.stream()
        .map(bookRepository::createBook)
        .collect(Collectors.toList());
  }
}
