package org.archivemanager.web.application;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.IDName;
import org.archivemanager.data.Record;
import org.archivemanager.data.RestResponse;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.data.AMCsvImportProcessor;
import org.archivemanager.services.data.AMExcelImportProcessor;
import org.archivemanager.services.data.ExcelExportProcessor;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.ImportProcessor;
import org.archivemanager.services.entity.Property;
import org.archivemanager.services.reporting.Report;
import org.archivemanager.services.reporting.ReportingService;
import org.archivemanager.services.search.FilterRule;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.util.IOUtility;
import org.archivemanager.util.StringFormatUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/app/data")
public class DataController extends ApplicationController {
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	@Autowired private AMExcelImportProcessor excelImportProcessor;
	@Autowired private AMCsvImportProcessor csvImportProcessor;
	@Autowired private ExcelExportProcessor exporter;
	@Autowired private ReportingService reportingService;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getRepository(final Model model, HttpServletRequest request, HttpServletResponse resp) {
		List<IDName> data = new ArrayList<IDName>();
		List<org.archivemanager.models.dictionary.Model> models = dictionaryService.getModels();
		for(org.archivemanager.models.dictionary.Model m : models){
			if(m.isSearchable()) {
				IDName value = new IDName(m.getQName().toString(), m.getName());
				data.add(value);
			}
		}
		model.addAttribute("models", data.stream().sorted((o1, o2)->o1.getName().compareTo(o2.getName())).collect(Collectors.toList()));
		
		List<IDName> reportData = new ArrayList<IDName>();
		List<Report> reports = reportingService.getReports();
		for(Report report : reports) {
			IDName value = new IDName(report.getName(), report.getLabel());
			reportData.add(value);
		}
		model.addAttribute("reports", reportData);
		return "data";
	}
	
	/** Services **/
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/search.json")
	public RestResponse<Record> search(
			@RequestParam String qname,
			@RequestParam(required=false) String query, 
			@RequestParam(required=false) String report,			 
			@RequestParam(required=false) boolean uploaded, 
			@RequestParam(required=false) FilterRule[] filters, 
			@RequestParam(required=false, defaultValue="1") int page, @RequestParam(required=false, defaultValue="0") int rows, 
			HttpServletRequest req) throws Exception {
		RestResponse<Record> data = new RestResponse<Record>();		
		int start = (page*rows) -  rows;
		int end = page*rows;
		data.setStartRow(start);
		if(uploaded) {
			Map<String,Entity> uidMap = (Map<String,Entity>)req.getSession().getAttribute("data-upload-uid");
			if(uidMap != null) {
				for(String key : uidMap.keySet()) {
					Entity entity = uidMap.get(key);
					Record map = new Record();
					map.add(new IDName("id", String.valueOf(entity.getId())));
					map.add(new IDName("qname", entity.getQName().toString()));
					for(Property property : entity.getProperties()) {
						Object value = property.getValue();
						if(value != null) 
							map.add(new IDName(property.getQName().toString(), value.toString()));
						else 
							map.add(new IDName(property.getQName().toString(), ""));					
					}
					data.addRow(map);
				}
				data.setTotal(data.getRows().size());
			}
		} else {
			Report r = reportingService.findByName(report);
			QName q = (qname != null && qname.length() > 0) ? new QName(qname) : null;
			SearchRequest request = new SearchRequest(q, query);
			org.archivemanager.models.dictionary.Model model = dictionaryService.getModel(q);
			if(model != null) {
				for(ModelField field : model.getFields().stream().filter(f->f.getValues().size() > 0).collect(Collectors.toList())) {
					String value = req.getParameter(field.getQName().toString());
					if(value != null) {
						request.addParameter(field.getQName(), value);
					}
				}
			}			
			request.setStartRow(start);
			request.setEndRow(end);
			data  = r.generate(request);
		}		
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/excel/generate.json")
	public String generateExcel(@RequestParam String qname,
			@RequestParam(required=false) String query, 
			@RequestParam(required=false) String report,			 
			@RequestParam(required=false) boolean uploaded, 
			@RequestParam(required=false) FilterRule[] filters, HttpServletRequest req) throws Exception {
		RestResponse<Record> response = search(qname, query, report, uploaded, filters, 1, 0, req);
		
		String fileName = exporter.export(response.getRows());
		return fileName+".xlsx";
	}
	@ResponseBody
	@RequestMapping(value="/fetch/{fileName}.xlsx")
	public void fetchExcel(@PathVariable String fileName, HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.addHeader("Content-disposition", "attachment; filename="+fileName+".xlsx");
		res.setContentType("application/vnd.ms-excel");
		String tmpdir = System.getProperty("java.io.tmpdir");
		File file = new File(tmpdir, fileName+".xlsx");
		FileInputStream in = new FileInputStream(file);
		IOUtility.pipe(in, res.getOutputStream());
	}
	/*** Upload ***/		
	@ResponseBody
	@RequestMapping(value="/upload.json", method = RequestMethod.POST)
	public RestResponse<Object> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		ImportProcessor processor = file.getOriginalFilename().endsWith(".csv") ? csvImportProcessor : excelImportProcessor;
		processor.process(file.getInputStream(), new HashMap<String,Object>());
		Map<String,Entity> entities = processor.getEntities();		
		request.getSession().setAttribute("data-upload-uid", entities);
		return data;
	}
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/upload/save.json", method = RequestMethod.POST)
	public RestResponse<Object> saveUpload(HttpServletRequest request) throws Exception {
		RestResponse<Object> resp = new RestResponse<Object>();
		Map<String,Entity> data = (Map<String,Entity>)request.getSession().getAttribute("data-upload-uid");
		try {
			entityService.addEntities(data.values());
			resp.setStatus(200);
			resp.getMessages().add("Success");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
	protected String format(String in) {
		if(in == null || in.length() == 0) return "";
		QName qname = new QName(in);
		return StringFormatUtility.toTitleCase(qname.getLocalName().replace("_", " "));
	}
	protected String unformat(String in) {
		return in.toLowerCase().replace(" ", "_");
	}
 }
