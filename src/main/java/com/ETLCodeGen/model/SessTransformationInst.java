package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class SessTransformationInst {

    String sInstanceName;
    String transformationName;
    String transformationType;
    String partitionType;
    YesNoEnum isRepartitionPoint;
    String stage;
    String pipeline;

    //List<SessTransformationGroup> sessTransformationGroupList;
    List<Partition> partitionList;
    List<Attribute> attributeList;
    List<FlatFile> fileList;
    @XmlElement(name = "FLATFILE",type=FlatFile.class)
    public List<FlatFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<FlatFile> fileList) {
		this.fileList = fileList;
	}

	public SessTransformationInst(){

    }

    public SessTransformationInst(String sInstanceName, String transformationName, String transformationType, String partitionType, YesNoEnum isRepartitionPoint, String stage, String pipeline) {
        this.sInstanceName = sInstanceName;
        this.transformationName = transformationName;
        this.transformationType = transformationType;
        this.partitionType = partitionType;
        this.isRepartitionPoint = isRepartitionPoint;
        this.stage = stage;
        this.pipeline = pipeline;
    }

    @XmlAttribute(name="SINSTANCENAME",required = true)
    public String getsInstanceName() {
        return sInstanceName;
    }

    public void setsInstanceName(String sInstanceName) {
        this.sInstanceName = sInstanceName;
    }

    @XmlAttribute(name="TRANSFORMATIONNAME",required = true)
    public String getTransformationName() {
        return transformationName;
    }

    public void setTransformationName(String transformationName) {
        this.transformationName = transformationName;
    }

    @XmlAttribute(name="TRANSFORMATIONTYPE",required = true)
    public String getTransformationType() {
        return transformationType;
    }

    public void setTransformationType(String transformationType) {
        this.transformationType = transformationType;
    }

    @XmlAttribute(name="PARTITIONTYPE")
    public String getPartitionType() {
        return partitionType;
    }

    public void setPartitionType(String partitionType) {
        this.partitionType = partitionType;
    }

    @XmlAttribute(name="ISREPARTITIONPOINT")
    public YesNoEnum getIsRepartitionPoint() {
        return isRepartitionPoint;
    }

    public void setIsRepartitionPoint(YesNoEnum isRepartitionPoint) {
        this.isRepartitionPoint = isRepartitionPoint;
    }

    @XmlAttribute(name="STAGE")
    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    @XmlAttribute(name="PIPELINE")
    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }
    @XmlElement(name = "PARTITION",type=Partition.class)
    public List<Partition> getPartitionList() {
        return partitionList;
    }

    public void setPartitionList(List<Partition> partitionList) {
        this.partitionList = partitionList;
    }
    @XmlElement(name = "ATTRIBUTE",type=Attribute.class)
    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }
   /* @XmlElement(name = "ATTRIBUTE",type=SessTransformationGroup.class)
    public List<SessTransformationGroup> getSessTransformationGroupList() {
        return sessTransformationGroupList;
    }

    public void setSessTransformationGroupList(List<SessTransformationGroup> sessTransformationGroupList) {
        this.sessTransformationGroupList = sessTransformationGroupList;
    }*/

    public void addAttribute(Attribute attribute) {
        if (this.attributeList == null) {
            this.attributeList = new ArrayList<Attribute>();
        }
        this.attributeList.add(attribute);
    }
    
    public void addFile(FlatFile file) {
        if (this.fileList == null) {
            this.fileList = new ArrayList<FlatFile>();
        }
        this.fileList.add(file);
    }
    
    public void addPartition(Partition partition){
        if (this.partitionList == null) {
            this.partitionList = new ArrayList<Partition>();
        }
        this.partitionList.add(partition);
    }

   /* public void addSessTransformationGroup(SessTransformationGroup sessTransformationGroup){
        if (this.sessTransformationGroupList == null) {
            this.sessTransformationGroupList = new ArrayList<SessTransformationGroup>();
        }
        this.sessTransformationGroupList.add(sessTransformationGroup);
    }*/
}
