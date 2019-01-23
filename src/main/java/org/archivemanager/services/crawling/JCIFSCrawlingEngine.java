package org.archivemanager.services.crawling;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.models.CrawlingModel;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.util.FileUtility;
import org.archivemanager.util.IOUtility;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;


//@Component
public class JCIFSCrawlingEngine implements CrawlingEngine {
	private EntityService entityService;
	private CrawlingService crawlingService;
	@Autowired private PropertyConfiguration propertyService;
	
	
	public JCIFSCrawlingEngine(EntityService entityService, CrawlingService crawlingService) {
		this.entityService = entityService;
		this.crawlingService = crawlingService;	
	}
	
	@Override
	public CrawlingJob crawl(Crawler crawler) {
		JCIFSCrawlingJob job = new JCIFSCrawlingJob(crawler);
		job.crawl();
		return job;
	}
	@Override
	public void setCrawlingDelay(long delay) {
		
	}
	@Override
	public void setStatus(int status) {
		
	}	
	
	public EntityService getEntityService() {
		return entityService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	public CrawlingService getCrawlingService() {
		return crawlingService;
	}
	public void setCrawlingService(CrawlingService crawlingService) {
		this.crawlingService = crawlingService;
	}
	public byte[] load(Crawler crawler, Document document) {
		SmbFile smbFile = null;
		InputStream stream = null;
		try {
			document.setDomain(crawler.getDomain());
			if(crawler.getUsername() != null && crawler.getUsername().length() > 0 && crawler.getPassword() != null && crawler.getPassword().length() > 0) {
				System.out.println("connecting with credentials "+crawler.getUsername()+"/"+crawler.getPassword());
				NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(crawler.getDomain(), crawler.getUsername(), crawler.getPassword());
				smbFile = new SmbFile(document.getUrl()+"/", auth);			
			} else {
				System.out.println("connecting with credentials administrator/Bernard1963");
				NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(crawler.getDomain(), "administrator", "Bernard1963");
				smbFile = new SmbFile(document.getUrl()+"/", auth);
			}
			if(smbFile != null) {
				stream = smbFile.getInputStream();
				byte[] data = IOUtility.read(stream);
				return data;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return new byte[0];
	}
	
	public class JCIFSCrawlingJob implements CrawlingJob {
		private static final long serialVersionUID = -7371763838990163884L;
		private Crawler crawler;
		private Seed seed;
		
		int filesProcessed = 0;
		int dirsProcessed = 0;
		String message;
		
		public JCIFSCrawlingJob(Crawler crawler) {
			this.crawler = crawler;			
		}
		
		public void crawl() {
			System.out.println("crawl job for " + crawler.getId() + " beginning...");			
			try {
				seed = getCrawlingService().getSeed(crawler.getUrl());			
				if(seed == null) {
					URL url = new URL(null, crawler.getUrl(), new jcifs.smb.Handler());
					seed = new Seed();
					seed.setLoaded(false);
					seed.addProperty(SystemModel.NAME, crawler.getUrl());
					seed.setUrl(url);
					seed.setLastCrawl(System.currentTimeMillis());
					seed.setStatus(Seed.STATUS_RUNNING);
					getEntityService().updateEntity(seed);
					crawlingService.addSeedToCrawler(crawler, seed);
				} else {
					seed.setLoaded(false);
					seed.setLastCrawl(System.currentTimeMillis());
					seed.setStatus(Seed.STATUS_RUNNING);
					crawlingService.saveSeed(seed);
				}
				SmbFile smbFile = null;
				if(crawler.getUsername() != null && crawler.getUsername().length() > 0 && crawler.getPassword() != null && crawler.getPassword().length() > 0) {
					NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(crawler.getDomain(), crawler.getUsername(), crawler.getPassword());
					smbFile = new SmbFile(crawler.getUrl()+"/", auth);
					System.out.println("seeding " + crawler.getUrl() + " with credntials " + crawler.getUsername() + "/" + crawler.getPassword());
				} else {
					System.out.println("connecting with credentials administrator/Bernard1963");
					NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(crawler.getDomain(), "administrator", "Bernard1963");
					smbFile = new SmbFile(crawler.getUrl()+"/", auth);
				}				
				scanInternal(smbFile);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * Internal, recursively-called scan function.
		 */
		private void scanInternal(SmbFile file) {	
			InputStream stream = null;
			String filename = null;
			try {
				if(!file.exists()) System.out.println(file.getName() + " does not exist");
				else {
					System.out.println("crawling " + file.getName());
					if(file.isFile()) {				
						filesProcessed++;
						try {
							Document document = getCrawlingService().getDocument(file.getURL().toString());
							if(document == null) {
								document = getDocument(file);
								document.setTimestamp(file.getLastModified());
							}
							getCrawlingService().saveDocument(document);
							if(FileUtility.isImage(file.getName())) {
								File root = getRootDirectory(document.getId());
								File docFile = new File(root, document.getId() + ".bin");
								if(!docFile.exists()) {
									stream = file.getInputStream();
									processImage(document.getId(), IOUtility.read(stream));
								}								
							}
							processTaxonomy(document);
						} catch(Exception e) {
							System.out.println("error scanning : "+file.getURL().toString());
							e.printStackTrace();
						}
					}
					if(file.isDirectory()) {				
						dirsProcessed++;
						SmbFile[] files = listFiles(file);
						for(int i=0; i < files.length; i++) {
							SmbFile subFile = files[i];
							scanInternal(subFile);
						}
					}
					message = "Crawling... " + filesProcessed + " Documents scanned, " + dirsProcessed + " Directories scanned";
				}
			} catch (Throwable t) {
				t.printStackTrace();
				message = "Error processing file:" + filename;
			} finally {
				if(stream != null) {
					try {
						stream.close();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		private SmbFile[] listFiles(SmbFile dir) {
	    	List<SmbFile> list = new ArrayList<SmbFile>();
	        try {
	        	if(dir.exists()) {
	        		SmbFile[] files = dir.listFiles();
	        		for(int i=0; i < files.length; i++) {
	        			SmbFile f = files[i];
	        			String fileName = f.getName();
	        			if(f.isDirectory() && !fileName.startsWith(".archive"))
	        				list.add(f);
	        			else if(f.isFile() && !fileName.equals("Thumbs.db") && !fileName.equals(".DS_Store"))
	        				list.add(f);
	        		}
	        	}
	        	return list.toArray(new SmbFile[list.size()]);
	        } catch(Exception e) {
	        	e.printStackTrace();
	        	return new SmbFile[0];
	        }
	    }
		protected void processTaxonomy(Document document) throws Exception {
			Association assoc = crawler.getTargetAssociation(CrawlingModel.CRAWLERS);
			if(assoc != null) {
				Entity collection = getEntityService().getEntity(assoc.getSource());			
				if(collection != null) {				
					String[] path = document.getPath().replace(crawler.getPath(), "").split("/");					
					if(path.length > 1) {
						Entity root1 = null;
						List<Association> sourceAssociations = collection.getSourceAssociations(RepositoryModel.CATEGORIES);
						for(Association categoryAssoc : sourceAssociations) {
							Entity cat = getEntityService().getEntity(categoryAssoc.getTarget());
							if(cat.getName().equals(path[1])) {
								root1 = cat;
							}
						}
						if(root1 == null) {
							root1 = new Entity(RepositoryModel.CATEGORY);
							root1.addProperty(SystemModel.NAME, path[1]);
							getEntityService().updateEntity(root1);
							Association catAssoc = new Association(RepositoryModel.CATEGORIES, collection.getId(), root1.getId());
							getEntityService().updateAssociation(catAssoc);
							collection.getSourceAssociations().add(catAssoc);
						}
						if(root1 != null && path.length > 2) {
							Entity root2 = null;
							List<Association> subcatAssociations = root1.getSourceAssociations(RepositoryModel.CATEGORIES);
							for(Association categoryAssoc : subcatAssociations) {
								Entity cat = getEntityService().getEntity(categoryAssoc.getTarget());
								if(cat.getName().equals(path[2])) {
									root2 = cat;
								}
							}
							if(root2 == null) {
								root2 = new Entity(RepositoryModel.CATEGORY);
								root2.addProperty(SystemModel.NAME, path[2]);
								getEntityService().updateEntity(root2);
								Association catAssoc = new Association(RepositoryModel.CATEGORIES, root1.getId(), root2.getId());
								getEntityService().updateAssociation(catAssoc);
								collection.getSourceAssociations().add(catAssoc);
							}
							if(root2 != null && path.length > 3) {
								Entity root3 = null;
								List<Association> subcatAssociations3 = root2.getSourceAssociations(RepositoryModel.CATEGORIES);
								for(Association categoryAssoc : subcatAssociations3) {
									Entity cat = getEntityService().getEntity(categoryAssoc.getTarget());
									if(cat.getName().equals(path[3])) {
										root3 = cat;
									}
								}
								if(root3 == null) {
									root3 = new Entity(RepositoryModel.CATEGORY);
									root3.addProperty(SystemModel.NAME, path[3]);
									getEntityService().updateEntity(root3);
									Association catAssoc = new Association(RepositoryModel.CATEGORIES, root2.getId(), root3.getId());
									getEntityService().updateAssociation(catAssoc);
									collection.getSourceAssociations().add(catAssoc);
								}
								
								if(root3 != null && path.length > 4) {
									Entity root4 = null;
									List<Association> subcatAssociations4 = root3.getSourceAssociations(RepositoryModel.CATEGORIES);
									for(Association categoryAssoc : subcatAssociations4) {
										Entity cat = getEntityService().getEntity(categoryAssoc.getTarget());
										if(cat.getName().equals(path[4])) {
											root4 = cat;
										}
									}
									if(root4 == null) {
										root4 = new Entity(RepositoryModel.CATEGORY);
										root4.addProperty(SystemModel.NAME, path[4]);
										getEntityService().updateEntity(root4);
										Association catAssoc = new Association(RepositoryModel.CATEGORIES, root3.getId(), root4.getId());
										getEntityService().updateAssociation(catAssoc);
										collection.getSourceAssociations().add(catAssoc);
									}
									
									if(root4 != null && path.length > 5) {
										Entity root5 = null;
										List<Association> subcatAssociations5 = root4.getSourceAssociations(RepositoryModel.CATEGORIES);
										for(Association categoryAssoc : subcatAssociations5) {
											Entity cat = getEntityService().getEntity(categoryAssoc.getTarget());
											if(cat.getName().equals(path[5])) {
												root5 = cat;
											}
										}
										if(root5 == null) {
											root5 = new Entity(RepositoryModel.CATEGORY);
											root5.addProperty(SystemModel.NAME, path[5]);
											getEntityService().updateEntity(root5);
											Association catAssoc = new Association(RepositoryModel.CATEGORIES, root4.getId(), root5.getId());
											getEntityService().updateAssociation(catAssoc);
											collection.getSourceAssociations().add(catAssoc);
										}
									}
								}
							}
						}
					}					
				} else System.out.println("no collection found for " + crawler.getId());
			}
		}
		protected void processImage(long id, byte[] content) throws Exception {
			ByteArrayInputStream bais = new ByteArrayInputStream(content);
			//BufferedImage rawImage = Imaging.getBufferedImage(bais, params);
			BufferedImage rawImage = ImageIO.read(bais);
			
			BufferedImage scaledImage = Scalr.resize(rawImage, Scalr.Method.BALANCED, Scalr.Mode.AUTOMATIC, 550, 700, Scalr.OP_ANTIALIAS);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(scaledImage, "png", baos);
			baos.flush();
			byte[] payload = baos.toByteArray();
			baos.close();
			
			File root = getRootDirectory(id);
			File file = new File(root, id + ".bin");
			
			if(!file.exists()) file.createNewFile();			
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(file);
				IOUtility.pipe(new ByteArrayInputStream(payload), output);
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if(output != null) {
					try {
						output.close();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		private File getRootDirectory(long id) {
			String idStr = String.valueOf(id);
			String homeDir = propertyService.getHomeDirectory() != null ? propertyService.getHomeDirectory() : "";
			File root = new File(homeDir + "/data/content");
			if(!root.exists()) root.mkdir();
			for(int i=0; i < idStr.length(); i++) {
				root = new File(root, Character.toString(idStr.charAt(i)));
				if(!root.exists()) root.mkdir();
			}
			return root;
		}
		
		@Override
		public int getDirectoriesProcessed() {
			return dirsProcessed;
		}
		@Override
		public int getFilesProcessed() {
			return filesProcessed;
		}

		@Override
		public List<Seed> getErrors() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Seed> getSeeds() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getStatus() {
			// TODO Auto-generated method stub
			return 0;
		}		
	}

	@Override
	public List<CrawlingJob> getRunningJobs() {
		// TODO Auto-generated method stub
		return null;
	}
	public Document getDocument(SmbFile file) {
		try {
			Document document = new Document();
			document.setProtocol(file.getURL().getProtocol());
			document.setDomain(file.getURL().getHost());
			if(file.getURL().getPath() != null && file.getURL().getPath().length() > 0) {
				if(FileUtility.isFile(file.getURL().getPath())) {
					int index = file.getURL().getPath().lastIndexOf("/");
					String fileName = file.getURL().getPath().substring(index+1, file.getURL().getPath().length());
					String path = file.getURL().getPath().substring(0, index);
					document.setFile(fileName);
					document.setPath(path);
				} else document.setPath(file.getURL().getPath());
			}
			if(file.getURL().getQuery() != null) {
				document.setQuery(file.getURL().getQuery());
			}
			document.addProperty(SystemModel.NAME, file.getName());
			return document;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
