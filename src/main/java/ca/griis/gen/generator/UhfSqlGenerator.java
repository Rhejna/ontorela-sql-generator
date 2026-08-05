package ca.griis.gen.generator;


import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.model.*;
import ca.griis.gen.process.SqlExecutor;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Générateur d'instruction de définition de composants SQL pour UHF.
 * <p>
 * <b>Propriétés des objets</b>
 * <ul>
 * <li>Unicité : oui.</li>
 * <li>Clonabilité : non.</li>
 * <li>Modifiabilité : non.</li>
 * </ul>
 *
 * <b>Tâches projetées</b><br>
 * TODO 2022-08-025 CK : ajuster le template selon le standard de programmation. <br>
 *
 * <b>Tâches réalisées</b><br>
 * 2022-08-25 (0.1.1) [CK] Révision et complément. <br>
 * 2022-02-17 (0.1.0) [RL] Mise en oeuvre initiale. <br>
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
 * @since 2019-03-16
 */
public class UhfSqlGenerator extends PostgreSqlGenerator {

  // **************************************************************************
  // Attributs spécifiques
  //
  private final DatabaseConfig dbConfig;
  private final UhfSqlTemplate sqlGen;
  private final Boolean includeVXX;
  private final String temporalIntervalType;
  private final String temporalPointType;
  private final String granularity;
  private final RelationCategory schemaTemporalCategory;

  // **************************************************************************
  // Constructeurs
  //
  public UhfSqlGenerator(SchemaModel schema, DatabaseConfig config) {
    super(schema, config);
    this.dbConfig = config;
    this.sqlGen = new UhfSqlTemplate();
    this.includeVXX = config.getIncludeVXX();
    this.temporalIntervalType = config.getTemporalIntervalType();
    this.granularity = config.getGranularity();
    this.temporalPointType = config.getTemporalPointType();
    this.schemaTemporalCategory = config.getRelationCategory();
  }

  // **************************************************************************
  // Opérations propres
  //

  /**
   * Caster l'attribut booléen en entier. <br>
   * Les contraintes Gist ne peuvent pas comparer des valeurs booléennes il faut convertir la valeur
   * en entier.
   *
   * @param attId : l'attribut a convertir
   * @return L'attribut avec un cast.
   */
  private String castBoolAttribute(String attId) {
    return "(" + attId + "::INT" + ")";
  }

  public void generateSQLScripts() {
    generateCreateSchemaScript();                  //000
    generateCreateDomainScript();                  //001
    generateCreatePeriodFunctionScript();          //002
    generateCreateTableScript();                   //110
    generateCreateHistoryViewScript();             //120
    generateCreateAssertionConstraintScript();     //130
    generateCreateTemporalFctScript();             //135
    generateCreateGroupingInsertProcedureScript(); //150
    generateDropTableScript();                     //910
    if(config.getEmiraScripts()){
      generateCreateEMIRAScripts();
    }
  }


  // **************************************************************************
  // Opérations propres redéfinies
  //
  @Override
  protected String generateCreateTableStatements() {
    StringBuilder s = new StringBuilder();
    //
    String schemaId = this.getSchema().getSchemaId();
    for (Relvar t : this.getSchema().getRelvarSet()) {
      // ====== Construire la relpart
      for (Relpart p : t.getRelpartSet()) {
        String tableId = p.getId();
        // ==== Créer l'ensemble des attributes
        // Les attributs initiaux
        Set<Attribute> attSet = new LinkedHashSet<>();
        attSet.addAll(p.getNonTemporalAttributeSet());
        attSet.addAll(p.getTemporalAttributeSet().stream().filter(
                a -> !a.getTimelineCategory().equals(PeriodCategory.ValidTimeCategory.Vxx))
            .collect(Collectors.toSet()));
        Set<Attribute> keySet = p.getKeyAttributeSet(false);
        // Identifiant de la clé primaire
        String pkId = tableId + "_pk";
        // ==== Générer les relparts de la table
        s.append(sqlTemplate.createTable(schemaId, tableId, attSet, pkId, keySet));
        // ==== Générer le commentaire
        s.append(
                sqlTemplate.createTableComment(schemaId, tableId, p.getId() + " relpart of " + t.getId()));
      }
    }
    s.append(generateCreateFkStatements());
    //
    return s.toString();
  }

  @Override
  protected String generateDropTableStatements() {
    StringBuilder s = new StringBuilder();
    //
    for (Relvar t : this.getSchema().getRelvarSet()) {
      for (Relpart p : t.getRelpartSet()) {
        s.append(sqlTemplate.dropTable(this.getSchema().getSchemaId(), p.getId()));
      }
    }
    return s.toString();
  }

