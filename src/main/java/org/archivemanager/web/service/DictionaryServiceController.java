package org.archivemanager.web.service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.ListNode;
import org.archivemanager.data.RestResponse;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelForm;
import org.archivemanager.models.dictionary.ModelObject;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.ImportProcessor;
import org.archivemanager.util.NumberUtility;
import org.archivemanager.web.model.ModelRecord;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/dictionary")
public class DictionaryServiceController extends WebserviceSupport {
		
	@RequestMapping(value = "/model-form", method = RequestMethod.GET)
	public String getModelForm(@RequestParam("qname") QName qname, final org.springframework.ui.Model m, HttpServletRequest request, HttpServletResponse resp) {
		List<ModelField> fields = getDictionaryService().getModelFields(qname);
		ModelForm form = new ModelForm(qname);		
		form.setLongtextFields(fields.stream().filter(field -> field.getType().equals("longtext")).collect(Collectors.toList()));
		form.setMediumtextFields(fields.stream().filter(field -> !field.isHidden() && field.getType().equals("mediumtext") && !field.getFormat().equals("password")).collect(Collectors.toList()));
		form.setSmalltextFields(fields.stream().filter(field -> !field.isHidden() && field.getType().equals("smalltext") && !field.getFormat().equals("password") && field.getValues().isEmpty()).collect(Collectors.toList()));
		form.setMultivalueFields(fields.stream().filter(field -> !field.isHidden() && field.getType().equals("smalltext") && !field.getFormat().equals("password") && !field.getValues().isEmpty()).collect(Collectors.toList()));
		form.setIntegerFields(fields.stream().filter(field -> !field.isHidden() && field.getType().equals("integer")).collect(Collectors.toList()));
		form.setBooleanFields(fields.stream().filter(field -> !field.isHidden() && field.getType().equals("boolean")).collect(Collectors.toList()));
		m.addAttribute("model", form);
		return "manager/model-form";
	}	
	@ResponseBody
	@RequestMapping(value="/models/fetch.json", method = RequestMethod.GET)
	public RestResponse<ListNode> fetchChildModels(@RequestParam QName qname, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String parent = request.getParameter("parent");
		return getModels(parent);
	}	
	@ResponseBody
	@RequestMapping(value="/model/fetch.json", method = RequestMethod.GET)
	public ModelRecord fetchModel(@RequestParam("qname") String qname, HttpServletRequest req, HttpServletResponse res) throws Exception {
		QName q = new QName(qname);
		Model model = getDictionaryService().getModel(q);		
		List<ModelField> fields = getDictionaryService().getModelFields(q).stream().filter(r->!r.isHidden()).collect(Collectors.toList());
		List<ModelRelation> relations = getDictionaryService().getModelRelations(q).stream().filter(r->!r.isHidden()).collect(Collectors.toList());
		ModelRecord record = new ModelRecord(model, fields, relations);		
		return record;
	}
	@ResponseBody
	@RequestMapping(value="/model/add.json", method = RequestMethod.POST)
	public RestResponse<Object> add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String namespace = request.getParameter("namespace");
		String localName = request.getParameter("localName");
		String description = request.getParameter("description");
		
