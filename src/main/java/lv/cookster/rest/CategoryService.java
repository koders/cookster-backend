package lv.cookster.rest;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import lv.cookster.entity.*;
import lv.cookster.entity.dto.RecipeDto;
import lv.cookster.entity.dto.StepDto;
import lv.cookster.util.Constants;
import org.apache.commons.io.FileUtils;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * about
 *
 * @author Rihards
 */
@Path("/categories")
public class CategoryService extends CookingService{

    private final static Logger Log = Logger.getLogger(CookingService.class.getName());

    @GET
    @Produces("application/json;charset=UTF-8")
    public List<Category> allCategories() {

        List<Category> categories = new ArrayList<Category>();

        Query q = em.createQuery("SELECT c FROM Category c");
        List<Category> resultList = (List<Category>) q.getResultList();
        categories.addAll(resultList);
        return categories;
    }

    @GET
    @Path("/{id}/recipes")
    @Produces("application/json;charset=UTF-8")
    public List<RecipeDto> allCategories(@PathParam("id")Long id) {

        List<RecipeDto> recipes;

        Query q = em.createQuery("SELECT r FROM Recipe r where r.category.id = :categoryId");
        q.setParameter("categoryId",id);
        List<Recipe> resultList = (List<Recipe>) q.getResultList();

        recipes = convertRecipesToDtoSmall(resultList);

        return recipes;
    }

    @PUT
    @Path("/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public OperationResult create(@FormDataParam("name") String name,
                                  @FormDataParam("picture") InputStream uploadedInputStream,
                                  @FormDataParam("picture") FormDataContentDisposition fileDetail,
                                  @FormDataParam("isPaid") boolean isPaid,
                                  @FormDataParam("price") double price) {
        OperationResult result = new OperationResult();
        try {
            Log.log(Level.SEVERE, "### Calling createCategory");

            String uuid = UUID.randomUUID().toString();

            String uploadedFileLocation = Constants.PROJECT_BASE_LOCATION + Constants.SOURCE_FOLDER + uuid;
            String uploadedFileLocation2 = Constants.PROJECT_BASE_LOCATION + Constants.TARGET_FOLDER + uuid;

            // save it
            writeToFile(uploadedInputStream, uploadedFileLocation);
            FileUtils.copyFile(new File(uploadedFileLocation), new File(uploadedFileLocation2));

            String urlName = Constants.BASE_URI + "uploads/" + uuid;

            createCategory(name, urlName, isPaid, price);

            String output = "File accessible from : " + urlName;

            result.setMessage(output);
            result.setResultCode(Constants.OPERATION_SUCCESSFUL_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            failResult(result, e);
        }
        return result;
    }


    @POST
    @Path("/{id}/edit")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public OperationResult create(@PathParam("id") String id,
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

    @GET
    @Path("/{id}/picture")
    @Produces("image/jpeg")
    public Response get(@PathParam("id") String id) {
        try {
            Category c = em.find(Category.class, Long.valueOf(id));
            File file = new File("tmpPic.jpeg");
            FileUtils.copyURLToFile(new URL(c.getPictureUrl()), file);
            return Response.ok(file).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    private void createCategory(String name, String pictureUrl, boolean isPaid, double price) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Category u = new Category();
        u.setName(name);
        u.setPictureUrl(pictureUrl);
        u.setPaid(isPaid);
        u.setPrice(price);

        em.persist(u);

        transaction.commit();
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
            stepDto.setStepId(s.getId());
            stepDto.setStepTime(s.getTime());
            stepDto.setDescription(s.getDescription());
            stepDto.setPictureUrl(s.getPictureUrl());
            stepDto.setOrderNumber(s.getOrderNumber());
            stepsDto.add(stepDto);
        }

        return stepsDto;
    }

}
