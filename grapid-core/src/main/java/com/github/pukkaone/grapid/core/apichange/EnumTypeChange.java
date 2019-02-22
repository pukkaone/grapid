package com.github.pukkaone.grapid.core.apichange;

import java.lang.reflect.ParameterizedType;

/**
 * Describes change in enum type relative to previous API version.
 *
 * @param <S>
 *     source type
 * @param <T>
 *     target type
 */
public abstract class EnumTypeChange<S, T> extends ChangeDescription<S> {

  /**
   * Constructor.
   *
   * @param description
   *     describes change relative to previous API version
   */
  protected EnumTypeChange(String description) {
    super(description);
  }

  /**
   * Checks if this change applies to the enum constant.
   *
   * @param source
   *     object to check
   * @return true if this change applies to the enum constant
   */
  public boolean isUpgradeApplicableTo(S source) {
    return isApplicableTo(source);
  }

  /**
   * Checks if this change applies to the enum constant.
   *
   * @param source
   *     object to check
   * @return true if this change applies to the enum constant
   */
  public boolean isDowngradeApplicableTo(S source) {
    var typeArgument = ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[1];
    return ((Class<?>) typeArgument).isAssignableFrom(source.getClass());
  }

  /**
   * Transforms enum value name to be compatible with this API version.
   *
   * @param source
   *     enum value name to transform
   * @return transformed enum value name
   */
  public String upgrade(String source) {
    return source;
  }

  /**
   * Transforms enum value name to be compatible with the previous API version.
   *
   * @param source
   *     enum value name to transform
   * @return transformed enum value name
   */
  public String downgrade(String source) {
    return source;
  }
}
