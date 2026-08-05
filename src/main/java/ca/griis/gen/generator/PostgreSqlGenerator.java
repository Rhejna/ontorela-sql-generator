package ca.griis.gen.generator;

import ca.griis.gen.process.SqlExecutor;
import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.model.Relvar;
import ca.griis.gen.model.SchemaModel;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgreSqlGenerator extends SqlGenerator {

    protected PostgreSqlTemplate sqlTemplate;

    /**
     * default constructor
     *
     * @param schema : name of the database schema
     */
    public PostgreSqlGenerator(SchemaModel schema, DatabaseConfig config) {
        super(schema, config, new PostgreSqlTemplate());
        sqlTemplate = new PostgreSqlTemplate();
    }

    public String getRDBMS() {
        return "POSTGRESQL";
    }

    // **************************************************************************
    // Opérations publiques générales
    //

    /**
     * Génération du script SQL de création des schémas.
     *
     * @return Un script SQL qui contient les instructions de création des schémas
     */
    public File generateCreateSchemaScript() {
        StringBuilder content = new StringBuilder();
        //
        content.append(sqlTemplate.createTemporalExtensions());
        //
        String schemaId = this.getSchema().getSchemaId();
        String com = "Schéma " + schemaId + " créé le " + this.getCurrentDate();
        content.append(sqlTemplate.createSchema(schemaId));
        content.append(sqlTemplate.createSchemaComment(schemaId, com));
        //
        String subject = "Create the schema";
        String filePrefix = "000-schema_cre";
        File script = generateScript(filePrefix, subject, content.toString());
        //
        if (this.config.getExecuteScript()) {
            try{
                SqlExecutor.executeScript(script, this.config);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return script;
    }

    /**
     * Génération du script SQL de création des domains.
     *
     * @return Un script SQL qui contient les instructions de création des domains
     */
    public File generateCreateDomainScript() {
        //
        String subject = "Create utility types";
        String filePrefix = "001-type_cre";
        File script = generateScript(filePrefix, subject, generateCreateDomainStatements());
        //
        if (this.config.getExecuteScript()) {
            try{
                SqlExecutor.executeScript(script, this.config);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return script;
    }

    /**
     * Génération du script SQL de création des tables
     *
     * @return Un script SQL qui contient les instructions de création des tables.
     */
    public File generateCreateTableScript() {
        //
        String subject = "Create tables";
        String filePrefix = "110-table_cre";
        return this.generateScript(filePrefix, subject, generateCreateTableStatements());
    }

    /**
     * Génération du script SQL de suppression des types définis et des tables.
     *
     * @return Un script SQL qui contient les instructions de suppression des types définis et des
     * tables.
     */
    public File generateDropTableScript() {
        //
        String content = generateDropTableStatements() +
                         generateDropDomainStatements();
        //
        String subject = "Create domains and tables";
        String filePrefix = "999-table_drp";
        return generateScript(filePrefix, subject, content);
    }

    /**
     * Génération du script SQL de création des tables.
     * @return Un script SQL qui contient les instructions de création des tables.
     */
    public List<File> generateCreateEMIRAScripts() {
        List<File> scripts = new ArrayList<>();

        for (Relvar t : this.getSchema().getRelvarSet()) {
            //
            String content = generateCreateSelectStatement(t) +
                    generateCreateUpdateStatement(t) +
                    generateCreateInsertStatement(t) +
                    generateCreateDeleteStatement(t);
            //
            String subject = "Emira operations for table" + t.getId();
            String filePrefix = t.getId() + "_emira";
            File script = this.generateScript(filePrefix, subject, content);
            scripts.add(script);
        }
        return scripts;
    }


}
