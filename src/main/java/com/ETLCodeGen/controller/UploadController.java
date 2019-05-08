package com.ETLCodeGen.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ETLCodeGen.model.DBPojo;
import com.ETLCodeGen.model.ErpInfo;
import com.ETLCodeGen.model.Folder;
import com.ETLCodeGen.model.Group;
import com.ETLCodeGen.model.Mapping;
import com.ETLCodeGen.model.MappingVariable;
import com.ETLCodeGen.model.PowerMart;
import com.ETLCodeGen.model.Repository;
import com.ETLCodeGen.model.SharedEnum;
import com.ETLCodeGen.model.Source;
import com.ETLCodeGen.model.TableAttribute;
import com.ETLCodeGen.model.TargetLoader;
import com.ETLCodeGen.model.TransformField;
import com.ETLCodeGen.model.Transformation;
import com.ETLCodeGen.model.UploadedFile;
import com.ETLCodeGen.validator.FileValidator;
import com.ETLCodeGen.validator.JaxbCharacterEscapeHandler;
import com.ETLCodeGen.validator.SourceType;
import com.ETLCodeGen.validator.TypeEnum;
import com.ETLCodeGen.validator.UploadForm;
import com.sun.xml.internal.bind.marshaller.DataWriter;

@Controller
public class UploadController {

	private static final Logger logger = Logger.getLogger(UploadController.class);

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	String outputPath = "C:/OutputFiles/";
	
	@Autowired
	XmlGeneratorController xmlGeneratorController;

	@Autowired
	SourceStageMappingController sourceStgController;

	@Autowired
	FileStgMappingController fileStgController;
	
	@Autowired
	XmlStageMappingController xmlStageController;
	
	@Autowired
	StageODSMappingController stageODSController;

	@Autowired
	ReadExcelController readExcelController;

	@Autowired
	InsUpdMappingController insUpdMappingController;

	@Autowired
	StgTwoProdController stgTwoProdController;

	@Autowired
	DeleteMappingController deleteMappingController;
	
	@Autowired
	WorkLetController workletController;

	@Autowired
	FileValidator fileValidator;

	@Resource(name = "excelinfoProperties")
	private Properties excelinfoProperties;
	
	@RequestMapping("/fileUploadForm")
	public ModelAndView getUploadForm(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result) {
		return new ModelAndView("uploadForm");
	}
	
	@RequestMapping(value="uploadFiles",method=RequestMethod.POST)
	@ResponseBody
    public ModelAndView uploadFiles(@RequestBody UploadForm uploadForm){
			//redirectTest();
		    OutputStream os = null;
		    File excelFile = null;
		    File xsdFile = null;
		    File xmlFile = null;
		    String xmlpath = null;
		    String filepath = null;
		    String xsdpath = null;
	        System.out.println("UploadFiles");
	        DateFormat dateFormat = new SimpleDateFormat("MMddyyyyHHmmss");
			Date date = new Date();
		try {
			
			if (uploadForm.getExcelFileName() != null) {

				excelFile = new File(outputPath + dateFormat.format(date) + "_" + uploadForm.getExcelFileName());
				os = new FileOutputStream(excelFile);
				// Starts writing the bytes in it
				os.write(uploadForm.getExcelFile());
				os.close();
			}

			if (uploadForm.getXmlFileName() != null) {
				xsdFile = new File(outputPath + dateFormat.format(date) + "_" + uploadForm.getXmlFileName());
				os = new FileOutputStream(xsdFile);
				// Starts writing the bytes in it
				os.write(uploadForm.getXmlFile());
				os.close();
			}
			if (uploadForm.getXmlFileName() != null) {
				xmlFile = new File(outputPath + dateFormat.format(date) + "_" + uploadForm.getXmlFileName());
				os = new FileOutputStream(xmlFile);
				// Starts writing the bytes in it
				os.write(uploadForm.getXmlFile());
				os.close();
			}
		} catch (IOException e) {
			logger.error("Error at fileUploaded() : " + e.getMessage());
		}
		filepath = excelFile.getAbsolutePath();
		if (xsdFile != null) {
			xsdpath = xsdFile.getAbsolutePath();
		} if( xmlFile !=null) {
			xmlpath = xmlFile.getAbsolutePath();
		}
	            boolean tableCreationSuccessful = false;
	    		String cbuName = "";
	    		String typeName = readExcelController.getCellValue(filepath, "Mapping Details", 4, "G");
	    		String folderName = readExcelController.getCellValue(filepath, "Mapping Details", 1, "D");
	    		if (folderName.startsWith("KMA")) {
	    			cbuName = "KMA";
	    		} else {
	    			cbuName = "HMA";
	    		}
	    		try {
	    			//System.out.println("path of properties:"+excelinfoProperties.keySet());
	    			CreateTable createTable = new CreateTable();
	    			createTable.setExcelinfoProperties(excelinfoProperties);
	    			tableCreationSuccessful = createTable.checkTablesDataFromExcel(filepath, cbuName);
	    		} catch (Exception e1) {
	    			logger.info("exception is.." + e1.getMessage());
	    			e1.printStackTrace();
	    		}
	    		if (tableCreationSuccessful) {
	    			try {
	    				String sourceType = readExcelController.getCellValue(filepath, "Mapping Details", 3, "G");
	    				if(typeName.equalsIgnoreCase("Type 2")) {
	    				if (sourceType.equalsIgnoreCase("Flat File")) {
	    					fileStgController.resetConList();
	    					generateFileStgMapping(filepath);
	    					xmlGeneratorController.generateSourceFileToStage1XML(filepath, outputPath);
	    					workletController.generateSourceFileToStage1Worklet(filepath, outputPath);
	    				} else {
	    					sourceStgController.resetConList();
	    					generateSourceStgMapping(filepath);
	    					xmlGeneratorController.generateSourceToStage1XML(filepath, outputPath);
	    					workletController.generateSourceToStage1Worklet(filepath, outputPath);
	    				}
	    				xmlGeneratorController.generateStage1ToStage2XML(filepath, outputPath);
	    				xmlGeneratorController.generateDeleteXML(filepath, outputPath);
	    				xmlGeneratorController.generateStage2ToProdXML(filepath, outputPath);
	    				
	    				insUpdMappingController.resetConList();
	    				generateInsUpdXML(filepath);

	    				stgTwoProdController.resetConList();
	    				generateStgProdMapping(filepath);

	    				deleteMappingController.resetConList();
	    				generateDeleteMapping(filepath);
	    				
	    				workletController.generateProdWorklet(filepath, outputPath);
	    				}
	    				else {
	    					if (sourceType.equalsIgnoreCase("Flat File")) {
	    						stageODSController.resetConList();
	    						generateStageODSMapping(filepath);
	    						fileStgController.resetConList();
		    					generateFileStgMapping(filepath);
		    					xmlGeneratorController.generateSourceFileToStage1XML(filepath, outputPath);
		    					workletController.generateSourceFileToStage1Worklet(filepath, outputPath);
		    					
	    					} else if(sourceType.equalsIgnoreCase("XML")){
	    						
	    						xmlStageController.resetConList();
	    						generateXMLFileStageMapping(filepath, xmlpath);
	    						//stageODSController.resetConList();
	    						//generateStageODSMapping(filepath);
	    						//xmlGeneratorController.generateXmlFileToODSXML(filepath, outputPath);
	    						//workletController.generateXmlFileToODSWorklet(filepath, outputPath);
	    					} else {
	    						stageODSController.resetConList();
	    						generateStageODSMapping(filepath);
	    						sourceStgController.resetConList();
		    					generateSourceStgMapping(filepath);
		    					xmlGeneratorController.generateSourceToStage1XML(filepath, outputPath);
		    					workletController.generateSourceToStage1Worklet(filepath, outputPath);
	    						
	    					}
	    					xmlGeneratorController.generateStageToODSXML(filepath, outputPath);
    						workletController.generateStageToODSWorklet(filepath, outputPath);
	    				}
	    			
	    			} catch (Exception e) {
	    				logger.info("inside xml conversion successful..." + e.getMessage());
	    				e.printStackTrace();
	    				return new ModelAndView("errorFile", "message", e.getMessage());
	    			}
	    		} else {
	    			logger.info("table creation failed message..");
	    			return new ModelAndView("errorFile", "message", "Table Creation failed. Please check logs.");
	    		}
	    		return new ModelAndView("showFile", "message", uploadForm.getExcelFileName() + "  " + "converted successfully !");
	    	}

	public static boolean isCellEmpty(final HSSFCell cell) {
		if (cell == null) {
			return true;
		}

		if (cell.getStringCellValue().isEmpty()) {
			return true;
		}

		return false;
	}

