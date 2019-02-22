package com.github.pukkaone.grapid.core;

import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * Compares digit sequences inside string numerically.
 */
public class VersionComparator implements Comparator<String> {

  // match boundary between digit and non-digit characters
  private static final Pattern DIGIT_BOUNDARY = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

  @Override
  public int compare(String version1, String version2) {
    String[] segments1 = DIGIT_BOUNDARY.split(version1);
    String[] segments2 = DIGIT_BOUNDARY.split(version2);

    int minSegmentsLength = Math.min(segments1.length, segments2.length);
    for (int i = 0; i < minSegmentsLength; ++i) {
      int result = 0;

      // If both segments are digits, then compare them numerically.
      char c1 = segments1[i].charAt(0);
      char c2 = segments2[i].charAt(0);
      if (Character.isDigit(c1) && Character.isDigit(c2)) {
        result = Long.valueOf(segments1[i]).compareTo(Long.valueOf(segments2[i]));
      }

      // If either segment is not digits, or segments are numerically equal, then
      // compare them lexicographically.
      if (result == 0) {
        result = segments1[i].compareTo(segments2[i]);
      }

      if (result != 0) {
        return result;
      }
    }

    return segments1.length - segments2.length;
  }
}
