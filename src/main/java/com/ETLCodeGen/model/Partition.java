package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Partition {
    String name;
    String description;

    List<KeyRange> keyRangeList;

    public Partition(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @XmlAttribute(name="NAME",required = true)
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

    @XmlElement(name = "KEYRANGE",type=KeyRange.class)
    public List<KeyRange> getKeyRangeList() {
        return keyRangeList;
    }

    public void setKeyRangeList(List<KeyRange> keyRangeList) {
        this.keyRangeList = keyRangeList;
    }

    public void addKeyRange(KeyRange keyRange){
        if(this.keyRangeList == null){
            this.keyRangeList = new ArrayList<KeyRange>();
        }
        this.keyRangeList.add(keyRange);
    }
}
