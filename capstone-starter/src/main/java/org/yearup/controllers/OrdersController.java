package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.*;
import org.yearup.models.*;

import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("orders")
@PreAuthorize("hasRole('ROLE_USER')")
@CrossOrigin
public class OrdersController {
    
    private UserDao userDao;
    private ProfileDao profileDao;
    private OrderDao orderDao;
    private ShoppingCartDao shoppingCartDao;
    private OrderLineItemDao orderLineItemDao;

    @Autowired
    public OrdersController(UserDao userDao, ProfileDao profileDao, OrderDao orderDao, ShoppingCartDao shoppingCartDao, OrderLineItemDao orderLineItemDao) {
        this.userDao = userDao;
        this.profileDao = profileDao;
        this.orderDao = orderDao;
        this.shoppingCartDao = shoppingCartDao;
        this.orderLineItemDao = orderLineItemDao;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public Order createOrder(Principal principal){
        // get user id
        User user = userDao.getByUserName(principal.getName());
        int userId = user.getId();
        
        // create order object with correct values
        Order order = new Order();
        order.setUserId(userId);
        Date date = new Date(System.currentTimeMillis());
        order.setDate(date);
        order.setAddress(profileDao.getByUserId(userId).getAddress());
        order.setCity(profileDao.getByUserId(userId).getCity());
        order.setState(profileDao.getByUserId(userId).getState());
        order.setZip(profileDao.getByUserId(userId).getZip());
        order.setShippingAmount(shoppingCartDao.getByUserId(userId).getTotal());
        orderDao.create(order);
        
        // use the ordersDao to create the order object to get order id
        int orderId = order.getOrderId();
        
        // get shopping cart from user
        ShoppingCart cart = shoppingCartDao.getByUserId(userId);
        List<OrderLineItem> lineItems = new ArrayList<>();
        
        // loop through shopping cart
        Map<Integer, ShoppingCartItem> items = cart.getItems();
        // each item will create an order line item (using order id and product id)
        items.values().forEach(shoppingCartItem -> {
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setOrderId(orderId);
            orderLineItem.setProductId(shoppingCartItem.getProductId());
            orderLineItem.setSalesPrice(shoppingCartItem.getLineTotal());
            orderLineItem.setQuantity(shoppingCartItem.getQuantity());
            orderLineItem.setDiscount(shoppingCartItem.getDiscountPercent());
            
            
        // send order line item to orderlineDao to record in db
        orderLineItemDao.create(orderLineItem);
        lineItems.add(orderLineItem);
        });
        
        order.setLineItems(lineItems);
        
        // when looping is done, clear cart
        shoppingCartDao.delete(userId);
        
        return order;
    }
}
