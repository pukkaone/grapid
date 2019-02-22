package com.github.pukkaone.grapid.core;

/**
 * Custom GraphQL directive adds arguments to the Java method invoked to yield a GraphQL field
 * value. Assigns the result of a Java expression to each argument.
 */
public final class ArgumentDirective {

  public static final String NAME = "argument";

  public static final String DEFINITION =
      "input _ArgumentInput { " +
          "name: String! " +
          "value: String! " +
          "} " +
      "directive @argument(" +
          "name: String, value: String, more: [_ArgumentInput]) on FIELD_DEFINITION";
}
