package org.mishu.Business.Validator.Product;

import org.mishu.Business.Validator.Validator;
import org.mishu.Model.Product;

public class QtyValidator implements Validator<Product> {

	/**
	 * validates the quantityof a product
	 * 
	 * @param product
	 *            for which quantity to be validated
	 */
	@Override
	public void validate(Product product) {
		if (product.getQuantity() < 0) {
			throw new IllegalArgumentException("Quantity cannot be negative!");
		}
	}
}
