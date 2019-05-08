package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class MetaDataExtension {
	String dataType;
	String description;
	String domainName;
	String isClientEditable = "YES";
	String isClientVisible = "YES";
	String isReusable = "NO";
	String isShareRead = "NO";
	String isShareWrite = "NO";
	int maxLength;
	String name;
	String value;
	String vendorName;
	
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
	@XmlAttribute(name="DOMAINNAME")
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	@XmlAttribute(name="ISCLIENTEDITABLE")
	public String getIsClientEditable() {
		return isClientEditable;
	}

	public void setIsClientEditable(String isClientEditable) {
		this.isClientEditable = isClientEditable;
	}
	@XmlAttribute(name="ISCLIENTVISIBLE")
	public String getIsClientVisible() {
		return isClientVisible;
	}

	public void setIsClientVisible(String isClientVisible) {
		this.isClientVisible = isClientVisible;
	}
	@XmlAttribute(name="ISREUSABLE")
	public String getIsReusable() {
		return isReusable;
	}

	public void setIsReusable(String isReusable) {
		this.isReusable = isReusable;
	}
	@XmlAttribute(name="ISSHAREREAD")
	public String getIsShareRead() {
		return isShareRead;
	}
	
	public void setIsShareRead(String isShareRead) {
		this.isShareRead = isShareRead;
	}
	@XmlAttribute(name="ISSHAREWRITE")
	public String getIsShareWrite() {
		return isShareWrite;
	}

	public void setIsShareWrite(String isShareWrite) {
		this.isShareWrite = isShareWrite;
	}
	@XmlAttribute(name="MAXLENGTH")
	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	@XmlAttribute(name="NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute(name="VALUE")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	@XmlAttribute(name="VENDORNAME")
	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	
}
