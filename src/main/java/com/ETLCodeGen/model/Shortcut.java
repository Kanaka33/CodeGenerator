package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder={"comments", "dbName", "folderName", "name", "objectSubType", "objectType", "referenceDbName", "referenceType", "refObjectName", "repositoryName", "versionNumber"})
public class Shortcut {
	String comments;
	String folderName;
	String name;
	String objectSubType;
	String objectType;
	String refObjectName;
	String repositoryName;
	String versionNumber;
	String referenceType;
	String dbName;
	String referenceDbName;
	
	public Shortcut(String comments, String dbName, String folderName, String name, String objectSubType, String objectType,
			String referenceDbName, String referenceType, String refObjectName, String repositoryName, String versionNumber) {
		this.comments = comments;
		this.dbName = dbName;
		this.folderName = folderName;
		this.name = name;
		this.objectSubType = objectSubType;
		this.objectType = objectType;
		this.referenceDbName = referenceDbName;
		this.referenceType = referenceType;
		this.refObjectName = refObjectName;
		this.repositoryName = repositoryName;
		this.versionNumber = versionNumber;
	}
	public Shortcut(String comments, String folderName, String name, String objectSubType, String objectType,
			 String referenceType, String refObjectName, String repositoryName, String versionNumber) {
		this.comments = comments;
		this.folderName = folderName;
		this.name = name;
		this.objectSubType = objectSubType;
		this.objectType = objectType;
		this.referenceType = referenceType;
		this.refObjectName = refObjectName;
		this.repositoryName = repositoryName;
		this.versionNumber = versionNumber;
	}
	@XmlAttribute(name="REFERENCEDDBD")
	public String getReferenceDbName() {
		return referenceDbName;
	}
	public void setReferenceDbName(String referenceDbName) {
		this.referenceDbName = referenceDbName;
	}
	@XmlAttribute(name="DBDNAME")
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	@XmlAttribute(name="COMMENTS")
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	@XmlAttribute(name="FOLDERNAME")
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	@XmlAttribute(name="NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute(name="OBJECTSUBTYPE")
	public String getObjectSubType() {
		return objectSubType;
	}
	public void setObjectSubType(String objectSubType) {
		this.objectSubType = objectSubType;
	}
	@XmlAttribute(name="OBJECTTYPE")
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	@XmlAttribute(name="REFOBJECTNAME")
	public String getRefObjectName() {
		return refObjectName;
	}
	public void setRefObjectName(String refObjectName) {
		this.refObjectName = refObjectName;
	}
	@XmlAttribute(name="REPOSITORYNAME")
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	@XmlAttribute(name="VERSIONNUMBER")
	public String getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}
	@XmlAttribute(name="REFERENCETYPE")
	public String getReferenceType() {
		return referenceType;
	}
	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}
	
}
