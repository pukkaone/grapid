package com.example.graphql;

import com.example.graphql.v2019_01_01.type.Meal;
import org.springframework.stereotype.Service;

/**
 * Implements enum operations.
 */
@Service
public class MealService {

  public Meal bestMeal() {
    return Meal.SECOND_BREAKFAST;
  }
}
