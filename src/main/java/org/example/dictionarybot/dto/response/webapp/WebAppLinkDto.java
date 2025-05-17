package org.example.dictionarybot.dto.response.webapp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;

import static org.example.dictionarybot.dto.response.MessageType.WEB_APP_MESSAGE;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WebAppLinkDto extends AbstractMessageDto {
    private final WebAppInfoDto web_app;
    private final String title;

    @JsonCreator
    public WebAppLinkDto(
            @JsonProperty("chatId") String chatId,
            @JsonProperty("text") String text,
            @JsonProperty("telegramMessageMetadata") TelegramMessageMetadata telegramMessageMetadata,
            @JsonProperty("web_app") WebAppInfoDto web_app,
            @JsonProperty("title") String title
    ) {
        super(chatId, WEB_APP_MESSAGE, telegramMessageMetadata);
        this.web_app = web_app;
        this.title = title;
    }
}
