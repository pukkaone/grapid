package com.github.pukkaone.grapid.web.version;

import com.github.pukkaone.grapid.core.GraphQLObject;
import java.util.List;

/**
 * Version GraphQL object.
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class Version extends GraphQLObject {

  public Version() {
  }

  public Version(GraphQLObject source) {
    super(source);
  }

  public String getVersion() {
    return super.readFieldValue("version");
  }

  public void setVersion(String value) {
    super.putFieldValue("version", value);
  }

  public List<Change> getChanges() {
    return super.readFieldValue("changes");
  }

  public void setChanges(List<Change> value) {
    super.putFieldValue("changes", value);
  }
}
