package com.example.graphql.resolver;

import com.example.graphql.v2019_01_01.type.Meal;
import org.springframework.stereotype.Component;

@Component
public class MealResolver {

  public Meal bestMeal() {
    return Meal.SECOND_BREAKFAST;
  }
}
