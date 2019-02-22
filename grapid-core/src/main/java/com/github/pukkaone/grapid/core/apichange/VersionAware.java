package com.github.pukkaone.grapid.core.apichange;

/**
 * Gets API version for class implementing this interface.
 */
public interface VersionAware {

  /**
   * Gets API version for class implementing this interface. By convention, the API version is the
   * last component of the name of the Java package containing the class.
   *
   * @return API version
   */
  default String getVersion() {
    var packageName = getClass().getPackageName();
    int dotIndex = packageName.lastIndexOf('.');
    return packageName.substring(dotIndex + 1);
  }
}
