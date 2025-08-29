package com.github.pilovr.mintopi.tools;

import com.github.pilovr.mintopi.service.language.LanguageProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextProviderBuilder {
    private LanguageProvider languageProvider;

    private String language;
    private Map<String, String> placeHolderValues;


    public TextProviderBuilder(LanguageProvider languageProvider, String language) {
        this.languageProvider = languageProvider;
        this.language = language;
    }

    public TextProviderBuilder(LanguageProvider languageProvider) {
        this.languageProvider = languageProvider;
    }

    public TextProviderBuilder language(String language) {
        this.language = language;
        return this;
    }

    public TextProviderBuilder placeHolderValues(Map<String, String> placeHolderValues) {
        this.placeHolderValues = placeHolderValues;
        return this;
    }

    public TextProviderBuilder addPlaceHolderValue(String key, String value) {
        if(this.placeHolderValues == null) {
            placeHolderValues = new ConcurrentHashMap<>();
        }
        this.placeHolderValues.put(key, value);
        return this;
    }



}
