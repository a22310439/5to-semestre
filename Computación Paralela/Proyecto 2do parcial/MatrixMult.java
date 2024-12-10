import java.awt.Color;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;

public class MatrixMult{

    public static int flag = 0;
    public static ExecutorService ex;
    public Proyecto2 proyecto;


    public MatrixMult(Proyecto2 p){
        this.proyecto = p;
    }


    public static int[][] multiply(int m1[][], int m2[][], JProgressBar pb, JLabel estado) {
        if (m1[0].length != m2.length) {
            throw new IllegalArgumentException("Las matrices no son compatibles para la multiplicación.");
        }
    
        int rows = m1.length;
        int cols = m2[0].length;
        int commonDim = m1[0].length;
        int[][] res = new int[rows][cols];
    
        pb.setMaximum(rows);
        estado.setText("Procesando...");
        estado.setBackground(new Color(0, 255, 255));
    
        for (int i = 0; i < rows; i++) {
            pb.setValue(i);
            for (int k = 0; k < commonDim; k++) {
                int temp = m1[i][k];
                for (int j = 0; j < cols; j++) {
                    res[i][j] += temp * m2[k][j];
                }
            }
        }
    
        pb.setValue(rows);
        estado.setText("Finalizado");
        estado.setBackground(new Color(120, 255, 120));
    
        return res;
    }

    public static int[][] multiplyConcurrent(int m1[][], int m2[][], int nThreads, JProgressBar[] pbArr, JLabel[] estadoArr) {
        if (m1[0].length != m2.length) {
            throw new IllegalArgumentException("Las matrices no son compatibles para la multiplicación.");
        }

        int rows = m1.length;
        int cols = m2[0].length;
        int commonDim = m1[0].length;
        int[][] res = new int[rows][cols];
        CountDownLatch latch = new CountDownLatch(nThreads);

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        int blockSize = (int) Math.ceil((double) rows / nThreads);

        for (int h = 0; h < nThreads; h++) {
            final int istart = h * blockSize;
            final int iend = Math.min(istart + blockSize, rows);
            final int threadIndex = h;

            executor.execute(() -> {
                pbArr[threadIndex].setMinimum(istart);
                pbArr[threadIndex].setMaximum(iend);
                estadoArr[threadIndex].setText("Procesando...");
                estadoArr[threadIndex].setBackground(new Color(0, 255, 255));

                for (int i = istart; i < iend; i++) {
                    pbArr[threadIndex].setValue(i);
                    for (int k = 0; k < commonDim; k++) {
                        int temp = m1[i][k];
                        for (int j = 0; j < cols; j++) {
                            res[i][j] += temp * m2[k][j];
                        }
                    }
                }

                pbArr[threadIndex].setValue(iend);
                estadoArr[threadIndex].setText("Finalizado");
                estadoArr[threadIndex].setBackground(new Color(120, 255, 120));

                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        return res;
    }

    public static int[][] generarMatriz(int largo, int ancho){
        if (largo == 0 || ancho == 0)
            throw new ArithmeticException("Dimensiones no válidas"); 

        int[][] m = new int[largo][ancho];

        //Inicializa la matríz en 0
        for (int i = 0; i < largo ; i++ ) {
            for (int j = 0; j < ancho ; j++ ) { 
                m[i][j] = ThreadLocalRandom.current().nextInt(-20, 9);
            }
        }
        return m;   
    }


   public static String print2D(int mat[][], int max){
        String str = "";
        if (mat == null) return str;

        for (int[] mat1 : mat) {
            for (int j = 0; j < mat1.length; j++) {
                str += (mat1[j] + " ");
            }
            str += "\n";
            if (str.length() > max) {
                str += "... [" + mat.length + " X " + mat[0].length + "]\n...\n";
                for (int j = 0; j < mat1.length; j++) {
                    str += (mat[mat.length-1][j] + " ");
                }
                return str;
            }
        }
        return str;
    }


    public static void main(String[] args) {
        System.out.println("============================================================");
        long start, time;
        int[][] m1;
        int[][] m2;
        m1 = generarMatriz(1000,500);
        m2 = generarMatriz(500,1000);


        start = System.nanoTime();
        multiplyConcurrent(m1,m2,10,null,null);
        time = System.nanoTime() - start;
        System.out.printf("Primero: %.1f ms\n", (double) time / 1_000_000);

        System.out.println("\n============================================================\n");

        start = System.nanoTime();
        //multiply(m1,m2,null,null);
        time = System.nanoTime() - start;
        System.out.printf("Segundo: took %.1f ms\n", (double) time / 1_000_000);
        //print2D(m1);
    }

}
