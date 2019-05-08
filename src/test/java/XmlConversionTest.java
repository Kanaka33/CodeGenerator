import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ETLCodeGen.model.Attribute;
import com.ETLCodeGen.model.Config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class XmlConversionTest {
    Config config;
    @Before
    public void setUp(){
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(new Attribute("Advanced",""));
        attributeList.add(new Attribute("Constraint based load ordering","NO"));
        attributeList.add(new Attribute("Cache LOOKUP() function","YES"));
        attributeList.add(new Attribute("Default buffer block size","256000"));
        attributeList.add(new Attribute("Line Sequential buffer length","2048"));
        attributeList.add(new Attribute("Maximum Memory Allowed For Auto Memory Attributes","640MB"));
        attributeList.add(new Attribute("Maximum Percentage of Total Memory Allowed For Auto Memory Attributes","10"));
        attributeList.add(new Attribute("Additional Concurrent Pipelines for Lookup Cache Creation","0"));
        attributeList.add(new Attribute("Custom Properties",""));
        attributeList.add(new Attribute("Pre-build lookup cache","Auto"));
        attributeList.add(new Attribute("Optimization Level","Medium"));
        attributeList.add(new Attribute("DateTime Format String","MM/DD/YYYY HH24:MI:SS.US"));
        attributeList.add(new Attribute("Pre 85 Timestamp Compatibility","YES"));
        attributeList.add(new Attribute("Log Options","0"));
        attributeList.add(new Attribute("Save session log by","Session runs"));
        attributeList.add(new Attribute("Save session log for these runs","0"));
        attributeList.add(new Attribute("Session Log File Max Size","0"));
        attributeList.add(new Attribute("Session Log File Max Time Period","0"));
        attributeList.add(new Attribute("Maximum Partial Session Log Files","1"));
        attributeList.add(new Attribute("Writer Commit Statistics Log Frequency","1"));
        attributeList.add(new Attribute("Writer Commit Statistics Log Interval","0"));
        attributeList.add(new Attribute("Error handling",""));
        attributeList.add(new Attribute("Stop on errors","0"));
        attributeList.add(new Attribute("Override tracing","None"));
        attributeList.add(new Attribute("On Stored Procedure error","Stop"));
        attributeList.add(new Attribute("On Pre-session command task error","Stop"));
        attributeList.add(new Attribute("On Pre-Post SQL error","Stop"));
        attributeList.add(new Attribute("Enable Recovery","NO"));
        attributeList.add(new Attribute("Error Log Type","None"));
        attributeList.add(new Attribute("Error Log Table Name Prefix",""));
        attributeList.add(new Attribute("Error Log File Name","PMError.log"));
        attributeList.add(new Attribute("Log Source Row Data","NO"));
        attributeList.add(new Attribute("Data Column Delimiter","|"));
        attributeList.add(new Attribute("Partitioning Options",""));
        attributeList.add(new Attribute("Dynamic Partitioning","Disabled"));
        attributeList.add(new Attribute("Number of Partitions","1"));
        attributeList.add(new Attribute("Multiplication Factor","Auto"));
        attributeList.add(new Attribute("Session on Grid",""));
        attributeList.add(new Attribute("Is Enabled","NO"));
    }

    @Test
    public void testObjectToXml() throws JAXBException, FileNotFoundException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
        marshaller.marshal(config, new File("C:/OutputFiles/config.xml"));
        marshaller.marshal(config, System.out);
    }
    @After
    public void tearDown(){
    }
}
