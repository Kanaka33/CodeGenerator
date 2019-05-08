package com.ETLCodeGen.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Folder {

    String name;
    String description;
    SharedEnum shared;
    String owner;
    String group ;
    String permissions;
    String uuid;

    List<Config> configList;
    List<Session> sessionList;
    List<Mapping> mappingList;
    List<Transformation> transformationList;
    List<Source> sourceList;
    List<Target> targetList;
    List<Shortcut> shortcutList;
    List<Worklet> workletList;
    
  	public Folder(String name, String group, String owner, SharedEnum shared, String description, String permissions, String uuid) {
        this.name = name;
        this.uuid = uuid;
        this.permissions = permissions;
        this.description = description;
        this.shared = shared;
        this.owner = owner;
        this.group = group;
    }
    
    @XmlElement(name = "WORKLET", type = Worklet.class)
    public List<Worklet> getWorkletList() {
		return workletList;
	}


	public void setWorkletList(List<Worklet> workletList) {
		this.workletList = workletList;
	}


	@XmlElement(name = "SHORTCUT", type = Shortcut.class)
    public List<Shortcut> getShortcutList() {
		return shortcutList;
	}

	public void setShortcutList(List<Shortcut> shortcutList) {
		this.shortcutList = shortcutList;
	}

	public void addShortcut(List<Shortcut> shortcuts) {
		// TODO Auto-generated method stub 
		if (this.shortcutList == null) {
            this.shortcutList = new ArrayList<Shortcut>();
        }
		for(Shortcut i : shortcuts) {
	        this.shortcutList.add(i);
	        }
	}

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

    @XmlAttribute(name = "UUID")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @XmlAttribute(name = "PERMISSIONS")
    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    @XmlAttribute(name = "SHARED")
    public SharedEnum getShared() {
        return shared;
    }

    public void setShared(SharedEnum shared) {
        this.shared = shared;
    }

    @XmlAttribute(name = "OWNER")
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @XmlAttribute(name = "GROUP")
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @XmlElement(name = "CONFIG", type = Config.class)
    public List<Config> getConfigList() {
        return configList;
    }

    public void setConfigList(List<Config> configList) {
        this.configList = configList;
    }

    @XmlElement(name = "SESSION", type = Session.class)
    public List<Session> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<Session> sessionList) {
        this.sessionList = sessionList;
    }

    @XmlElement(name = "TRANSFORMATION", type = Transformation.class)
    public List<Transformation> getTransformationList() {
        return transformationList;
    }

    public void setTransformationList(List<Transformation> transformationList) {
        this.transformationList = transformationList;
    }

    @XmlElement(name = "MAPPING", type = Mapping.class)
    public List<Mapping> getMappingList() {
        return mappingList;
    }

    public void setMappingList(List<Mapping> mappingList) {
        this.mappingList = mappingList;
    }

    @XmlElement(name = "SOURCE", type = Source.class)
    public List<Source> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<Source> sourceList) {
        this.sourceList = sourceList;
    }

    @XmlElement(name = "TARGET", type = Target.class)
    public List<Target> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<Target> targetList) {
        this.targetList = targetList;
    }

    public void addConfig(Config config) {
        if (this.configList == null) {
            this.configList = new ArrayList<Config>();
        }
        this.configList.add(config);
    }

    public void addSession(Session session) {
        if (this.sessionList == null) {
            this.sessionList = new ArrayList<Session>();
        }
        this.sessionList.add(session);
    }
    
    public void addWorklet(Worklet work) {
        if (this.workletList == null) {
            this.workletList = new ArrayList<Worklet>();
        }
        this.workletList.add(work);
    }

    public void addTransformation(Transformation transformation) {
        if (this.transformationList == null) {
            this.transformationList = new ArrayList<Transformation>();
        }
        this.transformationList.add(transformation);
    }

    public void addMapping(Mapping mapping){
        if (this.mappingList == null) {
            this.mappingList = new ArrayList<Mapping>();
        }
        this.mappingList.add(mapping);
    }

    public void addSource(Source source){
        if (this.sourceList == null) {
            this.sourceList = new ArrayList<Source>();
        }
        this.sourceList.add(source);
    }
    
   public void addTarget(Target target){
        if (this.targetList == null) {
            this.targetList = new ArrayList<Target>();
        }
        this.targetList.add(target);
    }
}
