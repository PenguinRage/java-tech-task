package com.rezdy.lunch.utils;

import com.rezdy.lunch.entity.Ingredient;
import com.rezdy.lunch.entity.Recipe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TestRecipeListFactory {

    Ingredient ingredientA;
    Ingredient ingredientB;
    Ingredient ingredientC;
    Ingredient ingredientD;

    public TestRecipeListFactory() {
        LocalDate past = LocalDate.parse("2019-01-06");
        LocalDate bestBeforeChristmas = LocalDate.parse("2020-12-25");
        LocalDate future = LocalDate.parse("2031-01-31");

        ingredientA = new Ingredient();
        ingredientA.setTitle("A");
        ingredientA.setUseBy(future);
        ingredientA.setBestBefore(future);

        ingredientC = new Ingredient();
        ingredientC.setTitle("C");
        ingredientC.setUseBy(future);
        ingredientC.setBestBefore(bestBeforeChristmas);

        ingredientB = new Ingredient();
        ingredientB.setTitle("B");
        ingredientB.setUseBy(future);
        ingredientB.setBestBefore(past);

        ingredientD = new Ingredient();
        ingredientD.setTitle("D");
        ingredientD.setUseBy(future);
        ingredientD.setBestBefore(LocalDate.now());



    }

    public ArrayList<Recipe> getRecipeList(final String[] list) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        for (String element : list) {
            if (element.equals("A")) recipes.add(getRecipeA());
            if (element.equals("B")) recipes.add(getRecipeB());
            if (element.equals("C")) recipes.add(getRecipeC());
        }
        return recipes;
    }

    public Recipe getRecipeA() {
        Recipe a = new Recipe();
        a.setTitle("A");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredientD);
        ingredients.add(ingredientA);
        a.setIngredients(new HashSet<>(ingredients));
        return a;
    }

    public Recipe getRecipeB() {
        Recipe b = new Recipe();
        b.setTitle("B");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredientD);
        ingredients.add(ingredientB);
        b.setIngredients(new HashSet<>(ingredients));
        return b;
    }

    public Recipe getRecipeC() {
        Recipe c = new Recipe();
        c.setTitle("C");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredientD);
        ingredients.add(ingredientC);
        c.setIngredients(new HashSet<>(ingredients));
        return c;
    }
}
