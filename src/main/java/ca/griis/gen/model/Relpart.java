package ca.griis.gen.model;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Relpart {

  // **************************************************************************
  // Attributs spécifiques
  //
  private Attribute attribute;
  private final Relvar relvar;
  private final PeriodCategory timelineCategory;
  // **************************************************************************
  // Constructeurs
  //

  public Relpart(Relvar relvar, PeriodCategory timelineCategory, Attribute attribute) {
    super();
    this.relvar = relvar;
    this.timelineCategory = timelineCategory;
    this.attribute = attribute;
  }

  public Relpart(Relvar relvar, PeriodCategory timelineCategory) {
    this(relvar, timelineCategory, null);
  }
  // **************************************************************************
  // Opérations propres
  //

  // **************************************************************************
  // Opérations publiques
  //
  public String getId() {
    return relvar.getId() + (this.attribute == null ? "" : "_" + getAttribute().getId()) + "_"
        + timelineCategory.getIdentifier();
  }

  public Relvar getRelvar() {
    return this.relvar;
  }

  public PeriodCategory getTimelineCategory() {
    return timelineCategory;
  }

  public Attribute getAttribute() {
    return attribute;
  }

  public void setAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

  /**
   * Obtenir l'ensemble des attributs temporels du relpart.
   *
   * @return L'ensemble d'attribut temporel du relpart.
   */
  public Set<Attribute> getTemporalAttributeSet() {
    return getTimelineCategory().getPostgreSQLAttribute();
  }

  /**
   * Obtenir l'ensemble des attributs temporels du relpart.
   *
   * @param isCalculable : l'attribut est calculable
   * @return L'ensemble d'attribut temporels calculable ou non de la relpart.
   */
  public Set<Attribute> getTemporalAttributeSet(boolean isCalculable) {
    return getTemporalAttributeSet().stream().filter(a -> a.isCalculable() == isCalculable)
        .collect(Collectors.toSet());
  }

  /**
   * Obtenir l'ensemble des attributs non temporels du relpart.
   *
   * @return L'ensemble des attributs non temporels du relpart.
   */
  public Set<Attribute> getNonTemporalAttributeSet() {
      Set<Attribute> attSet = new LinkedHashSet<>(getKeyAttributeSet(true));
    if (this.attribute != null) {
      attSet.add(this.attribute);
    }
    return attSet;
  }


  /**
   * Obtenir l'ensemble des attributs clé.
   *
   * @param orginalKeyOnly : les clés de la relation d'origine (du schéma d'origine)
   * @return L'ensemble des attributs clés de la relation d'origine ou tous les attributs clés de la
   * relpart.
   */
  public Set<Attribute> getKeyAttributeSet(boolean orginalKeyOnly) {
      Set<Attribute> keyAttSet = new LinkedHashSet<>(this.relvar.getKeyAttributeSet());
    if (!orginalKeyOnly) {
      keyAttSet.addAll(getTimelineCategory().getPostgreSQLAttribute().stream()
          .filter(a -> !a.isCalculable()).collect(Collectors.toSet()));
    }
    return keyAttSet;
  }

  /**
   * Obtenir tous les attributs du relpart.
   *
   * @param isCalculable : l'attribut est calculable
   * @return L'ensemble de tous les attributs calculables ou non de la relpart.
   */
  public Set<Attribute> getRelpartAttributeSet(boolean isCalculable) {
      Set<Attribute> attSet = new LinkedHashSet<>(getKeyAttributeSet(true));
    if (this.attribute != null) {
      attSet.add(this.attribute);
    }
    attSet.addAll(getTimelineCategory().getPostgreSQLAttribute().stream()
        .filter(a -> a.isCalculable() == isCalculable).collect(Collectors.toSet()));
    return attSet;
  }

  /**
   * Obtenir tous les attributs de la relpart (avec attributs calculables et non calculable).
   *
   * @return L'ensemble de tous les attributs de la relpart.
   */
  public Set<Attribute> getRelpartAttributeSet() {
    Set<Attribute> attSet = new LinkedHashSet<>();
    if (this.attribute != null) {
      attSet.add(this.attribute);
    }
    attSet.addAll(getKeyAttributeSet(true));
    attSet.addAll(getTimelineCategory().getPostgreSQLAttribute());
    return attSet;
  }

  public boolean isKeyRelpart() {
    return this.attribute == null;
  }

  // **************************************************************************
  // hashcode/equals/clone/toString
  //
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
    result = prime * result + ((relvar == null) ? 0 : relvar.hashCode());
    result = prime * result + ((timelineCategory == null) ? 0 : timelineCategory.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Relpart other = (Relpart) obj;
    if (attribute == null) {
      if (other.attribute != null)
        return false;
    } else if (!attribute.equals(other.attribute))
      return false;
    if (relvar == null) {
      if (other.relvar != null)
        return false;
    } else if (!relvar.equals(other.relvar))
      return false;
    if (timelineCategory == null) {
        return other.timelineCategory == null;
    } else return timelineCategory.equals(other.timelineCategory);
  }

  @Override
  public String toString() {
    return "Relpart [id=" + this.getId() + ", attId=" + (this.attribute == null ?
        "null" :
        this.attribute.getId()) + ", relvar=" + relvar.getId() + ", timelineCategory="
        + timelineCategory + "]";
  }

}
