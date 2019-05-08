package com.ETLCodeGen.controller;

import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("excel/")
public class ReadExcelController {

    Map<String,Workbook> workbookMap = new HashMap<String, Workbook>();
    DecimalFormat df = new DecimalFormat("###");

    @RequestMapping("getValue/{filePath}/{sheetName}/{rowNum}/{columnName}")
    public String getCellValue(@PathVariable String filePath,@PathVariable String sheetName,@PathVariable int rowNum,@PathVariable String columnName){
    	Workbook workbook = null; 
    	try {
    		/* NPOIFSFileSystem fileSystem = new NPOIFSFileSystem(new File(filePath));
         
         Biff8EncryptionKey.setCurrentUserPassword("1234");*/
         //Workbook workbook = new XSSFWorkbook(inputStream);
         //workbook = new HSSFWorkbook(fileSystem);
         workbook = workbookMap.get(filePath);;
       
            if(workbook == null) {
                workbook = WorkbookFactory.create(new File(filePath));
                workbookMap.put(filePath,workbook);
            }
            Sheet sheet = workbook.getSheet(sheetName);
            Cell cell = sheet.getRow(rowNum - 1).getCell(CellReference.convertColStringToIndex(columnName));
            if(cell != null)
                return readCellByType(cell, cell.getCellTypeEnum());
        } catch (Exception e) {
            return "";
        } finally {
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    private String readCellByType(Cell cell, CellType type) {
        String txt = "";
        if (cell != null) {
            switch (type) {
                case NUMERIC:
                    txt = dateOrNumberProcessing(cell);
                    break;
                case STRING:
                    txt = String.valueOf(cell.getRichStringCellValue());
                    break;
                case FORMULA:
                    txt = readCellByType(cell, cell.getCachedFormulaResultTypeEnum());
                    break;
                case BLANK:
                    break;
                default:
                    break;
            }
        }
        return txt;
    }

    private String dateOrNumberProcessing(Cell cell) {
        String txt;
        if (DateUtil.isCellDateFormatted(cell)) {
            final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            txt = String.valueOf(formatter.format(cell.getDateCellValue()));
        } else {
            txt = String.valueOf(cell.getNumericCellValue());
        }
        return txt;
    }

    @RequestMapping("closeWorkBook/{filePath}/{sheetName}")
    public void closeWorkBookForFile(@PathVariable String filePath)throws IOException{
        Workbook workbook = workbookMap.get(filePath);
        if(workbook != null){
            workbook.close();
        }
    }

    @RequestMapping("getMapForSourceToStage1/{filePath}/{sheetName}")
    public Map<String,String> getSessionDetailsForSourceToStage1(@PathVariable String filePath,@PathVariable String sheetName)throws IOException{
        Map<String,String> sourceMap = new HashMap<String, String>();
        for(int i=4;i<53;i++){
        	
            String cellKey = getCellValue(filePath,sheetName,i,"B");
            System.out.println("cellKey"+cellKey);
            if(!cellKey.isEmpty()) {
                String cellValue = getCellValue(filePath, sheetName, i, "C");
                sourceMap.put(cellKey, cellValue);
            }
        }
        return sourceMap;
    }

    @RequestMapping("getMapForStage1ToStage2/{filePath}/{sheetName}")
    public Map<String,String> getSessionDetailsForStage1ToStage2(@PathVariable String filePath,@PathVariable String sheetName)throws IOException{
        Map<String,String> sourceMap = new HashMap<String, String>();
        for(int i=4;i<53;i++){
            String cellKey = getCellValue(filePath,sheetName,i,"E");
            if(!cellKey.isEmpty()) {
                String cellValue = getCellValue(filePath, sheetName, i, "F");
                sourceMap.put(cellKey, cellValue);
            }
        }
        return sourceMap;
    }

    @RequestMapping("getMapForStage2ToDel/{filePath}/{sheetName}")
    public Map<String,String> getSessionDetailsForStage2ToDel(@PathVariable String filePath,@PathVariable String sheetName)throws IOException{
        Map<String,String> sourceMap = new HashMap<String, String>();
        for(int i=4;i<53;i++){
            String cellKey = getCellValue(filePath,sheetName,i,"H");
            if(!cellKey.isEmpty()) {
                String cellValue = getCellValue(filePath, sheetName, i, "I");
                sourceMap.put(cellKey, cellValue);
            }
        }
        return sourceMap;
    }

    @RequestMapping("getMapForStage2ToDV/{filePath}/{sheetName}")
    public Map<String,String> getSessionDetailsForStage2ToDV(@PathVariable String filePath, @PathVariable String sheetName)throws IOException {
        Map<String,String> sourceMap = new HashMap<String, String>();
        for(int i=4;i<53;i++){
            String cellKey = getCellValue(filePath,sheetName,i,"K");
            if(!cellKey.isEmpty()) {
                String cellValue = getCellValue(filePath, sheetName, i, "L");
                sourceMap.put(cellKey, cellValue);
            }
        }
        return sourceMap;
    }
}
