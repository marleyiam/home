package com.sismics.util;

/**
 * Environment properties utilities.
 *
 * @author jtremeaux 
 */
public class EnvironmentUtil {

    private static String OS = System.getProperty("os.name").toLowerCase();
    
    private static String APPLICATION_MODE = System.getProperty("application.mode");

    private static String WINDOWS_APPDATA = System.getenv("APPDATA");

    private static String MAC_OS_USER_HOME = System.getProperty("user.home");
    
    private static String HOME_HOME = System.getProperty("home.home");

    /**
     * In a web application context.
     */
    private static boolean webappContext;
    
    /**
     * Returns true if running under Microsoft Windows.
     * 
     * @return Running under Microsoft Windows
     */
    public static boolean isWindows() {
        return OS.indexOf("win") >= 0;
    }

    /**
     * Returns true if running under Mac OS.
     * 
     * @return Running under Mac OS
     */
    public static boolean isMacOs() {
        return OS.indexOf("mac") >= 0;
    }

    /**
     * Returns true if running under UNIX.
     * 
     * @return Running under UNIX
     */
    public static boolean isUnix() {
        return OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0;
    }
    
    /**
     * Returns true if we are in a unit testing environment.
     * 
     * @return Unit testing environment
     */
    public static boolean isUnitTest() {
        return !webappContext || isDevMode();
    }

    /**
     * Return true if we are in dev mode.
     *
     * @return Dev mode
     */
    public static boolean isDevMode() {
        return "dev".equalsIgnoreCase(APPLICATION_MODE);
    }

    /**
     * Returns the MS Windows AppData directory of this user.
     * 
     * @return AppData directory
     */
    public static String getWindowsAppData() {
        return WINDOWS_APPDATA;
    }

    /**
     * Returns the Mac OS home directory of this user.
     * 
     * @return Home directory
     */
    public static String getMacOsUserHome() {
        return MAC_OS_USER_HOME;
    }

    /**
     * Returns the home directory of Home (e.g. /var/home).
     * 
     * @return Home directory
     */
    public static String getHomeHome() {
        return HOME_HOME;
    }

    /**
     * Getter of webappContext.
     *
     * @return webappContext
     */
    public static boolean isWebappContext() {
        return webappContext;
    }

    /**
     * Setter of webappContext.
     *
     * @param webappContext webappContext
     */
    public static void setWebappContext(boolean webappContext) {
        EnvironmentUtil.webappContext = webappContext;
    }
}
