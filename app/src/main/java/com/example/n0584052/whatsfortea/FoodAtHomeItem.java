package com.example.n0584052.whatsfortea;

import java.io.Serializable;

/**
 * Created by joshh on 22/03/2018.
 */

public class FoodAtHomeItem implements Serializable {
    private String foodAtHomeName;
    private String foodAtHomeQuantity;

    public void setFoodAtHomeName(String foodAtHomeName) {
        this.foodAtHomeName = foodAtHomeName;
    }

    public void setFoodAtHomeQuantity(String foodAtHomeQuantity) {
        this.foodAtHomeQuantity = foodAtHomeQuantity;
    }

    public String getFoodAtHomeName(){
        return foodAtHomeName;
    }

    public String getFoodAtHomeQuantity(){
        return foodAtHomeQuantity;
    }
}
