/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessai;

import static chessai.ChessAI.*;
import java.awt.*;
import static java.awt.Color.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javafx.stage.*;
import javax.swing.*;

/**
 *
 * @author elteir
 */

class PosLabel extends JLabel {
    int x,y;
    public PosLabel(int x, int y) {
        super();
        this.x=x;
        this.y=y;
    }
}

class PC extends Thread {
    ChessWindow me;
    
    public PC(ChessWindow me) {
        this.me=me;
    }
    
    public void gotoMain() {
        me.setVisible(false);
        MainWindow mainWin = new MainWindow();
        mainWin.setVisible(true);
    }
    
    public void run() {
        try {
            printBoard();
            //test = true;
            Scanner s = new Scanner(System.in);
            //while(true) {
            //System.out.println("press to move");
            //System.in.read();
            Result result = alphaBeta(ChessWindow.turn, Integer.MIN_VALUE, Integer.MAX_VALUE, true, 0);
            if(result.from.x == -1) {
                if(Utilities.validState(ChessWindow.turn)) {
                    return;
                }
                JOptionPane.showMessageDialog(null, "Congratulations, you won!");
                log();
                gotoMain();
                //test=true;
                //alphaBeta(turn, Integer.MIN_VALUE, Integer.MAX_VALUE, true, 0);
                return;
            }
            System.out.println("moved "+ChessWindow.turn);
            makeMove(result.from, result.to);
            System.out.println("dl: " + ChessAI.depthLimit);
            printBoard();
            me.fillPieces();
            if(result.value==Integer.MAX_VALUE && result.howFar==1) {
                JOptionPane.showMessageDialog(null, "Hard luck, the computer won");
                gotoMain();
                return;
            }
            //System.out.println("utility: "+Utilities.getHeuristic(ChessWindow.turn));
            ChessWindow.turn = (ChessWindow.turn+1)%2;
        //}
        }
        catch(Exception e) {
            
        }
    }
}

public class ChessWindow extends javax.swing.JFrame {

    PosLabel[][] squares = new PosLabel[ChessAI.boardSize][ChessAI.boardSize];
    String[] twoCols = {"black", "white"};
    static int human, turn;
    PosLabel selected = null;
    Color[] squareCols = {WHITE, GRAY}; 
    ChessWindow me = this;
    /**
     * Creates new form ChessWindow
     */
    
    public void fillPieces() {
        for(int i=ChessAI.boardSize-1; i>=0; i--) {
            for(int j=0; j<ChessAI.boardSize; j++) {
                JLabel lbl = squares[ChessAI.boardSize-i-1][j];
                if(ChessAI.board[j][i].name!=0) {
                    lbl.setIcon(new ImageIcon(new ImageIcon(twoCols[ChessAI.board[j][i].color]+ChessAI.piecesNames[ChessAI.board[j][i].name-1]
                    +".png").getImage().getScaledInstance(lbl.getWidth(), lbl.getHeight(), Image.SCALE_DEFAULT)));
                }
                else {
                    lbl.setIcon(null);
                }
            }
        }
    }
    
    public boolean posHas(ArrayList<Pos>positions, Pos pos) {
        for(int i=0; i<positions.size(); i++) {
            if(positions.get(i).x == pos.x && positions.get(i).y == pos.y) {
                return true;
            }
        }
        return false;
    }
    
