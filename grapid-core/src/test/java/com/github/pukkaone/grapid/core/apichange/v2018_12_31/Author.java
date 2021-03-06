package com.github.pukkaone.grapid.core.apichange.v2018_12_31;

import com.github.pukkaone.grapid.core.GraphQLObject;

public class Author extends GraphQLObject {
  public Author() {
  }

  public Author(GraphQLObject source) {
    super(source);
  }

  public String getName() {
    return super.readFieldValue("name");
  }

  public void setName(String value) {
    super.putFieldValue("name", value);
  }
}
