package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class WorkFlowVariable {
	
	public WorkFlowVariable(String dataType, String defaultValue, String description, String isNull,
			String isPersistent, String name, String userDefined) {
		super();
		this.dataType = dataType;
		this.defaultValue = defaultValue;
		this.description = description;
		this.isNull = isNull;
		this.isPersistent = isPersistent;
		this.name = name;
		this.userDefined = userDefined;
	}
	String dataType;
	String defaultValue;
	String description;
	String isNull;
	String isPersistent;
	String name;
	String userDefined;
	
	@XmlAttribute(name = "DATATYPE")
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	@XmlAttribute(name = "DEFAULTVALUE")
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@XmlAttribute(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlAttribute(name = "ISNULL")
	public String getIsNull() {
		return isNull;
	}
	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}
	
	@XmlAttribute(name = "ISPERSISTENT")
	public String getIsPersistent() {
		return isPersistent;
	}
	public void setIsPersistent(String isPersistent) {
		this.isPersistent = isPersistent;
	}
	
	@XmlAttribute(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "USERDEFINED")
	public String getUserDefined() {
		return userDefined;
	}
	public void setUserDefined(String userDefined) {
		this.userDefined = userDefined;
	}
	
}
