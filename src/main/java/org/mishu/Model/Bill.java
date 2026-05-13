package org.mishu.Model;

public record Bill(int id, int clientId, int productId,
                   int quantity, double total) {
}
