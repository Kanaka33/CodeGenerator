package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class ConfigReference {

    String type;
    String refObjectName;
    List<Attribute> attributeList;

    public ConfigReference(String type, String refObjectName) {
        this.type = type;
        this.refObjectName = refObjectName;
    }

    public ConfigReference(String type, String refObjectName, List<Attribute> attributeList) {
        this.type = type;
        this.refObjectName = refObjectName;
        this.attributeList = attributeList;
    }

    @XmlAttribute(name="TYPE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute(name="REFOBJECTNAME")
    public String getRefObjectName() {
        return refObjectName;
    }

    public void setRefObjectName(String refObjectName) {
        this.refObjectName = refObjectName;
    }

    @XmlElement(name = "ATTRIBUTE",type=Attribute.class)
    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }
}
