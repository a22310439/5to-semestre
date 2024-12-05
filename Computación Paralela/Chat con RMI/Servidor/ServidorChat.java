package Servidor;

import Cliente.ClienteChatInterface;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServidorChat extends UnicastRemoteObject implements ServidorChatInterface {

    private final List<ClienteChatInterface> clientesConectados;

    public ServidorChat() throws RemoteException {
        super();
        clientesConectados = new ArrayList<>();
    }

    @Override
    public synchronized void registrarCliente(ClienteChatInterface cliente) throws RemoteException {
        this.clientesConectados.add(cliente);
        System.out.println("Cliente " + cliente.getNombre() + " registrado.");
    }

    @Override
    public synchronized void enviarMensajeBroadcast(String mensaje, String nombreCliente) throws RemoteException {
        for (ClienteChatInterface cliente : clientesConectados) {
            if (!cliente.getNombre().equals(nombreCliente)) {
                cliente.recibirMensaje(nombreCliente, mensaje, false); // false porque no es privado
            }
        }
    }

    @Override
    public synchronized List<ClienteChatInterface> obtenerClientesConectados() throws RemoteException {
        return new ArrayList<>(clientesConectados);
    }

    @Override
    public synchronized ClienteChatInterface obtenerCliente(String nombreCliente) throws RemoteException {
        for (ClienteChatInterface cliente : clientesConectados) {
            if (cliente.getNombre().equals(nombreCliente)) {
                return cliente;
            }
        }
        return null;
    }

    @Override
    public synchronized void desregistrarCliente(ClienteChatInterface cliente) throws RemoteException {
        clientesConectados.remove(cliente);
        System.out.println("Cliente " + cliente.getNombre() + " desregistrado.");
    }

    public static void main(String[] args) {
        try {
            // Crear una instancia del servidor
            ServidorChat servidor = new ServidorChat();

            // Registrar el servidor en el registro RMI
            LocateRegistry.createRegistry(1099);
            Naming.rebind("ServidorChat", servidor);

            System.out.println("Servidor listo y esperando conexiones...");
        } catch (MalformedURLException | RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
