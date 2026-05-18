package org.mishu.Business.Validator.Product;

import org.mishu.Business.Validator.Validator;
import org.mishu.Model.Product;

public class PriceValidator implements Validator<Product> {
	/**
	 * validates the price of a product
	 * 
	 * @param product
	 *            for which price to be validated
	 */
	@Override
	public void validate(Product product) {
		if (product.getPrice() < 0) {
			throw new IllegalArgumentException("Price cannot be a negative number!");
		}
	}
}
