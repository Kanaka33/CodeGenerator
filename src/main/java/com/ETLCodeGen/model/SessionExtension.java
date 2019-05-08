package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"dSQInstName", "dSQInstType", "name", "sInstanceName", "subtype", "transformationType", "type", "connectionReferenceList", "attributeList"})
public class SessionExtension {

    String name;
    String type;
    String subtype;
    String sInstanceName;
    String transformationType;
    String dSQInstName;
    String dSQInstType;

    List<ConnectionReference> connectionReferenceList;
    List<Attribute> attributeList;

    public SessionExtension(){

    }

   /* public SessionExtension(String name, String transformationType, String sInstanceName, String type, String subtype, String dSQInstType, String dSQInstName) {
        this.name = name;
        this.transformationType = transformationType;
        this.sInstanceName = sInstanceName;
        this.type = type;
        this.subtype = subtype;
        this.dSQInstType = dSQInstType;
        this.dSQInstName = dSQInstName;
    }*/


    @XmlAttribute(name = "NAME",required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "TYPE",required = true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute(name = "SUBTYPE",required = true)
    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    @XmlAttribute(name = "SINSTANCENAME",required = true)
    public String getsInstanceName() {
        return sInstanceName;
    }

    public void setsInstanceName(String sInstanceName) {
        this.sInstanceName = sInstanceName;
    }

    @XmlAttribute(name = "TRANSFORMATIONTYPE",required = true)
    public String getTransformationType() {
        return transformationType;
    }

    public void setTransformationType(String transformationType) {
        this.transformationType = transformationType;
    }

    @XmlAttribute(name = "DSQINSTTYPE")
    public String getdSQInstType() {
        return dSQInstType;
    }

    public void setdSQInstType(String dSQInstType) {
        this.dSQInstType = dSQInstType;
    }

    @XmlAttribute(name = "DSQINSTNAME")
    public String getdSQInstName() {
        return dSQInstName;
    }

    public void setdSQInstName(String dSQInstName) {
        this.dSQInstName = dSQInstName;
    }

    @XmlElement(name = "CONNECTIONREFERENCE", type = ConnectionReference.class)
    public List<ConnectionReference> getConnectionReferenceList() {
        return connectionReferenceList;
    }

    public void setConnectionReferenceList(List<ConnectionReference> connectionReferenceList) {
        this.connectionReferenceList = connectionReferenceList;
    }

    @XmlElement(name = "ATTRIBUTE", type = Attribute.class)
    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public void addConnectionReference(ConnectionReference connectionReference) {
        if (this.connectionReferenceList == null) {
            this.connectionReferenceList = new ArrayList<ConnectionReference>();
        }
        this.connectionReferenceList.add(connectionReference);
    }

    public void addAttribute(Attribute attribute) {
        if (this.attributeList == null) {
            this.attributeList = new ArrayList<Attribute>();
        }
        this.attributeList.add(attribute);
    }
}
