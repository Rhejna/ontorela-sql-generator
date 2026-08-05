package ca.griis.gen.model;

import org.jgrapht.graph.DirectedMultigraph;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Générateur d'instruction de définition de composants SQL pour UHF.
 * <p>
 * <b>Propriétés des objets</b>
 * <ul>
 * <li>Unicité : non.</li>
 * <li>Clonalité : non.</li>
 * <li>Modifiabilité : oui.</li>
 * </ul>
 *
 * <b>Tâches projetées</b><br>
 * TODO 2022-08-29 CK : exporter les graphe en .dot <br>
 * TODO 2023-08-23 RL : table du schéma de base a un clé étrangère qui se réfère à un autre table dun autre schéma.
 *  - Ce problème est arrivé avec la génération du schéma rh dans test-data/example/tp1.
 * <b>Tâches réalisées</b><br>
 * 2022-08-25 (0.1.1) [CK] Révision et complément. <br>
 * 2022-02-17 (0.1.0) [RL] Mise en oeuvre initiale. <br>
 *
 * <p>
 * <b>Copyright</b> 2016-2017, (<a href="https://griis.ca/">GRIIS</a>) <br>
 * GRIIS (Groupe de recherche interdisciplinaire en informatique de la santé) <br>
 * Faculté des sciences et Faculté de médecine et sciences de la santé <br>
 * Université de Sherbrooke (Québec) J1K 2R1 <br>
 * CANADA <br>
 * [(<a href="http://creativecommons.org/licenses/by-nc/3.0">CC-BY-NC-3.0</a>)]
 * </p>
 *
 * @author [CK] Christina.Khnaisser@USherbrooke.ca
 * @author [RL] Remi.Letourneau@USherbrooke.ca
 * @version 0.1.0
 * @since 2019-03-16
 */
public class SchemaModel {
  // **************************************************************************
  // Attributs spécifiques
  //
  private final String schemaId;
  private final Set<DefinedType> definedTypeSet;
  private final Set<Relvar> relvarSet;
  private final Set<ReferentialKey> referentialKeySet;

  // **************************************************************************
  // Constructeurs
  //
  public SchemaModel(String schemaId) {
    super();
    this.schemaId = schemaId;
    this.definedTypeSet = new LinkedHashSet<>();
    this.relvarSet = new LinkedHashSet<>();
    this.referentialKeySet = new LinkedHashSet<>();
  }

  // **************************************************************************
  // Opérations propres
  //
  // **************************************************************************
  // Opérations publiques
  //

  public String getSchemaId() {
    return this.schemaId;
  }

  // ***** Defined types
  public Set<DefinedType> getDefinedTypeSet() {
    return definedTypeSet;
  }

  // ***** Relvar
  public void addRelvar(Relvar r) {
    this.relvarSet.add(r);
  }

  // TODO 2020-02-24 CK : gérer si le relvar n'est pas trouvé.
  public Relvar getRelvar(String id) {
    return this.relvarSet.stream()
            .filter(r -> r.getId().equals(id))
            .findFirst().get();
  }

  public Set<Relvar> getRelvarSet() {
    return this.relvarSet;
  }

  // ***** ReferentialKey
  public void addReferentialKey(ReferentialKey rk) {
    this.referentialKeySet.add(rk);
  }

  public void addReferentialKey(Set<ReferentialKey> rkSet) {
    for (ReferentialKey rk : rkSet) {
      addReferentialKey(rk);
    }
  }

  public Set<ReferentialKey> getReferentialKeySet() {
    return this.referentialKeySet;
  }

  public Set<ReferentialKey> getReferentialKeySet(Relvar sourceRelvar) {
    return this.referentialKeySet.stream().filter(rk -> rk.getSourceRelvar().equals(sourceRelvar))
        .collect(Collectors.toSet());
  }

  public DirectedMultigraph<Relvar, ReferentialKey> getRelvaGraph() {
    DirectedMultigraph<Relvar, ReferentialKey> graph = new DirectedMultigraph<>(ReferentialKey.class);
    for (Relvar r : this.getRelvarSet()) {
      graph.addVertex(r);
    }
    for (ReferentialKey rk : this.getReferentialKeySet()) {
      try {
        graph.addEdge(rk.getSourceRelvar(), rk.getTargetRelvar(), rk);
      } catch (IllegalArgumentException e) {
        System.err.println(e.getMessage() + " :" + rk.getDefinition());
      }
    }
    return graph;
  }

  //TODO 2022-09-29 CK : faire le graphe des relparts.
  /*
  public DirectedMultigraph<Relpart, ReferentialKey> getRelpartGraph() {
    DirectedMultigraph<Relpart, ReferentialKey> graph = new DirectedMultigraph<>(ReferentialKey.class);
    for (Relvar r : this.getRelvarSet()) {
      for (Relpart p : r.getRelpartSet())
        graph.addVertex(p);
    }
    for (ReferentialKey rk : this.getReferentialKeySet()) {
      try {
        graph.addEdge(rk.getSourceRelvar(), rk.getTargetRelvar(), rk);
      } catch (IllegalArgumentException e) {
        System.err.println(e.getMessage() + " :" + rk.getDefinition());
      }
    }
    return graph;
  }*/

  public String getSchemaDescription() {
    StringBuilder d = new StringBuilder();
    for (Relvar r : this.relvarSet) {
      d.append("  RELVAR ").append(r.getId()).append(r.getCategory().getIdentifier());
      // Lister les attributs
      d.append("\n    {");
      for (Attribute a : r.getAttributeSet()) {
        d.append(a.getId()).append(" ").append(a.getType().getId());
      }
      d.append("}");
      // Lister les clés candidates
      d.append("\n   KEY {").append(r.getKeyAttributeSet().stream().map(Attribute::getId).collect(Collectors.joining(","))).append("}");
      // Lister les clés référentielles
      d.append("\n   FOREIGN KEY {").append(getReferentialKeySet(r).stream().map(ReferentialKey::getDefinition).collect(Collectors.joining(",")));
      d.append("}\n");
    }
    return d.toString();
  }
}
