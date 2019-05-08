package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder={"sessTransformationInstList", "configReference","sessionExtensionList","attributeList"})
public class Session {

    String name;
    String description;
    String versionNumber;
    String sortOrder;
    String reUsable;
    String mappingName;
    String isValid;

    ConfigReference configReference;
    List<SessTransformationInst> sessTransformationInstList;
    List<SessionExtension> sessionExtensionList;
    List<Attribute> attributeList;
    //List<IntAttribute> intattributeList;

    public Session(String name,String mappingName){
        this.name = name;
        this.mappingName = mappingName;
    }
   public Session(String description, String isValid, String mappingName, String name, String reUsable,  String sortOrder, String versionNumber) {
        
        this.description = description;
        this.isValid = isValid;
        this.mappingName = mappingName;
        this.name = name;
        this.reUsable = reUsable;
        this.sortOrder = sortOrder;
        this.versionNumber = versionNumber;
        
    }

    @XmlAttribute(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name="DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlAttribute(name="VERSIONNUMBER")
    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    @XmlAttribute(name="SORTORDER")
    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    @XmlAttribute(name="REUSABLE")
    public String getReUsable() {
        return reUsable;
    }

    public void setReUsable(String reUsable) {
        this.reUsable = reUsable;
    }

    @XmlAttribute(name="MAPPINGNAME")
    public String getMappingName() {
        return mappingName;
    }

    public void setMappingName(String mappingName) {
        this.mappingName = mappingName;
    }

    @XmlAttribute(name="ISVALID")
    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    @XmlElement(name = "SESSTRANSFORMATIONINST",type=SessTransformationInst.class)
    public List<SessTransformationInst> getSessTransformationInstList() {
        return sessTransformationInstList;
    }

    public void setSessTransformationInstList(List<SessTransformationInst> sessTransformationInstList) {
        this.sessTransformationInstList = sessTransformationInstList;
    }

    @XmlElement(name = "SESSIONEXTENSION",type=SessionExtension.class)
    public List<SessionExtension> getSessionExtensionList() {
        return sessionExtensionList;
    }

    public void setSessionExtensionList(List<SessionExtension> sessionExtensionList) {
        this.sessionExtensionList = sessionExtensionList;
    }

    @XmlElement(name = "CONFIGREFERENCE",type=ConfigReference.class)
    public ConfigReference getConfigReference() {
        return configReference;
    }

    public void setConfigReference(ConfigReference configReference) {
        this.configReference = configReference;
    }

    @XmlElement(name = "ATTRIBUTE",type=Attribute.class)
    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public void addAttribute(Attribute attribute){
        if(this.attributeList == null){
            this.attributeList = new ArrayList<Attribute>();
        }
        this.attributeList.add(attribute);
    }

    public void addSessionExtension(SessionExtension sessionExtension){
        if(this.sessionExtensionList == null){
            this.sessionExtensionList = new ArrayList<SessionExtension>();
        }
        this.sessionExtensionList.add(sessionExtension);
    }

    public void addSessTransformationInst(SessTransformationInst sessTransformationInst){
        if(this.sessTransformationInstList == null){
            this.sessTransformationInstList = new ArrayList<SessTransformationInst>();
        }
        this.sessTransformationInstList.add(sessTransformationInst);
    }
    
}
