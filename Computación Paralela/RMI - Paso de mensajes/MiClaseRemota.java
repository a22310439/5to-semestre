import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class MiClaseRemota extends UnicastRemoteObject implements
    MiInterfazRemota {

    public MiClaseRemota() throws RemoteException {
        // Código del constructor
    }

    @Override
    public void miMetodo1() throws RemoteException {
        // Aquí ponemos el código que queramos
        System.out.println("Estoy en miMetodo1()");
    }

    @Override
    public int miMetodo2() throws RemoteException {
        // Aquí ponemos el código que queramos
        return 5;
    }

    @Override
    public String otroMetodo() {
        return "a";
    }

    public static void main(String[] args) {
        try {

            String ip = "192.168.1.75";
            int port = 1234;

            LocateRegistry.createRegistry(port);
            
            MiInterfazRemota mir = new MiClaseRemota();

            System.out.println("Servidor esperando mensajes...");
            
            java.rmi.Naming.rebind("//" + ip + ":" + port + "/PruebaRMI", mir);

        } catch (MalformedURLException | RemoteException e) {
            System.out.println(e);
        }
    }
}