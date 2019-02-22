package com.github.pukkaone.grapid.core;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * GraphQL object representation.
 */
@SuppressWarnings("unchecked")
public abstract class GraphQLObject {

  /** backing representation which maps field name to value. */
  private transient Map<String, Object> fieldNameToValueMap;

  /**
   * Constructs object with backing representation shared with other object.
   *
   * @param fieldNameToValueMap
   *     map from field name to value
   */
  protected GraphQLObject(Map<String, Object> fieldNameToValueMap) {
    this.fieldNameToValueMap = fieldNameToValueMap;
  }

  /**
   * Constructs object with no field values.
   */
  protected GraphQLObject() {
    this(new HashMap<>());
  }

  /**
   * Constructs object with backing representation shared with other object.
   *
   * @param source
   *     source object
   */
  protected GraphQLObject(GraphQLObject source) {
    this(source.fieldNameToValueMap);
  }

  private static <T> Constructor<T> getConstructor(Class<T> targetClass) {
    try {
      return targetClass.getConstructor(Map.class);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("Constructor not found for " + targetClass, e);
    }
  }

  /**
   * Gets field value. This method is not named {@code getFieldValue} to avoid colliding with a
   * generated method if a GraphQL schema defines a similarly named field.
   *
   * @param fieldName
   *     field name
   * @param targetClass
   *     expected class of field value
   * @param <V>
   *     value type
   * @return field value
   */
  protected <V> V readFieldValue(String fieldName, Class<V> targetClass) {
    V value = (V) fieldNameToValueMap.get(fieldName);
    if (targetClass.isAssignableFrom(value.getClass())) {
      return value;
    }

    var constructor = getConstructor(targetClass);
    try {
      return constructor.newInstance(value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Cannot create instance", e);
    }
  }

  /**
   * Gets field value. This method is not named {@code getFieldValue} to avoid colliding with a
   * generated method if a GraphQL schema defines a similarly named field.
   *
   * @param fieldName
   *     field name
   * @param <V>
   *     value type
   * @return field value
   */
  protected <V> V readFieldValue(String fieldName) {
    return (V) fieldNameToValueMap.get(fieldName);
  }

  /**
   * Puts field value.
   *
   * @param fieldName
   *     field name
   * @param value
   *     field value
   */
  protected void putFieldValue(String fieldName, Object value) {
    fieldNameToValueMap.put(fieldName, value);
  }

  /**
   * Removes field from backing representation.
   *
   * @param fieldName
   *     field name to remove
   * @throws IllegalArgumentException
   *     if field name not found
   */
  public void removeField(String fieldName) {
    fieldNameToValueMap.remove(fieldName);
  }
}
