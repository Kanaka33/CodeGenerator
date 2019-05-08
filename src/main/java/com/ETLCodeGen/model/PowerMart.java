package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "POWERMART")
public class PowerMart {

    String repositoryVersion;
    String creationDate;
    List<Repository> repositoryList;

    public PowerMart(){

    }
    public PowerMart(String repositoryVersion, String creationDate) {
        this.repositoryVersion = repositoryVersion;
        this.creationDate = creationDate;
    }

    @XmlAttribute(name="REPOSITORY_VERSION")
    public String getRepositoryVersion() {
        return repositoryVersion;
    }

    public void setRepositoryVersion(String repositoryVersion) {
        this.repositoryVersion = repositoryVersion;
    }

    @XmlAttribute(name="CREATION_DATE")
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @XmlElement(name = "REPOSITORY",type=Repository.class)
    public List<Repository> getRepositoryList() {
        return repositoryList;
    }

    public void setRepositoryList(List<Repository> repositoryList) {
        this.repositoryList = repositoryList;
    }

    public void addRepository(Repository repository){
        if(this.repositoryList == null){
            this.repositoryList = new ArrayList<Repository>();
        }
        this.repositoryList.add(repository);
    }
}
