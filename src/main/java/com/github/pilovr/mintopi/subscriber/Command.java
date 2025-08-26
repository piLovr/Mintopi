package com.github.pilovr.mintopi.subscriber;

import com.github.pilovr.mintopi.subscriber.command.DummyClass;
import org.springframework.stereotype.Service;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface Command {
    String name();
    String category() default "";
    String shortDescription() default "";
    String longDescription() default "";
    String[] aliases() default {};
    String[] emojiAliases() default {};
    Class<? extends CommandSubscriber> parent() default DummyClass.class;
    // Add any other metadata fields you might need
}