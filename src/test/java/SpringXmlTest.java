import com.ETLCodeGen.controller.InsUpdMappingController;
import com.ETLCodeGen.controller.ReadExcelController;
import com.ETLCodeGen.controller.XmlGeneratorController;
import com.ETLCodeGen.model.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml"})
public class SpringXmlTest {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    @Autowired
    XmlGeneratorController xmlGeneratorController;

    @Autowired
    ReadExcelController readExcelController;
    
    @Autowired
    InsUpdMappingController insUpdMappingController;
    
   /* @Test
      public void testSessionValues()throws JAXBException,IOException {
        Date date = new Date();
        String dateStr = simpleDateFormat.format(date);

        String filePath = "D:/ETL/ETL_Code_Generator-KMF.xlsx";
        String outputPath = "D:/output/";
        try{
            xmlGeneratorController.generateSourceToStage1XML(filePath,outputPath);
            xmlGeneratorController.generateStage1ToStage2XML(filePath,outputPath);
        }catch (Exception e){

        }
    }*/

    @Test
    public void testMappingIndUpd()throws JAXBException{
        Date date = new Date();
        String dateStr = simpleDateFormat.format(date);

        String filePath = "D:/ETL/ETL_Code_Generator-KMF.xlsx";
        String sheetName = "Mapping Details";
        String cbuName = "";
        // Fetch Folder name from excel
        String folderName = readExcelController.getCellValue(filePath, sheetName, 1, "D");
        String repoName = readExcelController.getCellValue(filePath, sheetName, 2, "D");
        PowerMart powerMart = new PowerMart("184.93", dateStr);
        String MappingName = readExcelController.getCellValue(filePath, sheetName, 9, "D");
        Repository repository = new Repository(repoName, "184", "UTF-8", "Oracle");
        String uuid = UUID.randomUUID().toString();
        if (folderName.startsWith("KMA")) {
            cbuName = "KMA";
        } else {
            cbuName = "HMA";
        }
        String dvTableName = readExcelController.getCellValue(filePath, sheetName, 23, "AP");
        String lookupTableName = readExcelController.getCellValue(filePath, sheetName, 24, "BB");
        String connectionInfo = readExcelController.getCellValue(filePath, sheetName, 46, "BJ");
        String stgTableName = readExcelController.getCellValue(filePath, sheetName, 23, "J");
        String stg2TableName = readExcelController.getCellValue(filePath, sheetName, 23, "Z");
        Folder folder = new Folder(cbuName + "_SHARED", "", "INFA_ADMIN", SharedEnum.SHARED, "", "rwx------", uuid);
        Transformation transformation = new Transformation("", lookupTableName, "1", "YES", "Lookup Procedure", "1");
        transformation.setTransformFieldList(insUpdMappingController.getTransformFieldForLookupTransformation(filePath));
        transformation.setTableAttributeList(insUpdMappingController.getTableAttributesForLookupTransformation(filePath, dvTableName, connectionInfo));
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

        Transformation sqTransformation = new Transformation();
        sqTransformation.setName("SQTRANS");
        sqTransformation.setDescription("");
        sqTransformation.setObjectVersion("1");
        sqTransformation.setVersionNumber("1");
        sqTransformation.setReUsable("NO");
        sqTransformation.setType("Source Qualifier");

        sqTransformation.setTransformFieldList(insUpdMappingController.getTransformFieldSQTransformation(filePath));
        sqTransformation.setTableAttributeList(insUpdMappingController.getTableAttributesForSQTransformation(filePath, cbuName));
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
        rtrTransformation.addGroup(renewGroup);

        Group deafultGroup = new Group();
        deafultGroup.setDescription("Path for the data when none of the group conditions are satisfied.");
        deafultGroup.setName("DEFAULT1");
        deafultGroup.setOrder("5");
        deafultGroup.setType("OUTPUT/DEFAULT");
        rtrTransformation.addGroup(deafultGroup);

        List<TransformField> transformFieldList = new ArrayList<TransformField>();
        transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation(filePath, "INPUT"));
        transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation(filePath, "INSERT"));
        transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation(filePath, "UPDATE"));
        transformFieldList.addAll(insUpdMappingController.getTransformFieldRTRTransformation(filePath, "RENEW"));
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
        TargetLoader load2 = new TargetLoader();
        load2.setOrder("1");
        load2.setTargetInstance("sc_" + stg2TableName + "_REN");
        mapping.addTargetLoader(load);
        mapping.addTargetLoader(load1);
        mapping.addTargetLoader(load2);
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
        marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE POWERMART SYSTEM  \"powrmart.dtd\">");
        marshaller.marshal(powerMart, new File("C:/OutputFiles/" + MappingName + ".xml"));
    }


}
