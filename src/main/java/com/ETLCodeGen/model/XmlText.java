package com.ETLCodeGen.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class XmlText {

	String text;
	String type;
	@XmlAttribute(name = "TEXT")
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@XmlAttribute(name = "TYPE")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
