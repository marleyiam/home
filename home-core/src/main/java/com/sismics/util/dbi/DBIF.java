package com.sismics.util.dbi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sismics.home.core.dao.dbi.mapper.AuthenticationTokenMapper;
import com.sismics.home.core.dao.dbi.mapper.BaseFunctionMapper;
import com.sismics.home.core.dao.dbi.mapper.ConfigMapper;
import com.sismics.home.core.dao.dbi.mapper.RoleBaseFunctionMapper;
import com.sismics.home.core.dao.dbi.mapper.RoleMapper;
import com.sismics.home.core.dao.dbi.mapper.UserMapper;
import com.sismics.home.core.util.DirectoryUtil;

/**
 * DBI factory.
 *
 * @author jtremeaux
 */
public class DBIF {
    private static final Logger log = LoggerFactory.getLogger(DBIF.class);

    private static ComboPooledDataSource cpds;

    private static DBI dbi;

    static {
        if (dbi == null) {
            createDbi();
        }
    }

    public static void createDbi() {
        try {
            cpds = new ComboPooledDataSource(); // TODO use getDbProperties()
            dbi = new DBI(cpds);
            dbi.registerMapper(new AuthenticationTokenMapper());
            dbi.registerMapper(new BaseFunctionMapper());
            dbi.registerMapper(new ConfigMapper());
            dbi.registerMapper(new RoleBaseFunctionMapper());
            dbi.registerMapper(new RoleMapper());
            dbi.registerMapper(new UserMapper());
        } catch (Throwable t) {
            log.error("Error creating DBI", t);
        }

        Handle handle = null;
        try {
            handle = dbi.open();
            DbOpenHelper openHelper = new DbOpenHelper(handle) {

                @Override
                public void onCreate() throws Exception {
                    executeAllScript(0);
                }

                @Override
                public void onUpgrade(int oldVersion, int newVersion) throws Exception {
                    for (int version = oldVersion + 1; version <= newVersion; version++) {
                        executeAllScript(version);
                    }
                }
            };
            openHelper.open();
        } catch (Exception e) {
            if (handle != null && handle.isInTransaction()) {
                handle.rollback();
                handle.close();
            }
        }

    }

    private static Map<Object, Object> getDbProperties() {
        // Use properties file if exists
        try {
            URL dbPropertiesUrl = DBIF.class.getResource("/c3p0.properties");
            if (dbPropertiesUrl != null) {
                log.info("Configuring connection pool from c3p0.properties");

                InputStream is = dbPropertiesUrl.openStream();
                Properties properties = new Properties();
                properties.load(is);
                return properties;
            }
        } catch (IOException e) {
            log.error("Error reading c3p0.properties", e);
        }

        // Use environment parameters
        log.info("Configuring EntityManager from environment parameters");
        Map<Object, Object> props = new HashMap<Object, Object>();
        props.put("c3p0.driverClass", "org.h2.Driver");
        File dbDirectory = DirectoryUtil.getDbDirectory();
        String dbFile = dbDirectory.getAbsoluteFile() + File.separator + "home";
        props.put("c3p0.jdbcUrl", "jdbc:h2:file:" + dbFile + ";WRITE_DELAY=false;shutdown=true");
        props.put("c3p0.user", "sa");
        return props;
    }

    /**
     * Private constructor.
     */
    private DBIF() {
    }

    /**
     * Returns an instance of DBIF.
     *
     * @return Instance of DBIF
     */
    public static DBI get() {
        return dbi;
    }

    public static void reset() {
        if (cpds != null) {
            dbi.open().createStatement("DROP ALL OBJECTS").execute();
            cpds.close();
            cpds = null;
            dbi = null;
            createDbi();
        }
    }
}
