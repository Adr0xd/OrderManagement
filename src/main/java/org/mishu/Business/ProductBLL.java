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

	/**
	 *
	 * @param productId
	 *            th id of the product to be searched
	 * @return the product that should be fetched by id
	 */
	public Product findProductById(int productId) {
		Product p = dbWrapper.findById(productId);
		if (p == null) {
			throw new NoSuchElementException("Product with id=" + productId + " does " + "not exist");
		}
		return p;
	}

	/**
	 *
	 * @param name
	 *            the name of the product to be searched
	 * @return Product the product with that name
	 */
	public Product findProductByName(String name) {
		Product p = dbWrapper.findProductByName(name);
		if (p == null) {
			throw new NoSuchElementException("Product with name=" + name + " " + "does " + "not exist");
		}
		return p;
	}

	/**
	 *
	 * @param name
	 *            name that can be a substring of the products name
	 * @return List of products with that name as a substring of their actual name
	 */
	public List<Product> findProductsByName(String name) {
		List<Product> p = dbWrapper.findProductsByName(name);
		if (p == null || p.isEmpty()) {
			throw new NoSuchElementException("No products with this name=" + name + " exist");
		}
		return p;
	}

	/**
	 *
	 * @param product
	 *            product to be inserted in the table
	 */
	public void insertProduct(Product product) {
		for (Validator<Product> v : validators) {
			v.validate(product);
		}
		dbWrapper.insert(product);
	}

	/**
	 *
	 * @return list of all products in the table
	 */
	public List<Product> findAll() {
		return dbWrapper.findAll();
	}

	/**
	 *
	 * @param productId
	 *            product id to be deleted
	 */
	public void deleteProduct(int productId) {
		Product p = dbWrapper.findById(productId);
		if (p == null) {
			throw new NoSuchElementException("Product with id=" + productId + " does not exist");
		}
		dbWrapper.delete(productId);
	}

	/**
	 *
	 * @param productId
	 *            the product id that should be updated
	 * @param name
	 *            new name of the product, if null then keep last name
	 * @param price
	 *            new price of the product, if null keep the last price
	 * @param quantity
	 *            new quantity of the product, if null keep last quantity
	 */
	public void updateProduct(int productId, String name, Double price, Integer quantity) {
		Product p = dbWrapper.findById(productId);
		if (p == null) {
			throw new NoSuchElementException("Product with id=" + productId + " does " + "not exist");
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
