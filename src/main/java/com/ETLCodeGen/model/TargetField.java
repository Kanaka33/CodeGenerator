package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class TargetField {
	String businessName;
	String dataType;
	String description;
	int FieldNumber;
	String keyType;
	String name;
	String nullable;
	String pictureText;
	int precision;
	int scale;
	@XmlAttribute(name="BUSINESSNAME")
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	@XmlAttribute(name="DATATYPE")
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	@XmlAttribute(name="DESCRIPTION")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlAttribute(name="FIELDNUMBER")
	public int getFieldNumber() {
		return FieldNumber;
	}
	public void setFieldNumber(int count1) {
		FieldNumber = count1;
	}
	@XmlAttribute(name="KEYTYPE")
	public String getKeyType() {
		return keyType;
	}
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}
	@XmlAttribute(name="NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute(name="NULLABLE")
	public String getNullable() {
		return nullable;
	}
	public void setNullable(String nullable) {
		this.nullable = nullable;
	}
	@XmlAttribute(name="PICTURETEXT")
	public String getPictureText() {
		return pictureText;
	}
	public void setPictureText(String pictureText) {
		this.pictureText = pictureText;
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
	
}
