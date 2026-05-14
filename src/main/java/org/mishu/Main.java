package org.mishu;

import org.mishu.Business.ClientBLL;
import org.mishu.Business.OrdersBLL;
import org.mishu.Business.ProductBLL;
import org.mishu.Model.Product;

public class Main {
    public static void main(String[] args) {
        ClientBLL clientBLL = new ClientBLL();
        ProductBLL productBLL = new ProductBLL();
        OrdersBLL ordersBLL = new OrdersBLL();

//        Client client = new Client("Alice", "123 Main St", "alasi22222ce2@example.com");
        Product product = new Product("Keyboard", 99.99, 10);

//        int clientId = clientBLL.insertClient(client);
        int productId = productBLL.insertProduct(product);

//        Orders order = new Orders(clientId, productId, 2);
//        int orderId = ordersBLL.insertOrder(order);

//        System.out.println("Inserted clientId=" + clientId);
        System.out.println("Inserted productId=" + productId);
//        System.out.println("Inserted orderId=" + orderId);


        clientBLL.deleteClient(2);

        System.out.println("Deleted clientId=" + 2);

        clientBLL.updateClient(3, "Maricica", "alice@example.com", "1922 drive");

        System.out.println("updated client with id 3");
    }
}
