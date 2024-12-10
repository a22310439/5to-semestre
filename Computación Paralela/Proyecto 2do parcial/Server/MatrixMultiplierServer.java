package Server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class MatrixMultiplierServer extends UnicastRemoteObject implements MatrixMultiplierInterface {

    private final ExecutorService executorService;
    private int[][] matrizA; // Almacena la matriz A
    private final ReentrantLock lock; // Para sincronización en acceso a la matriz

    protected MatrixMultiplierServer() throws RemoteException {
        super();
        // Crea un pool de hilos con un número fijo de hilos igual al número de núcleos de la CPU
        int numThreads = 20;
        executorService = Executors.newFixedThreadPool(numThreads);
        lock = new ReentrantLock();
    }

    @Override
    public int[][] multiplyPart(int[][] m1, int[][] m2, int startRow, int endRow) throws RemoteException {
        int rows = endRow - startRow;
        int cols = m2[0].length;
        int commonDim = m1[0].length;
        int[][] res = new int[rows][cols];

        // Divide las filas en subtareas
        int blockSize = (int) Math.ceil((double) rows / 10);
        int numTasks = (int) Math.ceil((double) rows / blockSize);
        Future<?>[] futures = new Future<?>[numTasks];

        for (int i = 0; i < numTasks; i++) {
            final int blockStart = i * blockSize;
            final int blockEnd = Math.min(blockStart + blockSize, rows);

            futures[i] = executorService.submit(() -> {
                for (int r = blockStart; r < blockEnd; r++) {
                    for (int k = 0; k < commonDim; k++) {
                        int temp = m1[startRow + r][k];
                        for (int c = 0; c < cols; c++) {
                            res[r][c] += temp * m2[k][c];
                        }
                    }
                }
            });
        }

        // Manejar excepciones al esperar las tareas
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restablece el estado de interrupción
                throw new RemoteException("La ejecución fue interrumpida.", e);
            } catch (ExecutionException e) {
                throw new RemoteException("Error durante la ejecución concurrente.", e.getCause());
            }
        }

        System.out.println("Multiplicación de " + startRow + " a " + endRow + " completada.");

        return res;
    }

    @Override
    public boolean isReady() throws RemoteException {
        System.out.println("Conexión establecida.");
        return true;
    }

    @Override
    public void recibirMatrizA(int[][] matriz) throws RemoteException {
        lock.lock(); // Bloquea el acceso concurrente
        try {
            this.matrizA = matriz; // Almacena la matriz recibida
            System.out.println("Matriz A recibida con " + matriz.length + " filas y " + matriz[0].length + " columnas.");
        } finally {
            lock.unlock(); // Libera el bloqueo
        }
    }

    public int[][] getMatrizA() throws RemoteException {
        lock.lock();
        try {
            if (matrizA == null) {
                throw new RemoteException("No hay ninguna matriz almacenada.");
            }
            return matrizA;
        } finally {
            lock.unlock();
        }
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
