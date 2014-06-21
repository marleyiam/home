package com.sismics.home.rest;

import java.util.Date;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.sismics.home.core.model.context.AppContext;
import com.sismics.home.core.util.TransactionUtil;
import com.sismics.util.filter.TokenBasedSecurityFilter;

/**
 * Exhaustive test of the sensor resource.
 * 
 * @author bgamard
 */
public class TestSensorResource extends BaseJerseyTest {
    /**
     * Test the sensor resource.
     *
     * @throws JSONException
     */
    @Test
    public void testSensorResource() {
        // Login admin
        String adminAuthenticationToken = clientUtil.login("admin", "admin", false);

        // List all sensors
        JsonObject json = target().path("/sensor").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get(JsonObject.class);
        JsonArray sensors = json.getJsonArray("sensors");
        Assert.assertEquals(2, sensors.size());

        // Create a sensor
        json = target().path("/sensor").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .put(Entity.form(new Form()
                        .param("name", "First sensor")
                        .param("type", "ELECTRICITY")), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));

        // List all sensors
        json = target().path("/sensor").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get(JsonObject.class);
        sensors = json.getJsonArray("sensors");
        Assert.assertEquals(3, sensors.size());
        JsonObject sensor0 = sensors.getJsonObject(0);
        String sensor0Id = sensor0.getString("id");
        Assert.assertEquals("First sensor", sensor0.getString("name"));
        Assert.assertEquals("ELECTRICITY", sensor0.getString("type"));

        // Update a sensor
        json = target().path("/sensor/" + sensor0Id).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .post(Entity.form(new Form()
                        .param("name", "Temp meter")
                        .param("type", "TEMPERATURE")), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));

        // Check the update
        json = target().path("/sensor/" + sensor0Id).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get(JsonObject.class);
        Assert.assertEquals("Temp meter", json.getString("name"));
        Assert.assertEquals("TEMPERATURE", json.getString("type"));

        // Add a sample to the sensor
        long date = new Date().getTime();
        json = target().path("/sensor/" + sensor0Id + "/sample").request()
                .put(Entity.form(new Form()
                        .param("date", "" + date)
                        .param("value", "254.5")), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));
        
        // Get the sensor
        json = target().path("/sensor/" + sensor0Id).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get(JsonObject.class);
        Assert.assertEquals("Temp meter", json.getString("name"));
        JsonArray samples = json.getJsonArray("samples");
        Assert.assertEquals(1, samples.size());
        Assert.assertEquals(date, samples.getJsonObject(0).getJsonNumber("date").longValue());
        Assert.assertEquals(254, samples.getJsonObject(0).getInt("value"));
        
        // Delete the sensor
        json = target().path("/sensor/" + sensor0Id).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .delete(JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));

        // Check the deletion
        json = target().path("/sensor").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get(JsonObject.class);
        sensors = json.getJsonArray("sensors");
        Assert.assertEquals(2, sensors.size());
    }
    
    /**
     * Test samples compacting.
     * @throws Exception
     */
    @Test
    public void testCompacting() throws Exception {
        // Login admin
        String adminAuthenticationToken = clientUtil.login("admin", "admin", false);
        
        // Create a sensor
        JsonObject json = target().path("/sensor").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .put(Entity.form(new Form()
                        .param("name", "Compacting sensor")
                        .param("type", "ELECTRICITY")), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));
        String sensor0Id = json.getString("id");
        
        // Add a sample to the sensor, 3 minutes plus 12 seconds in the past
        DateTime datePast3Min = DateTime.now().minuteOfHour().roundFloorCopy().minusMinutes(3);
        json = target().path("/sensor/" + sensor0Id + "/sample").request()
                .put(Entity.form(new Form()
                        .param("date", "" + datePast3Min.plusSeconds(12).getMillis())
                        .param("value", "10")), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));
        
        // Add a sample to the sensor, 3 minutes plus 48 seconds in the past
        json = target().path("/sensor/" + sensor0Id + "/sample").request()
                .put(Entity.form(new Form()
                        .param("date", "" + datePast3Min.plusSeconds(48).getMillis())
                        .param("value", "20")), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));
        
        // Add a sample to the sensor, 3 minutes minus 8 seconds in the past
        json = target().path("/sensor/" + sensor0Id + "/sample").request()
                .put(Entity.form(new Form()
                        .param("date", "" + datePast3Min.minusSeconds(8).getMillis())
                        .param("value", "10")), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));
        
        // Add a sample to the sensor, 3 minutes plus 1 seconds in the past
        json = target().path("/sensor/" + sensor0Id + "/sample").request()
                .put(Entity.form(new Form()
                        .param("date", "" + datePast3Min.plusSeconds(1).getMillis())
                        .param("value", "30")), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));
        
        // Add a sample to the sensor, 1 minute in the past
        json = target().path("/sensor/" + sensor0Id + "/sample").request()
                .put(Entity.form(new Form()
                        .param("date", "" + datePast3Min.plusMinutes(2).getMillis())
                        .param("value", "42")), JsonObject.class);
        Assert.assertEquals("ok", json.getString("status"));
        
        // Compact sensors (this method is called by a batch, in real life)
        TransactionUtil.handle(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().getSensorService().compactSensors();
            }
        });
        
        // Get the sensor
        json = target().path("/sensor/" + sensor0Id).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminAuthenticationToken)
                .get(JsonObject.class);
        JsonArray samples = json.getJsonArray("samples");
        Assert.assertEquals(3, samples.size()); // 3 samples are left after compacting
        Assert.assertEquals(datePast3Min.getMillis() - 60000, samples.getJsonObject(0).getJsonNumber("date").longValue());
        Assert.assertEquals(10, samples.getJsonObject(0).getInt("value"));
        Assert.assertEquals(datePast3Min.getMillis(), samples.getJsonObject(1).getJsonNumber("date").longValue());
        Assert.assertEquals(20, samples.getJsonObject(1).getInt("value"));
        Assert.assertEquals(datePast3Min.getMillis() + 120000, samples.getJsonObject(2).getJsonNumber("date").longValue());
        Assert.assertEquals(42, samples.getJsonObject(2).getInt("value"));
    }
}
