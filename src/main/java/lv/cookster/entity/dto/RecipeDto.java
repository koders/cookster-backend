package lv.cookster.entity.dto;

import lv.cookster.entity.Author;
import lv.cookster.entity.Category;
import lv.cookster.entity.Ingredient;
import lv.cookster.entity.Level;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.sql.Timestamp;
import java.util.List;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class RecipeDto {

    private Long recipeId;
    private String shortName;
    private Long totalTime;
    private String longName;
    private String recipeDescription;
    private Long experience;
    private Category category;
    private Timestamp created;
    private Timestamp updated;
    private Level level;
    private Author author;
    private List<Ingredient> ingredients;
//    private String ingredients;
    private String thumbnailUrl;
    private String pictureUrl;
    private List<StepDto> steps;

    public RecipeDto(){
        super();
    }

    public List<StepDto> getSteps() {
        return steps;
    }

    public void setSteps(List<StepDto> steps) {
        this.steps = steps;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

    public Long getExperience() {
        return experience;
    }

    public void setExperience(Long experience) {
        this.experience = experience;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
