package com.zorroa.shotgun;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created by chambers on 3/23/16.
 */
public class ShotgunTests {

    private static final Logger logger = LoggerFactory.getLogger(ShotgunTests.class);

    private String server;
    private String script;
    private String key;

    public ShotgunTests() throws IOException {
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/unittest.properties"));

        server = props.getProperty("shotgun.server");
        script = props.getProperty("shotgun.script");
        key = props.getProperty("shotgun.key");
    }

    @Test
    public void testFind() {
        Shotgun sg = new Shotgun(server, script, key);
        List<Map<String, Object>> result = sg.find(
                new SgRequest("Shot")
                        .filter("code", "is", "01")
                        .filter("sg_sequence", "name_is", "01_intro")
                        .setFields("id", "image", "code"));
        assertEquals(1, result.size());
        assertEquals("01", result.get(0).get("code"));
    }

    @Test
    public void testFindWithPage() {
        Shotgun sg = new Shotgun(server, script, key);
        List<Map<String, Object>> result = sg.find(new SgRequest("Shot")
                .setCount(1)
                .setPage(5));
        assertEquals(1, result.size());
    }

    @Test
    public void testFindOne() {
        Shotgun sg = new Shotgun(server, script, key);
        Map<String, Object> result = sg.findOne(new SgRequest("Shot")
                .setFields("code")
                .filter("code", "is", "01")
                .filter("sg_sequence", "name_is", "01_intro"));
        assertEquals("01", result.get("code"));
    }
}
