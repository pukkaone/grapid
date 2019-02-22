package com.pukkaone.grapid.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import javax.annotation.processing.Generated;

/**
 * Code generator convenience methods.
 */
public final class CodeGeneratorUtils {

  public static final AnnotationSpec GENERATED =
      AnnotationSpec.builder(ClassName.get(Generated.class))
          .addMember("value", "$S", CodeGeneratorUtils.class.getPackageName())
          .build();

  public static final ClassName INJECT = ClassName.get("javax.inject", "Inject");

  public static final ClassName SINGLETON = ClassName.get("javax.inject", "Singleton");

  private static final ClassName NAMED = ClassName.get("javax.inject", "Named");

  static AnnotationSpec generateNamedAnnotation(ClassName className) {
    return AnnotationSpec.builder(NAMED)
        .addMember("value", "$S", className)
        .build();
  }
}
