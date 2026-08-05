package ca.griis.gen.process;

import ca.griis.gen.configuration.DatabaseConfig;
import ca.griis.gen.configuration.DbUtility;
import ca.griis.gen.model.*;
import schemacrawler.schema.*;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BuildSchema {
  // **************************************************************************
  // Attributs spécifiques
  //
  private final DatabaseConfig config;
  private SchemaModel schemaModel;

  // **************************************************************************
  // Constructeurs
  //
  public BuildSchema(DatabaseConfig config) {
    this.config = config;
    build();
  }

  // **************************************************************************
  // Opérations propres
  //
  private Relvar buildRelvar(Table table) {
    Relvar r = new Relvar(table.getName(), RelationCategory.N);
    // Récupérer les attributs
    for (Column column : table.getColumns()) {
      String typeId = column.getColumnDataType().getName() + '(' + column.getSize() + ')';
      if(column.getSize() == Integer.MAX_VALUE){
        typeId = column.getColumnDataType().getName();
      }
      r.addAttribute(column.getName(), new BuiltInType(typeId), false, null);
    }
    // Récupérer les attributs clés
    for (Column column : table.getPrimaryKey().getConstrainedColumns()) {
      String typeId = column.getColumnDataType().getName() + '(' + column.getSize() + ')';
      r.addKeyAttribute(column.getName(), new BuiltInType(typeId), false, null);
    }
    return r;
  }

  /**
   * Construire les contraintes référentielles d'une table à partir des informations récupérer de
   * SchemaCrawler. <br>
   * NOTE : Attention SchemaCrawler retourner les FK d'une table dans les deux directions.
   *
   * @param table : une table de SchemaCrawler.
   */
  private Set<ReferentialKey> buildReferentialKeySet(Table table) {
    Set<ReferentialKey> rkSet = new LinkedHashSet<>();
    //
    int i = 0;
    for (ForeignKey fk : table.getForeignKeys()) {
      String targetRelvarId =
          fk.getColumnReferences().get(0).getPrimaryKeyColumn().getParent().getName();
      if (!table.getName().equals(targetRelvarId)) {
        Map<String, String> attributMap = new LinkedHashMap<>();
        for (ColumnReference def : fk.getColumnReferences()) {
          attributMap.put(def.getForeignKeyColumn().getName(),
              def.getPrimaryKeyColumn().getName());
        }
        String sourceRelvarId = table.getName();
        Relvar sourceRelvar = this.schemaModel.getRelvar(sourceRelvarId);
        String fkId = sourceRelvarId + "_fk" + i++;
        ReferentialKey rk = new ReferentialKey(fkId, sourceRelvar,
            this.schemaModel.getRelvar(targetRelvarId), attributMap);
        rkSet.add(rk);
      }
    }
    return rkSet;
  }

  // **************************************************************************
  // Opérations publiques
  //
  protected void build() {
    // Si le schéma est déjà construit ne pas reconstruire.
    if (this.schemaModel != null) {
      return;
    }
    //
    this.schemaModel = new SchemaModel(config.getSrcServer().get(0).getSchemaId());
    String schemaId = this.schemaModel.getSchemaId();
    System.out.println(" >> Building schema: " + schemaId);
    //
    Catalog catalog = DbUtility.getCatalog(schemaId, config);
    assert catalog != null : "Enable to build database catalog:";
    // Vérifier l'existence du schéma
    Schema schema =
        catalog.getSchemas().stream().filter(s -> s.getName().equals(schemaId)).findAny()
            .orElse(null);
    if (schema != null) {
      // Récupérer les informations des tables
      for (final Table table : catalog.getTables(schema)) {
        this.schemaModel.addRelvar(buildRelvar(table));
        // Récupérer les informations des contraintes référentielles
        this.schemaModel.addReferentialKey(buildReferentialKeySet(table));
      }
    }
  }

  public SchemaModel getSchema() {
    return this.schemaModel;
  }
}
