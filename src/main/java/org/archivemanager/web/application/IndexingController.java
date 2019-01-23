package org.archivemanager.web.application;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.data.RestResponse;
import org.archivemanager.data.Sort;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.AssociationSorter;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.EntitySorter;
import org.archivemanager.services.entity.InvalidEntityException;
import org.archivemanager.services.scheduling.SchedulingService;
import org.archivemanager.services.search.indexing.EntityIndexingJob;
import org.archivemanager.web.model.DataDictionaryRecord;
import org.archivemanager.web.model.FolderTreeNode;
import org.archivemanager.web.model.ModelRecord;
import org.archivemanager.web.model.TreeNode;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/app/indexing")
public class IndexingController extends ApplicationController {
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	@Autowired private SchedulingService schedulingService;
	@Autowired private PropertyConfiguration properties;
	
	private EntitySorter entitySort = new EntitySorter(new Sort(ModelField.TYPE_SMALLTEXT, SystemModel.NAME.toString(), "asc"));
	private AssociationSorter assocSort;
	
	
	@PostConstruct
	public void initialize() {
		assocSort = new AssociationSorter(entityService, new Sort(ModelField.TYPE_SMALLTEXT, SystemModel.NAME.toString(), "asc"));
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getHome(final Model model, HttpServletRequest request, HttpServletResponse resp) {		
		model.addAttribute("newDictionary", new DataDictionaryRecord());
		return "indexing/indexes";
	}	
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editDataDictionary(@RequestParam QName qname, final Model model) throws InvalidEntityException {
		org.archivemanager.models.dictionary.DataDictionary dictionary = dictionaryService.getDataDictionary(qname);
		model.addAttribute("dictionary", dictionary);
		return "indexing/index";
	}
	
	/** Services **/
	@ResponseBody
	@RequestMapping(value="/search.json")
	public RestResponse<DataDictionaryRecord> search(@RequestParam(required=false) String query, @RequestParam(required=false, defaultValue="1") int page, 
			@RequestParam(required=false, defaultValue="20") int size) throws Exception {
		RestResponse<DataDictionaryRecord> data = new RestResponse<DataDictionaryRecord>();		
		int start = (page*size) -  size;
		int end = page*size;
		data.setStartRow(start);
		data.setEndRow(end);
		List<DataDictionary> dictionaries = dictionaryService.getDictionaries();
		//dictionaries.add(dictionaryService.getSystemDictionary());
		for(DataDictionary result : dictionaries) {
			DataDictionaryRecord dictionary = new DataDictionaryRecord(result);
			data.addRow(dictionary);
		}
		data.setTotal(dictionaries.size());
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/repository/taxonomy.json", method = RequestMethod.GET)
	public List<TreeNode> fetchRepositoryTaxonomy(HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		String id = req.getParameter("id");
		if(id == null) {
			List<Entity> list = entityService.getEntities(new QName[] {RepositoryModel.REPOSITORY});
			Collections.sort(list, entitySort);
			for(Entity entity : list) {
				TreeNode collectionNode = createTreeNode(entity.getQName(), entity.getId(), entity.getName(), "closed");
				nodes.add(collectionNode);
			}
		} else {
			Entity entity = entityService.getEntity(Long.valueOf(id));
			List<Association> sourceAssociations = entity.getSourceAssociations(RepositoryModel.COLLECTIONS);
			Collections.sort(sourceAssociations, assocSort);
			for(Association assoc : sourceAssociations) {
				Entity targetEntity = entityService.getEntity(assoc.getTarget());
				String name = targetEntity.getName();
				nodes.add(createTreeNode(targetEntity.getQName(), assoc.getTarget(), name, "open"));				
			}
		}
		return nodes;
	}
	@ResponseBody
	@RequestMapping(value="/collection/taxonomy.json", method = RequestMethod.GET)
	public List<TreeNode> fetchCollectionTaxonomy(HttpServletRequest req, HttpServletResponse res) {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		String collectionId = req.getParameter("collection");
		String id = req.getParameter("id");
		try {
			if(id == null) {
				Entity entity = entityService.getEntity(Long.valueOf(collectionId));
				List<Association> sourceAssociations = entity.getSourceAssociations(RepositoryModel.COLLECTIONS, RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
				Collections.sort(sourceAssociations, assocSort);
				if(sourceAssociations.size() == 0) {
					nodes.add(createTreeNode(entity.getQName(), entity.getId(), entity.getName(), "open"));
				} else {
					FolderTreeNode collectionNode = createFolderTreeNode(entity.getQName(), entity.getId(), entity.getName(), "open");
					for(int i=0; i < sourceAssociations.size(); i++) {
						Association assoc = sourceAssociations.get(i);
						Entity targetEntity = entityService.getEntity(assoc.getTarget());
						String name = targetEntity.getName();
						collectionNode.getChildren().add(createTreeNode(targetEntity.getQName(),assoc.getTarget(), name, "closed"));
					}
					nodes.add(collectionNode);
				}
			} else {
				Entity entity = entityService.getEntity(Long.valueOf(id));
				List<Association> sourceAssociations = entity.getSourceAssociations(RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
				//Collections.sort(sourceAssociations, assocSort);
				for(Association assoc : sourceAssociations) {
					Entity targetEntity = entityService.getEntity(assoc.getTarget());
					String name = targetEntity.getName();
					if(name == null) name = targetEntity.getPropertyValueString(RepositoryModel.DATE_EXPRESSION);
					if(name == null) name = targetEntity.getPropertyValueString(RepositoryModel.CONTAINER);
					if(dictionaryService.isA(targetEntity.getQName(), RepositoryModel.ITEM) && !targetEntity.hasAssociation(RepositoryModel.ITEMS))
						nodes.add(createTreeNode(targetEntity.getQName(), assoc.getTarget(), name, "open"));
					else
						nodes.add(createTreeNode(targetEntity.getQName(), assoc.getTarget(), name, "closed"));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		Collections.sort(nodes, new Comparator<TreeNode>() {
			@Override
			public int compare(TreeNode node1, TreeNode node2) {
				return 0;
			}			
		});
		return nodes;
	}
	@ResponseBody
	@RequestMapping(value="/add.json", method=RequestMethod.POST)
	public RestResponse<DataDictionaryRecord> addDictionary(@ModelAttribute("dictionary") DataDictionaryRecord dictionary) throws Exception {
		RestResponse<DataDictionaryRecord> data = new RestResponse<DataDictionaryRecord>();
		//entityService.updateEntity(dictionary);
		data.setStatus(200);
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/model/search.json")
	public RestResponse<ModelRecord> searchModels(@RequestParam QName qname, @RequestParam(required=false) String query, 
			@RequestParam(required=false, defaultValue="1") int page, @RequestParam(required=false, defaultValue="20") int rows) throws Exception {
		RestResponse<ModelRecord> data = new RestResponse<ModelRecord>();
		DataDictionary dictionary = dictionaryService.getDataDictionary(qname);
		if(dictionary != null) {
			int start = (page*rows) -  rows;
			int end = page*rows;
			data.setStartRow(start);
			data.setEndRow(end);
			for(org.archivemanager.models.dictionary.Model model : dictionary.getModels()) {
				ModelRecord record = new ModelRecord(model);
				data.addRow(record);
			}
			data.setTotal(data.getRows().size());
		}
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/model/index.json", method=RequestMethod.POST)
	public RestResponse<ModelRecord> addTransaction(@RequestParam QName dictionaryQname, @RequestParam QName modelQname) throws Exception {
		RestResponse<ModelRecord> data = new RestResponse<ModelRecord>();	
		org.archivemanager.models.dictionary.Model parentModel = dictionaryService.getModel(modelQname);
		List<org.archivemanager.models.dictionary.Model> models = dictionaryService.getChildModels(modelQname);
		if(models.size() == 0) models.add(parentModel);
		for(org.archivemanager.models.dictionary.Model model : models) {
			if(model.isSearchable()) {
				int count = entityService.count(model.getQName());
				double ratio = (double)count / properties.getEntityIndexingBatchSize();
				int pages =	(int)(Math.ceil(ratio));					
				for(int i=0; i < pages; i++) {
					EntityIndexingJob job = new EntityIndexingJob(entityService, model.getQName(), i, properties.getEntityIndexingBatchSize());
					schedulingService.run(job);					
				}
				Log.info(count+" "+model.getQName().getLocalName()+"s indexed successfully");			
				data.addRow(new ModelRecord(model));
			}
		}	
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/model/index-all.json", method=RequestMethod.POST)
	public RestResponse<ModelRecord> indexAll(@RequestParam QName qname) throws Exception {
		RestResponse<ModelRecord> data = new RestResponse<ModelRecord>();	
		DataDictionary dictionary = dictionaryService.getDataDictionary(qname);
		if(dictionary != null) {
			List<org.archivemanager.models.dictionary.Model> models = dictionary.getModels();
			for(org.archivemanager.models.dictionary.Model model : models) {
				if(model.isSearchable()) {
					int count = entityService.count(model.getQName());
					double ratio = (double)count / properties.getEntityIndexingBatchSize();
					int pages =	(int)(Math.ceil(ratio));
					for(int i=0; i < pages; i++) {
						EntityIndexingJob job = new EntityIndexingJob(entityService, model.getQName(), i, properties.getEntityIndexingBatchSize());
						schedulingService.run(job);
					}
					Log.info(count+" "+qname.getLocalName()+"s indexed successfully");
					data.addRow(new ModelRecord(model));
				}
			}
		}		
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/model/add.json", method=RequestMethod.POST)
	public RestResponse<ModelRecord> addTransaction(@RequestParam long accountId, @ModelAttribute("model") ModelRecord transaction) throws Exception {
		RestResponse<ModelRecord> data = new RestResponse<ModelRecord>();	
		data.addRow(transaction);
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/transaction/remove.json", method=RequestMethod.POST)
	public RestResponse<ModelRecord> removeTransaction(@RequestParam long transactionId) throws Exception {
		RestResponse<ModelRecord> data = new RestResponse<ModelRecord>();
		return data;
	}
}
