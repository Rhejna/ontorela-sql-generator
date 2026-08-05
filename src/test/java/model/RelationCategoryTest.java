package model;

import ca.griis.gen.model.RelationCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RelationCategoryTest {

  @Test
  public void test() {
    for (RelationCategory s : RelationCategory.values()) {
      s.getIdentifier();
      System.out.println(s.getIdentifier());
      System.out.println(s.getFullName());
      assertNotNull(s.getIdentifier());
      assertNotNull(s.getFullName());
    }
  }
}
