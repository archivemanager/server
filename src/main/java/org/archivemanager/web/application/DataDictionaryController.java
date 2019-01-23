package org.archivemanager.web.application;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.models.DictionaryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.binders.DataDictionaryModelEntityBinder;
import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelFieldValue;
import org.archivemanager.models.dictionary.ModelObject;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.InvalidEntityException;
import org.archivemanager.web.model.EntityRecord;
import org.archivemanager.web.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/app/dictionary")
public class DataDictionaryController extends ApplicationController {
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private DataDictionaryModelEntityBinder entityBinder;
		
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getHome(HttpServletRequest request, HttpServletResponse resp) {		
		return "dictionary";
	}
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editDataDictionary(@RequestParam QName qname, final Model model) throws InvalidEntityException {
		org.archivemanager.models.dictionary.DataDictionary dictionary = dictionaryService.getDataDictionary(qname);
		model.addAttribute("dictionary", dictionary);
		return "dictionary/dictionary";
	}
	
	/*** Services ***/
	@ResponseBody
	@RequestMapping(value="/taxonomy.json", method = RequestMethod.GET)
	public List<TreeNode> fetchRepositoryTaxonomy(HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		String id = req.getParameter("id");
		if(id != null) {
			ModelObject entity = dictionaryService.get(Long.valueOf(id));
			if(entity instanceof DataDictionary) {
				DataDictionary dictionary = (DataDictionary)entity;
				for(org.archivemanager.models.dictionary.Model model : dictionary.getModels()) {
					nodes.add(createTreeNode(model.getQName(), model.getId(), "model", "fas fa-database", model.getName(), "closed"));
				}
			} else if(entity instanceof org.archivemanager.models.dictionary.Model) {
				org.archivemanager.models.dictionary.Model model = (org.archivemanager.models.dictionary.Model)entity;
				for(ModelField field : model.getFields()) {
					nodes.add(createTreeNode(field.getQName(), field.getId(), "field", "fas fa-box", field.getName(), "open"));
				}
				for(ModelRelation relation : model.getSourceRelations()) {
					String status = relation.getFields().size() > 0 ? "closed" : "open";
					nodes.add(createTreeNode(relation.getQName(), relation.getId(), "relation", "fas fa-sitemap", relation.getName(), status));
				}
			} else if(entity instanceof ModelRelation) {
				ModelRelation relation = (ModelRelation)entity;
				for(ModelField field : relation.getFields()) {
					nodes.add(createTreeNode(field.getQName(), field.getId(), "field", "fas fa-box", field.getName(), "open"));
				}
			}		
		} else {
			List<DataDictionary> list = dictionaryService.getDictionaries();
			for(DataDictionary entity : list) {
				TreeNode collectionNode = createTreeNode(entity.getQName(), entity.getId(), "dictionary", "fas fa-bookmark", entity.getName(), "closed");
				nodes.add(collectionNode);
			}
		}
		return nodes;
	}
	@ResponseBody
	@RequestMapping(value="/field/search.json", method = RequestMethod.POST)
	public List<ModelObject> fetchField(@RequestParam long id, HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<ModelObject> nodes = new ArrayList<ModelObject>();
		ModelObject entity = dictionaryService.get(id);
		if(entity instanceof ModelField) {
			ModelField field = (ModelField)entity;
			for(ModelFieldValue value : field.getValues()) {
				nodes.add(value);
			}			
		}
		return nodes;
	}
	@ResponseBody
	@RequestMapping(value="/fetch.json", method = RequestMethod.GET)
	public EntityRecord fetchEntity(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse res) throws Exception {		
		ModelObject entity = dictionaryService.get(Long.valueOf(id));
		EntityRecord record = entityBinder.getEntityRecord(entity);		
		return record;
	}
	@ResponseBody
	@RequestMapping(value="/save.json", method=RequestMethod.POST)
	public ModelObject saveDictionary(@RequestParam String qname, @RequestParam String name, @RequestParam String description) throws Exception {
		DataDictionary dictionary = new DataDictionary(new QName(qname), name, description);
		dictionaryService.save(dictionary);
		return dictionary;		
	}
	@ResponseBody
	@RequestMapping(value="/object/save.json", method=RequestMethod.POST)
	public ModelObject saveObject(@RequestParam long id, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ModelObject object = (ModelObject)dictionaryService.get(id);
		object.setLabel(req.getParameter(SystemModel.LABEL.toString()));
		if(object instanceof DataDictionary) {
			DataDictionary dictionary = (DataDictionary)object;
			dictionary.setInheritance(req.getParameter(DictionaryModel.INHERITANCE.toString()));
			String shared = req.getParameter(DictionaryModel.SHARED.toString());
			dictionary.setShared(Boolean.parseBoolean(shared));
		} else if(object instanceof org.archivemanager.models.dictionary.Model) {
			org.archivemanager.models.dictionary.Model model = (org.archivemanager.models.dictionary.Model)object;
			if(req.getParameter(DictionaryModel.PARENT.toString()) != null) model.setParentName(new QName(req.getParameter(DictionaryModel.PARENT.toString())));	
			model.setLabel(req.getParameter(DictionaryModel.LABEL.toString()));			
			model.setHeader(req.getParameter(DictionaryModel.HEADER.toString()));
			
			if(req.getParameter(DictionaryModel.STORED.toString()) != null) 
				model.setStored(Boolean.parseBoolean(req.getParameter(DictionaryModel.STORED.toString())));
			else model.setStored(false);
			
			if(req.getParameter(DictionaryModel.SEARCHABLE.toString()) != null) 
				model.setSearchable(Boolean.parseBoolean(req.getParameter(DictionaryModel.SEARCHABLE.toString())));
			else model.setSearchable(false);
			
			if(req.getParameter(DictionaryModel.FREETEXT.toString()) != null) 
				model.setFreetext(Boolean.parseBoolean(req.getParameter(DictionaryModel.FREETEXT.toString())));
			else model.setFreetext(false);
			
			if(req.getParameter(DictionaryModel.AUDITABLE.toString()) != null) 
				model.setAuditable(Boolean.parseBoolean(req.getParameter(DictionaryModel.AUDITABLE.toString())));
			else model.setAuditable(false);
		} else if(object instanceof ModelField) {
			ModelField field = (ModelField)object;
			if(req.getParameter(DictionaryModel.LABEL.toString()) != null) field.setLabel(req.getParameter(DictionaryModel.LABEL.toString()));
			if(req.getParameter(DictionaryModel.VALUE.toString()) != null) field.setValue(req.getParameter(DictionaryModel.VALUE.toString()));
			if(req.getParameter(DictionaryModel.TYPE.toString()) != null) field.setType(req.getParameter(DictionaryModel.TYPE.toString()));
			if(req.getParameter(DictionaryModel.FORMAT.toString()) != null) field.setFormat(req.getParameter(DictionaryModel.FORMAT.toString()));
			if(req.getParameter(DictionaryModel.DEFAULTVALUE.toString()) != null) field.setDefaultValue(req.getParameter(DictionaryModel.DEFAULTVALUE.toString()));
			
			if(req.getParameter(DictionaryModel.MANDATORY.toString()) != null) 
				field.setMandatory(Boolean.parseBoolean(req.getParameter(DictionaryModel.MANDATORY.toString())));
			else field.setMandatory(false);
			
			if(req.getParameter(DictionaryModel.UNIQUE.toString()) != null) 
				field.setUnique(Boolean.parseBoolean(req.getParameter(DictionaryModel.UNIQUE.toString())));
			else field.setUnique(false);
			
			if(req.getParameter(DictionaryModel.HIDDEN.toString()) != null) 
				field.setHidden(Boolean.parseBoolean(req.getParameter(DictionaryModel.HIDDEN.toString())));
			else field.setHidden(false);
			
			if(req.getParameter(DictionaryModel.SORTABLE.toString()) != null) 
				field.setSortable(Boolean.parseBoolean(req.getParameter(DictionaryModel.SORTABLE.toString())));
			else field.setSortable(false);
			
			if(req.getParameter(DictionaryModel.SEARCHABLE.toString()) != null) 
				field.setSearchable(Boolean.parseBoolean(req.getParameter(DictionaryModel.SEARCHABLE.toString())));
			else field.setSearchable(false);
			
			if(req.getParameter(DictionaryModel.INDEX.toString()) != null) field.setIndex(Integer.parseInt(req.getParameter(DictionaryModel.INDEX.toString())));
			if(req.getParameter(DictionaryModel.MINVALUE.toString()) != null) field.setMinValue(Integer.parseInt(req.getParameter(DictionaryModel.MINVALUE.toString())));
			if(req.getParameter(DictionaryModel.MAXVALUE.toString()) != null) field.setMaxValue(Integer.parseInt(req.getParameter(DictionaryModel.MAXVALUE.toString())));
			if(req.getParameter(DictionaryModel.MINSIZE.toString()) != null) field.setMinSize(Integer.parseInt(req.getParameter(DictionaryModel.MINSIZE.toString())));
			if(req.getParameter(DictionaryModel.MAXSIZE.toString()) != null) field.setMaxSize(Integer.parseInt(req.getParameter(DictionaryModel.MAXSIZE.toString())));
			if(req.getParameter(DictionaryModel.ORDER.toString()) != null) field.setOrder(Integer.parseInt(req.getParameter(DictionaryModel.ORDER.toString())));
			if(req.getParameter(DictionaryModel.SORT.toString()) != null) field.setSort(Integer.parseInt(req.getParameter(DictionaryModel.SORT.toString())));
						
		} else if(object instanceof ModelRelation) {
			ModelRelation relation = (ModelRelation)object;
			if(req.getParameter(SystemModel.TARGET.toString()) != null) relation.setEndName(new QName(req.getParameter(SystemModel.TARGET.toString())));
			if(req.getParameter(DictionaryModel.LABEL.toString()) != null) relation.setLabel(req.getParameter(DictionaryModel.LABEL.toString()));
			if(req.getParameter(DictionaryModel.VIEW.toString()) != null) relation.setView(req.getParameter(DictionaryModel.VIEW.toString()));
			if(req.getParameter(DictionaryModel.MANY.toString()) != null) relation.setMany(Boolean.parseBoolean(req.getParameter(DictionaryModel.MANY.toString())));
			if(req.getParameter(DictionaryModel.CASCADE.toString()) != null) relation.setCascade(Boolean.parseBoolean(req.getParameter(DictionaryModel.CASCADE.toString())));
			if(req.getParameter(DictionaryModel.HIDDEN.toString()) != null) relation.setHidden(Boolean.parseBoolean(req.getParameter(DictionaryModel.HIDDEN.toString())));			
		}
		dictionaryService.save(object);
		return object;
	}
	@ResponseBody
	@RequestMapping(value="/model/save.json", method=RequestMethod.POST)
	public TreeNode saveModel(@RequestParam long id, @RequestParam String qname, @RequestParam String name, @RequestParam String description) throws Exception {
		ModelObject object = (ModelObject)dictionaryService.get(id);
		if(object instanceof DataDictionary) {
			DataDictionary dictionary = (DataDictionary)object;
			org.archivemanager.models.dictionary.Model model = new org.archivemanager.models.dictionary.Model(new QName(qname), description);
			model.setLabel(name);
			dictionary.getModels().add(model);
			model.setDictionary(dictionary);
			dictionaryService.save(model);
			return createTreeNode(model.getQName(), model.getId(), "model", "fas fa-database", model.getName(), "open");
		}
		return null;
	}
	@ResponseBody
	@RequestMapping(value="/field/save.json", method=RequestMethod.POST)
	public TreeNode saveModelField(@RequestParam long id, @RequestParam String qname, @RequestParam String name, @RequestParam String description) throws Exception {
		ModelObject object = (ModelObject)dictionaryService.get(id);
		if(object instanceof org.archivemanager.models.dictionary.Model) {
			org.archivemanager.models.dictionary.Model model = (org.archivemanager.models.dictionary.Model)object;
			ModelField field = new ModelField(new QName(qname), name, description);
			field.setModel(model);
			model.getFields().add(field);
			dictionaryService.save(field);
			return createTreeNode(field.getQName(), field.getId(), "field", "fas fa-box", field.getName(), "open");
		} else if(object instanceof ModelRelation) {
			ModelRelation relation = (ModelRelation)object;
			ModelField field = new ModelField(new QName(qname), name, description);
			field.setRelation(relation);
			relation.getFields().add(field);
			dictionaryService.save(field);
			return createTreeNode(relation.getQName(), relation.getId(), "relation", "fas fa-sitemap", relation.getName(), "open");
		}
		return null;
	}
	@ResponseBody
	@RequestMapping(value="/relation/save.json", method=RequestMethod.POST)
	public TreeNode saveRelation(@RequestParam long id, @RequestParam String qname, @RequestParam String name, @RequestParam String description) throws Exception {
		ModelObject object = (ModelObject)dictionaryService.get(id);
		if(object instanceof org.archivemanager.models.dictionary.Model) {
			org.archivemanager.models.dictionary.Model model = (org.archivemanager.models.dictionary.Model)object;
			ModelRelation relation = new ModelRelation(new QName(qname), name, description);
			relation.setModel(model);
			model.getSourceRelations().add(relation);
			dictionaryService.save(relation);
			return createTreeNode(relation.getQName(), relation.getId(), "field", "fas fa-sitemap", relation.getName(), "open");
		}
		return null;
	}
	@ResponseBody
	@RequestMapping(value="/value/save.json", method=RequestMethod.POST)
	public ModelObject saveValue(@RequestParam long id, @RequestParam String name, @RequestParam String value) throws Exception {
		ModelField field = (ModelField)dictionaryService.get(id);
		ModelFieldValue mfv = new ModelFieldValue(name, value, "");
		mfv.setLabel(name);
		mfv.setField(field);
		field.getValues().add(mfv);
		dictionaryService.save(mfv);
		return mfv;
	}
	@ResponseBody
	@RequestMapping(value="switch.json", method=RequestMethod.POST)
	public ModelObject switchOrder(@RequestParam long id, @RequestParam long target) throws Exception {
		ModelObject object1 = (ModelObject)dictionaryService.get(id);
		ModelObject object2 = (ModelObject)dictionaryService.get(target);
		if(object1 instanceof ModelField && object2 instanceof ModelField) {
			ModelField field1 = (ModelField)object1;
			ModelField field2 = (ModelField)object2;
			if(field1.getModel().getId() == field2.getModel().getId()) {
				int index1 = field1.getModel().getFields().indexOf(field1);
				int index2 = field2.getModel().getFields().indexOf(field2);
				if(index1 > 0) {
					Collections.swap(field1.getModel().getFields(), index1, index2);
					dictionaryService.save(field1);
				}
			}
		} else if(object1 instanceof ModelRelation && object2 instanceof ModelRelation) {
			ModelRelation relation1 = (ModelRelation)object1;
			ModelRelation relation2 = (ModelRelation)object2;
			if(relation1.getModel().getId() == relation2.getModel().getId()) {
				int index1 = relation1.getModel().getRelations().indexOf(relation1);
				int index2 = relation2.getModel().getRelations().indexOf(relation2);
				if(index1 > 0) {
					Collections.swap(relation1.getModel().getRelations(), index1, index2);
					dictionaryService.save(relation1);
				}
			}
		}		
		return object1;
	}
	@ResponseBody
	@RequestMapping(value="/value/bump.json", method=RequestMethod.POST)
	public ModelObject bumpValue(@RequestParam long id) throws Exception {
		ModelFieldValue mfv = (ModelFieldValue)dictionaryService.get(id);
		int index = mfv.getField().getValues().indexOf(mfv);
		if(index > 0) {
			Collections.swap(mfv.getField().getValues(), index, index-1);
			dictionaryService.save(mfv);
		}
		return mfv;
	}
	@ResponseBody
	@RequestMapping(value="/value/drop.json", method=RequestMethod.POST)
	public ModelObject dropValue(@RequestParam long id) throws Exception {
		ModelFieldValue mfv = (ModelFieldValue)dictionaryService.get(id);
		int index = mfv.getField().getValues().indexOf(mfv);
		if(mfv.getField().getValues().size() > index+1) {
			Collections.swap(mfv.getField().getValues(), index, index+1);
			dictionaryService.save(mfv);
		}
		return mfv;
	}
	@ResponseBody
	@RequestMapping(value="/object/remove.json", method=RequestMethod.POST)
	public ModelObject removeObject(@RequestParam long id) throws Exception {
		ModelObject o = dictionaryService.get(id);
		dictionaryService.remove(o);		
		return o;
	}	
}
