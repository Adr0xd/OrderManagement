package org.mishu.DataAccess.Wrappers;

import org.mishu.Model.Product;
import org.mishu.Utils.QuerySettings;

import java.util.List;

public class ProductDBWrapper extends DBWrapper<Product> {

	public ProductDBWrapper(Class<Product> type) {
		super(type);
	}

	/**
	 *
	 * finds a product by its name
	 *
	 * @param name
	 *            to name to be searched in the db
	 * @return Product that matches the name exactly
	 */
	public Product findProductByName(String name) {
		// finds product with exact name
		String query = createSelectQuery("name");
		List<Product> prods = get(name, "Name", query, new QuerySettings(false, false));
		return prods == null ? null : prods.getFirst();
	}

	/**
	 *
	 * finds all products with that name
	 *
	 * @param name
	 *            search all products that contain the string name
	 * @return List of Product that have name in their name
	 */
	public List<Product> findProductsByName(String name) {
		String query = createSelectLikeQuery("name");
		return get(name, "Name", query, new QuerySettings(true, true));
	}

	/**
	 *
	 * finds a product by its exact price
	 *
	 * @param price
	 *            search a product by its price
	 * @return List of Product that have that price
	 */
	public List<Product> findProductsByPrice(double price) {
		String query = createSelectQuery("price");
		return get(price, "Price", query, new QuerySettings(true, false));
	}

	/**
	 *
	 * finds a product based on quantity
	 *
	 * @param qty
	 *            search a product by quantity
	 * @return List of Product that have that quantity
	 *
	 */
	public List<Product> findProductsByQty(int qty) {
		String query = createSelectQuery("quantity");
		return get(qty, "Quantity", query, new QuerySettings(true, false));
	}
}
