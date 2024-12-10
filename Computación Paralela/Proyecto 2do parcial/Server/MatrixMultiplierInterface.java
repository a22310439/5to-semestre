package Server;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatrixMultiplierInterface extends Remote {
    int[][] multiplyPart(int[][] m1, int[][] m2, int startRow, int endRow) throws RemoteException;
    void recibirMatrizA(int[][] matriz) throws RemoteException;
    boolean isReady() throws RemoteException;
}
