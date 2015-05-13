package lv.cookster.rest.admin;

import lv.cookster.rest.CookingService;

import javax.persistence.EntityTransaction;
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

}
