package com.example.furnitech.service;

import com.example.furnitech.model.Client;
import com.example.furnitech.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client authenticate(String name, String password) {
        Client client = clientRepository.findByName(name);
        if (client != null && client.getPassword().equals(password)) {
            return client;
        }
        return null;
    }
}