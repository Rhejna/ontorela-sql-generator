package ca.griis.gen.generator;

import ca.griis.gen.model.Attribute;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;


public class MSSqlTemplate extends SqlTemplate {
  // *************************************************************************
  // Attributs spécifiques
  //

  // **************************************************************************
  // Constructeur
  //
  public MSSqlTemplate() {
    String templateFile = "antlr/ca/griis/gen/stg/MSSQL.stg";
    try {
      template = new STGroupFile(templateFile);
    } catch (Exception e) {
      System.err.println("Template file not found: " + templateFile);
    }
  }

  public String dropDomain(String schemaId, String domaineId) {
    return dropDomain(schemaId, domaineId, false);
  }

  @Override
  public String updateTableAttribut(String schemaId, String tableId, Attribute nomatt, Set<Attribute> keyattSet) {
    //TODO implement update
    return null;
  }

  @Override
  public String insertTable(String schemaId, String tableId, Set<Attribute> attSet) {
    //TODO implement insert
    return null;
  }

  @Override
  public String deleteTableProcedure(String schemaId, String tableId, Set<Attribute> keyattSet) {
    return null;
  }

  public String dropTable(String schemaId, String tableId) {
    ST drp = template.getInstanceOf("table_drp");
    drp.add("schemaId", schemaId);
    drp.add("tableId", tableId);
    drp.add("isCascade", false);
    return drp.render();
  }

  /**
   * Supprimer une vue en cascade.
   *
   * @param schemaId : l'identifiant du schéma
   * @param viewId   : l'identifiant de la vue.
   * @return la chaîne de caractère de suppression de la vue.
   */
  public String dropView(String schemaId, String viewId) {
    ST drp = template.getInstanceOf("view_drp");
    drp.add("schema_id", schemaId);
    drp.add("view_id", viewId);
    drp.add("is_cascade", false);
    return drp.render();
  }


}
