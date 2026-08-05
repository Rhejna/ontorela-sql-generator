package configuration;

import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.configuration.ServerProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ca.griis.gen.configuration.ConfigurationLoader.loadConfiguration;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationLoaderTest {

  public static String configFilePath = "test-data/configuration/config.yaml";

  @Test
  public void testLoadConfiguration1() {
    DatabaseConfig config = loadConfiguration(configFilePath);
    assertNotNull(config);
    // ==
    assertNotNull(config.getSrcServer());
    List<ServerProperties> srvServer = config.getSrcServer();
    assertNotNull(config.getSrcServer().get(0).getRdbms());
    assertNotNull(config.getSrcServer().get(0).getHost());
    assertNotNull(config.getSrcServer().get(0).getPort());
    assertNotNull(config.getSrcServer().get(0).getUser());
    assertNotNull(config.getSrcServer().get(0).getPass());
    assertNotNull(config.getSrcServer().get(0).getDatabaseId());
    assertNotNull(config.getSrcServer().get(0).getSchemaId());
    assertNotNull(config.getSrcServer().get(0).getHostUrl());
    //
    assertNotNull(config.getAuthor());
    assertNotNull(config.getOutDirPath());
    assertNotNull(config.getVersion());
    assertNotNull(config.getTemporalIntervalType());
    assertNotNull(config.getTemporalPointType());
    assertNotNull(config.getIncludeVXX());
    assertNotNull(config.getGranularity());
    assertNotNull(config.getExecuteScript());
    // ==
    System.out.println(config);
  }
}
