package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class WorkFlowLink {
	public WorkFlowLink(String condition, String fromTask, String toTask) {
		super();
		this.condition = condition;
		this.fromTask = fromTask;
		this.toTask = toTask;
	}
	String condition;
	String fromTask;
	String toTask;
	
	@XmlAttribute(name = "CONDITION")
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	@XmlAttribute(name = "FROMTASK")
	public String getFromTask() {
		return fromTask;
	}
	public void setFromTask(String fromTask) {
		this.fromTask = fromTask;
	}
	
	@XmlAttribute(name = "TOTASK")
	public String getToTask() {
		return toTask;
	}
	public void setToTask(String toTask) {
		this.toTask = toTask;
	}
	
}
