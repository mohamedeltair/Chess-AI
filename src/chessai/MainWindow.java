/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessai;

import java.awt.*;
import static java.awt.Color.*;
import java.awt.event.*;
import javax.swing.*;
import javafx.util.*;

/**
 *
 * @author elteir
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    MainWindow me = this;
    public static final String[] Cols = { "White", "Black"};
    public static final String[] Diffs = { "Easy", "Medium", "Hard"};
    
    public void addOp(Point point, JLabel lbl, int width, int height, String text) {
        JLabel op = new JLabel();
        op.setText(text);
        op.setSize(width/2, height/4);
        op.setLocation(point);
        op.setForeground(WHITE);
        op.setFont(new Font ("Garamond", Font.BOLD , 35));
        op.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
                ((JLabel)e.getSource()).setForeground(yellow);
            }
            
            public void mouseExited(MouseEvent e) {
                ((JLabel)e.getSource()).setForeground(white);
            }
            
            public void mouseClicked(MouseEvent e) {
                try {
                switch(text) {
                    case "New Game":
                        String choice = (String) JOptionPane.showInputDialog(null, 
                        "What is your color?",
                        "Color",
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        Cols, 
                        Cols[0]);
                        String diff = (String) JOptionPane.showInputDialog(null, 
                        "Choose a difficulty",
                        "Difficulty",
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        Diffs, 
                        Diffs[0]);
                        me.setVisible(false);
                        ChessWindow chessWin = new ChessWindow(choice.equals("White")?1:0, 1, false, diff.equals("Easy")?2:(diff.equals("Medium")?5:6));
                        chessWin.setVisible(true);
                        break;
                    case "Load Game":
                        String file = JOptionPane.showInputDialog(null, "File name");
                        String custom = ChessAI.readBoard(file);
                        Pair<Integer,Integer> pair = ChessAI.customBoard(custom);
                        String diffLoad = (String) JOptionPane.showInputDialog(null, 
                        "Choose a difficulty",
                        "Difficulty",
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        Diffs, 
                        Diffs[0]);
                        me.setVisible(false);
                        me.setVisible(false);
                        ChessWindow loadChessWin = new ChessWindow(pair.getKey(), pair.getValue(), true, diffLoad.equals("Easy")?2:(diffLoad.equals("Medium")?5:6));
                        loadChessWin.setVisible(true);
                        break;
                    case "Exit":
                        System.exit(0);
                }
                }
                catch(Exception ex){
                    System.out.println(ex);
                }
            }
        });
        lbl.add(op);
    }
    
    public void addOps(JLabel lbl, int width, int height) {
        addOp(new Point(width*4/11, height/4), lbl, width, height, "New Game");
        addOp(new Point(width*4/11, height*9/20), lbl, width, height, "Load Game");
        addOp(new Point(width*5/11, height*13/20), lbl, width, height, "Exit");
    }
    
    public MainWindow() {
        initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height * 4/5;
        int width = screenSize.width * 1/2;
        this.setLayout(null);
        this.setSize(new Dimension(width, height));
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        JLabel lbl = new JLabel();
        lbl.setSize(width, height);
        lbl.setLocation(new Point(0,0));
        lbl.setIcon(new ImageIcon(new ImageIcon("background.jpg").getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT)));
        lbl.setLayout(null);
        this.add(lbl);
        JLabel name = new JLabel();
        name.setText("Chess me if you can");
        name.setFont(new Font ("Garamond", Font.BOLD , 45));
        name.setSize(width*3/4, height/4);
        name.setForeground(WHITE);
        name.setLocation(new Point(width/5, 0));
        lbl.add(name);
        addOps(lbl, width, height);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
