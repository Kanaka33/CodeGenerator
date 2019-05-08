package com.ETLCodeGen.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.ETLCodeGen.model.Column;
@Component 
public class CreateTable {

	private static final Logger logger = Logger.getLogger(UploadController.class);

	private Properties excelinfoProperties;
	
	public Properties getExcelinfoProperties() {
		return excelinfoProperties;
	}

	public void setExcelinfoProperties(Properties excelinfoProperties) {
		this.excelinfoProperties = excelinfoProperties;
	}

	List<String> list = new ArrayList<String>();

	public boolean checkTablesDataFromExcel(String path, String cbuName) throws Exception {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path);
			//System.out.println("drivername::"+drivername);
			/* NPOIFSFileSystem fileSystem = new NPOIFSFileSystem(new File(path));
		        
		        Biff8EncryptionKey.setCurrentUserPassword("1234");*/
		} catch (FileNotFoundException e) {
			logger.error("File not found in the specified path.");
			logger.error(e.getStackTrace());
			throw new Exception(e.getMessage());
		}
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet mdSheet = workbook.getSheet("Mapping Details");
		int rowEnd = mdSheet.getLastRowNum() - 1;
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyyHHmmss");
		Date date = new Date();
		String schemaName = "";
		String mdj22 = "";
		String mdz22 = "";
		String mdap22 = "";
		String mdbr22 = "";
		String type = mdSheet.getRow(3).getCell(CellReference.convertColStringToIndex("G")).getStringCellValue();;
		if(type.equalsIgnoreCase("Type 2")) {
		 mdj22 = mdSheet.getRow(22).getCell(CellReference.convertColStringToIndex("J")).getStringCellValue();
		 mdz22 = mdSheet.getRow(22).getCell(CellReference.convertColStringToIndex("Z")).getStringCellValue();
		 mdap22 = mdSheet.getRow(22).getCell(CellReference.convertColStringToIndex("AP")).getStringCellValue();
		}else {
		 mdj22 = mdSheet.getRow(22).getCell(CellReference.convertColStringToIndex("J")).getStringCellValue();
		 mdap22 = mdSheet.getRow(22).getCell(CellReference.convertColStringToIndex("AP")).getStringCellValue();
		// mdbr22 = mdSheet.getRow(22).getCell(CellReference.convertColStringToIndex("BR")).getStringCellValue();
		}
		Map<String, List<Column>> tableColumnMap = new HashMap<String, List<Column>>();

		// List<Column> bCols = new ArrayList<Column>();
		List<Column> fCols = new ArrayList<Column>();
		List<Column> jCols = new ArrayList<Column>();
		List<Column> nCols = new ArrayList<Column>();
		List<Column> kCols = new ArrayList<Column>();
		int rowStart = 25;

		/*
		 * for(int row=rowStart; row<=rowEnd; row++) {
		 * 
		 * if(!isCellEmpty(mdSheet.getRow(row).getCell(CellReference.
		 * convertColStringToIndex("B")))){
		 * 
		 * Column col = new Column();
		 * col.setColumnName(mdSheet.getRow(row).getCell(CellReference.
		 * convertColStringToIndex("B")).getStringCellValue());
		 * //logger.info("column name is.."+mdSheet.getRow(row).getCell(CellReference.
		 * convertColStringToIndex("B")).getStringCellValue()); String dataType =
		 * mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("C")).
		 * getStringCellValue();
		 * 
		 * if(dataType.toLowerCase().contains("char")) { col.setDataType("VARCHAR2");
		 * }else if(dataType.equalsIgnoreCase("DECIMAL") ||
		 * dataType.toLowerCase().contains("num")) { col.setDataType("NUMERIC"); }else
		 * if(dataType.equalsIgnoreCase("DATE") ||
		 * dataType.toLowerCase().contains("times")) { col.setDataType("DATE"); }
		 * //logger.info("column datatype is.."+col.getDataType());
		 * 
		 * if(dataType.toLowerCase().contains("char2(") ||
		 * dataType.toLowerCase().contains("varchar2(")) {
		 * col.setDataType(dataType.toUpperCase()); }else {
		 * col.setLength((int)mdSheet.getRow(row).getCell(CellReference.
		 * convertColStringToIndex("F")).getNumericCellValue());
		 * 
		 * //logger.info("column length is.."+(int)mdSheet.getRow(row).getCell(
		 * CellReference.convertColStringToIndex("F")).getNumericCellValue());
		 * bCols.add(col); } }else break; }
		 */
		// kcols
				int emptyCount = 0;
				for (int row = rowStart; row <= rowEnd; row++) {
					if (isCellEmpty(mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("BR")))) {
						emptyCount++;
					}
					else {
						Column col = new Column();
						col.setColumnName(
								mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("BR")).getStringCellValue());
						//logger.info("column name is.."+ mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("BR")).getStringCellValue());
						String dataType = mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("BS"))
								.getStringCellValue();

						if (dataType.equalsIgnoreCase("char") || dataType.equalsIgnoreCase("varchar2")) {
							col.setDataType("VARCHAR2");
						} else if (dataType.equalsIgnoreCase("numeric") || dataType.equalsIgnoreCase("number")
								|| dataType.equalsIgnoreCase("decimal") || dataType.equalsIgnoreCase("number(p,s)")) {
							col.setDataType("NUMERIC");
						} else if (dataType.equalsIgnoreCase("DATE") || dataType.equalsIgnoreCase("Timestamp")) {
							col.setDataType("DATE");
						}
						//logger.info("column datatype is.." + col.getDataType());

						col.setLength((int) mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("BV"))
								.getNumericCellValue());
						//logger.info("column length is.." + (int) mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("BV")).getNumericCellValue());
						fCols.add(col);
					}
					if (emptyCount > 10)
						break;
				}
		// fcols
		emptyCount = 0;
		int count = 0;
		for (int row = rowStart; row <= rowEnd; row++) {
			if (isCellEmpty(mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("J")))) {
				emptyCount++;
			}
			else if (!isCellEmpty(mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("K")))) {
				Column col = new Column();
				if(mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("J")).getStringCellValue().equalsIgnoreCase("REC_CREATE_DATE")) {
					count++;
				}
				col.setColumnName(
						mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("J")).getStringCellValue());
				logger.info("column name is.."+ mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("J")).getStringCellValue());
				String dataType = mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("K"))
						.getStringCellValue();

				if (dataType.equalsIgnoreCase("char") || dataType.equalsIgnoreCase("varchar2") || dataType.equalsIgnoreCase("String")) {
					col.setDataType("VARCHAR2");
				} else if (dataType.equalsIgnoreCase("numeric") || dataType.equalsIgnoreCase("number")
						|| dataType.equalsIgnoreCase("decimal") || dataType.equalsIgnoreCase("number(p,s)")) {
					col.setDataType("NUMERIC");
				} else if (dataType.equalsIgnoreCase("DATE") || dataType.equalsIgnoreCase("Timestamp")) {
					col.setDataType("DATE");
				}
				logger.info("column datatype is.." + col.getDataType());

				col.setLength((int) mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("N"))
						.getNumericCellValue());
				//logger.info("column length is.." + (int) mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("N")).getNumericCellValue());
				fCols.add(col);
			}
			if (emptyCount > 10)
				break;
		}
		if(count<1) {
			Column col = new Column();
			col.setColumnName("REC_CREATE_DATE");
			col.setDataType("DATE");
			col.setLength(19);
			fCols.add(col);
		}
		// jcols
		emptyCount = 0;
		for (int row = rowStart; row <= rowEnd; row++) {
			if (isCellEmpty(mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("Z")))) {
				emptyCount++;
			}
			else {
				Column col = new Column();
				col.setColumnName(
						mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("Z")).getStringCellValue());
				// logger.info("column name
				// is.."+mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("Z")).getStringCellValue());

				String dataType = mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("AA"))
						.getStringCellValue();
				if (dataType.equalsIgnoreCase("char") || dataType.equalsIgnoreCase("varchar2")) {
					col.setDataType("VARCHAR2");
				} else if (dataType.equalsIgnoreCase("numeric") || dataType.equalsIgnoreCase("number(p,s)")
						|| dataType.equalsIgnoreCase("number") || dataType.equalsIgnoreCase("decimal")) {
					col.setDataType("NUMERIC");
				} else if (dataType.equalsIgnoreCase("DATE") || dataType.equalsIgnoreCase("Timestamp")) {
					col.setDataType("DATE");
				}
				// logger.info("column datatype is.."+col.getDataType());

				col.setLength((int) mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("AD"))
						.getNumericCellValue());
				// logger.info("column length is.."+(int)mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("AD")).getNumericCellValue());

				jCols.add(col);
			}
			if (emptyCount > 10)
				break;
		}
		// ncols
		emptyCount = 0;
		for (int row = rowStart; row <= rowEnd; row++) {
			if (isCellEmpty(mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("AP")))) {
				emptyCount++;
			}
			else {
				Column col = new Column();
				col.setColumnName(
						mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("AP")).getStringCellValue());
				// logger.info("column name
				// is.."+mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("AP")).getStringCellValue());
				String dataType = mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("AQ"))
						.getStringCellValue();

				if (dataType.equalsIgnoreCase("char") || dataType.equalsIgnoreCase("varchar2")) {
					col.setDataType("VARCHAR2");
				} else if (dataType.equalsIgnoreCase("numeric") || dataType.equalsIgnoreCase("number(p,s)")
						|| dataType.equalsIgnoreCase("number") || dataType.equalsIgnoreCase("decimal")) {
					col.setDataType("NUMERIC");
				} else if (dataType.equalsIgnoreCase("DATE") || dataType.equalsIgnoreCase("Timestamp")) {
					col.setDataType("DATE");
				}

				col.setLength((int) mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("AT"))
						.getNumericCellValue());
				 //logger.info("column length is.."+(int)mdSheet.getRow(row).getCell(CellReference.convertColStringToIndex("AT")).getNumericCellValue());

				nCols.add(col);
			}
			if (emptyCount > 10)
				break;
		}
		if(type.equalsIgnoreCase("Type 1") ) {
			//tableColumnMap.put(mdbr22, kCols);
			tableColumnMap.put(mdj22, fCols);
			tableColumnMap.put(mdap22, nCols);
		}else {
			tableColumnMap.put(mdj22, fCols);
			tableColumnMap.put(mdz22, jCols);
			tableColumnMap.put(mdap22, nCols);
		}
		Set<Boolean> tableStatus = new HashSet<Boolean>();
		String fileName = "C:/OutputFiles/Queries_" + dateFormat.format(date) + ".txt";
		FileWriter fileWriter = new FileWriter(fileName);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for (Map.Entry<String, List<Column>> entry : tableColumnMap.entrySet()) {
			if(type.equalsIgnoreCase("Type2") ) {
			if (entry.getKey().equalsIgnoreCase(mdj22) || entry.getKey().equalsIgnoreCase(mdz22)) {
				schemaName = cbuName + "_STAGE";
			} else {
				schemaName = cbuName + "_PROD";
			}
				}else {
					if (entry.getKey().equalsIgnoreCase(mdj22)) {
						schemaName = cbuName + "_STAGE";
					} else {
					schemaName = cbuName + "_ODS";
					}
				}
			boolean tableCreationSuccessful = createTableFromPojo(schemaName, entry.getKey(), entry.getValue());
			tableStatus.add(tableCreationSuccessful);
		}
		if (tableStatus.contains(true)) {
			if (list != null) {
				for (String s : list) {
					bufferedWriter.write(s);
					bufferedWriter.newLine();// append new line
				}
			}
			bufferedWriter.close();
			return true;
		}
		bufferedWriter.close();
		return false;
	}

	/*
	 * //encrypt BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
	 * textEncryptor.setPassword(myEncryptionPassword);
	 * 
	 * String myEncryptedText = textEncryptor.encrypt(myText); //decrypt
	 * BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
	 * textEncryptor.setPassword(myEncryptionPassword);
	 * 
	 * String plainText = textEncryptor.decrypt(myEncryptedText);
	 * 
	 */

	public boolean createTableFromPojo(String schemaName, String table_name, List<Column> column) throws SQLException {

		String query = "";
		StringBuilder sb1 = new StringBuilder();
		String driver = excelinfoProperties.getProperty("drivername");
		String connection = "";
		String username = "";
		String password = "";
		/*if (schemaName.equalsIgnoreCase("KMA_STAGE")) {
			System.out.println("KMA STAGE::" + schemaName);
			connection = excelinfoProperties.getProperty("kmaconnectionname");
			username = excelinfoProperties.getProperty("kmaStgusername");
			password = excelinfoProperties.getProperty("kmaStgpassword");
		}else if(schemaName.equalsIgnoreCase("KMA_PROD")) {
			System.out.println("KMA PROD::" + schemaName);
			connection = excelinfoProperties.getProperty("kmaconnectionname");
			username = excelinfoProperties.getProperty("kmaProdusername");
			password = excelinfoProperties.getProperty("kmaProdpassword");
			
		}else if(schemaName.equalsIgnoreCase("HMA_STAGE")) {
			System.out.println("HMA STAGE::" + schemaName);
			connection = excelinfoProperties.getProperty("hmaconnectionname");
			username = excelinfoProperties.getProperty("hmaStgusername");
			password = excelinfoProperties.getProperty("hmaStgpassword");
			
		}else if(schemaName.equalsIgnoreCase("HMA_PROD")) {
			System.out.println("HMA PROD::" + schemaName);
			connection = excelinfoProperties.getProperty("hmaconnectionname");
			username = excelinfoProperties.getProperty("hmaProdusername");
			password = excelinfoProperties.getProperty("hmaProdpassword");
			
		}else if(schemaName.equalsIgnoreCase("HMA_ODS")) {
			System.out.println("HMA ODS::" + schemaName);
			connection = excelinfoProperties.getProperty("dkwhconnectionname");
			username = excelinfoProperties.getProperty("dkwhusername");
			password = excelinfoProperties.getProperty("dkwhpaswd");
			
		}else if(schemaName.equalsIgnoreCase("HMA_ODS")) {
			System.out.println("HMA ODS::" + schemaName);
			connection = excelinfoProperties.getProperty("dhodsconnectionname");
			username = excelinfoProperties.getProperty("dhodsusername");
			password = excelinfoProperties.getProperty("dhodspassword");
			
		}

		try {
			Class.forName(driver);
			System.out.println("driver::" + driver);
			// Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("driver::in exception clause" + driver);
			logger.error(e.getMessage());
		}
		boolean tableCreationSuccessful = true;
		// Connection
		Connection con = DriverManager.getConnection(connection, username, password);
		System.out.println("con::" + con);
		con.setAutoCommit(false);
		Statement stmt = con.createStatement();
		DatabaseMetaData dbm = con.getMetaData();
		ResultSet rs = dbm.getTables(null, schemaName, table_name, null);
		if (rs.next()) {
			String sb = "Table " + schemaName + "." + table_name + " already exists..";
			list.add(sb);
			logger.info(sb);

		} else {
			*/
		try
		{
	    	
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {

			logger.error(e.getMessage());
		}  
		boolean tableCreationSuccessful = true;
		//Connection con=DriverManager.getConnection(connection,username,password);
		Connection con=DriverManager.getConnection("jdbc:oracle:thin:@hmaipcvadb-scan:1521/dhods","HIS60040","SRaina5*");
		con.setAutoCommit(false);
		Statement stmt=con.createStatement();  
		
		DatabaseMetaData dbm = con.getMetaData();
	    ResultSet rs = dbm.getTables(null, null, table_name , null);
	    if (rs.next()) {
	    	String sb = "Table " + table_name + " already exists..";
			list.add(sb);
	      logger.info("Table already exists.."); 
	      
	    } else {
	    	try {
				 sb1.append("create table "+table_name+"(");
	    			Iterator<Column> itr = column.iterator();
	    			 while(itr.hasNext()) {
				/*sb1.append("create table " + schemaName + "." + table_name + "(");
				Iterator<Column> itr = column.iterator();
				while (itr.hasNext()) {*/

					Column element = (Column) itr.next();
					if (element.getDataType().equals("NUMERIC") || element.getDataType().equals("DATE")) {
						sb1.append(element.getColumnName() + " " + element.getDataType() + " " + " " + ",");
					} else {
						sb1.append(element.getColumnName() + " " + element.getDataType() + " " + "("
								+ (int) element.getLength() + ")" + ",");
					}
				}
				sb1.append(")");

				if (sb1.length() > 0) {
					sb1.deleteCharAt(sb1.lastIndexOf(","));
				}
				query = sb1.toString();
				logger.info("sb1 formed .." + sb1);
				list.add(query);
				// writer.println(query);
				stmt.execute(query);
				con.commit();

			} catch (Exception e) {
				logger.info("inside rollback function.." + e.getMessage());
				logger.error(e.getStackTrace());
				con.rollback();
				// writer.close();
				tableCreationSuccessful = false;

			}
		}
		return tableCreationSuccessful;
	}

	public static boolean isCellEmpty(final XSSFCell cell) {
		if (cell == null) {
			return true;
		}

		if (cell.getStringCellValue().isEmpty()) {
			return true;
		}

		return false;
	}

}
