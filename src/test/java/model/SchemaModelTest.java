package model;

import ca.griis.gen.model.RelationCategory;
import ca.griis.gen.model.Relvar;
import ca.griis.gen.model.SchemaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchemaModelTest {

  // **************************************************************************
  // Attributs spécifiques
  //
  private static SchemaModel schemaModel;
  private static String schemaIdTest;
  private static Relvar r0;
  private static Relvar r1;

  // **************************************************************************
  // Constructeurs
  //
  @BeforeEach
  public void iniClass() {
    schemaIdTest = "public";
    schemaModel = new SchemaModel(schemaIdTest);
    assertNotNull(schemaModel);
    //
    r0 = new Relvar("r0", RelationCategory.V);
    r1 = new Relvar("r1", RelationCategory.V);
  }

  // **************************************************************************
  // Cas de test
  //
  @Test
  public void testSchemaId() {
    String schemaId = schemaModel.getSchemaId();
    assertTrue(schemaId.equals(schemaIdTest));
  }

  @Test
  public void testRelvarSet() {
    //
    schemaModel.addRelvar(r0);
    schemaModel.addRelvar(r1);
    //
    Set<Relvar> relvarSet = schemaModel.getRelvarSet();
    assertTrue(relvarSet.size() > 0);
    //
    for (Relvar r : relvarSet) {
      System.out.println(r);
    }
    //
  }

}
