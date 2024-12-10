package Server;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatrixMultiplierInterface extends Remote {
    void multiplyPart() throws RemoteException;
    void recibirMatrizB(int[][] matriz) throws RemoteException;
    void recibirParteMatrizA(int[][] matriz) throws RemoteException;
    int[][] enviarMatrizRes() throws RemoteException;
    boolean isReady() throws RemoteException;
}