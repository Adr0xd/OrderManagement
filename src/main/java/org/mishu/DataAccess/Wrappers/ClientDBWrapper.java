package org.mishu.DataAccess.Wrappers;

import org.mishu.Model.Client;
import org.mishu.Utils.QuerySettings;

import java.util.List;

public class ClientDBWrapper extends DBWrapper<Client> {

	public ClientDBWrapper(Class<Client> type) {
		super(type);
	}

	/**
	 *
	 * finds a client by its email
	 *
	 * @param email
	 *            the email of the client to be searched
	 * @return Client -> the client with that unique email
	 */
	public Client findByEmail(String email) {
		// find with exact email
		String query = createSelectQuery("email");
		List<Client> clients = get(email, "Email", query, new QuerySettings(false, false));
		return clients == null ? null : clients.getFirst();
	}

	/**
	 *
	 * finds a client by its exact name
	 *
	 * @param name
	 *            the exact name of the client to be fetched
	 * @return Client -> the client with that exact name
	 */
	public Client findClientByName(String name) {
		// find with exact name
		String query = createSelectQuery("name");
		List<Client> clients = get(name, "Name", query, new QuerySettings(false, false));
		return clients == null ? null : clients.getFirst();
	}

	/**
	 *
	 * finds all clients with that name
	 *
	 * @param name
	 *            the name of the client to be searched, can be a substring of the
	 *            actual name
	 * @return List of clients that have that name somewhere in their name
	 */
	public List<Client> findClientsByName(String name) {
		// finds clients with that name
		String query = createSelectLikeQuery("name");
		return get(name, "Name", query, new QuerySettings(true, true));
	}
}
