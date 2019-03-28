package com.example.repository;

import com.example.graphql.v2019_01_01.type.Book;
import com.example.graphql.v2019_01_01.type.BookInput;
import com.example.graphql.v2019_01_01.type.Offer;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {

  public Book createBook(BookInput bookInput) {
    var offer = Offer.builder()
        .price(bookInput.getOffer().getPrice())
        .build();

    return Book.builder()
        .id(UUID.randomUUID().toString())
        .title(bookInput.getTitle())
        .offer(offer)
        .build();
  }

  public Book findById(String id) {
    return Book.builder()
        .id(id)
        .title("TITLE")
        .build();
  }

  public List<Book> findByAuthorId(String authorId) {
    return List.of(findById(UUID.randomUUID().toString()));
  }
}
