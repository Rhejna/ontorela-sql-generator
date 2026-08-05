package ca.griis.gen.generator;

import ca.griis.gen.model.AttributeString;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Décrire la classe ici.
 * <p>
 * <b>Propriétés des objets</b>
 * <ul>
 * <li>Unicité : oui.</li>
 * <li>Clonabilité : non.</li>
 * <li>Modifiabilité : non.</li>
 * </ul>
 *
 * <b>Tâches projetées</b><br>
 * ..<br>
 *
 * <b>Tâches réalisées</b><br>
 * 2018-XX-XX (0.2.0) [XX] ... <br>
 * 2019-03-16 (0.1.0) [CK] Mise en oeuvre initiale. <br>
 *
 * <p>
 * <b>Copyright</b> 2016-2017, GRIIS (https://griis.ca/) <br>
 * GRIIS (Groupe de recherche interdisciplinaire en informatique de la santé) <br>
 * Faculté des sciences et Faculté de médecine et sciences de la santé <br>
 * Université de Sherbrooke (Québec) J1K 2R1 <br>
 * CANADA <br>
 * [CC-BY-NC-3.0 (http://creativecommons.org/licenses/by-nc/3.0)]
 * </p>
 *
 * @author [CK] Christina.Khnaisser@USherbrooke.ca
 * @author [RL] Remi.Letourneau@USherbrooke.ca
 * @version 0.1.0
 * @since 2022-02-17
 */
public class UhfSqlTemplate extends PostgreSqlTemplate {
  // **************************************************************************
  // Attributs spécifiques
  //
  private static UhfSqlTemplate instance = null;

  // **************************************************************************
  // Constructeurs
  //
  protected UhfSqlTemplate() {

    String templateFile = "antlr/ca/griis/gen/stg/UhfSQL.stg";
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
  public static UhfSqlTemplate getInstance() {
    if (instance == null) {
      instance = new UhfSqlTemplate();
    }
    return instance;
  }
  // **************************************************************************
  // Opérations publiques - UHF_Base
  //

  /**
   * Créer la fonction de construction d'un intervalle.
   *
   * @param schemaId        : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType  : le type temporel d'entrée.
   * @param outTemporalType : le type temporel de sortie.
   * @param begin           : le point de début.
   * @param end             : le point de fin.
   * @param function        : la fonction de construction.
   * @return la chaine de caractère de définition de la fonction de construction d'un intervalle.
   */
  // TODO 2022-09-01 CK : retirer begin end ?
  public String createIntervalFunction(String schemaId, String inTemporalType, String outTemporalType,
                                       String begin, String end, String function) {
    ST cre = template.getInstanceOf("createInterval_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("begin", begin);
    cre.add("end", end);
    cre.add("function", function);
    return cre.render();
  }

  /**
   * Créer la fonction de construction d'un intervalle Vxe.
   *
   * @param schemaId        : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType  : le type temporel d'entrée.
   * @param outTemporalType : le type temporel de sortie.
   * @param function        : la fonction de construction.
   * @return la chaine de caractère de définition de la fonction de construction d'un intervalle.
   */
  // TODO 2022-09-01 CK : appeler le gabarit create_interval_function.
  public String createVxePeriodFunction(String schemaId, String inTemporalType, String outTemporalType,
                                        String function) {
    ST cre = template.getInstanceOf("create_vxe_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("function", function);
    return cre.render();
  }

  /**
   * Créer la fonction de construction d'un intervalle Vbx.
   *
   * @param schemaId        : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType  : le type temporel d'entrée.
   * @param outTemporalType : le type temporel de sortie.
   * @param function        : la fonction de construction.
   * @return la chaine de caractère de définition de la fonction de construction d'un intervalle.
   */
  // TODO 2022-09-01 CK : appeler le gabarit create_interval_function.
  public String createVbxPeriodFunction(String schemaId, String inTemporalType, String outTemporalType,
                                        String function) {
    ST cre = template.getInstanceOf("create_vbx_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("function", function);
    return cre.render();
  }

  /**
   * Créer la fonction de construction d'un intervalle Vxx.
   *
   * @param schemaId        : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType  : le type temporel d'entrée.
   * @param outTemporalType : le type temporel de sortie.
   * @param function        : la fonction de construction.
   * @return la chaine de caractère de définition de la fonction de construction d'un intervalle.
   */
  // TODO 2022-09-01 CK : appeler le gabarit create_interval_function.
  public String createVxxPeriodFunction(String schemaId, String inTemporalType, String outTemporalType,
                                        String function) {
    ST vxeFunc = template.getInstanceOf("create_vxx_func");
    vxeFunc.add("schemaId", schemaId);
    vxeFunc.add("inTemporalType", inTemporalType);
    vxeFunc.add("outTemporalType", outTemporalType);
    vxeFunc.add("function", function);
    //
    return vxeFunc.render();
  }

  /**
   * Créer la fonction du prédécesseur d'un point.
   *
   * @param schemaId        : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType  : le type temporel d'entrée.
   * @param outTemporalType : le type temporel de sortie.
   * @param granularity     : la granularité de l'incrément.
   * @return la chaine de caractère de définition de la fonction prédécesseur d'un point.
   */
  public String createPointPreFunction(String schemaId, String inTemporalType, String outTemporalType,
                                       String granularity) {
    ST cre = template.getInstanceOf("createPointPre_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("granularity", granularity);
    return cre.render();
  }

  /**
   * Créer la fonction du prédécesseur d'un interval.
   *
   * @param schemaId        : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType  : le type temporel d'entrée.
   * @param outTemporalType : le type temporel de sortie.
   * @param granularity     : la granularité de l'incrément.
   * @return la chaine de caractère de définition de la fonction prédécesseur d'un interval.
   */
  public String createIntervalPreFunction(String schemaId, String inTemporalType, String outTemporalType,
                                          String granularity) {
    ST cre = template.getInstanceOf("createIntervalPre_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("granularity", granularity);
    return cre.render();
  }

  /**
   * Créer la fonction du successeur d'un point.
   *
   * @param schemaId        : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType  : le type temporel d'entrée.
   * @param outTemporalType : le type temporel de sortie.
   * @param granularity     : la granularité de l'incrément.
   * @return la chaine de caractère de définition de la fonction successeur d'un point.
   */
  public String createPointPostFunction(String schemaId, String inTemporalType, String outTemporalType,
                                        String granularity) {
    ST cre = template.getInstanceOf("createPointPost_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("granularity", granularity);
    return cre.render();
  }

  /**
   * Créer la fonction du successeur d'un intervalle.
   *
   * @param schemaId        : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType  : le type temporel d'entrée.
   * @param outTemporalType : le type temporel de sortie.
   * @param granularity     : la granularité de l'incrément.
   * @return la chaine de caractère de définition de la fonction successeur d'un intervalle.
   */
  public String createIntervalPostFunction(String schemaId, String inTemporalType, String outTemporalType,
                                           String granularity) {
    ST cre = template.getInstanceOf("createIntervalPost_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("granularity", granularity);
    return cre.render();
  }

  /**
   * Créer la fonction d'expansion d'un intervalle en une liste de point.
   *
   * @param schemaId            : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType      : le type temporel d'entrée.
   * @param outTemporalType     : le type temporel de sortie.
   * @param convertTemporalType
   * @return la chaine de caractère de définition de la fonction.
   */
  public String createUnpackIntervalPointFunction(String schemaId, String inTemporalType, String outTemporalType,
                                                  String convertTemporalType) {
    ST cre = template.getInstanceOf("createUnpackIntervalPoint_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("convertTemporalType", convertTemporalType);
    return cre.render();
  }

  /**
   * Créer la fonction d'expansion d'un intervalle en une liste d'intervalle singleton.
   *
   * @param schemaId            : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType      : le type temporel d'entrée.
   * @param outTemporalType     : le type temporel de sortie.
   * @param convertTemporalType
   * @return la chaine de caractère de définition de la fonction.
   */
  public String createUnpackFunction(String schemaId, String inTemporalType, String outTemporalType,
                                     String convertTemporalType, String fonction) {
    ST cre = template.getInstanceOf("createUnpack_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("convertTemporalType", convertTemporalType);
    cre.add("function", fonction);
    return cre.render();
  }

  public String createUnpackFunction(String schemaId, String inValidTemporalType,
                                     String inTemporalType, String outTemporalType,
                                     String convertTemporalType, String fonction) {
    ST cre = template.getInstanceOf("createUnpackParams_func");
    cre.add("schemaId", schemaId);
    cre.add("inValidTemporalType", inValidTemporalType);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("convertTemporalType", convertTemporalType);
    cre.add("function", fonction);
    return cre.render();
  }

  /**
   * Créer la fonction qui retourne le point de début d'un intervalle.
   *
   * @param schemaId            : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType      : le type temporel d'entrée.
   * @param outTemporalType     : le type temporel de sortie.
   * @param convertTemporalType
   * @return la chaine de caractère de définition de la fonction.
   */
  public String createIbeginFunction(String schemaId, String inTemporalType, String outTemporalType,
                                     String convertTemporalType) {
    ST cre = template.getInstanceOf("createIbegin_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("convertTemporalType", convertTemporalType);
    return cre.render();
  }

  /**
   * Créer la fonction qui retourne le point de fin d'un intervalle.
   *
   * @param schemaId            : l'identifiant du schéma auquel appartient la fonction.
   * @param inTemporalType      : le type temporel d'entrée.
   * @param outTemporalType     : le type temporel de sortie.
   * @param convertTemporalType
   * @return la chaine de caractère de définition de la fonction.
   */
  public String createIendFunction(String schemaId, String inTemporalType, String outTemporalType,
                                   String convertTemporalType) {
    ST cre = template.getInstanceOf("createIend_func");
    cre.add("schemaId", schemaId);
    cre.add("inTemporalType", inTemporalType);
    cre.add("outTemporalType", outTemporalType);
    cre.add("convertTemporalType", convertTemporalType);
    return cre.render();
  }

  /**
   * Créer la fonction qui retourne le premier point permis sur l'axe du temps.
   *
   * @param schemaId            : l'identifiant du schéma auquel appartient la fonction.
   * @param firstValue          : la valeur minimale du type temporel.
   * @param outTemporalType     : le type temporel de sortie.
   * @param convertTemporalType
   * @return la chaine de caractère de définition de la fonction.
   */
  public String createFirstFunction(String schemaId, String firstValue, String outTemporalType,
                                    String convertTemporalType) {
    ST cre = template.getInstanceOf("createFirst_func");
    cre.add("schemaId", schemaId);
    cre.add("firstValue", firstValue);
    cre.add("outTemporalType", outTemporalType);
    cre.add("convertTemporalType", convertTemporalType);
    return cre.render();
  }

  /**
   * Créer la fonction qui retourne le point final permis sur l'axe du temps.
   *
   * @param schemaId            : l'identifiant du schéma auquel appartient la fonction.
   * @param lastValue           : la valeur maximale du type temporel.
   * @param outTemporalType     : le type temporel de sortie.
   * @param convertTemporalType
   * @return la chaine de caractère de définition de la fonction.
   */
  public String createLastFunction(String schemaId, String lastValue, String outTemporalType,
                                   String convertTemporalType) {
    ST cre = template.getInstanceOf("createLast_func");
    cre.add("schemaId", schemaId);
    cre.add("lastValue", lastValue);
    cre.add("outTemporalType", outTemporalType);
    cre.add("convertTemporalType", convertTemporalType);
    return cre.render();
  }
  // **************************************************************************
  // Opérations publiques - UHF
  //

  /**
   * Créer une vue historique d'une table à partir 4 partitions : Vxx, Vxe, Vbx et Vbe.
   *
   * @param schemaId   : le schéma de la table.
   * @param viewId     : l'identidiant de la vue.
   * @param vxeTableId : l'identifiant de la table vxe.
   * @param vxeAtt     : une paire d'identifiant de l'attribut vxe et son alias
   * @param vbeTableId : l'identifiant de la table vbe.
   * @param vbeAtt     : une paire d'identifiant de l'attribut vbe et son alias
   * @param vbxTableId : l'identifiant de la table vbx.
   * @param vbxAtt     : une paire d'identifiant de l'attribut vbx et son alias.
   * @param vxxTableId : l'identifiant de la table vxx.
   * @param vxxAtt     : une paire d'identifiant de l'attribut vxx et son alias.
   * @param attSet     : liste de paires d'identifiant d'attribut et leur alias.
   * @return une chaine de caractère SQL de la vue historique d'une table.
   */
  public String createHistoryView(String schemaId, String viewId, Set<AttributeString> attSet, String vxxTableId,
                                  AttributeString vxxAtt, String vxeTableId, AttributeString vxeAtt, String vbxTableId, AttributeString vbxAtt,
                                  String vbeTableId, AttributeString vbeAtt) {
    ST view = template.getInstanceOf("history_view");
    //
    view.add("schemaId", schemaId);
    view.add("viewId", viewId);
    if (vxxAtt != null)
      view.add("vxxQuery", createSimpleQuery(schemaId, vxxTableId, attSet, vxxAtt));
    view.add("vxeQuery", createSimpleQuery(schemaId, vxeTableId, attSet, vxeAtt));
    view.add("vbxQuery", createSimpleQuery(schemaId, vbxTableId, attSet, vbxAtt));
    view.add("vbeQuery", createSimpleQuery(schemaId, vbeTableId, attSet, vbeAtt));
    //
    return view.render();
  }

  /**
   * Obtenir une contrainte d'unicité temporelle pour une table.
   *
   * @param schemaId     : le schéma.
   * @param constraintId : l'identificateur de la contrainte.
   * @param tableId      : la table de base.
   * @param vxeTableId   : l'identifiant de la table vxe.
   * @param vbeTableId   : l'identifiant de la table vbe.
   * @param vbxTableId   : l'identifiant de la table vbx.
   * @param vxeAtt       : l'attribut vxe : un identifiant ou une fonction selon le type.
   * @param vbeAtt       : l'attribut vbe : un identifiant ou une fonction selon le type.
   * @param vbxAtt       : l'attribut vbx : un identifiant ou une fonction selon le type.
   * @param keyAttSet    : liste de paire identifiant de l'attribut et son type.
   * @return une chaine de caractère SQL de la contrainte d'unicité temporel.
   */
  // temporalUniqueness_check(schemaId, vxeTableId, vbeTableId, vbxTableId,
  // vxeAttId, vbeAttId, vbxAttId, keySet)
  public String createTemporalUniquenessCheck(String schemaId, String constraintId, String tableId, String vxeTableId,
                                              String vbeTableId, String vbxTableId, String vxeAtt, String vbeAtt, String vbxAtt,
                                              Set<AttributeString> keyAttSet) {
    ST check = template.getInstanceOf("temporalUniqueness_check");
    check.add("schemaId", schemaId);
    check.add("constraintId", constraintId);
    check.add("tableId", tableId);
    check.add("vxeTableId", vxeTableId);
    check.add("vbeTableId", vbeTableId);
    check.add("vbxTableId", vbxTableId);
    check.add("vxeAtt", vxeAtt);
    check.add("vbeAtt", vbeAtt);
    check.add("vbxAtt", vbxAtt);
    for (AttributeString a : keyAttSet) {
      check.addAggr("keySet.{id, type}", a.getId(), a.getType());
    }
    return check.render();
  }

  /**
   * Obtenir une contrainte d'unicité temporelle pour une table.
   *
   * @param schemaId     : le schéma.
   * @param constraintId : l'identificateur de la contrainte.
   * @param tableId      : la table de base.
   * @param vxeTableId   : l'identifiant de la table vxe.
   * @param vbeTableId   : l'identifiant de la table vbe.
   * @param vbxTableId   : l'identifiant de la table vbx.
   * @param vxeAtt       : l'attribut vxe : un identifiant ou une fonction selon le type.
   * @param vbeAtt       : l'attribut vbe : un identifiant ou une fonction selon le type.
   * @param vbxAtt       : l'attribut vbx : un identifiant ou une fonction selon le type.
   * @param keyAttSet    : liste de paire identifiant de l'attribut et son type.
   * @return une chaine de caractère SQL de la contrainte d'unicité temporel.
   */
  // temporalUniqueness_check(schemaId, vxeTableId, vbeTableId, vbxTableId,
  // vxeAttId, vbeAttId, vbxAttId, keySet)
  public String createTemporalCircumlocutionCheck(String schemaId, String constraintId, String tableId,
                                                  String vxeTableId, String vbeTableId, String vbxTableId, String vxeAtt, String vbeAtt, String vbxAtt,
                                                  Set<AttributeString> keyAttSet) {
    ST check = template.getInstanceOf("temporalCircumlocution_check");
    check.add("schemaId", schemaId);
    check.add("constraintId", constraintId);
    check.add("tableId", tableId);
    check.add("vxeTableId", vxeTableId);
    check.add("vbeTableId", vbeTableId);
    check.add("vbxTableId", vbxTableId);
    check.add("vxeAtt", vxeAtt);
    check.add("vbeAtt", vbeAtt);
    check.add("vbxAtt", vbxAtt);
    for (AttributeString a : keyAttSet) {
      check.addAggr("keySet.{id, type}", a.getId(), a.getType());
    }
    return check.render();
  }

  /**
   * Obtenir une contrainte de contradiction temporelle pour une table.
   *
   * @param schemaId     : le schéma.
   * @param constraintId : l'identificateur de la contrainte.
   * @param tableId      : la table de base.
   * @param vxeTableId   : l'identifiant de la table vxe.
   * @param vbeTableId   : l'identifiant de la table vbe.
   * @param vbxTableId   : l'identifiant de la table vbx.
   * @param vxeAtt       : l'attribut vxe : un identifiant ou une fonction selon le type.
   * @param vbeAtt       : l'attribut vbe : un identifiant ou une fonction selon le type.
   * @param vbxAtt       : l'attribut vbx : un identifiant ou une fonction selon le type.
   * @param keyAttSet    : liste de paire identifiant de l'attribut et son type.
   * @param NoKeyAtt     : l'attribut no key de la table.
   * @return une chaine de caractère SQL de la contrainte d'unicité temporel.
   */
  // temporalContradiction_check(constraintId, schemaId, tableId, vxeTableId,
  // vbeTableId, vbxTableId, vxeAtt, vbeAtt, vbxAtt, keySet, NoKeyAtt)
  public String createTemporalContradictionCheck(String schemaId, String constraintId, String tableId,
                                                 String vxeTableId, String vbeTableId, String vbxTableId, String vxeAtt, String vbeAtt, String vbxAtt,
                                                 Set<AttributeString> keyAttSet, String NoKeyAtt) {
    ST check = template.getInstanceOf("temporalContradiction_check");
    check.add("schemaId", schemaId);
    check.add("constraintId", constraintId);
    check.add("tableId", tableId);
    check.add("vxeTableId", vxeTableId);
    check.add("vbeTableId", vbeTableId);
    check.add("vbxTableId", vbxTableId);
    check.add("vxeAtt", vxeAtt);
    check.add("vbeAtt", vbeAtt);
    check.add("vbxAtt", vbxAtt);
    for (AttributeString a : keyAttSet) {
      check.addAggr("keySet.{id, type}", a.getId(), a.getType());
    }
    check.add("NoKeyAtt", NoKeyAtt);
    return check.render();
  }

  public String createTemporalDensenessCheck(String schemaId, String tableSourceID, String tableAttId, String inTemporalType,
                                             String vxeAtt, String vbeAtt, String vbxAtt, Set<AttributeString> keyAttSet) {
    ST check = template.getInstanceOf("temporalDenseness_check");
    check.add("schemaId", schemaId);
    check.add("tableSourceID", tableSourceID);
    check.add("tableAttId", tableAttId);
    check.add("inTemporalType", inTemporalType);
    check.add("vxeAtt", vxeAtt);
    check.add("vbeAtt", vbeAtt);
    check.add("vbxAtt", vbxAtt);
    for (AttributeString a : keyAttSet) {
      check.addAggr("keySet.{id, type}", a.getId(), a.getType());
    }
    return check.render();
  }

  public String createTemporalNoRedundancyCheck(String tableId, String schemaId, Set<String> keyAttId) {
    ST check = template.getInstanceOf("temporalRedundancy_check");
    //
    check.add("tableId", tableId);
    check.add("schemaId", schemaId);
    for (String a : keyAttId) {
      check.addAggr("keyAttSet.{id}", a);
    }
    //
    return check.render();
  }

  public String createTemporalNoCircumlocutionCheck(String tableId, String schemaId, Set<String> keyAttId) {
    ST check = template.getInstanceOf("temporalNoCircumlocution_check");
    //
    check.add("tableId", tableId);
    check.add("schemaId", schemaId);
    for (String a : keyAttId) {
      check.addAggr("keyAttSet.{id}", a);
    }
    //
    return check.render();
  }

  public String createTemporalNoContradictionCheck(String tableId, String schemaId, Set<String> keyAttId, String noKeyAttId, boolean isFct) {
    ST check = template.getInstanceOf("temporalNoContradiction_check");
    //
    check.add("tableId", tableId);
    check.add("schemaId", schemaId);
    for (String a : keyAttId) {
      check.addAggr("keyAttSet.{id}", a);
    }
    check.add("noKeyAttId", new AttributeString(noKeyAttId, isFct));
    //
    return check.render();
  }

  /**
   * Créer la contrainte de non redondance temporelle en SQL.
   *
   * @param constraintId : l'identifiant de la contraintes.
   * @param schemaId     : le schéma.
   * @param tableId      : la table de base.
   * @param keyAttId     : l'ensemble des identifiants des attributs clés.
   * @param noKeyAttId   : l'identifiant de l'attribut non clé (en considérant que la table est en
   *                     6FN).
   * @param vtId         : l'identifiant de l'attribut temporel.
   * @return une chaine de caractère SQL de la contrainte de non redondance.
   */
  public String createNoRedundancyConstraint(String constraintId, String schemaId, String tableId, Set<String> keyAttId,
                                             String noKeyAttId, boolean isFct, String vtId) {
    ST check = template.getInstanceOf("noRedundancy");
    //
    check.add("constraintId", constraintId);
    check.add("schemaId", schemaId);
    check.add("tableId", tableId);
    for (String a : keyAttId) {
      check.addAggr("keyAttSet.{id}", a);
    }
    check.add("noKeyAttId", new AttributeString(noKeyAttId, isFct));
    check.add("vtId", vtId);
    //
    return check.render();
  }

  /**
   * Créer la contrainte de non-contradiction temporelle en SQL.
   *
   * @param constraintId : l'identifiant de la contrainte.
   * @param schemaId     : le schéma.
   * @param tableId      : la table de base.
   * @param keyAttId     : l'ensemble des identifiants des attributs clés.
   * @param noKeyAttId   : l'identifiant de l'attribut non clé (en considérant que la table est en
   *                     6FN).
   * @param vtId         : l'identifiant de l'attribut temporel.
   * @return une chaine de caractère SQL de la contrainte de non contradiction.
   */
  public String createNoContradictionConstraint(String constraintId, String schemaId, String tableId,
                                                Set<String> keyAttId, String noKeyAttId, boolean isFct, String vtId) {
    ST check = template.getInstanceOf("noContradiction");
    //
    check.add("constraintId", constraintId);
    check.add("schemaId", schemaId);
    check.add("tableId", tableId);
    for (String a : keyAttId) {
      check.addAggr("keyAttSet.{id}", a);
    }
    check.add("noKeyAttId", new AttributeString(noKeyAttId, isFct));
    check.add("vtId", vtId);
    //
    return check.render();
  }

  /**
   * Créer la contrainte de non circomlocution temporelle en SQL.
   *
   * @param constraintId : l'identifiant de la contrainte.
   * @param schemaId     : le schéma.
   * @param tableId      : la table de base.
   * @param keyAttId     : l'ensemble des identifiants des attributs clés.
   * @param noKeyAttId   : l'identifiant de l'attribut non clé (en considérant que la table est en
   *                     6FN).
   * @param vtId         : l'identifiant de l'attribut temporel.
   * @return une chaine de caractère SQL de la contrainte de non circomlocution.
   */
  public String createNoCircumlocutionConstraint(String constraintId, String schemaId, String tableId,
                                                 Set<String> keyAttId, String noKeyAttId, boolean isFct, String vtId) {
    ST check = template.getInstanceOf("noCircumlocution");
    //
    check.add("constraintId", constraintId);
    check.add("schemaId", schemaId);
    check.add("tableId", tableId);
    for (String a : keyAttId) {
      check.addAggr("keyAttSet.{id}", a);
    }
    check.add("noKeyAttId", new AttributeString(noKeyAttId, isFct));
    check.add("vtId", vtId);
    //
    return check.render();
  }

  /**
   * Créer la fonction de vérification de clé étrangère en sql
   *
   * @param constraintId     : l'identifiant de la contrainte.
   * @param schemaId         : le schéma.
   * @param orgTableId       : la table de base.
   * @param dstTableId       : la table de destination
   * @param keyAttMap        : L'identifiant d'orgTable avec la valeur de dstTable
   * @param orgTemporalAttId : l'identifiant de l'attribut temporel origine.
   * @param dstTemporalAttId : l'identifiant de l'attribut temporel destination.
   * @return une chaine de caractère SQL de la contrainte de non circomlocution.
   */
  public String createTemporalForeignKeyFct(String constraintId, String schemaId, String orgTableId, String dstTableId,
                                            Map<String, String> keyAttMap, String orgTemporalAttId, String dstTemporalAttId) {
    ST check = template.getInstanceOf("temporalFk_check");
    //
    check.add("constraintId", constraintId);
    check.add("schemaId", schemaId);
    check.add("orgTableId", orgTableId);
    check.add("dstTableId", dstTableId);
    for (Entry<String, String> e : keyAttMap.entrySet()) {
      check.addAggr("keyAttMap.{orgAttId, dstAttId}", e.getKey(), e.getValue());
    }
    check.add("orgTemporalAttId", orgTemporalAttId);
    check.add("dstTemporalAttId", dstTemporalAttId);
    //
    return check.render();
  }

  /**
   * Créer le procedure pour insérer selon le type temporelle (vbe, vxe, vbx, vxx)
   *
   * @param schemaId     le schéma.
   * @param groupingId   le nom de la relpart sans l'attribut temporelle (ex: s, s_statut)
   * @param temporalAtt  Le nom et le type de l'attribut temporelle
   * @param attSet       L'ensemble des attributs de la table
   * @param vxxRelpartId le nom de la relpart de type temporelle vxx
   * @param vxeRelpartId le nom de la relpart de type temporelle vxe
   * @param vbxRelpartId le nom de la relpart de type temporelle vbx
   * @param vbeRelpartId le nom de la relpart de type temporelle vbe
   * @return une chaine de caractère SQL de la procedure grouping insert
   */
  public String createGroupingInsert(String schemaId, String groupingId, AttributeString temporalAtt,
                                     Set<AttributeString> attSet, String vxxRelpartId, String vxeRelpartId, String vbxRelpartId, String vbeRelpartId) {
    ST insert = template.getInstanceOf("grouping_insert");
    //
    insert.add("schemaId", schemaId);
    insert.add("groupingId", groupingId);
    insert.add("temporalAtt", temporalAtt);
    for (AttributeString a : attSet) {
      insert.addAggr("attSet.{id, type, fct}", a.getId(), a.getType(), a.isFct());
    }
    insert.add("vxxRelpartId", vxxRelpartId);
    insert.add("vxeRelpartId", vxeRelpartId);
    insert.add("vbxRelpartId", vbxRelpartId);
    insert.add("vbeRelpartId", vbeRelpartId);
    //
    return insert.render();
  }

  /**
   * Créer la procedure pour décider quel sous procedure on appel selon l'état de la bd.
   *
   * @param schemaId          le schéma.
   * @param relvarId          le nom de la table (ex: s, sp)
   * @param temporalAtt       Le nom et le type de l'attribut temporelle
   * @param vbxAtt            le nom et le type de la relpart de type temporelle vbx
   * @param vxeAtt            le nom et le type de la relpart de type temporelle vxe
   * @param vbeAtt            le nom et le type de la relpart de type temporelle vbe
   * @param attSet            l'ensemble des attributs
   * @param keyAttSet         l'ensemble des attributs clé
   * @param noKeyAttSet       l'ensemble des attributs non-clé
   * @param temporalPointType le type temporelle pour un point
   * @return une chaine de caractère SQL de la procedure valid insert
   */
  public String createValidateInsert(String schemaId, String relvarId, AttributeString temporalAtt, AttributeString vbxAtt, AttributeString vxeAtt, AttributeString vbeAtt,
                                     Set<AttributeString> attSet, Set<AttributeString> keyAttSet, Set<AttributeString> noKeyAttSet, String temporalPointType) {
    ST insert = template.getInstanceOf("valid_insert");
    //
    insert.add("schemaId", schemaId);
    insert.add("relvarId", relvarId);
    insert.add("temporalAtt", temporalAtt);
    insert.add("vbxAtt", vbxAtt);
    insert.add("vxeAtt", vxeAtt);
    insert.add("vbeAtt", vbeAtt);
    for (AttributeString a : attSet) {
      insert.addAggr("attSet.{id, type, fct}", a.getId(), a.getType(), a.isFct());
    }
    for (AttributeString a : keyAttSet) {
      insert.addAggr("keyAttSet.{id, type, fct}", a.getId(), a.getType(), a.isFct());
    }
    for (AttributeString a : noKeyAttSet) {
      insert.addAggr("noKeyAttSet.{id, type, fct}", a.getId(), a.getType(), a.isFct());
    }
    insert.add("temporalPointType", temporalPointType);
    //
    return insert.render();
  }

  public String createAttInsert(String templateName, String schemaId, String groupingId, AttributeString temporalAtt,
                                Set<AttributeString> attSet, Set<AttributeString> keyAttSet, String noKeyAttId, String vxxRelpartId,
                                String vxeRelpartId, String vbxRelpartId, String vbeRelpartId, String temporalPointType, String temporalIntervalType) {
    ST insert = template.getInstanceOf(templateName);
    //
    insert.add("schemaId", schemaId);
    insert.add("groupingId", groupingId);
    insert.add("temporalAtt", temporalAtt);
    for (AttributeString a : attSet) {
      insert.addAggr("attSet.{id, type, fct}", a.getId(), a.getType(), a.isFct());
    }
    for (AttributeString a : keyAttSet) {
      insert.addAggr("keyAttSet.{id, type, fct}", a.getId(), a.getType(), a.isFct());
    }
    insert.add("noKeyAttId", noKeyAttId);
    insert.add("vxxRelpartId", vxxRelpartId);
    insert.add("vxeRelpartId", vxeRelpartId);
    insert.add("vbxRelpartId", vbxRelpartId);
    insert.add("vbeRelpartId", vbeRelpartId);
    insert.add("temporalPointType", temporalPointType);
    insert.add("temporalIntervalType", temporalIntervalType);
    //
    return insert.render();
  }
}
