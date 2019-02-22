package com.example.graphql.v2019_01_01;

import com.example.graphql.v2019_01_01.type.Book;
import com.github.pukkaone.grapid.core.apichange.ObjectTypeChange;
import org.springframework.stereotype.Component;

/**
 * In object type Book, field price moved to field of nested object offer.
 */
@Component
public class BookPriceChange
    extends ObjectTypeChange<Book, com.example.graphql.v2018_12_31.type.Book> {

  /**
   * Constructor.
   */
  public BookPriceChange() {
    super("In object type Book, field price moved to field of nested object offer.");
  }

  @Override
  public void downgrade(Book source, com.example.graphql.v2018_12_31.type.Book target) {
    target.setPrice(source.getOffer().getPrice());
    target.removeField("offer");
  }
}
