package com.ETLCodeGen.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"transformationList", "instanceList", "connectorList", "tLoaderList","mvariable","erpInfoList"})
public class Mapping {
    String name;
    String description;
    String isValid;
    int objectVersion;
    int versionNumber;
    List<Transformation> transformationList;
    List<Instance> instanceList;
    List<Connector> connectorList;
    List<TargetLoader> tLoaderList;
    List<ErpInfo> erpInfoList;
    List<MappingVariable> mvariable;
    
    @XmlElement(name = "MAPPINGVARIABLE", type = MappingVariable.class)
    public List<MappingVariable> getMvariable() {
		return mvariable;
	}

	public void setMvariable(List<MappingVariable> mvariable) {
		this.mvariable = mvariable;
	}

	@XmlElement(name = "ERPINFO", type = ErpInfo.class)
    public List<ErpInfo> getErpInfoList() {
        return erpInfoList;
    }

    public void setErpInfoList(List<ErpInfo> erpInfoList) {
        this.erpInfoList = erpInfoList;
    }

    @XmlElement(name = "TARGETLOADORDER", type = TargetLoader.class)
    public List<TargetLoader> gettLoaderList() {
        return tLoaderList;
    }

    public void settLoaderList(List<TargetLoader> tLoaderList) {
        this.tLoaderList = tLoaderList;
    }


    //need to add constructor and inner tags.
    @XmlAttribute(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @XmlElement(name = "TRANSFORMATION", type = Transformation.class)
    public List<Transformation> getTransformationList() {
        return transformationList;
    }

    public void setTransformationList(List<Transformation> transformationList) {
        this.transformationList = transformationList;
    }

    @XmlElement(name = "INSTANCE", type = Instance.class)
    public List<Instance> getInstanceList() {
        return instanceList;
    }

    public void setInstanceList(List<Instance> instanceList) {
        this.instanceList = instanceList;
    }

    @XmlElement(name = "CONNECTOR", type = Connector.class)
    public List<Connector> getConnectorList() {
        return connectorList;
    }

    public void setConnectorList(List<Connector> connectorList) {
        this.connectorList = connectorList;
    }

    public void addTransformation(Transformation transformation) {
        if (this.transformationList == null) {
            this.transformationList = new ArrayList<Transformation>();
        }
        this.transformationList.add(transformation);
    }

    public void addTransformations(List<Transformation> transformations) {
        if (this.transformationList == null) {
            this.transformationList = new ArrayList<Transformation>();
        }
        for (Transformation t : transformations) {
            this.transformationList.add(t);
        }
    }

    public void addInstance(List<Instance> instances) {
        if (this.instanceList == null) {
            this.instanceList = new ArrayList<Instance>();
        }
        for (Instance i : instances) {
            this.instanceList.add(i);
        }
    }

    public void addConnector(List<Connector> connectors) {
        if (this.connectorList == null) {
            this.connectorList = new ArrayList<Connector>();
        }
        for (Connector i : connectors) {
            this.connectorList.add(i);
        }
    }

    public void addErpInfo(ErpInfo erpInfo) {
        if (this.erpInfoList == null) {
            this.erpInfoList = new ArrayList<ErpInfo>();
        }
        this.erpInfoList.add(erpInfo);
    }
    
    public void addMappingVariable(MappingVariable mVariable) {
        if (this.mvariable == null) {
            this.mvariable = new ArrayList<MappingVariable>();
        }
        this.mvariable.add(mVariable);
    }

    public void addTargetLoader(TargetLoader loads) {
        // TODO Auto-generated method stub
        if (this.tLoaderList == null) {
            this.tLoaderList = new ArrayList<TargetLoader>();
        }

        this.tLoaderList.add(loads);

    }


}
