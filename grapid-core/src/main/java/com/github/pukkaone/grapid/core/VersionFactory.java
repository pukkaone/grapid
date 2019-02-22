package com.github.pukkaone.grapid.core;

/**
 * Converts a string to a {@link Version}.
 */
public interface VersionFactory {

  /**
   * Converts a string to a {@link Version}.
   *
   * @param input
   *     API version string
   * @return API version, or null if not found
   */
  Version getVersion(String input);
}
