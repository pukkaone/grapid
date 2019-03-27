package com.github.pukkaone.grapid.core.apichange.v2019_01_01;

import com.github.pukkaone.grapid.core.GraphQLObject;
import java.math.BigDecimal;

public class Offer extends GraphQLObject {
  public Offer() {
  }

  public Offer(GraphQLObject source) {
    super(source);
  }

  public BigDecimal getPrice() {
    return super.readFieldValue("price");
  }

  public void setPrice(BigDecimal value) {
    super.putFieldValue("price", value);
  }
}
