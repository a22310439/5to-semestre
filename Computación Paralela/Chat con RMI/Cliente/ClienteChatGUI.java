package Cliente;

import Servidor.ServidorChatInterface;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;

public class ClienteChatGUI extends JFrame {

    private JTextField campoMensaje;
    private JButton botonEnviar;

    private ClienteChatInterface cliente;
    private ServidorChatInterface servidor;

    private ScheduledExecutorService scheduler;

    private JTabbedPane tabbedPane;
    private Map<String, JTextArea> chats;

    private java.util.List<String> usuariosConectados;

    public ClienteChatGUI(String nombreCliente, String serverIP) {
        super("Chat - " + nombreCliente);

        try {
            // Configurar el cliente y el servidor
            cliente = new ClienteChat(nombreCliente);

            // Conectar al servidor utilizando la IP proporcionada
            String serverURL = "rmi://" + serverIP + ":1099/ServidorChat";
            servidor = (ServidorChatInterface) Naming.lookup(serverURL);
            servidor.registrarCliente(cliente);

            // Inicializar el mapa de chats
            chats = new HashMap<>();

            // Crear el JTabbedPane
            tabbedPane = new JTabbedPane();

            // Agregar el chat grupal a las pestañas
            JTextArea areaChatGrupal = new JTextArea();
            areaChatGrupal.setEditable(false);
            chats.put("Todos", areaChatGrupal);
            tabbedPane.addTab("Todos", new JScrollPane(areaChatGrupal));

            // Inicializar la lista de usuarios conectados
            usuariosConectados = new ArrayList<>();
            actualizarListaUsuarios();

            // Configurar la interfaz gráfica
            campoMensaje = new JTextField();
            botonEnviar = new JButton("Enviar");

            // Definir el ActionListener
            ActionListener enviarMensajeListener = (ActionEvent e) -> {
                enviarMensaje();
            };

            // Acción al pulsar el botón Enviar
            botonEnviar.addActionListener(enviarMensajeListener);

            // Acción al presionar Enter en el campo de mensaje
            campoMensaje.addActionListener(enviarMensajeListener);

            // Disposición de los componentes
            JPanel panelInferior = new JPanel(new BorderLayout());
            panelInferior.add(campoMensaje, BorderLayout.CENTER);
            panelInferior.add(botonEnviar, BorderLayout.EAST);

            getContentPane().add(tabbedPane, BorderLayout.CENTER);
            getContentPane().add(panelInferior, BorderLayout.SOUTH);

            // Añadir la barra de menú
            JMenuBar menuBar = new JMenuBar();
            JMenu menuChat = new JMenu("Chat");
            JMenuItem menuItemNuevoChat = new JMenuItem("Iniciar Chat Privado");

            menuChat.add(menuItemNuevoChat);
            menuBar.add(menuChat);
            setJMenuBar(menuBar);

            // Acción al seleccionar "Iniciar Chat Privado"
            menuItemNuevoChat.addActionListener(e -> {
                iniciarChatPrivado();
            });

            // Manejar cierre de la ventana
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    cerrarAplicacion();
                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(600, 400);
            setVisible(true);

            // Iniciar actualización periódica después de que el constructor haya terminado
            SwingUtilities.invokeLater(() -> {
                try {
                    cliente.setGUI(this);
                } catch (RemoteException e1) {
                    System.out.println("Error: " + e1.getMessage());
                }
                iniciarActualizacionPeriodica();
            });

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
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
        if (usuariosConectados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay usuarios conectados.", "Información", JOptionPane.INFORMATION_MESSAGE);
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
                        // Crear la pestaña si no existe
                        agregarTabChatPrivado(nombreUsuario);
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
            String mensaje = campoMensaje.getText();
            if (!mensaje.isEmpty()) {
                if (mensaje.startsWith("/msj")) {
                    // Procesar comando de mensaje privado
                    procesarComandoMensajePrivado(mensaje);
                } else {
                    // Enviar mensaje al chat activo
                    String tabKey = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
                    if (tabKey.equals("Todos")) {
                        // Enviar mensaje broadcast
                        servidor.enviarMensajeBroadcast(mensaje, cliente.getNombre());
                        // Mostrar el mensaje en el chat grupal
                        agregarMensajeATab("Todos", "Yo: " + mensaje);
                    } else {
                        // Enviar mensaje directo al chat activo
                        ClienteChatInterface clienteDestino = servidor.obtenerCliente(tabKey);
                        if (clienteDestino != null) {
                            clienteDestino.recibirMensaje(cliente.getNombre(), mensaje, true);
                            // Mostrar el mensaje en la pestaña correspondiente
                            agregarMensajeATab(tabKey, "Yo (privado): " + mensaje);
                        } else {
                            JOptionPane.showMessageDialog(this, "Usuario no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                campoMensaje.setText("");
            }
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void procesarComandoMensajePrivado(String comando) {
        // Eliminar el prefijo "/msj" y cualquier espacio al inicio
        String contenido = comando.substring(4).trim();
        int primerEspacio = contenido.indexOf(' ');
        if (primerEspacio == -1) {
            JOptionPane.showMessageDialog(this, "Formato incorrecto. Uso: /msj usuario mensaje", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String nombreUsuario = contenido.substring(0, primerEspacio).trim();
        String mensaje = contenido.substring(primerEspacio).trim();
        if (nombreUsuario.isEmpty() || mensaje.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Formato incorrecto. Uso: /msj usuario mensaje", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if (!nombreUsuario.equals(cliente.getNombre())) {
                ClienteChatInterface clienteDestino = servidor.obtenerCliente(nombreUsuario);
                if (clienteDestino != null) {
                    // Enviar el mensaje privado
                    clienteDestino.recibirMensaje(cliente.getNombre(), mensaje, true);
                    // Mostrar el mensaje en la pestaña correspondiente
                    agregarMensajeATab(nombreUsuario, "Yo (privado): " + mensaje);
                } else {
                    JOptionPane.showMessageDialog(this, "El usuario no está conectado.", "Usuario no encontrado", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No puedes enviarte mensajes a ti mismo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void mostrarMensaje(String remitente, String mensaje, boolean esPrivado) {
        SwingUtilities.invokeLater(() -> {
            String tabKey = esPrivado ? remitente : "Todos";
            agregarMensajeATab(tabKey, remitente + (esPrivado ? " (privado)" : "") + ": " + mensaje);
        });
    }

    private void agregarTabChatPrivado(String nombreUsuario) {
        if (!chats.containsKey(nombreUsuario)) {
            JTextArea areaChat = new JTextArea();
            areaChat.setEditable(false);
            chats.put(nombreUsuario, areaChat);
            tabbedPane.addTab(nombreUsuario, new JScrollPane(areaChat));
        }
    }

    private void agregarMensajeATab(String tabKey, String mensaje) {
        agregarTabChatPrivado(tabKey);
        JTextArea areaChat = chats.get(tabKey);
        areaChat.append(mensaje + "\n");
    }

    private void cerrarAplicacion() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        try {
            servidor.desregistrarCliente(cliente);
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.exit(0);
    }
    
    public static void main(String[] args) {
        // Solicitar la dirección IP
        String serverIP = JOptionPane.showInputDialog(null, "Ingrese la dirección IP del servidor:", "Conexión al Servidor", JOptionPane.QUESTION_MESSAGE);
        if (serverIP == null || serverIP.trim().isEmpty()) {
            System.out.println("La dirección IP no puede estar vacía.");
            System.exit(0);
        }

        // Solicitar el nombre del cliente
        String nombreCliente = JOptionPane.showInputDialog("Ingrese su nombre:");
        if (nombreCliente != null && !nombreCliente.isEmpty()) {
            new ClienteChatGUI(nombreCliente, serverIP);
        } else {
            System.out.println("El nombre no puede estar vacío.");
            System.exit(0);
        }
    }
}
