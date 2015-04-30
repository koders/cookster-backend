package lv.cookster.rest;

import lv.cookster.entity.Measurement;

import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Rihards on 11.01.2015
 */
@Path("/measurements")
public class MeasurementService extends CookingService {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Measurement> initialize() {
        Query q = em.createQuery("SELECT m FROM Measurement m");
        List<Measurement> resultList = (List<Measurement>) q.getResultList();
        return resultList;
    }

}
