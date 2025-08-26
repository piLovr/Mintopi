package com.github.pilovr.mintopi.config;

import com.github.pilovr.mintopi.subscriber.command.CommandHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(MintopiProperties.class)
public class MintopiAutoConfiguration {

    @Bean
    public CommandHandler commandHandler(List<CommandBlueprint> allCommands, MintopiProperties properties) {
        return new CommandHandler(allCommands, properties);
    }
}
