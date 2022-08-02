/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.integracao;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jairosilva
 */
public class frmBalanca extends javax.swing.JFrame {

    //Weight data fild names
    private final static String ID = "ID";
    private final static String CMD = "CMD";
    private final static String NET_OR_GROSS = "B/L";
    private final static String SIGNAL = "Sinal";
    private final static String GROSS_WEIGHT = "Bruto";
    private final static String TARE = "Tara";
    private final static String NET_WEIGHT = "Liquido";
    private final static String STABLE_STATUS = "Estab";

    //Net or gross status
    private final static String IS_GROSS = "B";
    private final String IS_NET = "L";

    //Stable status
    private final static String IS_STABLE = "E";
    private final static String IS_UNSTABLE = "I";

    static Socket s;
    static ServerSocket ss;
    static InputStreamReader isr;
    static BufferedReader br;
    static String msg;

    private FileOutputStream fos;
    private static DataInputStream dis;

    /**
     * Creates new form backdoor
     */
    public frmBalanca() throws  SQLException, SQLException {
        initComponents();

        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
         SerialComm s = new SerialComm();
        s.execute();
       
    }

    public static void getReadPeso(String peso){
        txtPeso.setText(peso);
    }
    private static void readFirstWeightData(BufferedReader inBuffer) throws IOException {
        //Aguarda por algum dado e imprime a linha recebida quando recebe  
        String weightData = inBuffer.readLine();
        String weight = readParameter(weightData, NET_WEIGHT);
        showResult(weightData, weight);

    }

    private static void readStableWeightData(BufferedReader inBuffer, PrintStream ps) throws IOException {
        //Aguarda por algum dado e imprime a linha recebida quando recebe  
        String stableStatus = "";
        String weightData = "";
        do {
            weightData = inBuffer.readLine();
            stableStatus = readParameter(weightData, STABLE_STATUS);
            //Send CMDA so the OP_WEB keep connectio alive for a while 
            ps.print("CMDA");
        } while (stableStatus.equals(IS_STABLE));
        String weight = readParameter(weightData, NET_WEIGHT);
        showResult(weightData, weight);
    }

    private static void showResult(String weightData, String weight) {
        System.out.println("The entire data received from OP_WEB is:");
        System.out.println(weightData);
        System.out.println("Net Weight is:");
        System.out.println(weight);
        txtResultado.setText(txtResultado.getText() + "\nPeso é:" + weight);
        txtPeso.setText(weight);

    }

    private static String readParameter(String weightData, String parameter) {
        parameter += ":";
        int startIndex = weightData.indexOf(parameter);
        startIndex += parameter.length() + 1;
        int endIndex = weightData.indexOf(" ", startIndex);
        String weight = weightData.substring(startIndex, endIndex);
        return weight;

    }
    Timer timer = null;

    private void getPesoAutomatico() {
        if (timer == null) {
            //  this.timerExecutando = 
            timer = new Timer();
            TimerTask tarefa = new TimerTask() {
                public void run() {
                    try {
                        getPesoManual();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.scheduleAtFixedRate(tarefa, 1, 100);
        }
    }

    private void getPesoManual() {

        Socket s = null;

        //Declaro a Stream de saida de dados  
        PrintStream ps = null;
        BufferedReader entrada;
        try {

            //Creates thes socket to connect in the specified IP and port
            //Place the correct IP and port for OP_WEB
            s = new Socket(txtIp.getText(), Integer.valueOf(txtPorta.getText()));

            InetAddress address = s.getInetAddress();

            txtResultado.setText(txtResultado.getText() + "\nBalança " + address);

            //Creates the data output
            ps = new PrintStream(s.getOutputStream());

            //Creates a BufferedReader for data input
            entrada = new BufferedReader(new InputStreamReader(s.getInputStream()));

            //Read the first weight data regardless the stable status
            System.out.println("**************************************");
            System.out.println("Showing the first arriving weight data");
            System.out.println("**************************************");

            readFirstWeightData(entrada);

            System.out.println("\n\n\n");

            //Read the first stable weight data
            System.out.println("****************************************************************");
            System.out.println("Showing the first arriving ------- stable ---------- weight data");
            System.out.println("****************************************************************");

            readStableWeightData(entrada, ps);
            //Trata possíveis exceções  
            txtResultado.setText(txtResultado.getText() + "\nPeso coletado com sucesso \n");

        } catch (IOException e) {

            System.out.println("Socket error.\n" + e.getMessage());

            txtResultado.setText("Socket error.\n" + e.getMessage());

        } finally {

            try {

                //Encerra o socket cliente  
                s.close();

            } catch (IOException e) {
                System.out.println("Socket error.\n" + e.getMessage());
                txtResultado.setText("Socket error.\n" + e.getMessage());

            }

        }

    }
  

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtComando = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtIp = new javax.swing.JTextField();
        txtPorta = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        txtPeso3 = new javax.swing.JTextField();
        txtPeso1 = new javax.swing.JTextField();
        txtPeso = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtResultado = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JPS - Android Hack");
        setBackground(new java.awt.Color(0, 204, 102));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Atacar alvo"));

        txtComando.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtComando.setText("INJETORA");
        txtComando.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtComandoActionPerformed(evt);
            }
        });

