package com.github.pukkaone.grapid.core;

/**
 * Converts a string to a {@link Version}.
 */
public interface VersionFactory {

  /**
   * Converts a string to a {@link Version}.
   *
   * @param identifier
   *     API version identifier
   * @return API version, or null if not found
   */
  Version getVersion(String identifier);
}
