import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.IntSummaryStatistics;

/**
 * Created by haoqing on 11/16/15.
 */
public class RequestMessage {
    //todo(haoqing) : use @Setter @Getter to protect attributes, for now all public
    public int magic;
    public int opCode;
    public int keyLength;
    public int extraLength;
    public int dataType;
    public byte[] status;
    public int totalLength;
    public byte[] opaque;
    public byte [] CAS;
    public byte [] extras;
    public byte [] key;
    public byte [] value;

    private InputStream in;

    public RequestMessage(){}

    public RequestMessage(InputStream in) throws IOException {

        if (in == null) {
            throw new IOException("buffered reader is null, error in creating request");
        }

        this.in = in;

        byte [] cur;

        magic = read(1)[0];
        opCode = read(1)[0];

        cur = read(2);
        keyLength = (cur[0] << 8) + (cur[1] & 0x0000007F) + ((cur[1] & 0x00000100) == 0 ? 0 : 0x00000100) ;

        extraLength = read(1)[0];

        dataType = read(1)[0];

        status = read(2);

        cur = read(4);
        ByteBuffer byteBuffer = ByteBuffer.wrap(cur);
        totalLength = byteBuffer.getInt();

        opaque = read(4);

        CAS = read(8);

        extras = read(extraLength);

        key = read(keyLength);

        value = read(totalLength - extraLength - keyLength);



    }

    private byte [] read(int num) {
        int count = num;
        int pos = 0;
        byte[] res = new byte[num];

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
