package ca.griis.gen.configuration;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.*;
import schemacrawler.schemacrawler.*;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import static schemacrawler.tools.utility.SchemaCrawlerUtility.matchSchemaRetrievalOptions;

public class DbUtility {
    // **************************************************************************
    // Attributs spécifiques
    //
    private static Connection con;

    // **************************************************************************
    // Constructeurs
    //
    // **************************************************************************
    // Opérations propres
    //
    private static Connection getConnection(String url, String databaseId, String user, String pass) {

        try {
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.err.println(" >> /!\" Failed to connect to the database: " + url + "\n"
                    + e.getSQLState() + " : " + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println(" >> Connected to the database: " + url);
        return con;
    }

    private static DatabaseConnectionSource getDatabaseConnectionSource(DatabaseConfig dbConfig) {
        final DatabaseConnectionSource dataSource =
                DatabaseConnectionSources.newDatabaseConnectionSource(
                        dbConfig.getSrcServer().get(0).getHostUrl(),
                        new MultiUseUserCredentials(dbConfig.getSrcServer().get(0).getUser(),
                                dbConfig.getSrcServer().get(0).getPass()));
        return dataSource;
    }

    // **************************************************************************
    // Opérations publiques
    //
    public static Connection getConnection(DatabaseConfig dbConfig) {
        List<ServerProperties> serverConfig = dbConfig.getSrcServer();
        con = getConnection(serverConfig.get(0).getHostUrl(),
                serverConfig.get(0).getDatabaseId(),
                serverConfig.get(0).getUser(),
                serverConfig.get(0).getPass());
        return con;
    }

    public static Connection getConnection(File dbConfigFile) {
        DatabaseConfig dbConfig = ConfigurationLoader.loadConfiguration(dbConfigFile);
        return getConnection(dbConfig);
    }

    public static Catalog getCatalog(String schemaId, DatabaseConfig dbConfig) {
        // ==== Définir les options
        new LoggingConfig(Level.OFF);
        final LimitOptionsBuilder limitOptionsBuilder =
                LimitOptionsBuilder.builder()
                        .includeSchemas(SchemaFullName -> SchemaFullName.contains(schemaId));
        final LoadOptionsBuilder loadOptionsBuilder =
                LoadOptionsBuilder.builder()
                        // met quel détail sont requis dans le schéma - ceci affecte le
                        // temps pris à analyser le schéma.
                        .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
        final SchemaCrawlerOptions options =
                SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
                        .withLimitOptions(limitOptionsBuilder.toOptions())
                        .withLoadOptions(loadOptionsBuilder.toOptions());
        final DatabaseConnectionSource dbConnection = getDatabaseConnectionSource(dbConfig);
        return SchemaCrawlerUtility.getCatalog(dbConnection, options);
    }

    /**
     * Récupérer la description d'un schéma SQL.
     *
     * @param schemaId le nom du schéma
     * @return la description
     * @throws SQLException
     */
    public static String getSchemaDescription(String schemaId, DatabaseConfig dbConfig) throws SQLException {
        // === Construire le catalogue
        StringBuilder d = new StringBuilder();
        try {
            Catalog catalog = getCatalog(schemaId, dbConfig);
            // Vérifier l'existance du schéma
            Schema schema =
                    catalog.getSchemas().stream().filter(s -> s.getName().equals(schemaId)).findAny()
                            .orElse(null);
            if (schema != null) {
                // Récupération des informations
                for (final Table table : catalog.getTables(schema)) {
                    d.append(" TABLE " + table);
                    if (table instanceof View) {
                        d.append(" VIEW ");
                    }
                    d.append("\n");
                    // Récupérer les attributs
                    for (final Column column : table.getColumns()) {
                        d.append("  {" + column + " " + column.getColumnDataType().getName() + '('
                                + column.getSize() + ')' + "}");
                        d.append("\n");
                    }
                    // Récupérer les attributs clés
                    d.append("  KEY {" + table.getPrimaryKey().getConstrainedColumns() + "}");
                    d.append("\n");
                    // Récupérer les clés référentielles
                    for (ForeignKey fk : table.getForeignKeys()) {
                        d.append("  FOREIGN KEY {" + fk.getColumnReferences() + "}");
                        d.append("\n");
                    }
                }
            }
        } catch (SchemaCrawlerException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return d.toString();
    }

}
