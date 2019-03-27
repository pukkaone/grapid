package com.example.graphql;

import com.example.graphql.v2019_01_01.type.Meal;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Implements enum operations.
 */
@Service
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
