import java.util.HashMap;
import java.util.Map;

/**
 * Created by haoqing on 11/16/15.
 */
public class MessageProcessor {
    private static ConcurrentCacheHashMap<String, MemValue> map = new ConcurrentCacheHashMap<String, MemValue>();

    public static byte[] process(RequestMessage message) {

        ResponseMessage responseMessage;

        if (message.opCode == 0x01) {
            map.put(new String(message.key),
                    new MemValue(message.value, MemValue.getFlagForSetRequest(message.extras)));
            responseMessage = new ResponseMessage(true, (byte) message.opCode, null, null);
        } else { // if it is get command
            MemValue memVal = map.get(new String(message.key));
            byte[] val = memVal == null ? null : memVal.value;
            responseMessage = new ResponseMessage(memVal != null, (byte) message.opCode, val,
                    memVal == null ? null : memVal.flag);
        }
        return responseMessage.toBytes();

    }

    public static class MemValue {
        byte[] value;
        byte[] flag;

        public MemValue(byte[] val, byte[] flag) {
            if (flag.length != 4) {
                System.out.println("error the flag length must be 4");
            }
            this.value = val;
            this.flag = flag;
        }

        public static byte[] getFlagForSetRequest(byte[] extras) {
            byte[] res = new byte[4];
            for (int i = 0; i < 4; i++) {
                res[i] = extras[i];
            }
            return res;
        }
    }
}