package org.archivemanager.web.application;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.RestResponse;
import org.archivemanager.data.Sort;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.binders.RepositoryModelEntityBinder;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.system.QName;
import org.archivemanager.models.system.User;
import org.archivemanager.services.data.ExcelCollectionImportProcessor;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.AssociationSorter;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.EntitySorter;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.services.security.SecurityService;
import org.archivemanager.util.NumberUtility;
import org.archivemanager.web.model.EntityRecord;
import org.archivemanager.web.model.FolderTreeNode;
import org.archivemanager.web.model.TreeNode;
import org.archivemanager.web.utility.WebUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/app/repository")
public class RepositoryManagerController extends ApplicationController {
	@Autowired private SecurityService securityService;
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	@Autowired private SearchService searchService;
	@Autowired private AssociationSorter assocSort;	
	@Autowired private ExcelCollectionImportProcessor collectionImportProcessor;
	@Autowired private RepositoryModelEntityBinder entityBinder;
	
	private EntitySorter entitySort = new EntitySorter(new Sort(ModelField.TYPE_SMALLTEXT, SystemModel.NAME.toString(), "desc"));
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getRepository(final Model model, HttpServletRequest request, HttpServletResponse resp) {
		User user = securityService.getCurrentUser(WebUtility.getHttpRequest(request));
		if(user != null) {
			model.addAttribute("openapps_user", user);
			model.addAttribute("roles", getRolesString(user.getRoles()));			
			//List<FileImportProcessor> processors = (List)entityService.getImportProcessors(RepositoryModel.COLLECTION.toString());
			//model.addAttribute("processors", processors);
		}
		return "repository";
	}	
	/*** Services ***/
	
