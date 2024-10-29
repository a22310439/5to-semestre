import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException; 
import javax.swing.*;


public class Dialog extends JDialog{

  private JLabel label1;
  private JButton btn1;
  private JTextArea textarea1;

  public Dialog(boolean modal, int[][] matrix, String str) {

    setLayout(null);
    setBounds(480,50,900,750);
    //setAlwaysOnTop(true);

    textarea1=new JTextArea();
    String content;
    content = MatrixMult.print2D(matrix, 100_000);
    textarea1.setText(content);
    textarea1.setFont(new Font("Consolas", 0, 10)); 

    JScrollPane scrollableTextArea = new JScrollPane(textarea1);  
    scrollableTextArea.setBounds(10,50,860,580);
    scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
    scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
    add(scrollableTextArea);   

    //Etiqueta
    label1 = new JLabel(str);
    label1.setIcon(new ImageIcon(getClass().getResource("/images/result.png")));
    label1.setFont(new Font("Segoe UI", 0, 24)); 
    label1.setBounds(10,10,600,36);
    add(label1);

    btn1 = new JButton();
    btn1.setText("Guardar");
    btn1.setBounds(400,650,100,25);
    btn1.addActionListener((ActionEvent e) -> {
        try {
            String nombre; 
            nombre = JOptionPane.showInputDialog(null, "Guardar como:", "Matriz" + str.substring(str.length() - 1));
            if ("".equals(nombre) || nombre == null) return;
            nombre += ".txt";
            try (FileWriter myWriter = new FileWriter(nombre)) {
                for (int[] matrix1 : matrix) {
                    for (int j = 0; j < matrix[0].length; j++) {
                        myWriter.write(Integer.toString(matrix1[j]) + " ");
                    }
                    myWriter.write("\n");
                }
            }
            System.out.println("Successfully wrote to the file.");
        } catch (IOException ex) {
            System.out.println("An error occurred.");
        }
    });
    add(btn1);



  }


}