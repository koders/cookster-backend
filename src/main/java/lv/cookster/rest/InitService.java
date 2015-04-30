package lv.cookster.rest;

import lv.cookster.entity.Author;
import lv.cookster.entity.Category;
import lv.cookster.entity.Level;
import lv.cookster.entity.OperationResult;

import javax.persistence.EntityTransaction;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

/**
 * Created by Rihards on 15.06.2014.
 */
@Path("init")
public class InitService extends CookingService {

    private final static Logger Log = Logger.getLogger(CookingService.class.getName());

    @PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public OperationResult initialize() {

        OperationResult result = new OperationResult();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {

            //Initialize levels
            Level level = new Level();
            level.setLevel("Slow");
            em.persist(level);

            level = new Level();
            level.setLevel("Medium");
            em.persist(level);

            level = new Level();
            level.setLevel("Fast");
            em.persist(level);

            //Initialize cateogries
            Category category = new Category();
            category.setName("Kūkas");
            category.setPaid(false);
            category.setPictureUrl("umad.png");
            em.persist(category);

            category = new Category();
            category.setName("Veģetāri ēdieni");
            category.setPaid(false);
            category.setPictureUrl("umad.png");
            em.persist(category);

            category = new Category();
            category.setName("Uzkodas");
            category.setPaid(true);
            category.setPictureUrl("umad.png");
            em.persist(category);

            category = new Category();
            category.setName("Grila ēdieni");
            category.setPaid(false);
            category.setPictureUrl("umad.png");
            em.persist(category);

            category = new Category();
            category.setName("Gaļas ēdieni");
            category.setPaid(true);
            category.setPrice(0.89);
            category.setPictureUrl("umad.png");
            em.persist(category);

            category = new Category();
            category.setName("Zupas");
            category.setPaid(false);
            category.setPictureUrl("umad.png");
            em.persist(category);

            category = new Category();
            category.setName("Brokastu ēdieni");
            category.setPaid(true);
            category.setPictureUrl("umad.png");
            em.persist(category);

            //Init authors
            Author a = new Author();
            a.setName("Cepēji un šmorētāji");
            a.setPictureUrl("umad.png");
            a.setAbout("Cepēji un šmorētāji esam divi, taču ēdāju parasti ir vismaz divreiz vairāk, jo lielākoties ēdieni top nedēļas nogalēs, kad maltītēm pievienojas visa ģimene. Bloga sākumi meklējami 2009. gadā, kad radās vēlme pagatavot “īstu” soļanku un to arī iemūžināt fotogrāfijās. Lai šo soļanku, kurai drīz vien pievienojās arī mums iecienītā siera kūka, varētu ātri dabūt rokā, receptes sākām apkopot bloga formātā. Kad reiz sākts, tad turpināt jau ir viegli :)" +
                    "\n" +
                    "Kāpēc ņam, ņam – šmak, šmak? Īsi sakot: pēc garām un viedām pārdomām par bloga nosaukumu tika apcerēti bērnības laiki, unc kā izrādās, dažam šī onomatopoēze ir pirmais teikums latviešu valodā.\n" +
                    "\n" +
                    "Ja vēlies mums ieteikt kādu recepti, nosūtīt sveicienu vai ēdienam veltītu dzejrindi, gaidām ziņu uz njampasts[at]gmail.com.\n" +
                    "\n" +
                    "Recepšu pārpublicēšana ir atļauta, ievietojot atsauci uz recepšu avotu.");
            a.setContactInfo("http://njamnjams.wordpress.com/about/");
            em.persist(a);

            tx.commit();
        } catch (Exception e) {
            Log.log(java.util.logging.Level.SEVERE, "Failed to init data.");
            tx.rollback();
            e.printStackTrace();
            failResult(result, e);
        }
        return result;
    }

}
