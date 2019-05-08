package com.ETLCodeGen.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder={"taskList", "taskInstanceList","workFlowLinkList","workFlowVariableList","atList"})
public class Worklet {
		
	String description;
	String isValid;
	String name;
	String reusable;
	String versionNumber;
	
	List<Task> taskList;
	List<TaskInstance> taskInstanceList;
	List<WorkFlowLink> workFlowLinkList;
	List<WorkFlowVariable> workFlowVariableList;
	List<Attribute> atList;
	
	@XmlElement(name = "ATTRIBUTE", type = Attribute.class)
	public List<Attribute> getAtList() {
		return atList;
	}
	public void setAtList(List<Attribute> atList) {
		this.atList = atList;
	}
	
	public void addAttribute(Attribute att) {
		if (this.atList == null) {
			this.atList = new ArrayList<Attribute>();
		}
		this.atList.add(att);
	}
	
	@XmlElement(name = "TASK", type = Task.class)
	public List<Task> getTaskList() {
		return taskList;
	}
	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}
	
	public void addTask(Task task) {
		if (this.taskList == null) {
			this.taskList = new ArrayList<Task>();
		}
		this.taskList.add(task);
	}
	
	@XmlElement(name = "TASKINSTANCE", type = TaskInstance.class)
	public List<TaskInstance> getTaskInstanceList() {
		return taskInstanceList;
	}
	public void setTaskInstanceList(List<TaskInstance> taskInstanceList) {
		this.taskInstanceList = taskInstanceList;
	}
	
	public void addTaskInstance(TaskInstance taskInstance) {
		if (this.taskInstanceList == null) {
			this.taskInstanceList = new ArrayList<TaskInstance>();
		}
		this.taskInstanceList.add(taskInstance);
	}
	
	@XmlElement(name = "WORKFLOWLINK", type = WorkFlowLink.class)
	public List<WorkFlowLink> getWorkFlowLinkList() {
		return workFlowLinkList;
	}
	public void setWorkFlowLinkList(List<WorkFlowLink> workFlowLinkList) {
		this.workFlowLinkList = workFlowLinkList;
	}
	
	public void addWorkFlowLink(WorkFlowLink workFlowLink) {
		if (this.workFlowLinkList == null) {
			this.workFlowLinkList = new ArrayList<WorkFlowLink>();
		}
		this.workFlowLinkList.add(workFlowLink);
	}
	
	@XmlElement(name = "WORKFLOWVARIABLE", type = WorkFlowVariable.class)
	public List<WorkFlowVariable> getWorkFlowVariableList() {
		return workFlowVariableList;
	}
	public void setWorkFlowVariableList(List<WorkFlowVariable> workFlowVariableList) {
		this.workFlowVariableList = workFlowVariableList;
	}
	
	public void addWorkFlowVariable(WorkFlowVariable workFlowVariable) {
		if (this.workFlowVariableList == null) {
			this.workFlowVariableList = new ArrayList<WorkFlowVariable>();
		}
		this.workFlowVariableList.add(workFlowVariable);
	}
	
	@XmlAttribute(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlAttribute(name = "ISVALID")
	public String getIsValid() {
		return isValid;
	}
	public void setIsValid(String isValid) {
		this.isValid = isValid;
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
	
	@XmlAttribute(name = "VERSIONNUMBER")
	public String getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	
}
