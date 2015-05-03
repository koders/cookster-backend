package lv.cookster.rest.admin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.multipart.FormDataMultiPart;
import lv.cookster.entity.*;
import lv.cookster.entity.dto.RecipeDto;
import lv.cookster.entity.dto.StepDto;
import lv.cookster.rest.CookingService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * about
 *
 * @author Rihards
 */
@Path("/admin/recipes")
@RolesAllowed("admin")
public class RecipeAdminService extends CookingService {

    private Gson gson = new Gson();
    private Cache cache = cm.getCache("recipeCache");

    private final static Logger Log = Logger.getLogger(RecipeAdminService.class.getName());

    @POST
    @Path("/create")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public OperationResult createFromJson(FormDataMultiPart multiPart) {
        OperationResult result = new OperationResult();
        try {
            Log.log(Level.SEVERE, "### Calling createRecipe");
            createRecipe(multiPart);
            successResult(result);
            cache.remove("allRecipes");
        } catch (JsonSyntaxException e) {
            Log.log(Level.SEVERE, "Failed to parse JSON.");
            e.printStackTrace();
            failResult(result, e);
        } catch (Exception e) {
            Log.log(Level.SEVERE, "Failed to commit recipe.");
            e.printStackTrace();
            failResult(result, e);
        }
        return result;
    }

//    @PUT
//    @Path("/createMany")
//    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    public OperationResult createManyFromJson(@FormParam("data") String data) {
//        OperationResult result = new OperationResult();
//        try {
//            Log.log(Level.SEVERE, "### Calling createRecipe");
//            createRecipe(data);
//            successResult(result);
//        } catch (JsonSyntaxException e) {
//            Log.log(Level.SEVERE, "Failed to parse JSON.");
//            e.printStackTrace();
//            failResult(result, e);
//        } catch (Exception e) {
//            Log.log(Level.SEVERE, "Failed to commit recipe.");
//            e.printStackTrace();
//            failResult(result, e);
//        }
//        return result;
//    }

    @DELETE
    @Path("/{id}/delete")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public OperationResult delete(@PathParam("id") Long id) {
        OperationResult result = new OperationResult();
        try {
            Log.log(Level.SEVERE, "### Calling deleteRecipe RecipeId=<" + id + ">");
            deleteRecipe(id);
            successResult(result);
            cache.remove("allRecipes");
        } catch (Exception e) {
            Log.log(Level.SEVERE, e.toString(), e);
            failResult(result, e);
        }
        return result;
    }

    @POST
    @Path("/{id}/edit")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public OperationResult edit(@PathParam("id") Long id,
                                @FormParam("data") String data) {

        OperationResult result = new OperationResult();
        try {
            Log.log(Level.SEVERE, "### Calling editRecipe RecipeId=<" + id + ">");
            editRecipe(id, data);
            successResult(result);
            cache.remove("allRecipes");
        } catch (JsonSyntaxException e) {
            Log.log(Level.SEVERE, "Failed to parse JSON.");
            failResult(result, e);
        } catch (Exception e) {
            Log.log(Level.SEVERE, "Failed to commit recipe.");
            failResult(result, e);
        }

        return result;

    }

    private void editRecipe(Long id, String data) {
        RecipeDto r = gson.fromJson(data, RecipeDto.class);

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Recipe recipe = convertToRecipe(r);
        recipe.setUpdated(new Timestamp(new Date().getTime()));
        if(recipe.getLevel() != null) {
            recipe.setLevel(em.find(lv.cookster.entity.Level.class, recipe.getLevel().getId()));
        }
        em.merge(recipe);

        deleteSteps(id);

        for(StepDto s:r.getSteps()) {
            Step step = new Step();
            step.setDescription(s.getDescription());
            step.setPictureUrl(s.getPictureUrl());
            step.setTime(s.getStepTime());
            step.setRecipe(recipe);
            step.setOrderNumber(s.getOrderNumber());
            em.persist(step);
        }

        transaction.commit();
    }

