	package com.ETLCodeGen.model;

	import javax.xml.bind.annotation.XmlAccessType;
	import javax.xml.bind.annotation.XmlAccessorType;
	import javax.xml.bind.annotation.XmlAttribute;

	@XmlAccessorType(XmlAccessType.PROPERTY)
	public class IntAttribute {
	    String name;
	    int value1;
	    
	    @XmlAttribute(name="VALUE")
	    public int getValue1() {
			return value1;
		}

		public void setValue1(int value1) {
			this.value1 = value1;
		}

		@XmlAttribute(name="NAME")
	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public IntAttribute(String name,int value1){
	        this.name = name;
	        this.value1 = value1;
	    }
}
