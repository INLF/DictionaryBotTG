package org.example.dictionarybot.redis.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.neo4j.data.Dictionary;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserData {
    @Builder.Default
    private UserState userState = UserState.START;
    private Dictionary chosenDictionary;


    private Map<CommonCommand, String> lastChosenCommand = new HashMap<>();

    public Map<CommonCommand, String> getLastChosenCommand() {
        if (lastChosenCommand == null) {
            lastChosenCommand = new HashMap<>();
        }
        return lastChosenCommand;
    }

}
