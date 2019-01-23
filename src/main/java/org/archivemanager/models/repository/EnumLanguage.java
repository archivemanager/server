package org.archivemanager.models.repository;

/**
 * Created by jnewman on 5/2/18.
 */
public enum EnumLanguage {

  ENGLISH ("en", "English"),
  ARABIC ("ar", "Arabic"),
  CHINESE ("zh", "Chinese"),
  CZECH ("cs", "Czech"),
  DANISH ("da", "Danish"),
  DUTCH ("nl", "Dutch"),
  FININISH ("fi", "Finnish"),
  FRENCH ("fr", "French"),
  GERMAN ("de", "German"),
  GREEK ("el", "Greek"),
  HEBREW ("he", "Hebrew"),
  HUNGARIAN ("hu", "Hungarian"),
  ICELANDIC ("is", "Icelandic"),
  ITALIAN ("it", "Italian"),
  JAPANESE ("ja", "Japanese"),
  KOREAN ("ko", "Korean"),
  NORWEGIAN ("no", "Norwegian"),
  POLISH ("pl", "Polish"),
  PORTUGESE ("pt", "Portugese"),
  RUSSIAN ("ru", "Russian"),
  SPANISH ("es", "Spanish"),
  SWEDISH ("sv", "Swedish"),
  THAI ("th", "Thai"),
  TURKISH ("tr", "Turkish"),
  YIDDISH ("yi", "Yiddish");

  private final String key;
  private final String value;

  EnumLanguage(String key, String value){
    this.key = key;
    this.value = value;
  }

  public String key() { return key; }
  public String value() { return value; }

}
