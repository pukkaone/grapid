package com.github.pukkaone.grapid.core.apichange;

import com.github.pukkaone.grapid.core.GraphQLObject;
import com.github.pukkaone.grapid.core.Version;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Associates an API version with the API changes appearing in that version.
 */
@SuppressWarnings("unchecked")
public class VersionChanges {

  @Getter
  private Version version;

  @Getter
  private Collection<ChangeDescription<?>> changes;

  private List<EnumTypeChange> enumTypeChanges;
  private List<InputTypeChange> inputTypeChanges;
  private List<ObjectTypeChange> objectTypeChanges;
  private Map<Class<?>, Function<String, Enum<?>>> enumTypeToUpgraderMap;
  private Map<Class<?>, Function<String, Enum<?>>> enumTypeToDowngraderMap;
  private Map<Class<?>, Function<GraphQLObject, GraphQLObject>> inputTypeToUpgraderMap;
  private Map<Class<?>, Function<GraphQLObject, GraphQLObject>> objectTypeToDowngraderMap;

  /**
   * Constructor.
   *
   * @param version
   *     API version
   * @param changes
   *     change descriptions
   * @param enumTypeToUpgraderMap
   *     enum type to upgrade transformer map
   * @param enumTypeToDowngraderMap
   *     enum type to downgrade transformer map
   * @param inputTypeToUpgraderMap
   *     input type to upgrade transformer map
   * @param objectTypeToDowngraderMap
   *     object type to downgrade transformer map
   */
  public VersionChanges(
      Version version,
      Collection<ChangeDescription<?>> changes,
      Map<Class<?>, Function<String, Enum<?>>> enumTypeToUpgraderMap,
      Map<Class<?>, Function<String, Enum<?>>> enumTypeToDowngraderMap,
      Map<Class<?>, Function<GraphQLObject, GraphQLObject>> inputTypeToUpgraderMap,
      Map<Class<?>, Function<GraphQLObject, GraphQLObject>> objectTypeToDowngraderMap) {

    this.version = version;

    this.changes = changes.stream()
        .filter(change -> change.getVersion().equals(version.toString()))
        .collect(Collectors.toList());

    this.enumTypeChanges = changes.stream()
        .filter(change -> change instanceof EnumTypeChange)
        .map(change -> (EnumTypeChange) change)
        .sorted(PriorityComparator.INSTANCE)
        .collect(Collectors.toList());

    this.inputTypeChanges = changes.stream()
        .filter(change -> change instanceof InputTypeChange)
        .map(change -> (InputTypeChange) change)
        .sorted(PriorityComparator.INSTANCE)
        .collect(Collectors.toList());

    this.objectTypeChanges = changes.stream()
        .filter(change -> change instanceof ObjectTypeChange)
        .map(change -> (ObjectTypeChange) change)
        .sorted(PriorityComparator.INSTANCE)
        .collect(Collectors.toList());

    this.enumTypeToUpgraderMap = enumTypeToUpgraderMap;
    this.enumTypeToDowngraderMap = enumTypeToDowngraderMap;
    this.inputTypeToUpgraderMap = inputTypeToUpgraderMap;
    this.objectTypeToDowngraderMap = objectTypeToDowngraderMap;
  }

  /**
   * Transforms enum constant to representation compatible with this API version.
   *
   * @param source
   *     enum constant to transform
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed enum constant
   */
  public <S extends Enum<S>, T extends Enum<T>> T upgradeEnum(S source) {
    var transformer = enumTypeToUpgraderMap.get(source.getClass());
    if (transformer == null) {
      return (T) source;
    }

    String enumConstantName = source.name();
    for (var change : enumTypeChanges) {
      if (change.isUpgradeApplicableTo(source)) {
        enumConstantName = change.upgrade(enumConstantName);
      }
    }

    return (T) transformer.apply(enumConstantName);
  }

  /**
   * Transforms enum constant to representation compatible with previous API version.
   *
   * @param source
   *     enum constant to transform
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed enum constant
   */
  public <S extends Enum<S>, T extends Enum<T>> T downgradeEnum(S source) {
    var transformer = enumTypeToDowngraderMap.get(source.getClass());
    if (transformer == null) {
      return (T) source;
    }

    String enumConstantName = source.name();
    var iterator = enumTypeChanges.listIterator(enumTypeChanges.size());
    while (iterator.hasPrevious()) {
      var change = iterator.previous();
      if (change.isDowngradeApplicableTo(source)) {
        enumConstantName = change.downgrade(enumConstantName);
      }
    }

    return (T) transformer.apply(enumConstantName);
  }

  /**
   * Transforms input to representation compatible with this API version.
   * To optimize performance, may mutate the source input.
   *
   * @param source
   *     input to transform
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed input
   */
  public <S, T> T upgradeInput(S source) {
    var transformer = (Function<S, T>) inputTypeToUpgraderMap.getOrDefault(
        source.getClass(), Function.identity());
    T target = transformer.apply(source);

    for (var change : inputTypeChanges) {
      if (change.isApplicableTo(source)) {
        change.upgrade(source, target);
      }
    }

    return target;
  }

  /**
   * Transforms object to representation compatible with the previous API version.
   * To optimize performance, may mutate the source object.
   *
   * @param source
   *     object to transform
   * @param <S>
   *     source type
   * @param <T>
   *     target type
   * @return transformed object
   */
  public <S, T> T downgradeObject(S source) {
    var transformer = (Function<S, T>) objectTypeToDowngraderMap.getOrDefault(
        source.getClass(), Function.identity());
    T target = transformer.apply(source);

    for (var change : objectTypeChanges) {
      if (change.isApplicableTo(source)) {
        change.downgrade(source, target);
      }
    }

    return target;
  }
}
