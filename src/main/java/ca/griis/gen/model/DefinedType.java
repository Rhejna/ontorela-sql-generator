package ca.griis.gen.model;

public class DefinedType extends Type {
  // **************************************************************************
  // Attributs spécifiques
  //
  private final Type baseType;
  // **************************************************************************
  // Constructeurs
  //

  public DefinedType(String typeId, Type baseType) {
    super(typeId, false);
    this.baseType = baseType;
  }

  // **************************************************************************
  // Opérations propres
  //

  // **************************************************************************
  // Opérations publiques
  //
  public Type getBaseType() {
    return this.baseType;
  }

  public static DefinedType getTemporalPeriodType() {
    return new DefinedType("UHF_INTERVAL", new BuiltInType("", false));
  }

  public static DefinedType getTemporalPeriodType(String PeriodType) {
    return new DefinedType("UHF_INTERVAL", new BuiltInType(PeriodType, true));
  }

  public static DefinedType getTemporalPointType() {
    return new DefinedType("UHF_POINT", new BuiltInType("", false));
  }

  public static DefinedType getTemporalPointType(String PointType) {
    return new DefinedType("UHF_POINT", new BuiltInType(PointType, true));
  }

  public static String getTemporalPeriodTypeId() {
    return "UHF_INTERVAL";
  }

  public static String getTemporalPointTypeId() {
    return "UHF_POINT";
  }

  // **************************************************************************
  // hashcode/equals/clone/toString
  //
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((baseType == null) ? 0 : baseType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    DefinedType other = (DefinedType) obj;
    if (baseType == null) {
      if (other.baseType != null)
        return false;
    } else if (!baseType.equals(other.baseType))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DefinedType [id=" + this.getId() + ", baseType=" + baseType + ", isBuiltIn()=" + isBuiltIn() + "]";
  }

}
