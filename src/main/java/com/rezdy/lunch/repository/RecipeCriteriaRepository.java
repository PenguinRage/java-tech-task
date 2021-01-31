package com.rezdy.lunch.repository;

import com.rezdy.lunch.repository.model.RecipeCriteriaQuery;
import com.rezdy.lunch.entity.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository(value = "RecipeCriteriaRepository")
public class RecipeCriteriaRepository {

    @Autowired
    final private EntityManager entityManager;

    final private CriteriaBuilder cb;

    public RecipeCriteriaRepository(final EntityManager entityManager) {
        this.entityManager = entityManager;
        cb = entityManager.getCriteriaBuilder();
    }

    public Recipe getRecipe(String title) {
        RecipeCriteriaQuery recipeCriteriaQuery = new RecipeCriteriaQuery(cb);
        final Predicate titlePredicate = cb.equal(recipeCriteriaQuery.getRoot().get("title"), title);
            return entityManager.createQuery(recipeCriteriaQuery.getQuery()
                    .where(titlePredicate)).getSingleResult();
    }

    private List<Recipe> getExpiredRecipes(final LocalDate date) {
        RecipeCriteriaQuery recipeCriteriaQuery = new RecipeCriteriaQuery(cb);
        ArrayList<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(recipeCriteriaQuery.getSubqueryRoot().get("title"), recipeCriteriaQuery.getRoot().get("title")));
        predicates.add(cb.lessThan(recipeCriteriaQuery.getSubqueryRoot().join("ingredients").get("useBy"), date));

        Predicate ExpiredIngredients = cb.exists(recipeCriteriaQuery.getSubquery().where(predicates.toArray(Predicate[]::new)));
        return entityManager.createQuery(recipeCriteriaQuery.getQuery()
                .where(ExpiredIngredients)).getResultList();
    }

    public List<Recipe> getNonExpiredRecipes(final LocalDate date) {
        RecipeCriteriaQuery recipeCriteriaQuery = new RecipeCriteriaQuery(cb);
        List<Recipe> expiredRecipes = getExpiredRecipes(date);
        List<String> expiredRecipeTitles = expiredRecipes.stream().map(Recipe::getTitle).collect(Collectors.toList());
        return entityManager.createQuery(recipeCriteriaQuery.getQuery()
                .where(recipeCriteriaQuery.getRoot().get("title")
                        .in(expiredRecipeTitles).not())).getResultList();
    }

    private List<Recipe> getExcludedIngredientRecipes(List<String> excluded) {
        RecipeCriteriaQuery recipeCriteriaQuery = new RecipeCriteriaQuery(cb);
        ArrayList<Predicate> predicates = new ArrayList<>();
        excluded.forEach(ingredient -> {
            predicates.add(cb.equal(recipeCriteriaQuery.getSubqueryRoot().join("ingredients").get("title"), ingredient));
        });

        Predicate joinOnPredicate = cb.equal(recipeCriteriaQuery.getSubqueryRoot().get("title"), recipeCriteriaQuery.getRoot().get("title"));
        Predicate queryPredicates = cb.and(joinOnPredicate, cb.or(predicates.toArray(Predicate[]::new)));
        Predicate ExpiredIngredients = cb.exists(recipeCriteriaQuery.getSubquery().where(queryPredicates));

        return entityManager.createQuery(recipeCriteriaQuery.getQuery().where(ExpiredIngredients)).getResultList();
    }

    public List<Recipe> getNonExcludedIngredientRecipes(List<String> excluded) {
        RecipeCriteriaQuery recipeCriteriaQuery = new RecipeCriteriaQuery(cb);
        List<Recipe> excludedRecipes = getExcludedIngredientRecipes(excluded);
        List<String> expiredRecipeTitles = excludedRecipes.stream().map(Recipe::getTitle).collect(Collectors.toList());
        return entityManager.createQuery(recipeCriteriaQuery.getQuery()
                .where(recipeCriteriaQuery.getRoot().get("title")
                        .in(expiredRecipeTitles).not())).getResultList();
    }
}
