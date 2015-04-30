package lv.cookster.rest;

import lv.cookster.entity.*;
import lv.cookster.entity.dto.RecipeDto;

import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * about
 *
 * @author Rihards
 */
@Path("/authors")
public class AuthorService extends CookingService{

    private final static Logger Log = Logger.getLogger(CookingService.class.getName());

    @GET
    @Produces("application/json;charset=UTF-8")
    public List<Author> allAuthors() {

        List<Author> authors = new ArrayList<Author>();

        Query q = em.createQuery("SELECT a FROM Author a");
        List<Author> resultList = (List<Author>) q.getResultList();
        authors.addAll(resultList);
        for(Author a: resultList) {
            a.setRecipes(null);
        }
        return authors;
    }

    @GET
    @Path("/{id}")
    @Produces("application/json;charset=UTF-8")
    public Author getAuthor(@PathParam("id")Long id) {

        Author author = em.find(Author.class, id);
        if(author == null)
            return null;

        for(Recipe r: author.getRecipes())
            r.setAuthor(null);
        author.setRecipes(null);
        return author;
    }

    @GET
    @Path("/{id}/recipes")
    @Produces("application/json;charset=UTF-8")
    public List<RecipeDto> recipesForAuthor(@PathParam("id")Long id) {

        List<RecipeDto> recipes;

        Query q = em.createQuery("SELECT r FROM Recipe r where r.author.id = :authorId");
        q.setParameter("authorId",id);
        List<Recipe> resultList = (List<Recipe>) q.getResultList();

        recipes = convertRecipesToDtoSmall(resultList);

        return recipes;
    }



}
