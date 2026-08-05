package ca.griis.gen.generator;

import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.model.SchemaModel;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Generator {
    private final String currentDate;
    protected final SchemaModel schema;
    protected final DatabaseConfig config;

    // ************************************************************************
    // Constructeurs
    //

    /**
     * default constructor
     * @param schema : name of the dataabase schema
     */
    public Generator(SchemaModel schema, DatabaseConfig config){
        this.schema = schema;
        this.config = config;
        DateFormat dateFormat = new SimpleDateFormat(("yyyyMMdd-HHmm"));
         Date date = new Date();
        this.currentDate = dateFormat.format((date));
    }

    // **************************************************************************
    // Opérations propres
    //
    protected String quote(String s) {
        char quote = '"';
        return quote + s + quote;
    }

    /**
     * Creates a file.
     *
     * @param directoryPath : the path of the output directory
     * @param fileName      : the file name
     * @param extension     : the extension of the file
     * @return a file
     * @throws IOException Throw if the directory is not created.
     */
    protected File createFile(String directoryPath, String fileName, String extension) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            // On essaye de construire le dossier à partir du dossier courant.
            directory = new File(System.getProperty("user.dir") + directoryPath);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Unable to create directory" + directoryPath);
            }
        }
        File f = new File(directory + "/" + fileName + extension);
        return f;
    }

    /**
     * Creates a documentation file.
     *
     * @param outDirPath : the path of the output directory
     * @param desc       : description file content
     * @param version    : the schema version to be used to define the file name
     * @return SSQ DDL file as outDirPath/schemaName_suffix_version_currentDate.sql
     */
    protected File createDocFile(String outDirPath, String desc, String version) {
        String fileName = String.join("_", schema.getSchemaId(), desc, version, this.currentDate);
        String path = outDirPath.endsWith("/") ? outDirPath : outDirPath + "/";
        File file = null;
        try {
            file = createFile(path, fileName, ".txt");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return file;
    }

    // **************************************************************************
    // Opérations publiques
    //
    public String getCurrentDate() {
        return this.currentDate;
    }

    public SchemaModel getSchema() {
        return this.schema;
    }

    public DatabaseConfig getConfig() {
        return config;
    }

    // **************************************************************************
    // Opérations publiques : à personaliser selon le SGBD.
    //
    protected abstract String generateCreateDomainStatements();
    protected abstract String generateDropDomainStatements();
    protected abstract String generateCreateTableStatements();
    protected abstract String generateCreateFkStatements();
    protected abstract String generateDropTableStatements();

    public abstract File generateCreateTableScript();
    public abstract File generateDropTableScript();
}
