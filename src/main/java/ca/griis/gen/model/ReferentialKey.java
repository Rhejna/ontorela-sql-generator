package ca.griis.gen.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReferentialKey {
  // **************************************************************************
  // Attributs spécifiques
  //
  private final String id;
  private final Relvar sourceRelvar;
  private final Relvar targetRelvar;
  private Map<String, String> attributeSet;

  // **************************************************************************
  // Constructeurs
  //
  public ReferentialKey(String id, Relvar sourceRelvar, Relvar targetRelvar, Map<String, String> attributeSet) {
    super();
    this.id = id;
    this.sourceRelvar = sourceRelvar;
    this.targetRelvar = targetRelvar;
    this.attributeSet = attributeSet;
  }

  public ReferentialKey(String id, Relvar sourceTable, Relvar targetTable, Set<Attribute> sourceAttributes,
                        Set<Attribute> targetAttributes) {
    this(id, sourceTable, targetTable, new LinkedHashMap<>());
    for (int i = 0; i < sourceAttributes.size(); i++) {
      this.attributeSet.put(sourceAttributes.stream().toList().get(i).getId(), targetAttributes.stream().toList().get(i).getId());
    }
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

  public Map<String, String> getAttributeSet() {
    return this.attributeSet;
  }

  public Relvar getSourceRelvar() {
    return this.sourceRelvar;
  }

  public Relvar getTargetRelvar() {
    return this.targetRelvar;
  }

  public String getDefinition() {
    return this.sourceRelvar.getId() + "{"
        + this.attributeSet.keySet().stream().map(String::toString).collect(Collectors.joining(",")) + "} --> "
        + this.targetRelvar.getId() + "{"
        + this.attributeSet.values().stream().map(String::toString).collect(Collectors.joining(",")) + "}";
  }

  // **************************************************************************
  // hashcode/equals/clone/toString
  //
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((attributeSet == null) ? 0 : attributeSet.hashCode());
    result = prime * result + ((sourceRelvar == null) ? 0 : sourceRelvar.hashCode());
    result = prime * result + ((targetRelvar == null) ? 0 : targetRelvar.hashCode());
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
    ReferentialKey other = (ReferentialKey) obj;
    if (attributeSet == null) {
      if (other.attributeSet != null)
        return false;
    } else if (!attributeSet.equals(other.attributeSet))
      return false;
    if (sourceRelvar == null) {
      if (other.sourceRelvar != null)
        return false;
    } else if (!sourceRelvar.equals(other.sourceRelvar))
      return false;
    if (targetRelvar == null) {
      if (other.targetRelvar != null)
        return false;
    } else if (!targetRelvar.equals(other.targetRelvar))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ReferentialKey [source=" + this.sourceRelvar.getId() + ", target=" + this.targetRelvar.getId()
        + ", attributeSet=" + this.attributeSet + "]";
  }

}
