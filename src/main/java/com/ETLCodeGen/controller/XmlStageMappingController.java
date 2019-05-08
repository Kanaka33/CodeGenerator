package com.ETLCodeGen.controller;


	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Controller;
	import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import com.ETLCodeGen.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
	import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

	@Controller
	@RequestMapping("mapping/")
	public class XmlStageMappingController {

		public List<Connector> conList = new ArrayList<Connector>();
		
		public List<String> transList = new ArrayList<String>();
		
		@Autowired
		private ReadExcelController readExcelController;
		
		public List<FieldAttribute> getFieldAttributesForXMLSource(String name, String type){
			List<FieldAttribute> fieldAttributeList = new ArrayList<FieldAttribute>();
			FieldAttribute fieldAt = null;
			if(!name.isEmpty()) {
				 fieldAt = new FieldAttribute("XML Mapping", "");
			}else {
				 fieldAt = new FieldAttribute("XML Mapping", "./"+name);
			}
			FieldAttribute fieldAt1 = new FieldAttribute("IsElement", "0");
			FieldAttribute fieldAt2 = new FieldAttribute("PivotNumber", "0");
			FieldAttribute fieldAt3 = new FieldAttribute("XSDDatatype", "simpleType(xsd:"+ type +")");
			fieldAttributeList.add(fieldAt);
			fieldAttributeList.add(fieldAt1);
			fieldAttributeList.add(fieldAt2);
			fieldAttributeList.add(fieldAt3);
			
			return fieldAttributeList;
		}
		
		public List<TableAttribute> getAttributesForSource(String filePath) {
			List<TableAttribute> tableAttributeList = new ArrayList<TableAttribute>();

			TableAttribute tableAttribute = new TableAttribute();
			tableAttribute.setName("Base Table Name");
			tableAttribute.setValue("");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Search Specification");
			tableAttribute.setValue("");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Sort Specification");
			tableAttribute.setValue("");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Add Currently Processed XML File Name Port");
			tableAttribute.setValue("NO");
			tableAttributeList.add(tableAttribute);

			return tableAttributeList;
		}

		public Source getSourceForSharedFolder(String filePath, String xmlpath, String sourceTableName) {
			
	               
			String sourceName = readExcelController.getCellValue(filePath, "Mapping Details", 2, "G");
			String sourceType = readExcelController.getCellValue(filePath, "Mapping Details", 3, "G");
			Source source = new Source("", sourceType, sourceName, "", sourceTableName, 1, "", 1);
			FlatFile file = new FlatFile();
			file.setCodePage("Latin1");
			file.setConsecDelimitersAsOne("NO");
			file.setDelimited("YES");
			file.setDelimiters("|");
			file.setEscapeCharacter("");
			file.setKeepEscapeChar("NO");
			file.setLineSequential("NO");
			file.setMultidelimiters("NO");
			file.setNullCharType("ASCII");
			file.setNullCharacter("*");
			file.setPadBytes("1");
			file.setQuoteCharacter("NONE");
			file.setRepeatable("NO");
			file.setRowDelimeter("10");
			file.setShiftSensitiveData("NO");
			file.setSkipRows("0");
			file.setStripTrailingBlanks("NO");
			source.addFlatFile(file);
			XMLInfo xmlInfo = new XMLInfo();
			
			XmlText xmlText = new XmlText();
			xmlText.setText("&lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-8&quot;?&gt;&#xA;&lt;METASCHEMA ORIGIN=&quot;xml,reposit&quot; version=&quot;7.01&quot;&gt;&#xA;&lt;NAMESPACEINFO NAMESPACE=&quot;&quot; PREFIX=&quot;&quot; ELEMENT-FORM-DEFAULT=&quot;unqualified&quot; ATTRIBUTE-FORM-DEFAULT=&quot;unqualified&quot; ROOTNAMESPACE=&quot;true&quot;&gt;&#xA;&lt;/NAMESPACEINFO&gt;&#xA;&lt;/METASCHEMA&gt;&#xA;");
			xmlText.setType("ADAPTER");
			XmlText xmlText1 = new XmlText();
			//xmlText1.setText(line+" ");
			xmlText1.setText("&lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-8&quot;?&gt;&#xA;&lt;xsd:schema xmlns:xsd=&quot;http://www.w3.org/2001/XMLSchema&quot; xmlns:infatype=&quot;http://www.informatica.com/types/&quot; elementFormDefault=&quot;unqualified&quot; attributeFormDefault=&quot;unqualified&quot;&gt;&#xA;&lt;xsd:element name=&quot;contact&quot;&gt;&#xA;&lt;xsd:complexType&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;xsd:element ref=&quot;name&quot; minOccurs=&quot;1&quot; maxOccurs=&quot;unbounded&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;email&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;phone&quot; minOccurs=&quot;1&quot; maxOccurs=&quot;unbounded&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;address&quot;/&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;customer&quot;&gt;&#xA;&lt;xsd:complexType&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;xsd:element ref=&quot;id&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;contact&quot;/&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;adf&quot;&gt;&#xA;&lt;xsd:complexType&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;xsd:element ref=&quot;appointment&quot; minOccurs=&quot;1&quot; maxOccurs=&quot;unbounded&quot;/&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;name&quot;&gt;&#xA;&lt;xsd:complexType mixed=&quot;true&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;256&quot;&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;xsd:attribute ref=&quot;part&quot;/&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;phone&quot;&gt;&#xA;&lt;xsd:complexType mixed=&quot;true&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;30&quot;&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;xsd:attribute ref=&quot;type&quot;/&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;address&quot;&gt;&#xA;&lt;xsd:complexType&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;xsd:element ref=&quot;street&quot; minOccurs=&quot;1&quot; maxOccurs=&quot;unbounded&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;city&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;regioncode&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;postalcode&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;country&quot;/&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;xsd:attribute ref=&quot;type&quot;/&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;odometer&quot;&gt;&#xA;&lt;xsd:complexType mixed=&quot;true&quot; infatype:type=&quot;xsd:integer&quot;&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;xsd:attribute ref=&quot;units&quot;/&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;dealer&quot;&gt;&#xA;&lt;xsd:complexType&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;xsd:element ref=&quot;name&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;id&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;contact&quot;/&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;appointment&quot;&gt;&#xA;&lt;xsd:complexType&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;xsd:element ref=&quot;calldate&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;id&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;state&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;confkey&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;source&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;requestdate&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;servicedesc&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;comments&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;vehicle&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;customer&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;dealer&quot;/&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;vehicle&quot;&gt;&#xA;&lt;xsd:complexType&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;xsd:element ref=&quot;year&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;make&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;model&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;vin&quot;/&gt;&#xA;&lt;xsd:element ref=&quot;odometer&quot;/&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;street&quot;&gt;&#xA;&lt;xsd:complexType mixed=&quot;true&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;128&quot;&gt;&#xA;&lt;xsd:sequence&gt;&#xA;&lt;/xsd:sequence&gt;&#xA;&lt;xsd:attribute ref=&quot;line&quot;/&gt;&#xA;&lt;/xsd:complexType&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;year&quot; type=&quot;xsd:integer&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;make&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;30&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;email&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;512&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:attribute name=&quot;part&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;5&quot;&gt;&#xA;&lt;/xsd:attribute&gt;&#xA;&#xA;&lt;xsd:element name=&quot;source&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;30&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;vin&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;17&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;calldate&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;19&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;country&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;128&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;id&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;128&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;city&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;128&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;model&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;30&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;regioncode&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;32&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;confkey&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;30&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;state&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;32&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:attribute name=&quot;type&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;4&quot;&gt;&#xA;&lt;/xsd:attribute&gt;&#xA;&#xA;&lt;xsd:element name=&quot;postalcode&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;10&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:attribute name=&quot;units&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;5&quot;&gt;&#xA;&lt;/xsd:attribute&gt;&#xA;&#xA;&lt;xsd:attribute name=&quot;line&quot; type=&quot;xsd:integer&quot; infatype:type=&quot;xsd:integer&quot;&gt;&#xA;&lt;/xsd:attribute&gt;&#xA;&#xA;&lt;xsd:element name=&quot;comments&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;4000&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;requestdate&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;19&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;xsd:element name=&quot;servicedesc&quot; type=&quot;xsd:string&quot; infatype:type=&quot;xsd:string&quot; infatype:length=&quot;4000&quot;&gt;&#xA;&lt;/xsd:element&gt;&#xA;&#xA;&lt;/xsd:schema&gt;&#xA;");
			xmlText1.setType("SCHEMA");
			XmlText xmlText2 = new XmlText();
			xmlText2.setText("&lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-8&quot;?&gt;&#xA;&lt;METATABLE version=&quot;7.01&quot;&gt;&#xA;&lt;GROUP GROUPNAME=&quot;XTIME_ADF&quot; OPTIONS=&quot;|FillAllFKs&quot;&gt;&#xA;&lt;GROUPSAT&gt;adf&lt;/GROUPSAT&gt;&#xA;&lt;/GROUP&gt;&#xA;&lt;GROUP GROUPNAME=&quot;XTIME_APPT&quot;&gt;&#xA;&lt;GROUPSAT&gt;adf/appointment&lt;/GROUPSAT&gt;&#xA;&lt;COLUMN COLUMN_NAME=&quot;FK_X_APPOINTMENT_X_ADF&quot; RELATIONSHIP=&quot;hierarchy&quot;/&gt;&#xA;&lt;/GROUP&gt;&#xA;&lt;GROUP GROUPNAME=&quot;XTIME_CUSTOMER_NAME&quot;&gt;&#xA;&lt;GROUPSAT&gt;adf/appointment/customer/contact/name&lt;/GROUPSAT&gt;&#xA;&lt;COLUMN COLUMN_NAME=&quot;FK_X_CUSTOMER_NAME_X_APPT&quot; RELATIONSHIP=&quot;hierarchy&quot;/&gt;&#xA;&lt;/GROUP&gt;&#xA;&lt;GROUP GROUPNAME=&quot;XTIME_CUSTOMER_PHONE&quot;&gt;&#xA;&lt;GROUPSAT&gt;adf/appointment/customer/contact/phone&lt;/GROUPSAT&gt;&#xA;&lt;COLUMN COLUMN_NAME=&quot;FK_X_CUSTOMER_PHONE_X_APPT&quot; RELATIONSHIP=&quot;hierarchy&quot;/&gt;&#xA;&lt;/GROUP&gt;&#xA;&lt;GROUP GROUPNAME=&quot;XTIME_CUSTOMER_STREET&quot;&gt;&#xA;&lt;GROUPSAT&gt;adf/appointment/customer/contact/address/street&lt;/GROUPSAT&gt;&#xA;&lt;COLUMN COLUMN_NAME=&quot;FK_X_CUSTOMER_STREET_X_APPT&quot; RELATIONSHIP=&quot;hierarchy&quot;/&gt;&#xA;&lt;/GROUP&gt;&#xA;&lt;GROUP GROUPNAME=&quot;XTIME_DEALER_NAME&quot;&gt;&#xA;&lt;GROUPSAT&gt;adf/appointment/dealer/contact/name&lt;/GROUPSAT&gt;&#xA;&lt;COLUMN COLUMN_NAME=&quot;FK_X_DEALER_NAME_X_APPT&quot; RELATIONSHIP=&quot;hierarchy&quot;/&gt;&#xA;&lt;/GROUP&gt;&#xA;&lt;GROUP GROUPNAME=&quot;XTIME_DEALER_PHONE&quot;&gt;&#xA;&lt;GROUPSAT&gt;adf/appointment/dealer/contact/phone&lt;/GROUPSAT&gt;&#xA;&lt;COLUMN COLUMN_NAME=&quot;FK_X_DEALER_PHONE_X_APPT&quot; RELATIONSHIP=&quot;hierarchy&quot;/&gt;&#xA;&lt;/GROUP&gt;&#xA;&lt;GROUP GROUPNAME=&quot;XTIME_DEALER_STREET&quot;&gt;&#xA;&lt;GROUPSAT&gt;adf/appointment/dealer/contact/address/street&lt;/GROUPSAT&gt;&#xA;&lt;COLUMN COLUMN_NAME=&quot;FK_X_DEALER_STREET_X_APPT&quot; RELATIONSHIP=&quot;hierarchy&quot;/&gt;&#xA;&lt;/GROUP&gt;&#xA;&lt;/METATABLE&gt;&#xA;");
			xmlText2.setType("TABLE");
			xmlInfo.addXmlText(xmlText);
			xmlInfo.addXmlText(xmlText1);
			xmlInfo.addXmlText(xmlText2);
			source.addXmlInfoList(xmlInfo);
			List<Group> gList = new ArrayList<Group>();
			gList = getGroupList(filePath);
			for(Group group : gList) {
			source.addGroup(group);
			}
			source.setAttributeList(getAttributesForSource(filePath));
			//need to modify
			int count = 0;
			int emptyCount = 0;
			Integer cumulativeLength = 0;
			Group group = new Group();
			SourceField sourceField = null;
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "B");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					if(readExcelController.getCellValue(filePath, "Mapping Details", i, "C").isEmpty()) {
						group.setName(columnName);
					}else {
					String type = readExcelController.getCellValue(filePath, "Mapping Details", i, "C");
					Integer precision = Integer.parseInt(readExcelController.getCellValue(filePath, "Mapping Details", i, "D").replace(".0", ""));
	                Integer scale = Integer.parseInt(readExcelController.getCellValue(filePath,"Mapping Details",i,"E").replace(".0",""));
		            Integer length = Integer.parseInt(readExcelController.getCellValue(filePath, "Mapping Details", i, "F").replace(".0", ""));
		         
					
					
				count++;
				sourceField = new SourceField();
				sourceField.setBusinessName("");
				sourceField.setDescription("");
				sourceField.setPhysicalOffSet(cumulativeLength);
				cumulativeLength += length;
				sourceField.setDataType(type);
				sourceField.setFieldNumber(count);
				sourceField.setFieldProperty(0);
				sourceField.setFieldType("ELEMITEM");
				sourceField.setGroup(group.getName());
				sourceField.setHidden("NO");
				if(columnName.startsWith("XPK_")) {
					sourceField.setKeyType("PRIMARY/GENERATED KEY");
					sourceField.setNullable("NOTNULL");
				}else if(columnName.startsWith("FK_")) {
					sourceField.setKeyType("FOREIGN KEY");
					sourceField.setNullable("NULL");
					sourceField.setReferencedDbd(readExcelController.getCellValue(filePath, "Mapping Details", 2, "G"));
					sourceField.setReferencedTable(readExcelController.getCellValue(filePath, "Mapping Details", 23, "B"));
					sourceField.setReferencesField(readExcelController.getCellValue(filePath, "Mapping Details", i, "G"));//need to check
				} else {
					sourceField.setKeyType("NOT A KEY");
					sourceField.setNullable("NULL");
				}
				/*if (type.equalsIgnoreCase("date")) {
					sourceField.setLength(length);
				} else {
					sourceField.setLength(0);
				}*/
				sourceField.setLength(length);
				sourceField.setLevel(0);
				sourceField.setName(columnName);
				sourceField.setOccurs(0);
				sourceField.setOffSet(0);
				sourceField.setPhysicalLength(precision);
				sourceField.setPictureText("");
				sourceField.setPrecision(precision);
				sourceField.setScale(scale);
				sourceField.setUsage_Flags("");
				sourceField.setFieldAttributeList(getFieldAttributesForXMLSource(columnName, type));
				source.addSourceField(sourceField);
					}
			/*try { 
	            // parse the document
	            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	            Document doc = docBuilder.parse(xmlpath); 
	            NodeList list = doc.getElementsByTagName("xs:element"); 
	            int gcount=0;
	            Group group = null;
	            //loop to print data
	            for(int i = 0 ; i < list.getLength(); i++)
	            {
	            	Element first = (Element)list.item(i);
	                Node node = list.item(i);
	                if(first.hasAttributes())
	                {
	                    String name = first.getAttribute("name"); 
	                    //System.out.println(name); 
	                    String type = first.getAttribute("type"); 
	                    //System.out.println(type); 
	                    if(!name.isEmpty()) {
	                    if(node.hasChildNodes()) {
	                    	 gcount++;
	                    	type = "Integer";
	                    	group = new Group();
	                    	// <GROUP DESCRIPTION ="" NAME ="XTIME_APPT" ORDER ="2" TYPE ="OUTPUT"/>
	                    	//if(first.getAttribute("maxOccurs").equalsIgnoreCase("unbounded")) {
	                    		group.setDescription("");
	                    		group.setName("X_"+name.toUpperCase());
	                    		group.setOrder(""+gcount);
	                    		group.setType("OUTPUT");
	                    		source.addGroup(group);
	                    	//}
	                    	source.setAttributeList(getAttributesForSource(filePath));
	                    	
	                    	Integer cumulativeLength = 0;
	    					count++;
	    					sourceField = new SourceField();
	    					sourceField.setBusinessName("");
	    					sourceField.setDescription("");
	    					sourceField.setPhysicalOffSet(cumulativeLength);
	    					//cumulativeLength += length;
	    					
	    					sourceField.setDataType(type);
	    					sourceField.setFieldNumber(count);
	    					sourceField.setFieldProperty(0);
	    					sourceField.setFieldType("ELEMITEM");
	    					sourceField.setGroup(group.getName());
	    					sourceField.setHidden("NO");
	    					sourceField.setKeyType("PRIMARY/GENERATED KEY");
	    					sourceField.setLength(0);
	    					sourceField.setLevel(0);
	    					sourceField.setName("XPK_X_"+name.toUpperCase());
	    					sourceField.setNullable("NOTNULL");
	    					sourceField.setOccurs(0);
	    					sourceField.setOffSet(0);
	    					sourceField.setPhysicalLength(0);
	    					sourceField.setPictureText("");
	    					sourceField.setPrecision(0);
	    					sourceField.setScale(0);
	    					sourceField.setUsage_Flags("");
	    					sourceField.setFieldAttributeList(getFieldAttributesForXMLSource("", type));
	    					source.addSourceField(sourceField);
	    					if((gcount/2)==1) {
	    						count++;
		    					sourceField = new SourceField();
		    					sourceField.setBusinessName("");
		    					sourceField.setDescription("");
		    					sourceField.setPhysicalOffSet(cumulativeLength);
		    					//cumulativeLength += length;
		    					sourceField.setDataType(type);
		    					sourceField.setFieldNumber(count);
		    					sourceField.setFieldProperty(0);
		    					sourceField.setFieldType("ELEMITEM");
		    					sourceField.setGroup(group.getName());
		    					sourceField.setHidden("NO");
		    					sourceField.setKeyType("FOREIGN KEY");
		    					sourceField.setLength(0);
		    					sourceField.setLevel(0);
		    					sourceField.setName("FK_X_"+name.toUpperCase());
		    					sourceField.setNullable("NULL");
		    					sourceField.setOccurs(0);
		    					sourceField.setOffSet(0);
		    					sourceField.setPhysicalLength(0);
		    					sourceField.setPictureText("");
		    					sourceField.setPrecision(0);
		    					sourceField.setScale(0);
		    					sourceField.setUsage_Flags("");
		    					sourceField.setFieldAttributeList(getFieldAttributesForXMLSource("", type));
		    					source.addSourceField(sourceField);
	    					}
	                    }else {
	                Integer cumulativeLength = 0;
					count++;
					sourceField = new SourceField();
					sourceField.setBusinessName("");
					sourceField.setDescription("");
					sourceField.setPhysicalOffSet(cumulativeLength);
					//cumulativeLength += length;
					sourceField.setDataType(type);
					sourceField.setFieldNumber(count);
					sourceField.setFieldProperty(0);
					sourceField.setFieldType("ELEMITEM");
					sourceField.setGroup(group.getName());
					sourceField.setHidden("NO");
					sourceField.setKeyType("NOT A KEY");
					if (dataType.equalsIgnoreCase("date")) {
						sourceField.setLength(length);
					} else {
						sourceField.setLength(0);
					}
					sourceField.setLength(0);
					sourceField.setLevel(0);
					sourceField.setName(name);
					sourceField.setNullable("NULL");
					sourceField.setOccurs(0);
					sourceField.setOffSet(0);
					sourceField.setPhysicalLength(0);
					sourceField.setPictureText("");
					sourceField.setPrecision(0);
					sourceField.setScale(0);
					sourceField.setUsage_Flags("");
					sourceField.setFieldAttributeList(getFieldAttributesForXMLSource(name, type));
					source.addSourceField(sourceField);
				 }
	                    }
		            }
	            }
		       } 
		        catch (ParserConfigurationException e) 
		        {
		            e.printStackTrace();
		        }
		        catch (SAXException e) 
		        { 
		            e.printStackTrace();
		        }
		        catch (IOException ed) 
		        {
		            ed.printStackTrace();
		        } */
				}
			}
			return source;
			
		}

		public Target getTargetPrdForSharedFolder(String filePath) {
			String targetTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
			Target target = new Target("", "", "Oracle", "", targetTableName, 1, "", 1);
			int emptyCount = 0;
			int count = 0;
			TargetField targetField = null;

			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					
					if(!readExcelController.getCellValue(filePath, "Mapping Details", i, "K").isEmpty())
					{
					count++;
					targetField = new TargetField();
					targetField.setBusinessName("");
					targetField.setDescription("");
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "K");
					Integer precision = Integer.parseInt(
							readExcelController.getCellValue(filePath, "Mapping Details", i, "L").replace(".0", ""));
					Integer scale = Integer.parseInt(
							readExcelController.getCellValue(filePath, "Mapping Details", i, "M").replace(".0", ""));
					targetField = new TargetField();
					targetField.setBusinessName("");
					targetField.setDataType(dataType);
					targetField.setDescription("");
					targetField.setFieldNumber(count);
					targetField.setKeyType("NOT A KEY");
					targetField.setName(columnName);
					targetField.setNullable("NULL");
					targetField.setPictureText("");
					targetField.setPrecision(precision);
					targetField.setScale(scale);
					target.addTargetField(targetField);
					}
				}
			}
			return target;
		}
		
		public Target getTargetPrd1ForSharedFolder(String filePath) {
			String targetTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "BR");
			Target target = new Target("", "", "Oracle", "", targetTableName, 1, "", 1);
			int emptyCount = 0;
			int count = 0;
			TargetField targetField = null;

			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "BR");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					count++;
					targetField = new TargetField();
					targetField.setBusinessName("");
					targetField.setDescription("");
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "BS");
					Integer precision = Integer.parseInt(
							readExcelController.getCellValue(filePath, "Mapping Details", i, "BT").replace(".0", ""));
					Integer scale = Integer.parseInt(
							readExcelController.getCellValue(filePath, "Mapping Details", i, "BU").replace(".0", ""));
					String prime = readExcelController.getCellValue(filePath, "Mapping Details", i, "BZ");
					String nullable = readExcelController.getCellValue(filePath, "Mapping Details", i, "BY");
					targetField = new TargetField();
					targetField.setBusinessName("");
					targetField.setDataType(dataType);
					targetField.setDescription("");
					targetField.setFieldNumber(count);
					if (prime.equalsIgnoreCase("Y")) {
						targetField.setKeyType("PRIMARY KEY");
					} else {
						targetField.setKeyType("NOT A KEY");
					}
					targetField.setName(columnName);
					targetField.setNullable(nullable);
					targetField.setPictureText("");
					targetField.setPrecision(precision);
					targetField.setScale(scale);
					target.addTargetField(targetField);
				}
			}
			return target;
		}
		
		public List<TableAttribute> getTableAttributesForSQTransformation(String filePath, String cbuName) {
			List<TableAttribute> tableAttributeList = new ArrayList<TableAttribute>();

			TableAttribute tableAttribute = new TableAttribute();
			
			tableAttribute = new TableAttribute();
			tableAttribute.setName("Tracing Level");
			tableAttribute.setValue("Normal");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Reset");
			tableAttribute.setValue("YES");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Restart");
			tableAttribute.setValue("YES");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Validate XML Source");
			tableAttribute.setValue("Validate only if DTD/Schema is present");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Is Partitionable");
			tableAttribute.setValue("NO");
			tableAttributeList.add(tableAttribute);

			return tableAttributeList;
		}
		
		public List<Group> getGroupList(String filePath){
			List<Group> glist = new ArrayList<Group>();
		
			/*try { 
	            // parse the document
	            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	            Document doc = docBuilder.parse(xsdpath); 
	            NodeList list = doc.getElementsByTagName("xs:element"); 
	            int gcount=0;
	            Group group = null;
	            //loop to print data
	            for(int k = 0 ; k < list.getLength(); k++)
	            {
	            	Element first = (Element)list.item(k);
	                Node node = list.item(k);
	                if(first.hasAttributes())
	                {
	                    String name = first.getAttribute("name"); 
	                    if(!name.isEmpty()) {
	                    if(node.hasChildNodes()) {
	                    	gcount++;
	                    	// <GROUP DESCRIPTION ="" NAME ="XTIME_APPT" ORDER ="2" TYPE ="OUTPUT"/>
	                    	    group = new Group();
	        	                group.setDescription("");
	                    		group.setName("XTIME_"+name.toUpperCase());
	                    		group.setOrder(""+gcount);
	                    		group.setType("OUTPUT");
	                    		glist.add(group);
	                    	}
	                    	
	                    }
	                }
	            }
			}catch(Exception e) {
				e.printStackTrace();
			}*/
			int emptyCount = 0;
			int gcount=0;
			Group group = null;
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "B");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					if( readExcelController.getCellValue(filePath, "Mapping Details", i, "C").isEmpty()) {
						gcount++;
                    	// <GROUP DESCRIPTION ="" NAME ="XTIME_APPT" ORDER ="2" TYPE ="OUTPUT"/>
                    	    group = new Group();
        	                group.setDescription("");
                    		group.setName(columnName);
                    		group.setOrder(""+gcount);
                    		group.setType("OUTPUT");
                    		glist.add(group);
                 		
					}else {
						
					}
				}
			}
			return glist;
		}
		
		public List<TransformField> getTransformFieldSQTransformation(String filePath, String xsdpath) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			TransformField transformField = null;
			int emptyCount = 0;
			String sourceName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "B");
			System.out.println("SQ");
			Group group = new Group();
	                    
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				transformField = new TransformField();
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "B");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					if(readExcelController.getCellValue(filePath, "Mapping Details", i, "C").isEmpty()) {
						group.setName(columnName);
                 		
					}else {
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "C");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "D");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "E");
					String description = readExcelController.getCellValue(filePath, "Mapping Details", i, "H");
					
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("NUMC") || dataType.equalsIgnoreCase("number(p,s)")
							|| dataType.equalsIgnoreCase("number") || dataType.equalsIgnoreCase("numeric")) {
						dataType = "decimal";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("BIT")
							|| dataType.equalsIgnoreCase("varchar2") || dataType.equalsIgnoreCase("CLNT")) {
						dataType = "string";
					} else if (dataType.equalsIgnoreCase("NVARCHAR") || dataType.equalsIgnoreCase("NCHAR")) {
						dataType = "nstring";
					} else if (dataType.equalsIgnoreCase("Float")) {
						dataType = "double";
					} else if (dataType.equalsIgnoreCase("int")) {
						dataType = "integer";
					}
					transformField.setDataType(dataType);
					transformField.setDefaultValue("");
					transformField.setDescription(description);
					transformField.setGroup(group.getName());
					transformField.setName(columnName);
					transformField.setPictureText("");
					if(columnName.startsWith("XPK_")) {
						transformField.setPortType("GENERATED KEY/INPUT/OUTPUT");
						transformField.setRefSourceField(columnName);
						transformField.setSeqGeneratorVal("1");
					}else {
						transformField.setPortType("INPUT/OUTPUT");
					}
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						transformField.setPrecision(Integer.parseInt(precision));
					}
					
					if (scale.matches("[0-9]+")) {
						transformField.setScale(Integer.parseInt(scale));
					}
					transformFieldList.add(transformField);
					Connector con = new Connector();
					con.setFromField(transformField.getName());
					con.setFromInstance("sc_" + sourceName);
					con.setFromInstanceType("Source Definition");
					con.setToField(transformField.getName());
					con.setToInstance("XMLDSQ_sc_" + sourceName);
					con.setToInstanceType("XML Source Qualifier");
					conList.add(con);
					if(!readExcelController.getCellValue(filePath, "Mapping Details", i, "J").isEmpty()) {
					Connector con1 = new Connector();
					con1.setFromField(transformField.getName());
					con1.setFromInstance("XMLDSQ_sc_" + sourceName);
					con1.setFromInstanceType("XML Source Qualifier");
					con1.setToField(readExcelController.getCellValue(filePath, "Mapping Details", i, "J"));
					con1.setToInstance("EXPTRANS");
					con1.setToInstanceType("Expression");
					conList.add(con1);
					}
					}
				}
			}
			return transformFieldList;
		}

		public List<TransformField> getTransformFieldEXPTransformation(String filePath, String xsdpath) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			TransformField transformField = null;
			int emptyCount = 0;
			Group group = new Group();
			System.out.println("exptrans");
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				transformField = new TransformField();
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					emptyCount = 0;
					if(readExcelController.getCellValue(filePath, "Mapping Details", i, "K").isEmpty()) {
						group.setName(columnName);
                 		
					}else {
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "K");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "L");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "M");
					
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("integer") || dataType.equalsIgnoreCase("int")
							|| dataType.equalsIgnoreCase("NUMC") || dataType.equalsIgnoreCase("number(p,s)")
							|| dataType.equalsIgnoreCase("number") || dataType.equalsIgnoreCase("numeric")) {
						dataType = "decimal";
					} else if (dataType.equalsIgnoreCase("BIT") || dataType.equalsIgnoreCase("CHAR")
							|| dataType.equalsIgnoreCase("varchar2") || dataType.equalsIgnoreCase("CLNT")) {
						dataType = "string";
					} else if (dataType.equalsIgnoreCase("NVARCHAR") || dataType.equalsIgnoreCase("NCHAR")
							|| dataType.equalsIgnoreCase("NTEXT")) {
						dataType = "string";
					} else if (dataType.equalsIgnoreCase("Float")) {
						dataType = "double";
					}
					transformField.setDataType(dataType);
					transformField.setDefaultValue("");
					transformField.setDescription("");
					transformField.setName(columnName);
					transformField.setPictureText("");
					transformField.setPortType("INPUT");
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						transformField.setPrecision(Integer.parseInt(precision));
					}
					if (scale.matches("[0-9]+")) {
						transformField.setScale(Integer.parseInt(scale));
					}
					transformFieldList.add(transformField);
					}
				}
			}
			return transformFieldList;
		}

		public List<TransformField> getTransformFieldEXPTransformation1(String filePath, String xsdpath) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			TransformField iTransformField = null;
			int emptyCount = 0;
			String targetName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
			System.out.println("exptrans1");
			int recCount = 0;
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				iTransformField = new TransformField();
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					if(readExcelController.getCellValue(filePath, "Mapping Details", i, "K").isEmpty()) {
						
                 		
					}else {
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "K");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "L");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "M");
					
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("integer") || dataType.equalsIgnoreCase("int")
							|| dataType.equalsIgnoreCase("NUMC") || dataType.equalsIgnoreCase("number(p,s)")
							|| dataType.equalsIgnoreCase("number") || dataType.equalsIgnoreCase("numeric")) {
						dataType = "decimal";
					} else if (dataType.equalsIgnoreCase("BIT") || dataType.equalsIgnoreCase("CHAR")
							|| dataType.equalsIgnoreCase("varchar2") || dataType.equalsIgnoreCase("CLNT")) {
						dataType = "string";
					} else if (dataType.equalsIgnoreCase("NVARCHAR") || dataType.equalsIgnoreCase("NCHAR")
							|| dataType.equalsIgnoreCase("NTEXT")) {
						dataType = "string";
					} else if (dataType.equalsIgnoreCase("Float")) {
						dataType = "double";
					}
					iTransformField.setDataType(dataType);
					iTransformField.setDefaultValue("");
					iTransformField.setDescription("");
					iTransformField.setPictureText("");
					iTransformField.setPortType("OUTPUT");
					iTransformField.setName("O_" + columnName);
					iTransformField.setExpression("LTRIM(RTRIM(UPPER(" + columnName + ")))");
					/*String toDate = readExcelController.getCellValue(filePath, "Mapping Details", i, "G");
					if (toDate.equalsIgnoreCase("D")) {
						iTransformField.setExpression("TO_DATE(" + columnName + ",&apos;YYYY-MM-DD HH24:MI:SS&apos;)");
						iTransformField.setDataType("date/time");
					}*/
					if(columnName.startsWith("REC_")) {
						recCount++;
						iTransformField.setExpression("SYSDATE");
						iTransformField.setDataType("date/time");
					}
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						iTransformField.setPrecision(Integer.parseInt(precision));
					}
					if (scale.matches("[0-9]+")) {
						iTransformField.setScale(Integer.parseInt(scale));
					}
					iTransformField.setExpressionType("GENERAL");
					transformFieldList.add(iTransformField);
						Connector con = new Connector();
						con.setFromField(iTransformField.getName());
						con.setFromInstance("EXPTRANS");
						con.setFromInstanceType("Expression");
						con.setToField(columnName);
						con.setToInstance("sc_"+ targetName);
						con.setToInstanceType("Target Definition");
						conList.add(con);
					}
				}
			}
			if(recCount<1) {
			iTransformField = new TransformField();
			iTransformField.setDataType("date/time");
				iTransformField.setDefaultValue("");
				iTransformField.setDescription("");
				iTransformField.setPictureText("");
				iTransformField.setPortType("OUTPUT");
				iTransformField.setName("O_REC_CREATE_DATE");
				iTransformField.setExpression("SYSDATE");
				iTransformField.setExpressionType("GENERAL");
				iTransformField.setPrecision(Integer.parseInt("19"));
				iTransformField.setScale(Integer.parseInt("0"));
				transformFieldList.add(iTransformField);
				Connector con = new Connector();
				con.setFromField(iTransformField.getName());
				con.setFromInstance("EXPTRANS");
				con.setFromInstanceType("Expression");
				con.setToField("REC_CREATE_DATE");
				con.setToInstance("sc_"+ targetName);
				con.setToInstanceType("Target Definition");
				conList.add(con);
			}
			return transformFieldList;
		}
		
		public List<Instance> getInstances(String filePath, String cbuName) {
			List<Instance> instanceList = new ArrayList<Instance>();
			String sourceName = readExcelController.getCellValue(filePath, "Mapping Details", 2, "G");
			String sourceTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "B");
			for (int i = 58;; i++) {
				String key = readExcelController.getCellValue(filePath, "Session Properties", i, "B");
				if (key != null && !key.isEmpty()) {
					String value = readExcelController.getCellValue(filePath, "Session Properties", i, "C");
					if (value.equalsIgnoreCase("Target Definition")) {
						Instance instance = new Instance();
						instance.setDescription("");
						instance.setName("sc_" + key);
						instance.setTransformationName("sc_" + key);
						instance.setTransformationType(value);
						instance.setType("TARGET");
						instanceList.add(instance);
					} else if (value.equalsIgnoreCase("Source Definition")) {
						Instance instance = new Instance();
						instance.setDbName(sourceName);
						instance.setDescription("");
						instance.setName(key);
						instance.setTransformationName(key);
						instance.setTransformationType(value);
						instance.setType("SOURCE");
						instanceList.add(instance);
					} else if (value.equalsIgnoreCase("XML Source Qualifier")) {
						Instance instance = new Instance();
						instance.setDescription("");
						instance.setName(key);
						instance.setReusable("NO");
						instance.setTransformationName(key);
						instance.setTransformationType(value);
						instance.setType("TRANSFORMATION");
						AssociatedSourceInstance as1 = new AssociatedSourceInstance();
						as1.setName("sc_" + sourceTableName);
						instance.addAssociated(as1);
						instanceList.add(instance);
					} else {
						Instance instance = new Instance();
						instance.setDescription("");
						instance.setName(key);
						instance.setReusable("NO");
						instance.setTransformationName(key);
						instance.setTransformationType(value);
						instance.setType("TRANSFORMATION");
						instanceList.add(instance);
					}
				} else {
					break;
				}
			}
			return instanceList;
		}

		public List<Shortcut> getShortCut(String filePath, String cbuName, String repoName) {
			List<Shortcut> list = new ArrayList<Shortcut>();
			String sourceTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "B");
			String cname = cbuName + "_SHARED";
			for (int i = 58;; i++) {
				if (!readExcelController.getCellValue(filePath, "Session Properties", i, "B").isEmpty()) {
					String key = readExcelController.getCellValue(filePath, "Session Properties", i, "B");
					String value = readExcelController.getCellValue(filePath, "Session Properties", i, "C");
					String sourceName = readExcelController.getCellValue(filePath, "Mapping Details", 2, "G");
					if (value.equalsIgnoreCase("Source Definition")) {
						Shortcut shortcut = new Shortcut("", sourceName, cname, key, value, "SOURCE", sourceName, "LOCAL",
								sourceTableName, repoName, "1");
						list.add(shortcut);
					}
					if (value.equalsIgnoreCase("Target Definition")) {
						Shortcut shortcut2 = new Shortcut("", cname, "sc_"+key, value, "TARGET", "LOCAL", key, repoName,
								"1");
						list.add(shortcut2);
					}

				} else {
					break;
				}
			}
			return list;
		}

		public void resetConList() {
			this.conList = new ArrayList<Connector>();
		}

}
