package com.example.service;

import com.example.graphql.v2019_01_01.type.Meal;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Implements enum operations.
 */
@Component
public class EnumService {

  public Meal bestMeal() {
    return Meal.SECOND_BREAKFAST;
  }

  public Meal echoEnum(Meal value) {
    return value;
  }

  public List<Meal> echoListEnum(List<Meal> value) {
    return value;
  }
}
