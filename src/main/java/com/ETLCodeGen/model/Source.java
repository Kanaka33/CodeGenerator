package com.ETLCodeGen.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"fileList", "xmlInfoList", "groupList", "attributeList", "sourceFieldList"})
public class Source {
    String businessName;
    String dataBaseType;
    String dbName;
    String description;
    String name;
    String ownerName;
    int objectVersion;
    int versionNumber;
    List<SourceField> sourceFieldList;
    List<FlatFile> fileList;
    List<TableAttribute> attributeList;
    List<Group> groupList;
    List<XMLInfo> xmlInfoList;
   /* List<String> sourceList;
    
    @XmlElement(name = "SOURCE", type = Source.class)
    public List<String> getSourceList() {
		return sourceList;
	}

	public void setSourceList(List<String> sourceList) {
		this.sourceList = sourceList;
	}
	
	 public void addSourceList(List<String> sourceList) {
			if (this.sourceList == null) {
				this.sourceList = new ArrayList<String>();
			}
			this.sourceList.addAll(sourceList);
		}*/
	
	@XmlElement(name = "XMLINFO", type = XMLInfo.class)
    public List<XMLInfo> getXmlInfoList() {
        return xmlInfoList;
    }

    public void setXmlInfoList(List<XMLInfo> sourceFieldList) {
        this.xmlInfoList = sourceFieldList;
    }

    public void addXmlInfoList(XMLInfo xmlInfo) {
        if (this.xmlInfoList == null) {
            this.xmlInfoList = new ArrayList<XMLInfo>();
        }
        this.xmlInfoList.add(xmlInfo);
    }
    
    @XmlElement(name = "GROUP", type = Group.class)
	public List<Group> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<Group> groupList) {
		this.groupList = groupList;
	}
	
    public void addGroup(Group group) {
		if (this.groupList == null) {
			this.groupList = new ArrayList<Group>();
		}
		this.groupList.add(group);
	}

    @XmlElement(name = "FLATFILE", type = FlatFile.class)
    public List<FlatFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<FlatFile> fileList) {
		this.fileList = fileList;
	}
	@XmlElement(name = "TABLEATTRIBUTE", type = TableAttribute.class)
	public List<TableAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<TableAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public Source() {

    }
	
    public Source(String businessName, String dataBaseType, String dbName, String description, String name,
                  int objectVersion, String ownerName, int versionNumber) {
        this.businessName = businessName;
        this.dataBaseType = dataBaseType;
        this.dbName = dbName;
        this.description = description;
        this.name = name;
        this.objectVersion = objectVersion;
        this.ownerName = ownerName;
        this.versionNumber = versionNumber;
    }

    @XmlAttribute(name = "BUSINESSNAME")
    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    @XmlAttribute(name = "DATABASETYPE")
    public String getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(String dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    @XmlAttribute(name = "DBDNAME")
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @XmlAttribute(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlAttribute(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "OWNERNAME")
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @XmlAttribute(name = "OBJECTVERSION")
    public int getObjectVersion() {
        return objectVersion;
    }

    public void setObjectVersion(int objectVersion) {
        this.objectVersion = objectVersion;
    }

    @XmlAttribute(name = "VERSIONNUMBER")
    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @XmlElement(name = "SOURCEFIELD", type = SourceField.class)
    public List<SourceField> getSourceFieldList() {
        return sourceFieldList;
    }

    public void setSourceFieldList(List<SourceField> sourceFieldList) {
        this.sourceFieldList = sourceFieldList;
    }

    public void addSourceField(SourceField sourceField) {
        if (this.sourceFieldList == null) {
            this.sourceFieldList = new ArrayList<SourceField>();
        }
        this.sourceFieldList.add(sourceField);
    }
    
    public void addTableAttribute(TableAttribute targetField) {
        if (this.attributeList == null) {
            this.attributeList = new ArrayList<TableAttribute>();
        }
        this.attributeList.add(targetField);
    }

    
    public void addFlatFile(FlatFile flatFile) {
        if (this.fileList == null) {
            this.fileList = new ArrayList<FlatFile>();
        }
        this.fileList.add(flatFile);
    }

}
