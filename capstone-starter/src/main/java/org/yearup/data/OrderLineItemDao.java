package org.yearup.data;

import org.yearup.models.OrderLineItem;

public interface OrderLineItemDao {
    void create(OrderLineItem orderLineItem);
    OrderLineItem getByOrderId(int orderId);
    void update(OrderLineItem orderLineItem);
    OrderLineItem delete(int orderId);
}
