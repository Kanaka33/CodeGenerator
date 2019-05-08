import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ETLCodeGen.controller.UploadController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml"})
public class SpringFinalTest {

    @Autowired
    UploadController uploadController;

    @Test
    public void testGenerateInsUpd(){
    	String filePath = "D:/ETL/ETL_Code_Generator_20-Aug-KMA.xlsx";
    	//String filePath = "D:/ETL/ETL_Code_Generator_21-Aug-HMA.xlsx";
        try {
            //uploadController.generateInsUpdXML(filePath);
        	uploadController.generateStgProdMapping(filePath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
