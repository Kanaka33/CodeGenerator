package com.ETLCodeGen.controller;

import com.ETLCodeGen.model.*;
import com.ETLCodeGen.validator.JaxbCharacterEscapeHandler;
import com.sun.xml.internal.bind.marshaller.DataWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("xml/")
public class XmlGeneratorController {

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	@Autowired
	ReadExcelController readExcelController;

	@RequestMapping("sourceToStage1Session/{filePath}/{outputPath}")
	public void generateSourceToStage1XML(@PathVariable String filePath, @PathVariable String outputPath)
			throws JAXBException, IOException {
		String mappingSheetName = "Mapping Details";
		String sessionSheetName = "Session Properties";

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String repoName = readExcelController.getCellValue(filePath, mappingSheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();

		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, mappingSheetName, 1, "D");

		Folder folder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid);
		// folder.addConfig(sessionController.getDefaultSessionConfigForSourceToStage1(filePath,sessionSheetName));
		Config config = new Config("default_session_config", "Default session configuration object", "YES", "1");
		IntAttribute intconfigAtt = null;
		Attribute configAtt = null;
		for (int i = 13; i < 40; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "B");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "C");
			if (value.contains(".0")) {
				Integer precision = Integer.parseInt(value.replace(".0", ""));
				intconfigAtt = new IntAttribute(key, precision);
				config.addIntAttribute(intconfigAtt);
			} else {
				if(!key.isEmpty()){
				configAtt = new Attribute(key, value);
				config.addAttribute(configAtt);
			}
			}
		}

		folder.addConfig(config);
		String srcToStg1SessionName = readExcelController.getCellValue(filePath, mappingSheetName, 5, "D");
		String srcToStg1MappingName = readExcelController.getCellValue(filePath, mappingSheetName, 4, "D");
		Session session = new Session("", "YES", srcToStg1MappingName, srcToStg1SessionName, "YES", "Binary", "1");

		// Set Target Definition
		SessTransformationInst td_sessTransformationInst = new SessTransformationInst();
		td_sessTransformationInst.setTransformationType("Target Definition");
		td_sessTransformationInst.setStage("1");
		td_sessTransformationInst.setPipeline("1");
		td_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
		td_sessTransformationInst.setPartitionType("PASS THROUGH");

		String targetDefinitionName = "sc_" + readExcelController.getCellValue(filePath, mappingSheetName, 23, "J");
		td_sessTransformationInst.setsInstanceName(targetDefinitionName);
		td_sessTransformationInst.setTransformationName(targetDefinitionName);
		session.addSessTransformationInst(td_sessTransformationInst);

		// Set Source Definition
		SessTransformationInst sd_sessTransformationInst = new SessTransformationInst();
		sd_sessTransformationInst.setStage("0");
		sd_sessTransformationInst.setPipeline("0");
		sd_sessTransformationInst.setTransformationType("Source Definition");
		sd_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);

		String sourceDefinitionName = "sc_" + readExcelController.getCellValue(filePath, mappingSheetName, 23, "B");
		sd_sessTransformationInst.setsInstanceName(sourceDefinitionName);
		sd_sessTransformationInst.setTransformationName(sourceDefinitionName);

		String ownerName = readExcelController.getCellValue(filePath, sessionSheetName, 44, "C");
		Attribute attribute = new Attribute("Owner Name", ownerName);
		sd_sessTransformationInst.addAttribute(attribute);
		session.addSessTransformationInst(sd_sessTransformationInst);

		// Set Source Qualifier
		SessTransformationInst sq_sessTransformationInst = new SessTransformationInst();
		sq_sessTransformationInst.setTransformationType("Source Qualifier");
		sq_sessTransformationInst.setStage("2");
		sq_sessTransformationInst.setPipeline("1");
		sq_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
		sq_sessTransformationInst.setPartitionType("PASS THROUGH");

		String sqDefinitionName = "SQ_" + sourceDefinitionName;
		sq_sessTransformationInst.setsInstanceName(sqDefinitionName);
		sq_sessTransformationInst.setTransformationName(sqDefinitionName);
		session.addSessTransformationInst(sq_sessTransformationInst);

		// Set Expression
		SessTransformationInst exp_sessTransformationInst = new SessTransformationInst();
		exp_sessTransformationInst.setTransformationType("Expression");
		exp_sessTransformationInst.setStage("2");
		exp_sessTransformationInst.setPipeline("1");
		exp_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
		exp_sessTransformationInst.setsInstanceName("EXPTRANS");
		exp_sessTransformationInst.setTransformationName("EXPTRANS");

		Partition partition = new Partition("Partition #1", "");

		exp_sessTransformationInst.addPartition(partition);
		session.addSessTransformationInst(exp_sessTransformationInst);

		ConfigReference configReference = new ConfigReference("Session config", "default_session_config");
		session.setConfigReference(configReference);

		// Session Extension for TD

		SessionExtension td_sessionExtension = new SessionExtension();
		td_sessionExtension.setsInstanceName(targetDefinitionName);
		td_sessionExtension.setName("Relational Writer");
		td_sessionExtension.setSubtype("Relational Writer");
		td_sessionExtension.setTransformationType("Target Definition");
		td_sessionExtension.setType("WRITER");

		String targetConnectionName = readExcelController.getCellValue(filePath, sessionSheetName, 45, "C");

		ConnectionReference td_connectionReference = new ConnectionReference();
		td_connectionReference.setConnectionName(targetConnectionName);
		td_connectionReference.setCnxRefName("DB Connection");
		td_connectionReference.setConnectionSubType("Oracle");
		td_connectionReference.setConnectionType("Relational");
		td_connectionReference.setConnectionNumber("1");
		td_connectionReference.setVariable("");
		td_sessionExtension.addConnectionReference(td_connectionReference);
		Attribute attribute1 = null;
		Attribute ad = new Attribute("Target load type", "Normal");
		td_sessionExtension.addAttribute(ad);
		for (int i = 47; i < 54; i++) {
			attribute1 = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, i, "B"),
					readExcelController.getCellValue(filePath, sessionSheetName, i, "C"));
			td_sessionExtension.addAttribute(attribute1);
		}
		// td_sessionExtension.setAttributeList(sessionController.getSessionForSourceToStage1(filePath,sessionSheetName));
		Attribute ad1 = new Attribute("Reject filename", "sc_stg_cccmdp1.bad");
		td_sessionExtension.addAttribute(ad1);
		session.addSessionExtension(td_sessionExtension);

		// Session Extension for SD

		SessionExtension sd_sessionExtension = new SessionExtension();
		sd_sessionExtension.setsInstanceName(sourceDefinitionName);
		sd_sessionExtension.setTransformationType("Source Definition");
		sd_sessionExtension.setName("Relational Reader");
		sd_sessionExtension.setSubtype("Relational Reader");
		sd_sessionExtension.setType("READER");
		sd_sessionExtension.setdSQInstName(sqDefinitionName);
		sd_sessionExtension.setdSQInstType("Source Qualifier");

		session.addSessionExtension(sd_sessionExtension);

		// Session Extension for SQ

		SessionExtension sq_sessionExtension = new SessionExtension();
		sq_sessionExtension.setsInstanceName(sqDefinitionName);
		sq_sessionExtension.setTransformationType("Source Qualifier");
		sq_sessionExtension.setName("Relational Reader");
		sq_sessionExtension.setSubtype("Relational Reader");
		sq_sessionExtension.setType("READER");

		String sourceConnectionName = readExcelController.getCellValue(filePath, sessionSheetName, 43, "C");

		ConnectionReference sq_connectionReference = new ConnectionReference();
		sq_connectionReference.setConnectionName(sourceConnectionName);
		sq_connectionReference.setCnxRefName("DB Connection");
		sq_connectionReference.setConnectionSubType("PWX DB2i5OS");
		sq_connectionReference.setConnectionType("Relational");
		sq_connectionReference.setConnectionNumber("1");
		sq_connectionReference.setVariable("");
		sq_connectionReference.setComponentVersion("8005000");

		sq_sessionExtension.addConnectionReference(sq_connectionReference);

		session.addSessionExtension(sq_sessionExtension);

		// session.setAttributeList(sessionController.getMainSessionForSourceToStage1(filePath,sessionSheetName));
		Attribute att = null;
		Attribute att1 = new Attribute("General Options", "");
		Attribute att2 = new Attribute("Write Backward Compatible Session Log File", "NO");
		session.addAttribute(att1);
		session.addAttribute(att2);
		for (int i = 4; i < 11; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "B");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "C");
			if (value.contains(".0")) {

			} else {
				if(!key.isEmpty()){
				att = new Attribute(key, value);
				session.addAttribute(att);
				}
			}
		}
		Attribute a1 = new Attribute("Number of rows to test", "1");
		session.addAttribute(a1);
		Attribute a3 = new Attribute("Commit Interval", "10000");
		session.addAttribute(a3);
		Attribute at = new Attribute("DTM buffer size", "5000000000");
		session.addAttribute(at);
		Attribute att3 = new Attribute("Parameter Filename", "");
		Attribute att4 = new Attribute("Enable Test Load", "NO");
		Attribute att5 = new Attribute("$Source connection value", "");
		Attribute att6 = new Attribute("$Target connection value", "");
		Attribute att7 = new Attribute("Commit Type", "Target");
		Attribute att8 = new Attribute("Commit On End Of File", "YES");
		Attribute att9 = new Attribute("Rollback Transactions on Errors", "NO");
		Attribute att10 = new Attribute("Java Classpath", "");
		Attribute att11 = new Attribute("Performance", "");
		Attribute att12 = new Attribute("Collect performance data", "NO");
		Attribute att13 = new Attribute("Write performance data to repository", "NO");
		Attribute att14 = new Attribute("Incremental Aggregation", "NO");
		Attribute att15 = new Attribute("Enable high precision", "NO");
		Attribute att16 = new Attribute("Session retry on deadlock", "YES");
		Attribute att17 = new Attribute("Pushdown Optimization", "NONE");
		Attribute att18 = new Attribute("Allow Temporary View for Pushdown", "NO");
		Attribute att19 = new Attribute("Allow Temporary Sequence for Pushdown", "NO");
		Attribute att20 = new Attribute("Allow Pushdown for User Incompatible Connections", "NO");

		session.addAttribute(att3);
		session.addAttribute(att4);
		session.addAttribute(att5);
		session.addAttribute(att6);
		session.addAttribute(att7);
		session.addAttribute(att8);
		session.addAttribute(att9);
		session.addAttribute(att10);
		session.addAttribute(att11);
		session.addAttribute(att12);
		session.addAttribute(att13);
		session.addAttribute(att14);
		session.addAttribute(att15);
		session.addAttribute(att16);
		session.addAttribute(att17);
		session.addAttribute(att18);
		session.addAttribute(att19);
		session.addAttribute(att20);
		folder.addSession(session);
		repository.addFolder(folder);
		powerMart.addRepository(repository);

		// close workbook
		readExcelController.closeWorkBookForFile(filePath);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + srcToStg1SessionName + ".xml");
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

	@RequestMapping("sourceFileToStage1Session/{filePath}/{outputPath}")
	public void generateSourceFileToStage1XML(@PathVariable String filePath, @PathVariable String outputPath)
			throws JAXBException, IOException {
		String mappingSheetName = "Mapping Details";
		String sessionSheetName = "Session Properties";

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String repoName = readExcelController.getCellValue(filePath, mappingSheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();

		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, mappingSheetName, 1, "D");

		Folder folder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid);
		// folder.addConfig(sessionController.getDefaultSessionConfigForSourceToStage1(filePath,sessionSheetName));
		Config config = new Config("default_session_config", "Default session configuration object", "YES", "1");
		Attribute configAtt = null;
		IntAttribute intconfigAtt = null;
		for (int i = 13; i < 40; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "B");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "C");
			if (value.contains(".0")) {
				Integer precision = Integer.parseInt(value.replace(".0", ""));
				intconfigAtt = new IntAttribute(key, precision);
				config.addIntAttribute(intconfigAtt);
			} else {
				if(!key.isEmpty()){
				configAtt = new Attribute(key, value);
				config.addAttribute(configAtt);
			}
			}
		}

		folder.addConfig(config);

		String srcToStg1SessionName = readExcelController.getCellValue(filePath, mappingSheetName, 5, "D");
		String srcToStg1MappingName = readExcelController.getCellValue(filePath, mappingSheetName, 4, "D");
		Session session = new Session("", "YES", srcToStg1MappingName, srcToStg1SessionName, "YES", "Binary", "1");

		// Set Target Definition
		SessTransformationInst td_sessTransformationInst = new SessTransformationInst();
		td_sessTransformationInst.setTransformationType("Target Definition");
		td_sessTransformationInst.setStage("1");
		td_sessTransformationInst.setPipeline("1");
		td_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
		td_sessTransformationInst.setPartitionType("PASS THROUGH");

		String targetDefinitionName = "sc_" + readExcelController.getCellValue(filePath, mappingSheetName, 23, "J");
		td_sessTransformationInst.setsInstanceName(targetDefinitionName);
		td_sessTransformationInst.setTransformationName(targetDefinitionName);

		Attribute attribute1 = new Attribute("Post SQL", "");
		td_sessTransformationInst.addAttribute(attribute1);
		session.addSessTransformationInst(td_sessTransformationInst);

		// Set Source Definition
		SessTransformationInst sd_sessTransformationInst = new SessTransformationInst();
		sd_sessTransformationInst.setStage("0");
		sd_sessTransformationInst.setPipeline("0");
		sd_sessTransformationInst.setTransformationType("Source Definition");
		sd_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);

		String sourceDefinitionName = "sc_" + readExcelController.getCellValue(filePath, mappingSheetName, 23, "B");
		sd_sessTransformationInst.setsInstanceName(sourceDefinitionName);
		sd_sessTransformationInst.setTransformationName(sourceDefinitionName);
		FlatFile file = new FlatFile();
		file.setCodePage("Latin1");
		file.setConsecDelimitersAsOne("NO");
		file.setDelimited("YES");
		file.setDelimiters("|");
		file.setEscapeCharacter("");
		file.setKeepEscapeChar("NO");
		file.setMultidelimiters("NO");
		file.setNullCharType("ASCII");
		file.setNullCharacter("*");
		file.setPadBytes("1");
		file.setQuoteCharacter("NONE");
		file.setRepeatable("NO");
		file.setRowDelimeter("10");
		file.setSkipRows("0");
		file.setStripTrailingBlanks("NO");
		sd_sessTransformationInst.addFile(file);
		session.addSessTransformationInst(sd_sessTransformationInst);

		// Set Source Qualifier
		SessTransformationInst sq_sessTransformationInst = new SessTransformationInst();
		sq_sessTransformationInst.setTransformationType("Source Qualifier");
		sq_sessTransformationInst.setStage("2");
		sq_sessTransformationInst.setPipeline("1");
		sq_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
		sq_sessTransformationInst.setPartitionType("PASS THROUGH");

		String sqDefinitionName = "SQ_" + sourceDefinitionName;
		sq_sessTransformationInst.setsInstanceName(sqDefinitionName);
		sq_sessTransformationInst.setTransformationName(sqDefinitionName);
		session.addSessTransformationInst(sq_sessTransformationInst);

		// Set Expression
		SessTransformationInst exp_sessTransformationInst = new SessTransformationInst();
		exp_sessTransformationInst.setTransformationType("Expression");
		exp_sessTransformationInst.setStage("2");
		exp_sessTransformationInst.setPipeline("1");
		exp_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
		exp_sessTransformationInst.setsInstanceName("EXPTRANS");
		exp_sessTransformationInst.setTransformationName("EXPTRANS");

		Partition partition = new Partition("Partition #1", "");

		exp_sessTransformationInst.addPartition(partition);
		session.addSessTransformationInst(exp_sessTransformationInst);

		ConfigReference configReference = new ConfigReference("Session config", "default_session_config");
		session.setConfigReference(configReference);

		// Session Extension for TD

		SessionExtension td_sessionExtension = new SessionExtension();
		td_sessionExtension.setsInstanceName(targetDefinitionName);
		td_sessionExtension.setName("Relational Writer");
		td_sessionExtension.setSubtype("Relational Writer");
		td_sessionExtension.setTransformationType("Target Definition");
		td_sessionExtension.setType("WRITER");

		String targetConnectionName = readExcelController.getCellValue(filePath, sessionSheetName, 45, "C");

		ConnectionReference td_connectionReference = new ConnectionReference();
		td_connectionReference.setConnectionName(targetConnectionName);
		td_connectionReference.setCnxRefName("DB Connection");
		td_connectionReference.setConnectionSubType("Oracle");
		td_connectionReference.setConnectionType("Relational");
		td_connectionReference.setConnectionNumber("1");
		td_connectionReference.setVariable("");
		td_sessionExtension.addConnectionReference(td_connectionReference);
		Attribute attribute = null;
		Attribute ad = new Attribute("Target load type", "Normal");
		td_sessionExtension.addAttribute(ad);
		for (int i = 47; i < 54; i++) {
			attribute = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, i, "B"),
					readExcelController.getCellValue(filePath, sessionSheetName, i, "C"));
			td_sessionExtension.addAttribute(attribute);
		}
		// td_sessionExtension.setAttributeList(sessionController.getSessionForSourceToStage1(filePath,sessionSheetName));
		Attribute ad1 = new Attribute("Reject filename", "sc_stg_cccmdp1.bad");
		td_sessionExtension.addAttribute(ad1);
		session.addSessionExtension(td_sessionExtension);

		// Session Extension for SD

		SessionExtension sd_sessionExtension = new SessionExtension();
		sd_sessionExtension.setdSQInstName(sqDefinitionName);
		sd_sessionExtension.setdSQInstType("Source Qualifier");
		sd_sessionExtension.setName("File Reader");
		sd_sessionExtension.setsInstanceName(sourceDefinitionName);
		sd_sessionExtension.setSubtype("File Reader");
		sd_sessionExtension.setTransformationType("Source Definition");
		sd_sessionExtension.setType("READER");

		ConnectionReference sd_connectionReference = new ConnectionReference();
		sd_connectionReference.setCnxRefName("Connection");
		sd_connectionReference.setConnectionName("");
		sd_connectionReference.setConnectionNumber("1");
		sd_connectionReference.setConnectionSubType("");
		sd_connectionReference.setConnectionType("");
		sd_connectionReference.setVariable("");

		sd_sessionExtension.addConnectionReference(sd_connectionReference);
		Attribute ab = new Attribute("Input Type", "File");
		sd_sessionExtension.addAttribute(ab);
		Attribute ab1 = new Attribute("Concurrent read partitioning", "Optimize throughput");
		sd_sessionExtension.addAttribute(ab1);
		Attribute ab2 = new Attribute("Command Type", "Command Generating Data");
		sd_sessionExtension.addAttribute(ab2);
		Attribute ab3 = new Attribute("Source filetype",
				readExcelController.getCellValue(filePath, sessionSheetName, 56, "C"));
		sd_sessionExtension.addAttribute(ab3);
		Attribute ab4 = new Attribute("Source file directory",
				readExcelController.getCellValue(filePath, sessionSheetName, 54, "C"));
		sd_sessionExtension.addAttribute(ab4);
		Attribute ab5 = new Attribute("Source filename",
				readExcelController.getCellValue(filePath, sessionSheetName, 55, "C"));
		sd_sessionExtension.addAttribute(ab5);
		Attribute ab6 = new Attribute("Command", "");
		sd_sessionExtension.addAttribute(ab6);
		Attribute ab7 = new Attribute("File Reader Truncate String Null", "NO");
		sd_sessionExtension.addAttribute(ab7);
		Attribute ab8 = new Attribute("Codepage Parameter", "");
		sd_sessionExtension.addAttribute(ab8);
		session.addSessionExtension(sd_sessionExtension);

		// Session Extension for SQ

		SessionExtension sq_sessionExtension = new SessionExtension();
		sq_sessionExtension.setsInstanceName(sqDefinitionName);
		sq_sessionExtension.setTransformationType("Source Qualifier");
		sq_sessionExtension.setName("File Reader");
		sq_sessionExtension.setSubtype("File Reader");
		sq_sessionExtension.setType("READER");

		session.addSessionExtension(sq_sessionExtension);

		// session.setAttributeList(sessionController.getMainSessionForSourceToStage1(filePath,sessionSheetName));
		Attribute att = null;
		Attribute att1 = new Attribute("General Options", "");
		Attribute att2 = new Attribute("Write Backward Compatible Session Log File", "NO");
		session.addAttribute(att1);
		session.addAttribute(att2);
		for (int i = 4; i < 11; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "B");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "C");
			if (value.contains(".0")) {

			} else {
				if(!key.isEmpty()){
				att = new Attribute(key, value);
				session.addAttribute(att);
				}
			}
		}
		Attribute a1 = new Attribute("Number of rows to test", "1");
		session.addAttribute(a1);
		Attribute a3 = new Attribute("Commit Interval", "10000");
		session.addAttribute(a3);
		Attribute at = new Attribute("DTM buffer size", "5000000000");
		session.addAttribute(at);
		Attribute att3 = new Attribute("Parameter Filename", "");
		Attribute att4 = new Attribute("Enable Test Load", "NO");
		Attribute att5 = new Attribute("$Source connection value", "");
		Attribute att6 = new Attribute("$Target connection value", "");
		Attribute att7 = new Attribute("Commit Type", "Target");
		Attribute att8 = new Attribute("Commit On End Of File", "YES");
		Attribute att9 = new Attribute("Rollback Transactions on Errors", "NO");
		Attribute att10 = new Attribute("Java Classpath", "");
		Attribute att11 = new Attribute("Performance", "");
		Attribute att12 = new Attribute("Collect performance data", "NO");
		Attribute att13 = new Attribute("Write performance data to repository", "NO");
		Attribute att14 = new Attribute("Incremental Aggregation", "NO");
		Attribute att15 = new Attribute("Enable high precision", "NO");
		Attribute att16 = new Attribute("Session retry on deadlock", "YES");
		Attribute att17 = new Attribute("Pushdown Optimization", "NONE");
		Attribute att18 = new Attribute("Allow Temporary View for Pushdown", "NO");
		Attribute att19 = new Attribute("Allow Temporary Sequence for Pushdown", "NO");
		Attribute att20 = new Attribute("Allow Pushdown for User Incompatible Connections", "NO");

		session.addAttribute(att3);
		session.addAttribute(att4);
		session.addAttribute(att5);
		session.addAttribute(att6);
		session.addAttribute(att7);
		session.addAttribute(att8);
		session.addAttribute(att9);
		session.addAttribute(att10);
		session.addAttribute(att11);
		session.addAttribute(att12);
		session.addAttribute(att13);
		session.addAttribute(att14);
		session.addAttribute(att15);
		session.addAttribute(att16);
		session.addAttribute(att17);
		session.addAttribute(att18);
		session.addAttribute(att19);
		session.addAttribute(att20);
		folder.addSession(session);
		repository.addFolder(folder);
		powerMart.addRepository(repository);
		// close workbook
		readExcelController.closeWorkBookForFile(filePath);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + srcToStg1SessionName + ".xml");
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
	
	@RequestMapping("stageToODSSession/{filePath}/{outputPath}")
	public void generateStageToODSXML(@PathVariable String filePath, @PathVariable String outputPath)
			throws JAXBException, IOException {
		String mappingSheetName = "Mapping Details";
		String sessionSheetName = "Session Properties";

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String repoName = readExcelController.getCellValue(filePath, mappingSheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();

		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, mappingSheetName, 1, "D");

		Folder folder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid);
		// folder.addConfig(sessionController.getDefaultSessionConfigForSourceToStage1(filePath,sessionSheetName));
		Config config = new Config("default_session_config", "Default session configuration object", "YES", "1");
		IntAttribute intconfigAtt = null;
		Attribute configAtt = null;
		for (int i = 14; i < 41; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "N");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "O");
			if (value.contains(".0")) {
				Integer precision = Integer.parseInt(value.replace(".0", ""));
				intconfigAtt = new IntAttribute(key, precision);
				config.addIntAttribute(intconfigAtt);
			} else {
				if(!key.isEmpty()){
				configAtt = new Attribute(key, value);
				config.addAttribute(configAtt);
			}
			}
		}

		folder.addConfig(config);
		String srcToStg1SessionName = readExcelController.getCellValue(filePath, mappingSheetName, 5, "K");
		String srcToStg1MappingName = readExcelController.getCellValue(filePath, mappingSheetName, 4, "K");
		Session session = new Session("", "YES", srcToStg1MappingName, srcToStg1SessionName, "YES", "Binary", "1");

		int emptyCount = 0;
		// Set Target Definition
		for (int i = 58;; i++) {
			String key = (readExcelController.getCellValue(filePath, sessionSheetName, i, "N"));
			String value = (readExcelController.getCellValue(filePath, sessionSheetName, i, "O"));
			if(emptyCount>2)break;
		if(key.isEmpty()){
			emptyCount++;
		}else {
			if(value.equalsIgnoreCase("Target Definition")) {
				//ins
		SessTransformationInst td_sessTransformationInst = new SessTransformationInst();
		td_sessTransformationInst.setTransformationType(value);
		td_sessTransformationInst.setStage("3");
		td_sessTransformationInst.setPipeline("1");
		td_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
		td_sessTransformationInst.setPartitionType("PASS THROUGH");
		td_sessTransformationInst.setsInstanceName("sc_"+key+"_INS");
		td_sessTransformationInst.setTransformationName("sc_"+key+"_INS");

		session.addSessTransformationInst(td_sessTransformationInst);
		//upd
		SessTransformationInst td_sessTransformationInst1 = new SessTransformationInst();
		td_sessTransformationInst1.setTransformationType(value);
		td_sessTransformationInst1.setStage("4");
		td_sessTransformationInst1.setPipeline("1");
		td_sessTransformationInst1.setIsRepartitionPoint(YesNoEnum.YES);
		td_sessTransformationInst1.setPartitionType("PASS THROUGH");
		td_sessTransformationInst1.setsInstanceName("sc_"+key+"_UPD");
		td_sessTransformationInst1.setTransformationName("sc_"+key+"_UPD");

		session.addSessTransformationInst(td_sessTransformationInst1);
			}else if(value.equalsIgnoreCase("Source Definition")) {

		// Set Source Definition
		SessTransformationInst sd_sessTransformationInst = new SessTransformationInst();
		sd_sessTransformationInst.setStage("0");
		sd_sessTransformationInst.setPipeline("0");
		sd_sessTransformationInst.setTransformationType(value);
		sd_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
		sd_sessTransformationInst.setsInstanceName(key);
		sd_sessTransformationInst.setTransformationName(key);

		/*String ownerName = readExcelController.getCellValue(filePath, sessionSheetName, 42, "O");
		Attribute attribute = new Attribute("Owner Name", ownerName);
		sd_sessTransformationInst.addAttribute(attribute);*/
		session.addSessTransformationInst(sd_sessTransformationInst);
			}else if(value.equalsIgnoreCase("Source Qualifier")){
		// Set Source Qualifier
		SessTransformationInst sq_sessTransformationInst = new SessTransformationInst();
		sq_sessTransformationInst.setTransformationType(value);
		sq_sessTransformationInst.setStage("5");
		sq_sessTransformationInst.setPipeline("1");
		sq_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
		sq_sessTransformationInst.setPartitionType("PASS THROUGH");

		sq_sessTransformationInst.setsInstanceName(key);
		sq_sessTransformationInst.setTransformationName(key);
		session.addSessTransformationInst(sq_sessTransformationInst);
		} else if(value.equalsIgnoreCase("Expression") || value.equalsIgnoreCase("Router") || value.equalsIgnoreCase("Update Strategy")){
		// Set Expression
		SessTransformationInst exp_sessTransformationInst = new SessTransformationInst();
		exp_sessTransformationInst.setTransformationType(value);
		exp_sessTransformationInst.setStage("5");
		exp_sessTransformationInst.setPipeline("1");
		exp_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
		exp_sessTransformationInst.setsInstanceName(key);
		exp_sessTransformationInst.setTransformationName(key);

		Partition partition = new Partition("Partition #1", "");

		exp_sessTransformationInst.addPartition(partition);
		session.addSessTransformationInst(exp_sessTransformationInst);
		} else if(value.equalsIgnoreCase("Lookup Procedure") || value.equalsIgnoreCase("Stored Procedure")) {
			SessTransformationInst exp_sessTransformationInst = new SessTransformationInst();
			exp_sessTransformationInst.setTransformationType(value);
			exp_sessTransformationInst.setStage("5");
			exp_sessTransformationInst.setPipeline("1");
			exp_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
			exp_sessTransformationInst.setsInstanceName("sc_"+key);
			exp_sessTransformationInst.setTransformationName("sc_"+key);

			Partition partition = new Partition("Partition #1", "");

			exp_sessTransformationInst.addPartition(partition);
			session.addSessTransformationInst(exp_sessTransformationInst);
		}
		}
		}
		ConfigReference configReference = new ConfigReference("Session config", "default_session_config");
		session.setConfigReference(configReference);

		emptyCount = 0;
		for (int i = 58;; i++) {
			String key = (readExcelController.getCellValue(filePath, sessionSheetName, i, "N"));
			String value = (readExcelController.getCellValue(filePath, sessionSheetName, i, "O"));
			if(emptyCount>2)break;
		if(readExcelController.getCellValue(filePath, sessionSheetName, i, "N").isEmpty()) {
			emptyCount++;
		}else {
			if(value.equalsIgnoreCase("Target Definition")) {
				// Session Extension for TD
				//ins
		SessionExtension td_sessionExtension = new SessionExtension();
		td_sessionExtension.setsInstanceName("sc_" + key + "_INS");
		td_sessionExtension.setName("Relational Writer");
		td_sessionExtension.setSubtype("Relational Writer");
		td_sessionExtension.setTransformationType("Target Definition");
		td_sessionExtension.setType("WRITER");

		String targetConnectionName = readExcelController.getCellValue(filePath, sessionSheetName, 44, "O");

		ConnectionReference td_connectionReference = new ConnectionReference();
		td_connectionReference.setConnectionName(targetConnectionName);
		td_connectionReference.setCnxRefName("DB Connection");
		td_connectionReference.setConnectionSubType("Oracle");
		td_connectionReference.setConnectionType("Relational");
		td_connectionReference.setConnectionNumber("1");
		td_connectionReference.setVariable("");
		td_sessionExtension.addConnectionReference(td_connectionReference);
		Attribute attribute1 = null;
		Attribute ad = new Attribute("Target load type", "Normal");
		td_sessionExtension.addAttribute(ad);
		for (int j = 46; j < 53; j++) {
			attribute1 = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, j, "N"),
					readExcelController.getCellValue(filePath, sessionSheetName, j, "O"));
			if(readExcelController.getCellValue(filePath, sessionSheetName, j, "N").equalsIgnoreCase("Update as Update")) {
				attribute1 = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, j, "N"), "NO");
			}
			td_sessionExtension.addAttribute(attribute1);
		}
		Attribute ad1 = new Attribute("Reject filename", "sc_"+key.toLowerCase()+"_ins1.bad");
		td_sessionExtension.addAttribute(ad1);
		session.addSessionExtension(td_sessionExtension);
		SessionExtension td_sessionExtension1 = new SessionExtension();
		td_sessionExtension1.setsInstanceName("sc_" + key + "_UPD");
		td_sessionExtension1.setName("Relational Writer");
		td_sessionExtension1.setSubtype("Relational Writer");
		td_sessionExtension1.setTransformationType("Target Definition");
		td_sessionExtension1.setType("WRITER");

		ConnectionReference td_connectionReference1 = new ConnectionReference();
		td_connectionReference1.setConnectionName(targetConnectionName);
		td_connectionReference1.setCnxRefName("DB Connection");
		td_connectionReference1.setConnectionSubType("Oracle");
		td_connectionReference1.setConnectionType("Relational");
		td_connectionReference1.setConnectionNumber("1");
		td_connectionReference1.setVariable("");
		td_sessionExtension1.addConnectionReference(td_connectionReference1);
		Attribute attribute1_1 = null;
		Attribute ad_1 = new Attribute("Target load type", "Normal");
		td_sessionExtension1.addAttribute(ad_1);
		for (int j = 46; j < 53; j++) {
			attribute1_1 = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, j, "N"),
					readExcelController.getCellValue(filePath, sessionSheetName, j, "O"));
			if(readExcelController.getCellValue(filePath, sessionSheetName, j, "N").equalsIgnoreCase("Insert")) {
				attribute1_1 = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, j, "N"), "NO");
			}
			td_sessionExtension1.addAttribute(attribute1_1);
		}
		Attribute ad1_1 = new Attribute("Reject filename", "sc_"+key.toLowerCase()+"_upd1.bad");
		td_sessionExtension1.addAttribute(ad1_1);
		session.addSessionExtension(td_sessionExtension1);
		} else if(value.equalsIgnoreCase("Source Definition")) {
		// Session Extension for SD

		SessionExtension sd_sessionExtension = new SessionExtension();
		sd_sessionExtension.setsInstanceName(key);
		sd_sessionExtension.setTransformationType(value);
		sd_sessionExtension.setName("Relational Reader");
		sd_sessionExtension.setSubtype("Relational Reader");
		sd_sessionExtension.setType("READER");
		sd_sessionExtension.setdSQInstName("SQ_"+key);
		sd_sessionExtension.setdSQInstType("Source Qualifier");

		session.addSessionExtension(sd_sessionExtension);
		} else if(value.equalsIgnoreCase("Source Qualifier")) {
		// Session Extension for SQ

		SessionExtension sq_sessionExtension = new SessionExtension();
		sq_sessionExtension.setsInstanceName(key);
		sq_sessionExtension.setTransformationType(value);
		sq_sessionExtension.setName("Relational Reader");
		sq_sessionExtension.setSubtype("Relational Reader");
		sq_sessionExtension.setType("READER");

		String sourceConnectionName = readExcelController.getCellValue(filePath, sessionSheetName, 43, "O");

		ConnectionReference sq_connectionReference = new ConnectionReference();
		sq_connectionReference.setConnectionName(sourceConnectionName);
		sq_connectionReference.setCnxRefName("DB Connection");
		sq_connectionReference.setConnectionSubType("PWX DB2i5OS");
		sq_connectionReference.setConnectionType("Relational");
		sq_connectionReference.setConnectionNumber("1");
		sq_connectionReference.setVariable("");
		sq_connectionReference.setComponentVersion("8005000");

		sq_sessionExtension.addConnectionReference(sq_connectionReference);

		session.addSessionExtension(sq_sessionExtension);
		} else if (value.equalsIgnoreCase("Lookup Procedure")) {
				SessionExtension lk_sessionExtension = new SessionExtension();
				lk_sessionExtension.setName("Relational Lookup");
				lk_sessionExtension.setSubtype("Relational Lookup");
				lk_sessionExtension.setsInstanceName("sc_"+key);
				lk_sessionExtension.setTransformationType(value);
				lk_sessionExtension.setType("LOOKUPEXTENSION");
				ConnectionReference lk_connectionReference = new ConnectionReference();
				lk_connectionReference.setCnxRefName("DB Connection");
				lk_connectionReference
						.setConnectionName(readExcelController.getCellValue(filePath, sessionSheetName, i, "R"));
				lk_connectionReference.setConnectionNumber("1");
				lk_connectionReference.setConnectionSubType("Oracle");
				lk_connectionReference.setConnectionType("Relational");
				lk_connectionReference.setVariable("");
				lk_sessionExtension.addConnectionReference(lk_connectionReference);
				session.addSessionExtension(lk_sessionExtension);
		} 
		}
		}
		// session.setAttributeList(sessionController.getMainSessionForSourceToStage1(filePath,sessionSheetName));
		Attribute att = null;
		Attribute att1 = new Attribute("General Options", "");
		Attribute att2 = new Attribute("Write Backward Compatible Session Log File", "NO");
		session.addAttribute(att1);
		session.addAttribute(att2);
		for (int i = 4; i < 12; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "N");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "O");
			if (value.contains(".0")) {

			} else {
				if(!key.isEmpty()){
				att = new Attribute(key, value);
				session.addAttribute(att);
				}
			}
		}
		Attribute a1 = new Attribute("Number of rows to test", "1");
		session.addAttribute(a1);
		Attribute a3 = new Attribute("Commit Interval", "10000");
		session.addAttribute(a3);
		Attribute at = new Attribute("DTM buffer size", "5000000000");
		session.addAttribute(at);
		Attribute att3 = new Attribute("Parameter Filename", "");
		Attribute att4 = new Attribute("Enable Test Load", "NO");
		Attribute att5 = new Attribute("$Source connection value", "");
		Attribute att6 = new Attribute("$Target connection value", "");
		Attribute att7 = new Attribute("Commit Type", "Target");
		Attribute att8 = new Attribute("Commit On End Of File", "YES");
		Attribute att9 = new Attribute("Rollback Transactions on Errors", "NO");
		Attribute att10 = new Attribute("Java Classpath", "");
		Attribute att11 = new Attribute("Performance", "");
		Attribute att12 = new Attribute("Collect performance data", "NO");
		Attribute att13 = new Attribute("Write performance data to repository", "NO");
		Attribute att14 = new Attribute("Incremental Aggregation", "NO");
		Attribute att15 = new Attribute("Enable high precision", "NO");
		Attribute att16 = new Attribute("Session retry on deadlock", "YES");
		Attribute att17 = new Attribute("Pushdown Optimization", "NONE");
		Attribute att18 = new Attribute("Allow Temporary View for Pushdown", "NO");
		Attribute att19 = new Attribute("Allow Temporary Sequence for Pushdown", "NO");
		Attribute att20 = new Attribute("Allow Pushdown for User Incompatible Connections", "NO");

		session.addAttribute(att3);
		session.addAttribute(att4);
		session.addAttribute(att5);
		session.addAttribute(att6);
		session.addAttribute(att7);
		session.addAttribute(att8);
		session.addAttribute(att9);
		session.addAttribute(att10);
		session.addAttribute(att11);
		session.addAttribute(att12);
		session.addAttribute(att13);
		session.addAttribute(att14);
		session.addAttribute(att15);
		session.addAttribute(att16);
		session.addAttribute(att17);
		session.addAttribute(att18);
		session.addAttribute(att19);
		session.addAttribute(att20);
		folder.addSession(session);
		repository.addFolder(folder);
		powerMart.addRepository(repository);

		// close workbook
		readExcelController.closeWorkBookForFile(filePath);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + srcToStg1SessionName + ".xml");
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
	
	@RequestMapping("sourceFileToODSSession/{filePath}/{outputPath}")
	public void generateSourceFileToODSXML(@PathVariable String filePath, @PathVariable String outputPath)
			throws JAXBException, IOException {
		String mappingSheetName = "Mapping Details";
		String sessionSheetName = "Session Properties";

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String repoName = readExcelController.getCellValue(filePath, mappingSheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();

		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, mappingSheetName, 1, "D");

		Folder folder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid);
		// folder.addConfig(sessionController.getDefaultSessionConfigForSourceToStage1(filePath,sessionSheetName));
		Config config = new Config("default_session_config", "Default session configuration object", "YES", "1");
		Attribute configAtt = null;
		IntAttribute intconfigAtt = null;
		for (int i = 14; i < 41; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "N");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "O");
			if (value.contains(".0")) {
				Integer precision = Integer.parseInt(value.replace(".0", ""));
				intconfigAtt = new IntAttribute(key, precision);
				config.addIntAttribute(intconfigAtt);
			} else {
				configAtt = new Attribute(key, value);
				config.addAttribute(configAtt);
			}

		}

		folder.addConfig(config);

		String srcToODSSessionName = readExcelController.getCellValue(filePath, mappingSheetName, 5, "K");
		String srcToODSMappingName = readExcelController.getCellValue(filePath, mappingSheetName, 4, "K");
		Session session = new Session("", "YES", srcToODSMappingName, srcToODSSessionName, "YES", "Binary", "1");
		int emptyCount = 0;
		// Set Target Definition
		for (int i = 58;; i++) {
			String key = (readExcelController.getCellValue(filePath, sessionSheetName, i, "N"));
			String value = (readExcelController.getCellValue(filePath, sessionSheetName, i, "O"));
			if(emptyCount>2)break;
		if(readExcelController.getCellValue(filePath, sessionSheetName, i, "N").isEmpty()) {
			emptyCount++;
		}else {
			if(value.equalsIgnoreCase("Target Definition")) {
				//ins
		SessTransformationInst td_sessTransformationInst = new SessTransformationInst();
		td_sessTransformationInst.setTransformationType(value);
		td_sessTransformationInst.setStage("3");
		td_sessTransformationInst.setPipeline("1");
		td_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
		td_sessTransformationInst.setPartitionType("PASS THROUGH");
		td_sessTransformationInst.setsInstanceName("sc_"+key+"_INS");
		td_sessTransformationInst.setTransformationName("sc_"+key+"_INS");

		session.addSessTransformationInst(td_sessTransformationInst);
		//upd
		SessTransformationInst td_sessTransformationInst1 = new SessTransformationInst();
		td_sessTransformationInst1.setTransformationType(value);
		td_sessTransformationInst1.setStage("4");
		td_sessTransformationInst1.setPipeline("1");
		td_sessTransformationInst1.setIsRepartitionPoint(YesNoEnum.YES);
		td_sessTransformationInst1.setPartitionType("PASS THROUGH");
		td_sessTransformationInst1.setsInstanceName("sc_"+key+"_UPD");
		td_sessTransformationInst1.setTransformationName("sc_"+key+"_UPD");

		session.addSessTransformationInst(td_sessTransformationInst1);
			}else if(value.equalsIgnoreCase("Source Definition")) {
		// Set Source Definition
		SessTransformationInst sd_sessTransformationInst = new SessTransformationInst();
		sd_sessTransformationInst.setStage("0");
		sd_sessTransformationInst.setPipeline("0");
		sd_sessTransformationInst.setTransformationType("Source Definition");
		sd_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);

		sd_sessTransformationInst.setsInstanceName(key);
		sd_sessTransformationInst.setTransformationName(key);
		FlatFile file = new FlatFile();
		file.setCodePage("Latin1");
		file.setConsecDelimitersAsOne("NO");
		file.setDelimited("YES");
		file.setDelimiters("|");
		file.setEscapeCharacter("");
		file.setKeepEscapeChar("NO");
		file.setMultidelimiters("NO");
		file.setNullCharType("ASCII");
		file.setNullCharacter("*");
		file.setPadBytes("1");
		file.setQuoteCharacter("NONE");
		file.setRepeatable("NO");
		file.setRowDelimeter("10");
		file.setSkipRows("0");
		file.setStripTrailingBlanks("NO");
		sd_sessTransformationInst.addFile(file);
		session.addSessTransformationInst(sd_sessTransformationInst);
			}else if(value.equalsIgnoreCase("Source Qualifier")){
		// Set Source Qualifier
		SessTransformationInst sq_sessTransformationInst = new SessTransformationInst();
		sq_sessTransformationInst.setTransformationType("Source Qualifier");
		sq_sessTransformationInst.setStage("5");
		sq_sessTransformationInst.setPipeline("1");
		sq_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
		sq_sessTransformationInst.setPartitionType("PASS THROUGH");

		sq_sessTransformationInst.setsInstanceName(key);
		sq_sessTransformationInst.setTransformationName(key);
		session.addSessTransformationInst(sq_sessTransformationInst);
			}
			else if(value.equalsIgnoreCase("Expression") || value.equalsIgnoreCase("Router") || value.equalsIgnoreCase("Update Strategy")){
		// Set Expression
		SessTransformationInst exp_sessTransformationInst = new SessTransformationInst();
		exp_sessTransformationInst.setTransformationType(value);
		exp_sessTransformationInst.setStage("5");
		exp_sessTransformationInst.setPipeline("1");
		exp_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
		exp_sessTransformationInst.setsInstanceName(key);
		exp_sessTransformationInst.setTransformationName(key);

		Partition partition = new Partition("Partition #1", "");

		exp_sessTransformationInst.addPartition(partition);
		session.addSessTransformationInst(exp_sessTransformationInst);
			} else if(value.equalsIgnoreCase("Lookup Procedure") || value.equalsIgnoreCase("Stored Procedure")) {
				SessTransformationInst exp_sessTransformationInst = new SessTransformationInst();
				exp_sessTransformationInst.setTransformationType(value);
				exp_sessTransformationInst.setStage("5");
				exp_sessTransformationInst.setPipeline("1");
				exp_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
				exp_sessTransformationInst.setsInstanceName("sc_"+key);
				exp_sessTransformationInst.setTransformationName("sc_"+key);

				Partition partition = new Partition("Partition #1", "");

				exp_sessTransformationInst.addPartition(partition);
				session.addSessTransformationInst(exp_sessTransformationInst);
			}
		}
		}
		ConfigReference configReference = new ConfigReference("Session config", "default_session_config");
		session.setConfigReference(configReference);
		emptyCount = 0;
		// Session Extension for TD
		for (int i = 58;; i++) {
			String key = (readExcelController.getCellValue(filePath, sessionSheetName, i, "N"));
			String value = (readExcelController.getCellValue(filePath, sessionSheetName, i, "O"));
			if(emptyCount>2)break;
		if(readExcelController.getCellValue(filePath, sessionSheetName, i, "N").isEmpty()) {
			emptyCount++;
		}else {
			if(value.equalsIgnoreCase("Target Definition")) {
				//ins
		SessionExtension td_sessionExtension = new SessionExtension();
		td_sessionExtension.setsInstanceName("sc_"+key+"_INS");
		td_sessionExtension.setName("Relational Writer");
		td_sessionExtension.setSubtype("Relational Writer");
		td_sessionExtension.setTransformationType(value);
		td_sessionExtension.setType("WRITER");

		String targetConnectionName = readExcelController.getCellValue(filePath, sessionSheetName, 44, "O");

		ConnectionReference td_connectionReference = new ConnectionReference();
		td_connectionReference.setConnectionName(targetConnectionName);//CDM_DM_USER
		td_connectionReference.setCnxRefName("DB Connection");
		td_connectionReference.setConnectionSubType("Oracle");
		td_connectionReference.setConnectionType("Relational");
		td_connectionReference.setConnectionNumber("1");
		td_connectionReference.setVariable("");
		td_sessionExtension.addConnectionReference(td_connectionReference);
		Attribute attribute = null;
		Attribute ad = new Attribute("Target load type", "Normal");
		td_sessionExtension.addAttribute(ad);
		for (int j = 46; j < 53; j++) {
			attribute = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, j, "N"),
					readExcelController.getCellValue(filePath, sessionSheetName, j, "O"));
			td_sessionExtension.addAttribute(attribute);
		}
		// td_sessionExtension.setAttributeList(sessionController.getSessionForSourceToStage1(filePath,sessionSheetName));
		Attribute ad1 = new Attribute("Reject filename", "sc_"+key.toLowerCase()+"_ins1.bad");
		td_sessionExtension.addAttribute(ad1);
		session.addSessionExtension(td_sessionExtension);
		//upd
		SessionExtension td_sessionExtension1 = new SessionExtension();
		td_sessionExtension1.setsInstanceName("sc_"+key+"_UPD");
		td_sessionExtension1.setName("Relational Writer");
		td_sessionExtension1.setSubtype("Relational Writer");
		td_sessionExtension1.setTransformationType(value);
		td_sessionExtension1.setType("WRITER");

		//String targetConnectionName1 = readExcelController.getCellValue(filePath, sessionSheetName, 45, "O");

		ConnectionReference td_connectionReference1 = new ConnectionReference();
		td_connectionReference1.setConnectionName(targetConnectionName);//CDM_DM_USER
		td_connectionReference1.setCnxRefName("DB Connection");
		td_connectionReference1.setConnectionSubType("Oracle");
		td_connectionReference1.setConnectionType("Relational");
		td_connectionReference1.setConnectionNumber("1");
		td_connectionReference1.setVariable("");
		td_sessionExtension1.addConnectionReference(td_connectionReference1);
		Attribute attribute_1 = null;
		Attribute ad_1 = new Attribute("Target load type", "Normal");
		td_sessionExtension1.addAttribute(ad_1);
		for (int j = 46; j < 53; j++) {
			attribute_1 = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, j, "N"),
					readExcelController.getCellValue(filePath, sessionSheetName, j, "O"));
			td_sessionExtension1.addAttribute(attribute_1);
		}
		// td_sessionExtension.setAttributeList(sessionController.getSessionForSourceToStage1(filePath,sessionSheetName));
		Attribute ad1_1 = new Attribute("Reject filename", "sc_"+key.toLowerCase()+"_upd1.bad");
		td_sessionExtension1.addAttribute(ad1_1);
		session.addSessionExtension(td_sessionExtension1);
			}else if(value.equalsIgnoreCase("Source Definition")){
		// Session Extension for SD

		SessionExtension sd_sessionExtension = new SessionExtension();
		sd_sessionExtension.setdSQInstName("SQ_"+key);
		sd_sessionExtension.setdSQInstType("Source Qualifier");
		sd_sessionExtension.setName("File Reader");
		sd_sessionExtension.setsInstanceName(key);
		sd_sessionExtension.setSubtype("File Reader");
		sd_sessionExtension.setTransformationType(value);
		sd_sessionExtension.setType("READER");

		ConnectionReference sd_connectionReference = new ConnectionReference();
		sd_connectionReference.setCnxRefName("Connection");
		sd_connectionReference.setConnectionName("");
		sd_connectionReference.setConnectionNumber("1");
		sd_connectionReference.setConnectionSubType("");
		sd_connectionReference.setConnectionType("");
		sd_connectionReference.setVariable("");

		sd_sessionExtension.addConnectionReference(sd_connectionReference);
		Attribute ab = new Attribute("Input Type", "File");
		sd_sessionExtension.addAttribute(ab);
		Attribute ab1 = new Attribute("Concurrent read partitioning", "Optimize throughput");
		sd_sessionExtension.addAttribute(ab1);
		Attribute ab2 = new Attribute("Command Type", "Command Generating Data");
		sd_sessionExtension.addAttribute(ab2);
		Attribute ab3 = new Attribute("Source filetype",
				readExcelController.getCellValue(filePath, sessionSheetName, 55, "O"));
		sd_sessionExtension.addAttribute(ab3);
		Attribute ab4 = new Attribute("Source file directory",
				readExcelController.getCellValue(filePath, sessionSheetName, 53, "O"));
		sd_sessionExtension.addAttribute(ab4);
		Attribute ab5 = new Attribute("Source filename",
				readExcelController.getCellValue(filePath, sessionSheetName, 54, "O"));
		sd_sessionExtension.addAttribute(ab5);
		Attribute ab6 = new Attribute("Command", "");
		sd_sessionExtension.addAttribute(ab6);
		Attribute ab7 = new Attribute("File Reader Truncate String Null", "NO");
		sd_sessionExtension.addAttribute(ab7);
		Attribute ab8 = new Attribute("Codepage Parameter", "");
		sd_sessionExtension.addAttribute(ab8);
		session.addSessionExtension(sd_sessionExtension);

		// Session Extension for SQ
			}else if(value.equalsIgnoreCase("Source Qualifier")){
		SessionExtension sq_sessionExtension = new SessionExtension();
		sq_sessionExtension.setsInstanceName(key);
		sq_sessionExtension.setTransformationType(value);
		sq_sessionExtension.setName("File Reader");
		sq_sessionExtension.setSubtype("File Reader");
		sq_sessionExtension.setType("READER");

		session.addSessionExtension(sq_sessionExtension);
			} else if (value.equalsIgnoreCase("Lookup Procedure")) {
				SessionExtension lk_sessionExtension = new SessionExtension();
				lk_sessionExtension.setName("Relational Lookup");
				lk_sessionExtension.setSubtype("Relational Lookup");
				lk_sessionExtension.setsInstanceName("sc_"+key);
				lk_sessionExtension.setTransformationType(value);
				lk_sessionExtension.setType("LOOKUPEXTENSION");
				ConnectionReference lk_connectionReference = new ConnectionReference();
				lk_connectionReference.setCnxRefName("DB Connection");
				lk_connectionReference
						.setConnectionName(readExcelController.getCellValue(filePath, sessionSheetName, i, "R"));
				lk_connectionReference.setConnectionNumber("1");
				lk_connectionReference.setConnectionSubType("Oracle");
				lk_connectionReference.setConnectionType("Relational");
				lk_connectionReference.setVariable("");
				lk_sessionExtension.addConnectionReference(lk_connectionReference);
				session.addSessionExtension(lk_sessionExtension);
			} 
		}
		}
		// session.setAttributeList(sessionController.getMainSessionForSourceToStage1(filePath,sessionSheetName));
		Attribute att = null;
		Attribute att1 = new Attribute("General Options", "");
		Attribute att2 = new Attribute("Write Backward Compatible Session Log File", "NO");
		session.addAttribute(att1);
		session.addAttribute(att2);
		for (int i = 4; i < 12; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "N");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "O");
			if (value.contains(".0")) {

			} else {
				att = new Attribute(key, value);
				session.addAttribute(att);
			}
		}
		Attribute a1 = new Attribute("Number of rows to test", "1");
		session.addAttribute(a1);
		Attribute a3 = new Attribute("Commit Interval", "10000");
		session.addAttribute(a3);
		Attribute at = new Attribute("DTM buffer size", "5000000000");
		session.addAttribute(at);
		Attribute att3 = new Attribute("Parameter Filename", "");
		Attribute att4 = new Attribute("Enable Test Load", "NO");
		Attribute att5 = new Attribute("$Source connection value", "");
		Attribute att6 = new Attribute("$Target connection value", "");
		Attribute att7 = new Attribute("Commit Type", "Target");
		Attribute att8 = new Attribute("Commit On End Of File", "YES");
		Attribute att9 = new Attribute("Rollback Transactions on Errors", "NO");
		Attribute att10 = new Attribute("Java Classpath", "");
		Attribute att11 = new Attribute("Performance", "");
		Attribute att12 = new Attribute("Collect performance data", "NO");
		Attribute att13 = new Attribute("Write performance data to repository", "NO");
		Attribute att14 = new Attribute("Incremental Aggregation", "NO");
		Attribute att15 = new Attribute("Enable high precision", "NO");
		Attribute att16 = new Attribute("Session retry on deadlock", "YES");
		Attribute att17 = new Attribute("Pushdown Optimization", "NONE");
		Attribute att18 = new Attribute("Allow Temporary View for Pushdown", "NO");
		Attribute att19 = new Attribute("Allow Temporary Sequence for Pushdown", "NO");
		Attribute att20 = new Attribute("Allow Pushdown for User Incompatible Connections", "NO");

		session.addAttribute(att3);
		session.addAttribute(att4);
		session.addAttribute(att5);
		session.addAttribute(att6);
		session.addAttribute(att7);
		session.addAttribute(att8);
		session.addAttribute(att9);
		session.addAttribute(att10);
		session.addAttribute(att11);
		session.addAttribute(att12);
		session.addAttribute(att13);
		session.addAttribute(att14);
		session.addAttribute(att15);
		session.addAttribute(att16);
		session.addAttribute(att17);
		session.addAttribute(att18);
		session.addAttribute(att19);
		session.addAttribute(att20);
		folder.addSession(session);
		repository.addFolder(folder);
		powerMart.addRepository(repository);
		// close workbook
		readExcelController.closeWorkBookForFile(filePath);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + srcToODSSessionName + ".xml");
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

	@RequestMapping("stage1ToStage2Session/{filePath}/{outputPath}")
	public void generateStage1ToStage2XML(@PathVariable String filePath, @PathVariable String outputPath)
			throws JAXBException, IOException {
		String mappingSheetName = "Mapping Details";
		String sessionSheetName = "Session Properties";

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String repoName = readExcelController.getCellValue(filePath, mappingSheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();

		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, mappingSheetName, 1, "D");

		Folder folder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid);
		// folder.addConfig(sessionController.getDefaultSessionConfigForStage1ToStage2(filePath,sessionSheetName));
		Config config = new Config("default_session_config", "Default session configuration object", "YES", "1");
		IntAttribute intconfigAtt = null;
		Attribute configAtt = null;
		for (int i = 13; i < 40; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "E");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "F");
			if (value.contains(".0")) {
				Integer precision = Integer.parseInt(value.replace(".0", ""));
				intconfigAtt = new IntAttribute(key, precision);
				config.addIntAttribute(intconfigAtt);
			} else {
				if(!key.isEmpty()){
				configAtt = new Attribute(key, value);
				config.addAttribute(configAtt);
				}
			}

		}

		folder.addConfig(config);
		String stg1ToStg2SessionName = readExcelController.getCellValue(filePath, mappingSheetName, 10, "D");
		String stg1ToStg2MappingName = readExcelController.getCellValue(filePath, mappingSheetName, 9, "D");
		Session session = new Session("", "YES", stg1ToStg2MappingName, stg1ToStg2SessionName, "YES", "Binary", "1");

		int count = 0;
		for (int j = 58;; j++) {

			if (!readExcelController.getCellValue(filePath, sessionSheetName, j, "E").isEmpty()) {
				String key = readExcelController.getCellValue(filePath, sessionSheetName, j, "E");
				String value = readExcelController.getCellValue(filePath, sessionSheetName, j, "F");
				// set Target Definition
				if (value.equalsIgnoreCase("Target Definition")) {
					count++;
					SessTransformationInst td_sessTransformationInst = new SessTransformationInst();
					td_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
					td_sessTransformationInst.setPartitionType("PASS THROUGH");
					td_sessTransformationInst.setPipeline("1");
					td_sessTransformationInst.setsInstanceName(key);
					td_sessTransformationInst.setStage(count + "");
					td_sessTransformationInst.setTransformationName(key);
					td_sessTransformationInst.setTransformationType(value);
					session.addSessTransformationInst(td_sessTransformationInst);
				}

				// Set Source Definition
				if (value.equalsIgnoreCase("Source Definition")) {
					SessTransformationInst sd_sessTransformationInst = new SessTransformationInst();
					sd_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
					sd_sessTransformationInst.setPipeline("0");
					sd_sessTransformationInst.setsInstanceName(key);
					sd_sessTransformationInst.setStage("0");
					sd_sessTransformationInst.setTransformationName(key);
					sd_sessTransformationInst.setTransformationType(value);

					session.addSessTransformationInst(sd_sessTransformationInst);
				}

				// set Source Qualifier
				if (value.equalsIgnoreCase("Source Qualifier")) {
					SessTransformationInst sq_sessTransformationInst = new SessTransformationInst();
					sq_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
					sq_sessTransformationInst.setPartitionType("PASS THROUGH");
					sq_sessTransformationInst.setPipeline("1");
					sq_sessTransformationInst.setsInstanceName(key);
					sq_sessTransformationInst.setStage("4");
					sq_sessTransformationInst.setTransformationName(key);
					sq_sessTransformationInst.setTransformationType(value);

					session.addSessTransformationInst(sq_sessTransformationInst);
				}
				if (value.equalsIgnoreCase("Expression") || value.equalsIgnoreCase("Router")
						|| value.equalsIgnoreCase("Lookup Procedure")) {
					SessTransformationInst exp_sessTransformationInst = new SessTransformationInst();
					exp_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
					exp_sessTransformationInst.setPipeline("1");
					exp_sessTransformationInst.setsInstanceName(key);
					exp_sessTransformationInst.setStage("4");
					exp_sessTransformationInst.setTransformationName(key);
					exp_sessTransformationInst.setTransformationType(value);

					Partition partition = new Partition("Partition #1", "");
					exp_sessTransformationInst.addPartition(partition);
					session.addSessTransformationInst(exp_sessTransformationInst);
				}

			} else
				break;
		}

		ConfigReference configReference = new ConfigReference("Session config", "default_session_config");
		List<Attribute> aList = new ArrayList<Attribute>();
		for (int m = 32; m < 35; m++) {
			Attribute at = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, m, "H"),
					readExcelController.getCellValue(filePath, sessionSheetName, m, "I"));
			aList.add(at);
		}
		configReference.setAttributeList(aList);
		session.setConfigReference(configReference);
		// Session Extension for TD
		for (int k = 58;; k++) {
			if (!readExcelController.getCellValue(filePath, sessionSheetName, k, "E").isEmpty()) {
				String key = readExcelController.getCellValue(filePath, sessionSheetName, k, "E");
				String value = readExcelController.getCellValue(filePath, sessionSheetName, k, "F");

				if (value.equalsIgnoreCase("Target Definition")) {
					SessionExtension td_sessionExtension = new SessionExtension();
					td_sessionExtension.setName("Relational Writer");
					td_sessionExtension.setsInstanceName(key);
					td_sessionExtension.setSubtype("Relational Writer");
					td_sessionExtension.setTransformationType(value);
					td_sessionExtension.setType("WRITER");

					ConnectionReference td_connectionReference = new ConnectionReference();
					td_connectionReference.setCnxRefName("DB Connection");
					td_connectionReference
							.setConnectionName(readExcelController.getCellValue(filePath, sessionSheetName, 44, "F"));
					td_connectionReference.setConnectionNumber("1");
					td_connectionReference.setConnectionSubType("Oracle");
					td_connectionReference.setConnectionType("Relational");
					td_connectionReference.setVariable("");
					td_sessionExtension.addConnectionReference(td_connectionReference);

					Attribute attribute1 = null;
					Attribute ad = new Attribute("Target load type", "Normal");
					td_sessionExtension.addAttribute(ad);
					for (int i = 46; i < 53; i++) {
						attribute1 = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, i, "E"),
								readExcelController.getCellValue(filePath, sessionSheetName, i, "F"));
						td_sessionExtension.addAttribute(attribute1);
					}
					Attribute ad1 = new Attribute("Reject filename", "sc_stg_cccmdp1.bad");
					td_sessionExtension.addAttribute(ad1);
					session.addSessionExtension(td_sessionExtension);
				}
				// session Extension for SD
				if (value.equalsIgnoreCase("Source Definition")) {
					SessionExtension sq_sessionExtension = new SessionExtension();
					sq_sessionExtension.setdSQInstName("SQTRANS");
					sq_sessionExtension.setdSQInstType("Source Qualifier");
					sq_sessionExtension.setName("Relational Reader");
					sq_sessionExtension.setsInstanceName(key);
					sq_sessionExtension.setSubtype("Relational Reader");
					sq_sessionExtension.setTransformationType(value);
					sq_sessionExtension.setType("READER");
					session.addSessionExtension(sq_sessionExtension);

				}
				// Session Extension for SQ
				if (value.equalsIgnoreCase("Source Qualifier")) {
					SessionExtension sq_sessionExtension2 = new SessionExtension();
					sq_sessionExtension2.setName("Relational Reader");
					sq_sessionExtension2.setsInstanceName(key);
					sq_sessionExtension2.setSubtype("Relational Reader");
					sq_sessionExtension2.setTransformationType(value);
					sq_sessionExtension2.setType("READER");
					ConnectionReference sq_connectionReference = new ConnectionReference();
					sq_connectionReference.setCnxRefName("DB Connection");
					sq_connectionReference
							.setConnectionName(readExcelController.getCellValue(filePath, sessionSheetName, 43, "F"));
					sq_connectionReference.setConnectionNumber("1");
					sq_connectionReference.setConnectionSubType("Oracle");
					sq_connectionReference.setConnectionType("Relational");
					sq_connectionReference.setVariable("");
					sq_sessionExtension2.addConnectionReference(sq_connectionReference);
					session.addSessionExtension(sq_sessionExtension2);

				}
				if (value.equalsIgnoreCase("Lookup Procedure")) {
					SessionExtension lk_sessionExtension = new SessionExtension();
					lk_sessionExtension.setName("Relational Lookup");
					lk_sessionExtension.setSubtype("Relational Lookup");
					lk_sessionExtension.setsInstanceName(key);
					lk_sessionExtension.setTransformationType(value);
					lk_sessionExtension.setType("LOOKUPEXTENSION");
					ConnectionReference lk_connectionReference = new ConnectionReference();
					lk_connectionReference.setCnxRefName("DB Connection");
					lk_connectionReference
							.setConnectionName(readExcelController.getCellValue(filePath, sessionSheetName, 55, "F"));
					lk_connectionReference.setConnectionNumber("1");
					lk_connectionReference.setConnectionSubType("Oracle");
					lk_connectionReference.setConnectionType("Relational");
					lk_connectionReference.setVariable("");
					lk_sessionExtension.addConnectionReference(lk_connectionReference);
					session.addSessionExtension(lk_sessionExtension);
				}
			} else
				break;
		}
		// Attribute att = null;
		Attribute att1 = new Attribute("General Options", "");
		Attribute att2 = new Attribute("Write Backward Compatible Session Log File", "NO");
		session.addAttribute(att1);
		session.addAttribute(att2);
		for (int i = 4; i < 11; i++) {
			String key1 = readExcelController.getCellValue(filePath, sessionSheetName, i, "E");
			String value1 = readExcelController.getCellValue(filePath, sessionSheetName, i, "F");
			if (value1.contains(".0")) {

			} else {
				if(!key1.isEmpty()){
				Attribute att = new Attribute(key1, value1);
				session.addAttribute(att);
				}
			}
		}
		Attribute a1 = new Attribute("Number of rows to test", "1");
		session.addAttribute(a1);
		Attribute a3 = new Attribute("Commit Interval", "10000");
		session.addAttribute(a3);
		Attribute at = new Attribute("DTM buffer size", "5000000000");
		session.addAttribute(at);
		Attribute att3 = new Attribute("Parameter Filename", "");
		Attribute att4 = new Attribute("Enable Test Load", "NO");
		Attribute att5 = new Attribute("$Source connection value", "");
		Attribute att6 = new Attribute("$Target connection value", "");
		Attribute att7 = new Attribute("Commit Type", "Target");
		Attribute att8 = new Attribute("Commit On End Of File", "YES");
		Attribute att9 = new Attribute("Rollback Transactions on Errors", "NO");
		Attribute att10 = new Attribute("Java Classpath", "");
		Attribute att11 = new Attribute("Performance", "");
		Attribute att12 = new Attribute("Collect performance data", "NO");
		Attribute att13 = new Attribute("Write performance data to repository", "NO");
		Attribute att14 = new Attribute("Incremental Aggregation", "NO");
		Attribute att15 = new Attribute("Enable high precision", "NO");
		Attribute att16 = new Attribute("Session retry on deadlock", "YES");
		Attribute att17 = new Attribute("Pushdown Optimization", "NONE");
		Attribute att18 = new Attribute("Allow Temporary View for Pushdown", "NO");
		Attribute att19 = new Attribute("Allow Temporary Sequence for Pushdown", "NO");
		Attribute att20 = new Attribute("Allow Pushdown for User Incompatible Connections", "NO");

		session.addAttribute(att3);
		session.addAttribute(att4);
		session.addAttribute(att5);
		session.addAttribute(att6);
		session.addAttribute(att7);
		session.addAttribute(att8);
		session.addAttribute(att9);
		session.addAttribute(att10);
		session.addAttribute(att11);
		session.addAttribute(att12);
		session.addAttribute(att13);
		session.addAttribute(att14);
		session.addAttribute(att15);
		session.addAttribute(att16);
		session.addAttribute(att17);
		session.addAttribute(att18);
		session.addAttribute(att19);
		session.addAttribute(att20);
		folder.addSession(session);
		repository.addFolder(folder);
		powerMart.addRepository(repository);
		// close workbook
		readExcelController.closeWorkBookForFile(filePath);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + stg1ToStg2SessionName + ".xml");
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

	@RequestMapping("deleteSession/{filePath}/{outputPath}")
	public void generateDeleteXML(@PathVariable String filePath, @PathVariable String outputPath)
			throws JAXBException, IOException {
		String mappingSheetName = "Mapping Details";
		String sessionSheetName = "Session Properties";

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String repoName = readExcelController.getCellValue(filePath, mappingSheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();

		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, mappingSheetName, 1, "D");

		Folder folder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid);
		// folder.addConfig(sessionController.getDefaultSessionConfigForStage1ToStage2(filePath,sessionSheetName));
		Config config = new Config("default_session_config", "Default session configuration object", "YES", "1");
		IntAttribute intconfigAtt = null;
		Attribute configAtt = null;
		for (int i = 13; i < 40; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "H");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "I");
			if (value.contains(".0")) {
				Integer precision = Integer.parseInt(value.replace(".0", ""));
				intconfigAtt = new IntAttribute(key, precision);
				config.addIntAttribute(intconfigAtt);
			} else {
				if(!key.isEmpty()){
				configAtt = new Attribute(key, value);
				config.addAttribute(configAtt);
			}
			}
		}

		folder.addConfig(config);
		String deleteSessionName = readExcelController.getCellValue(filePath, mappingSheetName, 15, "D");
		String deleteMappingName = readExcelController.getCellValue(filePath, mappingSheetName, 14, "D");
		Session session = new Session("", "YES", deleteMappingName, deleteSessionName, "YES", "Binary", "1");

		for (int j = 58;; j++) {
			// set Target Definition
			if (!readExcelController.getCellValue(filePath, sessionSheetName, j, "H").isEmpty()) {
				String key = readExcelController.getCellValue(filePath, sessionSheetName, j, "H");
				String value = readExcelController.getCellValue(filePath, sessionSheetName, j, "I");
				if (value.equalsIgnoreCase("Target Definition")) {
					SessTransformationInst td_sessTransformationInst = new SessTransformationInst();
					td_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
					td_sessTransformationInst.setPartitionType("PASS THROUGH");
					td_sessTransformationInst.setPipeline("1");
					td_sessTransformationInst.setsInstanceName(key);
					td_sessTransformationInst.setStage("1");
					td_sessTransformationInst.setTransformationName(key);
					td_sessTransformationInst.setTransformationType(value);
					session.addSessTransformationInst(td_sessTransformationInst);
				}

				// Set Source Definition
				if (value.equalsIgnoreCase("Source Definition")) {
					SessTransformationInst sd_sessTransformationInst = new SessTransformationInst();
					sd_sessTransformationInst.setStage("0");
					sd_sessTransformationInst.setPipeline("0");
					sd_sessTransformationInst.setTransformationType(value);
					sd_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
					sd_sessTransformationInst.setsInstanceName(key);
					sd_sessTransformationInst.setTransformationName(key);
					session.addSessTransformationInst(sd_sessTransformationInst);
				}

				// set Source Qualifier
				if (value.equalsIgnoreCase("Source Qualifier")) {
					SessTransformationInst sq_sessTransformationInst = new SessTransformationInst();
					sq_sessTransformationInst.setStage("2");
					sq_sessTransformationInst.setPipeline("1");
					sq_sessTransformationInst.setPartitionType("PASS THROUGH");
					sq_sessTransformationInst.setTransformationType(value);
					sq_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
					sq_sessTransformationInst.setsInstanceName(key);
					sq_sessTransformationInst.setTransformationName(key);
					session.addSessTransformationInst(sq_sessTransformationInst);
				}
				if (value.equalsIgnoreCase("Expression") || value.equalsIgnoreCase("Router")
						|| value.equalsIgnoreCase("Lookup Procedure")) {
					SessTransformationInst exp_sessTransformationInst = new SessTransformationInst();
					exp_sessTransformationInst.setStage("2");
					exp_sessTransformationInst.setPipeline("1");
					exp_sessTransformationInst.setTransformationType(value);
					exp_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
					exp_sessTransformationInst.setsInstanceName(key);
					exp_sessTransformationInst.setTransformationName(key);

					Partition partition = new Partition("Partition #1", "");

					exp_sessTransformationInst.addPartition(partition);
					session.addSessTransformationInst(exp_sessTransformationInst);
				}
			} else
				break;
		}

		ConfigReference configReference = new ConfigReference("Session config", "default_session_config");
		List<Attribute> aList = new ArrayList<Attribute>();
		for (int m = 32; m < 35; m++) {
			Attribute at = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, m, "H"),
					readExcelController.getCellValue(filePath, sessionSheetName, m, "I"));
			aList.add(at);
		}
		configReference.setAttributeList(aList);
		session.setConfigReference(configReference);
		String sourceTableName = readExcelController.getCellValue(filePath, mappingSheetName, 23, "AP");
		// Session Extension for TD
		for (int k = 58;; k++) {
			if (!readExcelController.getCellValue(filePath, sessionSheetName, k, "H").isEmpty()) {
				String key = readExcelController.getCellValue(filePath, sessionSheetName, k, "H");
				String value = readExcelController.getCellValue(filePath, sessionSheetName, k, "I");

				if (value.equalsIgnoreCase("Target Definition")) {
					SessionExtension td_sessionExtension = new SessionExtension();
					td_sessionExtension.setsInstanceName(key);
					td_sessionExtension.setName("Relational Writer");
					td_sessionExtension.setSubtype("Relational Writer");
					td_sessionExtension.setTransformationType(value);
					td_sessionExtension.setType("WRITER");

					ConnectionReference td_connectionReference = new ConnectionReference();
					td_connectionReference
							.setConnectionName(readExcelController.getCellValue(filePath, sessionSheetName, 44, "I"));
					td_connectionReference.setCnxRefName("DB Connection");
					td_connectionReference.setConnectionSubType("Oracle");
					td_connectionReference.setConnectionType("Relational");
					td_connectionReference.setConnectionNumber("1");
					td_connectionReference.setVariable("");
					td_sessionExtension.addConnectionReference(td_connectionReference);

					Attribute attribute1 = null;
					Attribute ad = new Attribute("Target load type", "Normal");
					td_sessionExtension.addAttribute(ad);
					for (int i = 46; i < 53; i++) {
						attribute1 = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, i, "H"),
								readExcelController.getCellValue(filePath, sessionSheetName, i, "I"));
						td_sessionExtension.addAttribute(attribute1);
					}
					Attribute ad1 = new Attribute("Reject filename", "sc_stg_cccmdp1.bad");
					td_sessionExtension.addAttribute(ad1);
					session.addSessionExtension(td_sessionExtension);
				}
				// Session Extension for SD
				if (value.equalsIgnoreCase("Source Definition")) {
					SessionExtension sq_sessionExtension = new SessionExtension();
					sq_sessionExtension.setdSQInstName("SQ_sc_" + sourceTableName);
					sq_sessionExtension.setdSQInstType("Source Qualifier");
					sq_sessionExtension.setsInstanceName(key);
					sq_sessionExtension.setName("Relational Reader");
					sq_sessionExtension.setTransformationType(value);
					sq_sessionExtension.setSubtype("Relational Reader");
					sq_sessionExtension.setType("READER");
					session.addSessionExtension(sq_sessionExtension);
				}
				// Session Extension for SQ
				if (value.equalsIgnoreCase("Source Qualifier")) {

					SessionExtension sq_sessionExtension2 = new SessionExtension();
					sq_sessionExtension2.setsInstanceName(key);
					sq_sessionExtension2.setTransformationType(value);
					sq_sessionExtension2.setName("Relational Reader");
					sq_sessionExtension2.setSubtype("Relational Reader");
					sq_sessionExtension2.setType("READER");
					ConnectionReference sq_connectionReference = new ConnectionReference();
					sq_connectionReference
							.setConnectionName(readExcelController.getCellValue(filePath, sessionSheetName, 43, "I"));
					sq_connectionReference.setCnxRefName("DB Connection");
					sq_connectionReference.setConnectionSubType("Oracle");
					sq_connectionReference.setConnectionType("Relational");
					sq_connectionReference.setConnectionNumber("1");
					sq_connectionReference.setVariable("");
					sq_sessionExtension2.addConnectionReference(sq_connectionReference);
					session.addSessionExtension(sq_sessionExtension2);

				}
				if (value.equalsIgnoreCase("Lookup Procedure")) {
					SessionExtension lk_sessionExtension = new SessionExtension();
					lk_sessionExtension.setsInstanceName(key);
					lk_sessionExtension.setTransformationType(value);
					lk_sessionExtension.setName("Relational Lookup");
					lk_sessionExtension.setSubtype("Relational Lookup");
					lk_sessionExtension.setType("LOOKUPEXTENSION");

					ConnectionReference lk_connectionReference = new ConnectionReference();
					lk_connectionReference
							.setConnectionName(readExcelController.getCellValue(filePath, sessionSheetName, 55, "I"));
					lk_connectionReference.setCnxRefName("DB Connection");
					lk_connectionReference.setConnectionSubType("Oracle");
					lk_connectionReference.setConnectionType("Relational");
					lk_connectionReference.setConnectionNumber("1");
					lk_connectionReference.setVariable("");
					lk_sessionExtension.addConnectionReference(lk_connectionReference);
					session.addSessionExtension(lk_sessionExtension);
				}
			} else
				break;
		}
		// Attribute att = null;
		Attribute att1 = new Attribute("General Options", "");
		Attribute att2 = new Attribute("Write Backward Compatible Session Log File", "NO");
		session.addAttribute(att1);
		session.addAttribute(att2);
		for (int i = 4; i < 11; i++) {
			String key1 = readExcelController.getCellValue(filePath, sessionSheetName, i, "H");
			String value1 = readExcelController.getCellValue(filePath, sessionSheetName, i, "I");
			if (value1.contains(".0")) {

			} else {
				if(!key1.isEmpty()){
				Attribute att = new Attribute(key1, value1);
				session.addAttribute(att);
				}
			}
		}
		Attribute a1 = new Attribute("Number of rows to test", "1");
		session.addAttribute(a1);
		Attribute a3 = new Attribute("Commit Interval", "10000");
		session.addAttribute(a3);
		Attribute at = new Attribute("DTM buffer size", "5000000000");
		session.addAttribute(at);
		Attribute att3 = new Attribute("Parameter Filename", "");
		Attribute att4 = new Attribute("Enable Test Load", "NO");
		Attribute att5 = new Attribute("$Source connection value", "");
		Attribute att6 = new Attribute("$Target connection value", "");
		Attribute att7 = new Attribute("Commit Type", "Target");
		Attribute att8 = new Attribute("Commit On End Of File", "YES");
		Attribute att9 = new Attribute("Rollback Transactions on Errors", "NO");
		Attribute att10 = new Attribute("Java Classpath", "");
		Attribute att11 = new Attribute("Performance", "");
		Attribute att12 = new Attribute("Collect performance data", "NO");
		Attribute att13 = new Attribute("Write performance data to repository", "NO");
		Attribute att14 = new Attribute("Incremental Aggregation", "NO");
		Attribute att15 = new Attribute("Enable high precision", "NO");
		Attribute att16 = new Attribute("Session retry on deadlock", "YES");
		Attribute att17 = new Attribute("Pushdown Optimization", "NONE");
		Attribute att18 = new Attribute("Allow Temporary View for Pushdown", "NO");
		Attribute att19 = new Attribute("Allow Temporary Sequence for Pushdown", "NO");
		Attribute att20 = new Attribute("Allow Pushdown for User Incompatible Connections", "NO");

		session.addAttribute(att3);
		session.addAttribute(att4);
		session.addAttribute(att5);
		session.addAttribute(att6);
		session.addAttribute(att7);
		session.addAttribute(att8);
		session.addAttribute(att9);
		session.addAttribute(att10);
		session.addAttribute(att11);
		session.addAttribute(att12);
		session.addAttribute(att13);
		session.addAttribute(att14);
		session.addAttribute(att15);
		session.addAttribute(att16);
		session.addAttribute(att17);
		session.addAttribute(att18);
		session.addAttribute(att19);
		session.addAttribute(att20);
		folder.addSession(session);
		repository.addFolder(folder);
		powerMart.addRepository(repository);
		// close workbook
		readExcelController.closeWorkBookForFile(filePath);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + deleteSessionName + ".xml");
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

	@RequestMapping("stage2ToProdSession/{filePath}/{outputPath}")
	public void generateStage2ToProdXML(@PathVariable String filePath, @PathVariable String outputPath)
			throws JAXBException, IOException {
		String mappingSheetName = "Mapping Details";
		String sessionSheetName = "Session Properties";

		Date date = new Date();
		String dateStr = simpleDateFormat.format(date);
		String repoName = readExcelController.getCellValue(filePath, mappingSheetName, 2, "D");
		PowerMart powerMart = new PowerMart("184.93", dateStr);
		Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
		String uuid = UUID.randomUUID().toString();

		// Fetch Folder name from excel
		String folderName = readExcelController.getCellValue(filePath, mappingSheetName, 1, "D");

		Folder folder = new Folder(folderName, "", "INFA_ADMIN", SharedEnum.NOTSHARED, "", "rwx------", uuid);
		// folder.addConfig(sessionController.getDefaultSessionConfigForStage1ToStage2(filePath,sessionSheetName));
		Config config = new Config("default_session_config", "Default session configuration object", "YES", "1");
		IntAttribute intconfigAtt = null;
		Attribute configAtt = null;
		for (int i = 14; i < 41; i++) {
			String key = readExcelController.getCellValue(filePath, sessionSheetName, i, "K");
			String value = readExcelController.getCellValue(filePath, sessionSheetName, i, "L");
			if (value.contains(".0")) {
				Integer precision = Integer.parseInt(value.replace(".0", ""));
				intconfigAtt = new IntAttribute(key, precision);
				config.addIntAttribute(intconfigAtt);
			} else {
				if(!key.isEmpty()){
				configAtt = new Attribute(key, value);
				config.addAttribute(configAtt);
			}
			}
		}

		folder.addConfig(config);
		String stg2ToProdSessionName = readExcelController.getCellValue(filePath, mappingSheetName, 20, "D");
		String stg2ToProdMappingName = readExcelController.getCellValue(filePath, mappingSheetName, 19, "D");
		Session session = new Session("", "YES", stg2ToProdMappingName, stg2ToProdSessionName, "YES", "Binary", "1");

		int count = 0;
		for (int j = 58;; j++) {
			// set Target Definition
			if (!readExcelController.getCellValue(filePath, sessionSheetName, j, "K").isEmpty()) {
				String key = readExcelController.getCellValue(filePath, sessionSheetName, j, "K");
				String value = readExcelController.getCellValue(filePath, sessionSheetName, j, "L");

				if (value.equalsIgnoreCase("Target Definition")) {
					count++;
					SessTransformationInst td_sessTransformationInst = new SessTransformationInst();
					td_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
					td_sessTransformationInst.setPartitionType("PASS THROUGH");
					td_sessTransformationInst.setPipeline("1");
					td_sessTransformationInst.setsInstanceName(key);
					td_sessTransformationInst.setStage(count + "");
					td_sessTransformationInst.setTransformationName(key);
					td_sessTransformationInst.setTransformationType(value);
					session.addSessTransformationInst(td_sessTransformationInst);
				}

				// Set Source Definition
				if (value.equalsIgnoreCase("Source Definition")) {
					SessTransformationInst sd_sessTransformationInst = new SessTransformationInst();
					sd_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
					sd_sessTransformationInst.setPipeline("0");
					sd_sessTransformationInst.setsInstanceName(key);
					sd_sessTransformationInst.setStage("0");
					sd_sessTransformationInst.setTransformationName(key);
					sd_sessTransformationInst.setTransformationType(value);

					session.addSessTransformationInst(sd_sessTransformationInst);
				}

				// set Source Qualifier
				if (value.equalsIgnoreCase("Source Qualifier")) {
					SessTransformationInst sq_sessTransformationInst = new SessTransformationInst();
					sq_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.YES);
					sq_sessTransformationInst.setPipeline("1");
					sq_sessTransformationInst.setPartitionType("PASS THROUGH");
					sq_sessTransformationInst.setsInstanceName(key);
					sq_sessTransformationInst.setStage("3");
					sq_sessTransformationInst.setTransformationName(key);
					sq_sessTransformationInst.setTransformationType(value);
					session.addSessTransformationInst(sq_sessTransformationInst);
				}
				if (value.equalsIgnoreCase("Expression") || value.equalsIgnoreCase("Router")
						|| value.equalsIgnoreCase("Update Strategy")) {
					SessTransformationInst exp_sessTransformationInst = new SessTransformationInst();
					exp_sessTransformationInst.setIsRepartitionPoint(YesNoEnum.NO);
					exp_sessTransformationInst.setPipeline("1");
					exp_sessTransformationInst.setsInstanceName(key);
					exp_sessTransformationInst.setStage("3");
					exp_sessTransformationInst.setTransformationName(key);
					exp_sessTransformationInst.setTransformationType(value);

					Partition partition = new Partition("Partition #1", "");

					exp_sessTransformationInst.addPartition(partition);
					session.addSessTransformationInst(exp_sessTransformationInst);
				}
			} else
				break;
		}

		ConfigReference configReference = new ConfigReference("Session config", "default_session_config");

		session.setConfigReference(configReference);
		String sourceTableName = readExcelController.getCellValue(filePath, mappingSheetName, 23, "Z");
		// Session Extension for TD
		for (int k = 58;; k++) {
			if (!readExcelController.getCellValue(filePath, sessionSheetName, k, "K").isEmpty()) {
				String key = readExcelController.getCellValue(filePath, sessionSheetName, k, "K");
				String value = readExcelController.getCellValue(filePath, sessionSheetName, k, "L");

				if (value.equalsIgnoreCase("Target Definition")) {
					SessionExtension td_sessionExtension = new SessionExtension();
					td_sessionExtension.setsInstanceName(key);
					td_sessionExtension.setName("Relational Writer");
					td_sessionExtension.setSubtype("Relational Writer");
					td_sessionExtension.setTransformationType(value);
					td_sessionExtension.setType("WRITER");

					ConnectionReference td_connectionReference = new ConnectionReference();
					td_connectionReference
							.setConnectionName(readExcelController.getCellValue(filePath, sessionSheetName, 44, "L"));
					td_connectionReference.setCnxRefName("DB Connection");
					td_connectionReference.setConnectionSubType("Oracle");
					td_connectionReference.setConnectionType("Relational");
					td_connectionReference.setConnectionNumber("1");
					td_connectionReference.setVariable("");
					td_sessionExtension.addConnectionReference(td_connectionReference);

					Attribute attribute1 = null;
					Attribute ad = new Attribute("Target load type", "Normal");
					td_sessionExtension.addAttribute(ad);
					for (int i = 46; i < 53; i++) {
						attribute1 = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, i, "K"),
								readExcelController.getCellValue(filePath, sessionSheetName, i, "L"));
						td_sessionExtension.addAttribute(attribute1);
					}
					Attribute ad1 = new Attribute("Reject filename", "sc_stg_cccmdp1.bad");
					td_sessionExtension.addAttribute(ad1);
					session.addSessionExtension(td_sessionExtension);
				}
				// Session Extension for SQ
				if (value.equalsIgnoreCase("Source Qualifier")) {
					SessionExtension sq_sessionExtension = new SessionExtension();
					sq_sessionExtension.setName("Relational Reader");
					sq_sessionExtension.setsInstanceName(key);
					sq_sessionExtension.setSubtype("Relational Reader");
					sq_sessionExtension.setTransformationType(value);
					sq_sessionExtension.setType("READER");

					ConnectionReference sq_connectionReference = new ConnectionReference();
					sq_connectionReference
							.setConnectionName(readExcelController.getCellValue(filePath, sessionSheetName, 43, "L"));
					sq_connectionReference.setCnxRefName("DB Connection");
					sq_connectionReference.setConnectionSubType("Oracle");
					sq_connectionReference.setConnectionType("Relational");
					sq_connectionReference.setConnectionNumber("1");
					sq_connectionReference.setVariable("");
					sq_sessionExtension.addConnectionReference(sq_connectionReference);
					session.addSessionExtension(sq_sessionExtension);
				}

				if (value.equalsIgnoreCase("Source Definition")) {
					SessionExtension sd_sessionExtension = new SessionExtension();
					sd_sessionExtension.setdSQInstName("SQ_sc_" + sourceTableName);
					sd_sessionExtension.setdSQInstType("Source Qualifier");
					sd_sessionExtension.setsInstanceName(key);
					sd_sessionExtension.setTransformationType(value);
					sd_sessionExtension.setName("Relational Reader");
					sd_sessionExtension.setSubtype("Relational Reader");
					sd_sessionExtension.setType("READER");
					session.addSessionExtension(sd_sessionExtension);
				}
			} else
				break;
		}
		Attribute att1 = new Attribute("General Options", "");
		Attribute att2 = new Attribute("Write Backward Compatible Session Log File", "NO");
		session.addAttribute(att1);
		session.addAttribute(att2);
		for (int i = 4; i < 12; i++) {
			String key1 = readExcelController.getCellValue(filePath, sessionSheetName, i, "K");
			String value1 = readExcelController.getCellValue(filePath, sessionSheetName, i, "L");
			if (value1.contains(".0")) {

			} else {
				if(!key1.isEmpty()){
				Attribute att = new Attribute(key1, value1);
				session.addAttribute(att);
				}
			}
		}
		Attribute a1 = new Attribute("Number of rows to test", "1");
		session.addAttribute(a1);
		Attribute a3 = new Attribute("Commit Interval", "10000");
		session.addAttribute(a3);
		Attribute at = new Attribute("DTM buffer size", "5000000000");
		session.addAttribute(at);
		Attribute att3 = new Attribute("Parameter Filename", "");
		Attribute att4 = new Attribute("Enable Test Load", "NO");
		Attribute att5 = new Attribute("$Source connection value", "");
		Attribute att6 = new Attribute("$Target connection value", "");
		Attribute att7 = new Attribute("Commit Type", "Target");
		Attribute att8 = new Attribute("Commit On End Of File", "YES");
		Attribute att9 = new Attribute("Rollback Transactions on Errors", "NO");
		Attribute att10 = new Attribute("Java Classpath", "");
		Attribute att11 = new Attribute("Performance", "");
		Attribute att12 = new Attribute("Collect performance data", "NO");
		Attribute att13 = new Attribute("Write performance data to repository", "NO");
		Attribute att14 = new Attribute("Incremental Aggregation", "NO");
		Attribute att15 = new Attribute("Enable high precision", "NO");
		Attribute att16 = new Attribute("Session retry on deadlock", "YES");
		Attribute att17 = new Attribute("Pushdown Optimization", "NONE");
		Attribute att18 = new Attribute("Allow Temporary View for Pushdown", "NO");
		Attribute att19 = new Attribute("Allow Temporary Sequence for Pushdown", "NO");
		Attribute att20 = new Attribute("Allow Pushdown for User Incompatible Connections", "NO");

		session.addAttribute(att3);
		session.addAttribute(att4);
		session.addAttribute(att5);
		session.addAttribute(att6);
		session.addAttribute(att7);
		session.addAttribute(att8);
		session.addAttribute(att9);
		session.addAttribute(att10);
		session.addAttribute(att11);
		session.addAttribute(att12);
		session.addAttribute(att13);
		session.addAttribute(att14);
		session.addAttribute(att15);
		session.addAttribute(att16);
		session.addAttribute(att17);
		session.addAttribute(att18);
		session.addAttribute(att19);
		session.addAttribute(att20);
		folder.addSession(session);
		repository.addFolder(folder);
		powerMart.addRepository(repository);
		// close workbook
		readExcelController.closeWorkBookForFile(filePath);
		JAXBContext jaxbContext = JAXBContext.newInstance(PowerMart.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM \"powrmart.dtd\">");
		try {
			FileWriter fw = new FileWriter(outputPath + stg2ToProdSessionName + ".xml");
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
