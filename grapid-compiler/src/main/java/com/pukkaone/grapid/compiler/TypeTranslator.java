package com.pukkaone.grapid.compiler;

import com.github.pukkaone.grapid.core.scalar.CustomGraphQLScalarTypes;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.Type;
import graphql.language.TypeName;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Translates GraphQL type to Java type.
 */
public class TypeTranslator {

  private static final ClassName LIST = ClassName.get("java.util", "List");

  // scalar types provided by graphql-java
  private static final String BIG_DECIMAL = "BigDecimal";
  private static final String BIG_INTEGER = "BigInteger";
  private static final String BOOLEAN = "Boolean";
  private static final String BYTE = "Byte";
  private static final String CHAR = "Char";
  private static final String FLOAT = "Float";
  private static final String ID = "ID";
  private static final String INT = "Int";
  private static final String LONG = "Long";
  private static final String SHORT = "Short";
  private static final String STRING = "String";

  private String typePackageName;

  /**
   * Constructor.
   *
   * @param typePackageName
   *     Java package of generated Java source code
   */
  public TypeTranslator(String typePackageName) {
    this.typePackageName = typePackageName;
  }

  private static Type extractNonNullType(Type maybeNonNullType) {
    if (maybeNonNullType instanceof NonNullType) {
      return ((NonNullType) maybeNonNullType).getType();
    }

    return null;
  }

  private com.squareup.javapoet.TypeName translateListType(ListType graphqlType) {
    com.squareup.javapoet.TypeName elementType = toJavaType(graphqlType.getType());
    return ParameterizedTypeName.get(LIST, elementType);
  }

  private com.squareup.javapoet.TypeName translateScalarTypeName(TypeName graphqlType) {
    var typeName = graphqlType.getName();
    switch (typeName) {
      case BIG_DECIMAL:
        return ClassName.get(BigDecimal.class);
      case BIG_INTEGER:
        return ClassName.get(BigInteger.class);
      case BOOLEAN:
        return com.squareup.javapoet.TypeName.BOOLEAN;
      case BYTE:
        return com.squareup.javapoet.TypeName.BYTE;
      case CHAR:
        return com.squareup.javapoet.TypeName.CHAR;
      case FLOAT:
        return com.squareup.javapoet.TypeName.DOUBLE;
      case ID:
      case STRING:
        return ClassName.get(String.class);
      case INT:
        return com.squareup.javapoet.TypeName.INT;
      case LONG:
        return com.squareup.javapoet.TypeName.LONG;
      case SHORT:
        return com.squareup.javapoet.TypeName.SHORT;
      default:
        return CustomGraphQLScalarTypes.INSTANCE.findByName(typeName)
            .map(scalarType -> ClassName.bestGuess(scalarType.getDescription()))
            .orElse(null);
    }
  }

  private com.squareup.javapoet.TypeName translateTypeName(TypeName graphqlType) {
    com.squareup.javapoet.TypeName javaType = translateScalarTypeName(graphqlType);
    if (javaType != null) {
      return javaType;
    }

    return ClassName.get(typePackageName, graphqlType.getName());
  }

  private com.squareup.javapoet.TypeName translateType(Type graphqlType) {
    if (graphqlType instanceof ListType) {
      return translateListType((ListType) graphqlType);
    } else if (graphqlType instanceof TypeName) {
      return translateTypeName((TypeName) graphqlType);
    }

    throw new IllegalArgumentException("Cannot translate GraphQL type " + graphqlType);
  }

  /**
   * Translates GraphQL type to Java type.
   *
   * @param graphqlType
   *     GraphQL type
   * @return Java type name
   */
  public com.squareup.javapoet.TypeName toJavaType(Type graphqlType) {
    var sourceType = graphqlType;
    var nonNullType = extractNonNullType(sourceType);
    if (nonNullType != null) {
      sourceType = nonNullType;
    }

    com.squareup.javapoet.TypeName javaType = translateType(sourceType);
    if (nonNullType == null) {
      javaType = javaType.box();
    }

    return javaType;
  }
}
