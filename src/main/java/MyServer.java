import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by haoqing on 11/16/15.
 */
public class MyServer {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(11211);

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
