package by.uniqo.bot.botapi.handlers;

import by.uniqo.bot.bean.Bot;
import by.uniqo.bot.bean.Client;
import by.uniqo.bot.botapi.handlers.fillingOrder.UserProfileData;
import by.uniqo.bot.cache.UserDataCache;
import by.uniqo.bot.service.*;
import by.uniqo.bot.utils.Emojis;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * @author get inspired by Sergei Viacheslaev's video
 */
@Component
@Slf4j
public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;
    private Bot myBot;
    private ReplyMessagesService messagesService;

    @Autowired
    private BotResponsesService botResponsesService;

    @Autowired
    private ClientService clientService;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService,
                          @Lazy Bot myBot, ReplyMessagesService messagesService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
        this.myBot = myBot;
        this.messagesService = messagesService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.ASK_START;
                Client client = Client.builder()
                        .chatId(message.getFrom().getId())
                        .firstName(message.getFrom().getFirstName())
                        .lastName(message.getFrom().getLastName())
                        .userName(message.getFrom().getUserName())
                        .build();
                clientService.saveClient(client);
                break;
            case "Интернет-магазины\n и услуги":
                botState = BotState.INTERNET_MAGAZINE_AND_SERVICES;
                break;
            case "Страхование":
                botState = BotState.INSURANCE;
                break;
            case "Медицина":
                botState = BotState.MEDICINE;
                break;
            case "Телекоммуникации":
                botState = BotState.TELECOMMUNICATIONS;
                break;
            case "Банки и финансы":
                botState = BotState.BANKS_AND_FINANCE;
                break;
            case "Транспорт и туризм":
                botState = BotState.TRANSPORT_AND_TOURISM;
                break;
            case "Мода и красота":
                botState = BotState.FASHION_AND_BEAUTY;
                break;
            case "Еда":
                botState = BotState.FOOD;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }


    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        LocaleMessageService localeMessageService;

        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");


        //From Destiny choose buttons
        if (buttonQuery.getData().equals("buttonInternetMagazineAndServices")) {
            callBackAnswer = new SendMessage(chatId, botResponsesService.getBotResponseByTag("tellAboutMagazineAndServices"));

        } else if (buttonQuery.getData().equals("buttonInsurance")) {
            callBackAnswer = new SendMessage(chatId, botResponsesService.getBotResponseByTag("tellAboutInsurance"));

        } else if (buttonQuery.getData().equals("buttonMedicine")) {
            callBackAnswer = new SendMessage(chatId, botResponsesService.getBotResponseByTag("tellAboutMedicine"));

        } else if (buttonQuery.getData().equals("buttonTelecommunications")) {
            callBackAnswer = new SendMessage(chatId, botResponsesService.getBotResponseByTag("tellAboutTelecommunications"));

        } else if (buttonQuery.getData().equals("buttonFashionAndBeauty")) {
            callBackAnswer = new SendMessage(chatId, botResponsesService.getBotResponseByTag("tellAboutFashionAndBeauty"));

        } else if (buttonQuery.getData().equals("buttonTransportAndTourism")) {
            callBackAnswer = new SendMessage(chatId, botResponsesService.getBotResponseByTag("tellAboutTransportAndTourism"));

        } else if (buttonQuery.getData().equals("buttonBanksAndFinance")) {
            callBackAnswer = new SendMessage(chatId, botResponsesService.getBotResponseByTag("tellAboutBanksAndFinance"));

        } else if (buttonQuery.getData().equals("buttonFood")) {
            callBackAnswer = new SendMessage(chatId, botResponsesService.getBotResponseByTag("tellAboutFood"));

        } else if (buttonQuery.getData().equals("buttonRealEstateAndRetail")) {
            callBackAnswer = new SendMessage(chatId, botResponsesService.getBotResponseByTag("tellAboutRealEstateAndRetail"));

        } else if (buttonQuery.getData().equals("buttonEducation")) {
            callBackAnswer = new SendMessage(chatId, botResponsesService.getBotResponseByTag("tellAboutEducation"));

        }  else if (buttonQuery.getData().equals("buttonStars")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setStars("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonScroll")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setScroll("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonBigBell")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setBigBell("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonLittleBell")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setLittleBell("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonRibbon")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setRibbon("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonBowtie")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setBowtie("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);;

        } else if (buttonQuery.getData().equals("buttonNext")) {
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CREDENTIALS);
            callBackAnswer = new SendMessage(chatId, "Теперь заполним инфо по доставке\n" +
                    "Укажите полное ФИО");
        }



        //From ModelColorText choose buttons
        else if (buttonQuery.getData().equals("goldFoil")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(messagesService.getReplyText("foil.nameOne"));
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-symbol.JPG");
            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.askModelNumber"));
        } else if (buttonQuery.getData().equals("silverFoil")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(messagesService.getReplyText("foil.nameTwo"));
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-symbol.JPG");
            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.askModelNumber"));

        } else if (buttonQuery.getData().equals("redFoil")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(messagesService.getReplyText("foil.nameThree"));
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-symbol.JPG");
            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.askModelNumber"));

        } else if (buttonQuery.getData().equals("blueFoil")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(messagesService.getReplyText("foil.nameFour"));
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-symbol.JPG");
            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.askModelNumber"));

        } else if (buttonQuery.getData().equals("blackFoil")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(messagesService.getReplyText("foil.nameFive"));
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-symbol.JPG");


            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.askModelNumber"));

        } else {
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        }


        return callBackAnswer;


    }


    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }

    @SneakyThrows
    public File getUsersProfile(int userId) {
        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
        File profileFile = ResourceUtils.getFile("classpath:static/docs/Your_order.TXT");

        try (FileWriter fw = new FileWriter(profileFile.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(userProfileData.toString());
        }


        return profileFile;

    }

}