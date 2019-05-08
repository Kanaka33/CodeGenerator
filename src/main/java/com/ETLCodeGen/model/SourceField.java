package com.ETLCodeGen.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SourceField {
	String businessName;
	String dataType;
	String description;
	int fieldNumber;
	int fieldProperty;
	String fieldType;
	String hidden;
	String keyType;
	int length;
	int level;
	String name;
	String nullable;
	int occurs;
	int offSet;
	int physicalLength;
	int physicalOffSet;
	String pictureText;
	int precision;
	int scale;
	String usage_Flags;
	String referencedDbd;
	String referencesField;
	String referencedTable;
	String group;
	
	List<FieldAttribute> fieldAtList;
	    
	@XmlAttribute(name="GROUP")
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@XmlAttribute(name="REFERENCEDDBD")
    public String getReferencedDbd() {
		return referencedDbd;
	}

	public void setReferencedDbd(String referencedDbd) {
		this.referencedDbd = referencedDbd;
	}
	
	@XmlAttribute(name="REFERENCEDFIELD")
	public String getReferencesField() {
		return referencesField;
	}

	public void setReferencesField(String referencesField) {
		this.referencesField = referencesField;
	}
	
	@XmlAttribute(name="REFERENCEDTABLE")
	public String getReferencedTable() {
		return referencedTable;
	}

	public void setReferencedTable(String referencedTable) {
		this.referencedTable = referencedTable;
	}

	@XmlElement(name = "FIELDATTRIBUTE", type = FieldAttribute.class)
	public List<FieldAttribute> getFieldAttributeList() {
	        return fieldAtList;
	}

	public void setFieldAttributeList(List<FieldAttribute> sourceFieldList) {
	        this.fieldAtList = sourceFieldList;
	}

	public void addFieldAttribute(FieldAttribute list) {
	        if (this.fieldAtList == null) {
	            this.fieldAtList = new ArrayList<FieldAttribute>();
	        }
	       this.fieldAtList.add(list);
	}
	    
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
		return fieldNumber;
	}
	public void setFieldNumber(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}
	@XmlAttribute(name="FIELDPROPERTY")
	public int getFieldProperty() {
		return fieldProperty;
	}
	public void setFieldProperty(int fieldProperty) {
		this.fieldProperty = fieldProperty;
	}
	@XmlAttribute(name="FIELDTYPE")
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	@XmlAttribute(name="HIDDEN")
	public String getHidden() {
		return hidden;
	}
	public void setHidden(String hidden) {
		this.hidden = hidden;
	}
	@XmlAttribute(name="KEYTYPE")
	public String getKeyType() {
		return keyType;
	}
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}
	@XmlAttribute(name="LENGTH")
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	@XmlAttribute(name="LEVEL")
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
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
	@XmlAttribute(name="OCCURS")
	public int getOccurs() {
		return occurs;
	}
	public void setOccurs(int occurs) {
		this.occurs = occurs;
	}
	@XmlAttribute(name="OFFSET")
	public int getOffSet() {
		return offSet;
	}
	public void setOffSet(int offSet) {
		this.offSet = offSet;
	}
	@XmlAttribute(name="PHYSICALLENGTH")
	public int getPhysicalLength() {
		return physicalLength;
	}
	public void setPhysicalLength(int physicalLength) {
		this.physicalLength = physicalLength;
	}
	@XmlAttribute(name="PHYSICALOFFSET")
	public int getPhysicalOffSet() {
		return physicalOffSet;
	}
	public void setPhysicalOffSet(int physicalOffSet) {
		this.physicalOffSet = physicalOffSet;
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
	@XmlAttribute(name="USAGE_FLAGS")
	public String getUsage_Flags() {
		return usage_Flags;
	}
	public void setUsage_Flags(String usage_Flags) {
		this.usage_Flags = usage_Flags;
	}
	
}
