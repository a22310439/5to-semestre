import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    protected Socket serverSocket;
    protected DataOutputStream outputServer;

    public Client() throws IOException{
        serverSocket = new Socket("127.0.0.1", 1234);
    }
    
    public void startClient() {
        try {
            //Flujo de datos hacia el servidor
            outputServer = new DataOutputStream(serverSocket.getOutputStream());
            for (int i = 0; i < 5; i++) {
                outputServer.writeUTF("Este es el mensaje número " + (i + 1) + "\n");
            }
            serverSocket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        System.out.println("Iniciando cliente...");
        client.startClient();
    }
}