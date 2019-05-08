import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ETLCodeGen.controller.ReadExcelController;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml"})
public class SpringExcelTest {

    @Autowired
    ReadExcelController readExcelController;

    @Test
    public void testReadExcel()throws IOException {
        String filePath = "D:/ETL/ETL_Code_Generator-UserInput - KMF Application.xlsx";
        String sheetName  = "Mapping Details";
        // Folder Name
        String folderName = readExcelController.getCellValue(filePath,sheetName,1,"C");
        System.out.println(folderName);
        //Source to Stage1 Mapping Name
        String srcToStg1MappingName = readExcelController.getCellValue(filePath,sheetName,4,"C");
        System.out.println(srcToStg1MappingName);

        //Source to Stage1 Session Name
        String srcToStg1SessionName = readExcelController.getCellValue(filePath,sheetName,5,"C");
        System.out.println(srcToStg1SessionName);

        //Source to Stage1 Worklet Name
        String srcToStg1WorkletName = readExcelController.getCellValue(filePath,sheetName,6,"C");
        System.out.println(srcToStg1WorkletName);

        //Stage1 to Stage2 Mapping Name
        String stg1ToStg2MappingName = readExcelController.getCellValue(filePath,sheetName,9,"C");
        System.out.println(stg1ToStg2MappingName);

        //Stage1 to Stage2 Session Name
        String stg1ToStg2SessionName = readExcelController.getCellValue(filePath,sheetName,10,"C");
        System.out.println(stg1ToStg2SessionName);

        //Stage1 to Stage2 Worklet Name
        String stg1ToStg2WorkletName = readExcelController.getCellValue(filePath,sheetName,11,"C");
        System.out.println(stg1ToStg2WorkletName);

        //Delete Mapping Name
        String delMappingName = readExcelController.getCellValue(filePath,sheetName,14,"C");
        System.out.println(delMappingName);

        //Delete Session Name
        String delSessionName = readExcelController.getCellValue(filePath,sheetName,15,"C");
        System.out.println(delSessionName);

        //Delete Worklet Name
        String delWorkletName = readExcelController.getCellValue(filePath,sheetName,16,"C");
        System.out.println(delWorkletName);

        //DV Mapping Name
        String dvMappingName = readExcelController.getCellValue(filePath,sheetName,19,"C");
        System.out.println(dvMappingName);

        //DV Session Name
        String dvSessionName = readExcelController.getCellValue(filePath,sheetName,20,"C");
        System.out.println(dvSessionName);

        //DV Worklet Name
        String dvWorkletName = readExcelController.getCellValue(filePath,sheetName,21,"C");
        System.out.println(dvWorkletName);
    }
}
