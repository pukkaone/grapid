package com.github.pukkaone.grapid.core.apichange.v2019_01_01;

import com.github.pukkaone.grapid.core.apichange.InputTypeChange;

/**
 * In input type BookInput, field price moved to field of nested input offer.
 */
public class BookInputPriceChange extends InputTypeChange<
    com.github.pukkaone.grapid.core.apichange.v2018_12_31.BookInput, BookInput> {

  /**
   * Constructor.
   */
  public BookInputPriceChange() {
    super("In input type BookInput, field price moved to field of nested input offer.");
  }

  @Override
  public void upgrade(
      com.github.pukkaone.grapid.core.apichange.v2018_12_31.BookInput source, BookInput target) {

    OfferInput offer = new OfferInput();
    offer.setPrice(source.getPrice());

    target.setOffer(offer);
    target.removeField("price");
  }
}
