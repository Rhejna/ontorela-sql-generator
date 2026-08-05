package ca.griis.gen.model;

public class AttributeString {
    String id;
    String type; //can be a type or an alias
    boolean isfct;

    /**
     * Construction d'une définition d'attribut.
     *
     * @param id   : l'identifiant de l'attribut
     * @param type : le type de l'attribut
     */
    public AttributeString(String id, String type) {
        super();
        this.id = id;
        this.type = type;
        this.isfct = false;
    }

    /**
     * Construction d'une définition d'attribut de type fonction ou non.
     *
     * @param id    : le nom de l'attribut.
     * @param type  : le type de l'attribut.
     * @param isfct : si l'attribut est une fonction.
     */
    public AttributeString(String id, String type, boolean isfct) {
        super();
        this.id = id;
        this.type = type;
        this.isfct = isfct;
    }

    /**
     * Construction d'une définition d'attribut sans type !!
     *
     * @param id    : le nom de l'attribut.
     * @param isfct : si l'attribut est une fonction.
     */
    public AttributeString(String id, boolean isfct) {
        super();
        this.id = id;
        this.type = null;
        this.isfct = isfct;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isFct() {
        return this.isfct;
    }
}