        jLabel1.setText("Balança");

        txtIp.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIp.setText("127.0.0.1");
        txtIp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIpActionPerformed(evt);
            }
        });

        txtPorta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPorta.setText("2001");
        txtPorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaActionPerformed(evt);
            }
        });

        jButton1.setText("Pegar Peso");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Pegar Peso Automatico");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtPeso3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPeso3.setForeground(new java.awt.Color(0, 0, 204));

        txtPeso1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPeso1.setForeground(new java.awt.Color(0, 0, 204));

        txtPeso.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPeso.setForeground(new java.awt.Color(0, 0, 204));
        txtPeso.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPeso.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        txtPeso.setEnabled(false);
        txtPeso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtComando, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIp, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtPeso3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPeso1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPeso)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)))
                .addGap(2, 2, 2))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtComando, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtIp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPeso3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPeso1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtPeso, txtPeso1, txtPeso3});

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.gif"))); // NOI18N
        jButton3.setText("Pausar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        txtResultado.setColumns(20);
        txtResultado.setRows(5);
        jScrollPane3.setViewportView(txtResultado);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.gif"))); // NOI18N
        jButton4.setText("Pausar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(1, 1, 1)))
                        .addGap(2, 2, 2))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtComandoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtComandoActionPerformed
        getPesoManual();
    }//GEN-LAST:event_txtComandoActionPerformed

    private void txtIpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIpActionPerformed

    private void txtPortaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPortaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        getPesoAutomatico();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        getPesoManual();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtPesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesoActionPerformed

    private boolean timerExecutando;
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        if (timerExecutando) {
            timer.cancel();
            timer = null;
            txtResultado.setText("Coleta automática parada");
            timerExecutando = false;
        }


    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        ServerThread serverThread = new ServerThread(null);

    }//GEN-LAST:event_jButton4ActionPerformed
    private static int size = 0;

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
            java.util.logging.Logger.getLogger(frmBalanca.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmBalanca.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmBalanca.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmBalanca.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new frmBalanca().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(frmBalanca.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

//        try {
//            ss = new ServerSocket(2000);
//            while (true) {
//                s = ss.accept();
//
//                isr = new InputStreamReader(s.getInputStream());
//                br = new BufferedReader(isr);
//                int tmp;
//                msg = "\n";
//                while ((tmp = br.read()) != -1) {
//                    msg += (char) tmp;
//                }
//
//                txtResultado.setText(txtResultado.getText() + "vai");
//                //     getImagem(msg);
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(frmBalanca.class.getName()).log(Level.SEVERE, null, ex.toString());
//        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private static javax.swing.JTextField txtComando;
    private javax.swing.JTextField txtIp;
    private static javax.swing.JTextField txtPeso;
    private static javax.swing.JTextField txtPeso1;
    private static javax.swing.JTextField txtPeso3;
    private static javax.swing.JTextField txtPorta;
    private static javax.swing.JTextArea txtResultado;
    // End of variables declaration//GEN-END:variables

}