		return addModel(namespace, localName, description);
	}
	@ResponseBody
	@RequestMapping(value="/model/update", method = RequestMethod.POST)
	public RestResponse<Object> updateModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		String parent = request.getParameter("parent");
		String namespace = request.getParameter("namespace");
		String localName = request.getParameter("localName");
		String description = request.getParameter("description");
		
		return updateModel(id, namespace, localName, description, parent);
	}
	@ResponseBody
	@RequestMapping(value="/model/remove.json", method = RequestMethod.POST)
	public RestResponse<Object> removeModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		
		return removeModel(id);
	}
	@ResponseBody
	@RequestMapping(value="/field/add.json", method = RequestMethod.POST)
	public RestResponse<Object> addField(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return addField(request);
	}
	@ResponseBody
	@RequestMapping(value="/relation/add.json", method = RequestMethod.POST)
	public RestResponse<Object> addRelation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return addRelation(request);
	}
	@ResponseBody
	@RequestMapping(value="/field/remove.json", method = RequestMethod.POST)
	public RestResponse<ModelField> removeModelField(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RestResponse<ModelField> data = new RestResponse<ModelField>();
		String id = request.getParameter("id");
		if(id != null && id.length() > 0) {
			/*
			ModelField node = getDictionaryService().getSystemDictionary().getModelField(Long.valueOf(id));			
			ListNode listNode = new ListNode();
			listNode.setId(String.valueOf(node.getId()));
			listNode.setUid(node.getUid());
			getDictionaryService().remove(node.getId());
			data.addData(node);
			*/
		} 
		
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/relation/update.json", method = RequestMethod.POST)
	public RestResponse<Object> updateRelation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return updateRelation(request);
	}
	@ResponseBody
	@RequestMapping(value="/field/update.json", method = RequestMethod.POST)
	public RestResponse<Object> updateField(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return updateField(request);
	}
	@ResponseBody
	@RequestMapping(value="/relation/remove.json", method = RequestMethod.POST)
	public RestResponse<ModelRelation> removeModelRelation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RestResponse<ModelRelation> data = new RestResponse<ModelRelation>();
		String id = request.getParameter("id");
		if(id != null && id.length() > 0) {
			/*
			ModelRelation node = getDictionaryService().getModelRelation(Long.valueOf(id));			
			ListNode listNode = new ListNode();
			listNode.setId(String.valueOf(node.getId()));
			listNode.setUid(node.getUid());
			getDictionaryService().remove(node);
			data.addData(node);
			*/
		} 
		return data;
	}
	
	
	protected RestResponse<ListNode> getModels(String parent) {
		RestResponse<ListNode> data = new RestResponse<ListNode>();
		if(parent == null || parent.equals("null")) {
			List<Model> list = getDictionaryService().getModels();
			for(Model node : list) {
				ListNode n = getListNode(String.valueOf(node.getId()), node.getUid(), "model", node.getQName().toString(), node.getQName().getLocalName());
				data.addRow(n);
			}									
		} 
		data.setEndRow(data.getRows().size());
		data.setTotal(data.getRows().size());
		return data;
	}
	protected RestResponse<Object> addModel(String namespace, String localname, String description) {
		RestResponse<Object> data = new RestResponse<Object>();
		Model model = new Model(null, new QName(namespace, localname));
		model.setDescription(description);
		//if(parentId != null && NumberUtility.isLong(parentId)) model.setParent(Long.valueOf(parentId));
		//getDictionaryService().addUpdate(model);
		//ListNode n = getListNode(String.valueOf(model.getId()), model.getUid(), "model", model.getQName().toString(), model.getQName().getLocalName());
		data.addRow(model);
		data.setTotal(1);
		data.setEndRow(1);
		return data;
	}
	protected RestResponse<Object> updateModel(String id, String namespace, String localName, String description, String parent) {
		RestResponse<Object> data = new RestResponse<Object>();
		if(id != null && id.length() > 0) {
			try {
				Entity node = getEntityService().getEntity(Long.valueOf(id));
				String qname = (String)node.getQName().toString();
				Model model = getDictionaryService().getModel(QName.createQualifiedName(qname));
				if(model != null) {					
					if(!model.getQName().getNamespace().equals(namespace) || !model.getQName().getLocalName().equals(localName))
						model.setQName(new QName(namespace, localName));
					model.setDescription(description);
					/*
					if(parent != null && NumberUtility.isLong(parent)) {
						Long parentId = Long.valueOf(parent);
						
					} else {
						model.setParent(null);
						model.setParentName(null);
					}
					*/
					//getDictionaryService().addUpdate(model);
					//ListNode n = getListNode(String.valueOf(model.getId()), model.getUid(), "model", model.getQName().toString(), model.getQName().getLocalName());
					data.addRow(model);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}				
		return data;
	}
	protected RestResponse<Object> removeModel(String id) {
		RestResponse<Object> data = new RestResponse<Object>();		
		ModelObject target = null;
		if(id != null && id.length() > 0) {
			try {
				Entity node = getEntityService().getEntity(Long.valueOf(id));
				String qname = node.getQName().toString();
				target = getDictionaryService().getModel(QName.createQualifiedName(qname));
			} catch(Exception e) {
				e.printStackTrace();
			}
		} 
		if(target != null) {
			ListNode node = new ListNode();
			node.setId(String.valueOf(target.getId()));
			node.setUid(target.getUid());
			try {
				//getDictionaryService().remove(null, target.getId());
				data.addRow(node);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	protected RestResponse<Object> addField(HttpServletRequest request) {
		RestResponse<Object> data = new RestResponse<Object>();
		String modelId = request.getParameter("model");
		String namespace = request.getParameter("namespace");
		String localName = request.getParameter("localName");
		String type = request.getParameter("type");
		String format = request.getParameter("format");
		String index = request.getParameter("index");
		String sort = request.getParameter("sort");
		String minimum = request.getParameter("minimum");
		String maximum = request.getParameter("maximum");
		String mandatory = request.getParameter("mandatory");
		String unique = request.getParameter("unique");
		if(modelId != null && NumberUtility.isLong(modelId)) {
			ModelField field = new ModelField(0, new QName(namespace, localName));
			//field.setModel(Long.valueOf(modelId));
			if(type != null && type.length() > 0) field.setType(type);
			if(format != null && format.length() > 0) field.setFormat(format);
			if(index != null && index.length() > 0) field.setIndex(Integer.valueOf(index));
			if(sort != null && sort.length() > 0) field.setSort(Integer.valueOf(sort));
			if(minimum != null && minimum.length() > 0) field.setMinSize(Integer.valueOf(minimum));
			if(maximum != null && maximum.length() > 0) field.setMaxSize(Integer.valueOf(maximum));
			if(mandatory != null && mandatory.length() > 0) field.setMandatory(Boolean.valueOf(mandatory));
			if(unique != null && unique.length() > 0) field.setUnique(Boolean.valueOf(unique));
			//getDictionaryService().addUpdate(field);				
			//ListNode n = getListNode(String.valueOf(field.getId()), field.getUid(), "field", field.getQName().toString(), field.getQName().getLocalName());
			data.addRow(field);
			data.setTotal(1);
			data.setEndRow(1);
		}
		return data;
	}
	protected RestResponse<Object> addRelation(HttpServletRequest request) {
		RestResponse<Object> data = new RestResponse<Object>();
		String modelId = request.getParameter("model");
		String namespace = request.getParameter("namespace");
		String localName = request.getParameter("localName");
		String startNamespace = request.getParameter("startNamespace");
		String startlocalName = request.getParameter("startlocalName");
		String endNamespace = request.getParameter("endNamespace");
		String endlocalName = request.getParameter("endlocalName");
		String direction = request.getParameter("direction");
		String many = request.getParameter("many");
		String cascade = request.getParameter("cascade");
		if(modelId != null && NumberUtility.isLong(modelId)) {
			ModelRelation relation = new ModelRelation(0L, new QName(namespace, localName));
			//relation.setModelId(Long.valueOf(modelId));
			relation.setStartName(new QName(startNamespace, startlocalName));
			relation.setEndName(new QName(endNamespace, endlocalName));
			relation.setDirection(Integer.valueOf(direction));
			relation.setMany(Boolean.valueOf(many));
			relation.setCascade(Boolean.valueOf(cascade));
			//getDictionaryService().addUpdate(relation);				
			//ListNode n = getListNode(String.valueOf(relation.getId()), relation.getUid(), "field", relation.getQName().toString(), relation.getQName().getLocalName());
			data.addRow(relation);
			data.setTotal(1);
			data.setEndRow(1);
		}
		return data;
	}
	protected RestResponse<Object> updateField(HttpServletRequest request) {
		RestResponse<Object> data = new RestResponse<Object>();
		String fieldlId = request.getParameter("id");
		String namespace = request.getParameter("namespace");
		String localName = request.getParameter("localName");
		String type = request.getParameter("type");
		String format = request.getParameter("format");
		String index = request.getParameter("index");
		String sort = request.getParameter("sort");
		String minimum = request.getParameter("minimum");
		String maximum = request.getParameter("maximum");
		String mandatory = request.getParameter("mandatory");
		String unique = request.getParameter("unique");
		if(fieldlId != null && NumberUtility.isLong(fieldlId)) {
			try {
			ModelField field = new ModelField(Long.valueOf(fieldlId), QName.createQualifiedName(namespace, localName));
				if(type != null && type.length() > 0) field.setType(type);
				if(format != null && format.length() > 0) field.setFormat(format);
				if(index != null && index.length() > 0) field.setIndex(Integer.valueOf(index));
				if(sort != null && sort.length() > 0) field.setSort(Integer.valueOf(sort));
				if(minimum != null && minimum.length() > 0) field.setMinSize(Integer.valueOf(minimum));
				if(maximum != null && maximum.length() > 0) field.setMaxSize(Integer.valueOf(maximum));
				if(mandatory != null && mandatory.length() > 0) field.setMandatory(Boolean.valueOf(mandatory));
				if(unique != null && unique.length() > 0) field.setUnique(Boolean.valueOf(unique));
				//getDictionaryService().addUpdate(field);				
				data.addRow(field);
				data.setTotal(1);
				data.setEndRow(1);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	protected RestResponse<Object> updateRelation(HttpServletRequest request) {
		RestResponse<Object> data = new RestResponse<Object>();
		//String modelId = request.getParameter("model");
		String fieldlId = request.getParameter("id");
		String namespace = request.getParameter("namespace");
		String localName = request.getParameter("localName");
		String startNamespace = request.getParameter("startNamespace");
		String startlocalName = request.getParameter("startlocalName");
		String endNamespace = request.getParameter("endNamespace");
		String endlocalName = request.getParameter("endlocalName");
		String direction = request.getParameter("direction");
		String many = request.getParameter("many");
		String cascade = request.getParameter("cascade");
		if(fieldlId != null && NumberUtility.isLong(fieldlId)) {
			try {
				ModelRelation relation = new ModelRelation(Long.valueOf(fieldlId), QName.createQualifiedName(namespace, localName));
				//relation.setModelId(Long.valueOf(modelId));
				relation.setStartName(new QName(startNamespace, startlocalName));
				relation.setEndName(new QName(endNamespace, endlocalName));
				relation.setDirection(Integer.valueOf(direction));
				relation.setMany(Boolean.valueOf(many));
				relation.setCascade(Boolean.valueOf(cascade));
				//getDictionaryService().addUpdate(relation);				
				data.addRow(relation);
				data.setTotal(1);
				data.setEndRow(1);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	protected List<ModelField> getFields(Model modelNode) {
		List<ModelField> data = new ArrayList<ModelField>();
		//String fieldsId = UUID.randomUUID().toString();
		for(ModelField field : modelNode.getFields()) {
			String type = field.getType() == ModelField.TYPE_ASPECT ? "field-aspect" : "field";
			ModelField node2 = getModelField(field.getId(), field.getUid(), type, field.getQName().getLocalName());
			data.add(node2);
			//if(field.getType() == ModelField.ASPECT) {
			/*
			for(ModelFieldAspect aspect : field.getAspects()) {
				ListNode node3 = getListNode(String.valueOf(aspect.getId()), aspect.getUid(), "aspect", aspect.getQName().getLocalName());
				data.add(node3);
				String aspectFieldId = UUID.randomUUID().toString();
				ListNode node4 = getListNode(aspectFieldId, aspectFieldId, "fields", "Fields");
				data.add(node4);
				for(ModelField aspectField : aspect.getFields()) {
					ModelField node5 = getModelField(String.valueOf(aspectField.getId()), aspectField.getUid(), "field", aspectField.getQName().getLocalName());
					data.add(node5);
				}
			}
			*/
		}
		return data;
	}
	protected List<ModelRelation> getRelations(Model modelNode) {
		List<ModelRelation> data = new ArrayList<ModelRelation>();
		//String  relationsId = UUID.randomUUID().toString();
		for(ModelRelation rel : modelNode.getRelations()) {
			if(rel.getQName().getLocalName() != null) { 
				String type = rel.getDirection() == 1 ? "incoming" : "outgoing";
				try {
					ModelRelation node4 = getModelRelation(rel.getId(), rel.getUid(), type, rel.getQName().getLocalName());
					data.add(node4);
				} catch(Exception e) {
					System.out.println("Failed adding relationship to tree : "+rel.getQName());
					e.printStackTrace();
				}
			}
		}
		return data;
	}
	protected Map<String, Object> modelToJson(Model model) {
		SortedMap<String,Object> map = new TreeMap<String,Object>();

		map.put("id", model.getId());
		map.put("uid", model.getUid());
		map.put("name", model.getName());
		map.put("qname", model.getQName().toString());
		map.put("namespace", model.getQName().getNamespace());
		map.put("localName", model.getQName().getLocalName());
		map.put("description", model.getDescription());
		List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> relations = new ArrayList<Map<String, Object>>();
		Model m = model;
		System.out.println(m.getQName().getLocalName());
		while(m != null) {
			for(ModelField field : m.getFields()) {
				System.out.println(field.getName() + ":" + field.getQName().getLocalName() + ":" + field.getType() +":"+ field.getType());
				SortedMap<String,Object> fieldMap = new TreeMap<String,Object>();
				fieldMap.put("id", field.getId());
				fieldMap.put("uid", field.getUid());
				fieldMap.put("name", field.getName());
				fieldMap.put("qname", field.getQName().toString());
				//fieldMap.put("namespace", field.getQName().getNamespace());
				fieldMap.put("localName", field.getQName().getLocalName());
				fieldMap.put("description", field.getDescription());
				fieldMap.put("format", field.getFormat());
				fieldMap.put("index", field.getIndex());
				fieldMap.put("label", field.getLabel());
				fieldMap.put("maxSize", field.getMaxSize());
				fieldMap.put("minSize", field.getMinSize());
				fieldMap.put("order", field.getOrder());
				fieldMap.put("sort", field.getSort());
				fieldMap.put("type", field.getType());
				fieldMap.put("values", field.getValues());
				fields.add(fieldMap);
			}
			for(ModelRelation relation : m.getRelations()) {
				SortedMap<String,Object> fieldMap = new TreeMap<String,Object>();
				fieldMap.put("id", relation.getId());
				fieldMap.put("uid", relation.getUid());
				fieldMap.put("name", relation.getName());
				fieldMap.put("qname", relation.getQName().toString());
				//fieldMap.put("namespace", relation.getQName().getNamespace());
				fieldMap.put("localName", relation.getQName().getLocalName());
				fieldMap.put("description", relation.getDescription());
				fieldMap.put("startName", relation.getStartName().toString());
				fieldMap.put("endName", relation.getEndName().toString());
				relations.add(fieldMap);
			}
			//m = m.getParent();
		}
		map.put("fields", fields);
		map.put("relations", relations);
		return map;
	}
	protected ListNode getListNode(String id, String uid, String type, String qname, String title) {
		ListNode node = new ListNode();
		node.setId(id);
		node.setUid(uid);
		node.setType(type);
		node.setTitle(title);
		node.setQname(qname);
		return node;
	}
	protected ModelRelation getModelRelation(long id, String uid, String type, String title) {
		try {
			ModelRelation node = new ModelRelation(Long.valueOf(id), QName.createQualifiedName(type));
			node.setUid(uid);
			return node;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	protected ModelField getModelField(long id, String uid, String type, String title) {
		try {
			ModelField node = new ModelField(Long.valueOf(id), QName.createQualifiedName(type));
			node.setUid(uid);
			return node;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	public class ModelImportProcessor extends ImportProcessor {
		private static final long serialVersionUID = -7586840926618821286L;

		public ModelImportProcessor(String id, String name) {
			setId(id);
			setName(name);
		}
		@Override
		public void process(InputStream stream, Map<String, Object> properties) throws Exception {
			// TODO Auto-generated method stub
			
		}
	}
}
