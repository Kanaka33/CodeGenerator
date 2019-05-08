package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SessTransformationGroup {

    String group;
    String partitionType;

    public SessTransformationGroup(String group, String partitionType) {
        this.group = group;
        this.partitionType = partitionType;
    }

    @XmlAttribute(name="GROUP",required = true)
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @XmlAttribute(name="PARTITIONTYPE")
    public String getPartitionType() {
        return partitionType;
    }

    public void setPartitionType(String partitionType) {
        this.partitionType = partitionType;
    }
}
