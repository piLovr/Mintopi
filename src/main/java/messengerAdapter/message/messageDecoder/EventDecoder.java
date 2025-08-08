package messengerAdapter.message.messageDecoder;

import com.piLovr.messengerAdapters.event.Event;
import com.piLovr.messengerAdapters.message.Message;

public interface EventDecoder {
    <T> boolean supports(Class<T> inputType);
    <T> Event decode(T source);
}