  // **************************************************************************
  // Opérations propres pour l'historicisation.
  //

  /**
   * Créer les contraintes temporelles de non-redondance. Une contrainte est seulement créer pour une
   * relation unitemporelle ayant un attribut temporel be.
   *
   * @return Les instructions SQL pour créer les contraintes de non-redondance des tables.
   */
  // TODO 2020-02-27 CK : ajuster pour la bitemporalité
  protected String generateNoRedundancyConstraintSet() {
    StringBuilder s = new StringBuilder();
    //
    for (Relvar t : this.getSchema().getRelvarSet()) {
      for (Relpart p : t.getRelpartSet()) {
        if (!(p.getTimelineCategory() instanceof PeriodCategory.BitemporalVTCategory)) {
          Attribute vt =
              p.getTemporalAttributeSet(false).stream().findFirst().orElse(null);
          // Créer une contrainte pour les relparts ayant un attribute temporel non
          // calculable (Vbe).
          if (vt != null) {
            String constraintId = p.getId().replace(PeriodCategory.prefixe, "_");
            String vtId = vt.getId();
            Set<String> keyAttIdSet =
                p.getKeyAttributeSet(true).stream().map(Attribute::getId)
                    .collect(Collectors.toSet());
            //
            Attribute attId = p.getAttribute();
            String attStringId = null;
            if (attId != null) {
              attStringId = attId.getId();
            }
            // Créer la contrainte
            s.append(sqlGen.createNoRedundancyConstraint(constraintId,
                this.getSchema().getSchemaId(), p.getId(), keyAttIdSet, attStringId,
                false, vtId));
            // Créer le commentaire
            s.append(sqlGen.createCommentConstraint(this.getSchema().getSchemaId(),
                constraintId + "_redundancy", p.getId(), "Check redundancy constraint"));
          }
        }
      }
    }
    //
    return s.toString();
  }

  /**
   * Créer les contraintes temporelles de circonlocution. Une contrainte est seulement créer pour
   * une relation unitemporelle ayant un attribut temporel be.
   *
   * @return Les instructions SQL pour créer les contraintes de circonlocution des tables.
   */
  //TODO 2020-02-27 CK : ajuster pour la bitemporalité
  protected String generateNoCircumlocutionConstraintSet() {
    StringBuilder s = new StringBuilder();
    //
    for (Relvar t : this.getSchema().getRelvarSet()) {
      for (Relpart p : t.getRelpartSet()) {
        //si la relpart n'est pas de catégorie bitemporel
        if (!(p.getTimelineCategory() instanceof PeriodCategory.BitemporalVTCategory)) {
          Attribute vt =
              p.getTemporalAttributeSet(false).stream().findFirst().orElse(null);
          // Créer une contrainte pour les relpart ayant un attribute temporel non
          // calculable (Vbe).
          if (vt != null) {
            String constraintId = p.getId().replace(PeriodCategory.prefixe, "_");
            String vtId = vt.getId();
            Set<String> keyAttId =
                p.getKeyAttributeSet(true).stream().map(Attribute::getId)
                    .collect(Collectors.toSet());
            //
            if (p.isKeyRelpart()) {
              s.append(sqlGen.createNoCircumlocutionConstraint(constraintId,
                  this.getSchema().getSchemaId(), p.getId(), keyAttId, null, false,
                  vtId));
            } else {
              String attId = p.getAttribute().getId();
              boolean isFct = false;
              if (p.getAttribute().getType().getId().equals("BOOL")) {
                attId = castBoolAttribute(attId);
                isFct = true;
              }
              // Créer la contrainte
              s.append(sqlGen.createNoCircumlocutionConstraint(constraintId,
                  this.getSchema().getSchemaId(), p.getId(), keyAttId, attId, isFct,
                  vtId));
            }
            // Créer le commentaire
            s.append(sqlGen.createCommentConstraint(this.getSchema().getSchemaId(),
                constraintId + "_circumlocution", p.getId(), "Check circumlocution constraint"));
          }
        }
      }
    }
    //
    return s.toString();
  }

