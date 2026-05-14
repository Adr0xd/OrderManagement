package org.mishu.DataAccess.Wrappers;

import org.mishu.Model.Client;
import org.mishu.Utils.QuerySettings;

import java.util.List;

public class ClientDBWrapper extends DBWrapper<Client> {

    public ClientDBWrapper(Class<Client> type) {
        super(type);
    }

    public Client findByEmail(String email) {
        // find with exact email
        String query = createSelectQuery("email");
        List<Client> clients = get(
                email,
                "Email",
                query,
                new QuerySettings(false, false));
        return clients == null ? null : clients.getFirst();
    }

    public Client findClientByName(String name) {
//        find with exact name
        String query = createSelectQuery("name");
        List<Client> clients = get(name,
                "Name",
                query,
                new QuerySettings(false, false));
        return clients == null ? null : clients.getFirst();
    }

    public List<Client> findClientsByName(String name) {
        // finds clients with that name
        String query = createSelectLikeQuery("name");
        return get(name,
                "Name",
                query,
                new QuerySettings(true, true));
    }
}
