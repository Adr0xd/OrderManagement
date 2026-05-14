package org.mishu.Business;

import org.mishu.Exceptions.DuplicateEmailException;
import org.mishu.Business.Validator.Client.EmailValidator;
import org.mishu.Business.Validator.Validator;
import org.mishu.DataAccess.Wrappers.ClientDBWrapper;
import org.mishu.Model.Client;

import java.util.List;
import java.util.NoSuchElementException;

public class ClientBLL {

    private final Validator<Client> emailValidator;

    private final ClientDBWrapper dbWrapper;

    public ClientBLL() {
        emailValidator = new EmailValidator();

        dbWrapper = new ClientDBWrapper(Client.class);
    }

    public Client findClientById(int clientId) {
        Client c = dbWrapper.findById(clientId);
        if (c == null) {
            throw new NoSuchElementException("The client with id=" + clientId + " does " +
                    "not exist");
        }
        return c;
    }

    public Client findClientByEmail(String email) {
        Client c = dbWrapper.findByEmail(email);
        if (c == null) {
            throw new NoSuchElementException("The client with email=" + email + "does not" +
                    " exist");
        }
        return c;
    }

    public Client findClientByName(String name) {
        Client c = dbWrapper.findClientByName(name);
        if (c == null) {
            throw new NoSuchElementException("The client with name=" + name + "does " +
                    "not" +
                    " exist");
        }
        return c;
    }

    public List<Client> findClientsByName(String name) {
        List<Client> clients = dbWrapper.findClientsByName(name);
        if(clients == null){
            throw new NoSuchElementException("No clients with name=" + name + "exist");
        }
        return clients;
    }


    public int insertClient(Client client) {
        emailValidator.validate(client);
        if (dbWrapper.findByEmail(client.getEmail()) != null) {
            throw new DuplicateEmailException("email already in use");
        }
        return dbWrapper.insert(client);
    }

}
