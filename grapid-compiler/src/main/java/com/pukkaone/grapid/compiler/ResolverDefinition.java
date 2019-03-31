package com.pukkaone.grapid.compiler;

import com.squareup.javapoet.ClassName;
import graphql.language.FieldDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Describes application class defining methods to yield field values.
 */
@Data
public class ResolverDefinition {

  private ClassName resolverClass;
  private List<FieldDefinition> fields;

  private ResolverDefinition(ClassName resolverClass, List<FieldDefinition> fields) {
    this.resolverClass = resolverClass;
    this.fields = fields;
  }

  /**
   * Constructor.
   *
   * @param resolverClass
   *     Java class name
   */
  public ResolverDefinition(ClassName resolverClass) {
    this(resolverClass, new ArrayList<>());
  }

  /**
   * Adds methods from other resolver definition.
   *
   * @param source
   *     resolver definition having methods to add
   * @return resolver definition having methods from this and source resolver definition
   */
  public ResolverDefinition merge(ResolverDefinition source) {
    var fields = new ArrayList<>(this.fields);
    fields.addAll(source.getFields());

    return new ResolverDefinition(resolverClass, fields);
  }
}
