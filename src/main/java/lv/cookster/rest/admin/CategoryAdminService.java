package lv.cookster.rest.admin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.multipart.FormDataMultiPart;
import lv.cookster.entity.*;
import lv.cookster.entity.dto.RecipeDto;
import lv.cookster.entity.dto.StepDto;
import lv.cookster.rest.CookingService;
import lv.cookster.util.Constants;
import org.apache.commons.io.FileUtils;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * about
 *
 * @author Rihards
 */
@Path("/admin/categories")
public class CategoryAdminService extends CookingService{

    private Gson gson = new Gson();
    private final static Logger Log = Logger.getLogger(CategoryAdminService.class.getName());

    @POST
    public OperationResult createCategoryRest(String data) {

        OperationResult result = new OperationResult();
        try {
            createCategory(data);
            successResult(result);
        } catch (JsonSyntaxException e) {
            Log.log(Level.SEVERE, "Failed to parse JSON.");
            e.printStackTrace();
            failResult(result, e);
        } catch (Exception e) {
            Log.log(Level.SEVERE, "Failed to commit category.");
            e.printStackTrace();
            failResult(result, e);
        }
        return result;

//        String name = multiPart.getField("name").getValueAs(String.class);
//        InputStream uploadedInputStream = multiPart.getField("picture").getValueAs(InputStream.class);
//        boolean isPaid = multiPart.getField("isPaid").getValueAs(boolean.class);
//        double price = multiPart.getField("price").getValueAs(double.class);
//        OperationResult result = new OperationResult();
//        try {
//            Log.log(Level.SEVERE, "### Calling createCategory");
//
//            String uuid = UUID.randomUUID().toString();
//
//            String uploadedFileLocation = Constants.PROJECT_BASE_LOCATION + Constants.SOURCE_FOLDER + uuid;
//            String uploadedFileLocation2 = Constants.PROJECT_BASE_LOCATION + Constants.TARGET_FOLDER + uuid;
//
//            // save it
//            writeToFile(uploadedInputStream, uploadedFileLocation);
//            FileUtils.copyFile(new File(uploadedFileLocation), new File(uploadedFileLocation2));
//
//            String urlName = Constants.BASE_URI + "uploads/" + uuid;
//
//            createCategory(name, urlName, isPaid, price);
//
//            String output = "File accessible from : " + urlName;
//
//            result.setMessage(output);
//            result.setResultCode(Constants.OPERATION_SUCCESSFUL_CODE);
//        } catch (Exception e) {
//            e.printStackTrace();
//            failResult(result, e);
//        }
//        return result;
    }


    @PUT
    @Path("/{id}/edit")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public OperationResult editCategoryRest(@PathParam("id") String id,
                                  @FormParam("name") String name) {
        OperationResult result = new OperationResult();
        try {
            Log.log(Level.SEVERE, "### Calling editCategory CategoryId=<" + id + ">");
            editCategory(id, name);
            result.setMessage(Constants.OPERATION_SUCCESSFUL_MESSAGE);
            result.setResultCode(Constants.OPERATION_SUCCESSFUL_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            failResult(result, e);
        }
        return result;
    }

    @DELETE
    @Path("/delete")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public OperationResult delete(@FormParam("id") String id) {
        OperationResult result = new OperationResult();
        try {
            Log.log(Level.SEVERE, "### Calling deleteCategory CategoryId=<" + id + ">");
            deleteCategory(id);
            result.setMessage(Constants.OPERATION_SUCCESSFUL_MESSAGE);
            result.setResultCode(Constants.OPERATION_SUCCESSFUL_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            failResult(result, e);
        }
        return result;
    }


    private void editCategory(String id, String name) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Category u = em.find(Category.class, Long.valueOf(id));
        u.setName(name);

        em.merge(u);

        transaction.commit();
    }

    private boolean createCategory(String data) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {
            Category category = gson.fromJson(data, Category.class);
            em.persist(category);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void deleteCategory(String id) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Category u = em.find(Category.class, Long.valueOf(id));
        em.remove(u);
        transaction.commit();
    }

    private List<StepDto> convertStepsToDto(List<Step> steps) {
        List<StepDto> stepsDto = new ArrayList<StepDto>();

        for(Step s:steps) {
            StepDto stepDto = new StepDto();
            stepDto.setId(s.getId());
            stepDto.setTime(s.getTime());
            stepDto.setDescription(s.getDescription());
            if(s.getPicture() != null) {
                stepDto.setPictureUrl(s.getPicture().getUrl());
            }
            stepDto.setOrderNumber(s.getOrderNumber());
            stepsDto.add(stepDto);
        }

        return stepsDto;
    }

}
