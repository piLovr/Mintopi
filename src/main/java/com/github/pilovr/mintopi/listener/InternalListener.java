package com.github.pilovr.mintopi.listener;

import com.github.pilovr.mintopi.client.Client;

public interface InternalListener {
    void registerListener(Listener listener);
    void unregisterListener(Listener listener);

    void setClient(Client client);

    String onInputRequired(String message);
}
