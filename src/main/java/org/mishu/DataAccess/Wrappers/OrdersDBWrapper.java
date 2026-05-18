package org.mishu.DataAccess.Wrappers;

import org.mishu.Model.Orders;
import org.mishu.Utils.QuerySettings;

import java.util.List;

public class OrdersDBWrapper extends DBWrapper<Orders> {
	public OrdersDBWrapper(Class<Orders> type) {
		super(type);
	}

	/**
	 *
	 * finds an order by the client id
	 *
	 * @param clientId
	 *            clientId to be searched
	 * @return returns the orders of the client with that id
	 */
	public List<Orders> findByClientId(int clientId) {
		String query = createSelectQuery("clientId");
		return get(clientId, "ClientId", query, new QuerySettings(true, false));
	}

	/**
	 *
	 * finds an order by the product id
	 *
	 * @param productId
	 *            productId to be searched
	 * @return returns the orders of the product with that id
	 */
	public List<Orders> findByProductId(int productId) {
		String query = createSelectQuery("productId");
		return get(productId, "ProductId", query, new QuerySettings(true, false));
	}
}
