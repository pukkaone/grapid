package com.github.pukkaone.grapid.core;

import graphql.language.ObjectTypeDefinition;
import graphql.language.StringValue;

/**
 * Custom GraphQL directive invokes a method of a Java service class to yield a GraphQL field value.
 * When applied to an root object type extension, affects all field definitions in the extension.
 */
public final class TieDirective {

  public static final String NAME = "tie";

  public static final String DEFINITION =
      "directive @tie(service: String) on FIELD_DEFINITION | OBJECT";

  /**
   * If the directive is present, then returns the service class specified in the directive, else
   * returns the given default.
   *
   * @param objectType
   *     GraphQL object type to check for directive
   * @param defaultService
   *     default service class
   * @return service class
   */
  public static String getService(ObjectTypeDefinition objectType, String defaultService) {
    var tieDirective = objectType.getDirective(TieDirective.NAME);
    if (tieDirective == null) {
      return defaultService;
    }

    var type = tieDirective.getArgument("service");
    if (type == null) {
      return defaultService;
    }

    var value = type.getValue();
    if (value instanceof StringValue) {
      return ((StringValue) value).getValue();
    }

    return defaultService;
  }
}
