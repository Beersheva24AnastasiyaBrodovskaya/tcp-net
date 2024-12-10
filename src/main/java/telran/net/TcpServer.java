package telran.net;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class TcpServer implements Runnable{
private static final int TIMEOUT = 10;
Protocol protocol;
int port;
ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
boolean gracefulShutdown = false;

public TcpServer(Protocol protocol, int port) {
    this.protocol = protocol;
    this.port = port;
}
    @Override
    public void run() {
       try (ServerSocket serverSocket = new ServerSocket(port)) {
         serverSocket.setSoTimeout(TIMEOUT);
         System.out.println("Server is listening on the port "+ port);
            while(!gracefulShutdown) {
                try{
                    Socket socket = serverSocket.accept();
                    var session = new TcpClientServerSession(protocol, socket);
                    Thread thread = new Thread(session);
                    executor.execute(thread);
                    
            } catch (SocketTimeoutException e) {
                if (gracefulShutdown) {
                    executor.shutdownNow();
                }
            }
        }
       } catch (Exception e) {
        System.out.println(e);
       }
    }

    public void shutdown() {
        gracefulShutdown = true;
    }

}