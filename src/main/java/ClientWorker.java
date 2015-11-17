import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by haoqing on 11/16/15.
 */
public class ClientWorker implements Runnable {

    private Socket clientSocket;

    ClientWorker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        BufferedReader in = null;
        OutputStream out = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = clientSocket.getOutputStream();
        } catch (IOException ex) {
            System.out.println("error!");
            return;
        }

        RequestMessage protocolMessage = null;
        while(true) {
            if (clientSocket.isClosed()) {
                break;
            }
            try {
                protocolMessage = new RequestMessage(in);
            } catch (IOException ex) {
                System.out.println("error when creating message:" + ex.getMessage());
                break;
            }

            if (protocolMessage != null) {
                String response = MessageProcessor.process(protocolMessage);
                try {
                    out.write(response.getBytes(Charset.forName("ISO-8859-1")));
                    out.flush();
                } catch (Exception ex) {
                    System.out.println("error when writing to streams");
                    ex.printStackTrace();
                    break;
                }
            }
        }
        try {
            clientSocket.close();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

