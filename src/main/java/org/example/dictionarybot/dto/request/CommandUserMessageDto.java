package org.example.dictionarybot.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class CommandUserMessageDto extends AbstractUserMessageDto{
    private final String command;
    @JsonCreator
    public CommandUserMessageDto(@JsonProperty("messageId") String messageId,
                                   @JsonProperty("chatId") String chatId,
                                   @JsonProperty("timeStamp") Long timeStamp,
                                   @JsonProperty("text") String command,
                                   @JsonProperty("userId") String userId) {
        super(chatId, UserMessageType.COMMAND, userId, messageId, timeStamp);
        this.command = command;
    }
}
