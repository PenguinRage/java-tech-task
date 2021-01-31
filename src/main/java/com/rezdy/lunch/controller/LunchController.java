package com.rezdy.lunch.controller;

import com.rezdy.lunch.entity.Recipe;
import com.rezdy.lunch.service.LunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class LunchController {

    final private LunchService lunchService;

    @Autowired
    public LunchController(LunchService lunchService) {
        this.lunchService = lunchService;
    }

    @GetMapping("/lunch")
    public List<Recipe> getRecipesByDate(@RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return lunchService.getRecipesByDate(date);
    }

    @GetMapping("/lunch/recipe")
    public Recipe getRecipe(@RequestParam(value = "title") String title) {
        return lunchService.getRecipeByTitle(title);
    }

    @GetMapping("/lunch/exclude")
    public List<Recipe> getRecipesByExcludedIngredients(@RequestParam(value = "ingredients") List<String> excludedIngredients) {
        return lunchService.getNonExcludedIngredientRecipes(excludedIngredients);
    }
}
