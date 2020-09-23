package by.uniqo.bot.service;

import by.uniqo.bot.bean.BotResponse;
import by.uniqo.bot.repository.BotResponsesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotResponsesService {

    @Autowired
    private BotResponsesRepository botResponsesRepository;

    public String getBotResponseByTag(String tag) {
        return botResponsesRepository.findBotResponsesByTag(tag).getBotMessage();
    }

}