	public void generateSourceStgMapping(String filePath) throws JAXBException {

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String sheetName = "Mapping Details";
		String cbuName = "";
		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, sheetName, 1, "D");
		String repoName = readExcelController.getCellValue(filePath, sheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();
		if (folderName.startsWith("KMA")) {
			cbuName = "KMA";
		} else {
			cbuName = "HMA";
		}
		String sourceTableName = readExcelController.getCellValue(filePath, sheetName, 23, "B");
		String stgTableName = readExcelController.getCellValue(filePath, sheetName, 23, "J");
		Folder folder = new Folder(cbuName + "_SHARED", "", "INFA_ADMIN", SharedEnum.SHARED, "", "rwx------", uuid);
		folder.addSource(sourceStgController.getSourceForSharedFolder(filePath, cbuName));
		folder.addTarget(sourceStgController.getTargetStgForSharedFolder(filePath));
		repository.addFolder(folder);

		String uuid1 = UUID.randomUUID().toString();

		Folder notSharedFolder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid1);

		Mapping mapping = new Mapping();
		mapping.setDescription("");
		mapping.setIsValid("YES");

		String mappingName = readExcelController.getCellValue(filePath, sheetName, 4, "D");
		mapping.setName(mappingName);
		mapping.setObjectVersion(1);
		mapping.setVersionNumber(1);
		
		Transformation sqTransformation = new Transformation();
		sqTransformation.setName("SQ_sc_" + sourceTableName);
		sqTransformation.setDescription("");
		sqTransformation.setObjectVersion("1");
		sqTransformation.setVersionNumber("1");
		sqTransformation.setReUsable("NO");
		sqTransformation.setType("Source Qualifier");
		sqTransformation.setTransformFieldList(sourceStgController.getTransformFieldSQTransformation(filePath));
		sqTransformation
				.setTableAttributeList(sourceStgController.getTableAttributesForSQTransformation(filePath, cbuName));
		mapping.addTransformation(sqTransformation);
		Transformation expTransformation = new Transformation();
		expTransformation.setName("EXPTRANS");
		expTransformation.setDescription("");
		expTransformation.setObjectVersion("1");
		expTransformation.setVersionNumber("1");
		expTransformation.setReUsable("NO");
		expTransformation.setType("Expression");

		List<TransformField> list = new ArrayList<TransformField>();
		List<TransformField> list1 = new ArrayList<TransformField>();
		list = sourceStgController.getTransformFieldEXPTransformation(filePath);
		list1 = sourceStgController.getTransformFieldEXPTransformation1(filePath);
		list.addAll(list1);
		expTransformation.setTransformFieldList(list);

		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");

		expTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(expTransformation);

