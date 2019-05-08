package com.ETLCodeGen.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "CONFIG")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Config {

	String name;
	String description;
	String isDefault;
	String versionNumber;
	List<Attribute> attributeList;
	List<IntAttribute> intattributeList;

	public Config() {

	}

	public Config(String name, String description, String isDefault, String versionNumber) {
		this.name = name;
		this.description = description;
		this.isDefault = isDefault;
		this.versionNumber = versionNumber;
	}

	@XmlAttribute(name = "NAME", required = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlAttribute(name = "ISDEFAULT")
	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	@XmlAttribute(name = "VERSIONNUMBER")
	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	@XmlElement(name = "ATTRIBUTE", type = Attribute.class)
	public List<Attribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<Attribute> attributeList) {
		this.attributeList = attributeList;
	}

	public void addAttribute(Attribute attribute) {
		if (this.attributeList == null) {
			this.attributeList = new ArrayList<Attribute>();
		}
		this.attributeList.add(attribute);
	}

	@XmlElement(name = "ATTRIBUTE", type = IntAttribute.class)
	public List<IntAttribute> getIntAttributeList() {
		return intattributeList;
	}

	public void setIntAttributeList(List<IntAttribute> attributeList) {
		this.intattributeList = attributeList;
	}

	public void addIntAttribute(IntAttribute attribute) {
		if (this.intattributeList == null) {
			this.intattributeList = new ArrayList<IntAttribute>();
		}
		this.intattributeList.add(attribute);
	}
}
