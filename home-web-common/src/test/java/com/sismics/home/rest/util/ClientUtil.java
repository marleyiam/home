package com.sismics.home.rest.util;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.sismics.util.filter.TokenBasedSecurityFilter;

/**
 * REST client utilities.
 *
 * @author jtremeaux 
 */
public class ClientUtil {
    private WebTarget resource;
    
    /**
     * Constructor of ClientUtil.
     * 
     * @param resource Resource corresponding to the base URI of REST resources.
     */
    public ClientUtil(WebTarget resource) {
        this.resource = resource;
    }
    
    /**
     * Creates a user.
     * 
     * @param username Username
     */
    public void createUser(String username) {
        // Login admin to create the user
        String adminAuthenticationToken = login("admin", "admin", false);
        
        // Create the user
        Form form = new Form();
        form.param("username", username);
        form.param("email", username + "@home.com");
        form.param("password", "12345678");
        form.param("time_zone", "Asia/Tokyo");
        resource.path("/user").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .put(Entity.form(form), JsonObject.class);
        
        // Logout admin
        logout(adminAuthenticationToken);
    }
    
    /**
     * Connects a user to the application.
     * 
     * @param username Username
     * @param password Password
     * @param remember Remember user
     * @return Authentication token
     */
    public String login(String username, String password, Boolean remember) {
        Form form = new Form();
        form.param("username", username);
        form.param("password", password);
        form.param("remember", remember.toString());
        Response response = resource.path("/user/login").request()
                .post(Entity.form(form));
        
        return getAuthenticationCookie(response);
    }

    /**
     * Connects a user to the application.
     * 
     * @param username Username
     * @return Authentication token
     */
    public String login(String username) {
        return login(username, "12345678", false);
    }
    
    /**
     * Disconnects a user from the application.
     * 
     * @param authenticationToken Authentication token
     */
    public void logout(String authenticationToken) {
        resource.path("/user/logout").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, authenticationToken)
                .post(null);
    }

    /**
     * Extracts the authentication token of the response.
     * 
     * @param response Response
     * @return Authentication token
     */
    public String getAuthenticationCookie(Response response) {
        String authToken = null;
        for (NewCookie cookie : response.getCookies().values()) {
            if (TokenBasedSecurityFilter.COOKIE_NAME.equals(cookie.getName())) {
                authToken = cookie.getValue();
            }
        }
        return authToken;
    }
}
