package com.github.pukkaone.grapid.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * API version identifier, suitable for use as a key.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class Version {

  @EqualsAndHashCode.Include
  private final String version;

  @Getter
  private final int ordinal;

  @Override
  public String toString() {
    return version;
  }
}
