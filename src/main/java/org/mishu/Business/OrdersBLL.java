package org.mishu.Business;

import org.mishu.Business.Validator.Order.OrderValidator;
import org.mishu.Business.Validator.Validator;
import org.mishu.DataAccess.Wrappers.ClientDBWrapper;
import org.mishu.DataAccess.Wrappers.OrdersDBWrapper;
import org.mishu.DataAccess.Wrappers.ProductDBWrapper;
import org.mishu.Model.Client;
import org.mishu.Model.Orders;
import org.mishu.Model.Product;

import java.util.List;
import java.util.NoSuchElementException;

public class OrdersBLL {

    private final Validator<Orders> validator;

    private final OrdersDBWrapper dbWrapper;
    private final ProductDBWrapper productDBWrapper;
    private final ClientDBWrapper clientDBWrapper;

    public OrdersBLL() {
        this.validator = new OrderValidator();

        this.dbWrapper = new OrdersDBWrapper(Orders.class);
        this.clientDBWrapper = new ClientDBWrapper(Client.class);
        this.productDBWrapper = new ProductDBWrapper(Product.class);
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

        Product p = productDBWrapper.findById(order.getProductId());
        if (p == null) {
            throw new NoSuchElementException("Product with id=" + order.getProductId() +
                    " does not exist");
        }

        Client c = clientDBWrapper.findById(order.getClientId());
        if (c == null) {
            throw new NoSuchElementException("Client with id=" + order.getClientId() +
                    " does not exist");
        }

        if (p.getQuantity() < order.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock for product id=" +
                    order.getProductId());
        }

        p.setQuantity(p.getQuantity() - order.getQuantity());
        productDBWrapper.update(p);

        return dbWrapper.insert(order);
    }

    public void deleteOrder(int orderId) {
        Orders o = dbWrapper.findById(orderId);
        if (o == null) {
            throw new NoSuchElementException("Order with id=" + orderId + " does not exist");
        }
        dbWrapper.delete(orderId);
    }

    public void updateOrder(int orderId, Integer clientId, Integer productId, Integer quantity) {
        Orders o = dbWrapper.findById(orderId);
        if (o == null) {
            throw new NoSuchElementException("No order with id=" + orderId + " found");
        }

        int newProductId = productId == null ? o.getProductId() : productId;
        int newClientId = clientId == null ? o.getClientId() : clientId;
        int newQuantity = quantity == null ? o.getQuantity() : quantity;

        Product p = productDBWrapper.findById(newProductId);
        if (p == null) {
            throw new NoSuchElementException("Product with id=" + newProductId +
                    " does not exist");
        }

        Client c = clientDBWrapper.findById(newClientId);
        if (c == null) {
            throw new NoSuchElementException("Client with id=" + newClientId +
                    " does not exist");
        }

        o.setClientId(newClientId);
        o.setProductId(newProductId);
        o.setQuantity(newQuantity);

        validator.validate(o);
        dbWrapper.update(o);
    }
}
