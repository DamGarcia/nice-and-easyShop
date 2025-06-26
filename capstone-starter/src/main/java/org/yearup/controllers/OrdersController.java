package org.yearup.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.data.OrderDao;

@RestController
@RequestMapping("order")
@PreAuthorize("hasRole('ROLE_USER')")
@CrossOrigin
public class OrdersController {
    
    private OrderDao orderDao;
}
