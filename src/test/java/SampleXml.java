import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ETLCodeGen.model.Folder;
import com.ETLCodeGen.model.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class SampleXml {
	    public static void main(String args[]) { 
	    	try {
	    		int count = 0;
	    		List<String> fewLines = new ArrayList<String>();
				List<String> allLines = Files.readAllLines(Paths.get("D:/ExceltoXmlTask/OneDrive_1_7-31-2018/sc_XTIME_APPT_XML.XML"));
				for (String line : allLines) {
					System.out.println("111 line:"+line);
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
						/*Folder folder = new Folder();
						
						folder.addSource((Source)fewLines);*/
					//System.out.println("line:"+line);
				

							
				} catch (IOException e) {
				e.printStackTrace();
			}
	        try {/* 
	            // parse the document
	        	 OutputStream os = null;
	 		   // File xsdFile = new File("D:/ExceltoXmlTask/OneDrive_1_7-31-2018/adf.xsd");
	 		   File xmlFile = new File("D:/ExceltoXmlTask/OneDrive_1_7-31-2018/sc_HyundaiSmartLaneCheckIn.XML");
	 		 // System.out.println("UploadFiles");
	 	        DateFormat dateFormat = new SimpleDateFormat("MMddyyyyHHmmss");
	 			Date date = new Date();
	 		
	 			//File inputFile = new File("input.txt");
	 	         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	 	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	 	         Document doc = dBuilder.parse(xmlFile);
	 	         doc.getDocumentElement().normalize();
	 	         System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	        	//DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	            //DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	            //Document doc = docBuilder.parse (new File("D:/ExceltoXmlTask/OneDrive_1_7-31-2018/adf.xsd"));
	           // Document docxml = docBuilder.parse (new File("D:/ExceltoXmlTask/OneDrive_1_7-31-2018/sc_XTIME_APPT_XML.XML"));//sc_XTIME_APPT_XML.XML
	           // System.out.println("xsdFile::"+xsdFile.toString());
	            //NodeList list = doc.getElementsByTagName("xs:element"); 
	           // NodeList sourcelist = docxml.getElementsByTagName("source"); 
	          // BufferedReader br = new BufferedReader(new FileReader(xsdFile));
	           //String line = "";
	          // BufferedReader br = new BufferedReader(new FileReader(xmlpath));
	           while((br.readLine()) != null){
					//System.out.println("Line:"+line);
					line = line.concat(br.readLine());
					line.replaceAll("<", "&lt;");
				}
				
				
				BufferedReader xmlbr = new BufferedReader(new FileReader(xmlFile));
		           String xmlline = "";
		           //System.out.print("xmlbr.read()==9"+xmlbr.);
		          // BufferedReader br = new BufferedReader(new FileReader(xmlpath));
		          int count=0;
		        	   
		           while((xmlbr.readLine()) != null){
						xmlline = xmlbr.readLine();
						if(xmlline.startsWith("<SOURCE")) {
							count++;
						}
						if(xmlline.startsWith("</FOLDER")) {
							count=0;
						}
						if(count>0) {
						System.out.println("Line:"+xmlline);
						//xmlline.replaceAll("<", "&lt;");
					//}
		           }
		         
				//	System.out.print("Line:"+line);
				
	            //NodeList compList = doc.getElementsByTagName("xs:element");
	            // Node n = (Node)doc.getElementsByTagName("xs:element");
	            //loop to print data
	            for(int i = 0 ; i < list.getLength(); i++)
	            {
	            	 System.out.println("content:::"+doc.getTextContent());
	            	Element first = (Element)list.item(i);
	            	Node node = list.item(i);
	                if(first.hasAttributes())
	                {
	                	 String name = first.getAttribute("name"); 
		                 String type = first.getAttribute("type"); 
		                 if(node.hasChildNodes()) {
		                    	if(type.isEmpty()) {
		                    		System.out.println("has child type null:"+name); 
		                    	}else {
		                    		System.out.println("has child name:"+name);
		                    		System.out.println("has child type:"+type);
		                    	}
		                 }
		                 else {
		                	 if(type.isEmpty()) {
		                		 	System.out.println("no child - name for type null:"+name);	
		                    		System.out.println("no child - change type if type null:"+"String"); 
		                     }else {
		                    	 System.out.println("else loop");
		                    	 System.out.println("no child name:"+name); 
		                    	 System.out.println("no child type:"+type); 
		                     }
		                 }
	                }
	            }
	        */} 
	       /* catch (ParserConfigurationException e) 
	        {
	            e.printStackTrace();
	        }
	        catch (SAXException e) 
	        { 
	            e.printStackTrace();
	        }*/
	        catch (Exception ed) 
	        {
	            ed.printStackTrace();
	        }
	        //need to check whether data can be inserted from source.xml file
	    }
}
