package lv.cookster;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import lv.cookster.util.TestService;

public class TestServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(TestService.class);
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
        final String responseMsg = target().path("test").request().get(String.class);

        assertEquals("Hello, World!", responseMsg);
    }
}
