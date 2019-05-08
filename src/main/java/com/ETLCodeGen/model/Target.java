package com.ETLCodeGen.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Target {
    String businessName;
    String constraint;
    String dataBaseType;
    int objectVersion;
    String tableOptions;
    String name;
    int versionNumber;
    String description;
    List<TargetField> targetFieldList;

    public Target(String businessName, String constraint, String dataBaseType, String description, String name, int objectVersion,
                  String tableOptions, int versionNumber) {
        this.businessName = businessName;
        this.constraint = constraint;
        this.dataBaseType = dataBaseType;
        this.description = description;
        this.name = name;
        this.objectVersion = objectVersion;
        this.tableOptions = tableOptions;
        this.versionNumber = versionNumber;

    }

    @XmlAttribute(name = "BUSINESSNAME")
    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    @XmlAttribute(name = "CONSTRAINT")
    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    @XmlAttribute(name = "DATABASETYPE")
    public String getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(String dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    @XmlAttribute(name = "OBJECTVERSION")
    public int getObjectVersion() {
        return objectVersion;
    }

    public void setObjectVersion(int objectVersion) {
        this.objectVersion = objectVersion;
    }

    @XmlAttribute(name = "TABLEOPTIONS")
    public String getTableOptions() {
        return tableOptions;
    }

    public void setTableOptions(String tableOptions) {
        this.tableOptions = tableOptions;
    }

    @XmlAttribute(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "VERSIONNUMBER")
    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @XmlAttribute(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "TARGETFIELD", type = TargetField.class)
    public List<TargetField> getTargetFieldList() {
        return targetFieldList;
    }

    public void setTargetFieldList(List<TargetField> targetFieldList) {
        this.targetFieldList = targetFieldList;
    }

    public void addTargetField(TargetField targetField) {
        if (this.targetFieldList == null) {
            this.targetFieldList = new ArrayList<TargetField>();
        }
        this.targetFieldList.add(targetField);
    }
}
