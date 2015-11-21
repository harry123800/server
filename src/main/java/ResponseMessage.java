import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoqing on 11/16/15.
 */
public class ResponseMessage extends RequestMessage {

    public ResponseMessage(Boolean isSuccessful, byte opCode, byte[] value, byte[] extras) {

        if (opCode == 0x00 && !isSuccessful) {
            byte[] newValue = {'N', 'o', 't', ' ', 'f', 'o', 'u', 'n', 'd' };
            value = newValue;
        }

        if (opCode == 0x01) {
            value = new byte[0];
        }

        magic = 0x81;

        super.opCode = opCode;

        keyLength = 0;

        if (super.opCode == 0x00) {
            extraLength = isSuccessful ? 0x04 : 0x00;
            super.extras = extras;
        } else { // set command
            extraLength = 0x00;
        }

        dataType = 0x00;

        status = new byte[2];
        status[0] = 0x00;
        status[1] = isSuccessful ? (byte) 0x00 : (byte) 0x01;


        opaque = new byte[4];
        for (int i = 0; i < 4; i++) {
            opaque[i] = 0x00;
        }

        CAS = new byte[8];
        for (int i = 0; i < 8; i++) {
            CAS[i] = 0x00;
        }

        key = new byte[0];
        super.value = value;
        totalLength = extraLength + keyLength + super.value.length;

    }

    public byte[] toBytes() {


        List<Byte> bytes = new ArrayList<Byte>();
        bytes.add((byte) super.magic);
        bytes.add((byte) super.opCode);

        byte[] cur;
        cur = getByteArrayFromInt(2, super.keyLength);
        bytes.add(cur[0]);
        bytes.add(cur[1]);

        cur = getByteArrayFromInt(1, super.extraLength);
        bytes.add(cur[0]);

        cur = getByteArrayFromInt(1, super.dataType);
        bytes.add(cur[0]);

        bytes.add(super.status[0]);
        bytes.add(super.status[1]);

        cur = getByteArrayFromInt(4, super.totalLength);
        bytes.add(cur[0]);
        bytes.add(cur[1]);
        bytes.add(cur[2]);
        bytes.add(cur[3]);

        for (int i = 0; i < super.opaque.length; i++) {
            bytes.add(super.opaque[i]);
        }

        for (int i = 0; i < super.CAS.length; i++) {
            bytes.add(super.CAS[i]);
        }

        for (int i = 0; i < super.extraLength; i++) {
            bytes.add(super.extras[i]);
        }

        for (int i = 0; i < super.keyLength; i++) {
            bytes.add(super.key[i]);
        }

        for (int i = 0; i < super.value.length; i++) {
            bytes.add(super.value[i]);
        }

        byte[] res = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            res[i] = bytes.get(i);
        }
        return res;

    }

    private byte[] getByteArrayFromInt(final int count, int val) {

        if (count > 4 || count <= 0) {
            return null;
        }
        byte[] res = new byte[count];
        int mask = 0x000000FF;
        for (int i = 0; i < count; i++) {
            int cur = mask & val;
            res[count - 1 - i] = (byte) cur;
            val >>>= 8;
        }
        return res;

    }
}
