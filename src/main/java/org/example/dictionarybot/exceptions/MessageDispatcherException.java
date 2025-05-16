package org.example.dictionarybot.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Setter
@Getter
public class MessageDispatcherException extends RuntimeException {
    private SendMessage sendMessage;

    public MessageDispatcherException(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }
}
