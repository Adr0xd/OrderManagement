package org.mishu.Exceptions;

/**
 * <p>
 * duplicate email exception is thrown when the user wants to insert an already
 * existing email or to update an email to another that already exists
 * </p>
 */
public class DuplicateEmailException extends RuntimeException {
	public DuplicateEmailException(String message) {
		super(message);
	}
}
