package com.github.pukkaone.grapid.web.version;

import com.github.pukkaone.grapid.core.GraphQLObject;

/**
 * Change GraphQL object.
 */
@SuppressWarnings("checkstyle:JavadocMethod")
public class Change extends GraphQLObject {
  public Change() {
  }

  public Change(GraphQLObject source) {
    super(source);
  }

  public String getDescription() {
    return super.readFieldValue("description");
  }

  public void setDescription(String value) {
    super.putFieldValue("description", value);
  }
}
