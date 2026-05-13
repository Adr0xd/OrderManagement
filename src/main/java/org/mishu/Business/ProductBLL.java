package org.mishu.Business;

import org.mishu.Business.Validator.Product.PriceValidator;
import org.mishu.Business.Validator.Product.QtyValidator;
import org.mishu.Business.Validator.Validator;
import org.mishu.Dao.ProductDAO;
import org.mishu.Model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ProductBLL {

    private final List<Validator<Product>> validators;

    private final ProductDAO dao;

    public ProductBLL() {
        this.validators = new ArrayList<>();
        validators.add(new PriceValidator());
        validators.add(new QtyValidator());

        this.dao = new ProductDAO(Product.class);
    }

    public Product findProductById(int productId) {
        Product p = dao.findById(productId);
        if (p == null) {
            throw new NoSuchElementException("Product with id=" + productId + " does " +
                    "not exist");
        }
        return p;
    }

    public Product findProductByName(String name) {
        Product p = dao.findProductByName(name);
        if (p == null) {
            throw new NoSuchElementException("Product with name=" + name + " " +
                    "does " +
                    "not exist");
        }
        return p;
    }

    public List<Product> findProductsByName(String name) {
        List<Product> p = dao.findProductsByName(name);
        if (p == null) {
            throw new NoSuchElementException("No products with this name=" + name +
                    " exist");
        }
        return p;
    }

    public List<Product> findProductsByPrice(double price) {
        List<Product> p = dao.findProductsByPrice(price);
        if (p == null) {
            throw new NoSuchElementException("No products for this price=" + price +
                    " exist");
        }
        return p;
    }

    public List<Product> findProductsByQuantity(int quantity) {
        List<Product> p = dao.findProductsByQty(quantity);
        if (p == null) {
            throw new NoSuchElementException("No products with this quantity=" + quantity +
                    " exist");
        }
        return p;
    }

    public int insertProduct(Product product) {
        for (Validator<Product> v : validators) {
            v.validate(product);
        }
        return dao.insert(product);
    }
}
