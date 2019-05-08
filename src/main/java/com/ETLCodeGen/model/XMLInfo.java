package com.ETLCodeGen.model;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class XMLInfo {

	List<XmlText> xmlText;
	@XmlElement(name = "XMLTEXT", type = XmlText.class)
	public List<XmlText> getXmlText() {
		return xmlText;
	}

	public void setXmlText(List<XmlText> xmlText) {
		this.xmlText = xmlText;
	}
	
	public void addXmlText(XmlText group) {
		if (this.xmlText == null) {
			this.xmlText = new ArrayList<XmlText>();
		}
		this.xmlText.add(group);
	}

}
