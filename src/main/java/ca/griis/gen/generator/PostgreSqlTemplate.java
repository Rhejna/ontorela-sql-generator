package ca.griis.gen.generator;

import ca.griis.gen.model.Attribute;
import ca.griis.gen.model.AttributeString;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.List;
import java.util.Set;

public class PostgreSqlTemplate extends SqlTemplate {
  // **************************************************************************
  // Attributs spécifiques
  //

  // **************************************************************************
  // Constructeurs
  //
  public PostgreSqlTemplate() {
    String templateFile = "antlr/ca/griis/gen/stg/PostgreSQL.stg";
    try {
        template = new STGroupFile(templateFile);
    } catch (Exception e) {
      System.err.println("Template file not found: " + templateFile);
    }
  }

  // **************************************************************************
  // Opérations propres
  //

  // **************************************************************************
  // Opérations publiques
  //

  // **************************************************************************
  // Opérations publiques - UHF_Base
  //

  /**
   * Créer l'extension GIST.
   *
   * @return la chaine de caractère pour la création de l'extension Gist
   */
  public String createTemporalExtensions() {
    ST ext = template.getInstanceOf("create_gist_extension");
    return ext.render();
  }

  /**
   * Créer une portée d'exécution pour un script (search_path).
   *
   * @param schemaList : la liste de schéma qui forme la portée.
   * @return la chaine de caractère pour la portée d'exécution d'un script.
   */
  public String createSearchPathFunction(List<String> schemaList) {
    ST searchPath = template.getInstanceOf("create_search_path");
    for (String e : schemaList) {
      searchPath.addAggr("schemaSet.{id}", e);
    }
    return searchPath.render();
  }

  /**
   * Obtenir une requête SQL simple d'une table avec une selection sur une table et une projetion sur
   * un ou plusieurs attributs.
   *
   * @param schemaId : le schéma de la table.
   * @param tableId  : la table.
   * @param attSet   : liste de paire d'attributs et leur alias (alias peut être null).
   * @param fctAtt   : liste de paire de signatures de function et leur alias.
   * @return une chaine de caractère SQL d'une requête temporel simple.
   */
  public String createSimpleQuery(String schemaId, String tableId, Set<AttributeString> attSet, AttributeString fctAtt) {
    ST query = template.getInstanceOf("simple_query");
    //
    query.add("schemaId", schemaId);
    query.add("tableId", tableId);
    //
    for (AttributeString a : attSet) {
      query.addAggr("attSet.{id, alias, fct}", a.getId(), a.getType(), a.isFct());
    }
    query.addAggr("attSet.{id, alias, fct}", fctAtt.getId(), fctAtt.getType(), fctAtt.isFct());
    //
    return query.render();
  }

  public String dropTable(String schemaId, String tableId) {
    ST drp = template.getInstanceOf("table_drp");
    drp.add("schemaId", schemaId);
    drp.add("tableId", tableId);
    drp.add("isCascade", true);
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
    drp.add("is_cascade", true);
    return drp.render();
  }

  /**
   * faire un update table where keyatt en sql
   *
   * @param schemaId : l'identifiant du schéma
   * @param tableId   : l'identifiant de la vue.
   * @param nomatt : l'attribut a update
   * @param keyattSet : la clé pour trouvé l'enregistrement
   * @return les donnée que contient la table selon son attribut
   */
  public String updateTableAttribut(String schemaId, String tableId, Attribute nomatt, Set<Attribute> keyattSet) {
    ST eval = template.getInstanceOf("update_procedure_attr");
    eval.add("schemaId", schemaId);
    eval.add("tableId", tableId);
    eval.addAggr("nomatt.{id, type, isBuiltIn}", nomatt.getId(), nomatt.getType().getId(), nomatt.getType().isBuiltIn());
    // Créer les attributs
    for (Attribute a : keyattSet) {
      eval.addAggr("keyattSet.{id, type, isBuiltIn}", a.getId(), a.getType().getId(), a.getType().isBuiltIn());
    }
    return eval.render();
  }

  /**
   * faire un insert into table
   *
   * @param schemaId : l'identifiant du schéma
   * @param tableId   : l'identifiant de la table.
   * @param attSet : list attribut de la table
   * @return les donnée que contient la table selon son attribut
   */
  public String insertTable(String schemaId, String tableId, Set<Attribute> attSet) {
    ST eval = template.getInstanceOf("insert_procedure");
    eval.add("schemaId", schemaId);
    eval.add("tableId", tableId);
    // Créer les attributs
    for (Attribute a : attSet) {
      eval.addAggr("attSet.{id, type, isBuiltIn}", a.getId(), a.getType().getId(), a.getType().isBuiltIn());
    }
    return eval.render();
  }

  /**
   * faire un delete table where keyatt en sql
   *
   * @param schemaId : l'identifiant du schéma
   * @param tableId   : l'identifiant de la vue.
   * @param keyattSet : la clé pour trouvé l'enregistrement
   * @return les donnée que contient la table selon son attribut
   */
  public String deleteTableProcedure(String schemaId, String tableId, Set<Attribute> keyattSet) {
    ST eval = template.getInstanceOf("delete_procedure");
    eval.add("schemaId", schemaId);
    eval.add("tableId", tableId);
    // Créer les key attributs
    for (Attribute a : keyattSet) {
      eval.addAggr("keyattSet.{id, type, isBuiltIn}", a.getId(), a.getType().getId(), a.getType().isBuiltIn());
    }
    return eval.render();
  }
}
