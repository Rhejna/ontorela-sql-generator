package model;

import ca.griis.gen.model.PeriodCategory.BitemporalVTCategory;
import ca.griis.gen.model.PeriodCategory.TransactionTimeCategory;
import ca.griis.gen.model.PeriodCategory.ValidTimeCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimelineCategoryTest {

  @Test
  public void testValidTimeCategory() {
    System.out.println("Test ValidTimeCategory ");
    for (ValidTimeCategory s : ValidTimeCategory.values()) {
      s.getIdentifier();
      //
      System.out.println(s.getIdentifier() + " " + s.getPostgreSQLAttribute());
      //
      assertTrue(ValidTimeCategory.values().length == 4);
      assertNotNull(s.getIdentifier());
      assertNotNull(s.getPostgreSQLAttribute());
    }
  }

  @Test
  public void testTransactionTimeCategory() {
    System.out.println("Test TransactionTimeCategory ");
    for (TransactionTimeCategory s : TransactionTimeCategory.values()) {
      s.getIdentifier();
      //
      System.out.println(s.getIdentifier() + " " + s.getPostgreSQLAttribute());
      //
      assertTrue(TransactionTimeCategory.values().length == 2);
      assertNotNull(s.getIdentifier());
      assertNotNull(s.getPostgreSQLAttribute());
    }
  }

  @Test
  public void testBitemporalVTCategory() {
    System.out.println("Test BitemporalVTCategory ");
    for (BitemporalVTCategory s : BitemporalVTCategory.values()) {
      s.getIdentifier();
      //
      assertTrue(BitemporalVTCategory.values().length == TransactionTimeCategory.values().length
          * ValidTimeCategory.values().length);
      System.out.println(s.getIdentifier() + " " + s.getPostgreSQLAttribute());
      //
      assertNotNull(s.getIdentifier());
      assertNotNull(s.getPostgreSQLAttribute());


    }
  }
}
