package com.github.pukkaone.grapid.core;

import graphql.language.ObjectTypeDefinition;
import graphql.language.StringValue;

/**
 * Custom GraphQL directive invokes a method of a Java class to yield a GraphQL field value. When
 * applied to an root object type extension, affects all field definitions in the extension.
 */
public final class ResolveDirective {

  public static final String NAME = "resolve";

  public static final String DEFINITION =
      "directive @resolve(class: String) repeatable on FIELD_DEFINITION | OBJECT";

  /**
   * If the object type definition has the directive, then returns the Java class specified in the
   * directive, else returns the given default.
   *
   * @param objectType
   *     GraphQL object type to check for directive
   * @param defaultClass
   *     default Java class
   * @return Java class
   */
  public static String getClass(ObjectTypeDefinition objectType, String defaultClass) {
    var directive = objectType.getDirectives(ResolveDirective.NAME)
        .stream()
        .findFirst();
    if (directive.isEmpty()) {
      return defaultClass;
    }

    var type = directive.get().getArgument("class");
    if (type == null) {
      return defaultClass;
    }

    var value = type.getValue();
    if (value instanceof StringValue) {
      return ((StringValue) value).getValue();
    }

    return defaultClass;
  }
}
