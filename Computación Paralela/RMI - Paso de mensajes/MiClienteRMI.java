import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
public class MiClienteRMI {
    public static void main(String[] args) {
    try {
        String ip = "192.168.1.75";
        String puerto = "1234";
        MiInterfazRemota mir =
        (MiInterfazRemota)Naming.lookup("//" + ip + ":" + puerto + "/PruebaRMI");

        // Imprimimos miMetodo1() tantas veces como devuelva miMetodo2()
        for (int i = 1; i <= mir.miMetodo2(); i++) mir.miMetodo1();

        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            System.out.println("Error: " + e);
        }
        System.out.println("Mensajes enviados");
    }
}