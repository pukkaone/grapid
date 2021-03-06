package com.example.graphql.resolver;

import com.example.graphql.v2019_01_01.type.Book;
import com.example.graphql.v2019_01_01.type.BookInput;
import com.example.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookResolver {

  private final BookRepository bookRepository;

  public Book createBook(BookInput bookInput) {
    return bookRepository.createBook(bookInput);
  }

  public Book book(String id) {
    return bookRepository.findById(id);
  }
}
