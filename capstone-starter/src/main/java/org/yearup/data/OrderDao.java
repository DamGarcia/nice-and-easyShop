package org.yearup.data;

import org.yearup.models.Order;

public interface OrderDao {
    Order getByUserId(int userId);
    void create(Order order);
    void update(int userId, Order order);
    Order delete(int userId);
}
