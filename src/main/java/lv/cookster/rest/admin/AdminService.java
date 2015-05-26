package lv.cookster.rest.admin;

import com.restfb.DefaultFacebookClient;
import com.restfb.Version;
import com.restfb.exception.FacebookOAuthException;
import lv.cookster.entity.OperationResult;
import lv.cookster.entity.User;
import lv.cookster.rest.CookingService;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Rihards on 13.05.2015.
 */
public class AdminService extends CookingService{

    private final static Logger Log = Logger.getLogger(AdminService.class.getName());


    protected boolean createObject(Object object) {
        Boolean result = true;
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            em.persist(object);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            Log.log(Level.SEVERE, e.getStackTrace().toString());
            result = false;
        }
        return result;
    }

    protected boolean deleteObject(Object object) {
        Boolean result = true;
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            em.remove(object);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            Log.log(Level.SEVERE, e.getStackTrace().toString());
            result = false;
        }
        return result;
    }

    protected boolean isAdmin(String fbToken) {
        OperationResult result= new OperationResult();
        facebookClient = new DefaultFacebookClient(fbToken, Version.VERSION_2_3);
        com.restfb.types.User fbUser = null;
        try {
            fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class);
        } catch (FacebookOAuthException e) {
            return false;
        }
        if(fbUser == null) {
            result.setMessage("failed to validate token");
            return false;
        }
        Query q = em.createQuery("SELECT u FROM User u WHERE u.facebookID = :fbUser");
        q.setParameter("fbUser", fbUser.getId());
        User user = null;
        try {
            user = (User)q.getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        if(user.isAdmin()) {
            return true;
        } else {
            return false;
        }
    }

}
