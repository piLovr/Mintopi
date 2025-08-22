package com.github.pilovr.mintopi.starter.domain.common;

public interface InternalListener {
    void registerListener(Listener listener);
    void unregisterListener(Listener listener);

    String onInputRequired(String message);
    void setClient(Client client);
}
