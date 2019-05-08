package com.ETLCodeGen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ETLCodeGen.model.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("mapping/")
public class StgTwoProdController {

	public List<Connector> conList = new ArrayList<Connector>();

	@Autowired
	private ReadExcelController readExcelController;

	public Source getSourceForSharedFolder(String filePath, String cbuName) {
		String sourceTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		Source source = new Source("", "Oracle", cbuName + "_STG", "", sourceTableName, 1, cbuName + "_STAGE", 1);
		int emptyCount = 0;
		int count = 0;
		SourceField sourceField = null;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "Z");

			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
				Integer precision = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "AB").replace(".0", ""));
				Integer scale = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "AC").replace(".0", ""));
				Integer length = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "AD").replace(".0", ""));
				String nullable = readExcelController.getCellValue(filePath, "Mapping Details", i, "AI");
				String prime = readExcelController.getCellValue(filePath, "Mapping Details", i, "AH");
				Integer cumulativeLength = 0;
				count++;
				sourceField = new SourceField();
				sourceField.setBusinessName("");
				sourceField.setDescription("");
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

	public Target getTargetPrdForSharedFolder(String filePath) {
		String targetTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		Target target = new Target("", "", "Oracle", "", targetTableName, 1, "", 1);
		int emptyCount = 0;
		int count = 0;
		TargetField targetField = null;

		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				count++;
				targetField = new TargetField();
				targetField.setBusinessName("");
				targetField.setDescription("");
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
				Integer precision = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "AR").replace(".0", ""));
				Integer scale = Integer.parseInt(
						readExcelController.getCellValue(filePath, "Mapping Details", i, "AS").replace(".0", ""));
				String prime = readExcelController.getCellValue(filePath, "Mapping Details", i, "AX");
				String nullable = readExcelController.getCellValue(filePath, "Mapping Details", i, "AW");
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
			String stgName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "Z");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
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
				con.setFromInstance("sc_" + stgName);
				con.setFromInstanceType("Source Definition");
				con.setToField(transformField.getName());
				con.setToInstance("SQ_sc_" + stgName);
				con.setToInstanceType("Source Qualifier");
				conList.add(con);
				Connector con1 = new Connector();
				con1.setFromField(transformField.getName());
				con1.setFromInstance("SQ_sc_" + stgName);
				con1.setFromInstanceType("Source Qualifier");
				con1.setToField(transformField.getName());
				con1.setToInstance("EXPTRANS");
				con1.setToInstanceType("Expression");
				conList.add(con1);

			}
		}
		return transformFieldList;
	}

	public List<TableAttribute> getTableAttributesForUpdTransformation(String filePath) {
		List<TableAttribute> tableAttributeList = new ArrayList<TableAttribute>();

		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Update Strategy Expression");
		tableAttribute.setValue("DD_UPDATE");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Forward Rejected Rows");
		tableAttribute.setValue("YES");
		tableAttributeList.add(tableAttribute);

		tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");
		tableAttributeList.add(tableAttribute);

		return tableAttributeList;

	}

	public List<TransformField> getupdTransformation(String filePath) {
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		String prodTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		TransformField transformField = null;
		System.out.println("updTrans");
		int emptyCount = 0;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			transformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
			String toField = columnName;
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else {
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AR");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AS");
				String update = readExcelController.getCellValue(filePath, "Mapping Details", i, "AY");
				String output = readExcelController.getCellValue(filePath, "Mapping Details", i, "AZ");
				if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
					dataType = "date/time";
				} else if (dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("number")
						|| dataType.equalsIgnoreCase("numeric")) {
					dataType = "decimal";
				} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
					dataType = "string";
				}
				if (update.equalsIgnoreCase("P") || update.equalsIgnoreCase("N") || output.equalsIgnoreCase("O")) {
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
					if (output.equalsIgnoreCase("O")) {
						toField = "O_" + columnName;
					}
					// list.add(transformField.getName());
					transformFieldList.add(transformField);
					if (!output.equalsIgnoreCase("O")) {
						Connector con = new Connector();
						con.setFromField(columnName + "3");
						con.setFromInstance("RTRTRANS");
						con.setFromInstanceType("Router");
						con.setToField(toField);
						con.setToInstance("EXP_" + prodTableName + "_UPD_DEL");
						con.setToInstanceType("Expression");
						conList.add(con);
					}
					Connector con1 = new Connector();
					con1.setFromField(toField);
					con1.setFromInstance("EXP_" + prodTableName + "_UPD_DEL");
					con1.setFromInstanceType("Expression");
					con1.setToField(transformField.getName());
					con1.setToInstance("UPDTRANS_UPD");
					con1.setToInstanceType("Update Strategy");
					conList.add(con1);

					Connector con2 = new Connector();
					con2.setFromField(transformField.getName());
					con2.setFromInstance("UPDTRANS_UPD");
					con2.setFromInstanceType("Update Strategy");
					con2.setToField(columnName);
					con2.setToInstance("sc_" + prodTableName + "_UPD_DEL");
					con2.setToInstanceType("Target Definition");
					conList.add(con2);

				}
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
				emptyCount = 0;
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AB");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AC");
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
		}
		return transformFieldList;
	}

	public List<Transformation> getIUDTransformation(String filePath) {
		List<Transformation> transformations = new ArrayList<Transformation>();
		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");

		String prodTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		Transformation iTransformation = new Transformation();
		iTransformation.setName("EXP_" + prodTableName + "_INS_REN");
		iTransformation.setDescription("");
		iTransformation.setObjectVersion("1");
		iTransformation.setVersionNumber("1");
		iTransformation.setReUsable("NO");
		iTransformation.setType("Expression");
		iTransformation.addTableAttribute(tableAttribute);

		Transformation uTransformation = new Transformation();
		uTransformation.setName("EXP_" + prodTableName + "_UPD_DEL");
		uTransformation.setDescription("");
		uTransformation.setObjectVersion("1");
		uTransformation.setVersionNumber("1");
		uTransformation.setReUsable("NO");
		uTransformation.setType("Expression");
		uTransformation.addTableAttribute(tableAttribute);

		int emptyCount = 0;
		for (int i = 26;; i++) {
			if (emptyCount == 10)
				break;
			TransformField iTransformField = new TransformField();
			TransformField uTransformField = new TransformField();
			String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "Z");
			if (columnName == null || columnName.isEmpty()) {
				emptyCount++;
			} else if (!columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD")) {

				String isLkp = readExcelController.getCellValue(filePath, "Mapping Details", i, "AK");
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AB");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AC");
				if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
					dataType = "date/time";
				} else if (dataType.contains("number") || dataType.equalsIgnoreCase("number")
						|| dataType.equalsIgnoreCase("numeric")) {
					dataType = "decimal";
				} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
					dataType = "string";
				}
				iTransformField.setDataType(dataType);
				iTransformField.setDefaultValue("");
				iTransformField.setDescription("");
				iTransformField.setPictureText("");
				iTransformField.setPortType("INPUT/OUTPUT");
				iTransformField.setName(columnName);
				iTransformField.setExpression(columnName);
				if (columnName.equalsIgnoreCase("EFF_TO_DATE")) {
					iTransformField.setName("O_" + columnName);
					iTransformField.setExpression("TO_DATE(&apos;12/31/9999&apos;, &apos;MM/DD/YYYY&apos;)");
					iTransformField.setPortType("OUTPUT");
				}
				if (columnName.equalsIgnoreCase("EFF_FROM_DATE")) {
					iTransformField.setName("O_" + columnName);
					iTransformField.setExpression("TO_DATE($$EFF_DATE, &apos;MM/DD/YYYY HH24:MI:SS&apos;)");
					iTransformField.setPortType("OUTPUT");
				}
				if (columnName.startsWith("REC_")) {
					iTransformField.setPortType("OUTPUT");
					iTransformField.setName("O_" + columnName);
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
				iTransformation.addTransformField(iTransformField);
				Connector con = new Connector();
				con.setFromField(iTransformField.getName());
				con.setFromInstance("EXP_" + prodTableName + "_INS_REN");
				con.setFromInstanceType("Expression");
				con.setToField(columnName);
				con.setToInstance("sc_" + prodTableName + "_INS_REN");
				con.setToInstanceType("Target Definition");
				conList.add(con);
				if (isLkp.equalsIgnoreCase("P") || isLkp.equalsIgnoreCase("N")
						|| columnName.equalsIgnoreCase("REC_UPDATE_DATE")) {
					uTransformField.setDataType(dataType);
					uTransformField.setDefaultValue("");
					uTransformField.setDescription("");
					uTransformField.setPictureText("");
					uTransformField.setName(columnName);
					uTransformField.setPortType("INPUT/OUTPUT");
					uTransformField.setExpression(columnName);
					if (columnName.equalsIgnoreCase("REC_UPDATE_DATE")) {
						uTransformField.setPortType("OUTPUT");
						uTransformField.setName("O_" + columnName);
						uTransformField.setExpression("SYSDATE");
					}
					if (columnName.equalsIgnoreCase("EFF_TO_DATE")) {
						uTransformField.setPortType("OUTPUT");
						uTransformField.setName("O_" + columnName);
						uTransformField.setExpression("TO_DATE($$EFF_DATE, &apos;MM/DD/YYYY HH24:MI:SS&apos;)");
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
				}
			}
		}
		transformations.add(iTransformation);
		transformations.add(uTransformation);
		return transformations;
	}

	public List<TransformField> getTransformFieldRTRTransformation(String filePath, String group) {
		String dvTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		// String stgTableName = readExcelController.getCellValue(filePath, "Mapping
		// Details", 23, "J");
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
				String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AA");
				String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AB");
				String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AC");
				String toField = columnName;
				transformField.setPortType("OUTPUT");
				if (group.equalsIgnoreCase("INSERT")) {
					appender = "1";

				} else if (group.equalsIgnoreCase("DEFAULT1")) {
					appender = "2";
				} else if (group.equalsIgnoreCase("UPDATE")) {
					appender = "3";
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
					transformField.setRefField(toField);
				}
				if (scale.matches("[0-9]+")) {
					transformField.setScale(Integer.parseInt(scale));
				}
				transformFieldList.add(transformField);

				if (group.equalsIgnoreCase("INSERT")) {
					if (!(columnName.startsWith("REC_") || columnName.startsWith("EFF_")
							|| columnName.equalsIgnoreCase("ETL_TRANSACTION_TYPE_CD"))) {
						Connector con2 = new Connector();
						con2.setFromField(transformField.getName());
						con2.setFromInstance("RTRTRANS");
						con2.setFromInstanceType("Router");
						con2.setToField(columnName);
						con2.setToInstance("EXP_" + dvTableName + "_INS_REN");
						con2.setToInstanceType("Expression");
						conList.add(con2);
					}
				}
			}
		}
		return transformFieldList;
	}

	public List<Instance> getInstances(String filePath) {
		List<Instance> instanceList = new ArrayList<Instance>();
		String sourceStg = readExcelController.getCellValue(filePath, "Session Properties", 43, "L");
		String stg2TableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		String dvTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		for (int i = 58;; i++) {
			String key = readExcelController.getCellValue(filePath, "Session Properties", i, "K");
			if (key != null && !key.isEmpty()) {
				String value = readExcelController.getCellValue(filePath, "Session Properties", i, "L");

				if (value.equalsIgnoreCase("Target Definition")) {
					Instance instance = new Instance();
					instance.setDescription("");
					instance.setName(key);
					instance.setTransformationName("sc_" + dvTableName);
					instance.setTransformationType(value);
					instance.setType("TARGET");
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
					AssociatedSourceInstance as1 = new AssociatedSourceInstance();
					as1.setName("sc_" + stg2TableName);
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
		String stg2TableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "Z");
		String dvTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
		String cname = cbuName + "_SHARED";
		int count = 0;
		for (int i = 58;; i++) {
			if (!readExcelController.getCellValue(filePath, "Session Properties", i, "K").isEmpty()) {
				String key = readExcelController.getCellValue(filePath, "Session Properties", i, "K");
				String value = readExcelController.getCellValue(filePath, "Session Properties", i, "L");
				String sourceStg = readExcelController.getCellValue(filePath, "Session Properties", 43, "L");
				if (value.equalsIgnoreCase("Source Definition")) {
					Shortcut shortcut = new Shortcut("", sourceStg, cname, key, value, "SOURCE", sourceStg, "LOCAL",
							stg2TableName, repoName, "1");
					list.add(shortcut);
				}
				if (value.equalsIgnoreCase("Target Definition")) {
					if (count >= 1)
						break;
					count++;
					String vName = "sc_" + dvTableName;
					Shortcut shortcut2 = new Shortcut("", cname, vName, value, "TARGET", "LOCAL", dvTableName, repoName,
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
