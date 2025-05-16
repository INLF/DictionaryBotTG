package org.example.dictionarybot.dto.response;


import org.example.dictionarybot.dto.response.menu.data.MenuRowDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelegramMessageMetadata {
    private String editMessageId;
    private boolean isNewMessageForEdit;
    private List<MenuRowDto> oldEditableMessageChangeMenu;
}
