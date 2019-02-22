package com.github.pukkaone.grapid.core.apichange;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.pukkaone.grapid.core.Version;
import com.github.pukkaone.grapid.core.apichange.v2019_01_01.Author;
import com.github.pukkaone.grapid.core.apichange.v2019_01_01.AuthorInput;
import com.github.pukkaone.grapid.core.apichange.v2019_01_01.Book;
import com.github.pukkaone.grapid.core.apichange.v2019_01_01.BookInput;
import com.github.pukkaone.grapid.core.apichange.v2019_01_01.BookInputPriceChange;
import com.github.pukkaone.grapid.core.apichange.v2019_01_01.BookPriceChange;
import com.github.pukkaone.grapid.core.apichange.v2019_01_01.Meal;
import com.github.pukkaone.grapid.core.apichange.v2019_01_01.MealSecondBreakfastChange;
import com.github.pukkaone.grapid.core.apichange.v2019_01_01.Offer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests data conversion between API versions.
 */
class VersionHistoryTest {

  private static final String NAME = "NAME";
  private static final String TITLE = "TITLE";
  private static final BigDecimal PRICE = new BigDecimal("12.34");

  private Version version2018 = new Version("v2018_12_31", 0);
  private VersionChanges versionChanges2018 = new VersionChanges(
      version2018, List.of(), Map.of(), Map.of(), Map.of(), Map.of());
  private Version version2019 = new Version("v2019_01_01", 1);
  private ChangeDescription<Book> bookPriceChange = new BookPriceChange();
  private VersionChanges versionChanges2019 = new VersionChanges(
      version2019,
      List.of(
          new MealSecondBreakfastChange(),
          new BookInputPriceChange(),
          bookPriceChange),
      Map.of(
          com.github.pukkaone.grapid.core.apichange.v2018_12_31.Meal.class,
          Meal::valueOf),
      Map.of(
          Meal.class,
          com.github.pukkaone.grapid.core.apichange.v2018_12_31.Meal::valueOf),
      Map.of(
          com.github.pukkaone.grapid.core.apichange.v2018_12_31.AuthorInput.class,
          AuthorInput::new,
          com.github.pukkaone.grapid.core.apichange.v2018_12_31.BookInput.class,
          BookInput::new),
      Map.of(
          Author.class,
          com.github.pukkaone.grapid.core.apichange.v2018_12_31.Author::new,
          Book.class,
          com.github.pukkaone.grapid.core.apichange.v2018_12_31.Book::new));
  private VersionHistory versionHistory = new VersionHistory(
      List.of(versionChanges2018, versionChanges2019));

  @Test
  void should_upgrade_enum_constant() {
    var actual = versionHistory.upgradeEnum(
        com.github.pukkaone.grapid.core.apichange.v2018_12_31.Meal.BREAKFAST, version2018);
    assertThat(actual).isEqualTo(Meal.BREAKFAST);
  }

  @Test
  void should_downgrade_enum_constant() {
    var actual = versionHistory.downgradeEnum(Meal.SECOND_BREAKFAST, version2018);
    assertThat(actual).isEqualTo(
        com.github.pukkaone.grapid.core.apichange.v2018_12_31.Meal.BREAKFAST);
  }

  @Test
  void should_upgrade_input() {
    var bookInput2018 = new com.github.pukkaone.grapid.core.apichange.v2018_12_31.BookInput();
    bookInput2018.setTitle(TITLE);
    bookInput2018.setPrice(PRICE);

    BookInput bookInput2019 = versionHistory.upgradeInput(bookInput2018, version2018);
    assertThat(bookInput2019.getTitle()).isEqualTo(TITLE);
    assertThat(bookInput2019.getOffer().getPrice()).isEqualTo(PRICE);
  }

  @Test
  void should_downgrade_object() {
    Offer offer = new Offer();
    offer.setPrice(PRICE);

    var book2019 = new Book();
    book2019.setTitle(TITLE);
    book2019.setOffer(offer);

    com.github.pukkaone.grapid.core.apichange.v2018_12_31.Book book2018 =
        versionHistory.downgradeObject(book2019, version2018);
    assertThat(book2018.getTitle()).isEqualTo(TITLE);
    assertThat(book2018.getPrice()).isEqualTo(PRICE);
  }

  @Test
  void when_processing_old_version_then_change_is_not_active() {
    boolean active = versionHistory.isActive(bookPriceChange, version2018);
    assertThat(active).isFalse();
  }

  @Test
  void when_processing_new_version_then_change_is_active() {
    boolean active = versionHistory.isActive(bookPriceChange, version2019);
    assertThat(active).isTrue();
  }
}
