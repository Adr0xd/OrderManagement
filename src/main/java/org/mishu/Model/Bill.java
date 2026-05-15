package org.mishu.Model;

public record Bill(int id, int orderId, int clientId,
                   int productId, int quantity, double total) {}
