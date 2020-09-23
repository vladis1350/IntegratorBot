package by.uniqo.bot.service;

import by.uniqo.bot.bean.Client;
import by.uniqo.bot.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService  {

    @Autowired
    private ClientRepository clientRepository;

    public Client saveClient(Client client) {
        if (client.equals(findClientByChatId(client.getChatId()))) {
            return client;
        }
        return clientRepository.save(client);
    }

    public Client findClientByChatId(long chatId) {
        return clientRepository.findClientByChatId(chatId);
    }
}
