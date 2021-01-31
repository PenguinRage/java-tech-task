package com.rezdy.lunch.repository.model;

import com.rezdy.lunch.entity.Recipe;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

public class RecipeCriteriaQuery {
    final private CriteriaQuery<Recipe> criteriaQuery;
    final private Root<Recipe> root;
    final private CriteriaQuery<Recipe> query;
    final private Subquery<Recipe> subquery;
    final private Root<Recipe> subqueryRoot;

    public RecipeCriteriaQuery(final CriteriaBuilder cb) {
        criteriaQuery = cb.createQuery(Recipe.class);
        root = criteriaQuery.from(Recipe.class);
        query = criteriaQuery.select(root);
        subquery = query.subquery(Recipe.class);
        subqueryRoot = subquery.from(Recipe.class);
        subquery.select(subqueryRoot);
    }
    public CriteriaQuery<Recipe> getQuery() {
        return query;
    }

    public Root<Recipe> getRoot() {
        return root;
    }

    public Subquery<Recipe> getSubquery() {
        return subquery;
    }

    public Root<Recipe> getSubqueryRoot() {
        return subqueryRoot;
    }
}