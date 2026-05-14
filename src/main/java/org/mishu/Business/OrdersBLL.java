package org.mishu.Business;

import org.mishu.Business.Validator.Validator;
import org.mishu.DataAccess.Wrappers.OrdersDBWrapper;
import org.mishu.Model.Orders;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class OrdersBLL {

    private final List<Validator<Orders>> validators;

    private final OrdersDBWrapper dbWrapper;

    public OrdersBLL() {
        this.validators = new ArrayList<>();

        this.dbWrapper = new OrdersDBWrapper(Orders.class);
    }

    public Orders findOrderById(int orderId) {
        Orders o = dbWrapper.findById(orderId);
        if(o== null){
            throw new NoSuchElementException("Order with id=" + orderId + " does not " +
                    "exist");
        }
        return o;
    }

    public List<Orders> findOrdersByClientId(int clientId) {
        List<Orders> list = dbWrapper.findByClientId(clientId);
        if (list == null) {
            throw new NoSuchElementException("Client with id=" + clientId + " has no " +
                    "orders");
        }
        return list;
    }

    public List<Orders> findOrdersByProductId(int productId) {
        List<Orders> list = dbWrapper.findByProductId(productId);
        if (list == null) {
            throw new NoSuchElementException("Product with id=" + productId + " does " +
                    "not appear in any order");
        }
        return list;
    }

}
