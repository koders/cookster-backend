package lv.cookster.rest;

import com.restfb.FacebookClient;
import lv.cookster.entity.OperationResult;
import lv.cookster.entity.Recipe;
import lv.cookster.entity.dto.RecipeDto;
import lv.cookster.util.Constants;
import net.sf.ehcache.CacheManager;
import org.apache.commons.io.FileUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main service class
 *
 * @author Rihards
 */
public class CookingService {

    protected EntityManagerFactory emf;
    protected EntityManager em;
    protected CacheManager cm = CacheManager.getInstance();
    protected FacebookClient facebookClient;

    public CookingService() {
        emf = Persistence.createEntityManagerFactory(Constants.CONNECTION_NAME, System.getProperties());
        em = emf.createEntityManager();
        if(cm.getCache("recipeCache") == null) {
            cm.addCache("recipeCache");
        }
    }

    protected String findParam(UriInfo uriInfo, String parameterName) {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        String parameter = queryParams.getFirst(parameterName);
        return parameter;
    }

    protected String detectFormat(UriInfo uriInfo) {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        String format = queryParams.getFirst("format");
        if (Constants.JSON_FORMAT_URL_PARAMETER.equalsIgnoreCase(format)) {
            return MediaType.APPLICATION_JSON;
        } else {
            return MediaType.APPLICATION_XML;
        }
    }

    protected void failResult(OperationResult result, Exception e) {
        em.getTransaction().rollback();
        result.setResultCode(Constants.OPERATION_FAILED_CODE);
        result.setMessage(e.getStackTrace().toString());
    }

    protected void successResult(OperationResult result) {
        result.setMessage(Constants.OPERATION_SUCCESSFUL_MESSAGE);
        result.setResultCode(Constants.OPERATION_SUCCESSFUL_CODE);
    }

    /**
     * save uploaded file to new location
     * @since 07.12.2014
     * @param uploadedInputStream
     * @param uploadedFileLocation
     */
    protected void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation) {

        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    /**
     * @since 07.12.2014
     * @param inputStream
     * @return uploaded file access url or NULL if failed to upload
     */
    protected String uploadFile(InputStream inputStream) {
        String result = "";
        try {
            String uuid = UUID.randomUUID().toString();
            String uploadedFileLocation = Constants.PROJECT_BASE_LOCATION + Constants.SOURCE_FOLDER + uuid + Constants.PICTURE_EXTENSION;
            String uploadedFileLocation2 = Constants.PROJECT_BASE_LOCATION + Constants.TARGET_FOLDER + uuid + Constants.PICTURE_EXTENSION;
            writeToFile(inputStream, uploadedFileLocation);
            FileUtils.copyFile(new File(uploadedFileLocation), new File(uploadedFileLocation2));
            result = Constants.BASE_URI + "uploads/" + uuid + Constants.PICTURE_EXTENSION;
        } catch (IOException e) {
            result = null;
            e.printStackTrace();
        }

        return result;
    }

    /**
     * For all recipes list screen
     * @since 14.12.2014
     * @param recipesList
     * @return recipes list converted to Dto for list screen
     */
    protected List<RecipeDto> convertRecipesToDtoSmall(List<Recipe> recipesList) {
        List<RecipeDto> recipes = new ArrayList<RecipeDto>();

        for(Recipe r: recipesList) {
            RecipeDto recipe = new RecipeDto();

//            recipe.setDescription(r.getDescription());
            recipe.setCategory(r.getCategory());
            recipe.setExperience(r.getExperience());
            recipe.setRecipeId(r.getId());
//            recipe.setLongName(r.getLongName());
            recipe.setShortName(r.getShortName());
            recipe.setTotalTime(r.getTime());
            recipe.setLevel(r.getLevel());
            recipe.setPictureUrl(r.getPictureUrl());
            recipe.setAuthor(r.getAuthor());
            if(recipe.getAuthor() != null)
                recipe.getAuthor().setRecipes(null);

//            q = em.createQuery("SELECT s FROM Step s WHERE s.recipe.id = :recipeId");
//            q.setParameter("recipeId",r.getId());
//            List<Step> steps = q.getResultList();
//            recipe.setSteps(convertStepsToDto(steps));
//
            recipes.add(recipe);
        }

        return recipes;
    }

}
