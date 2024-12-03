import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MiInterfazRemota extends Remote {
    public void miMetodo1() throws RemoteException;
    public int miMetodo2() throws RemoteException;
    public String otroMetodo() throws RemoteException;
}