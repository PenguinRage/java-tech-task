package com.rezdy.lunch.repository;

import com.rezdy.lunch.entity.Ingredient;
import com.rezdy.lunch.entity.Recipe;
import com.rezdy.lunch.runner.SpringDataProviderRunner;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.copy;
import static org.junit.Assert.*;

@RunWith(SpringDataProviderRunner.class)
@SpringBootTest
public class RecipeCriteriaRepositoryTest {

    @Autowired
    private RecipeCriteriaRepository recipeCriteriaRepository;


    @DataProvider
    public static Object[][] validRecipeTestData() {
        ArrayList<String> expectedSaladIngredients = new ArrayList<>();
        expectedSaladIngredients.add("Beetroot");
        expectedSaladIngredients.add("Salad Dressing");
        expectedSaladIngredients.add("Cucumber");
        expectedSaladIngredients.add("Lettuce");
        expectedSaladIngredients.add("Tomato");
        ArrayList<String> expectedHotDogIngredients = new ArrayList<>();
        expectedHotDogIngredients.add("Hotdog Bun");
        expectedHotDogIngredients.add("Ketchup");
        expectedHotDogIngredients.add("Mustard");
        expectedHotDogIngredients.add("Sausage");
        ArrayList<String> expectedToastieIngredients = new ArrayList<>();
        expectedToastieIngredients.add("Butter");
        expectedToastieIngredients.add("Cheese");
        expectedToastieIngredients.add("Ham");
        expectedToastieIngredients.add("Bread");
        return new Object[][]{
                {"Salad", expectedSaladIngredients ,"Simple Recipe Match Test Case"},
                {"HOTDOG", expectedHotDogIngredients, "Valid Uppercase Recipe Match Test Case"},
                {"ham and cheese toastie", expectedToastieIngredients, "Valid Lowercase Recipe Match Test Case"}
        };
    }

    @Test
    @Transactional
    @UseDataProvider("validRecipeTestData")
    public void getRecipe(final String title, final ArrayList<String> expected, final String testCase) {
        Recipe recipe = recipeCriteriaRepository.getRecipe(title);
        assertNotNull(testCase, recipe);
        for (Ingredient ingredient : recipe.getIngredients()) {
            assertTrue(expected.contains(ingredient.getTitle()));
        }
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void invalidGetRecipe() {
        Recipe recipe = recipeCriteriaRepository.getRecipe("pizza");
    }

    @DataProvider
    public static Object[][] nonExpiredRecipesTestData() {
        final LocalDate date1 = LocalDate.parse("2021-01-31");
        final LocalDate date2 = LocalDate.parse("1990-01-31");
        final LocalDate date3 = LocalDate.parse("2100-01-31");
        final List<String> expectedRecipes1 = new ArrayList<>();
        expectedRecipes1.add("Fry-up");
        expectedRecipes1.add("Hotdog");
        expectedRecipes1.add("Ham and Cheese Toastie");

        final List<String> expectedRecipes2 = new ArrayList<>();
        expectedRecipes2.add("Fry-up");
        expectedRecipes2.add("Hotdog");
        expectedRecipes2.add("Ham and Cheese Toastie");
        expectedRecipes2.add("Salad");
        expectedRecipes2.add("Omelette");

        return new Object[][]{
                {date1, expectedRecipes1 ,"Test Case: Today's Expired Recipes "},
                {date2, expectedRecipes2,"Test Case: No Expired Recipes "},
                {date3, Collections.emptyList(), "Test Case: All Recipes Expired "}
        };
    }

    @Test
    @Transactional
    @UseDataProvider("nonExpiredRecipesTestData")
    public void getNonExpiredRecipes(final LocalDate date, final List<String> expected, final String testCase) {
        final List<Recipe> recipes = recipeCriteriaRepository.getNonExpiredRecipes(date);
        assertNotNull(recipes);
        assertEquals(testCase, recipes.size(),expected.size());
        recipes.forEach(recipe -> assertTrue(testCase + recipe.getTitle(), expected.contains(recipe.getTitle())));
    }


    @DataProvider
    public static Object[][] nonExcludedRecipesTestData() {
        String salad = "Salad";
        String hotdog = "Hotdog";
        String hact = "Ham and Cheese Toastie";
        String omelette = "Omelette";
        String fryup = "Fry-up";

        ArrayList<String> excludedIngredients1 = new ArrayList<>();
        excludedIngredients1.add("Eggs");

        ArrayList<String> excludedIngredients3 = new ArrayList<>();
        excludedIngredients3.add("Eggs");
        excludedIngredients3.add("tomato");
        excludedIngredients3.add("mustard");
        excludedIngredients3.add("Ham");



        ArrayList<String> expectedRecipes1 = new ArrayList<>();
        expectedRecipes1.add(salad);
        expectedRecipes1.add(hotdog);
        expectedRecipes1.add(hact);


        ArrayList<String> expectedRecipes2 = new ArrayList<>();
        expectedRecipes2.add(salad);
        expectedRecipes2.add(hotdog);
        expectedRecipes2.add(hact);
        expectedRecipes2.add(omelette);
        expectedRecipes2.add(fryup);


        return new Object[][]{
                {excludedIngredients1, expectedRecipes1 ,"Test Case: Simple Recipes Exclusion  "},
                { new ArrayList<String>(), expectedRecipes2, "Test Case: No Recipes Excluded " },
                {excludedIngredients3, new ArrayList<String>(), "Test Case: Everything Excluded "}
        };
    }

    @Test
    @Transactional
    @UseDataProvider("nonExcludedRecipesTestData")
    public void getNonExcludedIngredientRecipes(final ArrayList<String> excludedIngredients, final ArrayList<String> expected, final String testCase) {
        final List<Recipe> recipes = recipeCriteriaRepository.getNonExcludedIngredientRecipes(excludedIngredients);
        assertNotNull(recipes);
        assertEquals(testCase, recipes.size(), expected.size());
        recipes.forEach(recipe -> assertTrue(testCase + recipe.getTitle(), expected.contains(recipe.getTitle())));
    }
}