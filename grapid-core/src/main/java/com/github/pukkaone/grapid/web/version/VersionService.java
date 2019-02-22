package com.github.pukkaone.grapid.web.version;

import com.github.pukkaone.grapid.core.apichange.ChangeDescription;
import com.github.pukkaone.grapid.core.apichange.VersionChanges;
import com.github.pukkaone.grapid.core.apichange.VersionHistory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implements version query.
 */
@RequiredArgsConstructor
@Service
public class VersionService {

  private final VersionHistory versionHistory;

  private static Change toChange(ChangeDescription changeDescription) {
    var change = new Change();
    change.setDescription(changeDescription.getDescription());
    return change;
  }

  private static Version toVersion(VersionChanges versionChanges) {
    var version = new Version();
    version.setVersion(versionChanges.getVersion().toString());
    version.setChanges(versionChanges.getChanges()
        .stream()
        .map(VersionService::toChange)
        .collect(Collectors.toList()));
    return version;
  }

  /**
   * Gets all versions.
   *
   * @return all versions
   */
  public List<Version> versions() {
    return versionHistory.getVersions()
        .stream()
        .map(VersionService::toVersion)
        .collect(Collectors.toList());
  }
}