  /**
   * Créer les contraintes temporelles de non-contradiction. Une contrainte est seulement créer pour
   * une relation unitemporelle ayant un attribut temporel be. Cette contrainte n'est pas essentielle
   * pour une relpart clé parce qu'elle a la même définition de la non-redondance.
   *
   * @return Les instructions SQL pour créer les contraintes de non-redondance des tables.
   */
  //TODO 2020-02-27 CK : ajuster pour la bitemporalité
  protected String generateNoContradictionConstraintSet() {
    StringBuilder s = new StringBuilder();
    //
    for (Relvar t : this.getSchema().getRelvarSet()) {
      for (Relpart p : t.getRelpartSet()) {
        if (!(p.getTimelineCategory() instanceof PeriodCategory.BitemporalVTCategory)) {
          Attribute vt =
              p.getTemporalAttributeSet(false).stream().findFirst().orElse(null);
          // Créer une contrainte pour les relpart ayant un attribute temporel non
          // calculable (Vbe).
          if (vt != null) {
            // Cette contrainte n'est pas essentielle pour une relpart clé parce qu'elle a
            // la même définition de la non-redondance.
            if (!p.isKeyRelpart()) {
              String constraintId = p.getId().replace(PeriodCategory.prefixe, "_");
              String vtId = vt.getId();
              Set<String> keyAttId =
                  p.getKeyAttributeSet(true).stream().map(Attribute::getId)
                      .collect(Collectors.toSet());
              String attId = p.getAttribute().getId();
              boolean isFct = false;
              if (p.getAttribute().getType().getId().equals("BOOL")) {
                attId = castBoolAttribute(attId);
                isFct = true;
              }
              // Créer la contrainte
              s.append(sqlGen.createNoContradictionConstraint(constraintId,
                  this.getSchema().getSchemaId(), p.getId(), keyAttId, attId, isFct,
                  vtId));
              // Créer le commentaire
              s.append(sqlGen.createCommentConstraint(this.getSchema().getSchemaId(),
                  constraintId + "_contradiction", p.getId(), "Check contradiction constraint"));
            }
          }
        }
      }
    }
    //
    return s.toString();
  }

  protected String generateVPeriodFunction() {
    StringBuilder s = new StringBuilder();
    // === Parameters
    String schemaId = this.getSchema().getSchemaId();
    // Fonction pour Vxx
    s.append(sqlGen.createVxxPeriodFunction(schemaId,
        PeriodCategory.ValidTimeCategory.Vxx.getPostgreSQLTypeId(),
        DefinedType.getTemporalPeriodTypeId(), temporalIntervalType));
    // Fonction pour Vxe
    s.append(sqlGen.createVxePeriodFunction(schemaId,
        PeriodCategory.ValidTimeCategory.Vxe.getPostgreSQLTypeId(),
        DefinedType.getTemporalPeriodTypeId(), temporalIntervalType));
    // Fonction pour Vbx
    s.append(sqlGen.createVbxPeriodFunction(schemaId,
        PeriodCategory.ValidTimeCategory.Vbx.getPostgreSQLTypeId(),
        DefinedType.getTemporalPeriodTypeId(), temporalIntervalType));
    return s.toString();
  }

