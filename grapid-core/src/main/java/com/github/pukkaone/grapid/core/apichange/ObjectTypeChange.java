package com.github.pukkaone.grapid.core.apichange;

/**
 * Describes change in object type relative to previous API version.
 *
 * @param <S>
 *     source type
 * @param <T>
 *     target type
 */
public abstract class ObjectTypeChange<S, T> extends ChangeDescription<S> {

  /**
   * Constructor.
   *
   * @param description
   *     describes change relative to previous API version
   */
  protected ObjectTypeChange(String description) {
    super(description);
  }

  /**
   * Mutates the target object to be compatible with the previous version.
   * To optimize performance, may mutate the source object.
   *
   * @param source
   *     source object
   * @param target
   *     object to mutate
   */
  public abstract void downgrade(S source, T target);
}
