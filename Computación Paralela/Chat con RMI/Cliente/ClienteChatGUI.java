package Cliente;

import Servidor.ServidorChatInterface;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;

public class ClienteChatGUI extends JFrame {

    private JTextField campoMensaje;
    private JButton botonEnviar;

    private ClienteChatInterface cliente;
    private ServidorChatInterface servidor; // opcional si se usa el servidor

    private ScheduledExecutorService scheduler;

    private JTabbedPane tabbedPane;
    private Map<String, JTextArea> chats;
    private Map<String, String> ipPorUsuario; // Mapa para almacenar IPs por nombre de usuario

    private java.util.List<String> usuariosConectados; // Solo se usa si hay servidor
    private boolean usarServidor;

    public ClienteChatGUI(String nombreCliente, String ipLocal, boolean usarServidor, String serverIP) {
        super("Chat - " + nombreCliente);
        this.usarServidor = usarServidor;

        try {
            System.setProperty("java.rmi.server.hostname", ipLocal);

            // Levantar RMI Registry local
            try {
                LocateRegistry.createRegistry(1099);
            } catch (RemoteException ex) {
                // Ya está levantado
            }

            cliente = new ClienteChat(nombreCliente);
            String clienteURL = "rmi://" + ipLocal + ":1099/ClienteChat_" + nombreCliente;
            Naming.rebind(clienteURL, cliente);
            cliente.setGUI(this);

            chats = new HashMap<>();
            ipPorUsuario = new HashMap<>();

            campoMensaje = new JTextField();
            botonEnviar = new JButton("Enviar");

            ActionListener enviarMensajeListener = (ActionEvent e) -> {
                enviarMensaje();
            };

            botonEnviar.addActionListener(enviarMensajeListener);
            campoMensaje.addActionListener(enviarMensajeListener);

            tabbedPane = new JTabbedPane();

            JPanel panelInferior = new JPanel(new BorderLayout());
            panelInferior.add(campoMensaje, BorderLayout.CENTER);
            panelInferior.add(botonEnviar, BorderLayout.EAST);

            getContentPane().add(tabbedPane, BorderLayout.CENTER);
            getContentPane().add(panelInferior, BorderLayout.SOUTH);

            JMenuBar menuBar = new JMenuBar();
            JMenu menuChat = new JMenu("Chat");
            JMenuItem menuItemConexionDirecta = new JMenuItem("Conectar a otro Cliente");
            menuChat.add(menuItemConexionDirecta);
            JMenuItem menuItemNuevoChat = new JMenuItem("Iniciar Chat Privado (con servidor)");
            menuChat.add(menuItemNuevoChat);

            menuBar.add(menuChat);
            setJMenuBar(menuBar);

            menuItemConexionDirecta.addActionListener(e -> {
                conectarAClienteDirecto();
            });

            menuItemNuevoChat.addActionListener(e -> {
                if (usarServidor) {
                    iniciarChatPrivado();
                } else {
                    JOptionPane.showMessageDialog(this, "Esta opción requiere conexión al servidor.", "Información", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    cerrarAplicacion();
                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(600, 400);
            setVisible(true);

            if (usarServidor) {
                conectarAlServidor(serverIP);
            }

        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Error al configurar el cliente: " + e.getMessage());
            System.exit(1);
        }
    }

    private void conectarAlServidor(String serverIP) {
        try {
            String serverURL = "rmi://" + serverIP + ":1099/ServidorChat";
            servidor = (ServidorChatInterface) Naming.lookup(serverURL);
            servidor.registrarCliente(cliente);

            JTextArea areaChatGrupal = new JTextArea();
            areaChatGrupal.setEditable(false);
            chats.put("Todos", areaChatGrupal);
            tabbedPane.addTab("Todos", new JScrollPane(areaChatGrupal));

            usuariosConectados = new ArrayList<>();
            actualizarListaUsuarios();

            SwingUtilities.invokeLater(() -> {
                iniciarActualizacionPeriodica();
            });
        } catch (RemoteException | NotBoundException | MalformedURLException ex) {
            System.out.println("Error al conectar con el servidor: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void iniciarActualizacionPeriodica() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                actualizarListaUsuarios();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void actualizarListaUsuarios() {
        if (!usarServidor || servidor == null) return;
        try {
            List<ClienteChatInterface> clientes = servidor.obtenerClientesConectados();
            usuariosConectados = new ArrayList<>();
            for (ClienteChatInterface c : clientes) {
                try {
                    String nombre = c.getNombre();
                    if (!nombre.equals(cliente.getNombre())) {
                        usuariosConectados.add(nombre);
                    }
                } catch (RemoteException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void iniciarChatPrivado() {
        if (!usarServidor || usuariosConectados == null || usuariosConectados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay usuarios conectados o no estás usando el servidor.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> comboUsuarios = new JComboBox<>(usuariosConectados.toArray(String[]::new));
        int opcion = JOptionPane.showConfirmDialog(this, comboUsuarios, "Iniciar Chat Privado", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            String nombreUsuario = (String) comboUsuarios.getSelectedItem();
            if (nombreUsuario != null && !nombreUsuario.trim().isEmpty()) {
                try {
                    ClienteChatInterface clienteDestino = servidor.obtenerCliente(nombreUsuario);
                    if (clienteDestino != null) {
                        agregarTabChatPrivado(nombreUsuario, clienteDestino);
                    } else {
                        JOptionPane.showMessageDialog(this, "El usuario no está conectado.", "Usuario no encontrado", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }

    private void enviarMensaje() {
        try {
            String mensaje = campoMensaje.getText().trim();
            if (!mensaje.isEmpty()) {
                String tabKey = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
                if (usarServidor && "Todos".equals(tabKey)) {
                    // Enviar mensaje broadcast via servidor
                    servidor.enviarMensajeBroadcast(mensaje, cliente.getNombre());
                    agregarMensajeATab("Todos", "Yo: " + mensaje);
                } else {
                    JTextArea areaChat = chats.get(tabKey);
                    ClienteChatInterface clienteDestino = (ClienteChatInterface) areaChat.getClientProperty("refRemota");

                    if (clienteDestino == null) {
                        // Si no hay servidor, intentamos resolver la referencia vía IP almacenada
                        if (!usarServidor) {
                            String ip = ipPorUsuario.get(tabKey);
                            if (ip != null) {
                                // Intentar lookup
                                String url = "rmi://" + ip + ":1099/ClienteChat_" + tabKey;
                                try {
                                    clienteDestino = (ClienteChatInterface) Naming.lookup(url);
                                    // Guardar la referencia para uso futuro
                                    areaChat.putClientProperty("refRemota", clienteDestino);
                                } catch (Exception ex) {
                                    System.out.println("Error en lookup inverso: " + ex.getMessage());
                                }
                            }
                        } else {
                            // Si se usa servidor, intentar obtener la referencia del servidor
                            clienteDestino = servidor.obtenerCliente(tabKey);
                            if (clienteDestino != null) {
                                areaChat.putClientProperty("refRemota", clienteDestino);
                            }
                        }
                    }

                    if (clienteDestino != null) {
                        clienteDestino.recibirMensaje(cliente.getNombre(), mensaje, true);
                        agregarMensajeATab(tabKey, "Yo (privado): " + mensaje);
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró el cliente destino.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                campoMensaje.setText("");
            }
        } catch (RemoteException e) {
            System.out.println("Error enviando mensaje: " + e.getMessage());
        }
    }

    public void mostrarMensaje(String remitente, String mensaje, boolean esPrivado) {
        SwingUtilities.invokeLater(() -> {
            String tabKey = esPrivado ? remitente : "Todos";
            agregarMensajeATab(tabKey, remitente + (esPrivado ? " (privado)" : "") + ": " + mensaje);
        });
    }

    private void agregarTabChatPrivado(String nombreUsuario, ClienteChatInterface refRemota) {
        if (!chats.containsKey(nombreUsuario)) {
            JTextArea areaChat = new JTextArea();
            areaChat.setEditable(false);
            chats.put(nombreUsuario, areaChat);
            JScrollPane scrollPane = new JScrollPane(areaChat);
            tabbedPane.addTab(nombreUsuario, scrollPane);

            // Guardar la referencia remota si se proporciona
            if (refRemota != null) {
                areaChat.putClientProperty("refRemota", refRemota);
            }
        }
    }

    private void agregarMensajeATab(String tabKey, String mensaje) {
        if (!chats.containsKey(tabKey)) {
            agregarTabChatPrivado(tabKey, null);
        }
        JTextArea areaChat = chats.get(tabKey);
        areaChat.append(mensaje + "\n");
    }

    private void cerrarAplicacion() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        if (usarServidor && servidor != null) {
            try {
                servidor.desregistrarCliente(cliente);
            } catch (RemoteException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.exit(0);
    }

    private void conectarAClienteDirecto() {
        String ipOtroCliente = JOptionPane.showInputDialog(this, "Ingrese la IP del otro cliente:", "Conexión directa", JOptionPane.QUESTION_MESSAGE);
        if (ipOtroCliente == null || ipOtroCliente.trim().isEmpty()) return;

        String nombreOtroCliente = JOptionPane.showInputDialog(this, "Ingrese el nombre del otro cliente:", "Conexión directa", JOptionPane.QUESTION_MESSAGE);
        if (nombreOtroCliente == null || nombreOtroCliente.trim().isEmpty()) return;

        try {
            String url = "rmi://" + ipOtroCliente + ":1099/ClienteChat_" + nombreOtroCliente;
            ClienteChatInterface clienteDestino = (ClienteChatInterface) Naming.lookup(url);
            if (clienteDestino != null) {
                agregarTabChatPrivado(nombreOtroCliente, clienteDestino);
                // Guardar la IP del otro cliente
                ipPorUsuario.put(nombreOtroCliente, ipOtroCliente);
                JOptionPane.showMessageDialog(this, "Conectado a " + nombreOtroCliente, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo conectar con el cliente especificado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Preguntar si se quiere usar servidor
        int opcion = JOptionPane.showConfirmDialog(null, "¿Desea conectarse a un servidor?", "Modo de conexión", JOptionPane.YES_NO_OPTION);
        boolean usarServidor = (opcion == JOptionPane.YES_OPTION);

        String serverIP = null;
        if (usarServidor) {
            serverIP = JOptionPane.showInputDialog("Ingrese la IP del servidor:");
            if (serverIP == null || serverIP.trim().isEmpty()) {
                System.out.println("La IP del servidor no puede estar vacía.");
                System.exit(0);
            }
        }

        String nombreCliente = JOptionPane.showInputDialog("Ingrese su nombre:");
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            System.out.println("El nombre no puede estar vacío.");
            System.exit(0);
        }

        String ipLocal = JOptionPane.showInputDialog("Ingrese la IP local de esta máquina:");
        if (ipLocal == null || ipLocal.trim().isEmpty()) {
            System.out.println("La IP local no puede estar vacía.");
            System.exit(0);
        }

        new ClienteChatGUI(nombreCliente, ipLocal, usarServidor, serverIP);
    }
}
