package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Connector {
		String fromField;
		String fromInstance;
		String fromInstanceType;
		String toField;
		String toInstance;
		String toInstanceType;
		@XmlAttribute(name="FROMFIELD")
		public String getFromField() {
			return fromField;
		}
		public void setFromField(String fromField) {
			this.fromField = fromField;
		}
		@XmlAttribute(name="FROMINSTANCE")
		public String getFromInstance() {
			return fromInstance;
		}
		public void setFromInstance(String fromInstance) {
			this.fromInstance = fromInstance;
		}
		@XmlAttribute(name="FROMINSTANCETYPE")
		public String getFromInstanceType() {
			return fromInstanceType;
		}
		public void setFromInstanceType(String fromInstanceType) {
			this.fromInstanceType = fromInstanceType;
		}
		@XmlAttribute(name="TOFIELD")
		public String getToField() {
			return toField;
		}
		public void setToField(String toField) {
			this.toField = toField;
		}
		@XmlAttribute(name="TOINSTANCE")
		public String getToInstance() {
			return toInstance;
		}
		public void setToInstance(String toInstance) {
			this.toInstance = toInstance;
		}
		@XmlAttribute(name="TOINSTANCETYPE")
		public String getToInstanceType() {
			return toInstanceType;
		}
		public void setToInstanceType(String toInstanceType) {
			this.toInstanceType = toInstanceType;
		}
		
}
