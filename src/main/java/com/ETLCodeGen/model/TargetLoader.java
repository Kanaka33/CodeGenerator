package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class TargetLoader {
	String order;
	String targetInstance;

	@XmlAttribute(name = "ORDER")
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@XmlAttribute(name = "TARGETINSTANCE")
	public String getTargetInstance() {
		return targetInstance;
	}

	public void setTargetInstance(String targetInstance) {
		this.targetInstance = targetInstance;
	}

}
