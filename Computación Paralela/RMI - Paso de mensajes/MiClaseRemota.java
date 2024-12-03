import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
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
        return "Estoy en otroMetodo()";
    }
    
    public static void main(String[] args) {
        try {
            MiInterfazRemota mir = new MiClaseRemota();

            java.rmi.Naming.rebind("//" +
            java.net.InetAddress.getLocalHost().getHostAddress() +
            ":" + args[0] + "/PruebaRMI", mir);
        } catch (MalformedURLException | UnknownHostException | RemoteException e) {
            System.out.println(e);
        }
    }
}