package ca.griis.gen.model;

public abstract class Type {
  // **************************************************************************
  // Attributs spécifiques
  //
  private final String id;
  private final boolean isBuiltIn;
  // **************************************************************************
  // Constructeurs
  //

  public Type(String id, boolean isBuiltIn) {
    super();
    this.id = id;
    this.isBuiltIn = isBuiltIn;
  }

  // **************************************************************************
  // Opérations propres
  //
  // **************************************************************************
  // Opérations publiques
  //
  public String getId() {
    return this.id;
  }

  public boolean isBuiltIn() {
    return this.isBuiltIn;
  }

  // **************************************************************************
  // hashcode/equals/clone/toString
  //
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + (isBuiltIn ? 1231 : 1237);
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
    Type other = (Type) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (isBuiltIn != other.isBuiltIn)
      return false;
    return true;
  }

}
