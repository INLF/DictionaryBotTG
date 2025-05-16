package org.example.dictionarybot.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.redis.data.UserData;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public void setEditableMessageId(String editableMessageId, String chatId) {
        redisTemplate.opsForValue().set(getEditableMessageKey(chatId), editableMessageId);
    }

    public String getEditableMessageId(String userId) {
        return redisTemplate.opsForValue().get(getEditableMessageKey(userId));
    }

    public void saveUserData(UserData userData, String userId) {
        redisTemplate.opsForValue().set(getUserDataKey(userId), convertUserDataToJsonString(userData));
    }

    public UserData getOrCreateUserData(String chatId) {
        String userDataStr = redisTemplate.opsForValue().get(getUserDataKey(chatId));
        if (userDataStr == null || userDataStr.isBlank()) {
            UserData defaultUserData = createDefaultUserData();
            saveUserData(defaultUserData, chatId);
            return defaultUserData;
        }
        return convertJsonStringToUserData(userDataStr);
    }

    private UserData createDefaultUserData() {
        return UserData.builder().userState(UserState.START).build();
    }


    private String convertUserDataToJsonString(UserData userData) {
        try {
            return objectMapper.writeValueAsString(userData);
        } catch (JsonProcessingException e) {
            log.info("Error while converting user data to json string", e);
            throw new RuntimeException(e);
        }
    }

    private UserData convertJsonStringToUserData(@NonNull String jsonString) {
        try {
            return objectMapper.readValue(jsonString, UserData.class);
        } catch (IOException e) {
            log.error("Error while converting jsonString of UserState", e);
            throw new RuntimeException(e);
        }
    }

    private String getUserDataKey(String userId) {
        return "userData-" + userId;
    }

    private String getEditableMessageKey(String userId) {
        return "editableMessageId-" + userId;
    }

}
