package org.example.dictionarybot.dispatcher;

import org.example.dictionarybot.exceptions.MessageDispatcherException;

public interface MessageDispatcher<T> {
    void dispatch(T message) throws MessageDispatcherException;

}
