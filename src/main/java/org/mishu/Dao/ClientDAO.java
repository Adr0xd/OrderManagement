package org.mishu.Dao;

import org.mishu.Model.Client;

import java.util.List;

public class ClientDAO extends AbstractDAO<Client> {

    public ClientDAO(Class<Client> type) {
        super(type);
    }

    public Client findByEmail(String email) {
        // find with exact email
        String query = createSelectQuery("email");
        List<Client> clients = get(email, "Email", query, false, false);
        return clients == null ? null : clients.getFirst();
    }

    public Client findClientByName(String name) {
//        find with exact name
        String query = createSelectQuery("name");
        List<Client> clients = get(name, "Name", query, false, false);
        return clients == null ? null : clients.getFirst();
    }

    public List<Client> findClientsByName(String name) {
        // finds clients with that name
        String query = createSelectLikeQuery("name");
        return get(name, "Name", query, true, true);
    }
}
