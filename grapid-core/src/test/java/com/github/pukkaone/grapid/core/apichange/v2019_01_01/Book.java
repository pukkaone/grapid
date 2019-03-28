package com.github.pukkaone.grapid.core.apichange.v2019_01_01;

import com.github.pukkaone.grapid.core.GraphQLObject;

public class Book extends GraphQLObject {
  public Book() {
  }

  public Book(GraphQLObject source) {
    super(source);
  }

  public String getTitle() {
    return super.readFieldValue("title");
  }

  public void setTitle(String value) {
    super.putFieldValue("title", value);
  }

  public Offer getOffer() {
    return super.readFieldValue("offer");
  }

  public void setOffer(Offer value) {
    super.putFieldValue("offer", value);
  }
}
