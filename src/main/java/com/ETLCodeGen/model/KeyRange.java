package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
@XmlAccessorType(XmlAccessType.PROPERTY)
public class KeyRange {

    String name;
    String startRange;
    String endRange;
    String srcInstanceName;
    String group;

    public KeyRange(String name, String startRange, String endRange, String srcInstanceName, String group) {
        this.name = name;
        this.startRange = startRange;
        this.endRange = endRange;
        this.srcInstanceName = srcInstanceName;
        this.group = group;
    }

    @XmlAttribute(name="NAME",required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name="STARTRANGE")
    public String getStartRange() {
        return startRange;
    }

    public void setStartRange(String startRange) {
        this.startRange = startRange;
    }

    @XmlAttribute(name="ENDRANGE")
    public String getEndRange() {
        return endRange;
    }

    public void setEndRange(String endRange) {
        this.endRange = endRange;
    }

    @XmlAttribute(name="SRCINSTANCENAME")
    public String getSrcInstanceName() {
        return srcInstanceName;
    }

    public void setSrcInstanceName(String srcInstanceName) {
        this.srcInstanceName = srcInstanceName;
    }

    @XmlAttribute(name="GROUP")
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
