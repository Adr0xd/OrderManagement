package org.mishu.Exceptions;

public class ZeroQtyOrderException extends RuntimeException {
    public ZeroQtyOrderException(String message) {
        super(message);
    }
}
