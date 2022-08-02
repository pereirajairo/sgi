/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame.faturamento;

import br.com.sgi.bean.Minuta;
import br.com.sgi.dao.MinutaDAO;
import br.com.sgi.frame.FatPedidoFaturar;
import br.com.sgi.frame.IntegrarPesos;
import br.com.sgi.frame.frmMinutasExpedicaoGerar;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.text.MaskFormatter;
import org.openswing.swing.mdi.client.MDIFrame;

/**
 *
 * @author Marcelo
 */
public class pnPedidoPadrao extends javax.swing.JPanel {

    private Minuta minuta;

    private MinutaDAO minutaDAO;

    //
    /**
     * Creates new form pnMesaPadrao
     */
    public pnPedidoPadrao() {
        initComponents();
        if (minutaDAO == null) {
            this.minutaDAO = new MinutaDAO();
        }

        lbStatusMesa.setForeground(Color.green.darker());
        liberarMesa.setVisible(false);
    }
//    private TelaMesa mesa;
    private String horaReservada;
    private static final DateFormat HORA = new SimpleDateFormat("HH:mm");
    private MaskFormatter fmtHora;
    private Timer timer;
    private ActionListener action;

    public void finalizaMesa() {
        this.setaStatus("DISPONÍVEL");
        this.lbTotalMesa.setText("0,00");
//        mesa = null;
    }

    public void modificaNum(Minuta minuta) throws SQLException {
        lbNumMesa.setText(minuta.getUsu_codlan().toString());
        lbTotalMesa.setText(minuta.getUsu_qtdvol().toString());
        lblStatus.setText("TNS " + minuta.getTransacao());
        if (minuta.getTransacao().equals("MG")) {
            lblStatus.setText("PES BAL");
        }
        lbStatusMesa.setText(minuta.getMinutaPedido().getUsu_sitmin());
        lblPedido.setText(minuta.getMinutaPedido().getUsu_numped().toString());
//        lblPedido.setVisible(true);
//        if (minuta.getMinutaPedido().getUsu_numped() == 0) {
//            lblPedido.setVisible(false);
//        }

        switch (minuta.getUsu_codemb()) {
            case 1:
                lblTipoMedida.setText("CAIXA");
                break;
            case 11:
                lblTipoMedida.setText("KG");
                break;
            case 13:
                lblTipoMedida.setText("VOLUME");
                break;
            case 15:
                lblTipoMedida.setText("PALLETS");
                break;
            default:
                lblTipoMedida.setText("OUTROS");

        }

        setaStatus(minuta.getUsu_sitmin());

        switch (minuta.getUsu_libmot()) {
            case "M":
                lblTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png")));
                break;
            case "A":
                lblTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/carros.png")));
                break;
            case "H":
                lblTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/truck_red.png")));
                break;
            case "I":
                lblTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png")));
                break;
            case "G":
                lblTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png")));
                break;
            default:
                break;

        }

    }

    private void getMinuta(String codigominuta) throws SQLException {
        minuta = new Minuta();
        minuta = minutaDAO.getMinuta("", "and usu_codlan = " + codigominuta);

        if (minuta != null) {
            if (minuta.getUsu_codlan() > 0) {
                lbTotalMesa.setText(minuta.getUsu_qtdvol().toString());
            }
        }
    }

    public void setaStatus(String st) {
        if (st.equalsIgnoreCase("LIBERADA")) {
            //lbStatusMesa.setText("LIBERADA");
            lbStatusMesa.setForeground(Color.blue);

        } else if (st.equalsIgnoreCase("Reservada")) {
            lbStatusMesa.setText("RESERVADA");
            lbStatusMesa.setForeground(Color.blue);

        } else {
            //lbStatusMesa.setText("FATURAR");
            lbStatusMesa.setForeground(Color.green.darker());
        }
    }

    public String getStatus() {
        return lbStatusMesa.getText();
    }

