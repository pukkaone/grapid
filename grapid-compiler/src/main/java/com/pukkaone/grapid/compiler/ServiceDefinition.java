package com.pukkaone.grapid.compiler;

import com.squareup.javapoet.ClassName;
import graphql.language.FieldDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Describes application class that will process query.
 */
@Data
public class ServiceDefinition {

  private ClassName serviceClass;
  private List<FieldDefinition> fields;

  private ServiceDefinition(ClassName serviceClass, List<FieldDefinition> fields) {
    this.serviceClass = serviceClass;
    this.fields = fields;
  }

  /**
   * Constructor.
   *
   * @param serviceClass
   *     Java service class name
   */
  public ServiceDefinition(ClassName serviceClass) {
    this(serviceClass, new ArrayList<>());
  }

  /**
   * Adds methods from other service definition.
   *
   * @param source
   *     service definition having methods to add
   * @return service definition having methods from this and source service definition
   */
  public ServiceDefinition merge(ServiceDefinition source) {
    var fields = new ArrayList<>(this.fields);
    fields.addAll(source.getFields());

    return new ServiceDefinition(serviceClass, fields);
  }
}
