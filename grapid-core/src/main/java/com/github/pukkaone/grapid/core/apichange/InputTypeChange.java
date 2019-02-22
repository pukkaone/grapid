package com.github.pukkaone.grapid.core.apichange;

/**
 * Describes change in input type relative to previous API version.
 *
 * @param <S>
 *     source type
 * @param <T>
 *     target type
 */
public abstract class InputTypeChange<S, T> extends ChangeDescription<S> {

  /**
   * Constructor.
   *
   * @param description
   *     describes change relative to previous API version
   */
  protected InputTypeChange(String description) {
    super(description);
  }

  /**
   * Mutates the target input to be compatible with this version.
   * To optimize performance, may mutate the source input.
   *
   * @param source
   *     source input
   * @param target
   *     input to mutate
   */
  public abstract void upgrade(S source, T target);
}
