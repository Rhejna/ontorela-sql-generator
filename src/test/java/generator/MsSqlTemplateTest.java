package generator;

import ca.griis.gen.generator.MSSqlTemplate;
import ca.griis.gen.configuration.ConfigurationLoader;
import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.model.Attribute;
import ca.griis.gen.model.BuiltInType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MsSqlTemplateTest {
    MSSqlTemplate template;
    DatabaseConfig config;

    //
    // **************************************************************************
    // Constructeurs
    //
    @BeforeEach
    public void initTest() {
        String configFilePath = "test-data/configuration/configMsSql.yaml";
        config = ConfigurationLoader.loadOnlyConfiguration(configFilePath);
        template = new MSSqlTemplate();
        assertNotNull(template);
    }

    // **************************************************************************
    // Cas de tests
    //
    @Test
    public void testCreateSchema() {
        String code = template.createSchema("s0");
        //
        System.out.println(code);
        mssqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateSchemaComment() {
        String code = template.createSchemaComment("s0", "Test comment schema");
        //
        System.out.println(code);
        mssqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testDropSchema() {
        String code = template.dropSchema("s0", false);
        //
        System.out.println(code);
        mssqlSyntaxChecker(code);
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
        mssqlSyntaxChecker(code0, true);
        System.out.println(code1);
        mssqlSyntaxChecker(code1);
        System.out.println(code2);
        mssqlSyntaxChecker(code2);
        //
        assertNotNull(code0);
        assertFalse(code0.isEmpty());
        assertNotNull(code1);
        assertFalse(code1.isEmpty());
        assertNotNull(code2);
        assertFalse(code2.isEmpty());
    }

    @Test
    public void testCreateDomainComment(){
        String code0 = template.createDomainComment("s0", "t0", "t0 is a INT");
        System.out.println(code0);
        mssqlSyntaxChecker(code0);

        assertNotNull(code0);
        assertFalse(code0.isEmpty());
    }

    @Test
    public void testDropDomain() {
        String code = template.dropDomain("s0", "t0");
        //
        System.out.println(code);
        mssqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
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
        code += template.createTableComment("s0", "r0", "r0 " + attSet);
        //
        System.out.println(code);
        mssqlSyntaxChecker(code, true);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCommentAttribute() {
        String code = template.createAttributeComment("s0", "r0", "a0", "Test comment atribut");
        //
        System.out.println(code);
        mssqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testDropTable() {
        String code = template.dropTable("s0", "r0");
        //
        System.out.println(code);
        mssqlSyntaxChecker(code);
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
        mssqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCommentView() {
        String code = template.createViewComment("s0", "v0_fr", "Test comment view");
        //
        System.out.println(code);
        mssqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testDropView() {
        String code = template.dropView("s0", "v0_fr");
        //
        System.out.println(code);
        mssqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    @Test
    public void testCreateForeignKey() {
        Set<String> srcTableAttSet = new LinkedHashSet<String>(Arrays.asList("a1"));
        Set<String> dstTableAttSet = new LinkedHashSet<String>(Arrays.asList("a1"));
        String code = template.createForeignKeyConstraint("r0_cr0", "s0", "r0", srcTableAttSet, "s0", "r1",
                dstTableAttSet);
        //
        System.out.println(code);
        mssqlSyntaxChecker(code);
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
        mssqlSyntaxChecker(code);
        //
        assertNotNull(code);
        assertFalse(code.isEmpty());
    }

    public void mssqlSyntaxChecker(String sql) {
        mssqlSyntaxChecker(sql, false);
    }

    public void mssqlSyntaxChecker(String sql, boolean isCommit) {
//        try (Connection conn = DbUtility.getConnection(config)) {
//            try (Statement stmt = conn.createStatement()) {
//
//                // Begin the transaction
//                conn.setAutoCommit(isCommit);
//
//                // Test the CREATE type statement without actually creating the table
//                stmt.execute(sql);
//                System.out.println("sql statement syntax is valid");
//
//                if (!isCommit) {
//                    // Rollback the transaction
//                    conn.rollback();
//                }
//                System.out.println("Transaction rolled back");
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

    }
}