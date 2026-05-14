package org.mishu.DataAccess.Wrappers;

import org.mishu.Model.Order;
import org.mishu.Utils.QuerySettings;

import java.util.List;

public class OrderDBWrapper extends DBWrapper<Order> {
    public OrderDBWrapper(Class<Order> type) {
        super(type);
    }

    public List<Order> findByClientId(int clientId) {
        String query = createSelectQuery("clientId");
        return get(clientId, "ClientId", query, new QuerySettings(true, false));
    }

    public List<Order> findByProductId(int productId) {
        String query = createSelectQuery("productId");
        return get(productId, "ProductId", query, new QuerySettings(true, false));
    }
}
