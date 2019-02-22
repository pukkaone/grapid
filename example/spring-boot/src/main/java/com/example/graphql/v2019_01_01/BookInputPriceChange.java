package com.example.graphql.v2019_01_01;

import com.example.graphql.v2019_01_01.type.BookInput;
import com.example.graphql.v2019_01_01.type.OfferInput;
import com.github.pukkaone.grapid.core.apichange.InputTypeChange;
import org.springframework.stereotype.Component;

/**
 * In input type BookInput, field price moved to field of nested input offer.
 */
@Component
public class BookInputPriceChange
    extends InputTypeChange<com.example.graphql.v2018_12_31.type.BookInput, BookInput> {

  /**
   * Constructor.
   */
  public BookInputPriceChange() {
    super("In input type BookInput, field price moved to field of nested input offer.");
  }

  @Override
  public void upgrade(com.example.graphql.v2018_12_31.type.BookInput source, BookInput target) {
    OfferInput offer = new OfferInput();
    offer.setPrice(source.getPrice());

    target.setOffer(offer);
    target.removeField("price");
  }
}
