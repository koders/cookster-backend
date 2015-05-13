package lv.cookster.rest.user;

import com.google.gson.Gson;
import com.restfb.DefaultFacebookClient;
import com.restfb.Version;
import lv.cookster.entity.OperationResult;
import lv.cookster.entity.Recipe;
import lv.cookster.entity.Step;
import lv.cookster.entity.User;
import lv.cookster.entity.dto.RecipeDto;
import lv.cookster.entity.dto.StepDto;
import lv.cookster.rest.CookingService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.ws.rs.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * user service
 *
 * @since 02.05.2015
 *
 * @author Rihards
 */
@Path("/users")
public class UserService extends CookingService {

    private Gson gson = new Gson();
    private Cache cache = cm.getCache("userCache");

    private final static Logger Log = Logger.getLogger(UserService.class.getName());

    /**
     * returns user object by passed id
     * @since 02.05.2015
     * @param userId
     * @return
     */
    @GET
    @Path("/{id}")
    @Produces("application/json;charset=UTF-8")
    public User getRecipe(@PathParam("id") Long userId) {

        User user = em.find(User.class, userId);

        return user;
    }

    /**
     * returns user's favorites by passed id
     * @since 02.05.2015
     * @param userId
     * @return
     */
    @GET
    @Path("/{id}/favorites")
    @Produces("application/json;charset=UTF-8")
    public List<Recipe> getFavorites(@PathParam("id") Long userId) {

        User user = em.find(User.class, userId);

        return user.getFavorites();
    }

    /**
     * returns user's favorites by passed id
     * @since 02.05.2015
     * @param userId
     * @return
     */
    @POST
    @Path("/registerFacebookUser")
    @Produces("application/json;charset=UTF-8")
    public OperationResult registerFacebookUserApi(@FormParam("fbToken") String fbToken) {
        OperationResult result= new OperationResult();
        facebookClient = new DefaultFacebookClient(fbToken, Version.VERSION_2_3);
        com.restfb.types.User fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class);
        if(fbUser == null) {
            result.setMessage("failed to validate token");
            return result;
        }
        if(!checkIfRegisteredFacebookUser(fbUser)) {
            registerFacebookUser(fbUser);
            result.setMessage("OK");
        } else {
            result.setMessage("Already registered");
        }
        return result;
    }

    /**
     * create new favorite for current user
     * @since 03.05.2015
     * @param fbToken
     * @param userId
     * @return
     */
    @POST
    @Path("/{id}/favorites/create")
    @Produces("application/json;charset=UTF-8")
    public OperationResult addFavorite(@PathParam("id") Long userId,
                                       @FormParam("fbToken")String fbToken,
                                       @FormParam("recipeId")Long recipeId) {
        OperationResult result= new OperationResult();
        facebookClient = new DefaultFacebookClient(fbToken, Version.VERSION_2_3);
        com.restfb.types.User fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class);
        if(fbUser == null) {
            result.setMessage("failed to validate token");
            return result;
        }
        User user =  em.find(User.class, userId);
        if(user == null) {
            result.setMessage("failed to find user");
            return result;
        }
        if(!fbUser.getId().equals(user.getFacebookID())) {
            result.setMessage("failed to match users");
            return result;
        }
        Recipe recipe = em.find(Recipe.class, recipeId);
        if(recipe == null) {
            result.setMessage("failed to find recipe");
            return result;
        }
        addFavorite(user, recipe);
        result.setMessage("OK");
        return result;
    }

    /**
     * Checks for facebook use rin database
     * @since 03.05.2015
     * @param fbUser
     * @return true if user is registered in our system, false otherwise
     */
    private boolean checkIfRegisteredFacebookUser(com.restfb.types.User fbUser) {
        Query q = em.createQuery("SELECT u FROM User u WHERE u.facebookID = :fbUser");
        q.setParameter("fbUser", fbUser.getId());
        User user = null;
        try {
            user = (User)q.getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    /**
     * Registers new user from facebook
     * @since 03.05.2015
     * @param fbUser
     */
    private void registerFacebookUser(com.restfb.types.User fbUser) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            User user = new User();
            user.setName(fbUser.getName());
            user.setFacebookID(fbUser.getId());
            user.setAbout(fbUser.getAbout());
            user.setContactInfo(fbUser.getEmail());
            if(fbUser.getPicture() != null) {
                user.setPictureUrl(fbUser.getPicture().getUrl());
            }
            user.setFavorites(new ArrayList<Recipe>());
            em.persist(user);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Adds new favorite recipe for user
     * @since 03.05.2015
     * @param user
     * @param recipe
     */
    private void addFavorite(User user, Recipe recipe) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            user.getFavorites().add(recipe);
            em.merge(user);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

}
