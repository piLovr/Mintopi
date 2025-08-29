package com.github.pilovr.mintopi.listener;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.room.Room;

public interface InternalListener<R extends Room, A extends Account> {
    void registerListener(Listener<R,A> listener);
    void unregisterListener(Listener<R,A> listener);

    void setClient(Listener<R,A> client);

    String onInputRequired(String message);
}
