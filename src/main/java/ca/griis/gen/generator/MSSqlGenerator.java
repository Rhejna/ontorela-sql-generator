package ca.griis.gen.generator;

import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.model.SchemaModel;

import java.io.File;
import java.util.List;

public class MSSqlGenerator extends SqlGenerator {

    protected MSSqlTemplate sqlTemplate;
    private final String RDBMS = "MSSQL";
    public MSSqlGenerator(SchemaModel schema, DatabaseConfig config) {
        super(schema, config, new MSSqlTemplate());
        this.sqlTemplate = new MSSqlTemplate();
    }

    public String getRDBMS() {
        return RDBMS;
    }

    @Override
    public File generateCreateSchemaScript() {
        StringBuilder content = new StringBuilder();
        //
        String schemaId = this.getSchema().getSchemaId();
        String com = "Schéma " + schemaId + " créé le " + this.getCurrentDate();
        content.append(sqlTemplate.createSchema(schemaId));
        content.append(sqlTemplate.createSchemaComment(schemaId, com));
        content.append("GO");
        //
        String subject = "Create the schema";
        String filePrefix = "000-schema_cre";
        File script = this.generateScript(filePrefix, subject, content.toString());
        //
        return script;
    }

    @Override
    public File generateCreateDomainScript() {
        return null;
    }

    /**
     * Génération du script SQL de création des tables
     *
     * @return Un script SQL qui contient les instructions de création des tables.
     */
    public File generateCreateTableScript() {
        StringBuilder content = new StringBuilder();
        //
        content.append(generateCreateTableStatements());
        //
        String subject = "Create tables";
        String filePrefix = "110-table_cre";
        File script = generateScript(filePrefix, subject, content.toString());
        return script;
    }

    @Override
    public File generateDropTableScript() {
        return null;
    }


// **************************************************************************
    // Opérations propre : TABLE
    //

    @Override
    public List<File> generateCreateEMIRAScripts() {
        //TODO : implement
        return null;
    }

}
