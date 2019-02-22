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
    var book = new Book();
    book.setId(UUID.randomUUID().toString());
    book.setTitle(bookInput.getTitle());

    var offer = new Offer();
    offer.setPrice(bookInput.getOffer().getPrice());
    book.setOffer(offer);
    return book;
  }

  public Book findById(String id) {
    var book = new Book();
    book.setId(id);
    book.setTitle("TITLE");
    return book;
  }

  public List<Book> findByAuthorId(String authorId) {
    return List.of(findById(UUID.randomUUID().toString()));
  }
}
