package org.mishu.Business;

import org.mishu.Business.Validator.Order.OrderValidator;
import org.mishu.Business.Validator.Validator;
import org.mishu.DataAccess.Wrappers.OrdersDBWrapper;
import org.mishu.Model.Orders;

import java.util.List;
import java.util.NoSuchElementException;

public class OrdersBLL {

    private final Validator<Orders> validator;

    private final OrdersDBWrapper dbWrapper;

    public OrdersBLL() {
        this.validator = new OrderValidator();

        this.dbWrapper = new OrdersDBWrapper(Orders.class);
    }

    public Orders findOrderById(int orderId) {
        Orders o = dbWrapper.findById(orderId);
        if (o == null) {
            throw new NoSuchElementException("Order with id=" + orderId + " does not " +
                    "exist");
        }
        return o;
    }

    public List<Orders> findOrdersByClientId(int clientId) {
        List<Orders> list = dbWrapper.findByClientId(clientId);
        if (list == null || list.isEmpty()) {
            throw new NoSuchElementException("Client with id=" + clientId + " has no " +
                    "orders");
        }
        return list;
    }

    public List<Orders> findOrdersByProductId(int productId) {
        List<Orders> list = dbWrapper.findByProductId(productId);
        if (list == null || list.isEmpty()) {
            throw new NoSuchElementException("Product with id=" + productId + " does " +
                    "not appear in any order");
        }
        return list;
    }

    public int insertOrder(Orders order) {
        validator.validate(order);
        return dbWrapper.insert(order);
    }

    public void deleteOrder(int orderId) {
        dbWrapper.delete(orderId);
    }

    public void updateOrder(int orderId, Integer clientId, Integer productId, Integer quantity) {
        Orders o = dbWrapper.findById(orderId);
        if (o == null) {
            throw new NoSuchElementException("No order with id=" + orderId + " found");
        }
        o.setClientId(clientId == null ? o.getClientId() : clientId);
        o.setProductId(productId == null ? o.getProductId() : productId);
        o.setQuantity(quantity == null ? o.getQuantity() : quantity);

        validator.validate(o);
        dbWrapper.update(o);
    }
}
