package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class TaskInstance {
// <TASKINSTANCE DESCRIPTION="" ISENABLED="YES" NAME="Start" REUSABLE="NO" TASKNAME="Start" TASKTYPE="Start"/>
	public TaskInstance(String description, String fail, String fails, String isEnabled, String name, String reusable,
			String taskName, String taskType, String treat) {
		super();
		this.description = description;
		this.fail = fail;
		this.fails = fails;
		this.isEnabled = isEnabled;
		this.name = name;
		this.reusable = reusable;
		this.taskName = taskName;
		this.taskType = taskType;
		this.treat = treat;
	}
	public TaskInstance(String description, String isEnabled, String name, String reusable,
			String taskName, String taskType) {
		super();
		this.description = description;
		this.isEnabled = isEnabled;
		this.name = name;
		this.reusable = reusable;
		this.taskName = taskName;
		this.taskType = taskType;
	}
	String description;
	String fail;
	String fails;
	String isEnabled;
	String name;
	String reusable;
	String taskName;
	String taskType;
	String treat;
	
	@XmlAttribute(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlAttribute(name = "FAIL_PARENT_IF_INSTANCE_DID_NOT_RUN")
	public String getFail() {
		return fail;
	}
	public void setFail(String fail) {
		this.fail = fail;
	}
	
	@XmlAttribute(name = "FAIL_PARENT_IF_INSTANCE_FAILS")
	public String getFails() {
		return fails;
	}
	public void setFails(String fails) {
		this.fails = fails;
	}
	
	@XmlAttribute(name = "ISENABLED")
	public String getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	@XmlAttribute(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "REUSABLE")
	public String getReusable() {
		return reusable;
	}
	public void setReusable(String reusable) {
		this.reusable = reusable;
	}
	
	@XmlAttribute(name = "TASKNAME")
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	@XmlAttribute(name = "TASKTYPE")
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	@XmlAttribute(name = "TREAT_INPUTLINK_AS_AND")
	public String getTreat() {
		return treat;
	}
	public void setTreat(String treat) {
		this.treat = treat;
	}
	
}
