package com.github.pukkaone.grapid.core.apichange.v2019_01_01;

import com.github.pukkaone.grapid.core.apichange.EnumTypeChange;

/**
 * Added enum value SECOND_BREAKFAST to enum type Meal.
 */
public class MealSecondBreakfastChange extends EnumTypeChange<
    com.github.pukkaone.grapid.core.apichange.v2018_12_31.Meal, Meal> {

  /**
   * Constructor.
   */
  public MealSecondBreakfastChange() {
    super("Added enum value SECOND_BREAKFAST to enum type Meal.");
  }

  @Override
  public String downgrade(String enumValueName) {
    return enumValueName.equals(Meal.SECOND_BREAKFAST.name())
        ? Meal.BREAKFAST.name() : enumValueName;
  }
}
