package com.github.pilovr.mintopi.client.listener;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.domain.Listener;

public interface InternalListener {
    void registerListener(Listener listener);
    void unregisterListener(Listener listener);

    void setClient(Client client);

    String onInputRequired(String message);
}
