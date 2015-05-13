package lv.cookster.rest;

import lv.cookster.entity.Level;
import lv.cookster.entity.Product;

import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Rihards on 12.05.2015
 */
@Path("/levels")
public class LevelService extends CookingService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Level> getLevels() {
        Query q = em.createQuery("SELECT l FROM Level l");
        List<Level> resultList = (List<Level>) q.getResultList();
        return resultList;
    }

}
