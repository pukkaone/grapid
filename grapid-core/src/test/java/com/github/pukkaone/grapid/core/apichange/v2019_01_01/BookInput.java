package com.github.pukkaone.grapid.core.apichange.v2019_01_01;

import com.github.pukkaone.grapid.core.GraphQLObject;

@SuppressWarnings({ "checkstyle:JavadocMethod", "checkstyle:JavadocType" })
public class BookInput extends GraphQLObject {
  public BookInput() {
  }

  public BookInput(GraphQLObject source) {
    super(source);
  }

  public String getTitle() {
    return super.readFieldValue("title");
  }

  public void setTitle(String value) {
    super.putFieldValue("title", value);
  }

  public OfferInput getOffer() {
    return super.readFieldValue("offer");
  }

  public void setOffer(OfferInput value) {
    super.putFieldValue("offer", value);
  }
}
