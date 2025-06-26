package org.yearup.data;

import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;

public interface OrderDao {
    Order getByUserId(int userId);
    void create(Profile profile, ShoppingCart shoppingCart);
    void update(int userId, Order order);
    Order delete(int userId);
}
