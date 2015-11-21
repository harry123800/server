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
        InputStream in = null;
        OutputStream out = null;
        try {
            in = clientSocket.getInputStream();
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
                System.out.println("finish creating");
            } catch (IOException ex) {
                System.out.println("error when creating message:" + ex.getMessage());
                break;
            }

            if (protocolMessage != null) {
                byte [] response = MessageProcessor.process(protocolMessage);
                try {
                    out.write(response);
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

