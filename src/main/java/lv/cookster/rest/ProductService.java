package lv.cookster.rest;

import lv.cookster.entity.Product;

import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Rihards on 11.01.2015
 */
@Path("/products")
public class ProductService extends CookingService {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Product> initialize() {
        Query q = em.createQuery("SELECT p FROM Product p");
        List<Product> resultList = (List<Product>) q.getResultList();
        return resultList;
    }

}
