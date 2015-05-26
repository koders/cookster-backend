package lv.cookster.rest.admin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lv.cookster.entity.*;
import lv.cookster.entity.dto.RecipeDto;
import lv.cookster.entity.dto.StepDto;
import lv.cookster.rest.CookingService;
import net.sf.ehcache.Cache;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
public class RecipeAdminService extends CookingService {

    private Gson gson = new Gson();
    private Cache cache;

    private final static Logger Log = Logger.getLogger(RecipeAdminService.class.getName());

    public RecipeAdminService() {
        if(cm.getCache("recipeCache") == null) {
            cm.addCache("recipeCache");
        }
        cache = cm.getCache("recipeCache");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OperationResult createFromJson(String data) {
        OperationResult result = new OperationResult();
        try {
            createRecipe(data);
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
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            deleteRecipe(id);
            cache.remove("allRecipes");
        } catch (Exception e) {
            Log.log(Level.SEVERE, e.toString(), e);
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response edit(@PathParam("id") Long id,
                         String data) {

        try {
//            Log.log(Level.SEVERE, "### Calling editRecipe RecipeId=<" + id + ">");
            RecipeDto recipeDto = gson.fromJson(data, RecipeDto.class);
            editRecipe(id, recipeDto);
            cache.remove("allRecipes");
        } catch (JsonSyntaxException e) {
            Log.log(Level.SEVERE, "Failed to parse JSON.");
            return Response.serverError().build();
        } catch (Exception e) {
            Log.log(Level.SEVERE, "Failed to commit recipe.");
            return Response.serverError().build();
        }

        return Response.ok().build();

    }

    private void editRecipe(Long id, RecipeDto recipeDto) {
        Recipe recipe = em.find(Recipe.class, id);
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {
            recipe.setUpdated(new Timestamp(new Date().getTime()));
            if(recipeDto.getLevel() != null) {
                recipe.setLevel(em.find(lv.cookster.entity.Level.class, recipeDto.getLevel().getId()));
            }
            recipe.setSteps(new ArrayList<Step>());
            em.merge(recipe);

//        deleteSteps(id);

            for(StepDto s:recipeDto.getSteps()) {
                Step step = new Step();
                step.setDescription(s.getDescription());
                // Picture
                step.setPicture(findImage(s.getPictureUrl()));
                if(step.getPicture() == null) {
                    Image image = new Image(s.getPictureId(), s.getPictureUrl());
                    step.setPicture(image);
                }
                step.setTime(s.getTime());
                step.setRecipe(recipe);
                step.setOrderNumber(s.getOrderNumber());
                em.persist(step);
            }

            // Ingredients
//            for(Ingredient ingredient:recipeDto.getIngredients()) {
//                if(ingredient.getProduct() != null) {
//                    ingredient.setProduct(em.find(Product.class, ingredient.getProduct().getId()));
//                }
//                if(ingredient.getMeasurement() != null) {
//                    ingredient.setMeasurement(em.find(Measurement.class, ingredient.getMeasurement().getId()));
//                }
//                ingredient.setRecipe(recipe);
//                em.persist(ingredient);
//            }

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    private Recipe convertToRecipe(RecipeDto r) {
        Recipe recipe = new Recipe();
        recipe.setCreated(r.getCreated());
        recipe.setId(r.getId());
        recipe.setUpdated(new Timestamp(new Date().getTime()));
        recipe.setTime(r.getTotalTime());
        recipe.setLongName(r.getLongName());
        recipe.setShortName(r.getShortName());
        Category c = em.find(Category.class, r.getCategory().getId());
        recipe.setCategory(c);
        recipe.setDescription(r.getDescription());
        recipe.setExperience(r.getExperience());
        lv.cookster.entity.Level l = em.find(lv.cookster.entity.Level.class, r.getLevel().getId());
        recipe.setLevel(l);
        Author a = em.find(Author.class, r.getAuthor().getId());
        recipe.setAuthor(a);
        //TODO
        recipe.setIngredients(r.getIngredients());
        for(Ingredient ingredient:recipe.getIngredients()) {
            ingredient.setRecipe(recipe);
        }
        // Picture
        recipe.setPicture(findImage(r.getPictureUrl()));
        if(recipe.getPicture() == null) {
            Image image = new Image(r.getPictureId(), r.getPictureUrl());
            recipe.setPicture(image);
        }
        // Thumbnail
        recipe.setThumbnail(findImage(r.getThumbnailUrl()));
        if(recipe.getThumbnail() == null) {
            Image image = new Image(r.getThumbnailId(), r.getThumbnailUrl());
            recipe.setThumbnail(image);
        }
        return recipe;
    }

    private Image findImage(String url) {
        Query q = em.createQuery("SELECT i FROM Image i WHERE i.url = :url");
        q.setParameter("url", url);
        try {
            Image result = (Image)q.getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
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

    private void createRecipe(String data) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {

            RecipeDto recipeDto = gson.fromJson(data, RecipeDto.class);
//            String shortName = multiPart.getField("shortName").getValueAs(String.class);
//            String longName = multiPart.getField("longName").getValueAs(String.class);
//            String experience = multiPart.getField("experience").getValueAs(String.class);
//            String time = multiPart.getField("time").getValueAs(String.class);
//            String description = multiPart.getField("description").getValueAs(String.class);
//            String category = multiPart.getField("category").getValueAs(String.class);
//            String level = multiPart.getField("level").getValueAs(String.class);
//            String author = multiPart.getField("author").getValueAs(String.class);
//
//            RecipeDto recipeDto = new RecipeDto();
//            recipeDto.setShortName(shortName);
//            recipeDto.setLongName(longName);
//            recipeDto.setExperience(Long.parseLong(experience));
//            recipeDto.setTotalTime(Long.parseLong(time));
//            recipeDto.setRecipeDescription(description);
//
//            recipeDto.setCategory(em.find(Category.class, Long.parseLong(category)));
//            recipeDto.setLevel(em.find(lv.cookster.entity.Level.class, Long.parseLong(level)));
//            recipeDto.setAuthor(em.find(Author.class, Long.parseLong(author)));
//
//            recipeDto.setPictureUrl(uploadFile(multiPart.getField("picture").getValueAs(InputStream.class)));
//            recipeDto.setThumbnailUrl(uploadFile(multiPart.getField("thumbnail").getValueAs(InputStream.class)));

            Recipe recipe = convertToRecipe(recipeDto);
            recipe.setCreated(new Timestamp(new Date().getTime()));

            em.persist(recipe);

            // Steps
            for(StepDto stepDto: recipeDto.getSteps()) {
//                if(multiPart.getField("stepOrderNumber"+i) == null
//                        || multiPart.getField("stepTime"+i) == null
//                        || multiPart.getField("stepDescription"+i) == null
//                        || multiPart.getField("stepPictureId"+i) == null
//                        || multiPart.getField("stepPictureUrl"+i) == null) {
//                    break;
//                }
//                String stepOrderNumber = multiPart.getField("stepOrderNumber"+i).getValueAs(String.class);
//                String stepTime = multiPart.getField("stepTime"+i).getValueAs(String.class);
//                String stepDescription = multiPart.getField("stepDescription"+i).getValueAs(String.class);
//                String stepPictureId = multiPart.getField("stepPictureId"+i).getValueAs(String.class);
//                String stepPictureUrl = multiPart.getField("stepPictureUrl"+i).getValueAs(String.class);

                Step step = new Step();
                step.setOrderNumber(stepDto.getOrderNumber());
                // Picture
                step.setPicture(findImage(stepDto.getPictureUrl()));
                if(step.getPicture() == null) {
                    Image image = new Image(stepDto.getPictureId(), stepDto.getPictureUrl());
                    step.setPicture(image);
                }
                step.setDescription(stepDto.getDescription());
                step.setTime(stepDto.getTime());
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

//    private List<StepDto> convertStepsToDto(List<Step> steps) {
//        List<StepDto> stepsDto = new ArrayList<StepDto>();
//
//        for(Step s:steps) {
//            StepDto stepDto = new StepDto();
//            stepDto.setStepId(s.getId());
//            stepDto.setStepTime(s.getTime());
//            stepDto.setDescription(s.getDescription());
//            stepDto.setPictureUrl(s.getPictureUrl());
//            stepDto.setOrderNumber(s.getOrderNumber());
//            stepsDto.add(stepDto);
//        }
//
//        return stepsDto;
//    }

}
