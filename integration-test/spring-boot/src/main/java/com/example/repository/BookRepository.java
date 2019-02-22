package com.example.repository;

import com.example.graphql.v2019_01_01.type.Book;
import com.example.graphql.v2019_01_01.type.BookInput;
import com.example.graphql.v2019_01_01.type.Offer;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Book data access operations.
 */
@Repository
public class BookRepository {

  /**
   * Creates book.
   *
   * @param bookInput
   *     book input data
   * @return book
   */
  public Book createBook(BookInput bookInput) {
    var book = new Book();
    book.setId(UUID.randomUUID().toString());
    book.setTitle(bookInput.getTitle());

    var offer = new Offer();
    offer.setPrice(bookInput.getOffer().getPrice());
    book.setOffer(offer);

    return book;
  }

  /**
   * Finds book by ID.
   *
   * @param id
   *     ID to find
   * @return book
   */
  public Book findById(String id) {
    var book = new Book();
    book.setId(id);
    book.setTitle("TITLE");
    return book;
  }

  /**
   * Finds books by author ID.
   *
   * @param authorId
   *     ID to find
   * @return book
   */
  public List<Book> findByAuthorId(String authorId) {
    return List.of(findById(UUID.randomUUID().toString()));
  }
}
