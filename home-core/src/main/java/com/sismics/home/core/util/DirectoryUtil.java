package com.sismics.home.core.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.sismics.util.EnvironmentUtil;

/**
 * Utilities to gain access to the storage directories used by the application.
 * 
 * @author jtremeaux
 */
public class DirectoryUtil {
    /**
     * Returns the base data directory.
     * 
     * @return Base data directory
     */
    public static File getBaseDataDirectory() {
        File baseDataDir = null;
        // TODO inject a variable from context.xml or similar (#41)
        if (StringUtils.isNotBlank(EnvironmentUtil.getHomeHome())) {
            // If the home.home property is set then use it
            baseDataDir = new File(EnvironmentUtil.getHomeHome());
        } else if (EnvironmentUtil.isUnitTest()) {
            // For unit testing, use a temporary directory
            baseDataDir = new File(System.getProperty("java.io.tmpdir"));
        } else {
            // We are in a webapp environment and nothing is specified, use the default directory for this OS
            if (EnvironmentUtil.isUnix()) {
                baseDataDir = new File("/var/home");
            } if (EnvironmentUtil.isWindows()) {
                baseDataDir = new File(EnvironmentUtil.getWindowsAppData() + "\\Sismics\\Home");
            } else if (EnvironmentUtil.isMacOs()) {
                baseDataDir = new File(EnvironmentUtil.getMacOsUserHome() + "/Library/Sismics/Home");
            }
        }

        if (baseDataDir != null && !baseDataDir.isDirectory()) {
            baseDataDir.mkdirs();
        }

        return baseDataDir;
    }
    
    /**
     * Returns the database directory.
     * 
     * @return Database directory.
     */
    public static File getDbDirectory() {
        return getDataSubDirectory("db");
    }

    /**
     * Returns the log directory.
     * 
     * @return Log directory.
     */
    public static File getLogDirectory() {
        return getDataSubDirectory("log");
    }

    /**
     * Returns a subdirectory of the base data directory
     * 
     * @return Subdirectory
     */
    private static File getDataSubDirectory(String subdirectory) {
        File baseDataDir = getBaseDataDirectory();
        File dataSubDirectory = new File(baseDataDir.getPath() + File.separator + subdirectory);
        if (!dataSubDirectory.isDirectory()) {
            dataSubDirectory.mkdirs();
        }
        return dataSubDirectory;
    }
}
