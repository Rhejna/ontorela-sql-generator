package ca.griis.gen.model;

import java.util.LinkedHashSet;
import java.util.Set;

public interface PeriodCategory {
  // **************************************************************************
  // Attributs spécifiques
  //
  String prefixe = "@";

  // **************************************************************************
  // Opérations publiques
  //
  default String getIdentifier() {
    //return prefixe + this.toString();
    return this.getAlias();
  }

  Set<Attribute> getPostgreSQLAttribute();

  String getAlias();

  // ************************************************************
  // Enumérateur spécifique pour Valid time
  //
  enum ValidTimeCategory implements PeriodCategory {
    // **********************************************************
    // Valeurs
    //
    Vbe, Vbx, Vxe, Vxx;

    // **********************************************************
    // Opérations publiques
    //
    public Set<Attribute> getPostgreSQLAttribute() {
      Set<Attribute> attDef = new LinkedHashSet<>();
      switch (this) {
        case Vxe:
          attDef.add(
              new Attribute(this.getIdentifier(), this.getPostgreSQLType(), true, Vxe));
          break;
        case Vbx:
          attDef.add(
              new Attribute(this.getIdentifier(), this.getPostgreSQLType(), true, Vbx));
          break;
        case Vbe:
          attDef.add(
              new Attribute(this.getIdentifier(), this.getPostgreSQLType(), false, Vbe));
        case Vxx:
          attDef.add(
              new Attribute(this.getIdentifier(), this.getPostgreSQLType(), true, Vxx));
          break;
      }
      return attDef;
    }

    public DefinedType getPostgreSQLType() {
      DefinedType dType = null;
      switch (this) {
        case Vxe:
          dType = new DefinedType("VXE_POINT",
              new BuiltInType(DefinedType.getTemporalPointTypeId(), false));
          break;
        case Vbx:
          dType = new DefinedType("VBX_POINT",
              new BuiltInType(DefinedType.getTemporalPointTypeId(), false));
          break;
        case Vbe:
          dType = new DefinedType("VBE_INTERVAL",
              new BuiltInType(DefinedType.getTemporalPeriodTypeId(), false));
          break;
        case Vxx:
          dType = new DefinedType("VXX_INTERVAL",
              new BuiltInType(DefinedType.getTemporalPeriodTypeId(), false));
          break;
      }
      return dType;
    }

    public String getPostgreSQLTypeId() {
      String dType = null;
      switch (this) {
        case Vxe:
          dType = "VXE_POINT";
          break;
        case Vbx:
          dType = "VBX_POINT";
          break;
        case Vbe:
          dType = "VBE_INTERVAL";
          break;
        case Vxx:
          dType = "VXX_INTERVAL";
          break;
      }
      return dType;
    }

    public String getAlias() {
      String alias = null;
      switch (this) {
        case Vxe:
          alias = "until";
          break;
        case Vbx:
          alias = "since";
          break;
        case Vbe:
          alias = "during";
          break;
        case Vxx:
          alias = "unknown";
          break;
      }
      return alias;
    }
  }


  // ************************************************************
  // Enumérateur spécifique pour Transaction time
  //
  public enum TransactionTimeCategory implements PeriodCategory {
    // **********************************************************
    // Valeurs
    //
    Tbe, Tbx;

    public Set<Attribute> getPostgreSQLAttribute() {
      Set<Attribute> attDef = new LinkedHashSet<>();
      switch (this) {
        case Tbe:
          attDef.add(
              new Attribute(this.getIdentifier(), this.getPostgreSQLType(), false, Tbe));
          break;
        case Tbx:
          attDef.add(
              new Attribute(this.getIdentifier(), this.getPostgreSQLType(), true, Tbx));
          break;
      }
      return attDef;
    }

    public DefinedType getPostgreSQLType() {
      DefinedType dType = null;
      switch (this) {
        case Tbe:
          dType = new DefinedType("TBE_INTERVAL", DefinedType.getTemporalPeriodType());
          break;
        case Tbx:
          dType = new DefinedType("TBX_POINT", DefinedType.getTemporalPointType());
          break;
      }
      return dType;
    }

    @Override
    public String getAlias() {
      String alias = null;
      switch (this) {
        case Tbe:
          alias = "during";
          break;
        case Tbx:
          alias = "since";
          break;
      }
      return alias;
    }
  }


