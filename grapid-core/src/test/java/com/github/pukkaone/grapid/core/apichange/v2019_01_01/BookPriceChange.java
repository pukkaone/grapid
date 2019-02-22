package com.github.pukkaone.grapid.core.apichange.v2019_01_01;

import com.github.pukkaone.grapid.core.apichange.ObjectTypeChange;

/**
 * In Book object type, price field type moved to inside new offer object.
 */
public class BookPriceChange extends ObjectTypeChange<
    Book, com.github.pukkaone.grapid.core.apichange.v2018_12_31.Book> {

  /**
   * Constructor.
   */
  public BookPriceChange() {
    super("In Book object type, price field type moved to inside new offer object.");
  }

  @Override
  public void downgrade(
      Book source, com.github.pukkaone.grapid.core.apichange.v2018_12_31.Book target) {

    target.setPrice(source.getOffer().getPrice());
    target.removeField("offer");
  }
}
