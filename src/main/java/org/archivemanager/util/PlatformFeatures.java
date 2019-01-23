package org.archivemanager.util;


public class PlatformFeatures {
    
    /*
    public static final boolean SUPPORTS_BENDING_PAGES = !EMBEDDED;
    public static final boolean HAS_HELVETICA = MAC || IOS;
    public static final boolean USE_IOS_THEME = IOS;
    public static final boolean START_FULL_SCREEN = EMBEDDED || IOS || ANDROID;
    public static final boolean LINK_TO_SOURCE = !(EMBEDDED || IOS || ANDROID);
    public static final boolean DISPLAY_PLAYGROUND = !(EMBEDDED || IOS || ANDROID);
    public static final boolean USE_EMBEDDED_FILTER = EMBEDDED || IOS || ANDROID;
    public static final boolean WEB_SUPPORTED = Platform.isSupported(ConditionalFeature.WEB);
    */
    
    private PlatformFeatures(){}
    
    public static String getOperatingSystem() {
    	return System.getProperty("os.name");
    }
    public static String getArchitecture() {
    	return System.getProperty("os.arch");
    }
    public static boolean isIOS() {
    	String OS_NAME = System.getProperty("os.name");
    	return "iOS".equals(OS_NAME) || "iOS Simulator".equals(OS_NAME);
    }
    public static boolean isAndroid() {    	
    	return "android".equals(System.getProperty("javafx.platform")) || "Dalvik".equals(System.getProperty("java.vm.name"));
    }
    public static boolean isEmbedded() {
    	String OS_ARCH = System.getProperty("os.arch");
    	return "arm".equals(OS_ARCH) && !isIOS() && !isAndroid();
    }
    public static boolean isDesktop() {
    	return !isIOS() && !isAndroid() && !isEmbedded();
    }
    public static boolean isMac() {
    	String OS_NAME = System.getProperty("os.name");
    	return OS_NAME.startsWith("Mac");
    }
    public static boolean isWindows() {
    	String OS_NAME = System.getProperty("os.name");
    	return OS_NAME.startsWith("Windows");
    }
    public static boolean isLinux() {
    	String OS_NAME = System.getProperty("os.name");
    	return OS_NAME.startsWith("Linux");
    }
    public static boolean isWebSupported() {
    	return true;
    }
    public static boolean is64bit() {
    	String OS_PLATFORM = System.getProperty("sun.arch.data.model");
    	return OS_PLATFORM.equals("64");
    }
    public static boolean is32bit() {
    	String OS_PLATFORM = System.getProperty("sun.arch.data.model");
    	return OS_PLATFORM.equals("32") || OS_PLATFORM.equals("unknown");
    }
}
