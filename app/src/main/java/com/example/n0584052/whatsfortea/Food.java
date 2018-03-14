package com.example.n0584052.whatsfortea;

import java.util.ArrayList;

/**
 * Created by N0584052 on 14/03/2018.
 */

public class Food {
    private String foodItemName;
    private ArrayList<String> ingredientNames = new ArrayList<String>();
    private ArrayList<String> ingredientQuantities = new ArrayList<String>();
    private String vegetarian;

    public ArrayList<String> getIngredientNames(){
        return ingredientNames;
    }

    public ArrayList<String> getIngredientQuantities(){
        return ingredientQuantities;
    }

    public String getVegetarian(){
        return vegetarian;
    }

    public String getFoodItemName(){
        return foodItemName;
    }

    public void addIngredientName(String theIngredientName){
        this.ingredientNames.add(theIngredientName);
    }

    public void addIngredientQuantity(String theIngredientQuantity){
        this.ingredientQuantities.add(theIngredientQuantity);
    }

    public void setIngredientNames(ArrayList<String> theIngredientNames){
        this.ingredientNames = theIngredientNames;
    }

    public void setIngredientQuantities(ArrayList<String> theIngredientQuantities){
        this.ingredientQuantities = theIngredientQuantities;
    }

    public void setVegetarian(String theVegetarian) {
        this.vegetarian = theVegetarian;
    }

    public void setFoodItemName(String theFoodItemName) {
        this.foodItemName = theFoodItemName;
    }
}
