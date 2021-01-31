package com.rezdy.lunch.service;

import com.rezdy.lunch.entity.Ingredient;
import com.rezdy.lunch.entity.Recipe;
import com.rezdy.lunch.repository.RecipeCriteriaRepository;
import com.rezdy.lunch.utils.TestRecipeListFactory;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.rezdy.lunch.runner.SpringDataProviderRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringDataProviderRunner.class)
@SpringBootTest
public class LunchServiceTest {

    @Autowired
    private LunchService lunchService;

    @MockBean
    private RecipeCriteriaRepository recipeRepository;

    @Before
    public void setUpRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setTitle("Salad");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient().setTitle("Tomato"));
        recipe.setIngredients(new HashSet<>(ingredients));
        Mockito.when(recipeRepository.getRecipe(recipe.getTitle())).thenReturn(recipe);
    }

    @DataProvider
    public static Object[][] validRecipeTestData() {
        final Recipe expectedRecipe = new Recipe();
        expectedRecipe.setTitle("Salad");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient().setTitle("Tomato"));
        expectedRecipe.setIngredients(new HashSet<>(ingredients));
        return new Object[][]{
                {"Salad", expectedRecipe , "Valid Recipe Test Case"}
        };
    }

    @Test
    @UseDataProvider("validRecipeTestData")
    public void validGetRecipeByTitle(final String title, final Recipe expected, final String testCase) {
        Recipe result = lunchService.getRecipeByTitle(title);
        assertNotNull(testCase, result);
        assertEquals(testCase, result.getTitle(), expected.getTitle());
        assertEquals(testCase, result.getIngredients().size(), expected.getIngredients().size());
    }

    @DataProvider
    public static Object[][] invalidRecipeTestData() {
        return new Object[][]{
                {"", "Empty String Test Case"},
                {"Football", "Recipe not Found Test case"}
        };
    }

    @Test
    @UseDataProvider("invalidRecipeTestData")
    public void invalidGetRecipeByTitle(final String title, final String testCase) {
        Recipe result = lunchService.getRecipeByTitle(title);
        assertNull(testCase, result);
    }

    @DataProvider
    public static Object[][] validRecipesTestData() {
        TestRecipeListFactory factory = new TestRecipeListFactory();
        String[] test1 = {"C", "B", "A"};
        String[] expected1 = {"A", "B", "C"};
        String[] test2 = {"C", "B", "A", "C", "A", "B"};
        String[] expected2 = {"A", "A", "B", "B", "C", "C"};

        return new Object[][]{
                {factory.getRecipeList(test1), expected1, "Empty String Test Case"},
                {factory.getRecipeList(test2), expected2, "Recipe not Found Test case"}
        };
    }
    @Test
    @UseDataProvider("validRecipesTestData")
    public void sortNonExpiredRecipes(final ArrayList<Recipe> recipes, final String[] expected, final String testCase) {
        // Cases already handled: Expired Recipes get filtered of usedBy through Criteria Queries
        Mockito.when(recipeRepository.getNonExpiredRecipes(any())).thenReturn(recipes);
        List<Recipe> result = lunchService.getRecipesByDate(LocalDate.now());
        for (int i =0; i < expected.length; i++) {
            System.out.println(result.get(i).getTitle());
        }
    }
}