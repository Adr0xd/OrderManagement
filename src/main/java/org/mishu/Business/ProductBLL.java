package org.mishu.Business;

import org.mishu.Business.Validator.Product.PriceValidator;
import org.mishu.Business.Validator.Product.QtyValidator;
import org.mishu.Business.Validator.Validator;
import org.mishu.DataAccess.Wrappers.ProductDBWrapper;
import org.mishu.Model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ProductBLL {

    private final List<Validator<Product>> validators;

    private final ProductDBWrapper dbWrapper;

    public ProductBLL() {
        this.validators = new ArrayList<>();
        validators.add(new PriceValidator());
        validators.add(new QtyValidator());

        this.dbWrapper = new ProductDBWrapper(Product.class);
    }

    public Product findProductById(int productId) {
        Product p = dbWrapper.findById(productId);
        if (p == null) {
            throw new NoSuchElementException("Product with id=" + productId + " does " +
                    "not exist");
        }
        return p;
    }

    public Product findProductByName(String name) {
        Product p = dbWrapper.findProductByName(name);
        if (p == null) {
            throw new NoSuchElementException("Product with name=" + name + " " +
                    "does " +
                    "not exist");
        }
        return p;
    }

    public List<Product> findProductsByName(String name) {
        List<Product> p = dbWrapper.findProductsByName(name);
        if (p == null || p.isEmpty()) {
            throw new NoSuchElementException("No products with this name=" + name +
                    " exist");
        }
        return p;
    }

    public List<Product> findProductsByPrice(double price) {
        List<Product> p = dbWrapper.findProductsByPrice(price);
        if (p == null || p.isEmpty()) {
            throw new NoSuchElementException("No products for this price=" + price +
                    " exist");
        }
        return p;
    }

    public List<Product> findProductsByQuantity(int quantity) {
        List<Product> p = dbWrapper.findProductsByQty(quantity);
        if (p == null || p.isEmpty()) {
            throw new NoSuchElementException("No products with this quantity=" + quantity +
                    " exist");
        }
        return p;
    }

    public int insertProduct(Product product) {
        for (Validator<Product> v : validators) {
            v.validate(product);
        }
        return dbWrapper.insert(product);
    }

    public void deleteProduct(int productId) {
        dbWrapper.delete(productId);
    }

    public void updateProduct(int productId, String name, Double price,
                              Integer quantity) {
        Product p = dbWrapper.findById(productId);
        if (p == null) {
            throw new NoSuchElementException("Product with id=" + productId + " does " +
                    "not exist");
        }

        p.setName(name == null ? p.getName() : name);
        p.setPrice(price == null ? p.getPrice() : price);
        p.setQuantity(quantity == null ? p.getQuantity() : quantity);

        for (Validator<Product> v : validators) {
            v.validate(p);
        }

        dbWrapper.update(p);

    }
}
