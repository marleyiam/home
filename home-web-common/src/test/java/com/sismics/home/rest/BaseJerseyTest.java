package com.sismics.home.rest;

import java.io.File;
import java.net.URI;
import java.net.URLDecoder;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.external.ExternalTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.After;
import org.junit.Before;

import com.sismics.home.rest.util.ClientUtil;
import com.sismics.util.dbi.DBIF;
import com.sismics.util.filter.RequestContextFilter;
import com.sismics.util.filter.TokenBasedSecurityFilter;

/**
 * Base class of integration tests with Jersey.
 * 
 * @author jtremeaux
 */
public abstract class BaseJerseyTest extends JerseyTest {
    /**
     * Test HTTP server.
     */
    HttpServer httpServer;
    
    /**
     * Utility class for the REST client.
     */
    protected ClientUtil clientUtil;
    
    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new ExternalTestContainerFactory();
    }
    
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new Application();
    }
    
    @Override
    protected URI getBaseUri() {
        return UriBuilder.fromUri(super.getBaseUri()).path("home").build();
    }
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        clientUtil = new ClientUtil(target());
        
        // Force shutdown
        DBIF.reset();
        
        String httpRoot = URLDecoder.decode(new File(getClass().getResource("/").getFile()).getAbsolutePath(), "utf-8");
        httpServer = HttpServer.createSimpleServer(httpRoot, "localhost", getPort());
        WebappContext context = new WebappContext("GrizzlyContext", "/home");
        context.addFilter("requestContextFilter", RequestContextFilter.class)
                .addMappingForUrlPatterns(null, "/*");
        context.addFilter("tokenBasedSecurityFilter", TokenBasedSecurityFilter.class)
                .addMappingForUrlPatterns(null, "/*");
        ServletRegistration reg = context.addServlet("jerseyServlet", ServletContainer.class);
        reg.setInitParameter("jersey.config.server.provider.packages", "com.sismics.home.rest.resource");
        reg.setLoadOnStartup(1);
        reg.addMapping("/*");
        context.deploy(httpServer);
        httpServer.start();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        if (httpServer != null) {
            httpServer.shutdownNow();
        }
    }
}
