package org.mishu.DataAccess.Wrappers;

import org.mishu.Model.Orders;
import org.mishu.Utils.QuerySettings;

import java.util.List;

public class OrdersDBWrapper extends DBWrapper<Orders> {
    public OrdersDBWrapper(Class<Orders> type) {
        super(type);
    }

    public List<Orders> findByClientId(int clientId) {
        String query = createSelectQuery("clientId");
        return get(clientId, "ClientId", query, new QuerySettings(true, false));
    }

    public List<Orders> findByProductId(int productId) {
        String query = createSelectQuery("productId");
        return get(productId, "ProductId", query, new QuerySettings(true, false));
    }
}
