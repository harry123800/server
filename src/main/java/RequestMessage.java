import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by haoqing on 11/16/15.
 */
public class RequestMessage {
    //todo(haoqing) : use @Setter @Getter to protect attributes, for now all public
    public int magic;
    public int opCode;
    public int keyLength = 0;
    public int extraLength = 0;
    public int dataType = 0;
    public char[] status;
    public int totalLength;
    public char[] opaque;
    public char [] CAS;
    public char [] extras;
    public char [] key;
    public char [] value;

    private BufferedReader in;

    public RequestMessage(){}

    public RequestMessage(BufferedReader in) throws IOException {

        if (in == null) {
            throw new IOException("buffered reader is null, error in creating request");
        }

        this.in = in;

        char [] cur;

        magic = read(1)[0];
        opCode = read(1)[0];

        cur = read(2);
        keyLength = (cur[0] << 8) + cur[1];

        extraLength = read(1)[0];

        dataType = read(1)[0];

        status = read(2);

        cur = read(4);
        totalLength = (cur[0] << 24) + (cur[1] << 16) + (cur[2] << 8) + cur[3];

        opaque = read(4);

        CAS = read(8);

        extras = read(extraLength);

        key = read(keyLength);

        value = read(totalLength - extraLength - keyLength);

    }

    private char[] read(int num) {

        int count = num;
        int pos = 0;
        char[] res = new char[num];

        while (true) {
            if (count == 0) {
                return res;
            }
            try {
                int cur_count = in.read(res, pos, count);
                if (cur_count < 0) {
                    cur_count = 0;
                }
                count -= cur_count;
                pos += cur_count;
            } catch (IOException ex) {
                System.out.println("error when reading from bufferedReader");
                System.out.println(ex.getMessage());
            }
        }

    }

}
