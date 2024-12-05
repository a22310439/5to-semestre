package Cliente;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClienteChat extends UnicastRemoteObject implements ClienteChatInterface {
    private final String nombre;
    private transient ClienteChatGUI gui;

    public ClienteChat(String nombre) throws RemoteException {
        super();
        this.nombre = nombre;
    }

    // Agregar un método para establecer la referencia de GUI
    @Override
    public void setGUI(ClienteChatGUI gui) throws RemoteException {
        this.gui = gui;
    }

    @Override
    public void recibirMensaje(String remitente, String mensaje, boolean esPrivado) throws RemoteException {
        if (gui != null) {
            gui.mostrarMensaje(remitente, mensaje, esPrivado);
        } else {
            System.out.println("GUI es null en ClienteChat. No se puede mostrar el mensaje.");
    }
}

    @Override
    public String getNombre() throws RemoteException {
        return nombre;
    }
}