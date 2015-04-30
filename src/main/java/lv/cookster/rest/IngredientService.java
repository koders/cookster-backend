package lv.cookster.rest;

import lv.cookster.entity.Ingredient;

import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Rihards on 11.01.2015
 */
@Path("/ingredients")
public class IngredientService extends CookingService {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Ingredient> initialize() {
        Query q = em.createQuery("SELECT i FROM Ingredient i");
        List<Ingredient> resultList = (List<Ingredient>) q.getResultList();
        return resultList;
    }

}