    public void fillSquares(int height, int width) {
        for(int i=0; i<ChessAI.boardSize; i++) {
            for(int j=0; j<ChessAI.boardSize; j++) {
                PosLabel lbl = squares[i][j] = new PosLabel(j,ChessAI.boardSize-i-1);
                lbl.setOpaque(true);
                lbl.setBackground(squareCols[(i+j)%2]);
                lbl.setSize(width, height);
                lbl.setLocation(new Point(j*width, i*height));
                lbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(turn != human)
                        return;
                    System.out.println("clicked");
                    PosLabel lbl = (PosLabel)e.getSource();
                    lbl.setBackground(YELLOW);
                    if(selected!=null && selected != lbl) {
                        selected.setBackground(squareCols[(selected.x+(ChessAI.boardSize-selected.y-1))%2]);
                        Piece[][] board = ChessAI.board;
                        int i=selected.x, j=selected.y;
                        if(board[i][j].name != 0 && board[i][j].color == human) {
                            ArrayList<Pos> positions = Utilities.moveCaller(board[i][j]);
                            if(posHas(positions, new Pos(lbl.x, lbl.y))) {
                                System.out.println("valid move");
                                Piece tempPiece = new Piece(board[lbl.x][lbl.y]);
                                int ind = makeMove(new Pos(i,j), new Pos(lbl.x, lbl.y));
                                if(Utilities.validState(human)) {
                                    fillPieces();
                                    selected = null;
                                    lbl.setBackground(squareCols[(lbl.x+(ChessAI.boardSize-lbl.y-1))%2]);
                                    turn = (turn+1)%2;
                                    try {
                                        new PC(me).start();
                                        return;
                                    }
                                    catch(Exception ex) {
                                        System.out.println(ex);
                                        return;
                                    }
                                }
                                else {
                                    if(ind != -1) {
                                        colPieces[human][board[lbl.x][lbl.y].ind].pos = new Pos(-1,-1);
                                        board[lbl.x][lbl.y].name = 1;
                                        board[lbl.x][lbl.y].ind = ind;
                                        colPieces[human][ind].pos = new Pos(lbl.x, lbl.y);
                                    }
                                    makeMove(new Pos(lbl.x, lbl.y), new Pos(i,j));
                                    board[lbl.x][lbl.y] = new Piece(tempPiece);
                                    if(board[lbl.x][lbl.y].name!=0) {
                                        colPieces[1-human][board[lbl.x][lbl.y].ind].pos = new Pos(lbl.x, lbl.y);
                                    }
                                    JOptionPane.showMessageDialog(null, "must protect the king!");
                                }
                            }
                        }
                    }
                    selected = lbl;
                }

            });
                this.add(lbl);
            }
        }
    }
    
    public void gotoMain() {
        this.setVisible(false);
        MainWindow mainWin = new MainWindow();
        mainWin.setVisible(true);
    }
    
    public void save() {
        try {
            String file = JOptionPane.showInputDialog(null, "File name");
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            for(int i=boardSize-1; i>=0; i--) {
                for(int j=0; j<boardSize; j++) 
                {
                    Piece piece = board[j][i];
                    int col = piece.color;
                    switch(piece.name) {
                        case 0: writer.write("..."); break;
                        case 1: writer.write("pa"+col); break;
                        case 2: writer.write("bi"+col); break;
                        case 3: writer.write("kn"+col); break;
                        case 4: writer.write("ro"+col); break;
                        case 5: writer.write("qu"+col); break;
                        case 6: writer.write("ki"+col); break;
                    }
                    writer.write(" ");
                }
                writer.write(System.getProperty( "line.separator" ));
            }
            writer.write(human+System.getProperty( "line.separator" )+turn);
            writer.flush();
            writer.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public void setMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        menuBar.add(file);
        JMenuItem checkDraw = new JMenuItem("Is it draw?"), save = new JMenuItem("Save"), mainMenu = new JMenuItem("Main Menu"), exit = new JMenuItem("Exit");
        checkDraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    if(Utilities.isDraw(turn)) {
                        JOptionPane.showMessageDialog(null, "The game is draw !");
                        gotoMain();
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "The game is not draw");
                    }
                }
                catch(Exception e) {
                    
                }
            }
        });
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                    save();
            }
        });
        mainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                    gotoMain();
            }
        });
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                    System.exit(0);
            }
        });
        file.add(checkDraw);
        file.add(save);
        file.add(mainMenu);
        file.add(exit);
        setJMenuBar(menuBar);
    }
    
    public ChessWindow(int choice, int start, boolean custom, int diff) throws IOException {
        initComponents();
        setMenu();
        human = choice;
        turn = start;
        ChessAI.depthLimit = diff;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height * 83/100;
        int width = screenSize.width * 1/2;
        this.setLayout(null);
        this.setSize(new Dimension(width, height));
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        fillSquares(screenSize.height*2/21, screenSize.width/16);
        int[] dirs = {-1,1};
        for(int i=0; i<colorDir.length; i++) {
            colorDir[i] = dirs[i] * (choice == 1 ? 1 : -1);
        }
        initiateGame(custom?-1:1-human);
        Utilities.synch();
        fillPieces();
        if(human == 1-turn)
            new PC(me).start();
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
            java.util.logging.Logger.getLogger(ChessWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChessWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChessWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChessWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ChessWindow(0,0,false, 0).setVisible(true);
                }
                catch(Exception e) {
                    
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
