package org.mishu.Dao;

import org.mishu.Model.Product;

import java.util.List;

public class ProductDAO extends AbstractDAO<Product> {

    public ProductDAO(Class<Product> type) {
        super(type);
    }

    public Product findProductByName(String name) {
        // finds product with exact name
        String query = createSelectQuery("name");
        List<Product> prods = get(name, "Name", query, false, false);
        return prods == null ? null : prods.getFirst();
    }

    public List<Product> findProductsByName(String name) {
        // finds product with name in their name
        String query = createSelectLikeQuery("name");
        return get(name, "Name", query, true, true);
    }

    public List<Product> findProductsByPrice(double price) {
        // find products with that price
        String query = createSelectQuery("price");
        return get(price, "Price", query, true, false);
    }

    public List<Product> findProductsByQty(int qty) {
        // find products with that quantity
        String query = createSelectQuery("quantity");
        return get(qty, "Quantity", query, true, false);
    }


}
