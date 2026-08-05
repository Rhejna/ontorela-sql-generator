package ca.griis.gen.model;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Relvar {
  // **************************************************************************
  // Attributs spécifiques
  //
  private final String id;
  private final RelationCategory category;
  private Set<Attribute> attributeSet;
  private final Set<Attribute> keyAttributeSet;
  private final Set<Relpart> relpartSet;

  // **************************************************************************
  // Constructeurs
  //
  public Relvar(String id, RelationCategory category, Set<Attribute> attributeSet,
                Set<Attribute> keyAttributeSet, Set<Relpart> relpartSet) {
    super();
    this.id = id;
    this.category = category;
    this.attributeSet = attributeSet;
    this.keyAttributeSet = keyAttributeSet;
    this.relpartSet = relpartSet;
  }

  public Relvar(String id, RelationCategory category, Set<Attribute> attributeSet,
                Set<Attribute> keyAttributeSet) {
    this(id, category, attributeSet, keyAttributeSet, new LinkedHashSet<>());
  }

  public Relvar(String id, RelationCategory category) {
    this(id, category, new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>());
  }

  // **************************************************************************
  // Opérations propres
  //
  // **************************************************************************
  // Opérations publiques
  //
  public void addAttribute(Attribute att) {
    this.attributeSet.add(att);
  }

  public void addAttribute(String id, Type type, boolean isCalculable, PeriodCategory category) {
    this.attributeSet.add(new Attribute(id, type, isCalculable, category));
  }

  public void setAttributeSet(Set<Attribute> attributeSet) {
    this.attributeSet = attributeSet;
  }

  public Set<Attribute> getAttributeSet() {
    return attributeSet;
  }

  public Set<Attribute> getNoKeyAttributeSet() {
    Set<Attribute> noKeyAttributeSet = new LinkedHashSet<>(attributeSet);
    noKeyAttributeSet.removeAll(this.keyAttributeSet);
    return noKeyAttributeSet;
  }

  public void addKeyAttribute(String id, Type type, boolean isCalculable,
                              PeriodCategory category) {
    this.keyAttributeSet.add(new Attribute(id, type, isCalculable, category));
  }

  public Set<Attribute> getKeyAttributeSet() {
    return keyAttributeSet;
  }

  // ****
  public void addRepart(Relpart relpart) {
    this.relpartSet.add(relpart);
  }

  public void addRelpart(PeriodCategory category) {
    Relpart relpart = new Relpart(this, category);
    this.relpartSet.add(relpart);
  }

  public void addRelpart(PeriodCategory category, Attribute attId) {
    Relpart relpart = new Relpart(this, category, attId);
    this.relpartSet.add(relpart);
  }

  public Set<Relpart> getRelpartSet() {
    return this.relpartSet;
  }

  public Set<Relpart> getKeyGrouping() {
    return this.relpartSet.stream().filter(p -> p.isKeyRelpart()).collect(Collectors.toSet());
  }

  public Set<Relpart> getNoKeyGrouping() {
    return this.relpartSet.stream().filter(p -> !p.isKeyRelpart()).collect(Collectors.toSet());
  }

  public Set<Relpart> getAttributeGrouping(Attribute att) {
    return this.relpartSet.stream()
        .filter(p -> !p.isKeyRelpart() && p.getAttribute().equals(att))
        .collect(Collectors.toSet());
  }

  // ****
  public String getId() {
    return id;
  }

  public RelationCategory getCategory() {
    return category;
  }

  // **************************************************************************
  // hashcode/equals/clone/toString
  //
  // ATTENTION 2020-02-24 CK : La catégorie et l'ensemble de relpart ne fait partie des fonctions
  // de hash ni de equals pour éviter la circularité avec Relpart.
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((attributeSet == null) ? 0 : attributeSet.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((keyAttributeSet == null) ? 0 : keyAttributeSet.hashCode());
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
    Relvar other = (Relvar) obj;
    if (attributeSet == null) {
      if (other.attributeSet != null)
        return false;
    } else if (!attributeSet.equals(other.attributeSet))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (keyAttributeSet == null) {
      if (other.keyAttributeSet != null)
        return false;
    } else if (!keyAttributeSet.equals(other.keyAttributeSet))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Relvar [id=" + this.id + ", category=" + this.category + ", attributeSet="
        + this.attributeSet + ", keyAttributeSet=" + this.keyAttributeSet + "]";
  }
}
