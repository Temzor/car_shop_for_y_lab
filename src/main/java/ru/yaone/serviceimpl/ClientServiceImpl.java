package ru.yaone.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.model.Client;
import ru.yaone.repository.ClientRepository;
import ru.yaone.services.ClientService;


import java.util.List;

@Service
@Loggable("Логирование класса ClientServiceImpl")
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Loggable("Логирование метода ClientServiceImpl.addClient")
    @Override
    public void addClient(Client client) {
        clientRepository.addClient(client);
    }

    @Loggable("Логирование метода ClientServiceImpl.getAllClients")
    @Override
    public List<Client> getAllClients() {
        return clientRepository.getAllClients();
    }

    @Loggable("Логирование метода ClientServiceImpl.getClientById")
    @Override
    public Client getClientById(int id) {
        return clientRepository.getClientById(id);
    }

    @Loggable("Логирование метода ClientServiceImpl.updateClient")
    @Override
    public void updateClient(int id, Client updatedClient) {
        clientRepository.updateClient(id, updatedClient);
    }

    @Loggable("Логирование метода ClientServiceImpl.deleteClientById")
    @Override
    public boolean deleteClientById(int id) {
        return clientRepository.deleteClientById(id);
    }
}