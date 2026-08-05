package ca.griis.gen.configuration;

public class ServerProperties {
  // **************************************************************************
  // Attributs spécifiques
  //
  private String rdbms;
  private String host;
  private String port;
  private String databaseId;
  private String schemaId;
  private String user;
  private String pass;
  // **************************************************************************
  // Constructeurs
  //

  // **************************************************************************
  // Opérations propres
  //
  // **************************************************************************
  // Opérations publiques
  //
  public String getRdbms() {
    return this.rdbms;
  }

  public void setRdbms(String rdbms) {
    this.rdbms = rdbms;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getDatabaseId() {
    return databaseId;
  }

  public void setDatabaseId(String databaseId) {
    this.databaseId = databaseId;
  }

  public String getSchemaId() {
    return schemaId;
  }

  public void setSchemaId(String schemaId) {
    this.schemaId = schemaId;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPass() {
    return pass;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }

  public String getHostUrl() {
    String url = null;
    if (this.rdbms.equals("postgresql")) {
      // jdbc:postgresql://host:port/
      url = "jdbc:postgresql://" + this.getHost() + ":" + this.getPort() + "/"+ this.getDatabaseId();
    }else if(this.rdbms.equals("mssql")){
      //config.getSrcServer().get(0).getHostUrl()+
      url = "jdbc:sqlserver://" + this.getHost() + ":" + this.getPort()+";encrypt=false;";
    }
    return url;
  }

  @Override
  public String toString() {
    return "ServerProperties [host=" + this.host +
        ", port=" + this.port +
        ", databaseId=" + this.databaseId +
        ", schemaId=" + this.schemaId +
        ", user=" + this.user +
        ", pass=" + this.pass + "]";
  }
}
