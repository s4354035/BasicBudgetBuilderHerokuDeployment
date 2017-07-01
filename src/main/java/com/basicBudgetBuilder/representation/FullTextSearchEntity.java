package com.basicBudgetBuilder.representation;

/**
 * Entity interface for search results
 * Created by Hanzi Jing on 6/05/2017.
 */
public interface FullTextSearchEntity {
     FullTextEntityType getType();
     double getScore();
}
