package model;


import ca.griis.gen.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RelvarTest {

  // **************************************************************************
  // Attributs spécifiques
  //
  private Relvar r0;
  private Attribute a0;
  private Attribute a1;
  private Attribute a2;
  private Set<Attribute> keys0;
  private Set<Attribute> atts0;
  private Relpart r0_keyVbe;
  private Relpart r0_keyVbx;
  private Relpart r0_a1Vbe;
  private Relpart r0_a1Vbx;
  //
  // private Relvar r1;
  // private Relvar r2;
  // private Relvar r3;

  // **************************************************************************
  // Constructeurs
  //
  @BeforeEach
  public void iniTest() {
    // Les attributes
    a0 = new Attribute("a0", new BuiltInType("INT"), false, null);
    a1 = new Attribute("a1", new DefinedType("D0", new BuiltInType("CHAR")), true, null);
    a2 = new Attribute("a2", new BuiltInType("DATE"), false, null);
    //
    atts0 = new LinkedHashSet<>(Arrays.asList(a0, a1, a2));
    keys0 = new LinkedHashSet<>(Arrays.asList(a0));
    r0 = new Relvar("R0", RelationCategory.V, atts0, keys0);
    //
    r0_keyVbe = new Relpart(r0, PeriodCategory.ValidTimeCategory.Vbe);
    r0.addRepart(r0_keyVbe);
    r0_keyVbx = new Relpart(r0, PeriodCategory.ValidTimeCategory.Vbx);
    r0.addRepart(r0_keyVbx);
    r0_a1Vbe = new Relpart(r0, PeriodCategory.ValidTimeCategory.Vbe, a1);
    r0.addRepart(r0_a1Vbe);
    r0_a1Vbx = new Relpart(r0, PeriodCategory.ValidTimeCategory.Vbx, a1);
    r0.addRepart(r0_a1Vbx);

  }

  // **************************************************************************
  // Cas de test
  //
  @Test
  public void testRelvarFullConstructor() {
    Relvar rel = new Relvar(r0.getId(), r0.getCategory(), atts0, keys0, r0.getRelpartSet());
    assertNotNull(rel);
    System.out.println(rel.toString());
  }


  @Test
  public void testRelvarPartialConstructor() {
    Relvar rel = new Relvar(r0.getId(), r0.getCategory());
    assertNotNull(rel);
    System.out.println(rel.toString());
  }

  @Test
  public void testAddAttributeSet() {
    Relvar newRelvar = new Relvar("newRelvar", RelationCategory.V);
    Attribute newAtt = new Attribute("newAtt", new BuiltInType("ChAR"), false, null);
    newRelvar.addAttribute(newAtt);
    assertEquals(1, newRelvar.getAttributeSet().size());
  }

  @Test
  public void testAddAttributeSetParam() {
    Relvar newRelvar = new Relvar("newRelvar", RelationCategory.V);
    newRelvar.addAttribute("a0", new BuiltInType("INT"), false, null);
    assertEquals(1, newRelvar.getAttributeSet().size());
  }

  @Test
  public void testSetAttributSet() {
    Relvar newRelvar = new Relvar("newRelvar", RelationCategory.V);
    a0 = new Attribute("a0", new BuiltInType("INT"), false, null);
    a1 = new Attribute("a1", new DefinedType("D0", new BuiltInType("CHAR")), true, null);
    a2 = new Attribute("a2", new BuiltInType("DATE"), false, null);
    //
    atts0 = new LinkedHashSet<>(Arrays.asList(a0, a1, a2));
    newRelvar.setAttributeSet(atts0);
    assertEquals(3, newRelvar.getAttributeSet().size());
  }

  @Test
  public void testGetNoKeyAttributeSet() {
    assertTrue(r0.getNoKeyAttributeSet().size() == 2);
  }

  @Test
  public void testAddKeyAttributeSet() {
    Relvar newRelvar = new Relvar("newRelvar", RelationCategory.V);
    newRelvar.addKeyAttribute("newKeyAtt", new BuiltInType("INT"), false, null);
    assertEquals(1, newRelvar.getKeyAttributeSet().size());
  }

  //
  //  @Test
  //  public void testSetKeyAttributeSet() {
  //    fail("Not yet implemented");
  //  }
  //
  //  @Test
  //  public void testGetKeyAttributeSet() {
  //    fail("Not yet implemented");
  //  }
  //
  //  @Test
  //  public void testAddForeignKeySetStringMapOfStringString() {
  //    fail("Not yet implemented");
  //  }
  //
  //  @Test
  //  public void testAddForeignKeySetStringStringString() {
  //    fail("Not yet implemented");
  //  }
  //
  //  @Test
  //  public void testGetForeignKeySet() {
  //    fail("Not yet implemented");
  //  }
  //
  @Test
  public void testAddRelpartTimelineCategory() {
    r0.addRelpart(PeriodCategory.ValidTimeCategory.Vxe);
    assertTrue(r0.getKeyGrouping().size() == 3);
  }

  @Test
  public void testAddRelpartTimelineCategoryAttr() {
    r0.addRelpart(PeriodCategory.ValidTimeCategory.Vxe, a1);
    assertTrue(r0.getNoKeyGrouping().size() == 3);
  }
  //
  //  @Test
  //  public void testSetRelpartSet() {
  //    fail("Not yet implemented");
  //  }
  //
  //  @Test
  //  public void testGetRelpartSet() {
  //    fail("Not yet implemented");
  //  }

  @Test
  public void testGetKeyGrouping() {
    assertTrue(r0.getKeyGrouping().size() == 2);
    //
    System.out.println(r0.getKeyGrouping());
  }

  @Test
  public void testGetNoKeyGrouping() {
    assertTrue(r0.getNoKeyGrouping().size() == 2);
    //
    System.out.println(r0.getNoKeyGrouping());
  }

  @Test
  public void testGetAttributeGrouping() {
    assertTrue(r0.getAttributeGrouping(a1).size() == 2);
    //
    System.out.println(r0.getAttributeGrouping(a1));
  }
  // **************************************************************************
  // Opérations propres
  //
}
