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
public class DeleteMappingController {

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
				// boolean isPrime = false;
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
		String rlktableName = readExcelController.getCellValue(filePath, "Mapping Details", 24, "T");
		TransformField transformField = null;
		int emptyCount = 0;
		List<String> list = new ArrayList<String>();
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "T");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "U");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "V");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "W");
				String toField = readExcelController.getCellValue(filePath, "Mapping Details", i, "X");
				if (dataType.contains("date") || dataType.equalsIgnoreCase("Timestamp")) {
					dataType = "date/time";
				} else if (dataType.contains("number") || dataType.equalsIgnoreCase("number")
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

				if (toField.isEmpty()) {
					System.out.println("lookup ColumName" + transformField.getName());
					Connector con = new Connector();
					con.setFromField(columnName);
					con.setFromInstance("sc_" + rlktableName);
					con.setFromInstanceType("Lookup Procedure");
					con.setToField("LKP_" + columnName);
					con.setToInstance("RTRTRANS");
					con.setToInstanceType("Router");
					conList.add(con);
				}
			}
			// String toConnector = readExcelController.getCellValue(filePath, "Mapping
			// Details", 26, "T");
			int count = 0;
			if (columnName.startsWith("I_")) {
				for (String s : list) {
					if (columnName.contains(s)) {
						String fromField = readExcelController.getCellValue(filePath, "Mapping Details", i, "X");
						Connector con = new Connector();
						con.setFromField(fromField);
						con.setFromInstance("EXPTRANS");
						con.setFromInstanceType("Expression");
						con.setToField(transformField.getName());
						con.setToInstance("sc_" + rlktableName);
						con.setToInstanceType("Lookup Procedure");
						conList.add(con);
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

		String lookupCondition = readExcelController.getCellValue(filePath, "Mapping Details", 41, "BJ");

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
		tableAttribute.setValue("");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("User Defined Join");
		tableAttribute.setValue("");
		tableAttributeList.add(tableAttribute);

		String sourceFilter = readExcelController.getCellValue(filePath, "Mapping Details", 30, "BJ");

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Source Filter");
		tableAttribute.setValue(sourceFilter);
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
		System.out.println("SQ");
		int emptyCount = 0;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
			String dvTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
			String isLkp = readExcelController.getCellValue(filePath, "Mapping Details", i, "AY");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				// if(!(columnName.startsWith("REC_") || columnName.startsWith("EFF_"))){
				// emptyCount = 0;
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AR");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AS");
				if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
					dataType = "date/time";
				} else if (dataType.contains("number") || dataType.equalsIgnoreCase("number")
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
				Connector con = new Connector();
				con.setFromField(columnName);
				con.setFromInstance("sc_" + dvTableName);
				con.setFromInstanceType("Source Definition");
				con.setToField(transformField.getName());
				con.setToInstance("SQ_sc_" + dvTableName);
				con.setToInstanceType("Source Qualifier");
				conList.add(con);
				// list.add(transformField.getName());
				if (isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N")) {
					if (!columnName.equalsIgnoreCase("EFF_TO_DATE")) {
						Connector con1 = new Connector();
						con1.setFromField(transformField.getName());
						con1.setFromInstance("SQ_sc_" + dvTableName);
						con1.setFromInstanceType("Source Qualifier");
						con1.setToField(transformField.getName());
						con1.setToInstance("EXPTRANS");
						con1.setToInstanceType("Expression");
						conList.add(con1);
					}
				}
				transformFieldList.add(transformField);
			}
		}
		return transformFieldList;
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
				if (!columnName.startsWith("EFF_TO_DATE")) {
					emptyCount = 0;
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AB");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AC");
					String isLkp = readExcelController.getCellValue(filePath, "Mapping Details", i, "AK");
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.contains("number") || dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")) {
						dataType = "decimal";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}
					if (isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N")
							|| columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
						transformField.setDataType(dataType);
						transformField.setDefaultValue("");
						transformField.setDescription("");
						transformField.setExpression(columnName);
						transformField.setExpressionType("GENERAL");
						transformField.setName(columnName);
						transformField.setPictureText("");
						transformField.setPortType("INPUT/OUTPUT");
						if (columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
							transformField.setName("O_" + columnName);
							transformField.setExpression("&apos;D&apos;");
							transformField.setPortType("OUTPUT");
							// columnName = "O_" + columnName;
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
						System.out.println("exp ColumName" + transformField.getName());
						con.setFromInstance("EXPTRANS");
						con.setFromInstanceType("Expression");
						con.setToField(columnName);
						con.setToInstance("RTRTRANS");
						con.setToInstanceType("Router");
						conList.add(con);
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

		String stageTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		Transformation iTransformation = new Transformation();
		iTransformation.setName("EXP_" + stageTableName + "_DEL");
		iTransformation.setDescription("");
		iTransformation.setObjectVersion("1");
		iTransformation.setVersionNumber("1");
		iTransformation.setReUsable("NO");
		iTransformation.setType("Expression");
		iTransformation.addTableAttribute(tableAttribute);

		// TransformField iTransformField = null;
		// TransformField uTransformField = null;
		// TransformField rTransformField = null;
		int emptyCount = 0;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			TransformField iTransformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "Z");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				// emptyCount = 0;
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AB");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AC");
				String isOp = readExcelController.getCellValue(filePath, "Mapping Details", i, "AM");
				String isLkp = readExcelController.getCellValue(filePath, "Mapping Details", i, "AK");
				String toField = columnName;
				if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
					dataType = "date/time";
				} else if (dataType.contains("number") || dataType.equalsIgnoreCase("number")
						|| dataType.equalsIgnoreCase("numeric")) {
					dataType = "decimal";
				} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
					dataType = "string";
				}
				if (!columnName.startsWith("EFF_TO_DATE")) {
					if (isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N") || isOp.equalsIgnoreCase("O")) {
						iTransformField.setDataType(dataType);
						iTransformField.setDefaultValue("");
						iTransformField.setDescription("");
						iTransformField.setPictureText("");
						/*
						 * if (columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
						 * iTransformField.setName("O_" + columnName);
						 * iTransformField.setExpression("O_" + columnName); }else {
						 */
						iTransformField.setName(columnName);
						iTransformField.setExpression(columnName);
						// }
						if (isOp.equalsIgnoreCase("O")) {
							iTransformField.setPortType("OUTPUT");
							iTransformField.setName("O_" + columnName);
							iTransformField.setExpression("SYSDATE");
							if (columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
								iTransformField.setName(columnName);
								iTransformField.setPortType("INPUT/OUTPUT");
								iTransformField.setExpression(columnName);
							}
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
						con.setFromInstance("EXP_" + stageTableName + "_DEL");
						con.setFromInstanceType("Expression");
						con.setToField(toField);
						con.setToInstance("sc_" + stageTableName + "_DEL");
						con.setToInstanceType("Target Definition");
						conList.add(con);
					}

				}
			}
		}
		transformations.add(iTransformation);
		return transformations;
	}

	public List<TransformField> getTransformFieldRTRTransformation(String filePath, String group) {
		String stg2TableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		// List<String> list = new ArrayList<String>();
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
				if (!(columnName.startsWith("EFF_TO_DATE") || columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD"))) {

					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AB");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AC");
					String isLkp = readExcelController.getCellValue(filePath, "Mapping Details", i, "AK");
					transformField.setPortType("OUTPUT");
					if (group.equalsIgnoreCase("DELETE")) {
						appender = "1";

					} else if (group.equalsIgnoreCase("DEFAULT1")) {
						appender = "2";
					} else {
						appender = "";
						transformField.setPortType("INPUT");
					}
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.contains("number") || dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")) {
						dataType = "decimal";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}
					if (isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N")) {
						transformField.setDataType(dataType);
						transformField.setDefaultValue("");
						transformField.setDescription("");
						transformField.setGroup(group);
						transformField.setName(columnName + appender);
						transformField.setPictureText("");
						precision = precision.replace(".0", "");
						if (precision.matches("[0-9]+")) {
							transformField.setPrecision(Integer.parseInt(precision));
						}
						if (!group.equalsIgnoreCase("INPUT")) {
							transformField.setRefField(columnName);
						}
						if (scale.matches("[0-9]+")) {
							transformField.setScale(Integer.parseInt(scale));
						}
						transformFieldList.add(transformField);
						if (group.equalsIgnoreCase("DELETE")) {
							// if(!(columnName.equalsIgnoreCase("EFF_TO_DATE"))) {
							Connector con = new Connector();
							con.setFromField(transformField.getName());
							con.setFromInstance("RTRTRANS");
							con.setFromInstanceType("Router");
							con.setToField(columnName);
							con.setToInstance("EXP_" + stg2TableName + "_DEL");
							con.setToInstanceType("Expression");
							conList.add(con);
							// }
						}
					}
				}
				if (columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {
					if (group.equalsIgnoreCase("INPUT")) {
						TransformField insTransformField = new TransformField();
						insTransformField.setDataType("string");
						insTransformField.setDefaultValue("");
						insTransformField.setDescription("");
						insTransformField.setGroup(group);
						insTransformField.setName(columnName + appender);
						insTransformField.setPictureText("");
						insTransformField.setPortType("INPUT");
						insTransformField.setPrecision(30);
						insTransformField.setScale(0);
						transformFieldList.add(insTransformField);

					} else {
						TransformField insTransformField = new TransformField();
						insTransformField.setDataType("string");
						insTransformField.setDefaultValue("");
						insTransformField.setDescription("");
						insTransformField.setGroup(group);
						insTransformField.setName(columnName + appender);
						insTransformField.setPictureText("");
						insTransformField.setPortType("OUTPUT");
						insTransformField.setPrecision(30);
						insTransformField.setRefField(columnName);
						insTransformField.setScale(0);
						transformFieldList.add(insTransformField);
						if (group.equalsIgnoreCase("DELETE")) {
							Connector con = new Connector();
							con.setFromField(insTransformField.getName());
							con.setFromInstance("RTRTRANS");
							con.setFromInstanceType("Router");
							con.setToField(columnName);
							con.setToInstance("EXP_" + stg2TableName + "_DEL");
							con.setToInstanceType("Expression");
							conList.add(con);
						}
					}
				}
			}
		}
		return transformFieldList;
	}

	public List<TransformField> getTransformFieldRTRTransformation_LKP(String filePath, String group) {
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		TransformField transformField = null;
		int emptyCount = 0;
		String appender = "";
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "T");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {

				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "U");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "V");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "W");
				String isLop = readExcelController.getCellValue(filePath, "Mapping Details", i, "X");
				if (!isLop.isEmpty()) {
					transformField.setPortType("OUTPUT");
					if (group.equalsIgnoreCase("DEFAULT1")) {
						appender = "2";
					} else if (group.equalsIgnoreCase("DELETE")) {
						appender = "1";
					} else {
						appender = "";
						transformField.setPortType("INPUT");
					}
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.contains("number") || dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")) {
						dataType = "decimal";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}
					columnName = columnName.substring(2);

					transformField.setName("LKP_" + columnName + appender);
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
						transformField.setRefField("LKP_" + columnName);
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

	public List<Instance> getInstances(String filePath) {
		List<Instance> instanceList = new ArrayList<Instance>();
		String sourceStg = readExcelController.getCellValue(filePath, "Session Properties", 43, "I");

		String stg2TableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		String dvTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		for (int i = 58;; i++) {
			String key = readExcelController.getCellValue(filePath, "Session Properties", i, "H");
			if (key != null && !key.isEmpty()) {
				String value = readExcelController.getCellValue(filePath, "Session Properties", i, "I");

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
					instance.setDbName(sourceStg);
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
		String dvTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		String lookupTableName = readExcelController.getCellValue(filePath, "Mapping Details", 24, "T");
		int count = 0;
		String cname = cbuName + "_SHARED";
		for (int i = 58;; i++) {
			if (!readExcelController.getCellValue(filePath, "Session Properties", i, "H").isEmpty()) {
				String key = readExcelController.getCellValue(filePath, "Session Properties", i, "H");
				String value = readExcelController.getCellValue(filePath, "Session Properties", i, "I");
				String sourceStg = readExcelController.getCellValue(filePath, "Session Properties", 43, "I");
				if (value.equalsIgnoreCase("Source Definition")) {
					Shortcut shortcut = new Shortcut("", sourceStg, cname, key, value, "SOURCE", sourceStg, "LOCAL",
							dvTableName, repoName, "1");
					list.add(shortcut);
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
