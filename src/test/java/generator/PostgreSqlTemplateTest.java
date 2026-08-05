package generator;

import ca.griis.gen.generator.PostgreSqlTemplate;
import ca.griis.gen.configuration.ConfigurationLoader;
import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.configuration.DbUtility;
import ca.griis.gen.model.Attribute;
import ca.griis.gen.model.BuiltInType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PostgreSqlTemplateTest {

    PostgreSqlTemplate template;
    DatabaseConfig config;

    //
    // **************************************************************************
    // Constructeurs
    //
    @BeforeEach
    public void initTest() {
        String configFilePath = "test-data/configuration/config.yaml";
        config = ConfigurationLoader.loadConfiguration(configFilePath);
        template = new PostgreSqlTemplate();
        assertNotNull(template);
    }

    // **************************************************************************
    // Cas de tests
    //
    @Test
    public void testCreateTemporalExtensions() {
        String code = template.createTemporalExtensions();
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateSchema() {
        String code = template.createSchema("s0");
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateSchemaComment() {
        String code = template.createSchemaComment("s0", "Test comment schema");
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testDropSchema() {
        String code = template.dropSchema("s0", false);
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateDomain() {
        String code0 = template.createDomain("s0", "t0", "INT", true, true);
        String code1 = template.createDomain("s0", "t1", "INT", true, false);
        String code2 = template.createDomain("s0", "t1", "t0", false, false);
        //
        System.out.println(code0);
        sqlSyntaxChecker(code0, true);
        System.out.println(code1);
        sqlSyntaxChecker(code1);
        System.out.println(code2);
        sqlSyntaxChecker(code2);
        //
        assertNotNull(code0);
        assertFalse(code0.isEmpty());
        assertNotNull(code1);
        assertFalse(code1.isEmpty());
        assertNotNull(code2);
        assertFalse(code2.isEmpty());
    }

    @Test
    public void testDropDomain() {
        String code = template.dropDomain("s0", "t0", true);
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateDomainComment() {
        String code0 = template.createDomainComment("s0", "t0", "t0 is a INT");
        System.out.println(code0);
        sqlSyntaxChecker(code0);

        assertNotNull(code0);
        assertFalse(code0.isEmpty());
    }



    @Test
    public void testCreateTable() {
        // Les attributes
        Attribute a0 = new Attribute("a0", new BuiltInType("INT"), false, null);
        Attribute a1 = new Attribute("a1", new BuiltInType("CHAR"), true, null);
        Attribute a2 = new Attribute("a2", new BuiltInType("DATE"), false, null);
        Set<Attribute> attSet = new LinkedHashSet<>(Arrays.asList(a0, a1, a2));
        //
        Set<Attribute> keySet = new LinkedHashSet<>(List.of(a0));
        String code = template.createTable("s0", "r0", attSet, "r0_pk0", keySet);
        //
        keySet = new LinkedHashSet<>(Arrays.asList(a0, a2));
        String code1 = template.createTable("s0", "r0", attSet, "r0_cc0", keySet);
        //
        System.out.println(code);
        System.out.println(code1);
        sqlSyntaxChecker(code, true);
        sqlSyntaxChecker(code1, true);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateTableComment() {

        String code = template.createTableComment("s0", "r0", "Test comment table");
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCommentAttribute() {
        String code = template.createAttributeComment("s0", "r0", "a0", "Test comment atribut");
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testDropTable() {
        String code = template.dropTable("s0", "r0");
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateNamingView() {
        Map<String, String> attSet = new HashMap<>() {
            {
                put("a0", "a0 fr");
                put("a1", "a1 fr");
                put("a2", "a2 fr");
            }
        };
        String code = template.createNamingView("s0", "v0_fr", "s0", "r0", attSet);
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCommentView() {
        String code = template.createViewComment("s0", "v0_fr", "Test comment view");
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testDropView() {
        String code = template.dropView("s0", "v0_fr");
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateUniqueConstraint() {
        Set<String> keySet = new LinkedHashSet<String>(Arrays.asList("a1", "a2"));
        //String code = SqlTemplate.createUniqueConstraint("s0", "r0_cc1", "r0", keySet);
        //
//        System.out.println(code);
//        //
//        assertNotNull(code);
//        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateForeignKey() {
        Set<String> srcTableAttSet = new LinkedHashSet<String>(Arrays.asList("a1"));
        Set<String> dstTableAttSet = new LinkedHashSet<String>(Arrays.asList("a1"));
        String code = template.createForeignKeyConstraint("r0_cr0", "s0", "r0", srcTableAttSet, "s0", "r1",
                dstTableAttSet);
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCommentForeignKey() {
        //String code = SqlTemplate.createCommentConstraint("r0_cr0", "s0", "r0", "Test comments FK");
        //
//        System.out.println(code);
//        //
//        assertNotNull(code);
//        assertFalse(code.isEmpty());
    }

//    @Test
//    public void testCreateMinParticipationCheckFunction() {
//        String code = SqlTemplate.createMinParticipationCheckFunction("s0", "r0_participation", "r0", "a0", "INT", 2);
//        //
//        System.out.println(code);
//        //
//        assertNotNull(code);
//        assertFalse(code.isEmpty());
//    }
//
//    @Test
//    public void testCreateMaxParticipationCheckFunction() {
//        String code = SqlTemplate.createMaxParticipationCheckFunction("s0", "r0_participation", "r0", "a0", "INT", 4);
//        //
//        System.out.println(code);
//        //
//        assertNotNull(code);
//        assertFalse(code.isEmpty());
//    }


    @Test
    public void testDeleteTable() {
        String code = template.delete("s0", "r0");
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateScriptHeader() {
        String code = template.createScriptHeader("POSTGRES", "s0", "2020-03-25", "UHF", "v1.0.0",
                "Test header creation");
        //
        System.out.println(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testSelectTableAttribut() {
        // Les attributes
        Attribute a0 = new Attribute("a0", new BuiltInType("INT"), false, null);
        Attribute a1 = new Attribute("a1", new BuiltInType("CHAR"), true, null);
        Attribute a2 = new Attribute("a2", new BuiltInType("DATE"), false, null);
        Set<Attribute> attSet = new LinkedHashSet<>(Arrays.asList(a0, a1, a2));

        String code = template.selectTableAttribut("s0", "r0", a0, attSet);
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testUpdateTableAttribut() {
        // Les attributes
        Attribute a0 = new Attribute("a0", new BuiltInType("INT"), false, null);
        Attribute a1 = new Attribute("a1", new BuiltInType("CHAR"), true, null);
        Attribute a2 = new Attribute("a2", new BuiltInType("DATE"), false, null);
        Set<Attribute> attSet = new LinkedHashSet<>(Arrays.asList(a1, a2));
        //
        Set<Attribute> keySet = new LinkedHashSet<>(List.of(a0));

        String code = template.updateTableAttribut("s0", "r0", a1, keySet);
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }
//insertTable
    @Test
    public void testInsertTable() {
        // Les attributes
        Attribute a0 = new Attribute("a0", new BuiltInType("INT"), false, null);
        Attribute a1 = new Attribute("a1", new BuiltInType("CHAR"), true, null);
        Attribute a2 = new Attribute("a2", new BuiltInType("DATE"), false, null);
        Set<Attribute> attSet = new LinkedHashSet<>(Arrays.asList(a0, a1, a2));

        String code = template.insertTable("s0", "r0", attSet);
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testDeleteTableProcedure() {
        // Les attributes
        Attribute a0 = new Attribute("a0", new BuiltInType("INT"), false, null);
        Attribute a1 = new Attribute("a1", new BuiltInType("CHAR"), true, null);
        Attribute a2 = new Attribute("a2", new BuiltInType("DATE"), false, null);
        //
        Set<Attribute> keySet = new LinkedHashSet<>(List.of(a0, a1));

        String code = template.deleteTableProcedure("s0", "r0", keySet);
        //
        System.out.println(code);
        sqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    public void sqlSyntaxChecker(String sql) {
        sqlSyntaxChecker(sql, false);
    }

    public void sqlSyntaxChecker(String sql, boolean isCommit) {
        try (Connection conn = DbUtility.getConnection(config)) {
            try (Statement stmt = conn.createStatement()) {
                // Begin the transaction
                conn.setAutoCommit(isCommit);
                // Test the CREATE type statement without actually creating the table
                stmt.execute(sql);
                System.out.println("sql statement syntax is valid");

                if (!isCommit) {
                    // Rollback the transaction
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