	@ResponseBody
	@RequestMapping(value="/search.json", method = RequestMethod.GET)
	public List<TreeNode> searchCollections(@RequestParam(required=false) String query, HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		SearchRequest entityQuery = new SearchRequest(RepositoryModel.COLLECTION, query);
		SearchResponse results = searchService.search(entityQuery);		
		if(results != null) {
			for(SearchResult entity : results.getResults()) {
				TreeNode collectionNode = createTreeNode(entity.getQName(), entity.getId(), entity.getName(), "open");
				nodes.add(collectionNode);
			}
		}
		return nodes;
	}
	@ResponseBody
	@RequestMapping(value="/taxonomy.json", method = RequestMethod.GET)
	public List<TreeNode> fetchRepositoryTaxonomy(HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		String id = req.getParameter("id");
		String query = req.getParameter("query");		
		if(id != null) {
			Entity entity = entityService.getEntity(Long.valueOf(id));
			List<Association> sourceAssociations = entity.getSourceAssociations(RepositoryModel.COLLECTIONS);
			Collections.sort(sourceAssociations, assocSort);
			for(Association assoc : sourceAssociations) {
				Entity targetEntity = entityService.getEntity(assoc.getTarget());				
				String name = targetEntity.getName();
				nodes.add(createTreeNode(targetEntity.getQName(), assoc.getTarget(), name, "open"));				
			}
		} else if(query != null && query.length() > 0) {
			if(NumberUtility.isLong(query)) {
				Entity entity = entityService.getEntity(Long.valueOf(query));
				org.archivemanager.models.dictionary.Model model = dictionaryService.getModel(entity.getQName());
				String name = entity.getName();
				if(model.getLabelName() != null)
					name = entity.getPropertyValueString(model.getLabelName());
				TreeNode node = new TreeNode(entity.getId(), entity.getQName().toString(), entity.getQName().getLocalName(), name, "open");
				String iconCls = "icon-" + entity.getQName().getLocalName();
				node.setIconCls(iconCls);
				nodes.add(node);
			} else {				
				SearchRequest entityQuery = new SearchRequest(RepositoryModel.COLLECTION, query);
				SearchResponse results = searchService.search(entityQuery);		
				if(results != null) {
					for(SearchResult entity : results.getResults()) {
						TreeNode collectionNode = createTreeNode(entity.getQName(), entity.getId(), entity.getName(), "open");
						nodes.add(collectionNode);
					}
				}
			}
		} else {
			List<Entity> list = entityService.getEntities(new QName[] {RepositoryModel.REPOSITORY});
			Collections.sort(list, entitySort);
			for(Entity entity : list) {
				TreeNode collectionNode = createTreeNode(entity.getQName(), entity.getId(), entity.getName(), "closed");
				nodes.add(collectionNode);
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
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/collection/upload/fetch.json", method = RequestMethod.GET)
	public List<TreeNode> retrieveUpload(@RequestParam(required=false) Long id, HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		Map<Long,Entity> idMap = (Map<Long,Entity>)req.getSession().getAttribute("collection-upload-id");
		Map<String,Entity> uidMap = (Map<String,Entity>)req.getSession().getAttribute("collection-upload-uid");
		if(idMap != null && uidMap != null) {
			if(id == null) {
				for(Long key : idMap.keySet()) {
					Entity entity = idMap.get(key);
					if(entity.getTargetAssociations(RepositoryModel.COLLECTIONS, RepositoryModel.CATEGORIES, RepositoryModel.ITEMS).size() == 0) {
						FolderTreeNode node = createFolderTreeNode(entity.getQName(), key, entity.getName(), "closed");
						nodes.add(node);
					}
				}				
			} else {
				Entity entity = idMap.get(id);
				List<Association> sourceAssociations = entity.getSourceAssociations(RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
				//Collections.sort(sourceAssociations, assocSort);
				for(Association assoc : sourceAssociations) {
					Entity targetEntity = uidMap.get(assoc.getTargetUid());
					for(Long targetId : idMap.keySet()) {
						Entity tempEntity = idMap.get(targetId);
						if(targetEntity.getUid().equals(tempEntity.getUid())) {
							String name = targetEntity.getName();
							if(name == null) name = targetEntity.getPropertyValueString(RepositoryModel.DATE_EXPRESSION);
							if(name == null) name = targetEntity.getPropertyValueString(RepositoryModel.CONTAINER);
							if(dictionaryService.isA(targetEntity.getQName(), RepositoryModel.ITEM))
								nodes.add(createTreeNode(targetEntity.getQName(), targetId, name, "open"));
							else
								nodes.add(createTreeNode(targetEntity.getQName(), targetId, name, "closed"));
						}
					}
				}
			}
		}
		return nodes;
	}
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/collection/upload/entity/fetch.json", method = RequestMethod.GET)
	public EntityRecord fetchEntity(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse res) throws Exception {
		Map<Long,Entity> idMap = (Map<Long,Entity>)req.getSession().getAttribute("collection-upload-id");		
		Entity result = idMap.get(id);
		EntityRecord record = entityBinder.getEntityRecord(id, result);		
		return record;
	}
	/*** Services ***/
	@ResponseBody
	@RequestMapping(value="/collection/upload.json", method = RequestMethod.POST)
	public RestResponse<Object> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		collectionImportProcessor.process(file.getInputStream(), new HashMap<String,Object>());
		Map<String,Entity> entities = collectionImportProcessor.getEntities();
		Map<Long,Entity> map = new HashMap<Long,Entity>();
		long id = 1;
		for(Entity e : entities.values()) {
			map.put(id, e);
			id++;
		}
		request.getSession().setAttribute("collection-upload-root", collectionImportProcessor.getRoot());
		request.getSession().setAttribute("collection-upload-id", map);
		request.getSession().setAttribute("collection-upload-uid", entities);
		return data;
	}
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/collection/upload/save.json", method = RequestMethod.POST)
	public RestResponse<Object> saveUpload(@RequestParam Long collectionId, @RequestParam String name, HttpServletRequest request) throws Exception {
		RestResponse<Object> resp = new RestResponse<Object>();
		Map<String,Entity> data = (Map<String,Entity>)request.getSession().getAttribute("collection-upload-uid");
		Entity collection = entityService.getEntity(collectionId);
		Entity root = (Entity)request.getSession().getAttribute("collection-upload-root");
		try {			
			entityService.addEntities(data.values());
			for(Association association : root.getSourceAssociations()) {
				association.setSource(collection.getId());
				entityService.updateAssociation(association);
				collection.getSourceAssociations().add(association);
			}			
			resp.setStatus(200);
			resp.getMessages().add("Success");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
}
