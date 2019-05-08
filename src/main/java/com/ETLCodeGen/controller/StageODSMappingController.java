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
	public class StageODSMappingController {

	    public List<Connector> conList = new ArrayList<Connector>();
	    
	    @Autowired
	    private ReadExcelController readExcelController;
	    
	    public List<String> transList = new ArrayList<String>();
	    
	    public Source getSourceForSharedFolder(String filePath, String cbuName) {
	        String sourceTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
	        String sourceDBName = readExcelController.getCellValue(filePath, "Session Properties", 43, "O");//cbuName + "_STG"
	        String ownerName = readExcelController.getCellValue(filePath, "Session Properties", 42, "O");//cbuName + "_STAGE"
	        Source source = new Source("", "Oracle", sourceDBName, "", sourceTableName, 1, ownerName, 1);
	        int emptyCount = 0;
	        int count = 0;
	        SourceField sourceField = null;
	        for (int i = 26; ; i++) {
	            if (emptyCount == 10) break;
	            String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
	            
	            if (columnName == null || columnName.isEmpty()) {
	                emptyCount++;
	            } else {
	            	if(!readExcelController.getCellValue(filePath, "Mapping Details", i, "K").isEmpty()) {
	            	String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "K");
		            Integer precision = Integer.parseInt(readExcelController.getCellValue(filePath, "Mapping Details", i, "L").replace(".0", ""));
	                Integer scale = Integer.parseInt(readExcelController.getCellValue(filePath,"Mapping Details",i,"M").replace(".0",""));
		            Integer length = Integer.parseInt(readExcelController.getCellValue(filePath, "Mapping Details", i, "N").replace(".0", ""));
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
	        }
	        return source;
	    }

	    public Target getTargetForSharedFolder(String filePath) {
	        String targetTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
	        Target target = new Target("", "", "Oracle", "", targetTableName, 1, "", 1);
	        int emptyCount = 0;
	        int count = 0;
	        TargetField targetField = null;
	        
	        for (int i = 26; ; i++) {
	            if (emptyCount == 10) break;
	            String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
	            if (columnName == null || columnName.isEmpty()) {
	                emptyCount++;
	            } else {
	                count++;
	                String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
	                Integer precision = Integer.parseInt(readExcelController.getCellValue(filePath, "Mapping Details", i, "AR").replace(".0", ""));
	                Integer scale = Integer.parseInt(readExcelController.getCellValue(filePath,"Mapping Details",i,"AS").replace(".0",""));
	                String prime = readExcelController.getCellValue(filePath, "Mapping Details", i, "AX");
	                String nullable = readExcelController.getCellValue(filePath, "Mapping Details", i, "AW");
	                targetField = new TargetField();
	                targetField.setBusinessName("");
	                targetField.setDataType(dataType);
	                targetField.setDescription("");
	                targetField.setFieldNumber(count);
	                if(prime.equalsIgnoreCase("Y")) {
	                	 targetField.setKeyType("PRIMARY KEY");
	                }else {
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

	    public List<TransformField> getTransformFieldForStoredProcTransformation(String filePath,String storedProcTransName) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			String storedProcName = readExcelController.getCellValue(filePath, "Mapping Details", 77, "BI");
			
			TransformField transformField = null;
			int emptyCount = 0;
			String columnName = "";
			String dataType = "";
			String precision = "";
			String scale = "";
			String toField = "";
			for (int i = 79;; i++) {
				if (emptyCount == 10)
					break;
				transformField = new TransformField();
				columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "BI");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					 dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "BJ");
					 precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "BK");
					 scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "BL");
					 toField = readExcelController.getCellValue(filePath, "Mapping Details", i, "BM");
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")) {
						dataType = "double";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}
					transformField.setDataType(dataType);
					if(!toField.isEmpty()) {
						transformField.setDefaultValue("ERROR(&apos;transformation error&apos;)");
					}else {
						transformField.setDefaultValue("");
					}
					transformField.setDescription("");
					transformField.setName(columnName);
					transformField.setPictureText("");
					if (toField.isEmpty()) {
						transformField.setPortType("INPUT");
					} else {
						transformField.setPortType("OUTPUT");
					}
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						transformField.setPrecision(Integer.parseInt(precision));
					}
					if (scale.matches("[0-9]+")) {
						transformField.setScale(Integer.parseInt(scale));
					}
					transformFieldList.add(transformField);
					if (transformField.getPortType().contains("OUTPUT")) {
						Connector con = new Connector();
						con.setFromField(transformField.getName());
						con.setFromInstance("sc_" + storedProcName);
						con.setFromInstanceType("Stored Procedure");
						con.setToField(toField);
						con.setToInstance("EXP_INS");
						con.setToInstanceType("Expression");
						conList.add(con);
					}else {
					Connector con = new Connector();
					con.setFromField(transformField.getName()+"1");
					con.setFromInstance("RTRTRANS");
					con.setFromInstanceType("Router");
					con.setToField(transformField.getName());
					con.setToInstance("sc_" + storedProcName);
					con.setToInstanceType("Stored Procedure");
					conList.add(con);
					}
				}
					
				}
			return transformFieldList;
		}

		public List<TableAttribute> getTableAttributesForStoredProcTransformation(String filePath, String tableName,
				String connectionInfo) {
			List<TableAttribute> tableAttributeList = new ArrayList<TableAttribute>();
			
			TableAttribute tableAttribute = new TableAttribute();
			tableAttribute.setName("Stored Procedure Name");
			tableAttribute.setValue(tableName);
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Connection Information");
			tableAttribute.setValue(connectionInfo);
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Call Text");
			tableAttribute.setValue("");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Stored Procedure Type");
			tableAttribute.setValue("Normal");
			tableAttributeList.add(tableAttribute);
			
			tableAttribute = new TableAttribute();
			tableAttribute.setName("Execution Order");
			tableAttribute.setValue("1");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Tracing Level");
			tableAttribute.setValue("Normal");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Subsecond Precision");
			tableAttribute.setValue("6");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Output is Repeatable");
			tableAttribute.setValue("Based On Input Order");
			tableAttributeList.add(tableAttribute);

			tableAttribute = new TableAttribute();
			tableAttribute.setName("Output is Deterministic");
			tableAttribute.setValue("NO");
			tableAttributeList.add(tableAttribute);

			
			return tableAttributeList;
		}
		
		public List<TransformField> getTransformFieldForLookupTransformation(String filePath,String rlktableName) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			TransformField transformField = null;
			String rlkTransName = readExcelController.getCellValue(filePath, "Mapping Details", 24, "BL");
			int emptyCount = 0;
			String dataType = "";
			String precision = "";
			String scale = "";
			String columnName = "";
			int j=0;
			List<String> list = new ArrayList<String>();
			if(rlkTransName.equalsIgnoreCase(rlktableName)) {
				j=26;
			}else {
				j=52;
			}
			String isLKP = "";
			for (int i = j;; i++) {
				if (emptyCount == 10)
					break;
				transformField = new TransformField();
				 columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "BL");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					 isLKP = readExcelController.getCellValue(filePath, "Mapping Details", i, "BP");
					 dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "BM");
					 precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "BN");
					 scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "BO");
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("number(p,s)") || dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")) {
						dataType = "double";
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
					if(j==26) {
						if (isLKP.equalsIgnoreCase("Y")) {
							//transformFieldLst.add(transformField);
							Connector con = new Connector();
							con.setFromField(transformField.getName());
							con.setFromInstance("sc_" + rlktableName);
							con.setFromInstanceType("Lookup Procedure");
							con.setToField("RLKP_"+transformField.getName());
							con.setToInstance("EXPVLDN");
							con.setToInstanceType("Expression");
							conList.add(con);
						}
						} else {
						if (isLKP.equalsIgnoreCase("Y") && !columnName.startsWith("I_")) {
							Connector con = new Connector();
							con.setFromField(transformField.getName());
							con.setFromInstance("sc_" + rlktableName);
							con.setFromInstanceType("Lookup Procedure");
							con.setToField("RLKP_"+transformField.getName());
							con.setToInstance("RTRTRANS");
							con.setToInstanceType("Router");
							conList.add(con);
						}
					}
				}
				
			}
			return transformFieldList;
		}

		public List<TableAttribute> getTableAttributesForLookupTransformation(String filePath, String tableName,
				String connectionInfo, String lookupCondition) {
			List<TableAttribute> tableAttributeList = new ArrayList<TableAttribute>();
			
			TableAttribute tableAttribute = new TableAttribute();
			tableAttribute.setName("Lookup Sql Override");
			tableAttribute.setValue("");
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
			String queryOne = "";
			String queryTwo = "";
			String connectionInfo1 = "";
			String firstColumn = "";
			String schema = "";
			if(connectionInfo.contains("HMA")) {
				connectionInfo1 = "HMA_LOOKUP";
				schema = "HMA_ODS";
			}else {
				connectionInfo1 = "KMA_LOOKUP";
				schema = "KMA_ODS";
			}
			String dvName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
			int emptyCount=0;
			for (int i = 52;; i++) {
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "BL");
				if(emptyCount==10){
					break;
				}
				if(!columnName.isEmpty()) {
					if(!columnName.startsWith("I_")) {
					
					query += "A." + columnName + " AS " + columnName + " ,";
				}
				}
				else {
					emptyCount++;
				}
			}
			
			query += "FROM ";
			query = query.replace(",FROM ", "FROM ");
			for(int j = 58;; j++) {
				String proc = readExcelController.getCellValue(filePath, "Session Properties", j, "O");
				if(!proc.isEmpty()) {
					if(proc.equalsIgnoreCase("Lookup Procedure")) {
					String key = readExcelController.getCellValue(filePath, "Session Properties", j, "Q");
					if(key.equalsIgnoreCase(dvName)) {
						queryOne = schema + "." + key;
					}
					else{
						queryTwo =  connectionInfo1 + "." + key;
					}
					}		
					}else
						break;
			}
			query += queryOne + " A , " + queryTwo + " A2 WHERE A.";
						
						for(int k=26;;k++) {
							if(!readExcelController.getCellValue(filePath, "Mapping Details", k, "AP").isEmpty()) {
							String con = readExcelController.getCellValue(filePath, "Mapping Details", k, "AU");
							if(con.equalsIgnoreCase("LKP")) {
								firstColumn = readExcelController.getCellValue(filePath, "Mapping Details", k, "AP");
							}
						}else break;
						}
					query +=	 firstColumn + " = A2." + firstColumn;
					
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
			String sourceName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
			System.out.println("SQ");
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				transformField = new TransformField();
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					if(!readExcelController.getCellValue(filePath, "Mapping Details", i, "K").isEmpty()) {
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "K");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "L");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "M");
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
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					emptyCount = 0;
					if(!readExcelController.getCellValue(filePath, "Mapping Details", i, "K").isEmpty()) {
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

		public List<TransformField> getTransformFieldEXPTransformation1(String filePath) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			TransformField iTransformField = null;
			int emptyCount = 0;
			int recCount = 0;
			System.out.println("exptrans1");
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				iTransformField = new TransformField();
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "J");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					if(!readExcelController.getCellValue(filePath, "Mapping Details", i, "K").isEmpty()) {
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
					String toDate = readExcelController.getCellValue(filePath, "Mapping Details", i, "P");//need to check 
					//String nameChange = readExcelController.getCellValue(filePath, "Mapping Details", i, "O");
					if (toDate.equalsIgnoreCase("D")) {
						iTransformField.setExpression(columnName);
						iTransformField.setDataType("date/time");
					}
					if(columnName.equalsIgnoreCase("REC_CREATE_DATE")) {
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
					//if(nameChange.equalsIgnoreCase("C")) {//need to check
						if(!readExcelController.getCellValue(filePath, "Mapping Details", i, "AP").isEmpty()) {
						Connector con = new Connector();
						con.setFromField(iTransformField.getName());
						con.setFromInstance("EXPTRANS");
						con.setFromInstanceType("Expression");
						con.setToField(readExcelController.getCellValue(filePath, "Mapping Details", i, "AP"));
						con.setToInstance("EXPVLDN");
						con.setToInstanceType("Expression");
						conList.add(con);
						}
					/*}else {
					Connector con = new Connector();
					con.setFromField(iTransformField.getName());
					con.setFromInstance("EXPTRANS");
					con.setFromInstanceType("Expression");
					con.setToField(columnName);
					con.setToInstance("EXPVLDN");
					con.setToInstanceType("Expression");
					conList.add(con);
					}*/
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
				con.setToField(iTransformField.getName().substring(2));
				con.setToInstance("EXPVLDN");
				con.setToInstanceType("Expression");
				conList.add(con);
			}
				emptyCount = 0;
				for (int k = 4;; k++) {
					String transName = readExcelController.getCellValue(filePath, "Session Properties", k, "Q");
					if (emptyCount == 10)
						break;
					iTransformField = new TransformField();
					
					String columnName = readExcelController.getCellValue(filePath, "Session Properties", k, "R");
					if (columnName == null || columnName.isEmpty()) {
						emptyCount++;
					} else {
						if(transName.equalsIgnoreCase("EXPTRANS")) {
							String rlk1Name = "";
							String toField1 = "";
							if(!readExcelController.getCellValue(filePath, "Session Properties", k, "Z").isEmpty()) {
								rlk1Name = readExcelController.getCellValue(filePath, "Session Properties", 3, "Z");
								toField1 = readExcelController.getCellValue(filePath, "Session Properties", k, "Z");
								
							}
							String rlk2Name = "";
							String toField2 = "";
							if(!readExcelController.getCellValue(filePath, "Session Properties", k, "AA").isEmpty()) {
								rlk2Name =	readExcelController.getCellValue(filePath, "Session Properties", 3, "AA");
								toField2 = readExcelController.getCellValue(filePath, "Session Properties", k, "AA");
								
							}
							String rtrName = "";
							String toField = "";
							if(!readExcelController.getCellValue(filePath, "Session Properties", k, "AB").isEmpty()) {
								rtrName = readExcelController.getCellValue(filePath, "Session Properties", 3, "AB");
								toField = readExcelController.getCellValue(filePath, "Session Properties", k, "Z");
								System.out.println("toField:"+toField);
							}
						String dataType = readExcelController.getCellValue(filePath, "Session Properties", k, "S");
						String precision = readExcelController.getCellValue(filePath, "Session Properties", k, "T");
						String scale = readExcelController.getCellValue(filePath, "Session Properties", k, "U");
						String exp = readExcelController.getCellValue(filePath, "Session Properties", k, "Y");
						if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
							dataType = "date/time";
						} else if (dataType.equalsIgnoreCase("number(p,s)")) {
							dataType = "decimal";
						}else if(dataType.equalsIgnoreCase("number")
								|| dataType.equalsIgnoreCase("numeric")){
							dataType = "double";
						} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
							dataType = "string";
						}
						iTransformField.setDataType(dataType);
						iTransformField.setDefaultValue("");
						iTransformField.setDescription("");
						iTransformField.setName(columnName);
						iTransformField.setExpression(exp);
						iTransformField.setExpressionType("GENERAL");
						iTransformField.setPictureText("");
						iTransformField.setPortType("OUTPUT");
						precision = precision.replace(".0", "");
						if (precision.matches("[0-9]+")) {
							iTransformField.setPrecision(Integer.parseInt(precision));
						}
						if (scale.matches("[0-9]+")) {
							iTransformField.setScale(Integer.parseInt(scale));
						}
						transformFieldList.add(iTransformField);
						Connector con1 = new Connector();
						con1.setFromField(iTransformField.getName());
						con1.setFromInstance("EXPTRANS");
						con1.setFromInstanceType("Expression");
						if(!rtrName.isEmpty()) {
						con1.setToField(toField);
						con1.setToInstance(rtrName);
						con1.setToInstanceType("Router");
						}else if(!rlk1Name.isEmpty()) {
							con1.setToField(toField1);
							con1.setToInstance("sc_"+rlk1Name);
							con1.setToInstanceType("Lookup Procedure");
						}else if(!rlk2Name.isEmpty()) {
							con1.setToField(toField2);
							con1.setToInstance("sc_"+rlk2Name);
							con1.setToInstanceType("Lookup Procedure");
						}
						conList.add(con1);
						}
					}
				}
			return transformFieldList;
		}
		
		public List<TransformField> getTransformFieldEXPVLDNTransformation(String filePath) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			TransformField iTransformField = null;
			transList.clear();
			int emptyCount = 0;
			for (int k = 4;; k++) {
				String transName = readExcelController.getCellValue(filePath, "Session Properties", k, "Q");
				if(!transName.isEmpty()) {
						if(transName.equalsIgnoreCase("EXPVLDN")) {
							String rtrName = "";
							String rlk1Name = "";
							String rlk2Name = "";
							iTransformField = new TransformField();
							if(!readExcelController.getCellValue(filePath, "Session Properties", k, "Z").isEmpty()) {
								rlk1Name = readExcelController.getCellValue(filePath, "Session Properties", 3, "Z");
								
							}
							if(!readExcelController.getCellValue(filePath, "Session Properties", k, "AA").isEmpty()) {
								rlk2Name =	readExcelController.getCellValue(filePath, "Session Properties", 3, "AA");
								
							}
							if(!readExcelController.getCellValue(filePath, "Session Properties", k, "AB").isEmpty()) {
								rtrName = readExcelController.getCellValue(filePath, "Session Properties", 3, "AB");
								
							}
							
					String colName = readExcelController.getCellValue(filePath, "Session Properties", k, "R");
					
					String dataType = readExcelController.getCellValue(filePath, "Session Properties", k, "S");
					String precision = readExcelController.getCellValue(filePath, "Session Properties", k, "T");
					String scale = readExcelController.getCellValue(filePath, "Session Properties", k, "U");
					iTransformField.setDataType(dataType);
					iTransformField.setDefaultValue("");
					iTransformField.setDescription("");
					iTransformField.setPictureText("");
					iTransformField.setPortType("OUTPUT");
					iTransformField.setName(colName);
					iTransformField.setExpression(readExcelController.getCellValue(filePath, "Session Properties", k, "Y"));
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						iTransformField.setPrecision(Integer.parseInt(precision));
					}
					if (scale.matches("[0-9]+")) {
						iTransformField.setScale(Integer.parseInt(scale));
					}
					iTransformField.setExpressionType("GENERAL");
					transformFieldList.add(iTransformField);
					transList.add(iTransformField.getName());
					if(!rtrName.isEmpty()) {
						Connector con1 = new Connector();
						con1.setFromField(iTransformField.getName());
						con1.setFromInstance("EXPVLDN");
						con1.setFromInstanceType("Expression");
						con1.setToField(iTransformField.getName().substring(2));
						con1.setToInstance(rtrName);
						con1.setToInstanceType("Router");
						conList.add(con1);
						} 
						if(!rlk1Name.isEmpty()) {
							Connector con1 = new Connector();
							con1.setFromField(iTransformField.getName());
							con1.setFromInstance("EXPVLDN");
							con1.setFromInstanceType("Expression");
							con1.setToField("I_"+iTransformField.getName().substring(2));
							con1.setToInstance("sc_"+rlk1Name);
							con1.setToInstanceType("Lookup Procedure");
							conList.add(con1);
						}
						if(!rlk2Name.isEmpty()) {
							Connector con1 = new Connector();
							con1.setFromField(iTransformField.getName());
							con1.setFromInstance("EXPVLDN");
							con1.setFromInstanceType("Expression");
							con1.setToField("I_"+iTransformField.getName().substring(2));
							con1.setToInstance("sc_"+rlk2Name);
							con1.setToInstanceType("Lookup Procedure");
							conList.add(con1);
						}
				}
			}else break;
			}
			emptyCount = 0;
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				iTransformField = new TransformField();
				String toField="";
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					String isEXPVLDN = readExcelController.getCellValue(filePath, "Mapping Details", i, "AY");
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AR");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AS");
					String comingFrom = readExcelController.getCellValue(filePath, "Mapping Details", i, "AU");
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("number(p,s)")) {
						dataType = "decimal";
					}else if(dataType.equalsIgnoreCase("number")
						|| dataType.equalsIgnoreCase("numeric")){
						dataType = "double";
					}else if (dataType.equalsIgnoreCase("BIT") || dataType.equalsIgnoreCase("CHAR")
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
					iTransformField.setPortType("INPUT/OUTPUT");
					iTransformField.setName(columnName);
					for(String s : transList) {
						if(s.contains(columnName)) {
							iTransformField.setName("");
						}
					}
					toField = columnName;
					if(columnName.equalsIgnoreCase("REC_CREATE_DATE")) {
						toField = "O_" + columnName;
					} 
					iTransformField.setExpression(columnName);
					if(comingFrom.equalsIgnoreCase("LKP")) {
						iTransformField.setName("RLKP_"+columnName);
						iTransformField.setExpression("RLKP_"+columnName);
					}
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						iTransformField.setPrecision(Integer.parseInt(precision));
					}
					if (scale.matches("[0-9]+")) {
						iTransformField.setScale(Integer.parseInt(scale));
					}
					iTransformField.setExpressionType("GENERAL");
					String isLKP = readExcelController.getCellValue(filePath, "Mapping Details", i, "AZ");
					if(!iTransformField.getName().isEmpty() && !isEXPVLDN.equalsIgnoreCase("N")) {
						if(isLKP.equalsIgnoreCase("Y")) {
							toField = "RLKP_" + columnName;
						}
						transformFieldList.add(iTransformField);
						Connector co1 = new Connector();
						co1.setFromField(iTransformField.getName());
						co1.setFromInstance("EXPVLDN");
						co1.setFromInstanceType("Expression");
						co1.setToField(toField);
						co1.setToInstance("RTRTRANS");
						co1.setToInstanceType("Router");
						conList.add(co1);
					}
			}
			}
			return transformFieldList;
		}
		
		public List<TransformField> getTransformFieldRTRTransformation(String filePath, String group) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			TransformField iTransformField = null;
			transList.clear();
			int emptyCount = 0;
			String appender = "";
			String isLKP = "";
			String toField = "";
			String refField = "";
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				iTransformField = new TransformField();
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
						isLKP = readExcelController.getCellValue(filePath, "Mapping Details", i, "AZ");
						String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
						String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AR");
						String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AS");
						transList.add(columnName);
						iTransformField.setPortType("OUTPUT");
						if (group.equalsIgnoreCase("INSERT")) {
							appender = "1";

						} else if (group.equalsIgnoreCase("DEFAULT1")) {
							appender = "2";
						} else if (group.equalsIgnoreCase("UPDATE")) {
							appender = "3";
						} else {
							appender = "";
							iTransformField.setPortType("INPUT");
						}
						if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
							dataType = "date/time";
						} else if (dataType.equalsIgnoreCase("number(p,s)")) {
							dataType = "decimal";
						}else if(dataType.equalsIgnoreCase("number")
								|| dataType.equalsIgnoreCase("numeric")){
							dataType = "double";
						} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
							dataType = "string";
						}
						
						iTransformField.setDataType(dataType);
						iTransformField.setDefaultValue("");
						iTransformField.setDescription("");
						iTransformField.setGroup(group);
						iTransformField.setName(columnName + appender);	
						toField = columnName;
						refField = columnName;
						
						if(columnName.equalsIgnoreCase("REC_CREATE_DATE")) {
							iTransformField.setName("O_"+ columnName + appender);
							refField = "O_" + columnName;
							toField = "O_" + columnName;
						} if(isLKP.equalsIgnoreCase("Y")) {
							iTransformField.setName("RLKP_" + columnName + appender);
							refField = "RLKP_" + columnName;
						}
						
						iTransformField.setPictureText("");
						precision = precision.replace(".0", "");
						if (precision.matches("[0-9]+")) {
							iTransformField.setPrecision(Integer.parseInt(precision));
						}
						if (!group.equalsIgnoreCase("INPUT")) {
							iTransformField.setRefField(refField);
						}
						if (scale.matches("[0-9]+")) {
							iTransformField.setScale(Integer.parseInt(scale));
						}
						
						transformFieldList.add(iTransformField);
						
						if (group.equalsIgnoreCase("UPDATE")) {
								Connector con = new Connector();
								con.setFromField(iTransformField.getName());
								con.setFromInstance("RTRTRANS");
								con.setFromInstanceType("Router");
								con.setToField(toField);
								con.setToInstance("EXP_UPD");
								con.setToInstanceType("Expression");
								conList.add(con);
						}
						if (group.equalsIgnoreCase("INSERT")) {
							if(!columnName.startsWith("O_EVENT")) {
							
							        Connector con = new Connector();
									con.setFromField(iTransformField.getName());
									con.setFromInstance("RTRTRANS");
									con.setFromInstanceType("Router");
									con.setToField(toField);
									con.setToInstance("EXP_INS");
									con.setToInstanceType("Expression");
									conList.add(con);
						} 
						}
						}
				}
			for (int k = 4;; k++) {
				String transName = readExcelController.getCellValue(filePath, "Session Properties", k, "Q");
				if(!transName.isEmpty()) {
				if(transName.equalsIgnoreCase("EXPVLDN")) {
					iTransformField = new TransformField();
					String z =  readExcelController.getCellValue(filePath, "Session Properties", k, "Z");
					String colName = readExcelController.getCellValue(filePath, "Session Properties", k, "R");
					String dataType = readExcelController.getCellValue(filePath, "Session Properties", k, "S");
					String precision = readExcelController.getCellValue(filePath, "Session Properties", k, "T");
					String scale = readExcelController.getCellValue(filePath, "Session Properties", k, "U");
					iTransformField.setPortType("OUTPUT");
					if (group.equalsIgnoreCase("INSERT")) {
						appender = "1";

					} else if (group.equalsIgnoreCase("DEFAULT1")) {
						appender = "2";
					} else if (group.equalsIgnoreCase("UPDATE")) {
						appender = "3";
					} else {
						appender = "";
						iTransformField.setPortType("INPUT");
					}
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("number(p,s)")) {
						dataType = "decimal";
					}else if(dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")){
						dataType = "double";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}
					iTransformField.setGroup(group);
					iTransformField.setDataType(dataType);
					iTransformField.setDefaultValue("");
					iTransformField.setDescription("");
					iTransformField.setPictureText("");
					iTransformField.setName(colName.substring(2) + appender);
						if(z.equalsIgnoreCase("Y")) {
							iTransformField.setName("");
						}
					precision = precision.replace(".0", "");
					if (precision.matches("[0-9]+")) {
						iTransformField.setPrecision(Integer.parseInt(precision));
					}
					if (scale.matches("[0-9]+")) {
						iTransformField.setScale(Integer.parseInt(scale));
					}
					if (!group.equalsIgnoreCase("INPUT")) {
						iTransformField.setRefField(colName.substring(2));
					}
					if(!iTransformField.getName().isEmpty()) {
						transformFieldList.add(iTransformField);
					}
				}
			}else break;
			}
			return transformFieldList;
		}

		public List<TransformField> getTransformFieldEXPINSTransformation(String filePath) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			String dv1Name = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
			TransformField transformField = null;
			int emptyCount = 0;
			transList.clear();
			System.out.println("EXP_INS");
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				transformField = new TransformField();
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AR");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AS");
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("number(p,s)")) {
						dataType = "decimal";
					}else if(dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")){
						dataType = "double";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}
					transformField.setDataType(dataType);
					transformField.setDefaultValue("");
					transformField.setDescription("");
					transformField.setExpression(columnName);
					transformField.setExpressionType("GENERAL");
					transformField.setName(columnName);
					if(columnName.equalsIgnoreCase("REC_CREATE_DATE")) {
						transformField.setName("O_"+ columnName);
						transformField.setExpression("O_"+columnName);
					}
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
					con.setFromInstance("EXP_INS");
					con.setFromInstanceType("Expression");
					con.setToField(columnName);
					con.setToInstance("sc_" + dv1Name + "_INS");
					con.setToInstanceType("Target Definition");
					conList.add(con);
				}
			}
			return transformFieldList;
		}
		
		public List<TransformField> getTransformFieldEXPUPDTransformation(String filePath) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			TransformField transformField = null;
			int emptyCount = 0;
			transList.clear();
			System.out.println("EXP_UPD");
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				transformField = new TransformField();
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AR");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AS");
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("number(p,s)")) {
						dataType = "decimal";
					}else if(dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")){
						dataType = "double";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}
					transformField.setDataType(dataType);
					transformField.setDefaultValue("");
					transformField.setDescription("");
					transformField.setExpression(columnName);
					transformField.setExpressionType("GENERAL");
					transformField.setName(columnName);
					if(columnName.equalsIgnoreCase("REC_CREATE_DATE")) {
						transformField.setName("O_"+ columnName);
						transformField.setExpression("O_"+columnName);
					}
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
					con.setFromInstance("EXP_UPD");
					con.setFromInstanceType("Expression");
					con.setToField(transformField.getName());
					con.setToInstance("UPDTRANS");
					con.setToInstanceType("Update Strategy");
					conList.add(con);
				}
			}
			return transformFieldList;
		}
		
		public List<TransformField> getTransformFieldUPDTransformation(String filePath) {
			List<TransformField> transformFieldList = new ArrayList<TransformField>();
			String dv2Name = readExcelController.getCellValue(filePath, "Mapping Details", 23, "AP");
			TransformField transformField = null;
			int emptyCount = 0;
			System.out.println("UPDTRANS");
			transList.clear();
			for (int i = 26;; i++) {
				if (emptyCount == 10)
					break;
				transformField = new TransformField();
				
				String columnName = readExcelController.getCellValue(filePath, "Mapping Details", i, "AP");
				if (columnName == null || columnName.isEmpty()) {
					emptyCount++;
				} else {
					String dataType = readExcelController.getCellValue(filePath, "Mapping Details", i, "AQ");
					String precision = readExcelController.getCellValue(filePath, "Mapping Details", i, "AR");
					String scale = readExcelController.getCellValue(filePath, "Mapping Details", i, "AS");
					if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("Timestamp")) {
						dataType = "date/time";
					} else if (dataType.equalsIgnoreCase("number(p,s)")) {
						dataType = "decimal";
					}else if(dataType.equalsIgnoreCase("number")
							|| dataType.equalsIgnoreCase("numeric")){
						dataType = "double";
					} else if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("varchar2")) {
						dataType = "string";
					}
					transformField.setDataType(dataType);
					transformField.setDefaultValue("");
					transformField.setDescription("");
					transformField.setName(columnName);
					if(columnName.equalsIgnoreCase("REC_CREATE_DATE")) {
						transformField.setName("O_"+ columnName);
					}
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
					con.setFromInstance("UPDTRANS");
					con.setFromInstanceType("Update Strategy");
					con.setToField(columnName);
					con.setToInstance("sc_" + dv2Name + "_UPD");
					con.setToInstanceType("Target Definition");
					conList.add(con);
				}
			}
			return transformFieldList;
		}
		
		public List<Instance> getInstances(String filePath, String cbuName) {
			List<Instance> instanceList = new ArrayList<Instance>();
			String sourceDBName = readExcelController.getCellValue(filePath, "Session Properties", 43, "O");
			String sourceTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
			String connectionInfo = cbuName + "_ODS";
			for (int i = 58;; i++) {
				String key = readExcelController.getCellValue(filePath, "Session Properties", i, "N");
				String query = readExcelController.getCellValue(filePath, "Session Properties", i, "P");
				String tName = readExcelController.getCellValue(filePath, "Session Properties", i, "Q");
				if (key != null && !key.isEmpty()) {
					String value = readExcelController.getCellValue(filePath, "Session Properties", i, "O");
					if (value.equalsIgnoreCase("Target Definition")) {
						Instance instance = new Instance();
						instance.setDescription("");
						instance.setName("sc_" + key + "_INS");
						instance.setTransformationName("sc_" + key);
						instance.setTransformationType(value);
						instance.setType("TARGET");
						instanceList.add(instance);
						Instance instance1 = new Instance();
						instance1.setDescription("");
						instance1.setName("sc_" + key + "_UPD");
						instance1.setTransformationName("sc_" + key);
						instance1.setTransformationType(value);
						instance1.setType("TARGET");
						instanceList.add(instance1);
					} else if (value.equalsIgnoreCase("Source Definition")) {
						Instance instance = new Instance();
						instance.setDbName(sourceDBName);
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
					} else if (value.equalsIgnoreCase("Lookup Procedure") || value.equalsIgnoreCase("Stored Procedure")) {
						Instance instance = new Instance();
						instance.setDescription("");
						instance.setName("sc_" + key);
						instance.setReusable("YES");
						instance.setTransformationName("sc_" + key);
						instance.setTransformationType(value);
						instance.setType("TRANSFORMATION");
						if(query.equalsIgnoreCase("Y")) {
							TableAttribute tAttribute = new TableAttribute();
							tAttribute.setName("Lookup Sql Override");
							tAttribute.setValue(getSQLQueryForLookupTransformation(filePath, tName, connectionInfo));
							instance.addTableAttribute(tAttribute);
						}
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
			String sourceTableName = readExcelController.getCellValue(filePath, "Mapping Details", 23, "J");
			String sourceDBName = readExcelController.getCellValue(filePath, "Session Properties", 43, "O");
			String cname = cbuName + "_SHARED";
			for (int i = 58;; i++) {
				if (!readExcelController.getCellValue(filePath, "Session Properties", i, "N").isEmpty()) {
					String key = readExcelController.getCellValue(filePath, "Session Properties", i, "N");
					String value = readExcelController.getCellValue(filePath, "Session Properties", i, "O");
					if (value.equalsIgnoreCase("Source Definition")) {
						Shortcut shortcut = new Shortcut("", sourceDBName, cname, key, value, "SOURCE", sourceDBName, "LOCAL",
								sourceTableName, repoName, "1");
						list.add(shortcut);
					}
					if (value.equalsIgnoreCase("Target Definition")) {
						Shortcut shortcut2 = new Shortcut("", cname, "sc_"+key, value, "TARGET", "LOCAL", key, repoName,
								"1");
						list.add(shortcut2);
					}
					if (value.equalsIgnoreCase("Lookup Procedure")) {
						Shortcut shortcut2 = new Shortcut("", cname, "sc_"+key, value, "TRANSFORMATION", "LOCAL", key,
								repoName, "1");

						list.add(shortcut2);
					}
					if (value.equalsIgnoreCase("Stored Procedure")) {
						Shortcut shortcut2 = null;
						if(key.equalsIgnoreCase("RPKG_CVA_GET_SEQUENCE")) {
							 shortcut2 = new Shortcut("", cname, "sc_"+key, value, "TRANSFORMATION", "LOCAL", "HMA_PKG_PRC_GET_SEQUENCE",
									repoName, "1");
					}
					else {
						 shortcut2 = new Shortcut("", cname, "sc_"+key, value, "TRANSFORMATION", "LOCAL", key,
								repoName, "1");
						}
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
