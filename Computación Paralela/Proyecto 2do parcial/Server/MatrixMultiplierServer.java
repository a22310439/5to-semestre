package Server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class MatrixMultiplierServer extends UnicastRemoteObject implements MatrixMultiplierInterface {

    private final ExecutorService executorService;
    private int[][] m1;
    private int[][] m2;
    private int[][] parteMatrizA;
    private int[][] res;
    private final ReentrantLock lock; // Para sincronización en acceso a la matriz

    protected MatrixMultiplierServer() throws RemoteException {
        super();
        // Crea un pool de hilos con un número fijo de hilos igual al número de núcleos de la CPU
        int numThreads = 20;
        executorService = Executors.newFixedThreadPool(numThreads);
        lock = new ReentrantLock();
    }

    @Override
    public void  multiplyPart() throws RemoteException {
        if (parteMatrizA == null || m2 == null) {
            throw new RemoteException("No se han recibido las matrices necesarias para realizar la multiplicación.");
        }

        int rows = parteMatrizA.length; // Número de filas de la parte de la matriz A
        int cols = m2[0].length;  // Número de columnas de la matriz B
        int commonDim = parteMatrizA[0].length; // Dimensión común entre A y B
        res = new int[rows][cols]; // Matriz de resultado

        // Divide las filas en subtareas
        int blockSize = (int) Math.ceil((double) rows / 10);
        int numTasks = (int) Math.ceil((double) rows / blockSize);

        for (int i = 0; i < numTasks; i++) {
            final int blockStart = i * blockSize;
            final int blockEnd = Math.min(blockStart + blockSize, rows);

            executorService.submit(() -> {
                for (int r = blockStart; r < blockEnd; r++) {
                    for (int k = 0; k < commonDim; k++) {
                        int temp = parteMatrizA[r][k];
                        for (int c = 0; c < cols; c++) {
                            res[r][c] += temp * m2[k][c];
                        }
                    }
                }
            });
        }

        System.out.println("Multiplicación completada en este servidor.");
    }

    @Override
    public boolean isReady() throws RemoteException {
        System.out.println("Conexión establecida.");
        return true;
    }

    @Override
    public void recibirParteMatrizA(int[][] matriz) throws RemoteException {
        lock.lock();
        try {
            this.parteMatrizA = matriz;
            System.out.println("Parte de la matriz A recibida con " + matriz.length + " filas y " + matriz[0].length + " columnas.");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void recibirMatrizB(int[][] matriz) throws RemoteException {
        lock.lock(); // Bloquea el acceso concurrente
        try {
            this.m2 = matriz; // Almacena la matriz recibida
            System.out.println("Matriz B recibida con " + matriz.length + " filas y " + matriz[0].length + " columnas.");
        } finally {
            lock.unlock(); // Libera el bloqueo
        }
    }

    public int[][] getMatrizA() throws RemoteException {
        lock.lock();
        try {
            if (m1 == null) {
                throw new RemoteException("No hay ninguna matriz almacenada.");
            }
            return m1;
        } finally {
            lock.unlock();
        }
    }

    public int[][] getMatrizB() throws RemoteException {
        lock.lock();
        try {
            if (m2 == null) {
                throw new RemoteException("No hay ninguna matriz almacenada.");
            }
            return m2;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int[][] enviarMatrizRes() throws RemoteException {
        return res;
    }

    public static void main(String[] args) {
        try {
            MatrixMultiplierServer multiplier = new MatrixMultiplierServer();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("MatrixMultiplier", multiplier);
            System.out.println("MatrixMultiplier Server is ready.");
        } catch (MalformedURLException | RemoteException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}
