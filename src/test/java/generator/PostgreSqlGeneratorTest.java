package generator;

import ca.griis.gen.process.BuildSchema;
import ca.griis.gen.generator.PostgreSqlGenerator;
import ca.griis.gen.configuration.ConfigurationLoader;
import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.model.SchemaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PostgreSqlGeneratorTest {
    public static String configFilePath = "test-data/configuration/config.yaml";
    private static PostgreSqlGenerator gen;
    DatabaseConfig config;

    //
    // **************************************************************************
    // Constructeurs
    //
    @BeforeEach
    public void initTest() {
        config = ConfigurationLoader.loadConfiguration(configFilePath);
        assertNotNull(config);
        BuildSchema builder = new BuildSchema(config);
        SchemaModel outSchema = builder.getSchema();
        gen = new PostgreSqlGenerator(outSchema, config);
        assertNotNull(gen);
    }
    
    // **************************************************************************
    // Cas de tests
    //
    @Test
    public void testCreateSchemaSqlFile() {
        File file = gen.generateCreateSchemaScript();
        assertNotNull(file);
    }

    @Test
    public void testCreateDomainSqlFile() {
        File file = gen.generateCreateDomainScript();
        assertNotNull(file);
    }

    @Test
    public void testCreateTableSqlFile() {
        File file = gen.generateCreateTableScript();
        assertNotNull(file);
    }

    @Test
    public void testCreateDropSqlFile() {
        File file = gen.generateDropTableScript();
        assertNotNull(file);
    }

    @Test
    public void testCreateEmiraSqlFile(){
        List<File> files = gen.generateCreateEMIRAScripts();
        assertNotNull(files);
        assertEquals(2, files.size());
    }

}