  // ************************************************************
  // Enumérateur spécifiques pour Bitemporalité Transaction et Valid time
  //
  public enum BitemporalVTCategory implements PeriodCategory {
    // **********************************************************
    // Valeurs
    //
    VbeTbx, VbeTbe, VbxTbx, VbxTbe, VxeTbx, VxeTbe, VxxTbx, VxxTbe;

    public Set<Attribute> getPostgreSQLAttribute() {
      Set<Attribute> attDef = new LinkedHashSet<>();
      switch (this) {
        case VbeTbe:
          attDef.add(new Attribute(ValidTimeCategory.Vbe.getIdentifier(),
              ValidTimeCategory.Vbe.getPostgreSQLType(), false, ValidTimeCategory.Vbe));
          attDef.add(new Attribute(TransactionTimeCategory.Tbe.getIdentifier(),
              TransactionTimeCategory.Tbe.getPostgreSQLType(), false,
              TransactionTimeCategory.Tbe));
          break;
        case VbeTbx:
          attDef.add(new Attribute(ValidTimeCategory.Vbe.getIdentifier(),
              ValidTimeCategory.Vbe.getPostgreSQLType(), false, ValidTimeCategory.Vbe));
          attDef.add(new Attribute(TransactionTimeCategory.Tbx.getIdentifier(),
              TransactionTimeCategory.Tbx.getPostgreSQLType(), true,
              TransactionTimeCategory.Tbx));
          break;
        case VbxTbe:
          attDef.add(new Attribute(ValidTimeCategory.Vbx.getIdentifier(),
              ValidTimeCategory.Vbx.getPostgreSQLType(), true, ValidTimeCategory.Vbx));
          attDef.add(new Attribute(TransactionTimeCategory.Tbe.getIdentifier(),
              TransactionTimeCategory.Tbe.getPostgreSQLType(), false,
              TransactionTimeCategory.Tbe));
          break;
        case VbxTbx:
          attDef.add(new Attribute(ValidTimeCategory.Vbx.getIdentifier(),
              ValidTimeCategory.Vbx.getPostgreSQLType(), true, ValidTimeCategory.Vbx));
          attDef.add(new Attribute(TransactionTimeCategory.Tbx.getIdentifier(),
              TransactionTimeCategory.Tbx.getPostgreSQLType(), true,
              TransactionTimeCategory.Tbx));
          break;
        case VxeTbe:
          attDef.add(new Attribute(ValidTimeCategory.Vxe.getIdentifier(),
              ValidTimeCategory.Vxe.getPostgreSQLType(), true, ValidTimeCategory.Vxe));
          attDef.add(new Attribute(TransactionTimeCategory.Tbe.getIdentifier(),
              TransactionTimeCategory.Tbe.getPostgreSQLType(), false,
              TransactionTimeCategory.Tbe));
          break;
        case VxeTbx:
          attDef.add(new Attribute(ValidTimeCategory.Vxe.getIdentifier(),
              ValidTimeCategory.Vxe.getPostgreSQLType(), true, ValidTimeCategory.Vxe));
          attDef.add(new Attribute(TransactionTimeCategory.Tbx.getIdentifier(),
              TransactionTimeCategory.Tbx.getPostgreSQLType(), true,
              TransactionTimeCategory.Tbx));
          break;
        case VxxTbe:
          attDef.add(new Attribute(ValidTimeCategory.Vxx.getIdentifier(),
              ValidTimeCategory.Vxx.getPostgreSQLType(), true, ValidTimeCategory.Vxx));
          attDef.add(new Attribute(TransactionTimeCategory.Tbe.getIdentifier(),
              TransactionTimeCategory.Tbe.getPostgreSQLType(), false,
              TransactionTimeCategory.Tbe));
          break;
        case VxxTbx:
          attDef.add(new Attribute(ValidTimeCategory.Vxx.getIdentifier(),
              ValidTimeCategory.Vxx.getPostgreSQLType(), true, ValidTimeCategory.Vxx));
          attDef.add(new Attribute(TransactionTimeCategory.Tbx.getIdentifier(),
              TransactionTimeCategory.Tbx.getPostgreSQLType(), true,
              TransactionTimeCategory.Tbx));
          break;
        default:
          break;
      }
      return attDef;
    }

    @Override
    public String getAlias() {
      return this.toString();
    }
  }

}


