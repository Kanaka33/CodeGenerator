package com.ETLCodeGen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ETLCodeGen.model.*;

import java.util.ArrayList;
import java.util.List;
//import java.util.Properties;

@Controller
@RequestMapping("mapping/")
public class InsUpdMappingController {

	// private Properties mappingProperties;
	public List<Connector> conList = new ArrayList<Connector>();
	@Autowired
	private ReadExcelController readExcelController;

	public Target getTargetForSharedFolder(String filePath) {
		String targetTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		Target target = new Target("", "", "Oracle", "", targetTableName, 1, "", 1);
		int emptyCount = 0;
		int count = 0;
		TargetField targetField = null;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "Z");
			String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
			String nullable = readExcelController.getCellValue(filePath, "Mapping Details", i, "AI");
			String prime = readExcelController.getCellValue(filePath, "Mapping Details", i, "AH");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				count++;
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
				Integer precision = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "AB").replace(".0", ""));
				Integer scale = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "AC").replace(".0", ""));
				targetField.setPrecision(precision);
				targetField.setScale(scale);
				target.addTargetField(targetField);
			}
		}
		return target;
	}

	public Source getStgSourceForSharedFolder(String filePath, String cbuName) {
		String sourceTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
		Source source = new Source("", "Oracle", cbuName + "_STG", "", sourceTableName, 1, cbuName + "_STAGE", 1);
		int emptyCount = 0;
		int count = 0;
		SourceField sourceField = null;
		Integer cumulativeLength = 0;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				count++;
				sourceField = new SourceField();
				sourceField.setBusinessName("");
				sourceField.setDescription("");
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "K");
				Integer length = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "N").replace(".0", ""));
				Integer precision = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "L").replace(".0", ""));
				Integer scale = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "M").replace(".0", ""));
				sourceField.setPhysicalOffSet(cumulativeLength);
				cumulativeLength += length;
				sourceField.setDataType(dataType);
				sourceField.setFieldNumber(count);
				sourceField.setFieldProperty(0);
				sourceField.setFieldType("ELEMITEM");
				sourceField.setHidden("NO");
				sourceField.setKeyType("NOT A KEY");
				if (dataType.equalsIgnoreCase("date")) {
					sourceField.setLength(length);
				} else {
					sourceField.setLength(0);
				}
				sourceField.setLevel(0);
				sourceField.setName(columnName);
				sourceField.setNullable("NULL");
				sourceField.setOccurs(0);
				sourceField.setOffSet(0);
				sourceField.setPhysicalLength(length);
				sourceField.setPictureText("");
				sourceField.setPrecision(precision);
				sourceField.setScale(scale);
				sourceField.setUsage_Flags("");
				source.addSourceField(sourceField);
			}
		}
		return source;
	}

	public Source getPrdSourceForSharedFolder(String filePath, String cbuName) {
		String sourceTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		Source source = new Source("", "Oracle", cbuName + "_PROD", "", sourceTableName, 1, cbuName + "_PROD", 1);
		int emptyCount = 0;
		int count = 0;
		SourceField sourceField = null;
		Integer cumulativeLength = 0;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				count++;
				sourceField = new SourceField();
				sourceField.setBusinessName("");
				sourceField.setDescription("");
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
				Integer length = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "AT").replace(".0", ""));
				Integer precision = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "AR").replace(".0", ""));
				Integer scale = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "AS").replace(".0", ""));
				String prime = readExcelController.getCellValue(filePath, "Mapping Details", i, "AX");
				String nullable = readExcelController.getCellValue(filePath, "Mapping Details", i, "AW");
				sourceField.setPhysicalOffSet(cumulativeLength);
				cumulativeLength += length;
				sourceField.setDataType(dataType);
				sourceField.setFieldNumber(count);
				sourceField.setFieldProperty(0);
				sourceField.setFieldType("ELEMITEM");
				sourceField.setHidden("NO");
				if (prime.equalsIgnoreCase("Y")) {
					sourceField.setKeyType("PRIMARY KEY");
				} else {
					sourceField.setKeyType("NOT A KEY");
				}
				if (dataType.equalsIgnoreCase("date")) {
					sourceField.setLength(length);
				} else {
					sourceField.setLength(0);
				}
				sourceField.setLevel(0);
				sourceField.setName(columnName);
				sourceField.setNullable(nullable);
				sourceField.setOccurs(0);
				sourceField.setOffSet(0);
				sourceField.setPhysicalLength(length);
				sourceField.setPictureText("");
				sourceField.setPrecision(precision);
				sourceField.setScale(scale);
				sourceField.setUsage_Flags("");
				source.addSourceField(sourceField);
			}
		}
		return source;
	}

	public List<TransformField> getTransformFieldForLookupTransformation(String filePath) {
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		String rlktableName = readExcelController.getCellValue(filePath, "Mapping Details", 24, "BB");
		TransformField transformField = null;
		int emptyCount = 0;
		List<String> list = new ArrayList<String>();
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "BB");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "BC");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "BD");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "BE");
				String toField = readExcelController.getCellValue(filePath, "Mapping Details", i, "BF");
				if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
					dataType = "date/time";
				} else if (dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("number")
						|| dataType.equalsIgnoreCase("numeric")) {
					dataType = "decimal";
				} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
					dataType = "string";
				}
				transformField.setDataType(dataType);
				transformField.setDefaultValue("");
				transformField.setDescription("");
				transformField.setName(columnName);
				transformField.setPictureText("");
				list.add(transformField.getName());
				if (columnName.startsWith("I_")) {
					transformField.setPortType("INPUT");
				} else {
					transformField.setPortType("LOOKUP/OUTPUT");
				}
				precision = precision.replace(".0", "");
				if (precision.matches("[0-9]+")) {
					transformField.setPrecision(Integer.parseInt(precision));
				}
				if (scale.matches("[0-9]+")) {
					transformField.setScale(Integer.parseInt(scale));
				}
				transformFieldList.add(transformField);

				if (toField.equalsIgnoreCase("Y")) {
					Connector con = new Connector();
					con.setFromField(transformField.getName());
					con.setFromInstance("sc_" + rlktableName);
					con.setFromInstanceType("Lookup Procedure");
					con.setToField("LKP_" + transformField.getName());
					con.setToInstance("RTRTRANS");
					con.setToInstanceType("Router");
					conList.add(con);
				}
			}
			// String toConnector = readExcelController.getCellValue(filePath, "Mapping
			// Details", 26, "BB");
			int count = 0;
			if (columnName.startsWith("I_")) {
				for (String s : list) {
					if (columnName.contains(s)) {
						Connector con1 = new Connector();
						con1.setFromField(s);
						con1.setFromInstance("EXPTRANS");
						con1.setFromInstanceType("Expression");
						con1.setToField(transformField.getName());
						con1.setToInstance("sc_" + rlktableName);
						con1.setToInstanceType("Lookup Procedure");
						conList.add(con1);
						count++;
					}
					if (count == 1) {
						break;
					}
				}
			}
		}
		return transformFieldList;
	}

	public List<TableAttribute> getTableAttributesForLookupTransformation(String filePath, String tableName,
			String connectionInfo) {
		List<TableAttribute> tableAttributeList = new ArrayList<TableAttribute>();

		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup Sql Override");
		tableAttribute.setValue(getSQLQueryForLookupTransformation(filePath, tableName, connectionInfo));
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup table name");
		tableAttribute.setValue(tableName);
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup Source Filter");
		tableAttribute.setValue("");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup caching enabled");
		tableAttribute.setValue("YES");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup policy on multiple match");
		tableAttribute.setValue("Use Last Value");
		tableAttributeList.add(tableAttribute);

		String lookupCondition = readExcelController.getCellValue(filePath, "Mapping Details", 43, "BJ");

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup condition");
		tableAttribute.setValue(lookupCondition);
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Connection Information");
		tableAttribute.setValue(connectionInfo);
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Source Type");
		tableAttribute.setValue("Database");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Recache if Stale");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup cache directory name");
		tableAttribute.setValue("$PMCacheDir");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup cache initialize");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup cache persistent");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup Data Cache Size");
		tableAttribute.setValue("20000000");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup Index Cache Size");
		tableAttribute.setValue("10000000");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Dynamic Lookup Cache");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Synchronize Dynamic Cache");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Output Old Value On Update");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Update Dynamic Cache Condition");
		tableAttribute.setValue("TRUE");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Cache File Name Prefix");
		tableAttribute.setValue("");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Re-cache from lookup source");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Insert Else Update");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Update Else Insert");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Datetime Format");
		tableAttribute.setValue("");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Thousand Separator");
		tableAttribute.setValue("None");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Decimal Separator");
		tableAttribute.setValue(".");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Case Sensitive String Comparison");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Null ordering");
		tableAttribute.setValue("Null Is Highest Value");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Sorted Input");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Lookup source is static");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Pre-build lookup cache");
		tableAttribute.setValue("Auto");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Subsecond Precision");
		tableAttribute.setValue("6");
		tableAttributeList.add(tableAttribute);

		return tableAttributeList;
	}

	private String getSQLQueryForLookupTransformation(String filePath, String lookupTableName, String connectionInfo) {
		String query = "SELECT ";
		int count = 0;
		String secondColumnName = "";
		String firstColumn = "";
		for (int i = 26;; i++) {
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "BB");
			if (columnName.startsWith("I_") || columnName == null || columnName.isEmpty()) {
				break;
			} else {
				count++;
				if (count == 1) {
					firstColumn = columnName;
				}
				if (count == 2) {
					secondColumnName = columnName;
				}
				query += "A." + columnName + " AS " + columnName + " ,";
			}
		}
		query = query.substring(0, query.length() - 1);

		query += "FROM " + connectionInfo + "." + lookupTableName + " A WHERE A." + secondColumnName
				+ " = (SELECT MAX(A2." + secondColumnName + ") FROM " + connectionInfo + "." + lookupTableName
				+ " A2 WHERE A." + firstColumn + " = A2." + firstColumn + ")";

		return query;
	}

	public List<TableAttribute> getTableAttributesForSQTransformation(String filePath, String cbuName) {
		List<TableAttribute> tableAttributeList = new ArrayList<TableAttribute>();

		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Sql Query");
		tableAttribute.setValue(getSQTANSMinusQuery(filePath, cbuName));
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("User Defined Join");
		tableAttribute.setValue("");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Source Filter");
		tableAttribute.setValue("");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Number Of Sorted Ports");
		tableAttribute.setValue("0");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Select Distinct");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Is Partitionable");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Pre SQL");
		tableAttribute.setValue("");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Post SQL");
		tableAttribute.setValue("");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Output is deterministic");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Output is repeatable");
		tableAttribute.setValue("Never");
		tableAttributeList.add(tableAttribute);

		return tableAttributeList;
	}

	public List<TransformField> getTransformFieldSQTransformation(String filePath) {
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		TransformField transformField = null;
		List<String> list = new ArrayList<String>();
		int emptyCount = 0;
		System.out.println("SQ");
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				// if(!(columnName.startsWith("REC_") || columnName.startsWith("EFF_"))){
				// emptyCount = 0;
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "K");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "L");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "M");
				if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
					dataType = "date/time";
				} else if (dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("number")
						|| dataType.equalsIgnoreCase("numeric")) {
					dataType = "decimal";
				} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
					dataType = "string";
				}
				transformField.setDataType(dataType);
				transformField.setDefaultValue("");
				transformField.setDescription("");
				transformField.setName(columnName);
				transformField.setPictureText("");
				transformField.setPortType("INPUT/OUTPUT");
				precision = precision.replace(".0", "");
				if (precision.matches("[0-9]+")) {
					transformField.setPrecision(Integer.parseInt(precision));
				}
				if (scale.matches("[0-9]+")) {
					transformField.setScale(Integer.parseInt(scale));
				}
				list.add(transformField.getName());
				/*
				 * if(!readExcelController.getCellValue(filePath, "Mapping Details", i,
				 * "R").isEmpty()) { String toField = readExcelController.getCellValue(filePath,
				 * "Mapping Details", i, "R");
				 * if(!toField.equalsIgnoreCase(transformField.getName())) {
				 */
				/*
				 * Connector con = new Connector(); con.setFromField(transformField.getName());
				 * con.setFromInstance("SQTRANS"); con.setFromInstanceType("Source Qualifier");
				 * con.setToField(transformField.getName()); con.setToInstance("EXPTRANS");
				 * con.setToInstanceType("Expression"); conList.add(con);
				 */
				/*
				 * isflag = false; list.clear(); System.out.println("isflag if::"+isflag); } }
				 */
				/*
				 * Connector con1 = new Connector();
				 * con1.setFromField(transformField.getName()); con1.setFromInstance("sc_" +
				 * stgName); con1.setFromInstanceType("Source Definition");
				 * con1.setToField(transformField.getName()); con1.setToInstance("SQTRANS");
				 * con1.setToInstanceType("Source Qualifier"); conList.add(con1);
				 * transformFieldList.add(transformField);
				 */
			}

			/*
			 * } if (list!=null){ for(String s : list) {
			 * System.out.println("isflag else::"+isflag); Connector con1 = new Connector();
			 * con1.setFromField(s); con1.setFromInstance("SQTRANS");
			 * con1.setFromInstanceType("Source Qualifier"); con1.setToField(s);
			 * con1.setToInstance("EXPTRANS"); con1.setToInstanceType("Expression");
			 * conList.add(con1); }
			 */
		}
		return transformFieldList;
	}

	public List<TransformField> getTransformFieldSQTransformation1(String filePath) {
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		TransformField transformField = null;
		List<String> list = new ArrayList<String>();
		System.out.println("SQ1");
		int emptyCount = 0;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
			String dvTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
			String stgTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
			// String columnName1 = readExcelController.getCellValue(filePath, "Mapping
			// Details", i, "J");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {

				// emptyCount = 0;

				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AR");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AS");
				if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
					dataType = "date/time";
				} else if (dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("number")
						|| dataType.equalsIgnoreCase("numeric")) {
					dataType = "decimal";
				} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
					dataType = "string";
				}
				transformField.setDataType(dataType);
				transformField.setDefaultValue("");
				transformField.setDescription("");
				transformField.setName(columnName);
				transformField.setPictureText("");
				transformField.setPortType("INPUT/OUTPUT");
				precision = precision.replace(".0", "");
				if (precision.matches("[0-9]+")) {
					transformField.setPrecision(Integer.parseInt(precision));
				}
				if (scale.matches("[0-9]+")) {
					transformField.setScale(Integer.parseInt(scale));
				}

				Connector con1 = new Connector();
				con1.setFromField(transformField.getName());
				con1.setFromInstance("SQTRANS");
				con1.setFromInstanceType("Source Qualifier");
				con1.setToField(transformField.getName());
				con1.setToInstance("EXPTRANS");
				con1.setToInstanceType("Expression");
				conList.add(con1);
				list.add(transformField.getDataType());
				transformFieldList.add(transformField);
				if (columnName.equalsIgnoreCase("REC_CREATE_DATE")) {
					Connector con = new Connector();
					con.setFromField(transformField.getName());
					con.setFromInstance("sc_" + stgTableName);
					con.setFromInstanceType("Source Definition");
					con.setToField(transformField.getName());
					con.setToInstance("SQTRANS");
					con.setToInstanceType("Source Qualifier");
					conList.add(con);
				} else {
					Connector con = new Connector();
					con.setFromField(columnName);
					con.setFromInstance("sc_" + dvTableName);
					con.setFromInstanceType("Source Definition");
					con.setToField(transformField.getName());
					con.setToInstance("SQTRANS");
					con.setToInstanceType("Source Qualifier");
					conList.add(con);
				}
			}
		}
		return transformFieldList;
	}

	public String getSQTANSMinusQuery(String filePath, String cbuName) {
		String stgQuery = "SELECT ";
		String prodQuery = "SELECT ";
		int emptyCount = 0;
		for (int i = 26;; i++) {
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
			String asColumnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
			String toDate = readExcelController.getCellValue(filePath, "Mapping Details", i, "Q");
			if (emptyCount == 10) {
				break;
			}
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else if (asColumnName != null && !asColumnName.isEmpty()) {
				if (toDate.equalsIgnoreCase("D")) {
					emptyCount = 0;
					stgQuery += "TO_DATE(" + columnName + ",'YYYYMMDD')" + " AS " + asColumnName + " ,";
					prodQuery += asColumnName + ",";

				} else {
					emptyCount = 0;
					stgQuery += columnName + " AS " + asColumnName + " ,";
					prodQuery += asColumnName + ",";
				}
			}
		}
		stgQuery = stgQuery.substring(0, stgQuery.length() - 1);
		prodQuery = prodQuery.substring(0, prodQuery.length() - 1);

		String stageTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
		String dvTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		stgQuery += "FROM " + cbuName + "_STAGE." + stageTableName;
		prodQuery += "FROM " + cbuName + "_PROD." + dvTableName + " WHERE EFF_TO_DATE>SYSDATE";
		String query = stgQuery + " MINUS " + prodQuery;
		return query;
	}

	public List<TransformField> getTransformFieldEXPTransformation(String filePath) {
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		TransformField transformField = null;
		int emptyCount = 0;
		System.out.println("exptrans");
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "Z");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				if (!columnName.startsWith("ETL_TRANSACTION_TYPE_CD")) {
					emptyCount = 0;
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AB");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AC");
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")) {
						dataType = "decimal";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}
					transformField.setDataType(dataType);
					transformField.setDefaultValue("");
					transformField.setDescription("");
					transformField.setName(columnName);
					transformField.setPictureText("");
					transformField.setPortType("INPUT/OUTPUT");
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
					System.out.println("exp ColumName" + transformField.getName());
					con.setFromInstance("EXPTRANS");
					con.setFromInstanceType("Expression");
					con.setToField(transformField.getName());
					con.setToInstance("RTRTRANS");
					con.setToInstanceType("Router");
					conList.add(con);
				}
				String expressionRen = readExcelController.getCellValue(filePath, "Mapping Details", 27, "BJ");
				String expression = "";
				if (columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
					TransformField insTransformField = new TransformField();
					insTransformField.setDataType("string");
					insTransformField.setDefaultValue("");
					insTransformField.setDescription("");
					expression = "&apos;I&apos;";
					insTransformField.setExpression(expression);
					insTransformField.setExpressionType("");
					insTransformField.setName("O_ETL_TRANSACTION_TYPE_CD_INS");
					insTransformField.setPictureText("");
					insTransformField.setPortType("OUTPUT");
					insTransformField.setPrecision(30);
					insTransformField.setScale(0);
					transformFieldList.add(insTransformField);
					Connector con = new Connector();
					con.setFromField(insTransformField.getName());
					con.setFromInstance("EXPTRANS");
					con.setFromInstanceType("Expression");
					con.setToField("ETL_TRANSACTION_TYPE_CD_INS");
					con.setToInstance("RTRTRANS");
					con.setToInstanceType("Router");
					conList.add(con);
					TransformField updTransformField = new TransformField();
					updTransformField.setDataType("string");
					updTransformField.setDefaultValue("");
					updTransformField.setDescription("");
					expression = "&apos;U&apos;";
					updTransformField.setExpression(expression);
					updTransformField.setExpressionType("");
					updTransformField.setName("O_ETL_TRANSACTION_TYPE_CD_UPD");
					updTransformField.setPictureText("");
					updTransformField.setPortType("OUTPUT");
					updTransformField.setPrecision(30);
					updTransformField.setScale(0);
					transformFieldList.add(updTransformField);
					Connector con1 = new Connector();
					con1.setFromField(updTransformField.getName());
					con1.setFromInstance("EXPTRANS");
					con1.setFromInstanceType("Expression");
					con1.setToField("ETL_TRANSACTION_TYPE_CD_UPD");
					con1.setToInstance("RTRTRANS");
					con1.setToInstanceType("Router");
					conList.add(con1);
					if(!expressionRen.isEmpty()) {
					TransformField renTransformField = new TransformField();
					renTransformField.setDataType("string");
					renTransformField.setDefaultValue("");
					renTransformField.setDescription("");
					expression = "&apos;R&apos;";
					renTransformField.setExpression(expression);
					renTransformField.setExpressionType("");
					renTransformField.setName("O_ETL_TRANSACTION_TYPE_CD_REN");
					renTransformField.setPictureText("");
					renTransformField.setPortType("OUTPUT");
					renTransformField.setPrecision(30);
					renTransformField.setScale(0);
					transformFieldList.add(renTransformField);
					Connector con2 = new Connector();
					con2.setFromField(renTransformField.getName());
					con2.setFromInstance("EXPTRANS");
					con2.setFromInstanceType("Expression");
					con2.setToField("ETL_TRANSACTION_TYPE_CD_REN");
					con2.setToInstance("RTRTRANS");
					con2.setToInstanceType("Router");
					conList.add(con2);
					}
				}
			}
		}
		return transformFieldList;
	}

	public List<Transformation> getIUDTransformation(String filePath) {
		List<Transformation> transformations = new ArrayList<Transformation>();
		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");
		String expression = readExcelController.getCellValue(filePath, "Mapping Details", 27, "BJ");
		String stageTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		Transformation iTransformation = new Transformation();
		iTransformation.setName("EXP_" + stageTableName + "_INS");
		iTransformation.setDescription("");
		iTransformation.setObjectVersion("1");
		iTransformation.setVersionNumber("1");
		iTransformation.setReUsable("NO");
		iTransformation.setType("Expression");
		iTransformation.addTableAttribute(tableAttribute);

		Transformation uTransformation = new Transformation();
		uTransformation.setName("EXP_" + stageTableName + "_UPD");
		uTransformation.setDescription("");
		uTransformation.setObjectVersion("1");
		uTransformation.setVersionNumber("1");
		uTransformation.setReUsable("NO");
		uTransformation.setType("Expression");
		uTransformation.addTableAttribute(tableAttribute);
		
		Transformation rTransformation = new Transformation();
		if(!expression.isEmpty()) {
		rTransformation.setName("EXP_" + stageTableName + "_REN");
		rTransformation.setDescription("");
		rTransformation.setObjectVersion("1");
		rTransformation.setVersionNumber("1");
		rTransformation.setReUsable("NO");
		rTransformation.setType("Expression");
		rTransformation.addTableAttribute(tableAttribute);
		}
		int emptyCount = 0;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			TransformField iTransformField = new TransformField();
			TransformField uTransformField = new TransformField();
			TransformField rTransformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "Z");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				// emptyCount = 0;
				String isLkp = readExcelController.getCellValue(filePath, "Mapping Details", i, "AK");
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AB");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AC");
				String isOp = readExcelController.getCellValue(filePath, "Mapping Details", i, "AL");
				String toField = columnName;
				if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
					dataType = "date/time";
				} else if (dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("number")
						|| dataType.equalsIgnoreCase("numeric")) {
					dataType = "decimal";
				} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
					dataType = "string";
				}
				if (!columnName.startsWith("EFF_")) {
					iTransformField.setDataType(dataType);
					iTransformField.setDefaultValue("");
					iTransformField.setDescription("");
					iTransformField.setPictureText("");
					if (columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
						iTransformField.setName(columnName + "_INS");
						iTransformField.setExpression(columnName + "_INS");
						Connector con2 = new Connector();
						con2.setFromField(iTransformField.getName() + "1");
						con2.setFromInstance("RTRTRANS");
						con2.setFromInstanceType("Router");
						con2.setToField(iTransformField.getName());
						con2.setToInstance("EXP_" + stageTableName + "_INS");
						con2.setToInstanceType("Expression");
						conList.add(con2);
					} else {
						iTransformField.setName(columnName);
						iTransformField.setExpression(columnName);
					}
					if (isOp.equalsIgnoreCase("O")) {
						iTransformField.setPortType("OUTPUT");
						iTransformField.setName("O_" + columnName);
						iTransformField.setExpression("SYSDATE");
					} else {
						iTransformField.setPortType("INPUT/OUTPUT");
					}
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						iTransformField.setPrecision(Integer.parseInt(precision));
					}
					if (scale.matches("[0-9]+")) {
						iTransformField.setScale(Integer.parseInt(scale));
					}
					iTransformField.setExpressionType("GENERAL");
					iTransformation.addTransformField(iTransformField);

					Connector con = new Connector();
					con.setFromField(iTransformField.getName());
					con.setFromInstance("EXP_" + stageTableName + "_INS");
					con.setFromInstanceType("Expression");
					con.setToField(toField);
					con.setToInstance("sc_" + stageTableName + "_INS");
					con.setToInstanceType("Target Definition");
					conList.add(con);
					if (!isOp.equalsIgnoreCase("O")) {
						Connector con2 = new Connector();
						con2.setFromField(iTransformField.getName() + "1");
						con2.setFromInstance("RTRTRANS");
						con2.setFromInstanceType("Router");
						con2.setToField(iTransformField.getName());
						con2.setToInstance("EXP_" + stageTableName + "_INS");
						con2.setToInstanceType("Expression");
						conList.add(con2);
					}
				}

				uTransformField.setDataType(dataType);
				uTransformField.setDefaultValue("");
				uTransformField.setDescription("");
				uTransformField.setPictureText("");
				if (columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
					uTransformField.setName(columnName + "_UPD");
					uTransformField.setExpression(columnName + "_UPD");
					Connector con = new Connector();
					con.setFromField(uTransformField.getName() + "3");
					con.setFromInstance("RTRTRANS");
					con.setFromInstanceType("Router");
					con.setToField(uTransformField.getName());
					con.setToInstance("EXP_" + stageTableName + "_UPD");
					con.setToInstanceType("Expression");
					conList.add(con);

				} else {
					if (isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N")) {
						uTransformField.setName("LKP_" + columnName);
						uTransformField.setExpression("LKP_" + columnName);
					} else {
						uTransformField.setName(columnName);
						uTransformField.setExpression(columnName);
					}
				}
				if (isOp.equalsIgnoreCase("O")) {
					uTransformField.setPortType("OUTPUT");
					uTransformField.setName("O_" + columnName);
					uTransformField.setExpression("SYSDATE");
				} else {
					uTransformField.setPortType("INPUT/OUTPUT");
				}
				precision = precision.replace(".0", "");
				if (precision.matches("[0-9]+")) {
					uTransformField.setPrecision(Integer.parseInt(precision));
				}
				if (scale.matches("[0-9]+")) {
					uTransformField.setScale(Integer.parseInt(scale));
				}
				uTransformField.setExpressionType("GENERAL");
				uTransformation.addTransformField(uTransformField);
				Connector con1 = new Connector();
				con1.setFromField(uTransformField.getName());
				con1.setFromInstance("EXP_" + stageTableName + "_UPD");
				con1.setFromInstanceType("Expression");
				con1.setToField(toField);
				con1.setToInstance("sc_" + stageTableName + "_UPD");
				con1.setToInstanceType("Target Definition");
				conList.add(con1);
				if (!(isOp.equalsIgnoreCase("O") || isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N"))) {
					Connector con = new Connector();
					con.setFromField(uTransformField.getName() + "3");
					con.setFromInstance("RTRTRANS");
					con.setFromInstanceType("Router");
					con.setToField(uTransformField.getName());
					con.setToInstance("EXP_" + stageTableName + "_UPD");
					con.setToInstanceType("Expression");
					conList.add(con);
				}
				if (!expression.isEmpty()){
						if(!columnName.startsWith("EFF_")) {
					System.out.println("NOT A EFF");
					rTransformField.setDataType(dataType);
					rTransformField.setDefaultValue("");
					rTransformField.setDescription("");
					rTransformField.setPictureText("");

					if (columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
						rTransformField.setName(columnName + "_REN");
						rTransformField.setExpression(columnName + "_REN");
						Connector rtrCon = new Connector();
						rtrCon.setFromField(rTransformField.getName() + "4");
						rtrCon.setFromInstance("RTRTRANS");
						rtrCon.setFromInstanceType("Router");
						rtrCon.setToField(rTransformField.getName());
						rtrCon.setToInstance("EXP_" + stageTableName + "_REN");
						rtrCon.setToInstanceType("Expression");
						conList.add(rtrCon);
					} else {
						if (isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N")) {
							rTransformField.setName("LKP_" + columnName);
							rTransformField.setExpression("LKP_" + columnName);
						} else {
							rTransformField.setName(columnName);
							rTransformField.setExpression(columnName);
						}
					}
					if (isOp.equalsIgnoreCase("O")) {

						rTransformField.setPortType("OUTPUT");
						rTransformField.setName("O_" + columnName);
						rTransformField.setExpression("SYSDATE");
					} else {
						rTransformField.setPortType("INPUT/OUTPUT");
					}
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						rTransformField.setPrecision(Integer.parseInt(precision));
					}
					if (scale.matches("[0-9]+")) {
						rTransformField.setScale(Integer.parseInt(scale));
					}

					rTransformField.setExpressionType("GENERAL");
					rTransformation.addTransformField(rTransformField);
					Connector con3 = new Connector();
					con3.setFromField(rTransformField.getName());
					con3.setFromInstance("EXP_" + stageTableName + "_REN");
					con3.setFromInstanceType("Expression");
					con3.setToField(toField);
					con3.setToInstance("sc_" + stageTableName + "_REN");
					con3.setToInstanceType("Target Definition");
					conList.add(con3);
					if (!(isOp.equalsIgnoreCase("O") || isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N"))) {
						Connector rtrCon = new Connector();
						rtrCon.setFromField(rTransformField.getName() + "4");
						rtrCon.setFromInstance("RTRTRANS");
						rtrCon.setFromInstanceType("Router");
						rtrCon.setToField(rTransformField.getName());
						rtrCon.setToInstance("EXP_" + stageTableName + "_REN");
						rtrCon.setToInstanceType("Expression");
						conList.add(rtrCon);
					}
				}
				}
			}
		}
		transformations.add(iTransformation);
		transformations.add(uTransformation);
		transformations.add(rTransformation);
		return transformations;
	}

	public List<TransformField> getTransformFieldRTRTransformation(String filePath, String group) {
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		// List<String> list = new ArrayList<String>();
		TransformField transformField = null;
		int emptyCount = 0;
		String appender = "";
		String expressionRen = readExcelController.getCellValue(filePath, "Mapping Details", 27, "BJ");
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "Z");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				if (!columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
					// emptyCount = 0;
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AB");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AC");
					String toField = "";
					transformField.setPortType("OUTPUT");
					if (group.equalsIgnoreCase("INSERT")) {
						appender = "1";

					} else if (group.equalsIgnoreCase("DEFAULT1")) {
						appender = "2";
					} else if (group.equalsIgnoreCase("UPDATE")) {
						appender = "3";
					} else if (group.equalsIgnoreCase("RENEW")) {
						appender = "4";
					} else {
						appender = "";
						transformField.setPortType("INPUT");
					}
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")) {
						dataType = "decimal";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}

					transformField.setDataType(dataType);
					transformField.setDefaultValue("");
					transformField.setDescription("");
					transformField.setGroup(group);
					transformField.setName(columnName + appender);
					toField = columnName;
					transformField.setPictureText("");
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						transformField.setPrecision(Integer.parseInt(precision));
					}
					if (!group.equalsIgnoreCase("INPUT")) {
						transformField.setRefField(toField);
					}
					if (scale.matches("[0-9]+")) {
						transformField.setScale(Integer.parseInt(scale));
					}
					transformFieldList.add(transformField);

				} else if (columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
					if (group.equalsIgnoreCase("INPUT")) {
						TransformField insTransformField = new TransformField();
						insTransformField.setDataType("string");
						insTransformField.setDefaultValue("");
						insTransformField.setDescription("");
						insTransformField.setGroup(group);
						insTransformField.setName("ETL_TRANSACTION_TYPE_CD_INS" + appender);
						insTransformField.setPictureText("");
						insTransformField.setPortType("INPUT");
						insTransformField.setPrecision(30);
						insTransformField.setScale(0);
						transformFieldList.add(insTransformField);
						TransformField updTransformField = new TransformField();
						updTransformField.setDataType("string");
						updTransformField.setDefaultValue("");
						updTransformField.setDescription("");
						updTransformField.setGroup(group);
						updTransformField.setName("ETL_TRANSACTION_TYPE_CD_UPD" + appender);
						updTransformField.setPictureText("");
						updTransformField.setPortType("INPUT");
						updTransformField.setPrecision(30);
						updTransformField.setScale(0);
						transformFieldList.add(updTransformField);
						if(!expressionRen.isEmpty()) {
						TransformField renTransformField = new TransformField();
						renTransformField.setDataType("string");
						renTransformField.setDefaultValue("");
						renTransformField.setDescription("");
						renTransformField.setGroup(group);
						renTransformField.setName("ETL_TRANSACTION_TYPE_CD_REN" + appender);
						renTransformField.setPictureText("");
						renTransformField.setPortType("INPUT");
						renTransformField.setPrecision(30);
						renTransformField.setScale(0);
						transformFieldList.add(renTransformField);
						}
					} else {
						TransformField insTransformField = new TransformField();
						insTransformField.setDataType("string");
						insTransformField.setDefaultValue("");
						insTransformField.setDescription("");
						insTransformField.setGroup(group);
						insTransformField.setName("ETL_TRANSACTION_TYPE_CD_INS" + appender);
						insTransformField.setPictureText("");
						insTransformField.setPortType("OUTPUT");
						insTransformField.setPrecision(30);
						insTransformField.setRefField("ETL_TRANSACTION_TYPE_CD_INS");
						insTransformField.setScale(0);
						transformFieldList.add(insTransformField);

						TransformField updTransformField = new TransformField();
						updTransformField.setDataType("string");
						updTransformField.setDefaultValue("");
						updTransformField.setDescription("");
						updTransformField.setGroup(group);
						updTransformField.setName("ETL_TRANSACTION_TYPE_CD_UPD" + appender);
						updTransformField.setPictureText("");
						updTransformField.setPortType("OUTPUT");
						updTransformField.setPrecision(30);
						updTransformField.setRefField("ETL_TRANSACTION_TYPE_CD_UPD");
						updTransformField.setScale(0);
						transformFieldList.add(updTransformField);
						if(!expressionRen.isEmpty()) {
						TransformField renTransformField = new TransformField();
						renTransformField.setDataType("string");
						renTransformField.setDefaultValue("");
						renTransformField.setDescription("");
						renTransformField.setGroup(group);
						renTransformField.setName("ETL_TRANSACTION_TYPE_CD_REN" + appender);
						renTransformField.setPictureText("");
						renTransformField.setPortType("OUTPUT");
						renTransformField.setPrecision(30);
						renTransformField.setRefField("ETL_TRANSACTION_TYPE_CD_REN");
						renTransformField.setScale(0);
						transformFieldList.add(renTransformField);
						}
					}
				}
			}
		}
		return transformFieldList;
	}

	public List<TransformField> getTransformFieldRTRTransformation_LKP(String filePath, String group) {
		String stg2TableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		TransformField transformField = null;
		int emptyCount = 0;
		String appender = "";
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "Z");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {

				String isLkp = readExcelController.getCellValue(filePath, "Mapping Details", i, "AK");
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AB");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AC");
				if (isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N")) {
					String toField = "";
					transformField.setPortType("OUTPUT");
					if (group.equalsIgnoreCase("INSERT")) {
						appender = "1";

					} else if (group.equalsIgnoreCase("DEFAULT1")) {
						appender = "2";
					} else if (group.equalsIgnoreCase("UPDATE")) {
						appender = "3";
					} else if (group.equalsIgnoreCase("RENEW")) {
						appender = "4";
					} else {
						appender = "";
						transformField.setPortType("INPUT");
					}
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")) {
						dataType = "decimal";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}

					transformField.setName("LKP_" + columnName + appender);
					toField = "LKP_" + columnName;
					transformField.setDataType(dataType);
					transformField.setDefaultValue("");
					transformField.setDescription("");
					transformField.setGroup(group);
					transformField.setPictureText("");
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						transformField.setPrecision(Integer.parseInt(precision));
					}
					if (!group.equalsIgnoreCase("INPUT")) {
						transformField.setRefField(toField);
					}
					if (scale.matches("[0-9]+")) {
						transformField.setScale(Integer.parseInt(scale));
					}
					transformFieldList.add(transformField);
					if (group.equalsIgnoreCase("UPDATE")) {
						if (isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N")) {
							Connector con = new Connector();
							con.setFromField(transformField.getName());
							con.setFromInstance("RTRTRANS");
							con.setFromInstanceType("Router");
							con.setToField(toField);
							con.setToInstance("EXP_" + stg2TableName + "_UPD");
							con.setToInstanceType("Expression");
							conList.add(con);
						}
					} else if (group.equalsIgnoreCase("RENEW")) {
						if (!columnName.startsWith("EFF_")) {
							if (isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N")) {
								Connector con3 = new Connector();
								con3.setFromField(transformField.getName());
								con3.setFromInstance("RTRTRANS");
								con3.setFromInstanceType("Router");
								con3.setToField(toField);
								con3.setToInstance("EXP_" + stg2TableName + "_REN");
								con3.setToInstanceType("Expression");
								conList.add(con3);
							}
						}
					}
				}
			}
		}
		return transformFieldList;
	}

	public List<Instance> getInstances(String filePath) {
		List<Instance> instanceList = new ArrayList<Instance>();
		String sourceStg = readExcelController.getCellValue(filePath, "Session Properties", 44, "F");
		String sourceProd = readExcelController.getCellValue(filePath, "Session Properties", 55, "F");

		String stg2TableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		String stgTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
		String dvTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		for (int i = 58;; i++) {
			String key = readExcelController.getCellValue(filePath, "Session Properties", i, "E");
			if (key != null && !key.isEmpty()) {
				String value = readExcelController.getCellValue(filePath, "Session Properties", i, "F");

				if (value.equalsIgnoreCase("Target Definition")) {
					Instance instance = new Instance();
					instance.setDescription("");
					instance.setName(key);
					instance.setTransformationName("sc_" + stg2TableName);
					instance.setTransformationType(value);
					instance.setType("TARGET");
					instanceList.add(instance);
				} else if (value.equalsIgnoreCase("Expression") || value.equalsIgnoreCase("Router")) {
					Instance instance = new Instance();
					instance.setDescription("");
					instance.setName(key);
					instance.setReusable("NO");
					instance.setTransformationName(key);
					instance.setTransformationType(value);
					instance.setType("TRANSFORMATION");
					instanceList.add(instance);
				} else if (value.equalsIgnoreCase("Lookup Procedure") || value.equalsIgnoreCase("Sequence")) {
					Instance instance = new Instance();
					instance.setDescription("");
					instance.setName(key);
					instance.setReusable("YES");
					instance.setTransformationName(key);
					instance.setTransformationType(value);
					instance.setType("TRANSFORMATION");
					instanceList.add(instance);

				} else if (value.equalsIgnoreCase("Source Definition")) {
					Instance instance = new Instance();
					if (key.contains("STG")) {
						instance.setDbName(sourceStg);
					} else {
						instance.setDbName(sourceProd);
					}
					instance.setDescription("");
					instance.setName(key);
					instance.setTransformationName(key);
					instance.setTransformationType(value);
					instance.setType("SOURCE");
					instanceList.add(instance);
				} else if (value.equalsIgnoreCase("Source Qualifier")) {
					Instance instance = new Instance();
					instance.setDescription("");
					instance.setName(key);
					instance.setReusable("NO");
					instance.setTransformationName(key);
					instance.setTransformationType(value);
					instance.setType("TRANSFORMATION");
					AssociatedSourceInstance as = new AssociatedSourceInstance();
					as.setName("sc_" + dvTableName);
					instance.addAssociated(as);
					AssociatedSourceInstance as1 = new AssociatedSourceInstance();
					as1.setName("sc_" + stgTableName);
					instance.addAssociated(as1);
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
		String stg2TableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		String stgTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
		String dvTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		String lookupTableName = readExcelController.getCellValue(filePath, "Mapping Details", 24, "BB");
		int count = 0;
		String cname = cbuName + "_SHARED";
		for (int i = 58;; i++) {
			if (!readExcelController.getCellValue(filePath, "Session Properties", i, "E").isEmpty()) {
				String key = readExcelController.getCellValue(filePath, "Session Properties", i, "E");
				String value = readExcelController.getCellValue(filePath, "Session Properties", i, "F");
				String sourceStg = readExcelController.getCellValue(filePath, "Session Properties", 44, "F");
				String sourceProd = readExcelController.getCellValue(filePath, "Session Properties", 55, "F");
				if (value.equalsIgnoreCase("Source Definition")) {
					if (key.contains("STG")) {
						Shortcut shortcut = new Shortcut("", sourceStg, cname, key, value, "SOURCE", sourceStg, "LOCAL",
								stgTableName, repoName, "1");

						list.add(shortcut);
					} else {
						Shortcut shortcut1 = new Shortcut("", sourceProd, cname, key, value, "SOURCE", sourceProd,
								"LOCAL", dvTableName, repoName, "1");

						list.add(shortcut1);
					}
				}
				if (value.equalsIgnoreCase("Target Definition")) {
					if (count >= 1)
						break;
					count++;

					String vName = "sc_" + stg2TableName;
					Shortcut shortcut2 = new Shortcut("", cname, vName, value, "TARGET", "LOCAL", stg2TableName,
							repoName, "1");
					list.add(shortcut2);
				}
				if (value.equalsIgnoreCase("Lookup Procedure")) {
					Shortcut shortcut2 = new Shortcut("", cname, key, value, "TRANSFORMATION", "LOCAL", lookupTableName,
							repoName, "1");

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
