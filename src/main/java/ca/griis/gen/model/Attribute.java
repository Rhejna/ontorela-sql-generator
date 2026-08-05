package ca.griis.gen.model;

/**
 * La représentation d'un attribut de sql temporalisé en Java
 * avec un id, type, une variable indiquant s'il est calculable et
 * la catégorie temporelle de l'attribut
 */
public class Attribute {
  // **************************************************************************
  // Attributs spécifiques
  //
  private final String id;
  private final Type type;
  private final boolean isCalculable;
  private final PeriodCategory category;
  // **************************************************************************
  // Constructeurs
  //

  /**
   * @param id           le nom unique
   * @param type         son type (int, CHAR, DATE)
   * @param isCalculable Est-t-il calculable (true, false)
   * @param category     sa catégorie temporelle (since, until, during, etc)
   */
  public Attribute(String id, Type type, boolean isCalculable, PeriodCategory category) {
    super();
    this.id = id;
    this.type = type;
    this.isCalculable = isCalculable;
    this.category = category;
  }
  // **************************************************************************
  // Opérations propres
  //
  // **************************************************************************
  // Opérations publiques
  //

  /**
   * @return le nom unique
   */
  public String getId() {
    return this.id;
  }

  /**
   * @return le type sql
   */
  public Type getType() {
    return this.type;
  }

  /**
   * @return s'il est calculable
   */
  public boolean isCalculable() {
    return this.isCalculable;
  }

  /**
   * @return une catégorie temporelle (since, until, during, etc)
   */
  public PeriodCategory getTimelineCategory() {
    return this.category;
  }

  // **************************************************************************
  // hashcode/equals/clone/toString
  //
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    Attribute other = (Attribute) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return id + " : " + type;
  }

}