    public void atualizaValor(String valor) {
        lbTotalMesa.setText(valor);
    }

    public void atualizaHora() {
        if (action == null) {
            action = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (horaReservada.equals(HORA.format(new Date()))) {
                        fimReserva();
                    }
                }
            };
        }
        timer = new Timer(1000, action);
        timer.setInitialDelay(0);
        timer.setRepeats(true);
        timer.start();
    }

    public void fimReserva() {
        setaStatus("DISPONÍVEL");
        horaReservada = null;
        if (timer != null) {
            timer.stop();
        }
        liberarMesa.setVisible(false);
        analisarMinuta.setVisible(true);
    }

    public double getValorTotal() {
        return Double.valueOf(lbTotalMesa.getText().replaceAll(",", "."));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu = new javax.swing.JPopupMenu();
        analisarMinuta = new javax.swing.JMenuItem();
        statusMesa = new javax.swing.JMenuItem();
        liberarMesa = new javax.swing.JMenuItem();
        lbNumMesa = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lbStatusMesa = new javax.swing.JLabel();
        lblTipoMedida = new javax.swing.JLabel();
        lbTotalMesa = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        lblTipo = new javax.swing.JLabel();
        lblPedido = new javax.swing.JLabel();

        analisarMinuta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        analisarMinuta.setText("Analisar Minuta");
        analisarMinuta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analisarMinutaActionPerformed(evt);
            }
        });
        menu.add(analisarMinuta);

        statusMesa.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        statusMesa.setText("Status Mesa");
        statusMesa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusMesaActionPerformed(evt);
            }
        });
        menu.add(statusMesa);

        liberarMesa.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        liberarMesa.setText("Liberar Mesa");
        liberarMesa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                liberarMesaActionPerformed(evt);
            }
        });
        menu.add(liberarMesa);

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        setPreferredSize(new java.awt.Dimension(145, 100));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
        });

        lbNumMesa.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lbNumMesa.setText("000");

        lblStatus.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblStatus.setText("STATUS:");

        lbStatusMesa.setBackground(new java.awt.Color(0, 204, 0));
        lbStatusMesa.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbStatusMesa.setForeground(new java.awt.Color(51, 153, 0));
        lbStatusMesa.setText("LIBERADA");

        lblTipoMedida.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblTipoMedida.setText("VOLUME");

        lbTotalMesa.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbTotalMesa.setText("0,00");

        jLabel64.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel64.setText("MINUTA:");

        lblTipo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/auto.png"))); // NOI18N
        lblTipo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lblPedido.setBackground(new java.awt.Color(51, 51, 51));
        lblPedido.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPedido.setForeground(new java.awt.Color(255, 255, 255));
        lblPedido.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPedido.setText("Pedido");
        lblPedido.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTipoMedida)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbTotalMesa)
                        .addGap(10, 10, 10)
                        .addComponent(lblTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblStatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbStatusMesa))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel64)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbNumMesa)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(lblPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbNumMesa)
                            .addComponent(jLabel64))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblStatus)
                            .addComponent(lbStatusMesa))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTipoMedida)
                            .addComponent(lbTotalMesa)))
                    .addComponent(lblTipo))
                .addGap(2, 2, 2)
                .addComponent(lblPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        setBackground(Color.orange);
    }//GEN-LAST:event_formMouseEntered

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        setBackground(Color.white);
    }//GEN-LAST:event_formMouseExited

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        if (evt.getButton() == 3) {
            // menu.show(this, evt.getX(), evt.getY());
            try {
                getMinuta(lbNumMesa.getText());
                if (this.minuta != null) {
                    if (minuta.getUsu_codlan() > 0) {
                        FatPedidoFaturar.getMinutaSelecionada(lbNumMesa.getText() + " " + minuta.getUsu_nommin());
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(IntegrarPesos.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (evt.getButton() == 1) {
            try {
                getMinuta(lbNumMesa.getText());
                if (this.minuta != null) {
                    if (minuta.getUsu_codlan() > 0) {
                        frmMinutasExpedicaoGerar sol = new frmMinutasExpedicaoGerar();
                        MDIFrame.add(sol, true);
                        sol.setMaximum(true); // executa maximizado
                        //  sol.setSize(800, 500);
                        sol.setPosicao();
                        sol.setRecebePalavraFaturar(this, minuta);
                    }
                }

            } catch (PropertyVetoException ex) {
                Logger.getLogger(pnPedidoPadrao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(pnPedidoPadrao.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }//GEN-LAST:event_formMouseClicked

    private void analisarMinutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analisarMinutaActionPerformed
        try {
            if (fmtHora == null) {
                fmtHora = new MaskFormatter("##:##");
            }
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        JFormattedTextField dataFormatada = new JFormattedTextField(fmtHora);
        JLabel rotulo = new JLabel("<html>Digite a Hora<br>da Reserva:</html>");
        JPanel tela = new JPanel();
        tela.setLayout(new BoxLayout(tela, BoxLayout.X_AXIS));
        tela.add(rotulo);
        tela.add(dataFormatada);

        JOptionPane.showMessageDialog(null, tela, "PubManager.Soft", JOptionPane.PLAIN_MESSAGE);

        dataFormatada.setText(dataFormatada.getText().replace(" ", "9"));
        System.out.println(dataFormatada.getText());

        if (Integer.valueOf(dataFormatada.getText().substring(0, 2)) <= 23 && Integer.valueOf(dataFormatada.getText().substring(3, 5)) <= 59) {
            horaReservada = dataFormatada.getText();
            atualizaHora();
            setaStatus("Reservada");
        } else {
            JOptionPane.showMessageDialog(null, "Horario Inválido", "PubManager.Soft", JOptionPane.WARNING_MESSAGE);
        }


    }//GEN-LAST:event_analisarMinutaActionPerformed

    private void liberarMesaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_liberarMesaActionPerformed
        if (Float.valueOf(lbTotalMesa.getText().replaceAll(",", ".")) <= 0) {
            if (JOptionPane.showConfirmDialog(null, "Deseja Liberar Mesa?", "PubManager.Soft",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                fimReserva();
            }
        }
    }//GEN-LAST:event_liberarMesaActionPerformed

    private void statusMesaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusMesaActionPerformed
//        if (getStatus().equalsIgnoreCase("Disponível")){
//            JOptionPane.showMessageDialog(null, "Mesa Disponível","PubManager.Soft",JOptionPane.INFORMATION_MESSAGE);
//        }else if (getStatus().equalsIgnoreCase("Ocupada")){
//            int aux = 0;
//            if (mesa.getTabelaAux() != null){
//                aux = mesa.getTabelaAux().getRowCount();
//            }
//            JOptionPane.showMessageDialog(null, "Mesa "+ lbNumMesa.getText()+"\nValor Gasto: "+lbTotalMesa.getText()+
//                    "\nItens Vendidos: "+aux,
//                    "PubManager.Soft",JOptionPane.INFORMATION_MESSAGE);
//        }else if (getStatus().equalsIgnoreCase("Reservada")){
//            JOptionPane.showMessageDialog(null, "Mesa Reservada\npara ás: "+ horaReservada+" !","PubManager.Soft",JOptionPane.INFORMATION_MESSAGE);
//        }
    }//GEN-LAST:event_statusMesaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem analisarMinuta;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel lbNumMesa;
    private javax.swing.JLabel lbStatusMesa;
    private javax.swing.JLabel lbTotalMesa;
    private javax.swing.JLabel lblPedido;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTipoMedida;
    private javax.swing.JMenuItem liberarMesa;
    private javax.swing.JPopupMenu menu;
    private javax.swing.JMenuItem statusMesa;
    // End of variables declaration//GEN-END:variables
}
