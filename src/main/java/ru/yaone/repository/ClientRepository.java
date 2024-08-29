package ru.yaone.repository;

import ru.yaone.model.Client;

import java.util.List;

public interface ClientRepository {
    void addClient(Client client);

    List<Client> getAllClients();

    Client getClientById(int id);

    boolean deleteClientById(int id);

    void updateClient(int id, Client updatedClient);
}