  /**
   * Créer les fonctions de construction de périodes temporelles.
   *
   * @return Les instructions SQL pour créer les fonctions de construction de périodes temporelles.
   */
  protected String generateCreatePeriodFunctionSet() {
    StringBuilder s = new StringBuilder();
    // === Parameters
    String schemaId = this.getSchema().getSchemaId(); //  ;)
    // === Functions
    List<String> SchemaSet = Arrays.asList(schemaId, "public");
    s.append(sqlGen.createSearchPathFunction(SchemaSet));
    if (Objects.equals(schemaTemporalCategory.toString(), "V"))
      s.append(generateVPeriodFunction());
    // Fonction pour convertir deux point en interval
    s.append(sqlGen.createIntervalFunction(schemaId, DefinedType.getTemporalPointTypeId(),
        DefinedType.getTemporalPeriodTypeId(), "b", "e", temporalIntervalType));
    // first & last functions
    if (Objects.equals(temporalPointType.toUpperCase(), "INT")) {
      s.append(sqlGen.createFirstFunction(schemaId, "0", DefinedType.getTemporalPointTypeId(),
          DefinedType.getTemporalPointTypeId()));
      s.append(sqlGen.createLastFunction(schemaId, "2147483647",
          DefinedType.getTemporalPointTypeId(), DefinedType.getTemporalPointTypeId()));
    }
    if (Objects.equals(temporalPointType.toUpperCase(), "TIMESTAMP")) {
      s.append(sqlGen.createFirstFunction(schemaId, "\'1800-01-01\'",
          DefinedType.getTemporalPointTypeId(), DefinedType.getTemporalPointTypeId()));
      s.append(sqlGen.createLastFunction(schemaId, "\'9999-12-31\'",
          DefinedType.getTemporalPointTypeId(), DefinedType.getTemporalPointTypeId()));
    }
    // Fonctions PRE point
    // FIXME 2022-08-29 CK : la granuralité dépend du type temporel. Il ne peut pas être toujours INTERVAL.
    if (Objects.equals(temporalPointType.toUpperCase(), "INT")) {
      s.append(sqlGen.createPointPreFunction(schemaId, DefinedType.getTemporalPointTypeId(),
          DefinedType.getTemporalPointTypeId(), null));
    }
    if (Objects.equals(temporalPointType.toUpperCase(), "TIMESTAMP")) {
      s.append(sqlGen.createPointPreFunction(schemaId, DefinedType.getTemporalPointTypeId(),
          DefinedType.getTemporalPointTypeId(), granularity));
    }
    // Fonctions POST point
    s.append(sqlGen.createPointPostFunction(schemaId, DefinedType.getTemporalPointTypeId(),
        DefinedType.getTemporalPointTypeId(), granularity));
    // FIXME 2022-08-29 CK : la granuralité dépend du type temporel. Il ne peut pas être toujours INTERVAL.
    if (Objects.equals(temporalPointType.toUpperCase(), "INT")) {
      s.append(sqlGen.createPointPostFunction(schemaId, DefinedType.getTemporalPointTypeId(),
          DefinedType.getTemporalPointTypeId(), null));
    }
    if (Objects.equals(temporalPointType.toUpperCase(), "TIMESTAMP")) {
      s.append(sqlGen.createPointPostFunction(schemaId, DefinedType.getTemporalPointTypeId(),
          DefinedType.getTemporalPointTypeId(), granularity));
    }
    //ibegin & iend functions
    s.append(sqlGen.createIbeginFunction(schemaId, DefinedType.getTemporalPeriodTypeId(),
        DefinedType.getTemporalPointTypeId(), DefinedType.getTemporalPointTypeId()));
    s.append(sqlGen.createIendFunction(schemaId, DefinedType.getTemporalPeriodTypeId(),
        DefinedType.getTemporalPointTypeId(), DefinedType.getTemporalPointTypeId()));
    // Fonctions PRE interval
    s.append(sqlGen.createIntervalPreFunction(schemaId, DefinedType.getTemporalPeriodTypeId(),
        DefinedType.getTemporalPointTypeId(), granularity));
    // Fonctions POST interval
    s.append(sqlGen.createIntervalPostFunction(schemaId, DefinedType.getTemporalPeriodTypeId(),
        DefinedType.getTemporalPointTypeId(), granularity));
    // Fonction Unpack
    s.append(sqlGen.createUnpackIntervalPointFunction(schemaId,
        DefinedType.getTemporalPeriodTypeId(), DefinedType.getTemporalPointTypeId(),
        DefinedType.getTemporalPointTypeId()));
    // FIXME 2022-08-29 CK : mettre le generate_series dans le template.
    // FIXME 2022-08-29 CK : l'incrément du generate_series dépendant du type POINT.
    //  voir https://www.postgresql.org/docs/current/functions-srf.html
    if (Objects.equals(schemaTemporalCategory.toString(), "V")) {
      s.append(sqlGen.createUnpackFunction(schemaId,
          PeriodCategory.ValidTimeCategory.Vbx.getPostgreSQLTypeId(),
          DefinedType.getTemporalPointTypeId(), DefinedType.getTemporalPointTypeId(),
          "generate_series(a, last(), 1)"));
      s.append(sqlGen.createUnpackFunction(schemaId,
          PeriodCategory.ValidTimeCategory.Vxe.getPostgreSQLTypeId(),
          DefinedType.getTemporalPointTypeId(), DefinedType.getTemporalPointTypeId(),
          "generate_series(first(), a, 1)"));
      //fonction unpack à deux paramètres
      s.append(sqlGen.createUnpackFunction(schemaId,
          PeriodCategory.ValidTimeCategory.Vbx.getPostgreSQLTypeId(),
          DefinedType.getTemporalPointTypeId(), DefinedType.getTemporalPointTypeId(),
          DefinedType.getTemporalPointTypeId(), "generate_series(a, b, 1)"));
      s.append(sqlGen.createUnpackFunction(schemaId,
          PeriodCategory.ValidTimeCategory.Vxe.getPostgreSQLTypeId(),
          DefinedType.getTemporalPointTypeId(), DefinedType.getTemporalPointTypeId(),
          DefinedType.getTemporalPointTypeId(), "generate_series(b, a, 1)"));
    }
    return s.toString();
  }

