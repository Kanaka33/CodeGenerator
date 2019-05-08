package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Repository {

    String name;
    String databaseType;
    String codePage;
    String version;
    String parentRepository;

    List<Folder> folderList;

    public Repository(String name, String version, String codePage, String databaseType) {
        this.databaseType = databaseType;
        this.codePage = codePage;
        this.version = version;
        this.name = name;
    }

    @XmlAttribute(name="NAME",required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name="DATABASETYPE")
    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    @XmlAttribute(name="CODEPAGE")
    public String getCodePage() {
        return codePage;
    }

    public void setCodePage(String codePage) {
        this.codePage = codePage;
    }

    @XmlAttribute(name="VERSION")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlAttribute(name="PARENTREPOSITORY")
    public String getParentRepository() {
        return parentRepository;
    }

    public void setParentRepository(String parentRepository) {
        this.parentRepository = parentRepository;
    }

    @XmlElement(name = "FOLDER",type=Folder.class)
    public List<Folder> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<Folder> folderList) {
        this.folderList = folderList;
    }

    public void addFolder(Folder folder){
        if(this.folderList == null){
            this.folderList = new ArrayList<Folder>();
        }
        this.folderList.add(folder);
    }
}
