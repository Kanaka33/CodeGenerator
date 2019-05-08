package com.ETLCodeGen.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Instance {
	String name;
	String description;
	String transformationType;
	String transformationName;
	String type;
	String reusable;
	String dbName;
	List<TableAttribute> tableAttributeList;
	List<AssociatedSourceInstance> associtesInstanceList;
	@XmlElement(name = "ASSOCIATED_SOURCE_INSTANCE", type = AssociatedSourceInstance.class)
	public List<AssociatedSourceInstance> getAssocitesInstanceList() {
		return associtesInstanceList;
	}
	public void setAssocitesInstanceList(List<AssociatedSourceInstance> associtesInstanceList) {
		this.associtesInstanceList = associtesInstanceList;
	}
	 public void addAssociated(AssociatedSourceInstance associtesInstance) {
	        if (this.associtesInstanceList == null) {
	            this.associtesInstanceList = new ArrayList<AssociatedSourceInstance>();
	        }
	        this.associtesInstanceList.add(associtesInstance);
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
		
	@XmlAttribute(name="DBDNAME")
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
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
	@XmlAttribute(name="TRANSFORMATION_TYPE")
	public String getTransformationType() {
		return transformationType;
	}
	public void setTransformationType(String transformationType) {
		this.transformationType = transformationType;
	}
	@XmlAttribute(name="TRANSFORMATION_NAME")
	public String getTransformationName() {
		return transformationName;
	}
	public void setTransformationName(String transformationName) {
		this.transformationName = transformationName;
	}
	@XmlAttribute(name="TYPE")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@XmlAttribute(name="REUSABLE")
	public String getReusable() {
		return reusable;
	}
	public void setReusable(String reusable) {
		this.reusable = reusable;
	}
	
}
