package org.mishu.DataAccess.Wrappers;

import org.mishu.Model.Product;
import org.mishu.Utils.QuerySettings;

import java.util.List;

public class ProductDBWrapper extends DBWrapper<Product> {

    public ProductDBWrapper(Class<Product> type) {
        super(type);
    }

    public Product findProductByName(String name) {
        // finds product with exact name
        String query = createSelectQuery("name");
        List<Product> prods = get(name, "Name", query, new QuerySettings(false, false));
        return prods == null ? null : prods.getFirst();
    }

    public List<Product> findProductsByName(String name) {
        // finds product with name in their name
        String query = createSelectLikeQuery("name");
        return get(name, "Name", query, new QuerySettings(true, true));
    }

    public List<Product> findProductsByPrice(double price) {
        // find products with that price
        String query = createSelectQuery("price");
        return get(price, "Price", query, new QuerySettings(true, false));
    }

    public List<Product> findProductsByQty(int qty) {
        // find products with that quantity
        String query = createSelectQuery("quantity");
        return get(qty, "Quantity", query, new QuerySettings(true, false));
    }
}
