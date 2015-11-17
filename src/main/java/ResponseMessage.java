/**
 * Created by haoqing on 11/16/15.
 */
public class ResponseMessage extends RequestMessage {

    public ResponseMessage(Boolean isSuccessful, char opCode, String value) {

        if (opCode == 0x00 && !isSuccessful) {
            value = "Not found";
        }

        if (opCode == 0x01) {
            value = "";
        }

        magic = 0x81;

        super.opCode = opCode;

        keyLength = 0;

        if (super.opCode == 0x00) {
            extraLength = isSuccessful ? 0x04 : 0x00;
        } else { // set command
            extraLength = 0x00;
        }

        dataType = 0x00;

        status = new char[2];
        status[0] = 0x00;
        status[1] = isSuccessful ? (char) 0x00 : (char) 0x01;

        totalLength = extraLength + keyLength + value.length();

        opaque = new char[4];
        for (int i = 0; i < 4; i++) {
            opaque[i] = (char) 0x00;
        }

        CAS = new char[8];
        for (int i = 0; i < 8; i++) {
            CAS[i] = (char) 0x00;
        }

        extras = new char[super.extraLength];
        for (int i = 0; i < super.extraLength; i++) {
            super.extras[i] = (char) 0x00;
        }

        key = new char[0];

        super.value = value.toCharArray();

    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append((char)super.magic);
        builder.append((char)super.opCode);
        builder.append(getCharArrayFromInt(2, super.keyLength));
        builder.append(getCharArrayFromInt(1, super.extraLength));
        builder.append(getCharArrayFromInt(1, super.dataType));
        builder.append(super.status);
        builder.append(getCharArrayFromInt(4, super.totalLength));
        builder.append(super.opaque);
        builder.append(super.CAS);
        builder.append(extras);
        builder.append(key);
        builder.append(value);

        return builder.toString();

    }

    private char[] getCharArrayFromInt(final int count, int val) {

        if (count > 4 || count <= 0) {
            return null;
        }
        char[] res = new char[count];
        int mask = 0x000000FF;
        for (int i = 0; i < count; i++) {
            int cur = mask & val;
            res[count - 1 - i] = (char) cur;
            val >>>= 8;
        }
        return res;

    }
}
