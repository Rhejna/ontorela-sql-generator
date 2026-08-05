package ca.griis.gen.model;

public class BuiltInType extends Type {
  // **************************************************************************
  // Attributs spécifiques
  //

  // **************************************************************************
  // Constructeurs
  //
  public BuiltInType(String id) {
    super(getStandardId(id), true);
    // TODO Auto-generated constructor stub
  }

  public BuiltInType(String id, Boolean isBuiltIn) {
    super(id, isBuiltIn);
    // TODO Auto-generated constructor stub
  }
  // **************************************************************************
  // Opérations propres
  //

  /**
   * Obtenir un identifiant de type "Standard" FIX 2020-02-20 CK : problème de schemacrawler, je ne
   * comprends pas les id des types.
   *
   * @param id : Le nom du type qui doit être standard.
   * @return le nom du type selon le standard sql.
   */
  private static String getStandardId(String id) {
    String t = id;
    // PostgreSQL changer bpchar à char
    if (t.startsWith("bpchar")) {
      t = t.replace("bp", "");
    } else if (t.startsWith("date") || t.startsWith("time") || t.startsWith("timestamp") || t.startsWith("bool")) {
      t = t.substring(0, t.indexOf('('));
    } else if (t.startsWith("int")) {
      t = "INT";
    } else if (t.startsWith("text")){
      t = "TEXT";
    }
    // Mettre en majuscule
    return t.toUpperCase();
  }

  // **************************************************************************
  // Opérations publiques
  //
  // **************************************************************************
  // hashcode/equals/clone/toString
  //
  public static BuiltInType getTemporalPeriodType() {
    return new BuiltInType("UHF_INTERVAL");
  }

  @Override
  public String toString() {
    return "BuiltInType [getId()=" + getId() + ", isBuiltIn()=" + isBuiltIn() + "]";
  }

}
