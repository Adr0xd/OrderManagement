package org.mishu.Business.Validator.Product;

import org.mishu.Business.Validator.Validator;
import org.mishu.Model.Product;

public class PriceValidator implements Validator<Product> {
    @Override
    public void validate(Product product) {
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be a negative number!");
        }
    }
}
