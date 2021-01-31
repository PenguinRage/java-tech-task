package com.rezdy.lunch.controller;

import com.rezdy.lunch.entity.Recipe;
import com.rezdy.lunch.runner.SpringDataProviderRunner;
import com.rezdy.lunch.service.LunchService;
import com.rezdy.lunch.utils.TestRecipeListFactory;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.NoResultException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringDataProviderRunner.class)
@ContextConfiguration()
@WebMvcTest(LunchController.class)
public class LunchControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LunchService lunchService;

    @DataProvider
    public static Object[][] validDateTestData() {
        TestRecipeListFactory factory = new TestRecipeListFactory();
        String[] test1 = {"C", "B", "A"};
        return new Object[][]{
                {"2021-01-31", factory.getRecipeList(test1)}
        };
    }
    @Test
    @UseDataProvider("validDateTestData")
    public void validDateForGetRecipesByDateRequest(final String date, List<Recipe> recipeList) throws Exception {
        when(lunchService.getRecipesByDate(LocalDate.parse(date))).thenReturn(recipeList);
        mvc.perform(get("/lunch?date=" + date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(recipeList.size())))
                .andExpect(jsonPath("$[0].title", is(recipeList.get(0).getTitle())));
    }

    @DataProvider
    public static Object[][] invalidDateTestData() {
        return new Object[][]{
                {"20-01-3120", "Invalid Date 1 - Expects Bad request"},
                {"not-a-date", "Invalid Date 2 - Expects Bad request"},
                {"0000000000", "Invalid Date 3 - Expects Bad request"}
        };
    }

    @Test
    @UseDataProvider("invalidDateTestData")
    public void invalidDateForGetRecipesByDateRequest(final String date, final String testCase) throws Exception {
        mvc.perform(get("/lunch?date=" + date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DataProvider
    public static Object[][] validTitleTestData() {
        return new Object[][]{
                {"Salad", new Recipe().setTitle("Salad")}
        };
    }
    @Test
    @UseDataProvider("validTitleTestData")
    public void validTitleForGetRecipeRequest(final String title, final Recipe expected) throws Exception {
        when(lunchService.getRecipeByTitle(title)).thenReturn(expected);
        mvc.perform(get("/lunch/recipe?title=" + title)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['title']", is(expected.getTitle())));
    }

    @DataProvider
    public static Object[][] invalidTitleTestData() {
        return new Object[][]{
                {"Pizza", "Test Case 404 - Not Found"}
        };
    }
    @Test
    @UseDataProvider("invalidTitleTestData")
    public void invalidTitleForGetRecipeRequest(final String title, final String testCase) throws Exception {
        when(lunchService.getRecipeByTitle(title)).thenThrow(NoResultException.class);
        mvc.perform(get("/lunch/recipe?title=" + title)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DataProvider
    public static Object[][] validExcludedIngredientsTestData() {
        TestRecipeListFactory factory = new TestRecipeListFactory();
        String[] test1 = {"A"};
        List<Recipe> expected = factory.getRecipeList(test1);
        List<String> excluded = new ArrayList<>();
        excluded.add("B");
        excluded.add("C");

        return new Object[][]{
                { excluded, expected, "Test Case: Simple valid exclude ingredient request" }
        };
    }
    @Test
    @UseDataProvider("validExcludedIngredientsTestData")
    public void validExcludedIngredientRecipesRequest(final List<String> list, final List<Recipe> expected, final String testCase) throws Exception {
        when(lunchService.getNonExcludedIngredientRecipes(list)).thenReturn(expected);
        mvc.perform(get("/lunch/exclude?ingredients="+ String.join(",", list))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expected.size())))
                .andExpect(jsonPath("$[0].title", is(expected.get(0).getTitle())));
    }

}