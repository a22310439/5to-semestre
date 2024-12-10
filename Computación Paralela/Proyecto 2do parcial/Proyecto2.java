import Server.MatrixMultiplierInterface;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;


public final class Proyecto2 extends JPanel {
    public static void main(String args[]) { 

        Proyecto2 proyecto = new Proyecto2();

        JFrame frame = new JFrame ("MyPanel");
        frame.setSize(1600,900);
        frame.setLocation(250, 50);
        frame.setTitle("Multiplicacion de matrices");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add (proyecto);
        frame.setResizable(false);
        frame.setVisible(true);
    }

	public int maxHilos = 20;

    private List<String> servidoresConectados = new ArrayList<>();

	//Componentes Visuales
	private JLabel labelTitulo;

	private JButton imgM1;
    private JButton imgM2;

    public JLabel labelM1;
    public JLabel labelM2;

    private JButton btnGenerarM1;
    private JButton btnGenerarM2;

    private JButton btnCalculoSecuencial;
    private JButton btnCalculoConcurrente;
    private JButton btnCalculoParalelo;
	private JButton btnCalculoSyC;
	private JButton btnCalculoSCP;

	private JLabel tituloSecuencial;
	private JLabel tituloConcurrente;
	private JLabel tituloParalelo;

	private JLabel imgHiloSec;
	private JLabel labelHiloSec;
	private JProgressBar pbSec;
	private JLabel labelResSec;
	private JButton btnResultadoSec;

	private JLabel labelNumeroHilos;
	private JButton btnRemoverHilo;    
    private JButton btnAgregarHilo; 
    private JLabel labelResConc;

    private JButton btnResultadoConc;


    private JProgressBar[] pbsConc = new JProgressBar[maxHilos];
    private JLabel[] labelsHiloConc = new JLabel[maxHilos];

    private JLabel imgHiloPar;
    private JLabel labelsHiloPar;
    private JProgressBar pbPar;     
    private JLabel labelResPar;
    private JButton btnResultadoPar;

    private JPanel servidoresDisponibles;
    private JButton btnAgregarServidor;
    private JButton btnRemoverServidor;
    private JLabel[] ipServidores = new JLabel[3];
    private JLabel[] serverIcons = new JLabel[3];
    private JLabel[] disponibleIcons = new JLabel[3];


    //Variables
	public int nHilos;
    public int nServers;
	public int[][] m1;
	public int[][] m2;
	public int[][] resultSec;
	public int[][] resultConc;
    public int[][] resultPar;

	public int rowsM1;
	public int colsM1;
	public int rowsM2;
	public int colsM2;
	public boolean error = false;

	public ThreadMultSecuencial hiloSecuencial;
	public ThreadMultConcurrente hiloConcurrente;
	public ThreadMultParalelo hiloParalelo;

