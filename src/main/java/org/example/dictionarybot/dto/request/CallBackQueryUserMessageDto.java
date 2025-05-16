package org.example.dictionarybot.dto.request;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;



@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class CallBackQueryUserMessageDto extends AbstractUserMessageDto{
    private final String callBackQueryData;
    @JsonCreator
    public CallBackQueryUserMessageDto(@JsonProperty("messageId") String messageId,
                                       @JsonProperty("chatId") String chatId,
                                       @JsonProperty("timeStamp") Long timeStamp,
                                       @JsonProperty("query_data") String callBackQueryData,
                                       @JsonProperty("userId") String userId) {
        super(chatId, UserMessageType.CALLBACK_QUERY, userId, messageId, timeStamp);
        this.callBackQueryData = callBackQueryData;
    }
}
