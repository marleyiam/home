package com.sismics.home.rest;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;
import com.sismics.util.filter.TokenBasedSecurityFilter;

/**
 * Exhaustive test of the camera resource.
 * 
 * @author bgamard
 */
public class TestCameraResource extends BaseJerseyTest {
    /**
     * Test the camera resource.
     *
     * @throws JSONException
     */
    @Test
    public void testCameraResource() {
        // Login admin
        String adminAuthenticationToken = clientUtil.login("admin", "admin", false);

        // List all cameras
        JsonObject json = target().path("/camera").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get(JsonObject.class);
        JsonArray cameras = json.getJsonArray("cameras");
        Assert.assertEquals(0, cameras.size());

        // Create a camera
        json = target().path("/camera").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .put(Entity.form(new Form()
                        .param("name", "Secondary camera")
                        .param("folder", Resources.getResource("pictures").getPath())), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));

        // List all cameras
        json = target().path("/camera").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get(JsonObject.class);
        cameras = json.getJsonArray("cameras");
        Assert.assertEquals(1, cameras.size());
        JsonObject camera0 = cameras.getJsonObject(0);
        String camera0Id = camera0.getString("id");
        Assert.assertEquals("Secondary camera", camera0.getString("name"));
        Assert.assertEquals(Resources.getResource("pictures").getPath(), camera0.getString("folder"));

        // Update a camera
        json = target().path("/camera/" + camera0Id).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .post(Entity.form(new Form()
                        .param("name", "Secondary camera updated")
                        .param("current", "01.jpg")), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));

        // Check the update
        json = target().path("/camera/" + camera0Id).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get(JsonObject.class);
        Assert.assertEquals("Secondary camera updated", json.getString("name"));
        Assert.assertEquals("01.jpg", json.getString("current"));
        
        // Get the current picture
        target().path("/camera/" + camera0Id + "/picture").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get();

        // Delete the camera
        json = target().path("/camera/" + camera0Id).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .delete(JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));

        // Check the deletion
        json = target().path("/camera").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get(JsonObject.class);
        cameras = json.getJsonArray("cameras");
        Assert.assertEquals(0, cameras.size());
    }
}