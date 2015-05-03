package lv.cookster.util;

import com.restfb.DefaultFacebookClient;
import com.restfb.Version;
import com.restfb.types.User;
import lv.cookster.rest.CookingService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/test")
public class TestService extends CookingService{

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Hello, World!";
    }

    @Path("/fb")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String facebookTest(@QueryParam("token")String token) {
        facebookClient = new DefaultFacebookClient(token, "9d10807fb4ed2723dae7eaa118ec28a3" , Version.VERSION_2_3);
        return facebookClient.fetchObject("me", User.class).getName();
    }
}
