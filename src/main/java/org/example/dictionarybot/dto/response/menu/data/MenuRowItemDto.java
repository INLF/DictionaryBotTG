package org.example.dictionarybot.dto.response.menu.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuRowItemDto {
    private String command;
    private String title;
    private String url;

    public MenuRowItemDto(String command, String title) {
        this.command = command;
        this.title = title;
    }
}
