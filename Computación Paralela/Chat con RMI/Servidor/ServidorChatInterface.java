package Servidor;

import Cliente.ClienteChatInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServidorChatInterface extends Remote {
    void registrarCliente(ClienteChatInterface cliente) throws RemoteException;
    void enviarMensajeBroadcast(String mensaje, String nombreCliente) throws RemoteException;
    void desregistrarCliente(ClienteChatInterface cliente) throws RemoteException;
    List<ClienteChatInterface> obtenerClientesConectados() throws RemoteException;
    ClienteChatInterface obtenerCliente(String nombreCliente) throws RemoteException;
}
