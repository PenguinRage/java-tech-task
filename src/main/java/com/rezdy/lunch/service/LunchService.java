package com.rezdy.lunch.service;

import com.rezdy.lunch.entity.Ingredient;
import com.rezdy.lunch.entity.Recipe;
import com.rezdy.lunch.repository.RecipeCriteriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LunchService {

    @Autowired
    private RecipeCriteriaRepository recipeCriteriaRepository;

    private LocalDate getEarliestIngredientBestBefore(Recipe recipe){
        return recipe.getIngredients().stream()
                .min(Comparator.comparing(Ingredient::getBestBefore))
                .orElseThrow(NoSuchElementException::new).getBestBefore();
    }

    private List<Recipe> sortRecipes(List<Recipe> recipes) {
        return recipes.stream()
                .sorted(Comparator.comparing(this::getEarliestIngredientBestBefore).reversed())
                .collect(Collectors.toList());
    }

    public List<Recipe> getRecipesByDate(LocalDate date) {
        // More Business Logic
        return sortRecipes(recipeCriteriaRepository.getNonExpiredRecipes(date));
    }

    public Recipe getRecipeByTitle(String title) {
        return recipeCriteriaRepository.getRecipe(title);
    }

    public List<Recipe> getNonExcludedIngredientRecipes(List<String> excludedIngredients) {
        // More Business Logic
        return recipeCriteriaRepository.getNonExcludedIngredientRecipes(excludedIngredients);
    }
}
