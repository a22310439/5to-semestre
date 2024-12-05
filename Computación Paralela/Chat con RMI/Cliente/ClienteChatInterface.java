package Cliente;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClienteChatInterface extends Remote {
    void recibirMensaje(String remitente, String mensaje, boolean esPrivado) throws RemoteException;
    String getNombre() throws RemoteException;
    void setGUI(ClienteChatGUI gui) throws RemoteException;
}
