package com.github.pilovr.mintopi.command;

import com.github.pilovr.mintopi.domain.message.Message;

import java.util.concurrent.Flow;

public interface Publisher extends Flow.Publisher<Message> {

}
