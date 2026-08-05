package generator;

import ca.griis.gen.generator.UhfSqlTemplate;
import ca.griis.gen.model.AttributeString;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UhfSqlTemplateTest {

  // **************************************************************************
  // Attributs spécifiques
  //
  // **************************************************************************
  // Constructeurs
  //
  // **************************************************************************
  // Opérations propres
  //
  // **************************************************************************
  // Opérations publiques
  //
  @Test
  public void testSqlTemplate() {
    assertNotNull(UhfSqlTemplate.getInstance());
  }

  @Test
  public void testGetPeriodFunctions() {
    //
    String codeVxx = UhfSqlTemplate.getInstance().createVxxPeriodFunction("s0", "DATE", "DATERANGE", "daterange");
    String codeVxe = UhfSqlTemplate.getInstance().createVxxPeriodFunction("s0", "DATE", "DATERANGE", "daterange");
    String codeVbx = UhfSqlTemplate.getInstance().createVxxPeriodFunction("s0", "DATE", "DATERANGE", "daterange");
    //
    //
    assertNotNull(codeVxx);
    assertFalse(codeVxx.isEmpty());
    assertNotNull(codeVxe);
    assertFalse(codeVxe.isEmpty());
    assertNotNull(codeVbx);
    assertFalse(codeVbx.isEmpty());
  }

  @Test
  public void testGetSimpleQuery() {
    AttributeString
        k = new AttributeString("k", "cle", false);
    AttributeString
        a1 = new AttributeString("a1", "a1", false);
    AttributeString
        a2 = new AttributeString("a2", null, false);
    AttributeString
        vbx = new AttributeString("vbx(@vbx)", "v", true);
    Set<AttributeString> attSet = new LinkedHashSet<>(Arrays.asList(k, a1, a2));
    //
    String code = UhfSqlTemplate.getInstance().createSimpleQuery("s0", "r0", attSet, vbx);
    //
    System.out.println(code);
    //
    assertNotNull(code);
    assertFalse(code.isEmpty());
  }

  @Test
  public void testGetHistoryView() {
    AttributeString k = new AttributeString("k", "cle", false);
    AttributeString a1 = new AttributeString("a1", "a1", false);
    AttributeString a2 = new AttributeString("a2", null, false);
    AttributeString vxx = new AttributeString("vxx()", "v", true);
    AttributeString vxe = new AttributeString("vxe(@vxe)", "v", true);
    AttributeString vbx = new AttributeString("vbx(@vbx)", "v", true);
    AttributeString vbe = new AttributeString("@vbe", "v", true);
    Set<AttributeString> attSet = new LinkedHashSet<>(Arrays.asList(k, a1, a2));
    //
    String code1 = UhfSqlTemplate.getInstance().createHistoryView("s0", "ro_history", attSet, "ro@Vxx", vxx,
        "r0@Vxe", vxe, "r0@Vbx", vbx, "r0@Vbe", vbe);
    //
    System.out.println(code1);
    //
    assertNotNull(code1);
    assertFalse(code1.isEmpty());
  }

  @Test
  public void testCreateNoRedundancyConstraint() {
    Set<String> keyAttId = new LinkedHashSet<>(List.of("a0"));
    String code1 = UhfSqlTemplate.getInstance().createNoRedundancyConstraint("p0", "s0", "p0", keyAttId, null, true,
        "vbe");
    String code2 = UhfSqlTemplate.getInstance().createNoRedundancyConstraint("p0", "s0", "p0", keyAttId, null,
        false, null);
    String code3 = UhfSqlTemplate.getInstance().createNoRedundancyConstraint("p0", "s0", "p0", keyAttId, "a1", true,
        null);
    String code4 = UhfSqlTemplate.getInstance().createNoRedundancyConstraint("p0", "s0", "p0", keyAttId, "a1",
        false, null);
    //
    System.out.println(code1);
    System.out.println(code2);
    System.out.println(code3);
    System.out.println(code4);
    //
    assertNotNull(code1);
    assertFalse(code1.isEmpty());
    assertFalse(code2.isEmpty());
    assertFalse(code3.isEmpty());
  }

  @Test
  public void testCreateNoContradictionConstraint() {
    Set<String> keyAttId = new LinkedHashSet<>(List.of("a0"));
    String code1 = UhfSqlTemplate.getInstance().createNoContradictionConstraint("p0", "s0", "p0", keyAttId, null,
        true, "vbe");
    String code2 = UhfSqlTemplate.getInstance().createNoContradictionConstraint("p0", "s0", "p0", keyAttId, null,
        false, null);
    String code3 = UhfSqlTemplate.getInstance().createNoContradictionConstraint("p0", "s0", "p0", keyAttId, "a1",
        true, null);
    String code4 = UhfSqlTemplate.getInstance().createNoContradictionConstraint("p0", "s0", "p0", keyAttId, "a1",
        false, null);
    //
    assertNotNull(code1);
    assertFalse(code1.isEmpty());
    assertFalse(code2.isEmpty());
    assertFalse(code3.isEmpty());
    assertFalse(code4.isEmpty());
  }

  @Test
  public void testCreateNoCircumlocutionConstraint() {
    Set<String> keyAttId = new LinkedHashSet<>(List.of("a0"));
    String code1 = UhfSqlTemplate.getInstance().createNoCircumlocutionConstraint("p0", "s0", "p0", keyAttId, null,
        true, "vbe");
    String code2 = UhfSqlTemplate.getInstance().createNoCircumlocutionConstraint("p0", "s0", "p0", keyAttId, null,
        false, null);
    String code3 = UhfSqlTemplate.getInstance().createNoCircumlocutionConstraint("p0", "s0", "p0", keyAttId, "a1",
        true, null);
    String code4 = UhfSqlTemplate.getInstance().createNoCircumlocutionConstraint("p0", "s0", "p0", keyAttId, "a1",
        false, null);
    //
    //
    assertNotNull(code1);
    assertFalse(code1.isEmpty());
    assertFalse(code2.isEmpty());
    assertFalse(code3.isEmpty());
    assertFalse(code4.isEmpty());
  }

  @Test
  public void testCreateTemporalForeignKeyFct() {
    Map<String, String> keyAttMap = new LinkedHashMap<>();
    keyAttMap.put("r1_a1", "r0_k");
    //
    String code1 = UhfSqlTemplate.getInstance().createTemporalForeignKeyFct("r1_fk0", "s0", "r1", "r0", keyAttMap,
        "@V", "@V");
    keyAttMap.put("r1_a2", "r0_a2");
    String code2 = UhfSqlTemplate.getInstance().createTemporalForeignKeyFct("r1_fk0", "s0", "r1", "r0", keyAttMap,
        "@V", "@V");
    //
    System.out.println(code1);
    System.out.println(code2);
    //
    assertNotNull(code1);
    assertFalse(code1.isEmpty());
    assertNotNull(code2);
    assertFalse(code2.isEmpty());
  }

  @Test
  public void testCreateGroupingInsert() {
    AttributeString temporalAtt = new AttributeString("v", "PERIOD", false);
    AttributeString k = new AttributeString("k", "INT", true);
    Set<AttributeString> attSet = new LinkedHashSet<>(List.of(k));
    //
    String code = UhfSqlTemplate.getInstance().createGroupingInsert("s0", "r0_key", temporalAtt, attSet,
        "r0_key@Vxx", "r0_key@Vxe", "r0_key@Vbx", "r0_key@Vbe");
    //
    System.out.println(code);
    //
    assertNotNull(code);
    assertFalse(code.isEmpty());
  }

}
