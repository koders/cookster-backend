package lv.cookster.rest.admin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lv.cookster.entity.Product;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Rihards on 12.05.2015
 */
@Path("/admin/products")
public class ProductAdminService extends AdminService {

    private Gson gson = new Gson();
    private final static Logger Log = Logger.getLogger(ProductAdminService.class.getName());

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProductApi(String data) {
        try {
            Product product = gson.fromJson(data, Product.class);
            if(!createObject(product)) {
                throw new Exception();
            }
        } catch (JsonSyntaxException e) {
            Log.log(Level.SEVERE, e.getStackTrace().toString());
            return Response.serverError().build();
        } catch (Exception e) {
            Log.log(Level.SEVERE, e.getStackTrace().toString());
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProductApi(@PathParam("id") Long id) {
        try {
            Product product = em.find(Product.class, id);
            if(!deleteObject(product)) {
                throw new Exception();
            }
        } catch (JsonSyntaxException e) {
            Log.log(Level.SEVERE, e.getStackTrace().toString());
            return Response.serverError().build();
        } catch (Exception e) {
            Log.log(Level.SEVERE, e.getStackTrace().toString());
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

}