    private Recipe convertToRecipe(RecipeDto r) {
        Recipe recipe = new Recipe();
        recipe.setCreated(r.getCreated());
        recipe.setId(r.getRecipeId());
        recipe.setUpdated(new Timestamp(new Date().getTime()));
        recipe.setTime(r.getTotalTime());
        recipe.setLongName(r.getLongName());
        recipe.setShortName(r.getShortName());
        recipe.setCategory(r.getCategory());
        recipe.setDescription(r.getRecipeDescription());
        recipe.setExperience(r.getExperience());
        recipe.setId(r.getRecipeId());
        recipe.setLevel(r.getLevel());
        recipe.setAuthor(r.getAuthor());
        recipe.setIngredients(r.getIngredients());
        recipe.setPictureUrl(r.getPictureUrl());
        recipe.setThumbnailUrl(r.getThumbnailUrl());
        return recipe;
    }

    private void deleteSteps(Long recipeId) {
        Query q = em.createQuery("SELECT s FROM Step s WHERE s.recipe.id = :recipeId");
        q.setParameter("recipeId", recipeId);
        List<Step> steps = q.getResultList();

        for(Step s: steps) {
            if(s == null)
                continue;
            em.remove(s);
        }
    }

    private void createRecipe(FormDataMultiPart multiPart) throws Exception {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {
            String shortName = multiPart.getField("shortName").getValueAs(String.class);
            String longName = multiPart.getField("longName").getValueAs(String.class);
            String experience = multiPart.getField("experience").getValueAs(String.class);
            String time = multiPart.getField("time").getValueAs(String.class);
            String description = multiPart.getField("description").getValueAs(String.class);
            String category = multiPart.getField("category").getValueAs(String.class);
            String level = multiPart.getField("level").getValueAs(String.class);
            String author = multiPart.getField("author").getValueAs(String.class);

            RecipeDto recipeDto = new RecipeDto();
            recipeDto.setShortName(shortName);
            recipeDto.setLongName(longName);
            recipeDto.setExperience(Long.parseLong(experience));
            recipeDto.setTotalTime(Long.parseLong(time));
            recipeDto.setRecipeDescription(description);

            recipeDto.setCategory(em.find(Category.class, Long.parseLong(category)));
            recipeDto.setLevel(em.find(lv.cookster.entity.Level.class, Long.parseLong(level)));
            recipeDto.setAuthor(em.find(Author.class, Long.parseLong(author)));

            recipeDto.setPictureUrl(uploadFile(multiPart.getField("picture").getValueAs(InputStream.class)));
            recipeDto.setThumbnailUrl(uploadFile(multiPart.getField("thumbnail").getValueAs(InputStream.class)));

            Recipe recipe = convertToRecipe(recipeDto);
            recipe.setCreated(new Timestamp(new Date().getTime()));

            em.persist(recipe);

            //shit start
            for(int i=1; ; i++) {
                if(multiPart.getField("stepOrderNumber"+i) == null
                        || multiPart.getField("stepTime"+i) == null
                        || multiPart.getField("stepDescription"+i) == null
                        || multiPart.getField("stepPicture"+i) == null) {
                    break;
                }
                String stepOrderNumber = multiPart.getField("stepOrderNumber"+i).getValueAs(String.class);
                String stepTime = multiPart.getField("stepTime"+i).getValueAs(String.class);
                String stepDescription = multiPart.getField("stepDescription"+i).getValueAs(String.class);
                String stepPicture = uploadFile(multiPart.getField("stepPicture"+i).getValueAs(InputStream.class));

                Step step = new Step();
                step.setOrderNumber(Long.parseLong(stepOrderNumber));
                step.setPictureUrl(stepPicture);
                step.setDescription(stepDescription);
                step.setTime(Long.parseLong(stepTime));
                step.setRecipe(recipe);

                em.persist(step);
            }

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    private void deleteRecipe(Long id) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Recipe r = em.find(Recipe.class, id);

        deleteSteps(id);

        em.remove(r);
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
