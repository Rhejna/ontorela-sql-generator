package configuration;

import ca.griis.gen.configuration.ConfigurationLoader;
import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.configuration.DbUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DbUtilityTest {
  // **************************************************************************
  // Attributs spécifiques
  public static String configFilePath1 = "test-data/configuration/config.yaml";
  public static String configFilePath2 = "test-data/configuration/configBadCon.yaml";

  //
  // **************************************************************************
  // Constructeurs
  //
  @BeforeAll
  public static void initTest() {
  }

  // **************************************************************************
  // Cas de tests
  //
  @Test
  public void testGetConnectionConfig() {
    DatabaseConfig config = ConfigurationLoader.loadConfiguration(configFilePath1);
    assertNotNull(config);
    assertNotNull(DbUtility.getConnection(config));
  }

  @Test
  public void testGetConnectionFile() {
    File configFile = new File(configFilePath1);
    assertNotNull(DbUtility.getConnection(configFile));
  }

  @Test
  public void testGetConnectionException() {
    // TODO 2022-08-24 : trouver comment lancer une erreur SQL et non pas Runtime. Voir le code source.
    File configFile2 = new File(configFilePath2);
    Exception exception2 = assertThrows(RuntimeException.class, () -> {
      DbUtility.getConnection(configFile2);
    });
    assertNotNull(exception2);
  }

  @Test
  public void testGetSchemaDescription() throws SQLException {
    DatabaseConfig config = ConfigurationLoader.loadConfiguration(configFilePath1);
    assertNotNull(config);
    String desc = DbUtility.getSchemaDescription(config.getSrcServer().get(0).getSchemaId(), config);
    System.out.println(desc);
    assertNotNull(desc);
  }
}
