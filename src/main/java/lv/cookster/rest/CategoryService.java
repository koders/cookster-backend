package lv.cookster.rest;

import lv.cookster.entity.*;
import lv.cookster.entity.dto.RecipeDto;
import org.apache.commons.io.FileUtils;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * about
 *
 * @author Rihards
 */
@Path("/categories")
@PermitAll
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
        q.setParameter("categoryId", id);
        List<Recipe> resultList = (List<Recipe>) q.getResultList();

        recipes = convertRecipesToDtoSmall(resultList);

        return recipes;
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

}
