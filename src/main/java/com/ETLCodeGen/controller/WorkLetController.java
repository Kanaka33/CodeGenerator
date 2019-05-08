package com.ETLCodeGen.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ETLCodeGen.model.Attribute;
import com.ETLCodeGen.model.Config;
import com.ETLCodeGen.model.ConfigReference;
import com.ETLCodeGen.model.ConnectionReference;
import com.ETLCodeGen.model.FlatFile;
import com.ETLCodeGen.model.Folder;
import com.ETLCodeGen.model.IntAttribute;
import com.ETLCodeGen.model.Partition;
import com.ETLCodeGen.model.PowerMart;
import com.ETLCodeGen.model.Repository;
import com.ETLCodeGen.model.SessTransformationInst;
import com.ETLCodeGen.model.Session;
import com.ETLCodeGen.model.SessionExtension;
import com.ETLCodeGen.model.SharedEnum;
import com.ETLCodeGen.model.Task;
import com.ETLCodeGen.model.TaskInstance;
import com.ETLCodeGen.model.WorkFlowLink;
import com.ETLCodeGen.model.WorkFlowVariable;
import com.ETLCodeGen.model.Worklet;
import com.ETLCodeGen.model.YesNoEnum;
import com.ETLCodeGen.validator.JaxbCharacterEscapeHandler;
import com.sun.xml.internal.bind.marshaller.DataWriter;

@Controller
@RequestMapping("xml/")
public class WorkLetController {
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	@Autowired
	ReadExcelController readExcelController;
	
	@RequestMapping("sourceFileToODSWorklet/{filePath}/{outputPath}")
	public void generateSourceFileToODSWorklet(@PathVariable String filePath, @PathVariable String outputPath)
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
		String worletName = readExcelController.getCellValue(filePath, mappingSheetName, 6, "K");
		String srcToStg1SessionName = readExcelController.getCellValue(filePath, mappingSheetName, 5, "K");
		String srcToStg1MappingName = readExcelController.getCellValue(filePath, mappingSheetName, 4, "K");
		Session session = new Session("", "YES", srcToStg1MappingName, srcToStg1SessionName, "YES", "Binary", "1");

