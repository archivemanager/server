package org.archivemanager.services.data;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.archivemanager.data.IDName;
import org.archivemanager.data.Record;
import org.springframework.stereotype.Component;


@Component
public class ExcelExportProcessor {
	
	
	public String export(List<Record> data) {
		String fileName = "archivemanager-"+UUID.randomUUID().toString().substring(0,5);
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		int index = 0;
		for(List<IDName> result : data) {			
			if(index == 0) {
				int cellIndex = 0;
				Row row = sheet.createRow(index);
				for(IDName name : result) {
					row.createCell(cellIndex).setCellValue(name.getId());
					cellIndex++;
				}				
				index++;
			}
			Row row = sheet.createRow(index);
			int colIndex = 0;
			for(IDName name : result) {
				if(name.getName() != null && name.getName().length() > 0) {
					row.createCell(colIndex).setCellValue(name.getName());					
				} else row.createCell(colIndex).setCellValue("");
				colIndex++;
			}			
			index++;
		}
		try {
			String tmpdir = System.getProperty("java.io.tmpdir");
			File file = new File(tmpdir, fileName+".xlsx");
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
	protected String getValue(List<IDName> list, String key) {
		for(IDName idName : list) {
			if(idName.getId().equals(key))
				return idName.getName();
		}
		return null;
	}	
}
