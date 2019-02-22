package com.github.pukkaone.grapid.core.apichange;

import java.lang.reflect.ParameterizedType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Describes a change in this API version relative to previous API version.
 *
 * @param <S>
 *     source type
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ChangeDescription<S> implements VersionAware {

  private final String description;

  /**
   * Checks if this change applies to the object.
   *
   * @param source
   *     object to check
   * @return true if this change applies to the object
   */
  public boolean isApplicableTo(S source) {
    var typeArgument = ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[0];
    return ((Class<?>) typeArgument).isAssignableFrom(source.getClass());
  }
}
