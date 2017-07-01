package com.basicBudgetBuilder.representation;

/**
 * Immutable class representing Category's name and color for statistics
 * Created by Hanzi Jing on 8/04/2017.
 */
public class CategoryRep {
    private String name;
    private String colour;
    private long id;
    public CategoryRep(long id, String name, String color){
        this.name = name;
        this.colour = color;
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public String getColour(){
        return colour;
    }
    public long getId(){
        return id;
    }
    @Override
    public boolean equals(Object a) {
        if (a instanceof CategoryRep) {
            return name.equals(((CategoryRep) a).getName());
        }
        return false;
    }
    @Override
    public int hashCode(){
        return name.hashCode() * 37 ;
    }
}

