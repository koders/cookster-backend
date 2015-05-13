package lv.cookster.rest.admin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lv.cookster.entity.Measurement;
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
@Path("/admin/measurements")
public class MeasurementAdminService extends AdminService {

    private Gson gson = new Gson();
    private final static Logger Log = Logger.getLogger(MeasurementAdminService.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMeasurementApi(String data) {
        try {
            Measurement measurement = gson.fromJson(data, Measurement.class);
            if(!createObject(measurement)) {
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
    public Response deleteMeasurementApi(@PathParam("id") Long id) {
        try {
            Measurement measurement = em.find(Measurement.class, id);
            if(!deleteObject(measurement)) {
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
