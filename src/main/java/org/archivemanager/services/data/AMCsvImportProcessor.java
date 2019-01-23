package org.archivemanager.services.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;


@Component
public class AMCsvImportProcessor extends ExcelImportProcessor {
	private static final long serialVersionUID = 7684321401118246497L;
	
	
	@Override
	public void process(InputStream stream, Map<String, Object> properties) throws Exception {
		int count = 0;		
		columns = new ArrayList<String>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(stream);
		XSSFSheet sheet = workbook.getSheetAt(0);
		Row header = sheet.getRow(0);
		for(Iterator<Cell> hit = header.cellIterator(); hit.hasNext();) {
			Cell cell = hit.next();
			columns.add(getStringCellValue(cell));
		}
		for(Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {
			Row data = rit.next();
			if(count > 0) {
				String id = getStringCellValue(data, "id");
				if(id != null) {
					
				} else {
					
				}
			}
		}
	}

}
