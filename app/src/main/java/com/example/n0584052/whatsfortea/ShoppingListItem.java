package com.example.n0584052.whatsfortea;

import java.io.Serializable;

/**
 * Created by joshh on 22/03/2018.
 */

public class ShoppingListItem implements Serializable {

    String shoppingListName;
    String shoppingListQuantity;

    public String getShoppingListName(){
        return shoppingListName;
    }

    public String getShoppingListQuantity(){
        return shoppingListQuantity;
    }

    public void setShoppingListName(String shoppingListName) {
        this.shoppingListName = shoppingListName;
    }

    public void setShoppingListQuantity(String shoppingListQuantity) {
        this.shoppingListQuantity = shoppingListQuantity;
    }
}
