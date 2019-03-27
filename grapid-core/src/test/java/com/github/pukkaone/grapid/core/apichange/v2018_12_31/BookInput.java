package com.github.pukkaone.grapid.core.apichange.v2018_12_31;

import com.github.pukkaone.grapid.core.GraphQLObject;
import java.math.BigDecimal;

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

  public BigDecimal getPrice() {
    return super.readFieldValue("price");
  }

  public void setPrice(BigDecimal value) {
    super.putFieldValue("price", value);
  }
}
