package ca.griis.gen.configuration;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ConfigurationLoader {
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
  public static File getConfigurationFile(String path) {
    return new File(path);
  }

  public static DatabaseConfig loadOnlyConfiguration(String configFilePath) {
    DatabaseConfig dbConfig = loadConfiguration(getConfigurationFile(configFilePath));
    return dbConfig;
  }

  public static DatabaseConfig loadConfiguration(String configFilePath) {
    DatabaseConfig dbConfig = loadConfiguration(getConfigurationFile(configFilePath));
    DbUtility.getConnection(dbConfig);
    return dbConfig;
  }

  /**
   * @param configFile Le fichier de configuration de la base de données.
   * @return La configuration de la base de données
   */
  public static DatabaseConfig loadConfiguration(File configFile) {
    System.out.println(" >> Loading configuration file: " + configFile.getAbsolutePath());
    DatabaseConfig config = null;
    //
    try {
      InputStream input = new FileInputStream(configFile);
      Constructor constructor = new Constructor(DatabaseConfig.class);
      Yaml yaml = new Yaml(constructor);
      config = yaml.load(input);
    } catch (FileNotFoundException e) {
      System.err.println(" Configuration file not found on :" + configFile.getAbsolutePath());
    }
    //
    return config;
  }
}
