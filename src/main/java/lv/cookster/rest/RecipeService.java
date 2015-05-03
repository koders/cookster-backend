package lv.cookster.rest;

import com.google.gson.Gson;
import lv.cookster.entity.*;
import lv.cookster.entity.dto.RecipeDto;
import lv.cookster.entity.dto.StepDto;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import javax.persistence.Query;
import javax.ws.rs.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * about
 *
 * @author Rihards
 */
@Path("/recipes")
public class RecipeService extends CookingService {

    private Gson gson = new Gson();
    private Cache cache = cm.getCache("recipeCache");

    private final static Logger Log = Logger.getLogger(RecipeService.class.getName());

    @GET
    @Produces("application/json;charset=UTF-8")
    public List<RecipeDto> allRecipes(@QueryParam("dateFrom")Timestamp dateFrom,
                                      @QueryParam("dateTo")Timestamp dateTo,
                                      @QueryParam("count")Integer count) {

        List<RecipeDto> recipes;

        //Check in cache
        Element element = cache.get("allRecipes");
        if(element != null) {
            //Cache hit
            return (List<RecipeDto>)element.getObjectValue();
        }

        //Cache miss
        Query q = em.createQuery("SELECT r FROM Recipe r");
        List<Recipe> recipesList = (List<Recipe>) q.getResultList();

        List<Recipe> resultList = new ArrayList<Recipe>();

        Integer i = 0;
        for(Recipe r:recipesList) {
            if((dateFrom == null || r.getUpdated().after(dateFrom))
                    &&(dateTo == null || r.getUpdated().before(dateTo))
                    && (count == null || i++ < count))
                resultList.add(r);
        }

        recipes = convertRecipesToDtoSmall(resultList);

        //Add to cache
        cache.put(new Element("allRecipes", recipes));

        return recipes;
    }


    @GET
    @Path("/{id}")
    @Produces("application/json;charset=UTF-8")
    public RecipeDto getRecipe(@PathParam("id") Long recipeId) {

        Recipe recipe = em.find(Recipe.class, recipeId);
        if(recipe == null)
            return null;

        Query q = em.createQuery("SELECT s FROM Step s where s.recipe.id = :recipeId");
        q.setParameter("recipeId", recipeId);
        List<Step> steps = (List<Step>) q.getResultList();


        RecipeDto resultRecipe = new RecipeDto();
        resultRecipe.setRecipeId(recipe.getId());
        resultRecipe.setCategory(recipe.getCategory());
        resultRecipe.setRecipeDescription(recipe.getDescription());
        resultRecipe.setExperience(recipe.getExperience());
        resultRecipe.setLongName(recipe.getLongName());
        resultRecipe.setShortName(recipe.getShortName());
        resultRecipe.setTotalTime(recipe.getTime());
        resultRecipe.setSteps(convertStepsToDto(steps));
        resultRecipe.setCreated(recipe.getCreated());
        resultRecipe.setUpdated(recipe.getUpdated());
        resultRecipe.setLevel(recipe.getLevel());
        resultRecipe.setAuthor(recipe.getAuthor());
        if(resultRecipe.getAuthor() != null)
            resultRecipe.getAuthor().setRecipes(null);
        resultRecipe.setIngredients(recipe.getIngredients());
        resultRecipe.setPictureUrl(recipe.getPictureUrl());
        resultRecipe.setThumbnailUrl(recipe.getThumbnailUrl());

        return resultRecipe;
    }

    /**
     * Get image for step
     * @since 20.10.2014
     * @param id recipe id
     * @param stepId step id
     * @return base64 string for step image
     */
    @GET
    @Path("/{id}/steps/{stepId}/picture")
    public String getImageForStep(@PathParam("id") Long id,
                                  @PathParam("stepId") Long stepId) {
        String result;
        try {
            Query q = em.createQuery("SELECT s FROM Step s where s.recipe.id = :recipeId and s.orderNumber = :stepId");
            q.setParameter("recipeId", id);
            q.setParameter("stepId", stepId);
            List<Step> steps = (List<Step>) q.getResultList();

            Step resultStep = steps.get(0);
            result = resultStep.getPictureUrl();

        } catch (Exception e) {
            e.printStackTrace();
            result = e.toString();
        }

        return result;
    }


    /**
     * Get single step info
     * @since 20.10.2014
     * @param id recipe id
     * @param stepId step id
     * @return Step DTO object
     */
    @GET
    @Path("/{id}/steps/{stepId}")
    public StepDto getStep(@PathParam("id") Long id,
                                  @PathParam("stepId") Long stepId) {
        StepDto result = null;
        try {
            Query q = em.createQuery("SELECT s FROM Step s where s.recipe.id = :recipeId and s.orderNumber = :stepId");
            q.setParameter("recipeId", id);
            q.setParameter("stepId", stepId);
            List<Step> steps = (List<Step>) q.getResultList();

            List<StepDto> stepsDto = convertStepsToDto(steps);
            result = stepsDto.get(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
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
