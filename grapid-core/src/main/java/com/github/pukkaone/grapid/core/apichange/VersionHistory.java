package com.github.pukkaone.grapid.core.apichange;

import com.github.pukkaone.grapid.core.GraphQLObject;
import com.github.pukkaone.grapid.core.Version;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * API version registry.
 */
@SuppressWarnings("unchecked")
public class VersionHistory {

  @Getter
  private List<VersionChanges> versions;

  private Map<ChangeDescription, Integer> changeToVersionOrdinalMap = new HashMap<>();

  /**
   * Constructor.
   *
   * @param versions
   *     versions to register
   */
  public VersionHistory(Collection<VersionChanges> versions) {
    this.versions = new ArrayList<>(versions);
    this.versions.sort(Comparator.comparing(change -> change.getVersion().getOrdinal()));

    for (var version : versions) {
      for (var change : version.getChanges()) {
        changeToVersionOrdinalMap.put(change, version.getVersion().getOrdinal());
      }
    }
  }

  /**
   * Transforms enum constant to representation compatible with the latest API version.
   *
   * @param source
   *     enum constant to transform
   * @param sourceVersion
   *     original API version of enum constant
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed enum constant
   */
  public <S extends Enum<S>, T extends Enum<T>> T upgradeEnum(S source, Version sourceVersion) {
    var current = source;
    int startIndex = sourceVersion.getOrdinal() - versions.get(0).getVersion().getOrdinal() + 1;
    var iterator = versions.listIterator(startIndex);
    while (iterator.hasNext()) {
      var version = iterator.next();
      current = version.upgradeEnum(current);
    }

    return (T) current;
  }

  /**
   * Transforms enum constant list to representation compatible with the latest API version.
   *
   * @param source
   *     enum constants to transform
   * @param sourceVersion
   *     original API version of enum constant
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed enum constants
   */
  public <S extends Enum<S>, T extends Enum<T>> List<T> upgradeEnumList(
      List<S> source, Version sourceVersion) {

    return source.stream()
        .map(element -> (T) upgradeEnum(element, sourceVersion))
        .collect(Collectors.toList());
  }

  /**
   * Transforms enum constant to representation compatible with the specified API version.
   *
   * @param source
   *     enum constant to transform
   * @param targetVersion
   *     API version to transform to
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed enum constant
   */
  public <S extends Enum<S>, T extends Enum<T>> T downgradeEnum(S source, Version targetVersion) {
    var current = source;
    var iterator = versions.listIterator(versions.size());
    while (iterator.hasPrevious()) {
      var version = iterator.previous();
      if (version.getVersion().getOrdinal() == targetVersion.getOrdinal()) {
        break;
      }

      current = version.downgradeEnum(current);
    }

    return (T) current;
  }

  /**
   * Transforms enum constant list to representation compatible with the specified API version.
   *
   * @param source
   *     enum constants to transform
   * @param targetVersion
   *     API version to transform to
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed enum constants
   */
  public <S extends Enum<S>, T extends Enum<T>> List<T> downgradeEnumList(
      List<S> source, Version targetVersion) {

    return source.stream()
        .map(element -> (T) downgradeEnum(element, targetVersion))
        .collect(Collectors.toList());
  }

  /**
   * Transforms input to representation compatible with the latest API version.
   * To optimize performance, may mutate the source input.
   *
   * @param source
   *     input to transform
   * @param sourceVersion
   *     original API version of input
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed input
   */
  public <S extends GraphQLObject, T> T upgradeInput(S source, Version sourceVersion) {
    var current = source;
    int startIndex = sourceVersion.getOrdinal() - versions.get(0).getVersion().getOrdinal() + 1;
    var iterator = versions.listIterator(startIndex);
    while (iterator.hasNext()) {
      var version = iterator.next();
      current = version.upgradeInput(current);
    }

    return (T) current;
  }

  /**
   * Transforms input list to representation compatible with the latest API version.
   * To optimize performance, may mutate the source input.
   *
   * @param source
   *     inputs to transform
   * @param sourceVersion
   *     original API version of input
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed inputs
   */
  public <S extends GraphQLObject, T> List<T> upgradeInputList(
      List<S> source, Version sourceVersion) {

    return source.stream()
        .map(element -> (T) upgradeInput(element, sourceVersion))
        .collect(Collectors.toList());
  }

  /**
   * Transforms object to representation compatible with the specified API version.
   * To optimize performance, may mutate the source object.
   *
   * @param source
   *     object to transform
   * @param targetVersion
   *     API version to transform to
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed object
   */
  public <S extends GraphQLObject, T> T downgradeObject(S source, Version targetVersion) {
    var current = source;
    var iterator = versions.listIterator(versions.size());
    while (iterator.hasPrevious()) {
      var version = iterator.previous();
      if (version.getVersion().getOrdinal() == targetVersion.getOrdinal()) {
        break;
      }

      current = version.downgradeObject(current);
    }

    return (T) current;
  }

  /**
   * Transforms object list to representation compatible with the specified API version.
   * To optimize performance, may mutate the source object.
   *
   * @param source
   *     objects to transform
   * @param targetVersion
   *     API version to transform to
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed objects
   */
  public <S extends GraphQLObject, T> List<T> downgradeObjectList(
      List<S> source, Version targetVersion) {

    return source.stream()
        .map(element -> (T) downgradeObject(element, targetVersion))
        .collect(Collectors.toList());
  }

  /**
   * Checks if a change is active while processing a request for the given API version.
   *
   * @param change
   *     change to check
   * @param requestVersion
   *     request API version
   * @return true if change is active
   */
  public boolean isActive(ChangeDescription change, Version requestVersion) {
    return requestVersion.getOrdinal() >= changeToVersionOrdinalMap.get(change);
  }
}
