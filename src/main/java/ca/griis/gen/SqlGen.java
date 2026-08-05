package ca.griis.gen;

import ca.griis.gen.configuration.ConfigurationLoader;
import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.generator.MSSqlGenerator;
import ca.griis.gen.generator.PostgreSqlGenerator;
import ca.griis.gen.generator.SqlGenerator;
import ca.griis.gen.process.BuildSchema;


public class SqlGen {

    public static void main(String[] args) {
        // ==== Récupération des arguments
        String configFilePath = args[0];
        // ==== Construction du schéma de base ====
        DatabaseConfig config = ConfigurationLoader.loadConfiguration(configFilePath);
        BuildSchema buildSchema = new BuildSchema(config);

        SqlGenerator generator;
        if (config.getSrcServer().get(0).getRdbms().equals("mssql")) {
            generator = new MSSqlGenerator(buildSchema.getSchema(), config);
            generator.generateCreateSchemaScript();
            generator.generateCreateTableScript();
        }
        else if (config.getSrcServer().get(0).getRdbms().equals("postgresql")) {
            generator = new PostgreSqlGenerator(buildSchema.getSchema(), config);
            generator.generateSQLScripts();
        }

        System.out.println(">> Schéma UHF generé: " );
    }
}