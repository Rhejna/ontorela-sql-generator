package ca.griis.gen.process;

import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.configuration.DbUtility;
import org.postgresql.util.PSQLException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlExecutor {

    // **************************************************************************
    // Attributs spécifiques
    //
    // **************************************************************************
    // Constructeurs
    //
    // **************************************************************************
    // Opérations propre
    //

    // **************************************************************************
    // Opérations publiques
    //

    public static void executeScript(String sql, DatabaseConfig config) throws SQLException {
        executeScript(sql, config, false);
    }

    /**
     * Exécution une commande sql dans une BD
     *
     * @param sql    le fichier sql à exécuter
     * @param config la configuration de la BD
     */
    public static void executeScript(String sql, DatabaseConfig config, boolean isCommit) throws SQLException {
        try (Connection conn = DbUtility.getConnection(config)) {
            try (Statement stmt = conn.createStatement()) {
                // Begin the transaction
                conn.setAutoCommit(isCommit);
                System.out.println(" >> Executing : " + sql);
                // == Exécuter la commande sql
                stmt.execute(sql);
                System.out.println("sql statement syntax is valid");

                if (!isCommit) {
                    // Rollback the transaction
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                }
            }
        }
    }

    public static void executeScript(File sqlFile, DatabaseConfig config) throws SQLException {
        executeScript(sqlFile, config, false);
    }

    /**
     * Exécution un fichier sql dans une BD
     *
     * @param sqlFile le fichier sql à exécuter
     * @param config  la configuration de la BD
     */
    public static void executeScript(File sqlFile, DatabaseConfig config, boolean isCommit) throws SQLException {
        try {
            String sql = Files.readString(sqlFile.toPath());
            executeScript(sql, config, isCommit);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
