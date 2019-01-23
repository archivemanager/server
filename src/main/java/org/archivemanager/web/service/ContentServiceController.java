package org.archivemanager.web.service;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FilenameUtils;
import org.archivemanager.data.RestResponse;
import org.archivemanager.models.CrawlingModel;
import org.archivemanager.models.system.User;
import org.archivemanager.services.content.ContentModel;
import org.archivemanager.services.content.FileNode;
import org.archivemanager.services.crawling.Crawler;
import org.archivemanager.services.crawling.CrawlingEngine;
import org.archivemanager.services.crawling.Document;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.util.FileUtility;
import org.archivemanager.util.IOUtility;
import org.archivemanager.web.utility.WebUtility;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/service/archivemanager/content")
public class ContentServiceController extends WebserviceSupport {
	
	
	@RequestMapping(value="/upload.json", method = RequestMethod.POST)
	public void upload(HttpServletRequest req, HttpServletResponse res) throws Exception {
		importUpload(req, res);
	}
	@ResponseBody
	@RequestMapping(value="/remove.json", method = RequestMethod.POST)
	public RestResponse<Object> removeAssociation(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("id") Long id) throws Exception {		
		return removeDigitalObject(id);
	}
	@RequestMapping(value="/stream/{id}")
	public ModelAndView stream(HttpServletRequest req, HttpServletResponse res, @PathVariable("id") long id) throws Exception {
		try {
			File root = getRootDirectory(id);
			File file = new File(root, id + ".bin");
			if(file.exists()) {
				res.setContentType("image/png");
				res.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".png\"");
				IOUtility.pipe(new FileInputStream(file), res.getOutputStream());			
			} else {
				URL url = new URL(getHostUrl(req)+"/theme/images/logo/ArchiveManager-viewer.png");
				IOUtility.pipe(url.openStream(), res.getOutputStream());
			}
		} catch(Exception e) {
			URL url = new URL(getHostUrl(req)+"/theme/images/logo/ArchiveManager-viewer.png");
			IOUtility.pipe(url.openStream(), res.getOutputStream());
			e.printStackTrace();
		}		
		return null;
	}
	@RequestMapping(value="/stream/original/{id}")
	public ModelAndView streamOriginal(HttpServletRequest req, HttpServletResponse res, @PathVariable("id") long id) throws Exception {
		try {
			Crawler crawler =null;
			Document document = new Document(getEntityService().getEntity(id));			
			List<Association> seeds = document.getTargetAssociations(CrawlingModel.DOCUMENTS);
			System.out.println(seeds.size() + " seeds found");
			for(Association seedAssoc : seeds) {
				Entity seed = getEntityService().getEntity(seedAssoc.getSource());
				List<Association> crawlers = seed.getTargetAssociations(CrawlingModel.SEEDS);
				System.out.println(crawlers.size() + " crawlers found");
				if(crawlers.size() > 0) {
					for(Association crawlerAssoc : crawlers) {
						crawler = new Crawler(getEntityService().getEntity(crawlerAssoc.getSource()));
					}
				} else {
					SearchRequest entityQuery = new SearchRequest(CrawlingModel.CRAWLER);
					SearchResponse results = getSearchService().search(entityQuery);	
					System.out.println(results.getResults().size() + " crawlers found");
					for(SearchResult crawlerEntity : results.getResults()) {
						Crawler c = new Crawler(getEntityService().getEntity(crawlerEntity.getId()));
						if(document.getPath().startsWith(c.getPath())) {
							crawler = c;
						}
					}
				}
			}
			if(crawler != null) {
				CrawlingEngine engine = getCrawlingService().getEngine(crawler.getProtocol());					
				byte[] data = engine.load(crawler, document);
				String mimeType = FileUtility.getMimetype(document.getFile());
				res.setContentType(mimeType);
				res.setHeader("Content-Disposition", "attachment; filename=\"" + document.getFile() + "\"");
				IOUtility.pipe(new ByteArrayInputStream(data), res.getOutputStream());
			} else {
				res.setContentType("image/png");
				res.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".png\"");
				URL url = new URL(getHostUrl(req)+"/theme/images/logo/ArchiveManager-viewer.png");
				IOUtility.pipe(url.openStream(), res.getOutputStream());
			}
		} catch(Exception e) {
			res.setContentType("image/png");
			res.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".png\"");
			URL url = new URL(getHostUrl(req)+"/theme/images/logo/ArchiveManager-viewer.png");
			IOUtility.pipe(url.openStream(), res.getOutputStream());
			e.printStackTrace();
		}		
		return null;
	}
	private File getRootDirectory(long id) {
		String idStr = String.valueOf(id);
		String homeDir = getPropertyService().getHomeDirectory() != null ? getPropertyService().getHomeDirectory() : "";
		File root = new File(homeDir + "/data/content");
		if(!root.exists()) root.mkdir();
		for(int i=0; i < idStr.length(); i++) {
			root = new File(root, Character.toString(idStr.charAt(i)));
			if(!root.exists()) root.mkdir();
		}
		return root;
	}
	public void importUpload(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = getSecurityService().getCurrentUser(WebUtility.getHttpRequest(request));
				
		byte[] payload = null;
		String fileName = null;
		String processor = null;
		String description = "";
		String type = "avatar";
		String group = "";
		int order = 0;
		long nodeId = 0;
				
		// Parse the request
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem> items = (List<FileItem>)upload.parseRequest(request);
			for(FileItem item : items) {
				if(!item.isFormField()) {
					payload = item.get();
					String name = item.getName();
				    if (name != null) {
				        fileName = FilenameUtils.getName(name);
				    }
			    } else if(item.getFieldName().equals("processor")) {
			    	processor = item.getString();
			    } else if(item.getFieldName().equals("description")) {
			    	description = item.getString();
			    } else if(item.getFieldName().equals("type")) {
			    	type = item.getString();
			    } else if(item.getFieldName().equals("group")) {
			    	group = item.getString();
			    } else if(item.getFieldName().equals("order")) {
			    	String orderStr = item.getString();
			    	if(orderStr != null) order = Integer.valueOf(orderStr);
			    } else if(item.getFieldName().equals("id")) {
			    	nodeId = Long.valueOf(item.getString());
			    }
			}
		} catch(Exception e) {
			e.printStackTrace();
	    }		
		if(nodeId > 0 && user != null) {
			try {
				boolean isImage = FileUtility.isImage(fileName);
				if(isImage && processor != null && processor.length() > 0 && !processor.equals("undefined")) {
					String[] dimensions = processor.split("_");
					if(dimensions.length == 2) {
						int width = Integer.valueOf(dimensions[0]);
						int height = Integer.valueOf(dimensions[1]);
						
						final Map<String, Object> params = new HashMap<String, Object>();						
						ByteArrayInputStream bais = new ByteArrayInputStream(payload);
						BufferedImage rawImage = Imaging.getBufferedImage(bais, params);
				        
						BufferedImage scaledImage = Scalr.resize(rawImage, Scalr.Method.BALANCED, Scalr.Mode.AUTOMATIC, width, height, Scalr.OP_ANTIALIAS);
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ImageIO.write(scaledImage, "png", baos);
						baos.flush();
						payload = baos.toByteArray();
						baos.close();					    
					}
				}
				String name = FileUtility.removeExtension(fileName)+".png";
				FileNode node = getDigitalObjectService().addDigitalObject(user.getXid(), name, description, group, order, payload);
				Association association = new Association(ContentModel.FILES, nodeId, Long.valueOf(node.getId()));
				association.addProperty(ContentModel.TYPE, type);
				getEntityService().updateAssociation(association);
			} catch(Exception e) {
		    	e.printStackTrace();
		    }
	    }		
		response.getWriter().print(request.getSession().getId());
	}
	public RestResponse<Object> removeDigitalObject(long id) {
		RestResponse<Object> data = new RestResponse<Object>();
		try {
			getDigitalObjectService().removeDigitalObject(id);
			data.addRow(id);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	public String getHostUrl(HttpServletRequest req) {
		return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
	}
}