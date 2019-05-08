package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ConnectionReference {

    String variable;
    String connectionType;
    String connectionSubType;
    String connectionNumber;
    String connectionName;
    String cnxRefName;
    String componentVersion;

    public ConnectionReference(){

    }

    public ConnectionReference(String variable, String connectionType, String connectionSubType, String connectionNumber, String connectionName, String cnxRefName) {
        this.variable = variable;
        this.connectionType = connectionType;
        this.connectionSubType = connectionSubType;
        this.connectionNumber = connectionNumber;
        this.connectionName = connectionName;
        this.cnxRefName = cnxRefName;
    }

    @XmlAttribute(name = "VARIABLE")
    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    @XmlAttribute(name = "CONNECTIONTYPE")
    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    @XmlAttribute(name = "CONNECTIONSUBTYPE")
    public String getConnectionSubType() {
        return connectionSubType;
    }

    public void setConnectionSubType(String connectionSubType) {
        this.connectionSubType = connectionSubType;
    }

    @XmlAttribute(name = "CONNECTIONNUMBER")
    public String getConnectionNumber() {
        return connectionNumber;
    }

    public void setConnectionNumber(String connectionNumber) {
        this.connectionNumber = connectionNumber;
    }

    @XmlAttribute(name = "CONNECTIONNAME")
    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    @XmlAttribute(name = "CNXREFNAME")
    public String getCnxRefName() {
        return cnxRefName;
    }

    public void setCnxRefName(String cnxRefName) {
        this.cnxRefName = cnxRefName;
    }

    @XmlAttribute(name = "COMPONENTVERSION")
    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }
}
