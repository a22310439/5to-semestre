package Server;

import java.rmi.Naming;

public class RMITestClient {
    public static void main(String[] args) {
        try {
            String serverName = "rmi://192.168.1.85:1099/MatrixMultiplier";
            MatrixMultiplierInterface multiplier = (MatrixMultiplierInterface) Naming.lookup(serverName);
            System.out.println("Conexión exitosa con el servidor.");
            System.out.println("¿Servidor listo? " + multiplier.isReady());
        } catch (Exception e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
