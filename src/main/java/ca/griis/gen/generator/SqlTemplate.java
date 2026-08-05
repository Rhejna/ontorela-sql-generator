package ca.griis.gen.generator;

import ca.griis.gen.model.Attribute;
import ca.griis.gen.model.AttributeString;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public abstract class SqlTemplate {

    // *************************************************************************
    // Attributs spécifiques
    //
    protected STGroup template;

    // **************************************************************************
    // Constructeur
    //

    // **************************************************************************
    // Opérations abstraites définie dans l'implémentation
    //
    public abstract String updateTableAttribut(String schemaId, String tableId, Attribute nomatt, Set<Attribute> keyattSet);
    public abstract String insertTable(String schemaId, String tableId, Set<Attribute> attSet);
    public abstract String deleteTableProcedure(String schemaId, String tableId, Set<Attribute> keyattSet);

    /**
     * faire un select table where nomatt en sql
     *
     * @param schemaId : l'identifiant du schéma
     * @param tableId   : l'identifiant de la vue.
     * @param attSet : le set attribut a select
     * @return les donnée que contient la table selon son attribut
     */
    public String selectTableAttribut(String schemaId, String tableId, Attribute nomatt, Set<Attribute> attSet) {
        ST eval = template.getInstanceOf("evaluate_function_attr");
        eval.add("schemaId", schemaId);
        eval.add("tableId", tableId);
        eval.add("nomatt", nomatt);
        // Créer les attributs
        for (Attribute a : attSet) {
            eval.addAggr("attSet.{id, type, isBuiltIn}", a.getId(), a.getType().getId(), a.getType().isBuiltIn());
        }
        return eval.render();
    }
    // ***********************************************************************************************
    // Opérations publiques - Documentation script
    //
    /**
     * Créer un entête de fichier pour le script SQL.
     *
     * @param schemaId     : l'identifiant du schéma auquel appartient la table
     * @param creationDate : la date de Créer du script
     * @param responsable  : le nom du responsable du script
     * @param version      : la version courante du script
     * @param objet        : l'objet du script
     * @return la chaine de caractère de l'entête.
     */
    public String createScriptHeader(String rdbms, String schemaId, String creationDate, String responsable, String version,
                                     String objet) {
        ST enteteF = template.getInstanceOf("scriptHeader");
        enteteF.add("rdbms", rdbms);
        enteteF.add("schemaId", schemaId);
        enteteF.add("creationDate", creationDate);
        enteteF.add("responsable", responsable);
        enteteF.add("version", version);
        enteteF.add("objet", objet);
        return enteteF.render();
    }
    // **************************************************************************
    // Opérations publiques - Gabarits pour la gestion d'un schéma
    //
    /**
     * Créer un schéma.
     *
     * @param schemaId : l'identifiant du schéma.
     * @return la chaine de caractère de définition d'un schéma.
     */
    public String createSchema(String schemaId) {
        ST cre = template.getInstanceOf("schema_cre");
        cre.add("schema_id", schemaId);
        return cre.render();
    }

    /**
     * Créer un commentaire sur un schéma.
     *
     * @param schemaId : l'identifiant du schéma.
     * @param value    : le commentaire.
     * @return la chaine de caractère de définition du commentaire.
     */
    public String createSchemaComment(String schemaId, String value) {
        ST com = template.getInstanceOf("schema_comment_cre");
        com.add("schemaId", schemaId);
        // Ajouter un caractère d'echappement pour l'apostrophe
        value = value.replace("'", "''");
        com.add("value", value);
        return com.render();
    }

    /**
     * Supprimer un schéma.
     *
     * @param schemaId  : l'identifiant du schéma.
     * @param isCascade : suppression en cascade ou pas.
     * @return la chaine de caractère de suppression du schéma.
     */
    public String dropSchema(String schemaId, boolean isCascade){
        ST drp = template.getInstanceOf("schema_drp");
        drp.add("schemaId", schemaId);
        drp.add("isCascade", isCascade);
        return drp.render();
    }

    // **************************************************************************
    // Opérations publiques - Gabarits pour la gestion des domaines et types définis
    //
    /**
     * Créer un type défini (DOMAIN en SQL).
     *
     * @param schemaId  : l'identifiant du schéma auquel appartient le domaine.
     * @param domaineId : l'identifiant du domaine défini.
     * @param typeId    : l'identifiant du type prédéfini.
     * @param isNotNull : si annulable.
     * @return la chaine de caractère de définition du domaine défini.
     */
    public String createDomain(String schemaId, String domaineId, String typeId, boolean isBuiltIn, boolean isNotNull) {
        ST cre = template.getInstanceOf("domain_cre");
        cre.add("schema_id", schemaId);
        cre.add("domaine_id", domaineId);
        cre.addAggr("type.{id, isBuiltIn}", typeId, isBuiltIn);
        cre.add("isNotNull", isNotNull);
        return cre.render();
    }

    /**
     * Créer un commentaire sur un domaine.
     *
     * @param schemaId : l'identifiant du schéma auquel appartient le domaine.
     * @param value    : le commentaire.
     * @return la chaine de caractère de définition du commentaire.
     */
    public String createDomainComment(String schemaId, String domainId, String value) {
        ST com = template.getInstanceOf("domain_comment_cre");
        com.add("schema_id", schemaId);
        com.add("domaine_id", domainId);
        value = value.replace("'", "''");
        com.add("value", value);
        return com.render();
    }

    /**
     * Supprimer un type défini.
     *
     * @param schemaId  : l'identifiant du schéma auquel appartient le domaine.
     * @param domaineId : l'identifiant du domaine à supprimer
     * @return la chaine de caractère de suppression du domaine défini.
     */
    public String dropDomain(String schemaId, String domaineId, boolean isCascade) {
        ST drp = template.getInstanceOf("domain_drp");
        drp.add("schema_id", schemaId);
        drp.add("domaine_id", domaineId);
        drp.add("isCascade", isCascade);
        return drp.render();
    }

    // **************************************************************************
    // Opérations publiques - Gabarits pour la gestion d'une table
    //
    /**
     * Créer une table.
     *
     * @param schemaId : l'identifiant du schéma auquel appartient la table.
     * @param tableId  : l'identifiant de la table.
     * @param attSet   : les définitions des attributs (id, type).
     * @param pkId     : l'identifiant de la contrainte clé.
     * @param keySet   : les identifiants des attributs formant la clé primaire.
     * @return la chaine de caractère de définition de la table.
     */
    public String createTable(String schemaId, String tableId, Set<Attribute> attSet, String pkId,
                              Set<Attribute> keySet) {
        ST cre = template.getInstanceOf("table_cre");
        cre.add("schemaId", schemaId);
        cre.add("tableId", tableId);
        // Créer les attributs
        for (Attribute a : attSet) {
            cre.addAggr("attSet.{id, type, isBuiltIn}", a.getId(), a.getType().getId(), a.getType().isBuiltIn());
        }
        // Créer une contrainte clé primaire
        createPrimaryKeyConstraint(cre, pkId, keySet);
        return cre.render();
    }

    /**
     * Créer un commentaire sur une table.
     *
     * @param schemaId : l'identifiant du schéma auquel appartient la table.
     * @param tableId  : l'identifiant de la table.
     * @param value    : le commentaire.
     * @return la chaine de caractère de définition du commentaire.
     */
    public String createTableComment(String schemaId, String tableId, String value) {
        ST com = template.getInstanceOf("table_comment_cre");
        com.add("schemaId", schemaId);
        com.add("tableId", tableId);
        value = value.replace("'", "''");
        com.add("value", value);
        return com.render();
    }

    /**
     * Créer un commentaire sur un attribut d'une table.
     *
     * @param schemaId : l'identifiant du schéma
     * @param tableId  : l'identifiant de la table
     * @param attId    : l'identifiant de l'attribut
     * @param value    : le commentaire de l'attribut
     * @return la chaine de caractère de définition d'un commentaire de l'attribut.
     */
    public String createAttributeComment(String schemaId, String tableId, String attId, String value) {
        ST com = template.getInstanceOf("attribut_comment_cre");
        com.add("schemaId", schemaId);
        com.add("tableId", tableId);
        com.add("attId", attId);
        value = value.replace("'", "''");
        com.add("value", value);
        return com.render();
    }

    /**
     * Supprimer une table en cascade.
     *
     * @param schemaId : l'identifiant du schéma
     * @param tableId  : l'identifiant de la table.
     * @return la chaîne de caractère de suppression de la table.
     */
    public abstract String dropTable(String schemaId, String tableId);

    // **************************************************************************
    // Opérations publiques - Gabarits de gestion des vues.
    //

    /**
     * Créer une vue de nommage.
     * La vue représente une table avec un autre nom et des alias sur les attributs.
     *
     * @param viewSchemaId  : l'identifiant du schéma dans lequel il faut créer la vue.
     * @param viewId        : l'identifiant de la vue.
     * @param tableSchemaId : le schéma de la table d'origine.
     * @param tableId       : l'identifiant de la table d'origine.
     * @param attSet        : l'ensemble d'attributs (nom, alias).
     * @return la chaine de caractère de définition de la vue.
     */
    public String createNamingView(String viewSchemaId, String viewId, String tableSchemaId, String tableId,
                                   Map<String, String> attSet) {
        ST cre = template.getInstanceOf("view_naming_cre");
        cre.add("viewSchemaId", viewSchemaId);
        cre.add("viewId", viewId);
        cre.add("tableSchemaId", tableSchemaId);
        cre.add("tableId", tableId);
        for (Map.Entry<String, String> a : attSet.entrySet()) {
            cre.addAggr("attSet.{id, alias}", a.getKey(), a.getValue());
        }
        return cre.render();
    }

    /**
     * Créer un commentaire sur une vue.
     *
     * @param schemaId : l'identifiant du schéma dans lequel il faut créer la vue.
     * @param viewId   : l'identifiant de la vue.
     * @param value    : le commentaire.
     * @return la chaine de caractère de définition du commentaire.
     */
    public String createViewComment(String schemaId, String viewId, String value) {
        ST com = template.getInstanceOf("view_comment_cre");
        com.add("schema_id", schemaId);
        com.add("view_id", viewId);
        value = value.replace("'", "''");
        com.add("value", value);
        return com.render();
    }

    /**
     * Supprimer une vue en cascade.
     *
     * @param schemaId : l'identifiant du schéma
     * @param viewId   : l'identifiant de la vue.
     * @return la chaîne de caractère de suppression de la vue.
     */
    public abstract String dropView(String schemaId, String viewId);
    // **************************************************************************
    // Opérations publiques - Gabarits de gestion des contraintes.
    //
    /**
     * Créer une contrainte de clé primaire.
     * La contrainte clé primaire est générée à l'intérieur d'une définition de table.
     * NOTE de mise en oeuvre : la création s'effectue à partir du gabarit "mère".
     * Pas besoin d'instancier le gabarit spécifique pour la génération.
     *
     * @param table  : le gabarit de Créer d'une table en cours de traitement.
     * @param pkId   : l'identifiant de la contrainte clé.
     * @param keySet : les identifiants des attributs formant la clé primaire.
     */
    protected void createPrimaryKeyConstraint(ST table, String pkId, Set<Attribute> keySet) {
        table.add("pkId", pkId);
        keySet.forEach(a -> table.addAggr("keySet.{id}", a.getId()));
        table.render();
    }

    /**
     * Créer une contrainte de clé secondaire sur une table.
     * La création s'effectue avec la modification de la table (ALTER).
     *
     * @param schemaId     : l'identifiant du schéma auquel appartient la table.
     * @param constraintId : l'identifiant de la contrainte clé.
     * @param tableId      : l'identifiant de la table.
     * @param keySet       : les identifiants des attributs formant la clé.
     * @return la chaine de caractère de définition de la clé secondaire.
     */
    public String createUniqueConstraint(String schemaId, String constraintId, String tableId, Set<String> keySet) {
        ST cre = template.getInstanceOf("uk_constraint_cre");
        cre.add("schemaId", schemaId);
        cre.add("tableId", tableId);
        cre.add("constraintId", constraintId);
        keySet.forEach(a -> cre.addAggr("keySet.{id}", a));
        return cre.render();
    }

    /**
     * Créer une contrainte référentielle sur une table.
     * La création s'effectue avec la modification de la table (ALTER).
     *
     * @param constraintId     : l'identifiant de la contrainte.
     * @param srcTableSchemaId : l'identifiant du schéma auquel appartient la table source.
     * @param srcTableId       : l'identifiant de la table source.
     * @param srcTableAttSet   : les identifiants des attributs de la table d'origine formant la clé.
     * @param dstTableSchemaId : l'identifiant du schéma auquel appartient la table destination.
     * @param dstTableId       : l'identifiant de la table de destination.
     * @param dstTableAttSet   : les identifiants des attributs de la table destination formant la clé.
     * @return la chaine de caractère de définition de la contrainte référentielle.
     */
    public String createForeignKeyConstraint(String constraintId,
                                             String srcTableSchemaId, String srcTableId, Set<String> srcTableAttSet,
                                             String dstTableSchemaId, String dstTableId, Set<String> dstTableAttSet) {
        ST cre = template.getInstanceOf("fk_constraint_cre");
        cre.add("constraintId", constraintId);
        // Créer les attributs de la table source
        cre.add("srcTableSchemaId", srcTableSchemaId);
        cre.add("srcTableId", srcTableId);
        srcTableAttSet.forEach(a -> cre.addAggr("srcTableAttSet.{id}", a));
        // Créer les attributs de la table destination
        cre.add("dstTableSchemaId", dstTableSchemaId);
        cre.add("dstTableId", dstTableId);
        dstTableAttSet.forEach(a -> cre.addAggr("dstTableAttSet.{id}", a));
        return cre.render();
    }

    /**
     * Créer un commentaire sur une contrainte.
     *
     * @param constraintId : l'identifiant de la contrainte.
     * @param schemaId     : l'identifiant du schéma.
     * @param tableId      : l'identifiant de la table.
     * @param value        : le commentaire.
     * @return la chaine de caractère de définition du commentaire.
     */
    public String createCommentConstraint(String schemaId, String constraintId, String tableId, String value) {
        ST ref = template.getInstanceOf("constraint_comment_cre");
        ref.add("constraintId", constraintId);
        ref.add("schemaId", schemaId);
        ref.add("tableId", tableId);
        value = value.replace("'", "''");
        ref.add("value", value);
        return ref.render();
    }


    /**
     * Créer un commentaire sur un index.
     *
     * @param constraintId : l'identifiant du schéma auquel appartient l'index.
     * @param value        : le commentaire.
     * @return la chaine de caractère de définition du commentaire.
     */
    public String createCommentIndex(String schemaId, String constraintId, String value) {
        ST commentCheck = template.getInstanceOf("index_comment_cre");
        commentCheck.add("schemaId", schemaId);
        commentCheck.add("constraintId", constraintId);
        value = value.replace("'", "''");
        commentCheck.add("value", value);
        return commentCheck.render();
    }

    /**
     * Supprimer une contrainte
     *
     * @param schemaId     : l'identifiant du schéma auquel appartient la contrainte.
     * @param tableId      : l'identifiant de la table.
     * @param constraintId : l'identifiant de la contrainte.
     * @return la chaîne de caractère de suppression de la contrainte.
     */
    public String dropConstraint(String schemaId, String tableId, String constraintId) {
        ST drp = template.getInstanceOf("constraint_drp");
        drp.add("schemaId", schemaId);
        drp.add("tableId", tableId);
        drp.add("constraintId", constraintId);
        return drp.render();
    }
    // **************************************************************************

    /**
     * Créer une fonction de vérification d'une participation minimale d'un attribut.
     *
     * @param schemaId      : l'identifiant du schéma.
     * @param constraintId  : l'identifiant de la contrainte.
     * @param tableId       : l'identifiant de la table.
     * @param domainKeyId   : l'identifiant de la clé primaire de la table domaine (déterminante).
     * @param domainKeyType : l'identifiant du type de la clé primaire de la table domaine.
     * @param min           : la participation minimale.
     * @return : la chaine de caractère de définition de la fonction de vérification.
     */
    // TODO 2020-02-25 CK : ajouter isBuiltIn pour les type dans le template et corriger la fonction
    public String createMinParticipationCheckFunction(String schemaId, String constraintId, String tableId,
                                                      String domainKeyId, String domainKeyType, int min) {
        ST fct = template.getInstanceOf("minParticipationCheck_func");
        fct.add("constraintId", constraintId);
        fct.add("schemaId", schemaId);
        fct.add("tableId", tableId);
        fct.add("domainKey", new AttributeString(domainKeyId, domainKeyType));
        fct.add("min", min);
        return fct.render();
    }

    /**
     * Créer une fonction de vérification d'une participation maximale d'un attribut.
     *
     * @param schemaId      : l'identifiant du schéma.
     * @param constraintId  : l'identifiant de la contrainte.
     * @param tableId       : l'identifiant de la table.
     * @param domainKeyId   : l'identifiant de la clé primaire de la table domaine (déterminante).
     * @param domainKeyType : l'identifiant du type de la clé primaire de la table domaine
     * @param max           : participation maximale
     * @return : la chaine de caractère de définition de la fonction de vérification.
     */
    // TODO 2020-02-25 CK : ajouter isBuiltIn pour les types dans le template et corriger la fonction
    public String createMaxParticipationCheckFunction(String schemaId, String constraintId, String tableId,
                                                      String domainKeyId, String domainKeyType, int max) {
        ST fct = template.getInstanceOf("maxParticipationCheck_func");
        fct.add("constraintId", constraintId);
        fct.add("schemaId", schemaId);
        fct.add("tableId", tableId);
        fct.add("domainKey", new AttributeString(domainKeyId, domainKeyType));
        fct.add("max", max);
        return fct.render();
    }


    /**
     * Créer une fonction de vérification d'un axiome d'union
     * La contrainte d'union vérifie que tous les tuples de la table d'union sont les tuples provenant
     * de l'union des tuples des tables éléments de l'union.
     *
     * @param constraintId    : identifiant de la contrainte.
     * @param schemaId        : identifiant du schéma.
     * @param unionTableId    : identifiant de la table d'union.
     * @param unionElementSet : les pairs de l'identifiant de la table élément de l'union et de l'attribut clé.
     * @return : chaine de caractère de la définition de la fonction de vérification de l'union.
     */
    // TODO 2022-09-01 CK : utiliser dans ontorela. déplacer dans OntorelaSQLGenerator
    public String createUnionAxiomCheckFunction(String constraintId, String schemaId, String unionTableId,
                                                Map<String, String> unionElementSet) {
        ST checkUnionAxiom = template.getInstanceOf("unionAxiomCheck_func");
        checkUnionAxiom.add("contrainte_id", constraintId);
        checkUnionAxiom.add("schema_id", schemaId);
        checkUnionAxiom.add("unionTable_id", unionTableId);
        for (Map.Entry<String, String> e : unionElementSet.entrySet()) {
            ST selectKeys = template.getInstanceOf("selectKeys");
            selectKeys.add("schema_id", schemaId);
            selectKeys.add("table_id", e.getKey());
            selectKeys.addAggr("keySet.{id}", e.getValue());
            checkUnionAxiom.addAggr("elementSet.{exp}", selectKeys.render());
        }
        return checkUnionAxiom.render();
    }

    /**
     * Créer une fonction de vérification d'une contrainte d'appartenance.
     *
     * @param constraintId   : identifiant de la contrainte.
     * @param schemaId       : identifiant du schéma.
     * @param sourceTableId  : identifiant de la table à vérifier.
     * @param sourceAttId    : identifiant de l'attribut de la table à vérifier.
     * @param targetTableMap : les paires (targetTableId, targetAttId) identifiant de la table et de son
     *                       attribut utilisés pour la vérification.
     * @return : chaine de caractère de la définition de la fonction de vérification de l'appartenance.
     */
    // TODO 2022-09-01 CK : utiliser dans ontorela. déplacer dans OntorelaSQLGenerator
    public String createMembershipCheckFunction(String constraintId, String schemaId, String sourceTableId, String sourceAttId,
                                                Map<String, String> targetTableMap) {
        ST checkMembership = template.getInstanceOf("membershipCheck_func");
        checkMembership.add("contrainte_id", constraintId);
        checkMembership.add("schema_id", schemaId);
        checkMembership.add("sourceTable_id", sourceTableId);
        checkMembership.add("sourceAtt", sourceAttId);
        for (Map.Entry<String, String> e : targetTableMap.entrySet()) {
            checkMembership.addAggr("targetTableMap.{table_id, targetAtt}", e.getKey(), e.getValue());
        }
        return checkMembership.render();
    }

    // ***********************************************************************************************
    // Opérations publiques - MANIPULATION DU SCHÉMA
    //

    /**
     * Créer une requête.
     *
     * @param attributs : la liste des attributs de la projection.
     * @param relvars   : la liste des relvars de la sélection.
     * @return la chaine de caractère de la requête.
     */
    public String createQuery(ArrayList<String> attributs, ArrayList<String> relvars) {
        ST query = template.getInstanceOf("query");
        for (String a : attributs) {
            query.addAggr("projection.{exp}", a);
        }
        for (String r : relvars) {
            query.addAggr("selection.{exp}", r);
        }
        return query.render();
    }

    /**
     * Définition d'une instruction de suppression de données d'une table.
     *
     * @param schemaId: l'identifiant du schéma auquel appartient la table
     * @param tableId   : l'identifiant de la table
     * @return la chaine de caractère de suppression des données d'une table.
     */
    public String delete(String schemaId, String tableId) {
        ST del = template.getInstanceOf("delete");
        del.add("schemaId", schemaId);
        del.add("tableId", tableId);
        return del.render();
    }
}
