package ca.griis.gen.generator;

import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.configuration.ServerProperties;
import ca.griis.gen.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


public abstract class SqlGenerator extends Generator {

    // **************************************************************************
    // Attributs spécifiques
    //
    protected SqlTemplate sqlTemplate;
    protected final String currentDate;

    // **************************************************************************
    // Constructeurs
    //
    /**
     * default constructor
     *
     * @param schema : name of the dataabase schema
     */
    public SqlGenerator(SchemaModel schema, DatabaseConfig config, SqlTemplate sqlTemplate) {
        super(schema, config);
        DateFormat dateFormat = new SimpleDateFormat(("yyyyMMdd-HHmm"));
        Date date = new Date();
        this.currentDate = dateFormat.format((date));
        this.sqlTemplate = sqlTemplate;
    }


    // **************************************************************************
    // Opérations propres
    //

    public void generateSQLScripts() {
        generateCreateSchemaScript();                  //000
        generateCreateDomainScript();                  //001
        generateCreateTableScript();                   //110
        generateDropTableScript();                     //999
        if(config.getEmiraScripts()){
            generateCreateEMIRAScripts();
        }
    }

    /**
     * Création d'un fichier SQL.
     *
     * @param object : l'objet du contenu du fichier
     * @return SSQ DDL file as outDirPath/schemaName_object_version_currentDate.sql
     */
    protected File createSqLFile(String object) {
        String fileFullName = String.join("_", this.schema.getSchemaId(), object, this.config.getVersion(), this.currentDate);
        File file = null;
        for (ServerProperties properties : this.config.getSrcServer()) {
            String path = config.getOutDirPath().endsWith("/") ? config.getOutDirPath() + this.currentDate + "/" : config.getOutDirPath() + "/" + this.currentDate
                    + "/" + properties.getRdbms();

            try {
                file = createFile(path, fileFullName, ".sql");
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        return file;
    }

    /**
     * Création de l'entête de script avec contenu.
     *
     * @param filePrefix : le préfixe pour le nom du fichier
     * @param subject    : le sujet du contenu
     * @param content    : le contenu
     * @return Le script avec le contenu.
     */
    protected File generateScript(String filePrefix, String subject, String content) {
        Writer writer;
        File script = createSqLFile(filePrefix);
        try {
            writer = new OutputStreamWriter(new FileOutputStream(script, true), StandardCharsets.UTF_8);
            writer.write(sqlTemplate.createScriptHeader(getRDBMS(),this.getSchema().getSchemaId(), this.getCurrentDate(), config.getAuthor(),
                    config.getVersion(), subject));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error IOException: "+ e.getMessage());
        }
        return script;
    }

    // **************************************************************************
    // Opérations publiques : Les types définis.
    //

    /**
     * Générer les instructions SQL pour la création des types définis (DOMAIN).
     *
     * @return Les instructions pour la création des types définis (DOMAIN).
     */
    protected String generateCreateDomainStatements() {
        StringBuilder s = new StringBuilder();
        //
        String schemaId = this.getSchema().getSchemaId();
        for (DefinedType t : this.getSchema().getDefinedTypeSet()) {
            s.append(sqlTemplate.createDomain(schemaId, t.getId(), t.getBaseType().getId(), t.getBaseType().isBuiltIn(), true));
        }
        //
        return s.toString();
    }

    /**
     * Générer les instructions SQL pour la suppression des types définis.
     *
     * @return Les instructions SQL pour la suppression des types définis.
     */
    protected String generateDropDomainStatements() {
        StringBuilder s = new StringBuilder();
        //
        String schemaId = this.getSchema().getSchemaId();
        for (DefinedType t : this.getSchema().getDefinedTypeSet()) {
            s.append(sqlTemplate.dropDomain(schemaId, t.getId(), true));
        }
        //
        return s.toString();
    }

    // **************************************************************************
    // Opérations propres : les tables.
    //

    /**
     * Générer les instructions SQL pour la création des tables incluant les contraintes référentielles.
     *
     * @return Les instructions SQL pour la création des tables incluant les contraintes référentielles.
     */
    protected String generateCreateTableStatements() {
        StringBuilder s = new StringBuilder();
        //
        String schemaId = this.getSchema().getSchemaId();
        for (Relvar t : this.getSchema().getRelvarSet()) {
            // Générer la table
            s.append(sqlTemplate.createTable(schemaId, t.getId(), t.getAttributeSet(), t.getId() + "_pk", t.getKeyAttributeSet()));
            // Générer le commentaire de la table
            s.append(sqlTemplate.createTableComment(schemaId, t.getId(), t.getId() + " " + t.getAttributeSet().toString()));
        }
        //
        s.append(generateCreateFkStatements());
        return s.toString();
    }

    /**
     * Générer les instructions SQL pour la création des clés référentielles.
     *
     * @return Les instructions SQL pour la création des clés référentielles.
     */
    protected String generateCreateFkStatements() {
        StringBuilder s = new StringBuilder();
        String schemaId = this.getSchema().getSchemaId();
        for (ReferentialKey fk : this.getSchema().getReferentialKeySet()) {
            if(fk.getTargetRelvar() != null){
                // Générer la contrainte
                s.append(
                        sqlTemplate.createForeignKeyConstraint(fk.getId(), schemaId, fk.getSourceRelvar().getId(), fk.getAttributeSet().keySet(),
                                schemaId, fk.getTargetRelvar().getId(), new HashSet<>(fk.getAttributeSet().values())));
                // Générer le commentaire
                s.append(sqlTemplate.createCommentConstraint(schemaId, fk.getId(), fk.getSourceRelvar().getId(),
                        fk.getSourceRelvar().getId() + " --> " + fk.getTargetRelvar().getId()));
            }

        }
        //
        return s.toString();
    }

    /**
     * Générer les instructions SQL pour la suppression des tables.
     *
     * @return Les instructions SQL pour la suppression des tables.
     */
    protected String generateDropTableStatements() {
        StringBuilder s = new StringBuilder();
        //
        for (Relvar t : this.getSchema().getRelvarSet()) {
            s.append(sqlTemplate.dropTable(this.getSchema().getSchemaId(), t.getId()));
        }
        //
        return s.toString();
    }

    // FIXME : selectionner les attribut spécifique pour l'évalution
    /**
     * Générer les instructions SQL pour l'expression d'Evaluation d'EMIRA
     * @param t : la table à évaluer
     * @return l'instruction d'évaluation d'un ensemble d'attributs.
     */
    protected String generateCreateSelectStatement(Relvar t) {
        StringBuilder s = new StringBuilder();
        //
        String schemaId = this.getSchema().getSchemaId();

        for (Attribute a : t.getAttributeSet())
        {
            // Générer les Selects
            s.append(sqlTemplate.selectTableAttribut(schemaId, t.getId(), a, t.getAttributeSet()));
        }
        return s.toString();
    }

    /**
     * Générer les instructions SQL pour l'expression de Modificaton d'EMIRA
     * @param t : la table à modifier
     * @return l'instruction de modification d'un ensemble d'attributs.
     */
    protected String generateCreateUpdateStatement(Relvar t) {
        StringBuilder s = new StringBuilder();
        //
        String schemaId = this.getSchema().getSchemaId();

        for (Attribute a : t.getNoKeyAttributeSet())
        {
            s.append(sqlTemplate.updateTableAttribut(schemaId, t.getId(), a, t.getKeyAttributeSet()));
        }
        return s.toString();
    }

    /**
     * Générer les instructions SQL pour l'expression d'Insertion d'EMIRA
     * @param t : la table à inserer
     * @return l'instruction d'insertion d'un ensemble d'attributs.
     */
    protected String generateCreateInsertStatement(Relvar t) {
        StringBuilder s = new StringBuilder();
        //
        String schemaId = this.getSchema().getSchemaId();
        s.append(sqlTemplate.insertTable(schemaId, t.getId(), t.getAttributeSet()));
        return s.toString();
    }

    /**
     * Générer les instructions SQL pour l'expression de Retrait d'EMIRA
     * @param t : la table pour retirer un élément.
     * @return l'instruction de retrait d'un ensemble d'attributs.
     */
    protected String generateCreateDeleteStatement(Relvar t) {
        StringBuilder s = new StringBuilder();
        //
        String schemaId = this.getSchema().getSchemaId();
        s.append(sqlTemplate.deleteTableProcedure(schemaId, t.getId(), t.getKeyAttributeSet()));
        return s.toString();
    }

    // **************************************************************************
    // Opérations propre : les tables.
    //
    public abstract String getRDBMS();
    public abstract File generateCreateSchemaScript();
    public abstract File generateCreateDomainScript();
    public abstract File generateCreateTableScript();
    public abstract File generateDropTableScript();
    public abstract List<File> generateCreateEMIRAScripts();
}
