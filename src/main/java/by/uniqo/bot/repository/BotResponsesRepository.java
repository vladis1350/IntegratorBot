package by.uniqo.bot.repository;

import by.uniqo.bot.bean.BotResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotResponsesRepository extends JpaRepository<BotResponse, Long> {
    BotResponse findBotResponsesByTag(String tag);
}