	public Proyecto2(){

		nHilos = 10;
        nServers = 0;
		crearComponentes();

		m1 = MatrixMult.generarMatriz(1000, 1000);
		m2 = MatrixMult.generarMatriz(1000, 1000);

		inicio();

        verificarServidores();

		//Generar M1
        btnGenerarM1.addActionListener((ActionEvent e) -> {
            labelM1.setText("Generando...");
            String inp1 = JOptionPane.showInputDialog(null, "Matriz A\nIngresa el numero de filas: ");
            String inp2 = JOptionPane.showInputDialog(null, "Matriz A\nIngresa el numero de columnas: ");
            try{
                rowsM1 = Integer.parseInt(inp1);
                colsM1 = Integer.parseInt(inp2);
                m1 = MatrixMult.generarMatriz(rowsM1, colsM1);
            }catch(NumberFormatException ex){ JOptionPane.showMessageDialog(null,"Matriz A\nValores no validos","Error",JOptionPane.ERROR_MESSAGE);}
            
            if (m1 != null){
                rowsM1 = m1.length;
                colsM1 = m1[0].length;
                String str = "<html><body><div style=\"text-align:center; width:80px;\">[" + rowsM1 + " X " + colsM1 + "]";
                int aux = m1.length;
                if (m1.length > 3)
                    aux = 3;
                for (int i = 0; i < aux ; i++ ) {
                    str += "<p style=\"white-space:nowrap;text-align:center; width:80px;\">" + arrToStr(m1[i]) + "</p>";
                }
                
                str += "</body></html>";
                labelM1.setText(str);
            }
            else
                labelM1.setText("No hay ninguna matriz");
                });
        //Generar M2
        btnGenerarM2.addActionListener((ActionEvent e) -> {
            labelM2.setText("Generando...");
            String inp1 = JOptionPane.showInputDialog(null, "Matriz B\nIngresa el numero de filas: ");
            String inp2 = JOptionPane.showInputDialog(null, "Matriz B\nIngresa el numero de columnas: ");
            try{
                rowsM2 = Integer.parseInt(inp1);
                colsM2 = Integer.parseInt(inp2);
                m2 = MatrixMult.generarMatriz(rowsM2, colsM2);
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(null,"Matriz B\nValores no validos","Error",JOptionPane.ERROR_MESSAGE);
            }

            
            if (m2 != null){
                rowsM2 = m2.length;
                colsM2 = m2[0].length;
                String str = "<html><body><div style=\"text-align:center; width:80px;\">[" + rowsM2 + " X " + colsM2 + "]</div>";
                int aux = m2.length;
                if (m2.length > 3)
                    aux = 3;
                for (int i = 0; i < aux ; i++ ) {
                    str += "<p style=\"white-space:nowrap;text-align:center; width:80px;\">" + arrToStr(m2[i]) + "</p>";
                }
                
                str += "</body></html>";
                labelM2.setText(str);
            }
            else
                labelM2.setText("No hay ninguna matriz");
                });

        //Calculo secuencial
        btnCalculoSecuencial.addActionListener((ActionEvent e) -> {
            if (colsM1 != rowsM2){
                JOptionPane.showMessageDialog(null,"Las columnas de A y las filas de B deben tener el mismo tamaño","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            limpiar();
            hiloSecuencial = new ThreadMultSecuencial();
            hiloSecuencial.start();
        });

        //Calculo concurrente
        btnCalculoConcurrente.addActionListener((ActionEvent e) -> {
            if (colsM1 != rowsM2){
                JOptionPane.showMessageDialog(null,"Las columnas de A y las filas de B deben tener el mismo tamaño","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            limpiar();
            hiloConcurrente = new ThreadMultConcurrente();
            hiloConcurrente.start();
        });

        //Calculo paralelo
        btnCalculoParalelo.addActionListener((ActionEvent e) -> {
            if (colsM1 != rowsM2) {
                JOptionPane.showMessageDialog(null, "Las columnas de A y las filas de B deben tener el mismo tamaño", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(nServers == 0){
                JOptionPane.showMessageDialog(null, "No hay servidores disponibles", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            limpiar(); // Limpia la interfaz antes de iniciar un nuevo cálculo.
        
            hiloParalelo = new ThreadMultParalelo();
            hiloParalelo.start();
        });

        //Calculo Sec&Conc
        btnCalculoSyC.addActionListener((ActionEvent e) -> {
            if (colsM1 != rowsM2){
                JOptionPane.showMessageDialog(null,"Las columnas de A y las filas de B deben tener el mismo tamaño","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            limpiar();
            hiloSecuencial = new ThreadMultSecuencial();
            hiloSecuencial.start();
            hiloConcurrente = new ThreadMultConcurrente();
            hiloConcurrente.start();
        });

        //Calculo Sec-Conc-Par
        btnCalculoSCP.addActionListener((ActionEvent e) -> {
            if (colsM1 != rowsM2){
                JOptionPane.showMessageDialog(null,"Las columnas de A y las filas de B deben tener el mismo tamaño","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(nServers == 0){
                JOptionPane.showMessageDialog(null, "No hay servidores disponibles", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            limpiar();
            hiloSecuencial = new ThreadMultSecuencial();
            hiloSecuencial.start();
            hiloConcurrente = new ThreadMultConcurrente();
            hiloConcurrente.start();
            hiloParalelo = new ThreadMultParalelo();
            hiloParalelo.start();
        });


        //Incrementar hilos
        btnAgregarHilo.addActionListener((ActionEvent e) -> {
            if ( nHilos < maxHilos) {
                nHilos += 1;
                labelNumeroHilos.setText("Numero de hilos: " + nHilos);
                pbsConc[nHilos-1].setMaximum(100);
                pbsConc[nHilos-1].setValue(0);
                pbsConc[nHilos-1].setVisible(true);
                labelsHiloConc[nHilos-1].setVisible(true);
                pbsConc[nHilos-1].setMaximum(100);
                pbsConc[nHilos-1].setMinimum(0);
                pbsConc[nHilos-1].setValue(0);
                labelsHiloConc[nHilos-1].setText("Hilo " + (nHilos-1));
                labelsHiloConc[nHilos-1].setBackground(new java.awt.Color(200,200,200));
            }
                });
        //Decrementar hilos
        btnRemoverHilo.addActionListener((ActionEvent e) -> {
            if ( nHilos > 1) {
                pbsConc[nHilos-1].setVisible(false);
                labelsHiloConc[nHilos-1].setVisible(false);
                nHilos -= 1;
                labelNumeroHilos.setText("Numero de hilos: " + nHilos);
            }
                });

        //Ver resultado secuencial
        btnResultadoSec.addActionListener((ActionEvent e) -> {
            Dialog ventana = new Dialog(true, resultSec, "Resultado algoritmo secuencial");
            ventana.setVisible(true);
        });

        //Ver resultado concurrente
        btnResultadoConc.addActionListener((ActionEvent e) -> {
            Dialog ventana = new Dialog(true, resultConc, "Resultado algoritmo concurrente");
            ventana.setVisible(true);
        });


        //Ver matriz A
        imgM1.addActionListener((ActionEvent e) -> {
            Dialog ventana = new Dialog(true, m1, "Matriz A");
            ventana.setVisible(true);
        });

        //Ver matriz B
        imgM2.addActionListener((ActionEvent e) -> {
            Dialog ventana = new Dialog(true, m2, "Matriz B");
            ventana.setVisible(true);
        });

        btnAgregarServidor.addActionListener((ActionEvent e) -> {
            if(nServers < 3){

                String ipServidor = JOptionPane.showInputDialog(null, "Ingrese la IP del servidor:", "Agregar Servidor", JOptionPane.PLAIN_MESSAGE);
                if (ipServidor == null || ipServidor.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No se ingresó una IP válida.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }else if(servidoresConectados.contains(ipServidor)){
                    JOptionPane.showMessageDialog(null, "Ese servidor ya se esta usando", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    String serverName = "rmi://" + ipServidor + ":1099/MatrixMultiplier";

                    // Intenta localizar el servidor y probar la conexión
                    MatrixMultiplierInterface multiplier = (MatrixMultiplierInterface) Naming.lookup(serverName);
                    System.out.println("isReady: " + multiplier.isReady());
                    if (multiplier.isReady()) {
                        servidoresConectados.add(ipServidor);
                        nServers++;
                        ipServidores[nServers-1].setText("IP: " + ipServidor);
                        serverIcons[nServers-1].setVisible(true);
                        disponibleIcons[nServers-1].setVisible(true);
                        ipServidores[nServers-1].setVisible(true);
                        JOptionPane.showMessageDialog(null, "Servidor conectado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (HeadlessException | MalformedURLException | NotBoundException | RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "No se pudo conectar al servidor: " + ipServidor, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Ya no se pueden agregar más servidores", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

            
        });

        btnRemoverServidor.addActionListener((ActionEvent e) -> {
            if (nServers > 0) {
                // Crear una lista desplegable con las direcciones IP de los servidores
                String[] opciones = servidoresConectados.toArray(String[]::new);
                String servidorAEliminar = (String) JOptionPane.showInputDialog(
                    null,
                    "Selecciona el servidor a eliminar:",
                    "Eliminar Servidor",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
                );
        
                // Verificar que el usuario seleccionó un servidor
                if (servidorAEliminar != null) {
                    // Confirmar la eliminación
                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "¿Estás seguro de que deseas eliminar el servidor " + servidorAEliminar + "?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
        
                    if (confirm == JOptionPane.YES_OPTION) {
                        // Encontrar el índice del servidor seleccionado
                        int index = servidoresConectados.indexOf(servidorAEliminar);
        
                        // Eliminar el servidor de la lista
                        servidoresConectados.remove(index);
        
                        // Actualizar la interfaz gráfica
                        for (int i = 0; i < servidoresConectados.size(); i++) {
                            ipServidores[i].setText(servidoresConectados.get(i));
                            serverIcons[i].setVisible(true);
                            disponibleIcons[i].setVisible(true);
                            ipServidores[i].setVisible(true);
                        }
        
                        // Ocultar elementos sobrantes si quedan menos servidores
                        for (int i = servidoresConectados.size(); i < ipServidores.length; i++) {
                            ipServidores[i].setVisible(false);
                            serverIcons[i].setVisible(false);
                            disponibleIcons[i].setVisible(false);
                        }
        
                        nServers--;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No hay servidores para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
	}

    private void verificarServidores() {
        ScheduledExecutorService scheduler = (ScheduledExecutorService) Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            for (String ip : new ArrayList<>(servidoresConectados)) {
                try {
                    String serverName = "rmi://" + ip + ":1099/MatrixMultiplier";
                    MatrixMultiplierInterface multiplier = (MatrixMultiplierInterface) Naming.lookup(serverName);
                    if (multiplier.isReady()) {
                        disponibleIcons[servidoresConectados.indexOf(ip)].setIcon(new ImageIcon(Proyecto2.this.getClass().getResource("/images/yes.png")));
                    }
                } catch (MalformedURLException | NotBoundException | RemoteException e) {
                    disponibleIcons[servidoresConectados.indexOf(ip)].setIcon(new ImageIcon(Proyecto2.this.getClass().getResource("/images/no.png")));
                    servidoresConectados.remove(ip);
                }
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 100, TimeUnit.MILLISECONDS);
    }

	public void crearComponentes(){
		//construct components
		labelTitulo = new JLabel();

		imgM1 = new JButton();
		imgM2 = new JButton();

		labelM1 = new JLabel();
		labelM2 = new JLabel();

        btnGenerarM1 = new JButton();
        btnGenerarM2 = new JButton();

		btnCalculoSecuencial = new JButton();
        btnCalculoConcurrente = new JButton();
        btnCalculoParalelo = new JButton();
        btnCalculoSyC = new JButton();
        btnCalculoSCP = new JButton();

        tituloSecuencial = new JLabel();   
		tituloConcurrente = new JLabel();
		tituloParalelo = new JLabel();

		imgHiloSec = new JLabel();  
		labelHiloSec = new JLabel(); 
		pbSec = new JProgressBar();
        labelResSec = new JLabel();
        btnResultadoSec = new JButton();

        labelNumeroHilos = new JLabel();
        btnRemoverHilo = new JButton();
        btnAgregarHilo = new JButton(); 
		labelResConc = new JLabel();
		btnResultadoConc = new JButton();
        
        imgHiloPar = new JLabel();  
        labelsHiloPar = new JLabel(); 
        pbPar = new JProgressBar();     
        labelResPar = new JLabel();
        btnResultadoPar = new JButton();

        servidoresDisponibles = new JPanel();
        btnAgregarServidor = new JButton();
        btnRemoverServidor = new JButton();

        for (int i = 0; i < 3 ; i++ ) {
            serverIcons[i] = new JLabel();
            disponibleIcons[i] = new JLabel();
            ipServidores[i] = new JLabel();
        }

		for (int i = 0; i < maxHilos ; i++ ) {
			pbsConc[i] = new JProgressBar();
			labelsHiloConc[i] = new JLabel();
		}

        //adjust size and set layout
        setPreferredSize (new Dimension (944, 574));
        setLayout (null);

        //add components
        labelTitulo.setBackground(new Color(242, 242, 0));
        labelTitulo.setFont(new Font("Segoe UI", 0, 24)); 
        labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        labelTitulo.setText("Multiplicacion de matrices");
        add(labelTitulo);

        imgM1.setIcon(new ImageIcon(getClass().getResource("/images/matrix.png"))); 
        imgM1.setBorderPainted( false );
        imgM1.setOpaque(false);
		imgM1.setContentAreaFilled(false);
        add(imgM1);
        imgM2.setIcon(new ImageIcon(getClass().getResource("/images/matrix.png"))); 
        imgM2.setBorderPainted( false );
        imgM2.setOpaque(false);
		imgM2.setContentAreaFilled(false);
        add(imgM2);

		labelM1.setHorizontalAlignment(SwingConstants.CENTER);
        labelM1.setFont(new Font("Segoe UI", 0, 12)); 
        labelM1.setText("No hay ninguna matriz");
        add(labelM1);
        labelM2.setHorizontalAlignment(SwingConstants.CENTER);
        labelM2.setFont(new Font("Segoe UI", 0, 12)); 
        labelM2.setText("No hay ninguna matriz");
        add(labelM2);

        btnGenerarM1.setIcon(new ImageIcon(getClass().getResource("/images/dice-game-icon.png"))); 
        btnGenerarM1.setText("Generar matriz");
        add(btnGenerarM1);
       	btnGenerarM2.setIcon(new ImageIcon(getClass().getResource("/images/dice-game-icon.png"))); 
        btnGenerarM2.setText("Generar matriz");
        add(btnGenerarM2); 

        btnCalculoSecuencial.setText("Calculo secuencial");
        add(btnCalculoSecuencial); 

        btnCalculoConcurrente.setText("Calculo concurrente");
        add(btnCalculoConcurrente);

        btnCalculoParalelo.setText("Calculo paralelo");
        add(btnCalculoParalelo);

        btnCalculoSyC.setText("Calculo secuencial y concurrente");
        add(btnCalculoSyC);

        btnCalculoSCP.setText("Calculo secuencial, concurrente y paralelo");
        add(btnCalculoSCP);

        tituloSecuencial.setBackground(new Color(242, 242, 0));
        tituloSecuencial.setFont(new Font("Segoe UI", 0, 18)); 
        tituloSecuencial.setHorizontalAlignment(SwingConstants.CENTER);
        tituloSecuencial.setText("Calculo secuencial");
        add(tituloSecuencial);

        tituloConcurrente.setBackground(new Color(242, 242, 0));
        tituloConcurrente.setFont(new Font("Segoe UI", 0, 18)); 
        tituloConcurrente.setHorizontalAlignment(SwingConstants.CENTER);
        tituloConcurrente.setText("Calculo concurrente");
        add(tituloConcurrente);

        tituloParalelo.setBackground(new Color(242, 242, 0));
        tituloParalelo.setFont(new Font("Segoe UI", 0, 18)); 
        tituloParalelo.setHorizontalAlignment(SwingConstants.CENTER);
        tituloParalelo.setText("Calculo Paralelo");
        add(tituloParalelo);

        servidoresDisponibles.setLayout(null); // Layout personalizado
        servidoresDisponibles.setBorder(BorderFactory.createTitledBorder("Servidores disponibles: " + nServers));
        add(servidoresDisponibles);

        btnAgregarServidor.setIcon(new ImageIcon(getClass().getResource("/images/add.png")));
        servidoresDisponibles.add(btnAgregarServidor);
        btnRemoverServidor.setIcon(new ImageIcon(getClass().getResource("/images/minus.png")));
        servidoresDisponibles.add(btnRemoverServidor);

        	//--- Secuencial ---//

        imgHiloSec.setIcon(new ImageIcon(getClass().getResource("/images/thread1.png"))); 
        add(imgHiloSec);

        labelHiloSec.setHorizontalAlignment(SwingConstants.CENTER);
        labelHiloSec.setText("Estado hilo");      
        labelHiloSec.setOpaque(true);
        labelHiloSec.setBackground(new java.awt.Color(200,200,200));
        add(labelHiloSec);

        pbSec.setStringPainted(true);
        add(pbSec);

        labelResSec.setFont(new Font("Segoe UI", 0, 16)); 
        labelResSec.setText("Resultado:");
        labelResSec.setHorizontalAlignment(SwingConstants.CENTER);
        add(labelResSec);

        btnResultadoSec.setText("Ver resultado algoritmo secuencial");
        btnResultadoSec.setIcon(new ImageIcon(getClass().getResource("/images/result.png")));
        add(btnResultadoSec);

        	//--- Concurrente ---//

        labelNumeroHilos.setFont(new Font("Segoe UI", 0, 16)); 
        labelNumeroHilos.setText("Numero de hilos: " + nHilos);
        add(labelNumeroHilos);

        btnRemoverHilo.setIcon(new ImageIcon(getClass().getResource("/images/minus.png"))); 
        add(btnRemoverHilo);

        btnAgregarHilo.setIcon(new ImageIcon(getClass().getResource("/images/add.png"))); 
        add(btnAgregarHilo);


        labelResConc.setFont(new Font("Segoe UI", 0, 16)); 
        labelResConc.setText("Resultado:");
        labelResConc.setHorizontalAlignment(SwingConstants.CENTER);
        add(labelResConc);

        btnResultadoConc.setText("Ver resultado algoritmo concurrente");
        btnResultadoConc.setIcon(new ImageIcon(getClass().getResource("/images/result.png")));
        add(btnResultadoConc);


        for (int i = 0;  i < maxHilos ; i++ ) {
        	pbsConc[i].setStringPainted(true);
        	add(pbsConc[i]);
        	labelsHiloConc[i].setIcon(new ImageIcon(getClass().getResource("/images/thread2.png")));
        	labelsHiloConc[i].setText("Hilo " + i);
	        labelsHiloConc[i].setOpaque(true);
	        labelsHiloConc[i].setBackground(new java.awt.Color(200,200,200));
        	add(labelsHiloConc[i]);
        }

        //--- Paralelo ---//
        imgHiloPar.setIcon(new ImageIcon(getClass().getResource("/images/thread1.png"))); 
        add(imgHiloPar);

        labelsHiloPar.setHorizontalAlignment(SwingConstants.CENTER);
        labelsHiloPar.setText("Estado hilo paralelo");      
        labelsHiloPar.setOpaque(true);
        labelsHiloPar.setBackground(new java.awt.Color(200,200,200));
        add(labelsHiloPar);

        pbPar.setStringPainted(true);
        add(pbPar);

        labelResPar.setFont(new Font("Segoe UI", 0, 16)); 
        labelResPar.setText("Resultado:");
        labelResPar.setHorizontalAlignment(SwingConstants.CENTER);
        add(labelResPar);

        btnResultadoPar.setText("Ver resultado algoritmo paralelo");
        btnResultadoPar.setIcon(new ImageIcon(getClass().getResource("/images/result.png")));
        add(btnResultadoPar);

        for (int i = 0; i < 3 ; i++ ) {
            serverIcons[i].setIcon(new ImageIcon(getClass().getResource("/images/server.png")));
            servidoresDisponibles.add(serverIcons[i]);
            disponibleIcons[i].setIcon(new ImageIcon(getClass().getResource("/images/yes.png")));
            servidoresDisponibles.add(disponibleIcons[i]);
            ipServidores[i].setText("");
            ipServidores[i].setHorizontalAlignment(SwingConstants.CENTER);
            servidoresDisponibles.add(ipServidores[i]);
            serverIcons[i].setOpaque(false);
            disponibleIcons[i].setOpaque(false);
            serverIcons[i].setVisible(false);
            disponibleIcons[i].setVisible(false);
            ipServidores[i].setVisible(false);
        }
        //set component bounds

        labelTitulo.setBounds(655, 10, 290, 35);

        imgM1.setBounds(558, 70, 64, 64);
        imgM2.setBounds(838, 70, 64, 64);

        labelM1.setBounds(638, 70, 121, 60);
        labelM2.setBounds(918, 70, 121, 60);

        btnGenerarM1.setBounds(563, 140, 195, 28); 
        btnGenerarM2.setBounds(843, 140, 195, 28);   

        btnCalculoSecuencial.setBounds(563, 180, 475, 23);
        btnCalculoConcurrente.setBounds(563, 210, 475, 23);
        btnCalculoParalelo.setBounds(563, 240, 475, 23);
        btnCalculoSyC.setBounds(563, 270, 475, 23);
        btnCalculoSCP.setBounds(563, 300, 475, 23);

        tituloSecuencial.setBounds(140, 330, 260, 20);
        tituloConcurrente.setBounds(670, 330, 260, 20);
        tituloParalelo.setBounds(1200, 330, 260, 20);

        imgHiloSec.setBounds(200, 370, 128, 128);
        labelHiloSec.setBounds(140, 500, 250, 30);      
        pbSec.setBounds(140, 550, 250, 50);
        labelResSec.setBounds(90, 740, 350, 22);
        btnResultadoSec.setBounds(90, 780, 350, 50);

        labelNumeroHilos.setBounds(660, 370, 180, 22);
        btnRemoverHilo.setBounds(850, 370, 30, 30);
        btnAgregarHilo.setBounds(890, 370, 30, 30);    
        labelResConc.setBounds(625, 740, 350, 22);
        btnResultadoConc.setBounds(620, 780, 350, 50);

        for (int i = 0; i < 3; i++) {
            serverIcons[i].setBounds(100 + i * 100, 20 , 30, 30);
            disponibleIcons[i].setBounds(100 + i * 100, 35, 20, 20);
            ipServidores[i].setBounds(65 + i * 100, 60, 100, 20);
        }

        for (int i = 0; i < maxHilos/2 ; i++ ) {
        	pbsConc[i*2].setBounds(545, 420+i*30, 145, 25);
        	labelsHiloConc[i*2].setBounds(695, 420+i*30, 100, 25);
        }
        for (int i = 0; i < maxHilos/2 ; i++ ) {
        	pbsConc[i*2+1].setBounds(805, 420+i*30, 145, 25);
        	labelsHiloConc[i*2+1].setBounds(955, 420+i*30, 100, 25);
        }

        imgHiloPar.setBounds(1266, 370, 128, 128);
        labelsHiloPar.setBounds(1205, 500, 250, 30);      
        pbPar.setBounds(1205, 550, 250, 50);
        labelResPar.setBounds(1155, 740, 350, 22);
        btnResultadoPar.setBounds(1155, 780, 350, 50);

        servidoresDisponibles.setBounds(1130, 610, 400, 120);
        btnAgregarServidor.setBounds(50, 80, 30, 30);
        btnRemoverServidor.setBounds(10, 80, 30, 30);

	}

	public void limpiar(){
		pbSec.setMaximum(100);
		pbSec.setValue(0);	
		labelHiloSec.setText("Estado hilo");
		labelHiloSec.setBackground(new java.awt.Color(200,200,200));

		for (int i = 0; i < maxHilos ; i++ ) {
			pbsConc[i].setMaximum(100);
			pbsConc[i].setMinimum(0);
			pbsConc[i].setValue(0);
			labelsHiloConc[i].setText("Hilo " + i);
			labelsHiloConc[i].setBackground(new java.awt.Color(200,200,200));
		}

	}

	public void inicio(){
		if (m1 != null){
			rowsM1 = m1.length;
			colsM1 = m1[0].length;
			String str = "<html><body><div style=\"text-align:center; width:80px;\">[" + rowsM1 + " X " + colsM1 + "]";
			int aux = m1.length;
			if (m1.length > 3) 
				aux = 3;
			for (int i = 0; i < aux ; i++ ) {
				str += "<p style=\"white-space:nowrap;text-align:center; width:80px;\">" + arrToStr(m1[i]) + "</p>";
			}
				
			str += "</body></html>";
			labelM1.setText(str);
		}else
			labelM1.setText("No hay ninguna matriz");


		if (m2 != null){
			rowsM2 = m2.length;
			colsM2 = m2[0].length;
			String str = "<html><body><div style=\"text-align:center; width:80px;\">[" + rowsM2 + " X " + colsM2 + "]";
			int aux = m2.length;
			if (m2.length > 3) 
				aux = 3;
			for (int i = 0; i < aux ; i++ ) {
				str += "<p style=\"white-space:nowrap;text-align:center; width:80px;\">" + arrToStr(m2[i]) + "</p>";
			}
				
			str += "</body></html>";
			labelM2.setText(str);
		}else
			labelM2.setText("No hay ninguna matriz");

		for (int i = 0; i < maxHilos ; i++ ) {
			pbsConc[i].setVisible(false);
			labelsHiloConc[i].setVisible(false);
		}

		for (int i = 0; i < nHilos ; i++ ) {
			pbsConc[i].setVisible(true);
			labelsHiloConc[i].setVisible(true);
		}
	}


    public static String arrToStr(int[] arr){
    	String str = "";
    	for (int i = 0; i < arr.length ; i++ ) {
    		str += arr[i] + " ";
    	}
    	return str;
    }


    public class ThreadMultSecuencial extends Thread {
    	long start, time;
        @Override
    	public void run(){
    		start = System.nanoTime();
    		resultSec = MatrixMult.multiply(m1,m2,pbSec, labelHiloSec);
    		time = System.nanoTime() - start;
        	labelResSec.setText("Resultado despues de " + (double) time / 1_000_000 + "ms");
		}
    }

    public class ThreadMultConcurrente extends Thread {
    	long start, time;
        @Override
    	public void run(){
    		start = System.nanoTime();
    		resultConc = MatrixMult.multiplyConcurrent(m1,m2,nHilos,pbsConc,labelsHiloConc);
    		time = System.nanoTime() - start;
        	labelResConc.setText("Resultado despues de " + (double) time / 1_000_000 + "ms");
		}
    }

    public class ThreadMultParalelo extends Thread {
        long start, time;
    
        @Override
        public void run() {
            try {
                start = System.nanoTime();
                int rows = m1.length;
                int cols = m2[0].length;
                int[][] result = new int[rows][cols];
                int blockSize = (int) Math.ceil((double) rows / nServers);

                CountDownLatch latch = new CountDownLatch(nServers);
                List<int[][]> partialResults = new ArrayList<>();
                ExecutorService executor = Executors.newFixedThreadPool(nServers);

                for (int i = 0; i < nServers; i++) {
                    final int serverIndex = i;
                    final int startRow = i * blockSize;
                    final int endRow = Math.min(startRow + blockSize, rows);

                    executor.execute(() -> {
                        try {
                            String serverName = "rmi://" + servidoresConectados.get(serverIndex) + ":1099/MatrixMultiplier";
                            MatrixMultiplierInterface multiplier = (MatrixMultiplierInterface) Naming.lookup(serverName);

                            // Llamada remota al servidor
                            int[][] partialResult = multiplier.multiplyPart(m1, m2, startRow, endRow);

                            synchronized (partialResults) {
                                partialResults.add(partialResult);
                            }

                        } catch (MalformedURLException | NotBoundException | RemoteException e) {
                            System.out.println("Error en el servidor: " + servidoresConectados.get(serverIndex));
                            JOptionPane.showMessageDialog(null, "Error en el servidor: " + servidoresConectados.get(serverIndex),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                latch.await();
                executor.shutdown();

                // Combinar resultados parciales
                int currentRow = 0;
                for (int[][] partialResult : partialResults) {
                    for (int[] row : partialResult) {
                        result[currentRow++] = row;
                    }
                }

                time = System.nanoTime() - start;
                labelResPar.setText("Resultado después de " + (double) time / 1_000_000 + " ms");

            } catch (InterruptedException e) {
                System.out.println("Error en la ejecución paralela: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Error durante la ejecución paralela.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}