package by.uniqo.bot.botapi.handlers.startOrder;

import by.uniqo.bot.botapi.handlers.BotState;
import by.uniqo.bot.botapi.handlers.InputMessageHandler;
import by.uniqo.bot.service.ReplyMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Спрашивает пользователя- хочет ли он сделать заказ. Меню первого уровня
 */

@Slf4j
@Component
public class StartOrderHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;

    public StartOrderHandler(ReplyMessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.ASK_START;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();

        SendMessage replyToUser = messagesService.getReplyMessage(chatId, "reply.askStart");
        replyToUser.setReplyMarkup(getInlineMessageButtons());

        return replyToUser;
    }

    private InlineKeyboardMarkup getInlineMessageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonInternetMagazineAndServices = new InlineKeyboardButton().setText("Интернет-магазины\n и сервисы");
        InlineKeyboardButton buttonInsurance = new InlineKeyboardButton().setText("Страхование");
        InlineKeyboardButton buttonMedicine = new InlineKeyboardButton().setText("Медицина");
        InlineKeyboardButton buttonTelecommunications = new InlineKeyboardButton().setText("Телекоммуникации");
        InlineKeyboardButton buttonBanksAndFinance = new InlineKeyboardButton().setText("Банки и финансы");
        InlineKeyboardButton buttonTransportAndTourism = new InlineKeyboardButton().setText("Транспорт и туризм");
        InlineKeyboardButton buttonFashionAndBeauty = new InlineKeyboardButton().setText("Мода и красота");
        InlineKeyboardButton buttonFood = new InlineKeyboardButton().setText("Еда");
        InlineKeyboardButton buttonRealEstateAndRetail = new InlineKeyboardButton().setText("Недвижимость и Ритейл");
        InlineKeyboardButton buttonEducation = new InlineKeyboardButton().setText("Образование");

        //Every button must have callBackData, or else not work !
        buttonInternetMagazineAndServices.setCallbackData("buttonInternetMagazineAndServices");

        buttonInsurance.setCallbackData("buttonInsurance");
        buttonMedicine.setCallbackData("buttonMedicine");
        buttonTelecommunications.setCallbackData("buttonTelecommunications");
        buttonBanksAndFinance.setCallbackData("buttonBanksAndFinance");
        buttonTransportAndTourism.setCallbackData("buttonTransportAndTourism");
        buttonFashionAndBeauty.setCallbackData("buttonFashionAndBeauty");
        buttonFood.setCallbackData("buttonFood");
        buttonRealEstateAndRetail.setCallbackData("buttonRealEstateAndRetail");
        buttonEducation.setCallbackData("buttonEducation");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonInternetMagazineAndServices);
        keyboardButtonsRow1.add(buttonInsurance);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonMedicine);
        keyboardButtonsRow2.add(buttonTelecommunications);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(buttonBanksAndFinance);
        keyboardButtonsRow3.add(buttonTransportAndTourism);

        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow4.add(buttonFashionAndBeauty);
        keyboardButtonsRow4.add(buttonFood);

        List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();
        keyboardButtonsRow5.add(buttonRealEstateAndRetail);
        keyboardButtonsRow5.add(buttonEducation);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);
        rowList.add(keyboardButtonsRow5);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


}