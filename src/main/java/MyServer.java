import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by haoqing on 11/16/15.
 */
public class MyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(11211);
        }
        catch (IOException ex) {
            System.out.println("please kill all processes in port 11211 and retry");
            System.exit(-1);
        }
        while (true) {
            try {
                ClientWorker clientWorker = new ClientWorker(serverSocket.accept());
                Thread thread = new Thread(clientWorker);
                thread.start();
            } catch (IOException e) {
                System.out.println("accept failed!");
            }
        }

    }
}
