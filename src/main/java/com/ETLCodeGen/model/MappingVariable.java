package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class MappingVariable {
	String dataType;
	String defaultValue;
	String description;
	String isExpressionVariable;
	String isParam;
	String name;
	String precision;
	String scale;
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

	@XmlAttribute(name = "ISEXPRESSIONVARIABLE")
	public String getIsExpressionVariable() {
		return isExpressionVariable;
	}

	public void setIsExpressionVariable(String isExpressionVariable) {
		this.isExpressionVariable = isExpressionVariable;
	}

	@XmlAttribute(name = "ISPARAM")
	public String getIsParam() {
		return isParam;
	}

	public void setIsParam(String isParam) {
		this.isParam = isParam;
	}

	@XmlAttribute(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "PRECISION")
	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	@XmlAttribute(name = "SCALE")
	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	@XmlAttribute(name = "USERDEFINED")
	public String getUserDefined() {
		return userDefined;
	}

	public void setUserDefined(String userDefined) {
		this.userDefined = userDefined;
	}

}
