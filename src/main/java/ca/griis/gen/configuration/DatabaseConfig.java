package ca.griis.gen.configuration;



import ca.griis.gen.model.RelationCategory;
import java.util.List;

public class DatabaseConfig {
  // **************************************************************************
  // Attributs spécifiques
  //
  private List<ServerProperties> srcServer;
  private String schemaTemporalCategory;
  private String outDirPath;
  private String author;
  private String version;
  private String temporalPointType;
  private String temporalIntervalType;
  private Boolean includeVXX;
  private String granularity;
  private Boolean executeScript;
  private Boolean emiraScripts;
  // **************************************************************************
  // Constructeurs
  //
  // **************************************************************************
  // Opérations propres
  //

  // **************************************************************************
  // Opérations publiques
  //
  // **************** Database connection parameters **************
  public List<ServerProperties> getSrcServer() {
    return srcServer;
  }

  public void setSrcServer(List<ServerProperties> srcServer) {
    this.srcServer = srcServer;
  }

  // **************** Historicization parameters *****************


  public RelationCategory getRelationCategory() {
    switch (this.schemaTemporalCategory) {
      case "V":
        return RelationCategory.V;
      case "T":
        return RelationCategory.T;
      case "VT":
        return RelationCategory.VT;
      default:
        break;
    }
    return null;
  }

  // TODO 2022-08-25 CK : créer un enum neutre TemporalCategory à être utiliser pour deux.
  public void setSchemaTemporalCategory(String schemaTemporalCategory) {
    this.schemaTemporalCategory = schemaTemporalCategory;
  }

  public String getOutDirPath() {
    return this.outDirPath;
  }

  public void setOutDirPath(String outDirPath) {
    this.outDirPath = outDirPath;
  }

  public String getAuthor() {
    return this.author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getTemporalPointType() {
    return temporalPointType;
  }

  public void setTemporalPointType(String temporalPointType) {
    this.temporalPointType = temporalPointType;
  }

  public String getTemporalIntervalType() {
    return temporalIntervalType;
  }

  public void setTemporalIntervalType(String temporalIntervalType) {
    this.temporalIntervalType = temporalIntervalType;
  }

  public Boolean getIncludeVXX() {
    return includeVXX;
  }

  public void setIncludeVXX(Boolean includeVXX) {
    this.includeVXX = includeVXX;
  }

  public String getGranularity() {
    return granularity;
  }

  public void setGranularity(String granularity) {
    this.granularity = granularity;
  }

  public Boolean getExecuteScript() {
    return executeScript;
  }

  public void setExecuteScript(Boolean executeScript) {
    this.executeScript = executeScript;
  }

  public Boolean getEmiraScripts() {
    return emiraScripts;
  }

  public void setEmiraScripts(Boolean emiraScripts) {
    this.emiraScripts = emiraScripts;
  }

  // **************** toString *****************
  @Override
  public String toString() {
    return "DatabaseConfig ["
        + "srcServer=" + srcServer
        + ", schemaTemporalCategory=" + schemaTemporalCategory
        + ", outDirPath=" + outDirPath
        + ", author=" + author
        + ", version=" + version
        + ", temporalPointType=" + temporalPointType
        + ", temporalIntervalType=" + temporalIntervalType
        + ", includeVXX=" + includeVXX + "]";
  }

}
