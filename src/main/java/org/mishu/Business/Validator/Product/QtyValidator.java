package org.mishu.Business.Validator.Product;

import org.mishu.Business.Validator.Validator;
import org.mishu.Model.Product;

public class QtyValidator implements Validator<Product> {
    @Override
    public void validate(Product product) {
        if(product.getQuantity() < 0){
            throw new IllegalArgumentException("Quantity cannot be negative!");
        }
    }
}
