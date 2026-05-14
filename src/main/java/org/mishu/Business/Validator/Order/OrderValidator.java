package org.mishu.Business.Validator.Order;

import org.mishu.Business.Validator.Validator;
import org.mishu.Exceptions.ZeroQtyOrderException;
import org.mishu.Model.Orders;

public class OrderValidator implements Validator<Orders> {
    @Override
    public void validate(Orders order) {
        if (order.getQuantity() <= 0) {
            throw new ZeroQtyOrderException("An order cannot have 0 or less products");
        }
    }
}
