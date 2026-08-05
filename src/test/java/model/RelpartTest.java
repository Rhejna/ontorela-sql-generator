package model;

import ca.griis.gen.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RelpartTest {

  // **************************************************************************
  // Attributs spécifiques
  //
  private Relpart pVbx;
  private Relvar r0;
  private Set<Attribute> keys0;
  private Set<Attribute> atts0;

  // **************************************************************************
  // Constructeurs
  //
  @BeforeEach
  public void iniTest() {
    // Les attributes
    Attribute a0 = new Attribute("a0", new BuiltInType("INT"), false, null);
    Attribute a1 = new Attribute("a1", new DefinedType("D0", new BuiltInType("CHAR")), true, null);
    Attribute a2 = new Attribute("a2", new BuiltInType("DATE"), false, null);
    //
    atts0 = new LinkedHashSet<>(List.of(a0, a1, a2));
    keys0 = new LinkedHashSet<>(List.of(a0));
    r0 = new Relvar("R0", RelationCategory.V, atts0, keys0);
    //
    pVbx = new Relpart(r0, PeriodCategory.ValidTimeCategory.Vbx);
  }

  // **************************************************************************
  // Cas de tests
  //
  @Test
  public void testGetId() {
    assertTrue(pVbx.getId().equals(r0.getId() + "_" + pVbx.getTimelineCategory().getIdentifier()));
  }

  @Test
  public void testGetRelvar() {
    assertTrue(pVbx.getRelvar().equals(r0));
  }

  @Test
  public void testGetTimelineCategory() {
    assertTrue(pVbx.getTimelineCategory().equals(PeriodCategory.ValidTimeCategory.Vbx));
  }

  @Test
  public void testGetRelpartAttribute() {
    System.out.println(pVbx.getRelpartAttributeSet());
    //
    assertTrue(pVbx.getRelpartAttributeSet().size() == 2);
  }

  @Test
  public void testGetTemporalAttributeSet() {
    assertTrue(pVbx.getTemporalAttributeSet(true).size() == 1);
    assertTrue(pVbx.getTemporalAttributeSet(false).size() == 0);
  }

  @Test
  public void testGetKeyAttributeSet() {
    System.out.println(pVbx.getKeyAttributeSet(true));
    System.out.println(pVbx.getKeyAttributeSet(false));
    //
    assertEquals(1, pVbx.getKeyAttributeSet(true).size());
    assertEquals(1, pVbx.getKeyAttributeSet(false).size());
  }

  @Test
  public void testGetRelpartAttributeSet() {
    System.out.println(pVbx.getRelpartAttributeSet());
    System.out.println(pVbx.getRelpartAttributeSet(true));
    //
    assertEquals(2, pVbx.getRelpartAttributeSet().size());
    assertEquals(2, pVbx.getRelpartAttributeSet(true).size());
  }

  @Test
  public void testIsKeyRelpart() {
    assertTrue(pVbx.isKeyRelpart());
  }

  @Test
  public void testEqualsObject() {
    assertTrue(pVbx.equals(pVbx));
  }
  // **************************************************************************
  // Opérations propres
  //
}
