import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public final class Proyecto2 extends JPanel {
    public static void main(String args[]) { 

        Proyecto2 proyecto = new Proyecto2();

        JFrame frame = new JFrame ("MyPanel");
        frame.setSize(1020,900);
        frame.setLocation(450, 50);
        frame.setTitle("Multiplicacion de matrices");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add (proyecto);
        frame.setResizable(false);
        frame.setVisible(true);
    }

	public int maxHilos = 20;

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
	private JButton btnCalculoSyC;

	private JLabel labelSecuencial;
	private JLabel jLabel1;

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


    //Variables
	public int nHilos;
	public int[][] m1;
	public int[][] m2;
	public int[][] resultSec;
	public int[][] resultConc;

	public int rowsM1;
	public int colsM1;
	public int rowsM2;
	public int colsM2;
	public boolean error = false;

	public ThreadMultSecuencial hiloSecuencial;
	public ThreadMultConcurrente hiloConcurrente;

	public Proyecto2(){

		nHilos = 10;
		crearComponentes();

		m1 = MatrixMult.generarMatriz(1000, 1000);
		m2 = MatrixMult.generarMatriz(1000, 1000);

		inicio();

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
        btnCalculoSyC = new JButton();

        labelSecuencial = new JLabel();   
		jLabel1 = new JLabel();    

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

        btnCalculoSyC.setText("Calculo secuencial y concurrente");
        add(btnCalculoSyC);

        labelSecuencial.setBackground(new Color(242, 242, 0));
        labelSecuencial.setFont(new Font("Segoe UI", 0, 18)); 
        labelSecuencial.setHorizontalAlignment(SwingConstants.CENTER);
        labelSecuencial.setText("Calculo secuencial");
        add(labelSecuencial);

        jLabel1.setBackground(new Color(242, 242, 0));
        jLabel1.setFont(new Font("Segoe UI", 0, 18)); 
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Calculo concurrente");
        add(jLabel1);

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


        //set component bounds

        labelTitulo.setBounds(360, 10, 290, 35);

        imgM1.setBounds(250, 70, 64, 64);
        imgM2.setBounds(530, 70, 64, 64);

        labelM1.setBounds(330, 70, 121, 60);
        labelM2.setBounds(610, 70, 121, 60);

        btnGenerarM1.setBounds(255, 140, 195, 28); 
        btnGenerarM2.setBounds(535, 140, 195, 28);   

        btnCalculoSecuencial.setBounds(255, 220, 475, 23);
        btnCalculoConcurrente.setBounds(255, 250, 475, 23);
        btnCalculoSyC.setBounds(255, 280, 475, 23);

        labelSecuencial.setBounds(140, 330, 260, 20);
        jLabel1.setBounds(570, 330, 260, 20);

        imgHiloSec.setBounds(200, 370, 128, 128);
        labelHiloSec.setBounds(140, 500, 250, 30);      
        pbSec.setBounds(140, 550, 250, 50);
        labelResSec.setBounds(90, 740, 350, 22);
        btnResultadoSec.setBounds(90, 780, 350, 50);

        labelNumeroHilos.setBounds(560, 370, 180, 22);
        btnRemoverHilo.setBounds(750, 370, 30, 30);
        btnAgregarHilo.setBounds(790, 370, 30, 30);    
        labelResConc.setBounds(520, 740, 350, 22);
        btnResultadoConc.setBounds(520, 780, 350, 50);

        for (int i = 0; i < maxHilos/2 ; i++ ) {
        	pbsConc[i*2].setBounds(470, 420+i*30, 145, 25);
        	labelsHiloConc[i*2].setBounds(620, 420+i*30, 100, 25);
        }
        for (int i = 0; i < maxHilos/2 ; i++ ) {
        	pbsConc[i*2+1].setBounds(730, 420+i*30, 145, 25);
        	labelsHiloConc[i*2+1].setBounds(880, 420+i*30, 100, 25);
        }

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

}




