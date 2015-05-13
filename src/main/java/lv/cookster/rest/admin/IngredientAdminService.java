package lv.cookster.rest.admin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lv.cookster.entity.Ingredient;
import lv.cookster.entity.Measurement;
import lv.cookster.entity.Product;
import lv.cookster.rest.CookingService;

import javax.persistence.EntityTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Rihards on 12.05.2015
 */
@Path("/admin/ingredients")
public class IngredientAdminService extends AdminService {

    private Gson gson = new Gson();
    private final static Logger Log = Logger.getLogger(IngredientAdminService.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createIngredientApi(String data) {
        try {
            Ingredient ingredient = gson.fromJson(data, Ingredient.class);
            if(!createIngredient(ingredient)) {
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
    public Response deleteIngredientApi(@PathParam("id") Long id) {
        try {
            Ingredient ingredient = em.find(Ingredient.class, id);
            if(!deleteObject(ingredient)) {
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

    private boolean createIngredient(Ingredient ingredient) {
        Boolean result = true;
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            if(ingredient.getMeasurement() != null && ingredient.getMeasurement().getId() != null) {
                ingredient.setMeasurement(em.find(Measurement.class, ingredient.getMeasurement().getId()));
            }
            if(ingredient.getProduct() != null && ingredient.getProduct().getId() != null) {
                ingredient.setProduct(em.find(Product.class, ingredient.getProduct().getId()));
            }
            em.persist(ingredient);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            Log.log(Level.SEVERE, e.getStackTrace().toString());
            result = false;
        }
        return result;
    }


}
