package org.mishu.Business.Validator.Order;

import org.mishu.Business.Validator.Validator;
import org.mishu.Exceptions.ZeroQtyOrderException;
import org.mishu.Model.Orders;

public class OrderValidator implements Validator<Orders> {

	/**
	 *
	 * validates the quantity of an order
	 * 
	 * @param order
	 *            for which quantity to be validated
	 */
	@Override
	public void validate(Orders order) {
		if (order.getQuantity() <= 0) {
			throw new ZeroQtyOrderException("An order cannot have 0 or less products");
		}
	}
}
