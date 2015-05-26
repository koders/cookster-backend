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
    private Cache cache;

    private final static Logger Log = Logger.getLogger(RecipeService.class.getName());

    public RecipeService() {
        if(cm.getCache("recipeCache") == null) {
            cm.addCache("recipeCache");
        }
        cache = cm.getCache("recipeCache");
    }

    @GET
    @Produces("application/json;charset=UTF-8")
    public List<RecipeDto> allRecipes(@QueryParam("dateFrom")Timestamp dateFrom,
                                      @QueryParam("dateTo")Timestamp dateTo,
                                      @QueryParam("count")Integer count) {

        List<Recipe> recipes;
        List<Recipe> resultList = new ArrayList<Recipe>();

        recipes = fetchRecipes();

        //Filter
        Integer i = 0;
        for(Recipe r:recipes) {
            if((dateFrom == null || r.getUpdated().after(dateFrom))
                    &&(dateTo == null || r.getUpdated().before(dateTo))
                    && (count == null || i++ < count))
                resultList.add(r);
        }

        return convertRecipesToDtoSmall(resultList);
    }


    @GET
    @Path("/{id}")
    @Produces("application/json;charset=UTF-8")
    public RecipeDto getRecipe(@PathParam("id") Long recipeId) {

        Recipe recipe = null;

        List<Recipe> recipes = fetchRecipes();

        for(Recipe r: recipes) {
            if(recipeId == r.getId()) {
                recipe = r;
                break;
            }
        }

        if(recipe == null)
            return null;

//        Query q = em.createQuery("SELECT s FROM Step s where s.recipe.id = :recipeId");
//        q.setParameter("recipeId", recipeId);
//        List<Step> steps = (List<Step>) q.getResultList();

        RecipeDto resultRecipe = new RecipeDto();
        resultRecipe.setId(recipe.getId());
        resultRecipe.setCategory(recipe.getCategory());
        resultRecipe.setDescription(recipe.getDescription());
        resultRecipe.setExperience(recipe.getExperience());
        resultRecipe.setLongName(recipe.getLongName());
        resultRecipe.setShortName(recipe.getShortName());
        resultRecipe.setTotalTime(recipe.getTime());
        resultRecipe.setSteps(convertStepsToDto(recipe.getSteps()));
        resultRecipe.setCreated(recipe.getCreated());
        resultRecipe.setUpdated(recipe.getUpdated());
        resultRecipe.setLevel(recipe.getLevel());
        resultRecipe.setAuthor(recipe.getAuthor());
        if(resultRecipe.getAuthor() != null)
            resultRecipe.getAuthor().setRecipes(null);
        resultRecipe.setIngredients(recipe.getIngredients());
        if(recipe.getPicture() != null) {
            resultRecipe.setPictureUrl(recipe.getPicture().getUrl());
        }
        if(recipe.getThumbnail() != null) {
            resultRecipe.setThumbnailUrl(recipe.getThumbnail().getUrl());
        }

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
            result = resultStep.getPicture().getUrl();

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
            stepDto.setId(s.getId());
            stepDto.setTime(s.getTime());
            stepDto.setDescription(s.getDescription());
            if(s.getPicture().getUrl() != null) {
                stepDto.setPictureUrl(s.getPicture().getUrl());
            }
            stepDto.setOrderNumber(s.getOrderNumber());
            stepsDto.add(stepDto);
        }

        return stepsDto;
    }

    protected List<Recipe> fetchRecipes() {
        List<Recipe> recipes;
        //Check in cache
        Element element = cache.get("allRecipes");
        if(element != null) {
            //Cache hit
            recipes = (List<Recipe>)element.getObjectValue();
        } else {
            //Cache miss
            Query q = em.createQuery("SELECT r FROM Recipe r");
            recipes = (List<Recipe>) q.getResultList();

            //Add to cache
            cache.put(new Element("allRecipes", recipes));
        }
        return recipes;
    }

}
