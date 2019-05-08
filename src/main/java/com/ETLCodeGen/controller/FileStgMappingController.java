package com.ETLCodeGen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ETLCodeGen.model.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("mapping/")
public class FileStgMappingController {

	public List<Connector> conList = new ArrayList<Connector>();

	@Autowired
	private ReadExcelController readExcelController;

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
		tableAttribute.setName("Datetime Format");
		tableAttribute.setValue("A  19 mm/dd/yyyy hh24:mi:ss");
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
		tableAttribute.setName("Add Currently Processed Flat File Name Port");
		tableAttribute.setValue("NO");
		tableAttributeList.add(tableAttribute);

		return tableAttributeList;
	}

	public Source getSourceForSharedFolder(String filePath, String cbuName) {
		String sourceTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "B");
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
		source.setAttributeList(getAttributesForSource(filePath));
		int emptyCount = 0;
		int count = 0;
		SourceField sourceField = null;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "B");

			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "C");
				Integer precision = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "D").replace(".0", ""));
				Integer scale = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "E").replace(".0", ""));
				Integer length = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "F").replace(".0", ""));
				String description = readExcelController.getCellValue(filePath, "Mapping Details", i, "H");
				Integer cumulativeLength = 0;
				count++;
				sourceField = new SourceField();
				sourceField.setBusinessName("");
				sourceField.setDescription(description);
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
				sourceField.setNullable("NOTNULL");
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

	public Target getTargetStgForSharedFolder(String filePath) {
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
		return target;
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
		int emptyCount = 0;
		System.out.println("SQ");
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String sourceName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "B");
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "B");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "C");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "D");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "E");
				String description = readExcelController.getCellValue(filePath, "Mapping Details", i, "H");
				if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp") || dataType.equalsIgnoreCase("DATS") || dataType.equalsIgnoreCase("TIMS")) {
					dataType = "date/time";
				} else if (dataType.equalsIgnoreCase("NUMC") || dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("DEC")
						|| dataType.equalsIgnoreCase("number") || dataType.equalsIgnoreCase("numeric")) {
					dataType = "decimal";
				} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("BIT")
						|| dataType.equalsIgnoreCase("varchar2") || dataType.equalsIgnoreCase("CLNT")) {
					dataType = "string";
				} else if (dataType.equalsIgnoreCase("NVARCHAR") || dataType.equalsIgnoreCase("NCHAR") || dataType.equalsIgnoreCase("LANG")) {
					dataType = "nstring";
				} else if (dataType.equalsIgnoreCase("Float")) {
					dataType = "double";
				}else if (dataType.equalsIgnoreCase("int") || dataType.equalsIgnoreCase("int4")) {
					dataType = "integer";
				}
				transformField.setDataType(dataType);
				transformField.setDefaultValue("");
				transformField.setDescription(description);
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
				con.setFromInstance("sc_" + sourceName);
				con.setFromInstanceType("Source Definition");
				con.setToField(transformField.getName());
				con.setToInstance("SQ_sc_" + sourceName);
				con.setToInstanceType("Source Qualifier");
				conList.add(con);
				Connector con1 = new Connector();
				con1.setFromField(transformField.getName());
				con1.setFromInstance("SQ_sc_" + sourceName);
				con1.setFromInstanceType("Source Qualifier");
				con1.setToField(transformField.getName());
				con1.setToInstance("EXPTRANS");
				con1.setToInstanceType("Expression");
				conList.add(con1);

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
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "B");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				emptyCount = 0;
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "C");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "D");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "E");
				String description = readExcelController.getCellValue(filePath, "Mapping Details", i, "H");
				if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp") || dataType.equalsIgnoreCase("DATS") || dataType.equalsIgnoreCase("TIMS")) {
					dataType = "date/time";
				} else if (dataType.equalsIgnoreCase("integer") || dataType.equalsIgnoreCase("int")
						|| dataType.equalsIgnoreCase("NUMC") || dataType.equalsIgnoreCase("number(p,s)")
						|| dataType.equalsIgnoreCase("number") || dataType.equalsIgnoreCase("numeric") || dataType.equalsIgnoreCase("DEC")) {
					dataType = "decimal";
				} else if (dataType.equalsIgnoreCase("BIT") || dataType.equalsIgnoreCase("LANG")
						|| dataType.equalsIgnoreCase("varchar2") || dataType.equalsIgnoreCase("CLNT")) {
					dataType = "string";
				} else if (dataType.equalsIgnoreCase("NVARCHAR") || dataType.equalsIgnoreCase("NCHAR") || dataType.equalsIgnoreCase("CHAR")
						|| dataType.equalsIgnoreCase("NTEXT")) {
					dataType = "string";
				} else if (dataType.equalsIgnoreCase("Float")) {
					dataType = "double";
				}
				transformField.setDataType(dataType);
				transformField.setDefaultValue("");
				transformField.setDescription(description);
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
				
			}
		}
		return transformFieldList;
	}

	public List<TransformField> getTransformFieldEXPTransformation1(String filePath) {
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		TransformField iTransformField = null;
		int emptyCount = 0;
		String type = readExcelController.getCellValue(filePath, "Mapping Details", 4, "G");
		String stgTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			iTransformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
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
				if(dataType.equalsIgnoreCase("String")) {
					iTransformField.setExpression("LTRIM(RTRIM(UPPER(" + columnName + ")))");
				} else {
					iTransformField.setExpression(columnName);
				}
				String toDate = readExcelController.getCellValue(filePath, "Mapping Details", i, "P");
				String toDate1 = readExcelController.getCellValue(filePath, "Mapping Details", i, "C");
				if (toDate.equalsIgnoreCase("D")) {
					// iTransformField.setExpression("IIF(TO_CHAR(" + columnName +
					// ")=&apos;0&apos;,NULL,TO_DATE(TO_CHAR(" + columnName +
					// "),&apos;YYYYMMDD&apos;))");
					iTransformField.setExpression("TO_DATE(TO_CHAR(" + columnName + "),'YYYYMMDD')");
					iTransformField.setDefaultValue("ERROR(&apos;transformation error&apos;)");
					if (toDate1.equalsIgnoreCase("String") && type.equalsIgnoreCase("Type 1")) {
						// iTransformField.setExpression("IIF(TO_CHAR(" + columnName +
						// ")=&apos;0&apos;,NULL,TO_DATE(TO_CHAR(" + columnName +
						// "),&apos;YYYYMMDD&apos;))");
						iTransformField.setExpression("TO_DATE(" + columnName + ",'YYYY-MM-DD HH24:MI:SS')");
						iTransformField.setDefaultValue("ERROR(&apos;transformation error&apos;)");
					}
				}
				
				if (columnName.startsWith("REC_")) {
					iTransformField.setExpression("SYSDATE");
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
				con.setToInstance("sc_" + stgTableName);
				con.setToInstanceType("Target Definition");
				conList.add(con);
			}
		}
		return transformFieldList;
	}

	public List<Instance> getInstances(String filePath) {
		List<Instance> instanceList = new ArrayList<Instance>();
		String sourceName = readExcelController.getCellValue(filePath, "Mapping Details", 2, "G");
		String sourceTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "B");
		String stgTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
		for (int i = 58;; i++) {
			String key = readExcelController.getCellValue(filePath, "Session Properties", i, "B");
			if (key != null && !key.isEmpty()) {
				String value = readExcelController.getCellValue(filePath, "Session Properties", i, "C");

				if (value.equalsIgnoreCase("Target Definition")) {
					Instance instance = new Instance();
					instance.setDescription("");
					instance.setName(key);
					instance.setTransformationName("sc_" + stgTableName);
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
				} else if (value.equalsIgnoreCase("Source Qualifier")) {
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
		String stgTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
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
					Shortcut shortcut2 = new Shortcut("", cname, key, value, "TARGET", "LOCAL", stgTableName, repoName,
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
