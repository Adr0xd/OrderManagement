package org.mishu.Exceptions;

/**
 * <p>
 * zero quantity exception thrown when the quantity for a product is zero or
 * less during creation
 * </p>
 */
public class ZeroQtyOrderException extends RuntimeException {
	public ZeroQtyOrderException(String message) {
		super(message);
	}
}
