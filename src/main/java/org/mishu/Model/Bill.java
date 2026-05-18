package org.mishu.Model;

/**
 * bill object
 * 
 * @param id
 *            bill id
 * @param orderId
 *            one-to-one relationship with an order
 * @param clientId
 *            the client for which the bill is issued
 * @param productId
 *            the product that the client bought
 * @param quantity
 *            how many products of that type he bought
 * @param total
 *            the total amount (price * quantity)
 */

public record Bill(int id, int orderId, int clientId, int productId, int quantity, double total) {
}
