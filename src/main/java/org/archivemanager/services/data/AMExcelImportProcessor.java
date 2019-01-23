package org.archivemanager.services.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AMExcelImportProcessor extends ExcelImportProcessor {
	private static final long serialVersionUID = 7684321401118246497L;
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	
	
	@Override
	public void process(InputStream stream, Map<String, Object> properties) throws Exception {
		int count = 0;		
		columns = new ArrayList<String>();
		Map<String,Entity> map = new HashMap<String,Entity>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(stream);
		XSSFSheet sheet = workbook.getSheetAt(0);
		Row header = sheet.getRow(0);
		for(Iterator<Cell> hit = header.cellIterator(); hit.hasNext();) {
			Cell cell = hit.next();
			columns.add(getStringCellValue(cell).toLowerCase());
		}
		for(Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {
			Row data = rit.next();
			Model model =  null;
			Entity entity = null;
			if(count > 0) {
				String id = getStringCellValue(data, "id");				
				if(id != null) {
					entity = entityService.getEntity(Long.valueOf(id));
					model = dictionaryService.getModel(entity.getQName());
				} else {
					String qname = getStringCellValue(data, "qname");
					entity = new Entity(new QName(qname));
					entity.setUid(java.util.UUID.randomUUID().toString());
					model = dictionaryService.getModel(entity.getQName());					
				}
			}
			if(model != null && entity != null) {
				for(ModelField field : model.getFields()) {
					String value = getStringCellValue(data, field.getLocalName());
					if(value != null) {
						entity.addProperty(field.getQName(), value);
					}
				}
				map.put(entity.getUid(), entity);
			}
			count++;
		}
		setEntities(map);
	}
}
