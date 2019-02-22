package com.github.pukkaone.grapid.core.apichange;

import java.util.Comparator;
import javax.annotation.Priority;

/**
 * Compares objects by {@link javax.annotation.Priority @Priority} value. If the class lacks the
 * annotation, then the object is sorted last.
 */
public class PriorityComparator implements Comparator<Object> {

  public static final PriorityComparator INSTANCE = new PriorityComparator();

  /** Priority value indicating the object should be processed last. */
  public static final int LAST = Integer.MAX_VALUE;

  private static int getPriority(Object object) {
    Priority priority = object.getClass().getAnnotation(Priority.class);
    return (priority != null) ? priority.value() : LAST;
  }

  @Override
  public int compare(Object object1, Object object2) {
    return Integer.compare(getPriority(object1), getPriority(object2));
  }
}
