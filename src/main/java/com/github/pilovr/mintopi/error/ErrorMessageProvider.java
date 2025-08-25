package com.github.pilovr.mintopi.error;

import com.github.pilovr.mintopi.service.language.DefaultLanguageKeys;
import com.github.pilovr.mintopi.service.language.LanguageProvider;
import lombok.Setter;

public class ErrorMessageProvider {
    @Setter
    private static LanguageProvider languageProvider;

    public static LanguageLoadException languageLoadException(String language, Object... args) {
        String message = languageProvider.getText(language, DefaultLanguageKeys.ExceptionMessagesImpl.getLanguageLoadException(), args);
        return new LanguageLoadException(message);
    }
}