		int emptyCount = 0;
		// Set Target Definition
		for (int i = 58;; i++) {
			String key = (readExcelController.getCellValue(filePath, sessionSheetName, i, "N"));
			String value = (readExcelController.getCellValue(filePath, sessionSheetName, i, "O"));
			if(emptyCount>2)break;
			if(key.isEmpty()) {
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
		if(key.isEmpty()){
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
		for (int i = 4; i < 11; i++) {
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
	
		Worklet work = new Worklet();
		work.setDescription("");
		work.setIsValid("YES");
		work.setName(worletName);
		work.setReusable("YES");
		work.setVersionNumber("1");
		Task task = new Task("", "DEC_FAIL", "NO", "Decision", "1");
		Attribute attribute11 = new Attribute("Decision Name", "");
		task.addAttribute(attribute11);
		work.addTask(task);
		Task task1 = new Task("", "Start", "NO", "Start", "1");
		work.addTask(task1);
		Task task2 = new Task("", "CONTROL", "NO", "Control", "1");
		Attribute attribute12 = new Attribute("Control Option", "Abort top-level workflow");
		task2.addAttribute(attribute12);
		work.addTask(task2);

		TaskInstance tIns = new TaskInstance("", "NO", "YES", "YES", "DEC_FAIL", "NO", "DEC_FAIL", "Decision", "NO");
		work.addTaskInstance(tIns);
		TaskInstance tIns1 = new TaskInstance("", "YES", "Start", "NO", "Start", "Start");
		work.addTaskInstance(tIns1);
		TaskInstance tIns2 = new TaskInstance("", "NO", "YES", "YES", "CONTROL", "NO", "CONTROL", "Control", "NO");
		work.addTaskInstance(tIns2);
		TaskInstance tIns3 = new TaskInstance("", "NO", "NO", "YES", srcToStg1SessionName, "YES", srcToStg1SessionName,
				"Session", "YES");
		work.addTaskInstance(tIns3);

		WorkFlowLink wLink = new WorkFlowLink("$" + srcToStg1SessionName + ".Status= FAILED OR $" + srcToStg1SessionName
				+ ".Status= ABORTED OR $" + srcToStg1SessionName + ".Status = STOPPED", srcToStg1SessionName,
				"DEC_FAIL");
		work.addWorkFlowLink(wLink);
		WorkFlowLink wLink1 = new WorkFlowLink("", "DEC_FAIL", "CONTROL");
		work.addWorkFlowLink(wLink1);
		WorkFlowLink wLink2 = new WorkFlowLink("", "Start", srcToStg1SessionName);
		work.addWorkFlowLink(wLink2);
		WorkFlowVariable wVariable = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$DEC_FAIL.StartTime", "NO");
		work.addWorkFlowVariable(wVariable);
		WorkFlowVariable wVariable1 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$DEC_FAIL.EndTime", "NO");
		work.addWorkFlowVariable(wVariable1);
		WorkFlowVariable wVariable2 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO", "NO",
				"$DEC_FAIL.Status", "NO");
		work.addWorkFlowVariable(wVariable2);
		WorkFlowVariable wVariable3 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$DEC_FAIL.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable3);
		WorkFlowVariable wVariable4 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$DEC_FAIL.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable4);
		WorkFlowVariable wVariable5 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$DEC_FAIL.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable5);
		WorkFlowVariable wVariable6 = new WorkFlowVariable("integer", "", "Evaluation result of condition expression",
				"NO", "NO", "$DEC_FAIL.Condition", "NO");
		work.addWorkFlowVariable(wVariable6);
		WorkFlowVariable wVariable7 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$Start.StartTime", "NO");
		work.addWorkFlowVariable(wVariable7);
		WorkFlowVariable wVariable8 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$Start.EndTime", "NO");
		work.addWorkFlowVariable(wVariable8);
		WorkFlowVariable wVariable9 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO", "NO",
				"$Start.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable9);
		WorkFlowVariable wVariable10 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$Start.Status", "NO");
		work.addWorkFlowVariable(wVariable10);
		WorkFlowVariable wVariable11 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$Start.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable11);
		WorkFlowVariable wVariable12 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$Start.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable12);
		WorkFlowVariable wVariable13 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$CONTROL.StartTime", "NO");
		work.addWorkFlowVariable(wVariable13);
		WorkFlowVariable wVariable14 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$CONTROL.EndTime", "NO");
		work.addWorkFlowVariable(wVariable14);
		WorkFlowVariable wVariable15 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$CONTROL.Status", "NO");
		work.addWorkFlowVariable(wVariable15);
		WorkFlowVariable wVariable16 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$CONTROL.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable16);
		WorkFlowVariable wVariable17 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable17);
		WorkFlowVariable wVariable18 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$" + srcToStg1SessionName + ".ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable18);
		WorkFlowVariable wVariable19 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$" + srcToStg1SessionName + ".StartTime", "NO");
		work.addWorkFlowVariable(wVariable19);
		WorkFlowVariable wVariable20 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$" + srcToStg1SessionName + ".EndTime", "NO");
		work.addWorkFlowVariable(wVariable20);
		WorkFlowVariable wVariable21 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".Status", "NO");
		work.addWorkFlowVariable(wVariable21);
		WorkFlowVariable wVariable22 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO",
				"$" + srcToStg1SessionName + ".PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable22);
		WorkFlowVariable wVariable23 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable23);
		WorkFlowVariable wVariable24 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$" + srcToStg1SessionName + ".ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable24);
		WorkFlowVariable wVariable25 = new WorkFlowVariable("integer", "", "Rows successfully read", "NO", "NO",
				"$" + srcToStg1SessionName + ".SrcSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable25);
		WorkFlowVariable wVariable26 = new WorkFlowVariable("integer", "", "Rows failed to read", "NO", "NO",
				"$" + srcToStg1SessionName + ".SrcFailedRows", "NO");
		work.addWorkFlowVariable(wVariable26);
		WorkFlowVariable wVariable27 = new WorkFlowVariable("integer", "", "Rows successfully loaded", "NO", "NO",
				"$" + srcToStg1SessionName + ".TgtSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable27);
		WorkFlowVariable wVariable28 = new WorkFlowVariable("integer", "", "Rows failed to load", "NO", "NO",
				"$" + srcToStg1SessionName + ".TgtFailedRows", "NO");
		work.addWorkFlowVariable(wVariable28);
		WorkFlowVariable wVariable29 = new WorkFlowVariable("integer", "", "Total number of transformation errors",
				"NO", "NO", "$" + srcToStg1SessionName + ".TotalTransErrors", "NO");
		work.addWorkFlowVariable(wVariable29);
		WorkFlowVariable wVariable30 = new WorkFlowVariable("integer", "", "First error code", "NO", "NO",
				"$" + srcToStg1SessionName + ".FirstErrorCode", "NO");
		work.addWorkFlowVariable(wVariable30);
		WorkFlowVariable wVariable31 = new WorkFlowVariable("string", "", "First error message", "NO", "NO",
				"$" + srcToStg1SessionName + ".FirstErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable31);
		Attribute at1 = new Attribute("Allow Concurrent Run", "NO");
		work.addAttribute(at1);
		folder.addWorklet(work);

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
			FileWriter fw = new FileWriter(outputPath + worletName + ".xml");
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

	@RequestMapping("stageToODSWorklet/{filePath}/{outputPath}")
	public void generateStageToODSWorklet(@PathVariable String filePath, @PathVariable String outputPath)
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
		String worletName = readExcelController.getCellValue(filePath, mappingSheetName, 6, "K");
		String srcToStg1SessionName = readExcelController.getCellValue(filePath, mappingSheetName, 5, "K");
		String srcToStg1MappingName = readExcelController.getCellValue(filePath, mappingSheetName, 4, "K");
		Session session = new Session("", "YES", srcToStg1MappingName, srcToStg1SessionName, "YES", "Binary", "1");

		int emptyCount = 0;
		// Set Target Definition
		for (int i = 58;; i++) {
			String key = (readExcelController.getCellValue(filePath, sessionSheetName, i, "N"));
			String value = (readExcelController.getCellValue(filePath, sessionSheetName, i, "O"));
			if(emptyCount>2)break;
		if(key.isEmpty()) {
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

		String ownerName = readExcelController.getCellValue(filePath, sessionSheetName, 42, "O");
		Attribute attribute = new Attribute("Owner Name", ownerName);
		sd_sessTransformationInst.addAttribute(attribute);
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
		if(key.isEmpty()) {
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
		
		Worklet work = new Worklet();
		work.setDescription("");
		work.setIsValid("YES");
		work.setName(worletName);
		work.setReusable("YES");
		work.setVersionNumber("1");
		Task task = new Task("", "DEC_FAIL", "NO", "Decision", "1");
		Attribute attribute11 = new Attribute("Decision Name", "");
		task.addAttribute(attribute11);
		work.addTask(task);
		Task task1 = new Task("", "Start", "NO", "Start", "1");
		work.addTask(task1);
		Task task2 = new Task("", "CONTROL", "NO", "Control", "1");
		Attribute attribute12 = new Attribute("Control Option", "Abort top-level workflow");
		task2.addAttribute(attribute12);
		work.addTask(task2);

		TaskInstance tIns = new TaskInstance("", "NO", "YES", "YES", "DEC_FAIL", "NO", "DEC_FAIL", "Decision", "NO");
		work.addTaskInstance(tIns);
		TaskInstance tIns1 = new TaskInstance("", "YES", "Start", "NO", "Start", "Start");
		work.addTaskInstance(tIns1);
		TaskInstance tIns2 = new TaskInstance("", "NO", "YES", "YES", "CONTROL", "NO", "CONTROL", "Control", "NO");
		work.addTaskInstance(tIns2);
		TaskInstance tIns3 = new TaskInstance("", "NO", "NO", "YES", srcToStg1SessionName, "YES", srcToStg1SessionName,
				"Session", "YES");
		work.addTaskInstance(tIns3);

		WorkFlowLink wLink = new WorkFlowLink("$" + srcToStg1SessionName + ".Status= FAILED OR $" + srcToStg1SessionName
				+ ".Status= ABORTED OR $" + srcToStg1SessionName + ".Status = STOPPED", srcToStg1SessionName,
				"DEC_FAIL");
		work.addWorkFlowLink(wLink);
		WorkFlowLink wLink1 = new WorkFlowLink("", "DEC_FAIL", "CONTROL");
		work.addWorkFlowLink(wLink1);
		WorkFlowLink wLink2 = new WorkFlowLink("", "Start", srcToStg1SessionName);
		work.addWorkFlowLink(wLink2);
		WorkFlowVariable wVariable = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$DEC_FAIL.StartTime", "NO");
		work.addWorkFlowVariable(wVariable);
		WorkFlowVariable wVariable1 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$DEC_FAIL.EndTime", "NO");
		work.addWorkFlowVariable(wVariable1);
		WorkFlowVariable wVariable2 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO", "NO",
				"$DEC_FAIL.Status", "NO");
		work.addWorkFlowVariable(wVariable2);
		WorkFlowVariable wVariable3 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$DEC_FAIL.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable3);
		WorkFlowVariable wVariable4 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$DEC_FAIL.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable4);
		WorkFlowVariable wVariable5 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$DEC_FAIL.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable5);
		WorkFlowVariable wVariable6 = new WorkFlowVariable("integer", "", "Evaluation result of condition expression",
				"NO", "NO", "$DEC_FAIL.Condition", "NO");
		work.addWorkFlowVariable(wVariable6);
		WorkFlowVariable wVariable7 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$Start.StartTime", "NO");
		work.addWorkFlowVariable(wVariable7);
		WorkFlowVariable wVariable8 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$Start.EndTime", "NO");
		work.addWorkFlowVariable(wVariable8);
		WorkFlowVariable wVariable9 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO", "NO",
				"$Start.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable9);
		WorkFlowVariable wVariable10 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$Start.Status", "NO");
		work.addWorkFlowVariable(wVariable10);
		WorkFlowVariable wVariable11 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$Start.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable11);
		WorkFlowVariable wVariable12 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$Start.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable12);
		WorkFlowVariable wVariable13 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$CONTROL.StartTime", "NO");
		work.addWorkFlowVariable(wVariable13);
		WorkFlowVariable wVariable14 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$CONTROL.EndTime", "NO");
		work.addWorkFlowVariable(wVariable14);
		WorkFlowVariable wVariable15 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$CONTROL.Status", "NO");
		work.addWorkFlowVariable(wVariable15);
		WorkFlowVariable wVariable16 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$CONTROL.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable16);
		WorkFlowVariable wVariable17 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable17);
		WorkFlowVariable wVariable18 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$" + srcToStg1SessionName + ".ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable18);
		WorkFlowVariable wVariable19 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$" + srcToStg1SessionName + ".StartTime", "NO");
		work.addWorkFlowVariable(wVariable19);
		WorkFlowVariable wVariable20 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$" + srcToStg1SessionName + ".EndTime", "NO");
		work.addWorkFlowVariable(wVariable20);
		WorkFlowVariable wVariable21 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".Status", "NO");
		work.addWorkFlowVariable(wVariable21);
		WorkFlowVariable wVariable22 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO",
				"$" + srcToStg1SessionName + ".PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable22);
		WorkFlowVariable wVariable23 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable23);
		WorkFlowVariable wVariable24 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$" + srcToStg1SessionName + ".ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable24);
		WorkFlowVariable wVariable25 = new WorkFlowVariable("integer", "", "Rows successfully read", "NO", "NO",
				"$" + srcToStg1SessionName + ".SrcSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable25);
		WorkFlowVariable wVariable26 = new WorkFlowVariable("integer", "", "Rows failed to read", "NO", "NO",
				"$" + srcToStg1SessionName + ".SrcFailedRows", "NO");
		work.addWorkFlowVariable(wVariable26);
		WorkFlowVariable wVariable27 = new WorkFlowVariable("integer", "", "Rows successfully loaded", "NO", "NO",
				"$" + srcToStg1SessionName + ".TgtSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable27);
		WorkFlowVariable wVariable28 = new WorkFlowVariable("integer", "", "Rows failed to load", "NO", "NO",
				"$" + srcToStg1SessionName + ".TgtFailedRows", "NO");
		work.addWorkFlowVariable(wVariable28);
		WorkFlowVariable wVariable29 = new WorkFlowVariable("integer", "", "Total number of transformation errors",
				"NO", "NO", "$" + srcToStg1SessionName + ".TotalTransErrors", "NO");
		work.addWorkFlowVariable(wVariable29);
		WorkFlowVariable wVariable30 = new WorkFlowVariable("integer", "", "First error code", "NO", "NO",
				"$" + srcToStg1SessionName + ".FirstErrorCode", "NO");
		work.addWorkFlowVariable(wVariable30);
		WorkFlowVariable wVariable31 = new WorkFlowVariable("string", "", "First error message", "NO", "NO",
				"$" + srcToStg1SessionName + ".FirstErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable31);
		Attribute at1 = new Attribute("Allow Concurrent Run", "NO");
		work.addAttribute(at1);
		folder.addWorklet(work);

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
			FileWriter fw = new FileWriter(outputPath + worletName + ".xml");
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

	@RequestMapping("sourceToStage1Worklet/{filePath}/{outputPath}")
	public void generateSourceToStage1Worklet(@PathVariable String filePath, @PathVariable String outputPath)
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
		String worletName = readExcelController.getCellValue(filePath, mappingSheetName, 6, "D");
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

		Worklet work = new Worklet();
		work.setDescription("");
		work.setIsValid("YES");
		work.setName(worletName);
		work.setReusable("YES");
		work.setVersionNumber("1");
		Task task = new Task("", "DEC_FAIL", "NO", "Decision", "1");
		Attribute attribute11 = new Attribute("Decision Name", "");
		task.addAttribute(attribute11);
		work.addTask(task);
		Task task1 = new Task("", "Start", "NO", "Start", "1");
		work.addTask(task1);
		Task task2 = new Task("", "CONTROL", "NO", "Control", "1");
		Attribute attribute12 = new Attribute("Control Option", "Abort top-level workflow");
		task2.addAttribute(attribute12);
		work.addTask(task2);

		TaskInstance tIns = new TaskInstance("", "NO", "YES", "YES", "DEC_FAIL", "NO", "DEC_FAIL", "Decision", "NO");
		work.addTaskInstance(tIns);
		TaskInstance tIns1 = new TaskInstance("", "YES", "Start", "NO", "Start", "Start");
		work.addTaskInstance(tIns1);
		TaskInstance tIns2 = new TaskInstance("", "NO", "YES", "YES", "CONTROL", "NO", "CONTROL", "Control", "NO");
		work.addTaskInstance(tIns2);
		TaskInstance tIns3 = new TaskInstance("", "NO", "NO", "YES", srcToStg1SessionName, "YES", srcToStg1SessionName,
				"Session", "YES");
		work.addTaskInstance(tIns3);

		WorkFlowLink wLink = new WorkFlowLink("$" + srcToStg1SessionName + ".Status= FAILED OR $" + srcToStg1SessionName
				+ ".Status= ABORTED OR $" + srcToStg1SessionName + ".Status = STOPPED", srcToStg1SessionName,
				"DEC_FAIL");
		work.addWorkFlowLink(wLink);
		WorkFlowLink wLink1 = new WorkFlowLink("", "DEC_FAIL", "CONTROL");
		work.addWorkFlowLink(wLink1);
		WorkFlowLink wLink2 = new WorkFlowLink("", "Start", srcToStg1SessionName);
		work.addWorkFlowLink(wLink2);
		WorkFlowVariable wVariable = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$DEC_FAIL.StartTime", "NO");
		work.addWorkFlowVariable(wVariable);
		WorkFlowVariable wVariable1 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$DEC_FAIL.EndTime", "NO");
		work.addWorkFlowVariable(wVariable1);
		WorkFlowVariable wVariable2 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO", "NO",
				"$DEC_FAIL.Status", "NO");
		work.addWorkFlowVariable(wVariable2);
		WorkFlowVariable wVariable3 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$DEC_FAIL.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable3);
		WorkFlowVariable wVariable4 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$DEC_FAIL.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable4);
		WorkFlowVariable wVariable5 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$DEC_FAIL.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable5);
		WorkFlowVariable wVariable6 = new WorkFlowVariable("integer", "", "Evaluation result of condition expression",
				"NO", "NO", "$DEC_FAIL.Condition", "NO");
		work.addWorkFlowVariable(wVariable6);
		WorkFlowVariable wVariable7 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$Start.StartTime", "NO");
		work.addWorkFlowVariable(wVariable7);
		WorkFlowVariable wVariable8 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$Start.EndTime", "NO");
		work.addWorkFlowVariable(wVariable8);
		WorkFlowVariable wVariable9 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO", "NO",
				"$Start.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable9);
		WorkFlowVariable wVariable10 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$Start.Status", "NO");
		work.addWorkFlowVariable(wVariable10);
		WorkFlowVariable wVariable11 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$Start.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable11);
		WorkFlowVariable wVariable12 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$Start.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable12);
		WorkFlowVariable wVariable13 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$CONTROL.StartTime", "NO");
		work.addWorkFlowVariable(wVariable13);
		WorkFlowVariable wVariable14 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$CONTROL.EndTime", "NO");
		work.addWorkFlowVariable(wVariable14);
		WorkFlowVariable wVariable15 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$CONTROL.Status", "NO");
		work.addWorkFlowVariable(wVariable15);
		WorkFlowVariable wVariable16 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$CONTROL.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable16);
		WorkFlowVariable wVariable17 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable17);
		WorkFlowVariable wVariable18 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$" + srcToStg1SessionName + ".ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable18);
		WorkFlowVariable wVariable19 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$" + srcToStg1SessionName + ".StartTime", "NO");
		work.addWorkFlowVariable(wVariable19);
		WorkFlowVariable wVariable20 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$" + srcToStg1SessionName + ".EndTime", "NO");
		work.addWorkFlowVariable(wVariable20);
		WorkFlowVariable wVariable21 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".Status", "NO");
		work.addWorkFlowVariable(wVariable21);
		WorkFlowVariable wVariable22 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO",
				"$" + srcToStg1SessionName + ".PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable22);
		WorkFlowVariable wVariable23 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable23);
		WorkFlowVariable wVariable24 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$" + srcToStg1SessionName + ".ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable24);
		WorkFlowVariable wVariable25 = new WorkFlowVariable("integer", "", "Rows successfully read", "NO", "NO",
				"$" + srcToStg1SessionName + ".SrcSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable25);
		WorkFlowVariable wVariable26 = new WorkFlowVariable("integer", "", "Rows failed to read", "NO", "NO",
				"$" + srcToStg1SessionName + ".SrcFailedRows", "NO");
		work.addWorkFlowVariable(wVariable26);
		WorkFlowVariable wVariable27 = new WorkFlowVariable("integer", "", "Rows successfully loaded", "NO", "NO",
				"$" + srcToStg1SessionName + ".TgtSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable27);
		WorkFlowVariable wVariable28 = new WorkFlowVariable("integer", "", "Rows failed to load", "NO", "NO",
				"$" + srcToStg1SessionName + ".TgtFailedRows", "NO");
		work.addWorkFlowVariable(wVariable28);
		WorkFlowVariable wVariable29 = new WorkFlowVariable("integer", "", "Total number of transformation errors",
				"NO", "NO", "$" + srcToStg1SessionName + ".TotalTransErrors", "NO");
		work.addWorkFlowVariable(wVariable29);
		WorkFlowVariable wVariable30 = new WorkFlowVariable("integer", "", "First error code", "NO", "NO",
				"$" + srcToStg1SessionName + ".FirstErrorCode", "NO");
		work.addWorkFlowVariable(wVariable30);
		WorkFlowVariable wVariable31 = new WorkFlowVariable("string", "", "First error message", "NO", "NO",
				"$" + srcToStg1SessionName + ".FirstErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable31);
		Attribute at1 = new Attribute("Allow Concurrent Run", "NO");
		work.addAttribute(at1);
		folder.addWorklet(work);

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
			FileWriter fw = new FileWriter(outputPath + worletName + ".xml");
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

	@RequestMapping("sourceFileToStage1Worklet/{filePath}/{outputPath}")
	public void generateSourceFileToStage1Worklet(@PathVariable String filePath, @PathVariable String outputPath)
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
		String worletName = readExcelController.getCellValue(filePath, mappingSheetName, 6, "D");
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

		Worklet work = new Worklet();
		work.setDescription("");
		work.setIsValid("YES");
		work.setName(worletName);
		work.setReusable("YES");
		work.setVersionNumber("1");
		Task task = new Task("", "DEC_FAIL", "NO", "Decision", "1");
		Attribute attribute11 = new Attribute("Decision Name", "");
		task.addAttribute(attribute11);
		work.addTask(task);
		Task task1 = new Task("", "Start", "NO", "Start", "1");
		work.addTask(task1);
		Task task2 = new Task("", "CONTROL", "NO", "Control", "1");
		Attribute attribute12 = new Attribute("Control Option", "Abort top-level workflow");
		task2.addAttribute(attribute12);
		work.addTask(task2);

		TaskInstance tIns = new TaskInstance("", "NO", "YES", "YES", "DEC_FAIL", "NO", "DEC_FAIL", "Decision", "NO");
		work.addTaskInstance(tIns);
		TaskInstance tIns1 = new TaskInstance("", "YES", "Start", "NO", "Start", "Start");
		work.addTaskInstance(tIns1);
		TaskInstance tIns2 = new TaskInstance("", "NO", "YES", "YES", "CONTROL", "NO", "CONTROL", "Control", "NO");
		work.addTaskInstance(tIns2);
		TaskInstance tIns3 = new TaskInstance("", "NO", "NO", "YES", srcToStg1SessionName, "YES", srcToStg1SessionName,
				"Session", "YES");
		work.addTaskInstance(tIns3);

		WorkFlowLink wLink = new WorkFlowLink("$" + srcToStg1SessionName + ".Status= FAILED OR $" + srcToStg1SessionName
				+ ".Status= ABORTED OR $" + srcToStg1SessionName + ".Status = STOPPED", srcToStg1SessionName,
				"DEC_FAIL");
		work.addWorkFlowLink(wLink);
		WorkFlowLink wLink1 = new WorkFlowLink("", "DEC_FAIL", "CONTROL");
		work.addWorkFlowLink(wLink1);
		WorkFlowLink wLink2 = new WorkFlowLink("", "Start", srcToStg1SessionName);
		work.addWorkFlowLink(wLink2);
		WorkFlowVariable wVariable = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$DEC_FAIL.StartTime", "NO");
		work.addWorkFlowVariable(wVariable);
		WorkFlowVariable wVariable1 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$DEC_FAIL.EndTime", "NO");
		work.addWorkFlowVariable(wVariable1);
		WorkFlowVariable wVariable2 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO", "NO",
				"$DEC_FAIL.Status", "NO");
		work.addWorkFlowVariable(wVariable2);
		WorkFlowVariable wVariable3 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$DEC_FAIL.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable3);
		WorkFlowVariable wVariable4 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$DEC_FAIL.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable4);
		WorkFlowVariable wVariable5 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$DEC_FAIL.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable5);
		WorkFlowVariable wVariable6 = new WorkFlowVariable("integer", "", "Evaluation result of condition expression",
				"NO", "NO", "$DEC_FAIL.Condition", "NO");
		work.addWorkFlowVariable(wVariable6);
		WorkFlowVariable wVariable7 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$Start.StartTime", "NO");
		work.addWorkFlowVariable(wVariable7);
		WorkFlowVariable wVariable8 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$Start.EndTime", "NO");
		work.addWorkFlowVariable(wVariable8);
		WorkFlowVariable wVariable9 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO", "NO",
				"$Start.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable9);
		WorkFlowVariable wVariable10 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$Start.Status", "NO");
		work.addWorkFlowVariable(wVariable10);
		WorkFlowVariable wVariable11 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$Start.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable11);
		WorkFlowVariable wVariable12 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$Start.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable12);
		WorkFlowVariable wVariable13 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$CONTROL.StartTime", "NO");
		work.addWorkFlowVariable(wVariable13);
		WorkFlowVariable wVariable14 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$CONTROL.EndTime", "NO");
		work.addWorkFlowVariable(wVariable14);
		WorkFlowVariable wVariable15 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$CONTROL.Status", "NO");
		work.addWorkFlowVariable(wVariable15);
		WorkFlowVariable wVariable16 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$CONTROL.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable16);
		WorkFlowVariable wVariable17 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$CONTROL.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable17);
		WorkFlowVariable wVariable18 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$CONTROL.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable18);
		WorkFlowVariable wVariable19 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$" + srcToStg1SessionName + ".StartTime", "NO");
		work.addWorkFlowVariable(wVariable19);
		WorkFlowVariable wVariable20 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$" + srcToStg1SessionName + ".EndTime", "NO");
		work.addWorkFlowVariable(wVariable20);
		WorkFlowVariable wVariable21 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".Status", "NO");
		work.addWorkFlowVariable(wVariable21);
		WorkFlowVariable wVariable22 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO",
				"$" + srcToStg1SessionName + ".PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable22);
		WorkFlowVariable wVariable23 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$" + srcToStg1SessionName + ".ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable23);
		WorkFlowVariable wVariable24 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$" + srcToStg1SessionName + ".ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable24);
		WorkFlowVariable wVariable25 = new WorkFlowVariable("integer", "", "Rows successfully read", "NO", "NO",
				"$" + srcToStg1SessionName + ".SrcSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable25);
		WorkFlowVariable wVariable26 = new WorkFlowVariable("integer", "", "Rows failed to read", "NO", "NO",
				"$" + srcToStg1SessionName + ".SrcFailedRows", "NO");
		work.addWorkFlowVariable(wVariable26);
		WorkFlowVariable wVariable27 = new WorkFlowVariable("integer", "", "Rows successfully loaded", "NO", "NO",
				"$" + srcToStg1SessionName + ".TgtSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable27);
		WorkFlowVariable wVariable28 = new WorkFlowVariable("integer", "", "Rows failed to load", "NO", "NO",
				"$" + srcToStg1SessionName + ".TgtFailedRows", "NO");
		work.addWorkFlowVariable(wVariable28);
		WorkFlowVariable wVariable29 = new WorkFlowVariable("integer", "", "Total number of transformation errors",
				"NO", "NO", "$" + srcToStg1SessionName + ".TotalTransErrors", "NO");
		work.addWorkFlowVariable(wVariable29);
		WorkFlowVariable wVariable30 = new WorkFlowVariable("integer", "", "First error code", "NO", "NO",
				"$" + srcToStg1SessionName + ".FirstErrorCode", "NO");
		work.addWorkFlowVariable(wVariable30);
		WorkFlowVariable wVariable31 = new WorkFlowVariable("string", "", "First error message", "NO", "NO",
				"$" + srcToStg1SessionName + ".FirstErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable31);
		Attribute at1 = new Attribute("Allow Concurrent Run", "NO");
		work.addAttribute(at1);
		folder.addWorklet(work);

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
			FileWriter fw = new FileWriter(outputPath + worletName + ".xml");
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

	@RequestMapping("prodWorklet/{filePath}/{outputPath}")
	public void generateProdWorklet(@PathVariable String filePath, @PathVariable String outputPath)
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
		String prodWorkletName = readExcelController.getCellValue(filePath, mappingSheetName, 21, "D");
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
		for (int i = 4; i < 12; i++) {
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
		String deleteSessionName = readExcelController.getCellValue(filePath, mappingSheetName, 15, "D");
		String deleteMappingName = readExcelController.getCellValue(filePath, mappingSheetName, 14, "D");
		Session session1 = new Session("", "YES", deleteMappingName, deleteSessionName, "YES", "Binary", "1");

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
					session1.addSessTransformationInst(td_sessTransformationInst);
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
					session1.addSessTransformationInst(sd_sessTransformationInst);
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
					session1.addSessTransformationInst(sq_sessTransformationInst);
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
					session1.addSessTransformationInst(exp_sessTransformationInst);
				}
			} else
				break;
		}

		ConfigReference configReferenc = new ConfigReference("Session config", "default_session_config");
		List<Attribute> aList1 = new ArrayList<Attribute>();
		for (int m = 32; m < 35; m++) {
			Attribute at1 = new Attribute(readExcelController.getCellValue(filePath, sessionSheetName, m, "H"),
					readExcelController.getCellValue(filePath, sessionSheetName, m, "I"));
			aList1.add(at1);
		}
		configReferenc.setAttributeList(aList1);
		session1.setConfigReference(configReferenc);
		String sourceProdTableName = readExcelController.getCellValue(filePath, mappingSheetName, 23, "AP");
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
					session1.addSessionExtension(td_sessionExtension);
				}
				// Session Extension for SD
				if (value.equalsIgnoreCase("Source Definition")) {
					SessionExtension sq_sessionExtension = new SessionExtension();
					sq_sessionExtension.setdSQInstName("SQ_sc_" + sourceProdTableName);
					sq_sessionExtension.setdSQInstType("Source Qualifier");
					sq_sessionExtension.setsInstanceName(key);
					sq_sessionExtension.setName("Relational Reader");
					sq_sessionExtension.setTransformationType(value);
					sq_sessionExtension.setSubtype("Relational Reader");
					sq_sessionExtension.setType("READER");
					session1.addSessionExtension(sq_sessionExtension);
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
					session1.addSessionExtension(sq_sessionExtension2);

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
					session1.addSessionExtension(lk_sessionExtension);
				}
			} else
				break;
		}
		// Attribute att = null;
		Attribute attr1 = new Attribute("General Options", "");
		Attribute attr2 = new Attribute("Write Backward Compatible Session Log File", "NO");
		session1.addAttribute(attr1);
		session1.addAttribute(attr2);
		for (int i = 4; i < 12; i++) {
			String key1 = readExcelController.getCellValue(filePath, sessionSheetName, i, "H");
			String value1 = readExcelController.getCellValue(filePath, sessionSheetName, i, "I");
			if (value1.contains(".0")) {

			} else {
				if(!key1.isEmpty()){
				Attribute att = new Attribute(key1, value1);
				session1.addAttribute(att);
				}
			}
		}
		Attribute a11 = new Attribute("Number of rows to test", "1");
		session1.addAttribute(a11);
		Attribute a33 = new Attribute("Commit Interval", "10000");
		session1.addAttribute(a33);
		Attribute at1 = new Attribute("DTM buffer size", "5000000000");
		session1.addAttribute(at1);
		Attribute attr3 = new Attribute("Parameter Filename", "");
		Attribute attr4 = new Attribute("Enable Test Load", "NO");
		Attribute attr5 = new Attribute("$Source connection value", "");
		Attribute attr6 = new Attribute("$Target connection value", "");
		Attribute attr7 = new Attribute("Commit Type", "Target");
		Attribute attr8 = new Attribute("Commit On End Of File", "YES");
		Attribute attr9 = new Attribute("Rollback Transactions on Errors", "NO");
		Attribute attr10 = new Attribute("Java Classpath", "");
		Attribute attr11 = new Attribute("Performance", "");
		Attribute attr12 = new Attribute("Collect performance data", "NO");
		Attribute attr13 = new Attribute("Write performance data to repository", "NO");
		Attribute attr14 = new Attribute("Incremental Aggregation", "NO");
		Attribute attr15 = new Attribute("Enable high precision", "NO");
		Attribute attr16 = new Attribute("Session retry on deadlock", "YES");
		Attribute attr17 = new Attribute("Pushdown Optimization", "NONE");
		Attribute attr18 = new Attribute("Allow Temporary View for Pushdown", "NO");
		Attribute attr19 = new Attribute("Allow Temporary Sequence for Pushdown", "NO");
		Attribute attr20 = new Attribute("Allow Pushdown for User Incompatible Connections", "NO");

		session1.addAttribute(attr3);
		session1.addAttribute(attr4);
		session1.addAttribute(attr5);
		session1.addAttribute(attr6);
		session1.addAttribute(attr7);
		session1.addAttribute(attr8);
		session1.addAttribute(attr9);
		session1.addAttribute(attr10);
		session1.addAttribute(attr11);
		session1.addAttribute(attr12);
		session1.addAttribute(attr13);
		session1.addAttribute(attr14);
		session1.addAttribute(attr15);
		session1.addAttribute(attr16);
		session1.addAttribute(attr17);
		session1.addAttribute(attr18);
		session1.addAttribute(attr19);
		session1.addAttribute(attr20);
		folder.addSession(session1);

		String stg2ToProdSessionName = readExcelController.getCellValue(filePath, mappingSheetName, 20, "D");
		String stg2ToProdMappingName = readExcelController.getCellValue(filePath, mappingSheetName, 19, "D");
		Session session2 = new Session("", "YES", stg2ToProdMappingName, stg2ToProdSessionName, "YES", "Binary", "1");

		count = 0;
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
					session2.addSessTransformationInst(td_sessTransformationInst);
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

					session2.addSessTransformationInst(sd_sessTransformationInst);
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
					session2.addSessTransformationInst(sq_sessTransformationInst);
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
					session2.addSessTransformationInst(exp_sessTransformationInst);
				}
			} else
				break;
		}

		ConfigReference configRefer = new ConfigReference("Session config", "default_session_config");

		session.setConfigReference(configRefer);
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
					session2.addSessionExtension(td_sessionExtension);
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
					session2.addSessionExtension(sq_sessionExtension);
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
					session2.addSessionExtension(sd_sessionExtension);
				}
			} else
				break;
		}
		// Attribute att = null;
		Attribute attrib1 = new Attribute("General Options", "");
		Attribute attrib2 = new Attribute("Write Backward Compatible Session Log File", "NO");
		session2.addAttribute(attrib1);
		session2.addAttribute(attrib2);
		for (int i = 4; i < 12; i++) {
			String key1 = readExcelController.getCellValue(filePath, sessionSheetName, i, "K");
			String value1 = readExcelController.getCellValue(filePath, sessionSheetName, i, "L");
			if (value1.contains(".0")) {

			} else {
				if(!key1.isEmpty()){
				Attribute att = new Attribute(key1, value1);
				session2.addAttribute(att);
				}
			}
		}
		Attribute attribute11 = new Attribute("Number of rows to test", "1");
		session2.addAttribute(attribute11);
		Attribute attribute33 = new Attribute("Commit Interval", "10000");
		session2.addAttribute(attribute33);
		Attribute attribute44 = new Attribute("DTM buffer size", "5000000000");
		session2.addAttribute(attribute44);
		Attribute attrib3 = new Attribute("Parameter Filename", "");
		Attribute attrib4 = new Attribute("Enable Test Load", "NO");
		Attribute attrib5 = new Attribute("$Source connection value", "");
		Attribute attrib6 = new Attribute("$Target connection value", "");
		Attribute attrib7 = new Attribute("Commit Type", "Target");
		Attribute attrib8 = new Attribute("Commit On End Of File", "YES");
		Attribute attrib9 = new Attribute("Rollback Transactions on Errors", "NO");
		Attribute attrib10 = new Attribute("Java Classpath", "");
		Attribute attrib11 = new Attribute("Performance", "");
		Attribute attrib12 = new Attribute("Collect performance data", "NO");
		Attribute attrib13 = new Attribute("Write performance data to repository", "NO");
		Attribute attrib14 = new Attribute("Incremental Aggregation", "NO");
		Attribute attrib15 = new Attribute("Enable high precision", "NO");
		Attribute attrib16 = new Attribute("Session retry on deadlock", "YES");
		Attribute attrib17 = new Attribute("Pushdown Optimization", "NONE");
		Attribute attrib18 = new Attribute("Allow Temporary View for Pushdown", "NO");
		Attribute attrib19 = new Attribute("Allow Temporary Sequence for Pushdown", "NO");
		Attribute attrib20 = new Attribute("Allow Pushdown for User Incompatible Connections", "NO");

		session2.addAttribute(attrib3);
		session2.addAttribute(attrib4);
		session2.addAttribute(attrib5);
		session2.addAttribute(attrib6);
		session2.addAttribute(attrib7);
		session2.addAttribute(attrib8);
		session2.addAttribute(attrib9);
		session2.addAttribute(attrib10);
		session2.addAttribute(attrib11);
		session2.addAttribute(attrib12);
		session2.addAttribute(attrib13);
		session2.addAttribute(attrib14);
		session2.addAttribute(attrib15);
		session2.addAttribute(attrib16);
		session2.addAttribute(attrib17);
		session2.addAttribute(attrib18);
		session2.addAttribute(attrib19);
		session2.addAttribute(attrib20);
		folder.addSession(session2);

		Worklet work = new Worklet();
		work.setDescription("");
		work.setIsValid("YES");
		work.setName(prodWorkletName);
		work.setReusable("YES");
		work.setVersionNumber("1");
		Task task = new Task("", "DEC_FAIL_SESSION", "NO", "Decision", "1");
		Attribute attribute22 = new Attribute("Decision Name", "");
		task.addAttribute(attribute22);
		work.addTask(task);
		Task task1 = new Task("", "Start", "NO", "Start", "1");
		work.addTask(task1);
		Task task2 = new Task("", "CTRL_STOP_JOB", "NO", "Control", "1");
		Attribute attribute12 = new Attribute("Control Option", "Abort top-level workflow");
		task2.addAttribute(attribute12);
		work.addTask(task2);
		TaskInstance tIns = new TaskInstance("", "NO", "NO", "YES", stg2ToProdSessionName, "YES", stg2ToProdSessionName,
				"Session", "YES");
		work.addTaskInstance(tIns);
		TaskInstance tIns4 = new TaskInstance("", "NO", "YES", "YES", "CTRL_STOP_JOB", "NO", "CTRL_STOP_JOB", "Control",
				"YES");
		work.addTaskInstance(tIns4);
		TaskInstance tIns1 = new TaskInstance("", "YES", "Start", "NO", "Start", "Start");
		work.addTaskInstance(tIns1);
		TaskInstance tIns2 = new TaskInstance("", "NO", "YES", "YES", "DEC_FAIL_SESSION", "NO", "DEC_FAIL_SESSION",
				"Decision", "YES");
		work.addTaskInstance(tIns2);
		TaskInstance tIns3 = new TaskInstance("", "NO", "NO", "YES", stg1ToStg2SessionName, "YES",
				stg1ToStg2SessionName, "Session", "YES");
		work.addTaskInstance(tIns3);
		TaskInstance tIns5 = new TaskInstance("", "NO", "NO", "YES", deleteSessionName, "YES", deleteSessionName,
				"Session", "YES");
		work.addTaskInstance(tIns5);
		WorkFlowLink wLink = new WorkFlowLink("$" + deleteSessionName + ".Status= succeeded", deleteSessionName,
				stg2ToProdSessionName);
		work.addWorkFlowLink(wLink);
		WorkFlowLink wLink1 = new WorkFlowLink("", "DEC_FAIL_SESSION", "CTRL_STOP_JOB");
		work.addWorkFlowLink(wLink1);
		WorkFlowLink wLink2 = new WorkFlowLink(
				"$" + stg1ToStg2SessionName + ".Status=&#x9;FAILED OR&#xD;&#xA;$" + stg1ToStg2SessionName
						+ ".Status=&#x9;ABORTED OR&#xD;&#xA;$" + stg1ToStg2SessionName + ".Status=&#x9;STOPPED",
				stg1ToStg2SessionName, "DEC_FAIL_SESSION");
		work.addWorkFlowLink(wLink2);
		WorkFlowLink wLink3 = new WorkFlowLink("$" + stg2ToProdSessionName + ".Status= FAILED OR $"
				+ stg2ToProdSessionName + ".Status= ABORTED OR $" + stg2ToProdSessionName + ".Status = STOPPED",
				stg2ToProdSessionName, "DEC_FAIL_SESSION");
		work.addWorkFlowLink(wLink3);
		WorkFlowLink wLink4 = new WorkFlowLink("$" + deleteSessionName + ".Status= FAILED OR $" + deleteSessionName
				+ ".Status= ABORTED OR $" + deleteSessionName + ".Status = STOPPED", deleteSessionName,
				"DEC_FAIL_SESSION");
		work.addWorkFlowLink(wLink4);
		WorkFlowLink wLink5 = new WorkFlowLink("", "Start", stg1ToStg2SessionName);
		work.addWorkFlowLink(wLink5);
		WorkFlowLink wLink6 = new WorkFlowLink("$" + stg1ToStg2SessionName + ".Status= Succeeded",
				stg1ToStg2SessionName, deleteSessionName);
		work.addWorkFlowLink(wLink6);
		WorkFlowVariable wVariable = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$DEC_FAIL_SESSION.StartTime", "NO");
		work.addWorkFlowVariable(wVariable);
		WorkFlowVariable wVariable1 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$DEC_FAIL_SESSION.EndTime", "NO");
		work.addWorkFlowVariable(wVariable1);
		WorkFlowVariable wVariable2 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO", "NO",
				"$DEC_FAIL_SESSION.Status", "NO");
		work.addWorkFlowVariable(wVariable2);
		WorkFlowVariable wVariable3 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$DEC_FAIL_SESSION.PrevTaskStatus",
				"NO");
		work.addWorkFlowVariable(wVariable3);
		WorkFlowVariable wVariable4 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$DEC_FAIL_SESSION.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable4);
		WorkFlowVariable wVariable5 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$DEC_FAIL_SESSION.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable5);
		WorkFlowVariable wVariable6 = new WorkFlowVariable("integer", "", "Evaluation result of condition expression",
				"NO", "NO", "$DEC_FAIL_SESSION.Condition", "NO");
		work.addWorkFlowVariable(wVariable6);
		WorkFlowVariable wVariable7 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$Start.StartTime", "NO");
		work.addWorkFlowVariable(wVariable7);
		WorkFlowVariable wVariable8 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$Start.EndTime", "NO");
		work.addWorkFlowVariable(wVariable8);
		WorkFlowVariable wVariable9 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO", "NO",
				"$Start.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable9);
		WorkFlowVariable wVariable10 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$Start.Status", "NO");
		work.addWorkFlowVariable(wVariable10);
		WorkFlowVariable wVariable11 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$Start.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable11);
		WorkFlowVariable wVariable12 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$Start.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable12);
		WorkFlowVariable wVariable13 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$CTRL_STOP_JOB.StartTime", "NO");
		work.addWorkFlowVariable(wVariable13);
		WorkFlowVariable wVariable14 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$CTRL_STOP_JOB.EndTime", "NO");
		work.addWorkFlowVariable(wVariable14);
		WorkFlowVariable wVariable15 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$CTRL_STOP_JOB.Status", "NO");
		work.addWorkFlowVariable(wVariable15);
		WorkFlowVariable wVariable16 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO", "$CTRL_STOP_JOB.PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable16);
		WorkFlowVariable wVariable17 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$CTRL_STOP_JOB.ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable17);
		WorkFlowVariable wVariable18 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$CTRL_STOP_JOB.ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable18);
		WorkFlowVariable wVariable19 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$" + stg2ToProdSessionName + ".StartTime", "NO");
		work.addWorkFlowVariable(wVariable19);
		WorkFlowVariable wVariable20 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$" + stg2ToProdSessionName + ".EndTime", "NO");
		work.addWorkFlowVariable(wVariable20);
		WorkFlowVariable wVariable21 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$" + stg2ToProdSessionName + ".Status", "NO");
		work.addWorkFlowVariable(wVariable21);
		WorkFlowVariable wVariable22 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO",
				"$" + stg2ToProdSessionName + ".PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable22);
		WorkFlowVariable wVariable23 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$" + stg2ToProdSessionName + ".ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable23);
		WorkFlowVariable wVariable24 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$" + stg2ToProdSessionName + ".ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable24);
		WorkFlowVariable wVariable25 = new WorkFlowVariable("integer", "", "Rows successfully read", "NO", "NO",
				"$" + stg2ToProdSessionName + ".SrcSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable25);
		WorkFlowVariable wVariable26 = new WorkFlowVariable("integer", "", "Rows failed to read", "NO", "NO",
				"$" + stg2ToProdSessionName + ".SrcFailedRows", "NO");
		work.addWorkFlowVariable(wVariable26);
		WorkFlowVariable wVariable27 = new WorkFlowVariable("integer", "", "Rows successfully loaded", "NO", "NO",
				"$" + stg2ToProdSessionName + ".TgtSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable27);
		WorkFlowVariable wVariable28 = new WorkFlowVariable("integer", "", "Rows failed to load", "NO", "NO",
				"$" + stg2ToProdSessionName + ".TgtFailedRows", "NO");
		work.addWorkFlowVariable(wVariable28);
		WorkFlowVariable wVariable29 = new WorkFlowVariable("integer", "", "Total number of transformation errors",
				"NO", "NO", "$" + stg2ToProdSessionName + ".TotalTransErrors", "NO");
		work.addWorkFlowVariable(wVariable29);
		WorkFlowVariable wVariable30 = new WorkFlowVariable("integer", "", "First error code", "NO", "NO",
				"$" + stg2ToProdSessionName + ".FirstErrorCode", "NO");
		work.addWorkFlowVariable(wVariable30);
		WorkFlowVariable wVariable31 = new WorkFlowVariable("string", "", "First error message", "NO", "NO",
				"$" + stg2ToProdSessionName + ".FirstErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable31);
		WorkFlowVariable wVariable32 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$" + deleteSessionName + ".StartTime", "NO");
		work.addWorkFlowVariable(wVariable32);
		WorkFlowVariable wVariable33 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$" + deleteSessionName + ".EndTime", "NO");
		work.addWorkFlowVariable(wVariable33);
		WorkFlowVariable wVariable34 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$" + deleteSessionName + ".Status", "NO");
		work.addWorkFlowVariable(wVariable34);
		WorkFlowVariable wVariable35 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO",
				"$" + deleteSessionName + ".PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable35);
		WorkFlowVariable wVariable36 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$" + deleteSessionName + ".ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable36);
		WorkFlowVariable wVariable37 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$" + deleteSessionName + ".ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable37);
		WorkFlowVariable wVariable38 = new WorkFlowVariable("integer", "", "Rows successfully read", "NO", "NO",
				"$" + deleteSessionName + ".SrcSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable38);
		WorkFlowVariable wVariable39 = new WorkFlowVariable("integer", "", "Rows failed to read", "NO", "NO",
				"$" + deleteSessionName + ".SrcFailedRows", "NO");
		work.addWorkFlowVariable(wVariable39);
		WorkFlowVariable wVariable40 = new WorkFlowVariable("integer", "", "Rows successfully loaded", "NO", "NO",
				"$" + deleteSessionName + ".TgtSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable40);
		WorkFlowVariable wVariable41 = new WorkFlowVariable("integer", "", "Rows failed to load", "NO", "NO",
				"$" + deleteSessionName + ".TgtFailedRows", "NO");
		work.addWorkFlowVariable(wVariable41);
		WorkFlowVariable wVariable42 = new WorkFlowVariable("integer", "", "Total number of transformation errors",
				"NO", "NO", "$" + deleteSessionName + ".TotalTransErrors", "NO");
		work.addWorkFlowVariable(wVariable42);
		WorkFlowVariable wVariable43 = new WorkFlowVariable("integer", "", "First error code", "NO", "NO",
				"$" + deleteSessionName + ".FirstErrorCode", "NO");
		work.addWorkFlowVariable(wVariable43);
		WorkFlowVariable wVariable44 = new WorkFlowVariable("string", "", "First error message", "NO", "NO",
				"$" + deleteSessionName + ".FirstErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable44);
		WorkFlowVariable wVariable45 = new WorkFlowVariable("date/time", "", "The time this task started", "NO", "NO",
				"$" + stg1ToStg2SessionName + ".StartTime", "NO");
		work.addWorkFlowVariable(wVariable45);
		WorkFlowVariable wVariable46 = new WorkFlowVariable("date/time", "", "The time this task completed", "NO", "NO",
				"$" + stg1ToStg2SessionName + ".EndTime", "NO");
		work.addWorkFlowVariable(wVariable46);
		WorkFlowVariable wVariable47 = new WorkFlowVariable("integer", "", "Status of this task's execution", "NO",
				"NO", "$" + stg1ToStg2SessionName + ".Status", "NO");
		work.addWorkFlowVariable(wVariable47);
		WorkFlowVariable wVariable48 = new WorkFlowVariable("integer", "",
				"Status of the previous task that is not disabled", "NO", "NO",
				"$" + stg1ToStg2SessionName + ".PrevTaskStatus", "NO");
		work.addWorkFlowVariable(wVariable48);
		WorkFlowVariable wVariable49 = new WorkFlowVariable("integer", "", "Error code for this task's execution", "NO",
				"NO", "$" + stg1ToStg2SessionName + ".ErrorCode", "NO");
		work.addWorkFlowVariable(wVariable49);
		WorkFlowVariable wVariable50 = new WorkFlowVariable("string", "", "Error message for this task's execution",
				"NO", "NO", "$" + stg1ToStg2SessionName + ".ErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable50);
		WorkFlowVariable wVariable51 = new WorkFlowVariable("integer", "", "Rows successfully read", "NO", "NO",
				"$" + stg1ToStg2SessionName + ".SrcSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable51);
		WorkFlowVariable wVariable52 = new WorkFlowVariable("integer", "", "Rows failed to read", "NO", "NO",
				"$" + stg1ToStg2SessionName + ".SrcFailedRows", "NO");
		work.addWorkFlowVariable(wVariable52);
		WorkFlowVariable wVariable53 = new WorkFlowVariable("integer", "", "Rows successfully loaded", "NO", "NO",
				"$" + stg1ToStg2SessionName + ".TgtSuccessRows", "NO");
		work.addWorkFlowVariable(wVariable53);
		WorkFlowVariable wVariable54 = new WorkFlowVariable("integer", "", "Rows failed to load", "NO", "NO",
				"$" + stg1ToStg2SessionName + ".TgtFailedRows", "NO");
		work.addWorkFlowVariable(wVariable54);
		WorkFlowVariable wVariable55 = new WorkFlowVariable("integer", "", "Total number of transformation errors",
				"NO", "NO", "$" + stg1ToStg2SessionName + ".TotalTransErrors", "NO");
		work.addWorkFlowVariable(wVariable55);
		WorkFlowVariable wVariable56 = new WorkFlowVariable("integer", "", "First error code", "NO", "NO",
				"$" + stg1ToStg2SessionName + ".FirstErrorCode", "NO");
		work.addWorkFlowVariable(wVariable56);
		WorkFlowVariable wVariable57 = new WorkFlowVariable("string", "", "First error message", "NO", "NO",
				"$" + stg1ToStg2SessionName + ".FirstErrorMsg", "NO");
		work.addWorkFlowVariable(wVariable57);
		Attribute att = new Attribute("Allow Concurrent Run", "NO");
		work.addAttribute(att);
		folder.addWorklet(work);

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
			FileWriter fw = new FileWriter(outputPath + prodWorkletName + ".xml");
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
