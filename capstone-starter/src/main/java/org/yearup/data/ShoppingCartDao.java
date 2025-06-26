package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    void create(int userId, int productId);
    ShoppingCart getByUserId(int userId);
    void update(int userId, ShoppingCartItem item);
    ShoppingCart delete(int userId);
}
