package org.archivemanager.models.repository;

/**
 * Created by jnewman on 5/2/18.
 */
public enum ContentType {

  ARTWORK ("artwork","Artwork"),
  AUDIO ("audio","Audio"),
  CORRESPONDENCE ("correspondence","Correspondence"),
  CATEGORY ("category","Category"),
  FINANCIAL ("financial","Financial Material"),
  JOURNALS ("journals","Journals"),
  LEGAL ("legal","Legal Material"),
  MANUSCRIPT ("manuscript","Manuscript"),
  MEDICAL ("medical","Medical"),
  MEMORABILIA ("memorabilia","Memorabilia"),
  MISCELLANEOUS ("miscellaneous","Miscellaneous"),
  NOTEBOOKS ("notebooks","Notebooks"),
  PHOTOGRAPHS ("photographs","Photographic Material"),
  PRINTED_MATERIAL ("printed_material","Printed Material"),
  PROFESSIONAL ("professional","Professional"),
  RESEARCH ("research","Research"),
  SCRAPBOOKS ("scrapbooks","Scrapbooks"),
  VIDEO ("video","Video");


  private final String key;
  private final String value;

  ContentType(String key, String value){
    this.key = key;
    this.value = value;
  }

  public String key() { return key; }
  public String value() { return value; }

}
