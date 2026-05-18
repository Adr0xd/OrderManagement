package org.mishu.Business;

import org.mishu.Business.Validator.Client.EmailValidator;
import org.mishu.Business.Validator.Validator;
import org.mishu.DataAccess.Wrappers.ClientDBWrapper;
import org.mishu.Exceptions.DuplicateEmailException;
import org.mishu.Model.Client;
import org.mishu.Model.Product;

import java.util.List;
import java.util.NoSuchElementException;

public class ClientBLL {

	private final Validator<Client> emailValidator;

	private final ClientDBWrapper dbWrapper;

	public ClientBLL() {
		emailValidator = new EmailValidator();

		dbWrapper = new ClientDBWrapper(Client.class);
	}

	/**
	 *
	 * @return list of all clients in the table
	 */
	public List<Client> findAll() {
		return dbWrapper.findAll();
	}

	/**
	 *
	 * @param name
	 *            name of client to be fetched
	 * @return exact client with that name
	 */
	public Client findClientByName(String name) {
		Client c = dbWrapper.findClientByName(name);
		if (c == null) {
			throw new NoSuchElementException("The client with name=" + name + "does " + "not" + " exist");
		}
		return c;
	}

	/**
	 *
	 * @param client
	 *            to be inserted into the table
	 * @return integer representing the id of the newly inserted client
	 */
	public int insertClient(Client client) {
		emailValidator.validate(client);
		if (dbWrapper.findByEmail(client.getEmail()) != null) {
			throw new DuplicateEmailException("email already in use");
		}
		return dbWrapper.insert(client);
	}

	/**
	 *
	 * @param clientId
	 *            to be deleted
	 */
	public void deleteClient(int clientId) {
		Client c = dbWrapper.findById(clientId);
		if (c == null) {
			throw new NoSuchElementException("Client with id=" + clientId + " does " +
                    "not " + "exist");
		}
		dbWrapper.delete(clientId);
	}

	/**
	 *
	 * @param clientId
	 *            the id of the client to be updated
	 * @param name
	 *            new name of client, if null keep last
	 * @param email
	 *            new email of client, if null keep last
	 * @param address
	 *            new address of client, if null keep last
	 */
	public void updateClient(int clientId, String name, String email, String address) {
		Client c = dbWrapper.findById(clientId);

		if (c == null) {
			throw new NoSuchElementException("No client with id=" + clientId + " to update");
		}

		c.setName(name == null ? c.getName() : name);
		c.setEmail(email == null ? c.getEmail() : email);
		c.setAddress(address == null ? c.getAddress() : address);

		emailValidator.validate(c);

		Client existing = dbWrapper.findByEmail(c.getEmail());

		if (existing != null && existing.getId() != clientId) {
			throw new DuplicateEmailException("email already in use");
		}

		dbWrapper.update(c);
	}

}
