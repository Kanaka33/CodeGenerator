package com.ETLCodeGen.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kanakadurga.P
 *
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "groupList", "transformFieldList", "tableAttributeList" })
public class Transformation {
	String name;
	String description;
	String versionNumber;
	String reUsable;
	String objectVersion;
	String type;
	String refdbdName;
	String refSourceName;
	List<TransformField> transformFieldList;
	List<Group> groupList;
	List<TableAttribute> tableAttributeList;

	public Transformation() {

	}

	public Transformation(String description, String name, String versionNumber, String reUsable, String type,
			String objectVersion) {
		this.description = description;
		this.name = name;
		this.versionNumber = versionNumber;
		this.reUsable = reUsable;
		this.type = type;
		this.objectVersion = objectVersion;
	}
	
	@XmlAttribute(name = "REF_DBD_NAME")
	public String getRefdbdName() {
		return refdbdName;
	}

	public void setRefdbdName(String refdbdName) {
		this.refdbdName = refdbdName;
	}
	
	@XmlAttribute(name = "REF_SOURCE_NAME")
	public String getRefSourceName() {
		return refSourceName;
	}

	public void setRefSourceName(String refSourceName) {
		this.refSourceName = refSourceName;
	}

	@XmlAttribute(name = "NAME")
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

	@XmlAttribute(name = "VERSIONNUMBER")
	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	@XmlAttribute(name = "REUSABLE")
	public String getReUsable() {
		return reUsable;
	}

	public void setReUsable(String reUsable) {
		this.reUsable = reUsable;
	}

	@XmlAttribute(name = "OBJECTVERSION")
	public String getObjectVersion() {
		return objectVersion;
	}

	public void setObjectVersion(String objectVersion) {
		this.objectVersion = objectVersion;
	}

	@XmlAttribute(name = "TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name = "TRANSFORMFIELD", type = TransformField.class)
	public List<TransformField> getTransformFieldList() {
		return transformFieldList;
	}

	public void setTransformFieldList(List<TransformField> transformFieldList) {
		this.transformFieldList = transformFieldList;
	}

	@XmlElement(name = "GROUP", type = Group.class)
	public List<Group> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<Group> groupList) {
		this.groupList = groupList;
	}

	@XmlElement(name = "TABLEATTRIBUTE", type = TableAttribute.class)
	public List<TableAttribute> getTableAttributeList() {
		return tableAttributeList;
	}

	public void setTableAttributeList(List<TableAttribute> tableAttributeList) {
		this.tableAttributeList = tableAttributeList;
	}

	public void addTableAttribute(TableAttribute targetField) {
		if (this.tableAttributeList == null) {
			this.tableAttributeList = new ArrayList<TableAttribute>();
		}
		this.tableAttributeList.add(targetField);
	}

	public void addGroup(Group group) {
		if (this.groupList == null) {
			this.groupList = new ArrayList<Group>();
		}
		this.groupList.add(group);
	}

	public void addTransformField(TransformField transformField) {
		if (this.transformFieldList == null) {
			this.transformFieldList = new ArrayList<TransformField>();
		}
		this.transformFieldList.add(transformField);
	}
}
