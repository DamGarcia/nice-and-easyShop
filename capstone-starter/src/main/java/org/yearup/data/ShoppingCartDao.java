package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    void create(int userId, int productId);
    void update(int userId, ShoppingCartItem item);
    ShoppingCart delete(int userId);
}