  protected String generateHistoryViewSet() {
    StringBuilder s = new StringBuilder();
    //
    for (Relvar t : this.getSchema().getRelvarSet()) {
      // ==== Key grouping
      Set<Relpart> keyRepartSet = t.getKeyGrouping();
      Relpart keyGrouping = keyRepartSet.stream().findFirst().get();
      String keyGroupingId = keyGrouping.getId().substring(0,
          keyGrouping.getId().indexOf("_" + keyGrouping.getTimelineCategory().getAlias()));
      Set<AttributeString> keyAttSet = new LinkedHashSet<>();
      for (Attribute a : keyRepartSet.stream().findFirst().get().getKeyAttributeSet(true)) {
        keyAttSet.add(new AttributeString(a.getId(), null, false));
      }
      // Définir les attributs temporelles pour faire l'union
      AttributeString vxxAtt = null;
      if (includeVXX) {
        vxxAtt = new AttributeString("uhf_interval()", "validtime", true);
      }
      AttributeString vxeAtt = new AttributeString(
          "uhf_interval(\"" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier() + "\")",
          "validtime", true);
      AttributeString vbxAtt = new AttributeString(
          "uhf_interval(\"" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier() + "\")",
          "validtime", true);
      AttributeString vbeAtt =
          new AttributeString(PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
              "validtime", false);
      // Construire la vue
      s.append(
          sqlGen.createHistoryView(this.getSchema().getSchemaId(), keyGroupingId + "_history",
              keyAttSet,
              keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxx.getIdentifier(),
              vxxAtt,
              keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
              vxeAtt,
              keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
              vbxAtt,
              keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
              vbeAtt));
      // ==== Ai grouping
      for (Attribute a : t.getNoKeyAttributeSet()) {
        Set<Relpart> repartSet = t.getAttributeGrouping(a);
        Relpart aiGrouping = repartSet.stream().findFirst().get();
        String aiGroupingId = aiGrouping.getId().substring(0,
            aiGrouping.getId().indexOf("_" + aiGrouping.getTimelineCategory().getAlias()));
        Set<AttributeString> attSet = new LinkedHashSet<>(keyAttSet);
        attSet.add(new AttributeString(a.getId(), null, false));
        // Construire la vue
        s.append(sqlGen.createHistoryView(this.getSchema().getSchemaId(),
            aiGroupingId + "_history", attSet,
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxx.getIdentifier(),
            vxxAtt,
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
            vxeAtt,
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
            vbxAtt,
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
            vbeAtt));
      }
    }
    //
    return s.toString();
  }

  protected String generateCreateTemporalFct() {
    StringBuilder s = new StringBuilder();
    // === Parameters
    String schemaId = this.getSchema().getSchemaId();
    //
    for (Relvar t : this.getSchema().getRelvarSet()) {
      for (Relpart p : t.getRelpartSet()) {
        Attribute vt = p.getTemporalAttributeSet(false).stream().findFirst().orElse(null);
        // Créer une contrainte pour les relpart ayant un attribute temporel non
        // calculable (Vbe).
        if (vt != null) {
          Set<String> keyAttId = p.getKeyAttributeSet(true).stream().map(Attribute::getId)
              .collect(Collectors.toSet());
          //
          s.append(sqlGen.createTemporalNoRedundancyCheck(p.getId(), schemaId, keyAttId));
          s.append(
              sqlGen.createTemporalNoCircumlocutionCheck(p.getId(), schemaId, keyAttId));
          //
          if (!p.isKeyRelpart()) {
            keyAttId = p.getKeyAttributeSet(true).stream().map(Attribute::getId)
                .collect(Collectors.toSet());
            String attId = p.getAttribute().getId();
            boolean isFct = false;
            if (p.getAttribute().getType().getId().equals("BOOL")) {
              attId = castBoolAttribute(attId);
              isFct = true;
            }
            s.append(
                sqlGen.createTemporalNoContradictionCheck(p.getId(), schemaId, keyAttId,
                    attId, isFct));
          }
        }
      }
    }
    //
    return s.toString();
  }

