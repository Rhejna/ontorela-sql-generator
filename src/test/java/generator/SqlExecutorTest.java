package generator;

import ca.griis.gen.configuration.ConfigurationLoader;
import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.process.SqlExecutor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SqlExecutorTest {

  private static DatabaseConfig config;

  // **************************************************************************
  // Constructeurs
  //
  @BeforeAll
  static void beforeAll() {
    String configFilePath = "test-data/example/supply/configV.yaml";
    config = ConfigurationLoader.loadOnlyConfiguration(configFilePath);
    assertNotNull(config);
  }

  // **************************************************************************
  // Cas de tests
  //
  @Test
  void testExecuteCommand() {
    String sql0 = "select current_user";
    assertDoesNotThrow(() -> SqlExecutor.executeScript(sql0, config));
    //
    String sql1 = "select * from supply.s";
    assertDoesNotThrow(() -> SqlExecutor.executeScript(sql1, config));
    //
    String sql2 = "create table test (i int)";
    assertDoesNotThrow(() -> SqlExecutor.executeScript(sql2, config ));
  }

  @Test
  void testExecuteScript() {
    File sql0 = new File("./test-data/SqlExecutor/commande-valid.sql");
    assertDoesNotThrow(() -> SqlExecutor.executeScript(sql0, config));
  }
}