		mapping.addInstance(sourceStgController.getInstances(filePath));
		mapping.addConnector(sourceStgController.conList);
		TargetLoader load = new TargetLoader();
		load.setOrder("1");
		load.setTargetInstance("sc_" + stgTableName);
		mapping.addTargetLoader(load);
		ErpInfo erpInfo = new ErpInfo();
		mapping.addErpInfo(erpInfo);
		notSharedFolder.addMapping(mapping);
		notSharedFolder.addShortcut(sourceStgController.getShortCut(filePath, cbuName, repoName));
		repository.addFolder(notSharedFolder);
		powerMart.addRepository(repository);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + mappingName + ".xml");
			StringWriter sw = new StringWriter();
			PrintWriter printWriter = new PrintWriter(sw);
			DataWriter dataWriter = new DataWriter(printWriter, "UTF-8", new JaxbCharacterEscapeHandler());
			// Perform Marshalling operation

			marshaller.marshal(powerMart, dataWriter);
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">\n"
					+ sw.toString());
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void generateStageODSMapping(String filePath) throws JAXBException {

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String uuid = UUID.randomUUID().toString();
		String uuid1 = UUID.randomUUID().toString();
		String msheetName = "Mapping Details";
		String sSheetName = "Session Properties";
		String cbuName = "";
		// Fetch Folder name from excel
		String key = "";
		String value = "";
		String lookupTableName = "";
		String lookupConnectionName = "";
		String storedProcTableName = "";
		String connectionInfo = readExcelController.getCellValue(filePath, msheetName, 72, "BJ");
		String sourceTableName = readExcelController.getCellValue(filePath, msheetName, 23, "J");
		String storedProcTransName = "";
		String lookupTransName="";
		String folderName = readExcelController.getCellValue(filePath, msheetName, 1, "D");
		String repoName = readExcelController.getCellValue(filePath, msheetName, 2, "D");
		String mappingName = readExcelController.getCellValue(filePath, msheetName, 4, "K");
		Mapping mapping = null;
		Folder notSharedFolder = null;
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		
		if (folderName.startsWith("KMA")) {
			cbuName = "KMA";
		} else {
			cbuName = "HMA";
		}
		Folder folder = new Folder(cbuName + "_SHARED", "", "INFA_ADMIN", SharedEnum.SHARED, "", "rwx------", uuid);
		String lookupCondition1 = readExcelController.getCellValue(filePath, msheetName, 69, "BJ");
		String lookupCondition2 = readExcelController.getCellValue(filePath, msheetName, 70, "BJ");
		
		for (int i = 58;; i++) {
			if (!readExcelController.getCellValue(filePath, sSheetName, i, "N").isEmpty()) {
				 key = readExcelController.getCellValue(filePath, sSheetName, i, "N");
				 value = readExcelController.getCellValue(filePath, sSheetName, i, "O");
				 String lookupCondition = "";
				 if(value.equalsIgnoreCase("Stored Procedure")) {
					 storedProcTableName = readExcelController.getCellValue(filePath, sSheetName, i, "Q");
					 storedProcTransName = readExcelController.getCellValue(filePath, sSheetName, i, "N");
					 Transformation stdProcTransformation = null;
					 if(storedProcTransName.equalsIgnoreCase("RPKG_CVA_GET_SEQUENCE")) {
						 stdProcTransformation = new Transformation("", "HMA_PKG_PRC_GET_SEQUENCE", "1", "YES", "Stored Procedure", "1");
					 }else {
						 stdProcTransformation = new Transformation("", storedProcTransName, "1", "YES", "Stored Procedure", "1");
					 }
						stdProcTransformation
								.setTransformFieldList(stageODSController.getTransformFieldForStoredProcTransformation(filePath,storedProcTransName));
						stdProcTransformation.setTableAttributeList(stageODSController.getTableAttributesForStoredProcTransformation(filePath,
								storedProcTableName, connectionInfo));
						folder.addTransformation(stdProcTransformation);
				 } if(value.equalsIgnoreCase("Lookup Procedure")) {
					 lookupTableName = readExcelController.getCellValue(filePath, sSheetName, i, "Q");
					 lookupConnectionName = readExcelController.getCellValue(filePath, sSheetName, i, "R");
					 if(!lookupConnectionName.isEmpty()) {
						 connectionInfo = lookupConnectionName;
					 }
					 lookupTransName = readExcelController.getCellValue(filePath, sSheetName, i, "N");
					 Transformation lookupTransformation = new Transformation("", lookupTransName, "1", "YES", "Lookup Procedure", "1");
					 if(lookupTransName.equalsIgnoreCase(readExcelController.getCellValue(filePath, msheetName, 24, "BL"))) {
						 lookupCondition = lookupCondition1;
					 }else {
						 lookupCondition = lookupCondition2;
					 }
					 lookupTransformation
								.setTransformFieldList(stageODSController.getTransformFieldForLookupTransformation(filePath,lookupTransName));
					 lookupTransformation.setTableAttributeList(stageODSController.getTableAttributesForLookupTransformation(filePath,
								lookupTableName, connectionInfo, lookupCondition));
						folder.addTransformation(lookupTransformation);
				 }
			}else {
				break;
			}
		}
		folder.addSource(stageODSController.getSourceForSharedFolder(filePath, cbuName));
		folder.addTarget(stageODSController.getTargetForSharedFolder(filePath));
		repository.addFolder(folder);
		

		notSharedFolder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid1);

		mapping = new Mapping();
		mapping.setDescription("");
		mapping.setIsValid("YES");
		mapping.setName(mappingName);
		mapping.setObjectVersion(1);
		mapping.setVersionNumber(1);
		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");
		TableAttribute tableAttribute_UPD = new TableAttribute();
		tableAttribute_UPD.setName("Update Strategy Expression");
		tableAttribute_UPD.setValue("DD_UPDATE");
		TableAttribute tableAttribute_UPD1 = new TableAttribute();
		tableAttribute_UPD1.setName("Forward Rejected Rows");
		tableAttribute_UPD1.setValue("YES");
		for (int i = 58;; i++) {
			if (!readExcelController.getCellValue(filePath, sSheetName, i, "N").isEmpty()) {
				 key = readExcelController.getCellValue(filePath, sSheetName, i, "N");
				 value = readExcelController.getCellValue(filePath, sSheetName, i, "O");
		if(key.equalsIgnoreCase("SQ_sc_" + sourceTableName)) {
		Transformation sqTransformation = new Transformation();
		sqTransformation.setName(key);
		sqTransformation.setDescription("");
		sqTransformation.setObjectVersion("1");
		sqTransformation.setVersionNumber("1");
		sqTransformation.setReUsable("NO");
		sqTransformation.setType(value);
		sqTransformation.setTransformFieldList(stageODSController.getTransformFieldSQTransformation(filePath));
		sqTransformation
				.setTableAttributeList(stageODSController.getTableAttributesForSQTransformation(filePath, cbuName));
		mapping.addTransformation(sqTransformation);
		}
		if(key.equalsIgnoreCase("EXPTRANS")){
		Transformation expTransformation = new Transformation();
		expTransformation.setName(key);
		expTransformation.setDescription("");
		expTransformation.setObjectVersion("1");
		expTransformation.setVersionNumber("1");
		expTransformation.setReUsable("NO");
		expTransformation.setType(value);

		List<TransformField> list = new ArrayList<TransformField>();
		List<TransformField> list1 = new ArrayList<TransformField>();
		list = stageODSController.getTransformFieldEXPTransformation(filePath);
		list1 = stageODSController.getTransformFieldEXPTransformation1(filePath);
		list.addAll(list1);
		expTransformation.setTransformFieldList(list);
		

		expTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(expTransformation);
				}if(key.equalsIgnoreCase("EXPVLDN")){
					Transformation expTransformation = new Transformation();
					expTransformation.setName(key);
					expTransformation.setDescription("");
					expTransformation.setObjectVersion("1");
					expTransformation.setVersionNumber("1");
					expTransformation.setReUsable("NO");
					expTransformation.setType(value);

					expTransformation.setTransformFieldList(stageODSController.getTransformFieldEXPVLDNTransformation(filePath));
					
					expTransformation.addTableAttribute(tableAttribute);
					mapping.addTransformation(expTransformation);
					}if(key.equalsIgnoreCase("RTRTRANS")) {
					Transformation rtrTransformation = new Transformation();
					rtrTransformation.setName(key);
					rtrTransformation.setDescription("");
					rtrTransformation.setObjectVersion("1");
					rtrTransformation.setVersionNumber("1");
					rtrTransformation.setReUsable("NO");
					rtrTransformation.setType(value);

					Group inputGroup = new Group();
					inputGroup.setDescription("");
					inputGroup.setName("INPUT");
					inputGroup.setOrder("1");
					inputGroup.setType("INPUT");
					rtrTransformation.addGroup(inputGroup);

					Group insertGroup = new Group();
					insertGroup.setDescription("");
					insertGroup.setName("INSERT");
					insertGroup.setOrder("2");
					insertGroup.setType("OUTPUT");

					String expression = readExcelController.getCellValue(filePath, msheetName, 51, "BJ");
					insertGroup.setExpression(expression);
					rtrTransformation.addGroup(insertGroup);

					Group updateGroup = new Group();
					updateGroup.setDescription("");
					updateGroup.setName("UPDATE");
					updateGroup.setOrder("3");
					updateGroup.setType("OUTPUT");
					expression = readExcelController.getCellValue(filePath, msheetName, 52, "BJ");
					updateGroup.setExpression(expression);
					rtrTransformation.addGroup(updateGroup);

					Group deafultGroup = new Group();
					deafultGroup.setDescription("Path for the data when none of the group conditions are satisfied.");
					deafultGroup.setName("DEFAULT1");
					deafultGroup.setOrder("5");
					deafultGroup.setType("OUTPUT/DEFAULT");
					rtrTransformation.addGroup(deafultGroup);
					List<TransformField> transformFieldList = new ArrayList<TransformField>();
					transformFieldList.addAll(stageODSController.getTransformFieldRTRTransformation(filePath, "INPUT"));
					transformFieldList.addAll(stageODSController.getTransformFieldRTRTransformation(filePath, "INSERT"));
					transformFieldList.addAll(stageODSController.getTransformFieldRTRTransformation(filePath, "UPDATE"));
					transformFieldList.addAll(stageODSController.getTransformFieldRTRTransformation(filePath, "DEFAULT1"));
					rtrTransformation.setTransformFieldList(transformFieldList);
					rtrTransformation.addTableAttribute(tableAttribute);
					mapping.addTransformation(rtrTransformation);
					
				    }if(key.equalsIgnoreCase("EXP_INS"))
				    {
					Transformation expInsTransformation = new Transformation();
					expInsTransformation.setName(key);
					expInsTransformation.setDescription("");
					expInsTransformation.setObjectVersion("1");
					expInsTransformation.setVersionNumber("1");
					expInsTransformation.setReUsable("NO");
					expInsTransformation.setType(value);

					expInsTransformation.setTransformFieldList(stageODSController.getTransformFieldEXPINSTransformation(filePath));
					
					expInsTransformation.addTableAttribute(tableAttribute);
					mapping.addTransformation(expInsTransformation);
				    }
				if(key.equalsIgnoreCase("EXP_UPD"))
				{
					Transformation expUpdTransformation = new Transformation();
					expUpdTransformation.setName(key);
					expUpdTransformation.setDescription("");
					expUpdTransformation.setObjectVersion("1");
					expUpdTransformation.setVersionNumber("1");
					expUpdTransformation.setReUsable("NO");
					expUpdTransformation.setType(value);

					expUpdTransformation.setTransformFieldList(stageODSController.getTransformFieldEXPUPDTransformation(filePath));
					
					expUpdTransformation.addTableAttribute(tableAttribute);
					mapping.addTransformation(expUpdTransformation);
				}
				if(key.equalsIgnoreCase("UPDTRANS"))
				{
					Transformation updTransformation = new Transformation();
					updTransformation.setName(key);
					updTransformation.setDescription("");
					updTransformation.setObjectVersion("1");
					updTransformation.setVersionNumber("1");
					updTransformation.setReUsable("NO");
					updTransformation.setType(value);

					updTransformation.setTransformFieldList(stageODSController.getTransformFieldUPDTransformation(filePath));
					updTransformation.addTableAttribute(tableAttribute_UPD);
					updTransformation.addTableAttribute(tableAttribute_UPD1);
					updTransformation.addTableAttribute(tableAttribute);
					mapping.addTransformation(updTransformation);
				}
			}else {
				break;
			}
		}
		mapping.addInstance(stageODSController.getInstances(filePath,cbuName));
		mapping.addConnector(stageODSController.conList);
		for (int i = 58;; i++) {
			if (!readExcelController.getCellValue(filePath, sSheetName, i, "N").isEmpty()) {
				 key = readExcelController.getCellValue(filePath, sSheetName, i, "N");
				 value = readExcelController.getCellValue(filePath, sSheetName, i, "O");
				 if(value.equalsIgnoreCase("Target Definition")) {
					 TargetLoader load = new TargetLoader();
					 load.setOrder("1");
					 load.setTargetInstance("sc_" + key + "_INS");
					 mapping.addTargetLoader(load);
					 TargetLoader load1 = new TargetLoader();
					 load1.setOrder("1");
					 load1.setTargetInstance("sc_" + key + "_UPD");
					 mapping.addTargetLoader(load1);
					 }
				 
			}else {
				break;
			}
		}
		ErpInfo erpInfo = new ErpInfo();
		mapping.addErpInfo(erpInfo);
		notSharedFolder.addMapping(mapping);
		notSharedFolder.addShortcut(stageODSController.getShortCut(filePath, cbuName, repoName));
		repository.addFolder(notSharedFolder);
		powerMart.addRepository(repository);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + mappingName + ".xml");
			StringWriter sw = new StringWriter();
			PrintWriter printWriter = new PrintWriter(sw);
			DataWriter dataWriter = new DataWriter(printWriter, "UTF-8", new JaxbCharacterEscapeHandler());
			// Perform Marshalling operation

			marshaller.marshal(powerMart, dataWriter);
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">\n"
					+ sw.toString());
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void generateXMLFileStageMapping(String filePath,String xsdpath) throws JAXBException {


		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String uuid = UUID.randomUUID().toString();
		String uuid1 = UUID.randomUUID().toString();
		String msheetName = "Mapping Details";
		String sSheetName = "Session Properties";
		String cbuName = "";
		// Fetch Folder name from excel
		String key = "";
		String value = "";
		String lookupTableName = "";
		String lookupConnectionName = "";
		String storedProcTableName = "";
		String connectionInfo = readExcelController.getCellValue(filePath, msheetName, 72, "BJ");
		String sourceFileName = readExcelController.getCellValue(filePath, msheetName, 23, "B");
		String storedProcTransName = "";
		String lookupTransName="";
		String folderName = readExcelController.getCellValue(filePath, msheetName, 1, "D");
		String repoName = readExcelController.getCellValue(filePath, msheetName, 2, "D");
		String mappingName = readExcelController.getCellValue(filePath, msheetName, 4, "D");
		Mapping mapping = null;
		Folder notSharedFolder = null;
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		
		if (folderName.startsWith("KMA")) {
			cbuName = "KMA";
		} else {
			cbuName = "HMA";
		}
		Folder folder = new Folder(cbuName + "_SHARED", "", "INFA_ADMIN", SharedEnum.SHARED, "", "rwx------", uuid);
		/*List<String> fewLines = new ArrayList<String>();
		try {
    		int count = 0;
			List<String> allLines = Files.readAllLines(Paths.get(xsdpath));
			
			for (String line : allLines) {
				if(line.trim().startsWith("<SOURCE")) {
					count++;
				}
				if(line.trim().startsWith("</FOLDER")) {
					count=0;
				}
				if(count>0) {
					fewLines.add(line);
				
			}

			}			
			} catch (IOException e) {
			e.printStackTrace();
		}
		Source source = new Source();
		source.addSourceList(fewLines);
		folder.addSource(source);*/
		//folder.addSource(xmlStageController.getSourceForSharedFolder(filePath, xsdpath, sourceFileName));
		folder.addTarget(xmlStageController.getTargetPrdForSharedFolder(filePath));
		repository.addFolder(folder);
		

		notSharedFolder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid1);

		mapping = new Mapping();
		mapping.setDescription("");
		mapping.setIsValid("YES");
		mapping.setName(mappingName);
		mapping.setObjectVersion(1);
		mapping.setVersionNumber(1);
		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");
		TableAttribute tableAttribute_UPD = new TableAttribute();
		tableAttribute_UPD.setName("Update Strategy Expression");
		tableAttribute_UPD.setValue("DD_UPDATE");
		TableAttribute tableAttribute_UPD1 = new TableAttribute();
		tableAttribute_UPD1.setName("Forward Rejected Rows");
		tableAttribute_UPD1.setValue("YES");
		for (int i = 58;; i++) {
			if (!readExcelController.getCellValue(filePath, sSheetName, i, "B").isEmpty()) {
				 key = readExcelController.getCellValue(filePath, sSheetName, i, "B");
				 value = readExcelController.getCellValue(filePath, sSheetName, i, "C");
		if(key.equalsIgnoreCase("XMLDSQ_sc_" + sourceFileName)) {
		Transformation sqTransformation = new Transformation();
		sqTransformation.setName(key);
		sqTransformation.setDescription("");
		sqTransformation.setObjectVersion("1");
		sqTransformation.setRefdbdName(readExcelController.getCellValue(filePath, msheetName, 2, "G"));
		sqTransformation.setRefSourceName("sc_" + sourceFileName);
		sqTransformation.setVersionNumber("1");
		sqTransformation.setReUsable("NO");
		sqTransformation.setType(value);
		sqTransformation.setGroupList(xmlStageController.getGroupList(filePath));
		//sqTransformation.setGroupList(xmlStageController.getGroupList(xsdpath));
		sqTransformation.setTransformFieldList(xmlStageController.getTransformFieldSQTransformation(filePath, xsdpath));
		sqTransformation
				.setTableAttributeList(xmlStageController.getTableAttributesForSQTransformation(filePath, cbuName));
		mapping.addTransformation(sqTransformation);
				}
				if(key.equalsIgnoreCase("EXPTRANS")){
		Transformation expTransformation = new Transformation();
		expTransformation.setName(key);
		expTransformation.setDescription("");
		expTransformation.setObjectVersion("1");
		expTransformation.setVersionNumber("1");
		expTransformation.setReUsable("NO");
		expTransformation.setType(value);

		List<TransformField> list = new ArrayList<TransformField>();
		List<TransformField> list1 = new ArrayList<TransformField>();
		list = xmlStageController.getTransformFieldEXPTransformation(filePath, xsdpath);
		list1 = xmlStageController.getTransformFieldEXPTransformation1(filePath, xsdpath);
		list.addAll(list1);
		expTransformation.setTransformFieldList(list);
		

		expTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(expTransformation);
		}
			}else {
				break;
			}
		}
		mapping.addInstance(xmlStageController.getInstances(filePath,cbuName));
		mapping.addConnector(xmlStageController.conList);
		
		String stgTableName = readExcelController.getCellValue(filePath, msheetName, 23, "J");
		TargetLoader load = new TargetLoader();
		load.setOrder("1");
		load.setTargetInstance("sc_" + stgTableName);
		mapping.addTargetLoader(load);
		ErpInfo erpInfo = new ErpInfo();
		mapping.addErpInfo(erpInfo);
		notSharedFolder.addMapping(mapping);
		
		notSharedFolder.addShortcut(xmlStageController.getShortCut(filePath, cbuName, repoName));
		repository.addFolder(notSharedFolder);
		powerMart.addRepository(repository);
		System.out.print("NOTSHARED"+repository);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + mappingName + ".xml");
			StringWriter sw = new StringWriter();
			PrintWriter printWriter = new PrintWriter(sw);
			DataWriter dataWriter = new DataWriter(printWriter, "UTF-8", new JaxbCharacterEscapeHandler());
			// Perform Marshalling operation

			marshaller.marshal(powerMart, dataWriter);
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">\n"
					+ sw.toString());
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	
	}
	
	/*public void generateFileODSMapping(String filePath) throws JAXBException {

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String uuid = UUID.randomUUID().toString();
		String uuid1 = UUID.randomUUID().toString();
		String msheetName = "Mapping Details";
		String sSheetName = "Session Properties";
		String cbuName = "";
		// Fetch Folder name from excel
		String key = "";
		String value = "";
		String lookupTableName = "";
		String lookupConnectionName = "";
		String storedProcTableName = "";
		String connectionInfo = readExcelController.getCellValue(filePath, msheetName, 72, "BJ");
		String sourceTableName = readExcelController.getCellValue(filePath, msheetName, 23, "B");
		String storedProcTransName = "";
		String lookupTransName="";
		String folderName = readExcelController.getCellValue(filePath, msheetName, 1, "D");
		String repoName = readExcelController.getCellValue(filePath, msheetName, 2, "D");
		String mappingName = readExcelController.getCellValue(filePath, msheetName, 4, "K");
		Mapping mapping = null;
		Folder notSharedFolder = null;
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		
		if (folderName.startsWith("KMA")) {
			cbuName = "KMA";
		} else {
			cbuName = "HMA";
		}
		Folder folder = new Folder(cbuName + "_SHARED", "", "INFA_ADMIN", SharedEnum.SHARED, "", "rwx------", uuid);
		String lookupCondition1 = readExcelController.getCellValue(filePath, msheetName, 69, "BJ");
		String lookupCondition2 = readExcelController.getCellValue(filePath, msheetName, 70, "BJ");
		for (int i = 58;; i++) {
			if (!readExcelController.getCellValue(filePath, sSheetName, i, "N").isEmpty()) {
				 key = readExcelController.getCellValue(filePath, sSheetName, i, "N");
				 value = readExcelController.getCellValue(filePath, sSheetName, i, "O");
				 String lookupCondition = "";
				 if(value.equalsIgnoreCase("Stored Procedure")) {
					 storedProcTableName = readExcelController.getCellValue(filePath, sSheetName, i, "Q");
					 storedProcTransName = readExcelController.getCellValue(filePath, sSheetName, i, "N");
					 Transformation stdProcTransformation = null;
					 if(storedProcTransName.equalsIgnoreCase("RPKG_CVA_GET_SEQUENCE")) {
						 stdProcTransformation = new Transformation("", "HMA_PKG_PRC_GET_SEQUENCE", "1", "YES", "Stored Procedure", "1");
					 }else {
						 stdProcTransformation = new Transformation("", storedProcTransName, "1", "YES", "Stored Procedure", "1");
					 }
						stdProcTransformation
								.setTransformFieldList(fileODSController.getTransformFieldForStoredProcTransformation(filePath,storedProcTransName));
						stdProcTransformation.setTableAttributeList(fileODSController.getTableAttributesForStoredProcTransformation(filePath,
								storedProcTableName, connectionInfo));
						folder.addTransformation(stdProcTransformation);
				 } if(value.equalsIgnoreCase("Lookup Procedure")) {
					 lookupTableName = readExcelController.getCellValue(filePath, sSheetName, i, "Q");
					 lookupConnectionName = readExcelController.getCellValue(filePath, sSheetName, i, "R");
					 if(!lookupConnectionName.isEmpty()) {
						 connectionInfo = lookupConnectionName;
					 }
					 lookupTransName = readExcelController.getCellValue(filePath, sSheetName, i, "N");
					 Transformation lookupTransformation = new Transformation("", lookupTransName, "1", "YES", "Lookup Procedure", "1");
					 if(lookupTransName.equalsIgnoreCase(readExcelController.getCellValue(filePath, msheetName, 24, "BL"))) {
						 lookupCondition = lookupCondition1;
					 }else {
						 lookupCondition = lookupCondition2;
					 }
					 lookupTransformation
								.setTransformFieldList(fileODSController.getTransformFieldForLookupTransformation(filePath,lookupTransName));
					 lookupTransformation.setTableAttributeList(fileODSController.getTableAttributesForLookupTransformation(filePath,
								lookupTableName, connectionInfo, lookupCondition));
						folder.addTransformation(lookupTransformation);
				 }
			}else {
				break;
			}
		}
		folder.addSource(fileODSController.getSourceForSharedFolder(filePath, cbuName));
		folder.addTarget(fileODSController.getTargetPrdForSharedFolder(filePath));
		repository.addFolder(folder);
		

		notSharedFolder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid1);

		mapping = new Mapping();
		mapping.setDescription("");
		mapping.setIsValid("YES");
		mapping.setName(mappingName);
		mapping.setObjectVersion(1);
		mapping.setVersionNumber(1);
		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");
		TableAttribute tableAttribute_UPD = new TableAttribute();
		tableAttribute_UPD.setName("Update Strategy Expression");
		tableAttribute_UPD.setValue("DD_UPDATE");
		TableAttribute tableAttribute_UPD1 = new TableAttribute();
		tableAttribute_UPD1.setName("Forward Rejected Rows");
		tableAttribute_UPD1.setValue("YES");
		for (int i = 58;; i++) {
			if (!readExcelController.getCellValue(filePath, sSheetName, i, "N").isEmpty()) {
				 key = readExcelController.getCellValue(filePath, sSheetName, i, "N");
				 value = readExcelController.getCellValue(filePath, sSheetName, i, "O");
		if(key.equalsIgnoreCase("SQ_sc_" + sourceTableName)) {
		Transformation sqTransformation = new Transformation();
		sqTransformation.setName(key);
		sqTransformation.setDescription("");
		sqTransformation.setObjectVersion("1");
		sqTransformation.setVersionNumber("1");
		sqTransformation.setReUsable("NO");
		sqTransformation.setType(value);
		sqTransformation.setTransformFieldList(fileODSController.getTransformFieldSQTransformation(filePath));
		sqTransformation
				.setTableAttributeList(fileODSController.getTableAttributesForSQTransformation(filePath, cbuName));
		mapping.addTransformation(sqTransformation);
				}
				if(key.equalsIgnoreCase("EXPTRANS")){
		Transformation expTransformation = new Transformation();
		expTransformation.setName(key);
		expTransformation.setDescription("");
		expTransformation.setObjectVersion("1");
		expTransformation.setVersionNumber("1");
		expTransformation.setReUsable("NO");
		expTransformation.setType(value);

		List<TransformField> list = new ArrayList<TransformField>();
		List<TransformField> list1 = new ArrayList<TransformField>();
		list = fileODSController.getTransformFieldEXPTransformation(filePath);
		list1 = fileODSController.getTransformFieldEXPTransformation1(filePath);
		list.addAll(list1);
		expTransformation.setTransformFieldList(list);
		

		expTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(expTransformation);
				}if(key.equalsIgnoreCase("EXPVLDN")){
					Transformation expTransformation = new Transformation();
					expTransformation.setName(key);
					expTransformation.setDescription("");
					expTransformation.setObjectVersion("1");
					expTransformation.setVersionNumber("1");
					expTransformation.setReUsable("NO");
					expTransformation.setType(value);

					expTransformation.setTransformFieldList(fileODSController.getTransformFieldEXPVLDNTransformation(filePath));
					
					expTransformation.addTableAttribute(tableAttribute);
					mapping.addTransformation(expTransformation);
							}if(key.equalsIgnoreCase("RTRTRANS")) {
					Transformation rtrTransformation = new Transformation();
					rtrTransformation.setName(key);
					rtrTransformation.setDescription("");
					rtrTransformation.setObjectVersion("1");
					rtrTransformation.setVersionNumber("1");
					rtrTransformation.setReUsable("NO");
					rtrTransformation.setType(value);

					Group inputGroup = new Group();
					inputGroup.setDescription("");
					inputGroup.setName("INPUT");
					inputGroup.setOrder("1");
					inputGroup.setType("INPUT");
					rtrTransformation.addGroup(inputGroup);

					Group insertGroup = new Group();
					insertGroup.setDescription("");
					insertGroup.setName("INSERT");
					insertGroup.setOrder("2");
					insertGroup.setType("OUTPUT");

					String expression = readExcelController.getCellValue(filePath, msheetName, 51, "BJ");
					insertGroup.setExpression(expression);
					rtrTransformation.addGroup(insertGroup);

					Group updateGroup = new Group();
					updateGroup.setDescription("");
					updateGroup.setName("UPDATE");
					updateGroup.setOrder("3");
					updateGroup.setType("OUTPUT");
					expression = readExcelController.getCellValue(filePath, msheetName, 52, "BJ");
					updateGroup.setExpression(expression);
					rtrTransformation.addGroup(updateGroup);

					Group deafultGroup = new Group();
					deafultGroup.setDescription("Path for the data when none of the group conditions are satisfied.");
					deafultGroup.setName("DEFAULT1");
					deafultGroup.setOrder("5");
					deafultGroup.setType("OUTPUT/DEFAULT");
					rtrTransformation.addGroup(deafultGroup);
					List<TransformField> transformFieldList = new ArrayList<TransformField>();
					//transformFieldList.addAll(fileODSController.getTransformFieldRTRTransformation_LKP(filePath, "INPUT"));
					transformFieldList.addAll(fileODSController.getTransformFieldRTRTransformation(filePath, "INPUT"));
					//transformFieldList.addAll(fileODSController.getTransformFieldRTRTransformation_LKP(filePath, "INSERT"));
					transformFieldList.addAll(fileODSController.getTransformFieldRTRTransformation(filePath, "INSERT"));
					//transformFieldList.addAll(fileODSController.getTransformFieldRTRTransformation_LKP(filePath, "UPDATE"));
					transformFieldList.addAll(fileODSController.getTransformFieldRTRTransformation(filePath, "UPDATE"));
					//transformFieldList.addAll(fileODSController.getTransformFieldRTRTransformation_LKP(filePath, "DEFAULT1"));
					transformFieldList.addAll(fileODSController.getTransformFieldRTRTransformation(filePath, "DEFAULT1"));
					rtrTransformation.setTransformFieldList(transformFieldList);
					rtrTransformation.addTableAttribute(tableAttribute);
					mapping.addTransformation(rtrTransformation);
					
				}if(key.equalsIgnoreCase("EXP_INS"))
				{
					Transformation expInsTransformation = new Transformation();
					expInsTransformation.setName(key);
					expInsTransformation.setDescription("");
					expInsTransformation.setObjectVersion("1");
					expInsTransformation.setVersionNumber("1");
					expInsTransformation.setReUsable("NO");
					expInsTransformation.setType(value);

					expInsTransformation.setTransformFieldList(fileODSController.getTransformFieldEXPINSTransformation(filePath));
					
					expInsTransformation.addTableAttribute(tableAttribute);
					mapping.addTransformation(expInsTransformation);
				}
				if(key.equalsIgnoreCase("EXP_UPD"))
				{
					Transformation expUpdTransformation = new Transformation();
					expUpdTransformation.setName(key);
					expUpdTransformation.setDescription("");
					expUpdTransformation.setObjectVersion("1");
					expUpdTransformation.setVersionNumber("1");
					expUpdTransformation.setReUsable("NO");
					expUpdTransformation.setType(value);

					expUpdTransformation.setTransformFieldList(fileODSController.getTransformFieldEXPUPDTransformation(filePath));
					
					expUpdTransformation.addTableAttribute(tableAttribute);
					mapping.addTransformation(expUpdTransformation);
				}
				if(key.equalsIgnoreCase("UPDTRANS"))
				{
					Transformation updTransformation = new Transformation();
					updTransformation.setName(key);
					updTransformation.setDescription("");
					updTransformation.setObjectVersion("1");
					updTransformation.setVersionNumber("1");
					updTransformation.setReUsable("NO");
					updTransformation.setType(value);

					updTransformation.setTransformFieldList(fileODSController.getTransformFieldUPDTransformation(filePath));
					updTransformation.addTableAttribute(tableAttribute_UPD);
					updTransformation.addTableAttribute(tableAttribute_UPD1);
					updTransformation.addTableAttribute(tableAttribute);
					mapping.addTransformation(updTransformation);
				}
			}else {
				break;
			}
		}
		mapping.addInstance(fileODSController.getInstances(filePath,cbuName));
		mapping.addConnector(fileODSController.conList);
		for (int i = 58;; i++) {
			if (!readExcelController.getCellValue(filePath, sSheetName, i, "N").isEmpty()) {
				 key = readExcelController.getCellValue(filePath, sSheetName, i, "N");
				 value = readExcelController.getCellValue(filePath, sSheetName, i, "O");
				 if(value.equalsIgnoreCase("Target Definition")) {
					 TargetLoader load = new TargetLoader();
					 load.setOrder("1");
					 load.setTargetInstance("sc_" + key + "_INS");
					 mapping.addTargetLoader(load);
					 TargetLoader load1 = new TargetLoader();
					 load1.setOrder("1");
					 load1.setTargetInstance("sc_" + key + "_UPD");
					 mapping.addTargetLoader(load1);
					 }
				 
			}else {
				break;
			}
		}
		ErpInfo erpInfo = new ErpInfo();
		mapping.addErpInfo(erpInfo);
		notSharedFolder.addMapping(mapping);
		notSharedFolder.addShortcut(fileODSController.getShortCut(filePath, cbuName, repoName));
		repository.addFolder(notSharedFolder);
		powerMart.addRepository(repository);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + mappingName + ".xml");
			StringWriter sw = new StringWriter();
			PrintWriter printWriter = new PrintWriter(sw);
			DataWriter dataWriter = new DataWriter(printWriter, "UTF-8", new JaxbCharacterEscapeHandler());
			// Perform Marshalling operation

			marshaller.marshal(powerMart, dataWriter);
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">\n"
					+ sw.toString());
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/
	
	public void generateFileStgMapping(String filePath) throws JAXBException {

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		// String filePath = "D:/ETL/ETL_Code_Generator-KMF.xlsx";
		String sheetName = "Mapping Details";
		String cbuName = "";
		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, sheetName, 1, "D");
		String repoName = readExcelController.getCellValue(filePath, sheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();
		if (folderName.startsWith("KMA")) {
			cbuName = "KMA";
		} else {
			cbuName = "HMA";
		}
		String sourceTableName = readExcelController.getCellValue(filePath, sheetName, 23, "B");
		String stgTableName = readExcelController.getCellValue(filePath, sheetName, 23, "J");
		Folder folder = new Folder(cbuName + "_SHARED", "", "INFA_ADMIN", SharedEnum.SHARED, "", "rwx------", uuid);
		folder.addSource(fileStgController.getSourceForSharedFolder(filePath, cbuName));
		folder.addTarget(fileStgController.getTargetStgForSharedFolder(filePath));
		repository.addFolder(folder);

		String uuid1 = UUID.randomUUID().toString();

		Folder notSharedFolder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid1);

		Mapping mapping = new Mapping();
		mapping.setDescription("");
		mapping.setIsValid("YES");

		String mappingName = readExcelController.getCellValue(filePath, sheetName, 4, "D");
		mapping.setName(mappingName);
		mapping.setObjectVersion(1);
		mapping.setVersionNumber(1);
		Transformation sqTransformation = new Transformation();
		sqTransformation.setName("SQ_sc_" + sourceTableName);
		sqTransformation.setDescription("");
		sqTransformation.setObjectVersion("1");
		sqTransformation.setVersionNumber("1");
		sqTransformation.setReUsable("NO");
		sqTransformation.setType("Source Qualifier");
		sqTransformation.setTransformFieldList(fileStgController.getTransformFieldSQTransformation(filePath));
		sqTransformation
				.setTableAttributeList(fileStgController.getTableAttributesForSQTransformation(filePath, cbuName));
		mapping.addTransformation(sqTransformation);
		Transformation expTransformation = new Transformation();
		expTransformation.setName("EXPTRANS");
		expTransformation.setDescription("");
		expTransformation.setObjectVersion("1");
		expTransformation.setVersionNumber("1");
		expTransformation.setReUsable("NO");
		expTransformation.setType("Expression");

		List<TransformField> list = new ArrayList<TransformField>();
		List<TransformField> list1 = new ArrayList<TransformField>();
		list = fileStgController.getTransformFieldEXPTransformation(filePath);
		list1 = fileStgController.getTransformFieldEXPTransformation1(filePath);
		list.addAll(list1);
		expTransformation.setTransformFieldList(list);

		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");

		expTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(expTransformation);

		mapping.addInstance(fileStgController.getInstances(filePath));
		mapping.addConnector(fileStgController.conList);
		TargetLoader load = new TargetLoader();
		load.setOrder("1");
		load.setTargetInstance("sc_" + stgTableName);
		mapping.addTargetLoader(load);
		ErpInfo erpInfo = new ErpInfo();
		mapping.addErpInfo(erpInfo);
		notSharedFolder.addMapping(mapping);
		notSharedFolder.addShortcut(fileStgController.getShortCut(filePath, cbuName, repoName));
		repository.addFolder(notSharedFolder);
		powerMart.addRepository(repository);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + mappingName + ".xml");
			StringWriter sw = new StringWriter();
			PrintWriter printWriter = new PrintWriter(sw);
			DataWriter dataWriter = new DataWriter(printWriter, "UTF-8", new JaxbCharacterEscapeHandler());
			// Perform Marshalling operation

			marshaller.marshal(powerMart, dataWriter);
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">\n"
					+ sw.toString());
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void generateInsUpdXML(String filePath) throws JAXBException {
		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);

		// String filePath = "D:/ETL/ETL_Code_Generator-KMF.xlsx";
		String sheetName = "Mapping Details";
		String cbuName = "";
		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, sheetName, 1, "D");
		String repoName = readExcelController.getCellValue(filePath, sheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();
		if (folderName.startsWith("KMA")) {
			cbuName = "KMA";
		} else {
			cbuName = "HMA";
		}
		String lookupTableName = readExcelController.getCellValue(filePath, sheetName, 24, "BB");
		String connectionInfo = readExcelController.getCellValue(filePath, sheetName, 46, "BJ");
		String stg2TableName = readExcelController.getCellValue(filePath, sheetName, 23, "Z");
		Folder folder = new Folder(cbuName + "_SHARED", "", "INFA_ADMIN", SharedEnum.SHARED, "", "rwx------", uuid);
		Transformation transformation = new Transformation("", lookupTableName, "1", "YES", "Lookup Procedure", "1");
		transformation
				.setTransformFieldList(insUpdMappingController.getTransformFieldForLookupTransformation(filePath));
		transformation.setTableAttributeList(insUpdMappingController.getTableAttributesForLookupTransformation(filePath,
				lookupTableName, connectionInfo));
		folder.addTransformation(transformation);
		folder.addSource(insUpdMappingController.getStgSourceForSharedFolder(filePath, cbuName));
		folder.addSource(insUpdMappingController.getPrdSourceForSharedFolder(filePath, cbuName));
		folder.addTarget(insUpdMappingController.getTargetForSharedFolder(filePath));
		repository.addFolder(folder);

		String uuid1 = UUID.randomUUID().toString();

		Folder notSharedFolder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid1);

		Mapping mapping = new Mapping();
		mapping.setDescription("");
		mapping.setIsValid("YES");

		String mappingName = readExcelController.getCellValue(filePath, sheetName, 9, "D");
		mapping.setName(mappingName);
		mapping.setObjectVersion(1);
		mapping.setVersionNumber(1);
		List<TransformField> list = new ArrayList<TransformField>();
		List<TransformField> list1 = new ArrayList<TransformField>();
		Transformation sqTransformation = new Transformation();
		sqTransformation.setName("SQTRANS");
		sqTransformation.setDescription("");
		sqTransformation.setObjectVersion("1");
		sqTransformation.setVersionNumber("1");
		sqTransformation.setReUsable("NO");
		sqTransformation.setType("Source Qualifier");
		int count = 0;
		int emptyCount = 0;
		for (int i = 26;; i++) {
			if (count > 5 || emptyCount > 10)
				break;
			String columnName1 = readExcelController.getCellValue(filePath, sheetName, i, "J");
			String columnName2 = readExcelController.getCellValue(filePath, sheetName, i, "AP");
			if (columnName1 == null || columnName2.isEmpty()) {
				emptyCount++;
			} else if (columnName1.equalsIgnoreCase(columnName2)) {
				count++;
			}
		}

		if (count < 5) {
			list = insUpdMappingController.getTransformFieldSQTransformation(filePath);
			list1 = insUpdMappingController.getTransformFieldSQTransformation1(filePath);
			list.addAll(list1);
		} else {
			list = insUpdMappingController.getTransformFieldSQTransformation1(filePath);
		}
		sqTransformation.setTransformFieldList(list);
		sqTransformation.setTableAttributeList(
				insUpdMappingController.getTableAttributesForSQTransformation(filePath, cbuName));
		mapping.addTransformation(sqTransformation);
		Transformation expTransformation = new Transformation();
		expTransformation.setName("EXPTRANS");
		expTransformation.setDescription("");
		expTransformation.setObjectVersion("1");
		expTransformation.setVersionNumber("1");
		expTransformation.setReUsable("NO");
		expTransformation.setType("Expression");
		expTransformation.setTransformFieldList(insUpdMappingController.getTransformFieldEXPTransformation(filePath));

		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");

		expTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(expTransformation);

		Transformation rtrTransformation = new Transformation();
		rtrTransformation.setName("RTRTRANS");
		rtrTransformation.setDescription("");
		rtrTransformation.setObjectVersion("1");
		rtrTransformation.setVersionNumber("1");
		rtrTransformation.setReUsable("NO");
		rtrTransformation.setType("Router");

		Group inputGroup = new Group();
		inputGroup.setDescription("");
		inputGroup.setName("INPUT");
		inputGroup.setOrder("1");
		inputGroup.setType("INPUT");
		rtrTransformation.addGroup(inputGroup);

		Group insertGroup = new Group();
		insertGroup.setDescription("");
		insertGroup.setName("INSERT");
		insertGroup.setOrder("2");
		insertGroup.setType("OUTPUT");

		String expression = readExcelController.getCellValue(filePath, sheetName, 25, "BJ");
		insertGroup.setExpression(expression);
		rtrTransformation.addGroup(insertGroup);

		Group updateGroup = new Group();
		updateGroup.setDescription("");
		updateGroup.setName("UPDATE");
		updateGroup.setOrder("3");
		updateGroup.setType("OUTPUT");
		expression = readExcelController.getCellValue(filePath, sheetName, 26, "BJ");
		updateGroup.setExpression(expression);
		rtrTransformation.addGroup(updateGroup);

		Group renewGroup = new Group();
		renewGroup.setDescription("");
		renewGroup.setName("RENEW");
		renewGroup.setOrder("4");
		renewGroup.setType("OUTPUT");
		expression = readExcelController.getCellValue(filePath, sheetName, 27, "BJ");
		renewGroup.setExpression(expression);
		if(!expression.isEmpty()) {
		rtrTransformation.addGroup(renewGroup);
		}
		Group deafultGroup = new Group();
		deafultGroup.setDescription("Path for the data when none of the group conditions are satisfied.");
		deafultGroup.setName("DEFAULT1");
		deafultGroup.setOrder("5");
		deafultGroup.setType("OUTPUT/DEFAULT");
		rtrTransformation.addGroup(deafultGroup);
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation_LKP(filePath, "INPUT"));
		transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation(filePath, "INPUT"));
		transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation_LKP(filePath, "INSERT"));
		transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation(filePath, "INSERT"));
		transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation_LKP(filePath, "UPDATE"));
		transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation(filePath, "UPDATE"));
		if(!expression.isEmpty()) {
		transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation_LKP(filePath, "RENEW"));
		transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation(filePath, "RENEW"));
		}
		transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation_LKP(filePath, "DEFAULT1"));
		transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation(filePath, "DEFAULT1"));
		rtrTransformation.setTransformFieldList(transformFieldList);
		rtrTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(rtrTransformation);
		mapping.addTransformations(insUpdMappingController.getIUDTransformation(filePath));
		mapping.addInstance(insUpdMappingController.getInstances(filePath));
		mapping.addConnector(insUpdMappingController.conList);
		TargetLoader load = new TargetLoader();
		load.setOrder("1");
		load.setTargetInstance("sc_" + stg2TableName + "_INS");
		TargetLoader load1 = new TargetLoader();
		load1.setOrder("1");
		load1.setTargetInstance("sc_" + stg2TableName + "_UPD");
		mapping.addTargetLoader(load);
		mapping.addTargetLoader(load1);
		if(!expression.isEmpty()) {
		TargetLoader load2 = new TargetLoader();
		load2.setOrder("1");
		load2.setTargetInstance("sc_" + stg2TableName + "_REN");
		mapping.addTargetLoader(load2);
		}
		ErpInfo erpInfo = new ErpInfo();
		mapping.addErpInfo(erpInfo);
		notSharedFolder.addMapping(mapping);
		notSharedFolder.addShortcut(insUpdMappingController.getShortCut(filePath, cbuName, repoName));
		repository.addFolder(notSharedFolder);
		powerMart.addRepository(repository);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + mappingName + ".xml");
			StringWriter sw = new StringWriter();
			PrintWriter printWriter = new PrintWriter(sw);
			DataWriter dataWriter = new DataWriter(printWriter, "UTF-8", new JaxbCharacterEscapeHandler());
			// Perform Marshalling operation

			marshaller.marshal(powerMart, dataWriter);
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">\n"
					+ sw.toString());
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateStgProdMapping(String filePath) throws JAXBException {

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);

		// String filePath = "D:/ETL/ETL_Code_Generator-KMF.xlsx";
		String sheetName = "Mapping Details";
		String cbuName = "";
		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, sheetName, 1, "D");
		String repoName = readExcelController.getCellValue(filePath, sheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();
		if (folderName.startsWith("KMA")) {
			cbuName = "KMA";
		} else {
			cbuName = "HMA";
		}
		String dvTableName = readExcelController.getCellValue(filePath, sheetName, 23, "AP");
		String stg2TableName = readExcelController.getCellValue(filePath, sheetName, 23, "Z");
		Folder folder = new Folder(cbuName + "_SHARED", "", "INFA_ADMIN", SharedEnum.SHARED, "", "rwx------", uuid);
		folder.addSource(stgTwoProdController.getSourceForSharedFolder(filePath, cbuName));
		folder.addTarget(stgTwoProdController.getTargetPrdForSharedFolder(filePath));
		repository.addFolder(folder);

		String uuid1 = UUID.randomUUID().toString();

		Folder notSharedFolder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid1);

		Mapping mapping = new Mapping();
		mapping.setDescription("");
		mapping.setIsValid("YES");

		String mappingName = readExcelController.getCellValue(filePath, sheetName, 19, "D");
		mapping.setName(mappingName);
		mapping.setObjectVersion(1);
		mapping.setVersionNumber(1);
		Transformation sqTransformation = new Transformation();
		sqTransformation.setName("SQ_sc_" + stg2TableName);
		sqTransformation.setDescription("");
		sqTransformation.setObjectVersion("1");
		sqTransformation.setVersionNumber("1");
		sqTransformation.setReUsable("NO");
		sqTransformation.setType("Source Qualifier");
		sqTransformation.setTransformFieldList(stgTwoProdController.getTransformFieldSQTransformation(filePath));
		sqTransformation
				.setTableAttributeList(stgTwoProdController.getTableAttributesForSQTransformation(filePath, cbuName));
		mapping.addTransformation(sqTransformation);
		Transformation expTransformation = new Transformation();
		expTransformation.setName("EXPTRANS");
		expTransformation.setDescription("");
		expTransformation.setObjectVersion("1");
		expTransformation.setVersionNumber("1");
		expTransformation.setReUsable("NO");
		expTransformation.setType("Expression");
		expTransformation.setTransformFieldList(stgTwoProdController.getTransformFieldEXPTransformation(filePath));

		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");

		expTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(expTransformation);

		Transformation rtrTransformation = new Transformation();
		rtrTransformation.setName("RTRTRANS");
		rtrTransformation.setDescription("");
		rtrTransformation.setObjectVersion("1");
		rtrTransformation.setVersionNumber("1");
		rtrTransformation.setReUsable("NO");
		rtrTransformation.setType("Router");

		Group inputGroup = new Group();
		inputGroup.setDescription("");
		inputGroup.setName("INPUT");
		inputGroup.setOrder("1");
		inputGroup.setType("INPUT");
		rtrTransformation.addGroup(inputGroup);

		Group insertGroup = new Group();
		insertGroup.setDescription("");
		insertGroup.setName("INSERT");
		insertGroup.setOrder("2");
		insertGroup.setType("OUTPUT");

		String expression = readExcelController.getCellValue(filePath, sheetName, 37, "BJ");
		insertGroup.setExpression(expression);
		rtrTransformation.addGroup(insertGroup);

		Group updateGroup = new Group();
		updateGroup.setDescription("");
		updateGroup.setName("UPDATE");
		updateGroup.setOrder("3");
		updateGroup.setType("OUTPUT");
		expression = readExcelController.getCellValue(filePath, sheetName, 38, "BJ");
		updateGroup.setExpression(expression);
		rtrTransformation.addGroup(updateGroup);

		Group deafultGroup = new Group();
		deafultGroup.setDescription("Path for the data when none of the group conditions are satisfied.");
		deafultGroup.setName("DEFAULT1");
		deafultGroup.setOrder("5");
		deafultGroup.setType("OUTPUT/DEFAULT");
		rtrTransformation.addGroup(deafultGroup);
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		transformFieldList.addAll(stgTwoProdController.getTransformFieldRTRTransformation(filePath, "INPUT"));
		transformFieldList.addAll(stgTwoProdController.getTransformFieldRTRTransformation(filePath, "INSERT"));
		transformFieldList.addAll(stgTwoProdController.getTransformFieldRTRTransformation(filePath, "UPDATE"));
		transformFieldList.addAll(stgTwoProdController.getTransformFieldRTRTransformation(filePath, "DEFAULT1"));
		rtrTransformation.setTransformFieldList(transformFieldList);
		rtrTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(rtrTransformation);
		mapping.addTransformations(stgTwoProdController.getIUDTransformation(filePath));
		Transformation updTransformation = new Transformation();
		updTransformation.setName("UPDTRANS_UPD");
		updTransformation.setDescription("");
		updTransformation.setObjectVersion("1");
		updTransformation.setVersionNumber("1");
		updTransformation.setReUsable("NO");
		updTransformation.setType("Update Strategy");
		updTransformation.setTransformFieldList(stgTwoProdController.getupdTransformation(filePath));
		updTransformation.setTableAttributeList(stgTwoProdController.getTableAttributesForUpdTransformation(filePath));
		mapping.addTransformation(updTransformation);
		mapping.addInstance(stgTwoProdController.getInstances(filePath));
		mapping.addConnector(stgTwoProdController.conList);
		TargetLoader load = new TargetLoader();
		load.setOrder("1");
		load.setTargetInstance("sc_" + dvTableName + "_INS_REN");
		TargetLoader load1 = new TargetLoader();
		load1.setOrder("1");
		load1.setTargetInstance("sc_" + dvTableName + "_UPD_DEL");
		mapping.addTargetLoader(load);
		mapping.addTargetLoader(load1);
		MappingVariable mVariable = new MappingVariable();
		mVariable.setDataType("string");
		mVariable.setDefaultValue("");
		mVariable.setDescription("");
		mVariable.setIsExpressionVariable("NO");
		mVariable.setIsParam("YES");
		mVariable.setName("$$EFF_DATE");
		mVariable.setPrecision("2910");
		mVariable.setScale("0");
		mVariable.setUserDefined("YES");
		mapping.addMappingVariable(mVariable);
		ErpInfo erpInfo = new ErpInfo();
		mapping.addErpInfo(erpInfo);
		notSharedFolder.addMapping(mapping);
		notSharedFolder.addShortcut(stgTwoProdController.getShortCut(filePath, cbuName, repoName));
		repository.addFolder(notSharedFolder);
		powerMart.addRepository(repository);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + mappingName + ".xml");
			StringWriter sw = new StringWriter();
			PrintWriter printWriter = new PrintWriter(sw);
			DataWriter dataWriter = new DataWriter(printWriter, "UTF-8", new JaxbCharacterEscapeHandler());
			// Perform Marshalling operation

			marshaller.marshal(powerMart, dataWriter);
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">\n"
					+ sw.toString());
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void generateDeleteMapping(String filePath) throws JAXBException {
		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);

		String sheetName = "Mapping Details";
		String cbuName = "";
		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, sheetName, 1, "D");
		String repoName = readExcelController.getCellValue(filePath, sheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();
		if (folderName.startsWith("KMA")) {
			cbuName = "KMA";
		} else {
			cbuName = "HMA";
		}
		String dvTableName = readExcelController.getCellValue(filePath, sheetName, 23, "AP");
		String lookupTableName = readExcelController.getCellValue(filePath, sheetName, 24, "T");
		String connectionInfo = readExcelController.getCellValue(filePath, sheetName, 46, "BJ");
		String stg2TableName = readExcelController.getCellValue(filePath, sheetName, 23, "Z");
		Folder folder = new Folder(cbuName + "_SHARED", "", "INFA_ADMIN", SharedEnum.SHARED, "", "rwx------", uuid);
		Transformation transformation = new Transformation("", lookupTableName, "1", "YES", "Lookup Procedure", "1");
		transformation
				.setTransformFieldList(deleteMappingController.getTransformFieldForLookupTransformation(filePath));
		transformation.setTableAttributeList(deleteMappingController.getTableAttributesForLookupTransformation(filePath,
				lookupTableName, connectionInfo));
		folder.addTransformation(transformation);
		folder.addSource(deleteMappingController.getPrdSourceForSharedFolder(filePath, cbuName));
		folder.addTarget(deleteMappingController.getTargetForSharedFolder(filePath));
		repository.addFolder(folder);

		String uuid1 = UUID.randomUUID().toString();

		Folder notSharedFolder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid1);

		Mapping mapping = new Mapping();
		mapping.setDescription("");
		mapping.setIsValid("YES");

		String mappingName = readExcelController.getCellValue(filePath, sheetName, 14, "D");
		mapping.setName(mappingName);
		mapping.setObjectVersion(1);
		mapping.setVersionNumber(1);
		Transformation sqTransformation = new Transformation();
		sqTransformation.setName("SQ_sc_" + dvTableName);
		sqTransformation.setDescription("");
		sqTransformation.setObjectVersion("1");
		sqTransformation.setVersionNumber("1");
		sqTransformation.setReUsable("NO");
		sqTransformation.setType("Source Qualifier");
		sqTransformation.setTransformFieldList(deleteMappingController.getTransformFieldSQTransformation(filePath));
		sqTransformation.setTableAttributeList(
				deleteMappingController.getTableAttributesForSQTransformation(filePath, cbuName));
		mapping.addTransformation(sqTransformation);
		Transformation expTransformation = new Transformation();
		expTransformation.setName("EXPTRANS");
		expTransformation.setDescription("");
		expTransformation.setObjectVersion("1");
		expTransformation.setVersionNumber("1");
		expTransformation.setReUsable("NO");
		expTransformation.setType("Expression");
		expTransformation.setTransformFieldList(deleteMappingController.getTransformFieldEXPTransformation(filePath));

		TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setName("Tracing Level");
		tableAttribute.setValue("Normal");

		expTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(expTransformation);

		Transformation rtrTransformation = new Transformation();
		rtrTransformation.setName("RTRTRANS");
		rtrTransformation.setDescription("");
		rtrTransformation.setObjectVersion("1");
		rtrTransformation.setVersionNumber("1");
		rtrTransformation.setReUsable("NO");
		rtrTransformation.setType("Router");

		Group inputGroup = new Group();
		inputGroup.setDescription("");
		inputGroup.setName("INPUT");
		inputGroup.setOrder("1");
		inputGroup.setType("INPUT");
		rtrTransformation.addGroup(inputGroup);

		Group insertGroup = new Group();
		insertGroup.setDescription("");
		insertGroup.setName("DELETE");
		insertGroup.setOrder("2");
		insertGroup.setType("OUTPUT");

		String expression = readExcelController.getCellValue(filePath, sheetName, 32, "BJ");
		insertGroup.setExpression(expression);
		rtrTransformation.addGroup(insertGroup);

		Group deafultGroup = new Group();
		deafultGroup.setDescription("Path for the data when none of the group conditions are satisfied.");
		deafultGroup.setName("DEFAULT1");
		deafultGroup.setOrder("3");
		deafultGroup.setType("OUTPUT/DEFAULT");
		rtrTransformation.addGroup(deafultGroup);
		List<TransformField> transformFieldList = new ArrayList<TransformField>();
		transformFieldList.addAll(deleteMappingController.getTransformFieldRTRTransformation_LKP(filePath, "INPUT"));
		transformFieldList.addAll(deleteMappingController.getTransformFieldRTRTransformation(filePath, "INPUT"));
		transformFieldList.addAll(deleteMappingController.getTransformFieldRTRTransformation_LKP(filePath, "DELETE"));
		transformFieldList.addAll(deleteMappingController.getTransformFieldRTRTransformation(filePath, "DELETE"));
		transformFieldList.addAll(deleteMappingController.getTransformFieldRTRTransformation_LKP(filePath, "DEFAULT1"));
		transformFieldList.addAll(deleteMappingController.getTransformFieldRTRTransformation(filePath, "DEFAULT1"));
		rtrTransformation.setTransformFieldList(transformFieldList);
		rtrTransformation.addTableAttribute(tableAttribute);
		mapping.addTransformation(rtrTransformation);
		mapping.addTransformations(deleteMappingController.getIUDTransformation(filePath));
		mapping.addInstance(deleteMappingController.getInstances(filePath));
		mapping.addConnector(deleteMappingController.conList);
		TargetLoader load = new TargetLoader();
		load.setOrder("1");
		load.setTargetInstance("sc_" + stg2TableName + "_DEL");
		mapping.addTargetLoader(load);
		ErpInfo erpInfo = new ErpInfo();
		mapping.addErpInfo(erpInfo);
		notSharedFolder.addMapping(mapping);
		notSharedFolder.addShortcut(deleteMappingController.getShortCut(filePath, cbuName, repoName));
		repository.addFolder(notSharedFolder);
		powerMart.addRepository(repository);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + mappingName + ".xml");
			StringWriter sw = new StringWriter();
			PrintWriter printWriter = new PrintWriter(sw);
			DataWriter dataWriter = new DataWriter(printWriter, "UTF-8", new JaxbCharacterEscapeHandler());
			// Perform Marshalling operation

			marshaller.marshal(powerMart, dataWriter);
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">\n"
					+ sw.toString());
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}