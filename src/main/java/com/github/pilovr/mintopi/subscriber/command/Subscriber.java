package com.github.pilovr.mintopi.subscriber.command;

import com.github.pilovr.mintopi.subscriber.CommandSubscriber;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface Subscriber {
    String name();
}