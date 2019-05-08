package com.ETLCodeGen.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Task {
	public Task(String description, String name, String reusable, String type, String versionNumber) {
		super();
		this.description = description;
		this.name = name;
		this.reusable = reusable;
		this.type = type;
		this.versionNumber = versionNumber;
	}

	String description;
	String type;
	String name;
	String reusable;
	String versionNumber;
	List<Attribute> attributeList;

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

	@XmlAttribute(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlAttribute(name = "TYPE")
	public String getType() {
		return type;
	}

	public void setIsValid(String type) {
		this.type = type;
	}

	@XmlAttribute(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "REUSABLE")
	public String getReusable() {
		return reusable;
	}

	public void setReusable(String reusable) {
		this.reusable = reusable;
	}

	@XmlAttribute(name = "VERSIONNUMBER")
	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

}
