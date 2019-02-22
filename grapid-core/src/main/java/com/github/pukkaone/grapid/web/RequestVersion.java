package com.github.pukkaone.grapid.web;

import com.github.pukkaone.grapid.core.Version;
import com.github.pukkaone.grapid.core.apichange.ChangeDescription;
import com.github.pukkaone.grapid.core.apichange.VersionHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * API version of current request.
 */
@Component
@RequiredArgsConstructor
public class RequestVersion {

  private static final String VERSION_ATTRIBUTE = RequestVersion.class.getName() + ".version";

  private final VersionHistory versionHistory;

  private Version getVersion() {
    return (Version) RequestContextHolder.currentRequestAttributes()
        .getAttribute(VERSION_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
  }

  /**
   * Sets current request API version.
   *
   * @param version
   *     API version to set for the current request
   */
  public void setVersion(Version version) {
    RequestContextHolder.currentRequestAttributes()
        .setAttribute(VERSION_ATTRIBUTE, version, RequestAttributes.SCOPE_REQUEST);
  }

  /**
   * Checks if a change is active while processing the current request API version.
   *
   * @param change
   *     change to check
   * @return true if change is active
   */
  public boolean isActive(ChangeDescription change) {
    return versionHistory.isActive(change, getVersion());
  }
}
