package com.example.graphql.v2019_01_01;

import com.example.graphql.v2019_01_01.type.Meal;
import com.github.pukkaone.grapid.core.apichange.EnumTypeChange;
import org.springframework.stereotype.Component;

/**
 * Enum Meal added enum value SECOND_BREAKFAST.
 */
@Component
public class MealSecondBreakfastChange
    extends EnumTypeChange<com.example.graphql.v2018_12_31.type.Meal, Meal> {

  /**
   * Constructor.
   */
  public MealSecondBreakfastChange() {
    super("Enum Meal added enum value SECOND_BREAKFAST.");
  }

  @Override
  public String downgrade(String enumValueName) {
    return enumValueName.equals(Meal.SECOND_BREAKFAST.name())
        ? Meal.BREAKFAST.name() : enumValueName;
  }
}