  /**
   * Créer les fonctions de vérification des contraintes d'unicité de l'histoire et la circumlocution
   * de l'histoire entre les relparts d'une relation.
   *
   * @return Les instructions SQL pour créer les fonctions de vérification des contraintes des
   * conhérences temporelles.
   */
  protected String generateCreateTemporalCoherenceFctSet() {
    StringBuilder s = new StringBuilder();
    //
    for (Relvar t : this.getSchema().getRelvarSet()) {
      // ==== Key grouping
      Set<Relpart> keyRelpartSet = t.getKeyGrouping();
      Relpart keyGrouping = keyRelpartSet.stream().findFirst().get();
      String keyGroupingId = keyGrouping.getId();
      keyGroupingId = keyGroupingId.substring(0,
          keyGroupingId.indexOf("_" + keyGrouping.getTimelineCategory().getAlias()));
      Set<AttributeString> keyAttSet = new LinkedHashSet<>();
      for (Attribute a : keyRelpartSet.stream().findFirst().get().getKeyAttributeSet(true)) {
        keyAttSet.add(new AttributeString(a.getId(), a.getType().getId(),
            a.getType().isBuiltIn()));
      }
      String vxeAtt = "\"" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier() + "\"";
      String vbeAtt = "\"" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier() + "\"";
      String vbxAtt = "\"" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier() + "\"";
      // Créer uniqueness and circumlocution and contradiction test functions
      s.append(
          sqlGen.createTemporalUniquenessCheck(this.getSchema().getSchemaId(), keyGroupingId,
              t.getId(),
              keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
              keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
              keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
              vxeAtt, vbeAtt, vbxAtt, keyAttSet));
      s.append(sqlGen.createTemporalCircumlocutionCheck(this.getSchema().getSchemaId(),
          keyGroupingId, t.getId(),
          keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
          keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
          keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(), vxeAtt,
          vbeAtt, vbxAtt, keyAttSet));
      // ==== Ai grouping
      for (Attribute a : t.getNoKeyAttributeSet()) {
        Set<Relpart> repartSet = t.getAttributeGrouping(a);
        Relpart aiGrouping = repartSet.stream().findFirst().get();
        String aiGroupingId = aiGrouping.getId();
        aiGroupingId = aiGroupingId.substring(0,
            aiGroupingId.indexOf("_" + aiGrouping.getTimelineCategory().getAlias()));
        Set<AttributeString> attSet = new LinkedHashSet<>();
        attSet.addAll(keyAttSet);
        attSet.add(new AttributeString(a.getId(), a.getType().getId(),
            a.getType().isBuiltIn()));
        // Créer uniqueness and circumlocution test functions
        s.append(sqlGen.createTemporalUniquenessCheck(this.getSchema().getSchemaId(),
            aiGroupingId, t.getId(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
            vxeAtt, vbeAtt, vbxAtt, keyAttSet));
        s.append(sqlGen.createTemporalCircumlocutionCheck(this.getSchema().getSchemaId(),
            aiGroupingId, t.getId(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
            vxeAtt, vbeAtt, vbxAtt, keyAttSet));
        s.append(sqlGen.createTemporalContradictionCheck(this.getSchema().getSchemaId(),
            aiGroupingId, t.getId(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
            vxeAtt, vbeAtt, vbxAtt, keyAttSet, a.getId()));
        s.append(
            sqlGen.createTemporalDensenessCheck(this.getSchema().getSchemaId(), t.getId(),
                aiGroupingId, DefinedType.getTemporalPointTypeId(),
                PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
                PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
                PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(), keyAttSet));
      }
    }
    //
    return s.toString();
  }

  /**
   * Créer les fonctions d'insertion de données par groupement de relparts.
   *
   * @return Les instructions SQL pour créer les fonctions d'insertions par groupement de relparts.
   */
  protected String generateCreateGroupingInsertProcSet() {
    StringBuilder s = new StringBuilder();
    //
    for (Relvar t : this.getSchema().getRelvarSet()) {
      // ==== Key grouping
      Set<Relpart> keyRepartSet = t.getKeyGrouping();
      String keyGroupingId = keyRepartSet.stream().findFirst().get().getId();
      keyGroupingId = keyGroupingId.substring(0, keyGroupingId.indexOf(
          "_" + keyRepartSet.stream().findFirst().get().getTimelineCategory().getAlias()));
      Set<AttributeString> keyAttSet = new LinkedHashSet<>();
      for (Attribute a : keyRepartSet.stream().findFirst().get().getKeyAttributeSet(true)) {
        keyAttSet.add(new AttributeString(a.getId(), a.getType().getId(),
            a.getType().isBuiltIn()));
      }
      //
      AttributeString temporalAtt =
          new AttributeString(schemaTemporalCategory.getFullName(),
              BuiltInType.getTemporalPeriodType().getId());
      //
      String vxxRelpartId = null;
      if (includeVXX) {
        vxxRelpartId =
            keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxx.getIdentifier();
      }
      s.append(sqlGen.createGroupingInsert(this.getSchema().getSchemaId(), keyGroupingId,
          temporalAtt, keyAttSet, vxxRelpartId,
          keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
          keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
          keyGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier()));
      // ==== Ai grouping
      for (Attribute a : t.getNoKeyAttributeSet()) {
        Set<Relpart> repartSet = t.getAttributeGrouping(a);
        Relpart relpart = repartSet.stream().findFirst().get();
        String aiGroupingId = relpart.getId();
        aiGroupingId = aiGroupingId.substring(0,
            aiGroupingId.indexOf("_" + relpart.getTimelineCategory().getAlias()));
        Set<AttributeString> attSet = new LinkedHashSet<>();
        attSet.addAll(keyAttSet);
        attSet.add(new AttributeString(a.getId(), a.getType().getId(),
            a.getType().isBuiltIn()));
        //
        if (includeVXX) {
          vxxRelpartId =
              aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxx.getIdentifier();
        }
        s.append(sqlGen.createGroupingInsert(this.getSchema().getSchemaId(), aiGroupingId,
            temporalAtt, attSet, vxxRelpartId,
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier()));
        s.append(sqlGen.createAttInsert("attr_insert_vxe", this.getSchema().getSchemaId(),
            aiGroupingId, temporalAtt, attSet, keyAttSet, a.getId(), vxxRelpartId,
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
            temporalPointType, temporalIntervalType));
        s.append(
            sqlGen.createAttInsert("attr_insert_vxe_vbe", this.getSchema().getSchemaId(),
                aiGroupingId, temporalAtt, attSet, keyAttSet, a.getId(), vxxRelpartId,
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
                temporalPointType, temporalIntervalType));
        s.append(
            sqlGen.createAttInsert("attr_insert_vxe_vbx", this.getSchema().getSchemaId(),
                aiGroupingId, temporalAtt, attSet, keyAttSet, a.getId(), vxxRelpartId,
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
                temporalPointType, temporalIntervalType));
        s.append(sqlGen.createAttInsert("attr_insert_vbe", this.getSchema().getSchemaId(),
            aiGroupingId, temporalAtt, attSet, keyAttSet, a.getId(), vxxRelpartId,
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
            temporalPointType, temporalIntervalType));
        s.append(
            sqlGen.createAttInsert("attr_insert_vbe_vbe", this.getSchema().getSchemaId(),
                aiGroupingId, temporalAtt, attSet, keyAttSet, a.getId(), vxxRelpartId,
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
                temporalPointType, temporalIntervalType));
        s.append(
            sqlGen.createAttInsert("attr_insert_vbe_vbx", this.getSchema().getSchemaId(),
                aiGroupingId, temporalAtt, attSet, keyAttSet, a.getId(), vxxRelpartId,
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
                aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
                temporalPointType, temporalIntervalType));
        s.append(sqlGen.createAttInsert("attr_insert_vbx", this.getSchema().getSchemaId(),
            aiGroupingId, temporalAtt, attSet, keyAttSet, a.getId(), vxxRelpartId,
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
            aiGroupingId + "_" + PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
            temporalPointType, temporalIntervalType));
      }
    }
    //
    return s.toString();
  }

  /**
   * Créer les procedures d'insertion de données par groupement de relparts.
   *
   * @return Les instructions SQL pour créer les fonctions d'insertions par groupement de relparts.
   */
  protected String generateCreateValidateInsertProcSet() {
    StringBuilder s = new StringBuilder();
    //
    for (Relvar t : this.getSchema().getRelvarSet()) {
      // ==== Key grouping
      Set<Relpart> keyRepartSet = t.getKeyGrouping();
      Set<AttributeString> keyAttSet = new LinkedHashSet<>();
      for (Attribute a : keyRepartSet.stream().findFirst().get().getKeyAttributeSet(true)) {
        keyAttSet.add(new AttributeString(a.getId(), a.getType().getId(),
            a.getType().isBuiltIn()));
      }
      //
      AttributeString temporalAtt =
          new AttributeString(schemaTemporalCategory.getFullName(),
              BuiltInType.getTemporalPeriodType().getId());
      AttributeString vbxAtt =
          new AttributeString(PeriodCategory.ValidTimeCategory.Vbx.getIdentifier(),
              PeriodCategory.ValidTimeCategory.Vbx.getPostgreSQLTypeId());
      AttributeString vxeAtt =
          new AttributeString(PeriodCategory.ValidTimeCategory.Vxe.getIdentifier(),
              PeriodCategory.ValidTimeCategory.Vxe.getPostgreSQLTypeId());
      AttributeString vbeAtt =
          new AttributeString(PeriodCategory.ValidTimeCategory.Vbe.getIdentifier(),
              PeriodCategory.ValidTimeCategory.Vbe.getPostgreSQLTypeId());
      //
      Set<AttributeString> attSet = new LinkedHashSet<>();
      Set<AttributeString> noKeyAttSet = new LinkedHashSet<>();
      attSet.addAll(keyAttSet);
      // ==== Ai grouping
      for (Attribute a : t.getNoKeyAttributeSet()) {
        attSet.add(new AttributeString(a.getId(), a.getType().getId(),
            a.getType().isBuiltIn()));
        noKeyAttSet.add(new AttributeString(a.getId(), a.getType().getId(),
            a.getType().isBuiltIn()));
      }
      s.append(
          sqlGen.createValidateInsert(this.getSchema().getSchemaId(), t.getId(), temporalAtt,
              vbxAtt, vxeAtt, vbeAtt, attSet, keyAttSet, noKeyAttSet, temporalPointType));
    }
    //
    return s.toString();
  }

  // **************************************************************************
  // Opérations publiques générales
  //

  /**
   * Génération du script SQL de création des fonctions d'intervalles.
   * TODO 2022-08-25 CK : revoir au complet selon UHF_Base.
   *
   * @return Un script SQL qui contient les instructions de création des fonctions d'intervalles.
   */
  public File generateCreatePeriodFunctionScript() {
    //
    String subject = "Create interval utility functions";
    String filePrefix = "002-interval-function_cre";
    File script = generateScript(filePrefix, subject, generateCreatePeriodFunctionSet());
    //
    if (this.dbConfig.getExecuteScript()) {
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
   * TODO 2022-08-25 CK : générer dans l'ordre : since-during-until
   *
   * @return Un script SQL qui contient les instructions de création des tables.
   */
  @Override
  public File generateCreateTableScript() {
    //
    String subject = "Create tables";
    String filePrefix = "110-table_cre";
    File script = generateScript(filePrefix, subject, generateCreateTableStatements());
    //
    if (this.dbConfig.getExecuteScript()) {
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
   * Génération du script SQL de création des vues
   *
   * @return Un script SQL qui contient les instructions de création des vues.
   */
  public File generateCreateHistoryViewScript() {
    StringBuilder content = new StringBuilder();
    //
    content.append(generateHistoryViewSet());
    //
    String subject = "Create history views";
    String filePrefix = "120-history-views_cre";
    File script = generateScript(filePrefix, subject, content.toString());
    //
    if (this.dbConfig.getExecuteScript()) {
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
   * Génération du script SQL de création des contraintes temporelles.
   *
   * @return Un script SQL qui contient les instructions de création des contraintes temporelles
   */
  public File generateCreateAssertionConstraintScript() {
    StringBuilder content = new StringBuilder();
    //
    content.append(generateNoRedundancyConstraintSet());
    content.append(generateNoCircumlocutionConstraintSet());
    content.append(generateNoContradictionConstraintSet());
    //
    String subject = "Create temporal constraints";
    String filePrefix = "130-assertion-constraint_cre";
    File script = generateScript(filePrefix, subject, content.toString());
    //
    if (this.dbConfig.getExecuteScript()) {
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
   * Génération du script SQL de création des fonctions d'assertions temporelles.
   *
   * @return Un script SQL qui contient les instructions de création d'assertions temporelles.
   */
  public File generateCreateTemporalFctScript() {
    StringBuilder content = new StringBuilder();
    //
    content.append(generateCreateTemporalFct());
    //content.append(generateCreateTemporalForeignKeyFctSet());
    content.append(generateCreateTemporalCoherenceFctSet());
    //
    String subject = "Create the assertions functions\n"
        + "  Assertion that cannot be defined directly using constraints";
    String filePrefix = "135-assertion-function_cre";
    File script = generateScript(filePrefix, subject, content.toString());
    //
    if (this.dbConfig.getExecuteScript()) {
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
   * Génération du script SQL de création des procedures d'insertions par groupement de relparts
   *
   * @return Un script SQL qui contient les instructions de création des procedures d'insertions.
   */
  public File generateCreateGroupingInsertProcedureScript() {
    StringBuilder content = new StringBuilder();
    //
    content.append(generateCreateGroupingInsertProcSet());
    content.append(generateCreateValidateInsertProcSet());
    //
    String subject = "Create insert function per relation grouping";
    String filePrefix = "150-ins_grouping_proc_cre";
    File script = generateScript(filePrefix, subject, content.toString());
    //
    if (this.dbConfig.getExecuteScript()) {
      try{
        SqlExecutor.executeScript(script, this.config);
      }
      catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    return script;
  }
}
