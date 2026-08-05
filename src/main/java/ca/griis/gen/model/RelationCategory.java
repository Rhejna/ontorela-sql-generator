package ca.griis.gen.model;

public enum RelationCategory {
  // **************************************************************************
  // Valeurs
  //
  N,  //Partition non-temporelle
  V,  //partition de validité
  T,  //partition de transaction
  VT; //partition bitemporellle

  // **************************************************************************
  // Attributs spécifiques
  //
  private static String prefixe = "@";

  // **************************************************************************
  // Opérations propres
  //
  // **************************************************************************
  // Opérations publiques
  //
  public String getIdentifier() {
    return prefixe + this.toString();
  }

  public String getFullName() {
    String dType = null;
    switch (this) {
      case N:
        dType = "N";
        break;
      case V:
        dType = "validTime";
        break;
      case T:
        dType = "transactionTime";
        break;
      case VT:
        dType = "bitemporalTime";
        break;
    }
    return dType;
  }
}
