package org.mishu.Business.Validator;

/**
 *
 * <p>
 * generic validator interface
 * </p>
 *
 */
public interface Validator<T> {

	public void validate(T t);
}
