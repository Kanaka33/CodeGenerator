package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class TransformField {
	String name;
	String description;
	String dataType;
    String defaultValue;
    int precision;
    int scale;
    String portType;
    String pictureText;
    String group;
    String expression;
    String expressionType;
    String refField;
    String refSourceField;
    String seqGeneratorVal;
    
    
    @XmlAttribute(name="REF_SOURCE_FIELD")
    public String getRefSourceField() {
		return refSourceField;
	}
	public void setRefSourceField(String refSourceField) {
		this.refSourceField = refSourceField;
	}
	@XmlAttribute(name="SEQUENCE_GENERATOR_VALUE")
	public String getSeqGeneratorVal() {
		return seqGeneratorVal;
	}
	public void setSeqGeneratorVal(String seqGeneratorVal) {
		this.seqGeneratorVal = seqGeneratorVal;
	}
	@XmlAttribute(name="REF_FIELD")
    public String getRefField() {
		return refField;
	}
	public void setRefField(String refField) {
		this.refField = refField;
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
	@XmlAttribute(name="DATATYPE")
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	@XmlAttribute(name="DEFAULTVALUE")
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	@XmlAttribute(name="PRECISION")
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	@XmlAttribute(name="SCALE")
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	@XmlAttribute(name="PORTTYPE")
	public String getPortType() {
		return portType;
	}
	public void setPortType(String portType) {
		this.portType = portType;
	}
	@XmlAttribute(name="PICTURETEXT")
	public String getPictureText() {
		return pictureText;
	}
	public void setPictureText(String pictureText) {
		this.pictureText = pictureText;
	}
	@XmlAttribute(name="GROUP")
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	@XmlAttribute(name="EXPRESSION")
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	@XmlAttribute(name="EXPRESSIONTYPE")
	public String getExpressionType() {
		return expressionType;
	}
	public void setExpressionType(String expressionType) {
		this.expressionType = expressionType;
	}
    
}
