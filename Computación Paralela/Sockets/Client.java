import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    protected Socket serverSocket;
    protected DataOutputStream outputServer;

    public Client() throws IOException{
        serverSocket = new Socket("192.168.1.75", 1234);
    }
    
    public void startClient() {
        try {
            //Flujo de datos hacia el servidor
            outputServer = new DataOutputStream(serverSocket.getOutputStream());
            for (int i = 0; i < 10; i++) {
                outputServer.writeUTF("Este es el mensaje número " + (i + 1) + "\n");
                System.out.println("Mensaje " + (i + 1) + " enviado");
            }
            outputServer.flush();
            serverSocket.close();
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        System.out.println("Iniciando cliente...");
        client.startClient();
    }
}