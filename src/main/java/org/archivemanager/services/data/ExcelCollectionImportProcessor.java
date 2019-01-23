package org.archivemanager.services.data;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.archivemanager.models.ClassificationModel;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.InvalidEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ExcelCollectionImportProcessor extends ExcelImportProcessor {
	private static final long serialVersionUID = 9184136800231116563L;
	private final static Logger log = Logger.getLogger(ExcelCollectionImportProcessor.class.getName());	
	@Autowired protected EntityService entityService;
	
	private QName qname;
	private Entity root;
	protected Map<QName,QName> qnames = new HashMap<QName,QName>();
	protected Map<String, Entity> categories = new HashMap<String, Entity>();	
	
	
	public void process(InputStream stream, Map<String, Object> properties) throws Exception {
		categories.clear();
		getEntities().clear();
		
		Entity collection = new Entity();
		collection.setUid(java.util.UUID.randomUUID().toString());
		collection.setQName(RepositoryModel.CATEGORY);
		setRoot(collection);
		
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
				String localName = clean(getStringCellValue(data, "local_name"));
				if(localName.length() > 0) {
					String series = clean(getStringCellValue(data, "category1"));
					String subseries = clean(getStringCellValue(data, "category2"));
					String medium = clean(getStringCellValue(data, "medium"));
					String form = clean(getStringCellValue(data, "form"));
					String name = clean(getStringCellValue(data, "name"));
					String beginDate = clean(getStringCellValue(data, "begin"));
					String endDate = clean(getStringCellValue(data, "end"));
					String dateExpression = clean(getStringCellValue(data, "date_expression"));
					String description = clean(getStringCellValue(data, "description"));
					String container1Type = clean(getStringCellValue(data, "cont1"));
					String container1Start = clean(getStringCellValue(data, "cont1_start"));
					String container1End = clean(getStringCellValue(data, "cont1_end"));
					String container2Type = clean(getStringCellValue(data, "cont2"));
					String container2Start = clean(getStringCellValue(data, "cont2_start"));
					String container2End = clean(getStringCellValue(data, "cont2_end"));
					String language = clean(getStringCellValue(data, "language"));
					String accessionDate = clean(getStringCellValue(data, "accession_date"));
					String notesType = clean(getStringCellValue(data, "note"));
					String notesText = clean(getStringCellValue(data, "note_type"));
					String summary = clean(getStringCellValue(data, "summary"));
					String subjects = clean(getStringCellValue(data, "subjects"));
					
					Entity row = new Entity();
					try {
						if(localName.equals("Printed Material")) localName = "printed_material";
						QName qname = QName.createQualifiedName("openapps_org_repository_1_0", localName.toLowerCase());
						row.setQName(qname);
					} catch (Exception e) {
						log.log(Level.SEVERE, "", e);
					}
					row.setUid(UUID.randomUUID().toString());				
					row.addProperty(SystemModel.NAME, name);
					row.addProperty(RepositoryModel.DESCRIPTION, description);
					row.addProperty(RepositoryModel.SUMMARY, summary);
					row.addProperty(RepositoryModel.CONTAINER, getContainer(container1Type,container1Start,container1End,container2Type,container2Start,container2End));
					row.addProperty(RepositoryModel.LANGUAGE, language);
					row.addProperty(new QName("openapps_org_repository_1_0", "form"), form);
					row.addProperty(new QName("openapps_org_repository_1_0", "medium"), medium);
					if(accessionDate != null) {
						row.addProperty(RepositoryModel.ACCESSION_DATE, accessionDate);						
					}
					if(dateExpression != null) {
						row.addProperty(RepositoryModel.DATE_EXPRESSION, dateExpression);
					}					
					row.addProperty(RepositoryModel.BEGIN_DATE, beginDate);
					row.addProperty(RepositoryModel.END_DATE, endDate);
					if(notesType != null && notesType.length() > 0 && notesText != null && notesText.length() > 0) {
						Entity note = new Entity(SystemModel.NOTE);
						note.addProperty(SystemModel.NOTE_TYPE, notesType);
						note.addProperty(SystemModel.NOTE_CONTENT, notesText);
					}
					if(subjects != null && subjects.length() > 0) {
						String[] parts = subjects.split(",");
						for(String part : parts) {
							try {
								Entity subject = getEntityService().getEntity(Long.valueOf(part.trim()));
								if(subject != null) {
									Association assoc = new Association(ClassificationModel.SUBJECTS, row, subject);
									row.getSourceAssociations().add(assoc);
									subject.getTargetAssociations().add(assoc);
								}
							} catch(Exception e) {
								log.log(Level.INFO, "subject not found for id:"+part);
							}
						}
					}
					
					Entity seriesCategory = null;
					seriesCategory = categories.get(series);
					if(seriesCategory == null) {
						List<Association> associations = getRoot().getAssociations(RepositoryModel.CATEGORIES);
						for(Association category : associations) {
							Entity entity = entityService.getEntity(category.getTarget());
							if(entity.getName().equals(series)) {
								seriesCategory = entity;
								categories.put(entity.getName(), entity);
							}
						}
					}
					if(seriesCategory == null) {
						seriesCategory = new Entity(RepositoryModel.CATEGORY);
						seriesCategory.setUid(UUID.randomUUID().toString());
						seriesCategory.addProperty(SystemModel.NAME, series);	
						seriesCategory.addProperty(RepositoryModel.CATEGORY_LEVEL, "series");
						categories.put(series, seriesCategory);
						getEntities().put(String.valueOf(seriesCategory.getUid()), seriesCategory);					
					}
					Entity subseriesCategory = null;
					subseriesCategory = categories.get(series+"//"+subseries);
					if(subseriesCategory == null) {
						List<Association> associations = seriesCategory.getAssociations(RepositoryModel.CATEGORIES);
						for(Association category : associations) {
							Entity entity = getEntities().get(category.getTargetUid());
							if(entity.getName().equals(series)) {
								subseriesCategory = entity;
								categories.put(seriesCategory.getName()+"//"+entity.getName(), entity);
							}
						}
					}
					if(subseriesCategory == null) {
						subseriesCategory = new Entity(RepositoryModel.CATEGORY);
						subseriesCategory.setUid(UUID.randomUUID().toString());
						subseriesCategory.addProperty(SystemModel.NAME, subseries);
						subseriesCategory.addProperty(RepositoryModel.CATEGORY_LEVEL, "subseries");
						categories.put(series+"//"+subseries, subseriesCategory);
						getEntities().put(String.valueOf(subseriesCategory.getUid()), subseriesCategory);
					}
					if(!containsAssociation(seriesCategory, subseriesCategory)) {
						Association assoc = new Association(RepositoryModel.CATEGORIES, seriesCategory, subseriesCategory);
						seriesCategory.getSourceAssociations().add(assoc);
						subseriesCategory.getTargetAssociations().add(assoc);
					}								
					Association assoc = new Association(RepositoryModel.ITEMS, subseriesCategory, row);
					subseriesCategory.getSourceAssociations().add(assoc);
					row.getTargetAssociations().add(assoc);				
					getEntities().put(String.valueOf(row.getUid()), row);
				}
			}
			count++;
	    }		
	}
	
	protected boolean containsAssociation(Entity parent, Entity child) {
		for(Association assoc : parent.getSourceAssociations()) {
			Entity entity = getEntities().get(assoc.getTargetUid());
			if(entity.getName().equals(child.getName()))
				return true;
		}
		return false;
	}
	protected String getContainer(String container1Type, String container1Start, String container1End, String container2Type, String container2Start, String container2End) {
		StringWriter writer = new StringWriter();
		if(container1Type != null && container1Type.length() > 1 && container1Start != null && container1Start.length() > 0) {
			if(container1End != null && !container1Start.equals(container1End))
				writer.write(container1Type+" "+container1Start+" - "+container1End);
			else
				writer.write(container1Type+" "+container1Start);
		}
		if(container2Type != null && container2Type.length() > 1 && container2Start != null && container2Start.length() > 0) {
			if(container2End != null && !container2Start.equals(container2End))
				writer.write(" "+container2Type+" "+container2Start+" - "+container2End);
			else
				writer.write(" "+container2Type+" "+container2Start);
		}
		return writer.toString();
	}
	
	public String cleanString(String in) {		
		return in;
	}
	public QName getQname(QName qname) {
		return qname;
	}
	@Override
	public Entity getRoot() {
		return root;
	}
	public Entity getEntityById(String id) {
		return getEntities().get(id);
	}
	public void addEntity(Entity parent, Entity child) throws InvalidEntityException {
		if(child == null) throw new InvalidEntityException("null child");
		if(child.getId() == null) child.setUid(UUID.randomUUID().toString());
		if(parent != null) {
			if(parent.getQName().equals(RepositoryModel.COLLECTION) && child.getQName().equals(RepositoryModel.CATEGORY))
				addAssoc(parent, child, RepositoryModel.CATEGORIES);
			else if(parent.getQName().equals(RepositoryModel.CATEGORY) && child.getQName().equals(RepositoryModel.CATEGORY))
				addAssoc(parent, child, RepositoryModel.CATEGORIES);
			else addAssoc(parent, child, RepositoryModel.ITEMS);
		}
		else throw new InvalidEntityException("null parent");
		getEntities().put(child.getUid(), child);
	}
	public void addAssoc(Entity source, Entity target, QName qname) {
		Association assoc = new Association(qname, source.getQName(), target.getQName());
		if(assoc != null) {
			assoc.setSourceUid(source.getUid());
			assoc.setTargetUid(target.getUid());
			target.getTargetAssociations().add(assoc);
			source.getSourceAssociations().add(assoc);
		}
	}
	public void setRoot(Entity root) {
		this.root = root;
		if(root.getId() == null) root.setUid(UUID.randomUUID().toString());
	}
	public void setEntityService(EntityService nodeSvc) {
		this.entityService = nodeSvc;
	}
	public EntityService getEntityService() {
		return entityService;
	}
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}
	public Map<QName,QName> getQnames() {
		return qnames;
	}
	public void setQnames(Map<QName,QName> qnames) {
		this.qnames = qnames;
	}
}
