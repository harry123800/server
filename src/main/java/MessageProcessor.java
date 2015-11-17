import java.util.HashMap;
import java.util.Map;

/**
 * Created by haoqing on 11/16/15.
 */
public class MessageProcessor {
    private static ConcurrentCacheHashMap<String, String> map = new ConcurrentCacheHashMap<String, String>();

    public static String process(RequestMessage message) {

        ResponseMessage responseMessage;

        if (message.opCode == 0x01) {
            map.put(new String(message.key), new String(message.value));
            responseMessage = new ResponseMessage(true, (char) message.opCode, null);
        } else { // if it is get command
            String val = map.get(new String(message.key));
            responseMessage = new ResponseMessage(val != null, (char) message.opCode, val);
        }
        return responseMessage.toString();

    }
}