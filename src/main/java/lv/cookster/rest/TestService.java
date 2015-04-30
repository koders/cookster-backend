package lv.cookster.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;

/**
 * Created by Rihards on 10.04.2014.
 */
@Path("/test")
public class TestService extends CookingService {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String initialize() {
//        PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
//        writer.println("The first line");
//        writer.println("The second line");
//        writer.close();

        File f = new File("test");
        System.out.println(f.getAbsoluteFile().toString());
        return "привет мир!";
    }

}
