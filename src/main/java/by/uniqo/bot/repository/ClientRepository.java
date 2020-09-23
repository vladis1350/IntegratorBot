package by.uniqo.bot.repository;

import by.uniqo.bot.bean.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findClientByChatId(long chatId);
}
