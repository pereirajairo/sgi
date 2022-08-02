/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.dao.SucataDAO;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class SucatasFechamento extends InternalFrame {
    
    private Sucata sucata;
    private List<Pedido> listPedido = new ArrayList<Pedido>();
    private SucataDAO sucataDAO;
    private SucataMovimentoDAO sucataMovimentoDAO;
    private SucataMovimento sucataMovimento;
    private SucatasManutencao veioCampo;
    private UtilDatas utilDatas;
    
    private String datI;
    private String datF;
    private String processo;
    
    public SucatasFechamento() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Fechamento "));
            this.setSize(800, 500);
            
            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (sucataDAO == null) {
                this.sucataDAO = new SucataDAO();
            }
            
            if (sucataMovimentoDAO == null) {
                sucataMovimentoDAO = new SucataMovimentoDAO();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }
    private double pesopedido = 0;
    private double rentabilidade = 0;
    
    public void setRecebePalavra(SucatasManutencao veioInput, SucataMovimento sucataMovimento, Filial filial,
            String processo, Double pesopedido) throws Exception {
        this.veioCampo = veioInput;
        
        this.sucataMovimento = sucataMovimento;
        if (sucataMovimento != null) {
            if (sucataMovimento.getCodigolancamento() > 0) {
                String msg = "";
                this.processo = processo;
                this.pesopedido = pesopedido;
                txtPedido.setText(sucataMovimento.getPedido().toString());
                
                txtSucata.setText(sucataMovimento.getSucata());
                txtOrdemCompra.setText(sucataMovimento.getOrdemcompra().toString());
                txtPesoSucataOrdem.setValue(sucataMovimento.getPesoordemcompra());
                txtFornecedor.setText(sucataMovimento.getCliente().toString());
                
                txtNotaEntrada.setText(sucataMovimento.getNotaentrada().toString());
                
                txtPesoNotaEntrada.setValue(sucataMovimento.getPesoordemcompra());
                double pesorecebido = sucataMovimento.getPesorecebido();
                
                if (pesopedido < 0) {
                    pesopedido = pesopedido * -1;
                }
                
                txtRentabilidade.setValue(sucataMovimento.getPercentualrendimento());
                double pesogerado = 0;
                double pesosucata = sucataMovimento.getPesosucata();
                
                if (sucataMovimento.getPercentualrendimento() > 0) {
                    if (!sucataMovimento.getDebitocredito().equals("4 - CREDITO")) {
                        pesogerado = pesopedido / (sucataMovimento.getPercentualrendimento() / 100);
                    } else {
                        pesopedido = sucataMovimento.getPesoordemcompra() * (sucataMovimento.getPercentualrendimento() / 100);
                        pesogerado = pesopedido / (sucataMovimento.getPercentualrendimento() / 100);
                    }
                    
                } else {
                    pesogerado = sucataMovimento.getPesosucata();
                }
                
                txtPesoPedido.setValue(pesopedido);
                txtPesoSucata.setValue(pesogerado);
                txtPesoRecebido.setValue(pesorecebido);
                double pesopedidonew = sucataMovimento.getPesorecebido() * (sucataMovimento.getPercentualrendimento() / 100);
                
                lblPesoNew.setText(String.valueOf(FormatarNumeros.ConverterDouble(pesopedidonew)));
                double pesodif = sucataMovimento.getPesorecebido() - sucataMovimento.getPesoordemcompra();
                txtPesoDiferenca.setValue(pesodif);
                
                if (pesodif == 0) {
                    msg += "Atenção: A Sucata recebida esta ok";
                } else {
                    if (pesodif < 0) {
                        msg += "Atenção: A Sucata recebida esta menor do que a programada.  "
                                + "Não atende o pedido do cliente: \n"
                                + "Peso Pedido: " + sucataMovimento.getPesosucata() + "\n"
                                + "Peso Calculado:  " + pesopedidonew;
                    } else {
                        msg += "Atenção: A Sucata recebida esta maior do que a programada. \n "
                                + "Atende o pedido do cliente e vai gerar saldo de sucata ";
                    }
                }
                
                txtMensagem.setText(msg);
               
                
            }
            
        }
        
    }
    
    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }
    
    private ImageIcon getImage(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return (new ImageIcon(url));
        } else {
            return null;
            
        }
    }
    
    private void calcularValor() {
        pesopedido = txtPesoPedido.getDouble();
        if (pesopedido != 0) {
            rentabilidade = txtRentabilidade.getDouble();
            double pesosucata = 0;
            if (rentabilidade > 0) {
                pesosucata = pesopedido / (rentabilidade / 100);
            } else {
                pesosucata = pesopedido;
            }
            txtPesoSucata.setValue(pesosucata);
            txtPesoSucataOrdem.setValue(pesosucata);
            txtPesoNotaEntrada.setValue(pesosucata);
            String pesopedidoS = FormatarPeso.mascaraPorcentagem(pesopedido, FormatarPeso.PORCENTAGEM);
            lblPesoNew.setText(pesopedidoS);
            txtRentabilidade.requestFocus();
            txtMensagem.setText("SIMULAÇÃO REALIZADA COM SUCESSO\n"
                    + "GERAR PEDIDO COM " + pesopedidoS + " KG");
        }
    }
    
    private void calcularValorRecebimento() {
        double peso = txtPesoNotaEntrada.getDouble();
        double pesorecebido = txtPesoRecebido.getDouble();
        double dif = pesorecebido - peso;
        txtPesoDiferenca.setValue(dif);
        double pesonew = 0;
        if (txtRentabilidade.getDouble() > 0) {
            
            pesonew = pesorecebido * (txtRentabilidade.getDouble() / 100);
        } else {
            pesonew = pesorecebido;
        }
        
        lblPesoNew.setText(String.valueOf(pesonew));
        txtMensagem.setText("SIMULAÇÃO REALIZADA COM SUCESSO\n"
                + "GERAR PEDIDO COM " + pesonew + " KG");
    }
    
    private void habilitar(boolean acao) {
        txtPesoPedido.setEnabled(acao);
        txtRentabilidade.setEnabled(acao);
        txtPesoRecebido.setEnabled(acao);
        //   txtRentabilidade.setValue(this.rentabilidade);
        txtPesoPedido.requestFocus();
    }
    
    public void removerRegistro(boolean retorno, String info) throws SQLException {
        if (retorno) {
            if (this.sucataMovimento != null) {
                if (sucataMovimento.getSequencia() > 0) {
                    sucataMovimento.setDebitocredito("0 - REMOVIDO");
                    sucataMovimento.setObservacaomovimento(info);
                    if (!this.sucataMovimentoDAO.remover(sucataMovimento)) {
                        
                    }
                    
                }
            }
        }
    }
    
    public class JTableRenderer extends DefaultTableCellRenderer {
        
        protected void setValue(Object value) {
            if (value instanceof ImageIcon) {
                if (value != null) {
                    ImageIcon d = (ImageIcon) value;
                    setIcon(d);
                } else {
                    setText("");
                    setIcon(null);
                }
            } else {
                super.setValue(value);
            }
        }
    }
    private static Color COR_ESTOQUE_HFF = new Color(66, 111, 66);
    
    public class ColorirRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(
                JTable jTableCarga, Object value, boolean selected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(jTableCarga, value, selected, hasFocus, row, col);
            setBackground(Color.WHITE);
            String str = (String) value;
            if (str == null) {
                str = "";
            }
            if ("NC".equals(str)) {
                setForeground(Color.RED);
            } else {
                setForeground(Color.WHITE);
                setBackground(COR_ESTOQUE_HFF);
            }

            //   setBackground(COR_ESTOQUE_HFF);
            return this;
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

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtPedido = new org.openswing.swing.client.TextControl();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPesoPedido = new org.openswing.swing.client.NumericControl();
        txtRentabilidade = new org.openswing.swing.client.NumericControl();
        txtPesoSucata = new org.openswing.swing.client.NumericControl();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtMensagem = new javax.swing.JTextArea();
        btnSimular = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtOrdemCompra = new org.openswing.swing.client.TextControl();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtFornecedor = new org.openswing.swing.client.NumericControl();
        txtPesoSucataOrdem = new org.openswing.swing.client.NumericControl();
        txtSucata = new org.openswing.swing.client.TextControl();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtNotaEntrada = new org.openswing.swing.client.TextControl();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtPesoNotaEntrada = new org.openswing.swing.client.NumericControl();
        txtPesoDiferenca = new org.openswing.swing.client.NumericControl();
        txtPesoRecebido = new org.openswing.swing.client.NumericControl();
        lblPesoNew = new javax.swing.JLabel();
        btnSair = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Pedido [Sucata]"));

        jLabel1.setText("Pedido");

        txtPedido.setEnabled(false);
        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel2.setText("Peso Pedido");

        jLabel3.setText("% Rent");

        jLabel4.setText("Peso Sucata");

        txtPesoPedido.setDecimals(2);
        txtPesoPedido.setEnabled(false);
        txtPesoPedido.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesoPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoPedidoActionPerformed(evt);
            }
        });

        txtRentabilidade.setDecimals(2);
        txtRentabilidade.setEnabled(false);
        txtRentabilidade.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtRentabilidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRentabilidadeActionPerformed(evt);
            }
        });

        txtPesoSucata.setDecimals(2);
        txtPesoSucata.setEnabled(false);
        txtPesoSucata.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txtPesoPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRentabilidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPesoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addGap(23, 23, 23)
                                .addComponent(txtPesoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addGap(23, 23, 23)
                        .addComponent(txtRentabilidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3))
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(txtPesoSucata, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtMensagem.setColumns(20);
        txtMensagem.setRows(5);
        txtMensagem.setBorder(javax.swing.BorderFactory.createTitledBorder("Info"));
        txtMensagem.setEnabled(false);
        jScrollPane1.setViewportView(txtMensagem);

        btnSimular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        btnSimular.setText("Simulação");
        btnSimular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimularActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordem Compra [Sucata]"));

        jLabel5.setText("Ordem");

        txtOrdemCompra.setEnabled(false);
        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel6.setText("Sucata");

        jLabel7.setText("Forncedor");

        jLabel8.setText("Peso Ordem");

        txtFornecedor.setEnabled(false);
        txtFornecedor.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtPesoSucataOrdem.setDecimals(2);
        txtPesoSucataOrdem.setEnabled(false);
        txtPesoSucataOrdem.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N

        txtSucata.setEnabled(false);
        txtSucata.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(13, 13, 13)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFornecedor, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txtPesoSucataOrdem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSucata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addGap(23, 23, 23)
                                .addComponent(txtSucata, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6))
                        .addGap(23, 23, 23)
                        .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7))
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(txtPesoSucataOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Recebimento [Sucata]"));

        jLabel9.setText("Nota");

        txtNotaEntrada.setEnabled(false);
        txtNotaEntrada.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel10.setText("Recebido");

        jLabel11.setText("Peso OC");

        jLabel12.setText("Diferença");

        txtPesoNotaEntrada.setDecimals(2);
        txtPesoNotaEntrada.setEnabled(false);
        txtPesoNotaEntrada.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtPesoDiferenca.setDecimals(2);
        txtPesoDiferenca.setEnabled(false);
        txtPesoDiferenca.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N

        txtPesoRecebido.setDecimals(2);
        txtPesoRecebido.setEnabled(false);
        txtPesoRecebido.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesoRecebido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoRecebidoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtPesoRecebido, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE))
                    .addComponent(txtPesoNotaEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPesoDiferenca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNotaEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))
                                .addGap(23, 23, 23)
                                .addComponent(txtPesoRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel10))
                        .addGap(23, 23, 23)
                        .addComponent(txtPesoNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11))
                .addGap(23, 23, 23)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12)
                    .addComponent(txtPesoDiferenca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblPesoNew.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        lblPesoNew.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPesoNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        lblPesoNew.setText("PESO:");
        lblPesoNew.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
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
                        .addComponent(lblPesoNew, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSimular, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPesoNew, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSimular)
                    .addComponent(btnSair))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel2, jPanel3, jPanel4});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnSair, btnSimular, lblPesoNew});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimularActionPerformed
        habilitar(true);

    }//GEN-LAST:event_btnSimularActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void txtPesoPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoPedidoActionPerformed
        
        calcularValor();
    }//GEN-LAST:event_txtPesoPedidoActionPerformed

    private void txtRentabilidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRentabilidadeActionPerformed
        
        calcularValor();
        txtPesoRecebido.requestFocus();
    }//GEN-LAST:event_txtRentabilidadeActionPerformed

    private void txtPesoRecebidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoRecebidoActionPerformed
        calcularValorRecebimento();
    }//GEN-LAST:event_txtPesoRecebidoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSimular;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPesoNew;
    private org.openswing.swing.client.NumericControl txtFornecedor;
    private javax.swing.JTextArea txtMensagem;
    private org.openswing.swing.client.TextControl txtNotaEntrada;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.NumericControl txtPesoDiferenca;
    private org.openswing.swing.client.NumericControl txtPesoNotaEntrada;
    private org.openswing.swing.client.NumericControl txtPesoPedido;
    private org.openswing.swing.client.NumericControl txtPesoRecebido;
    private org.openswing.swing.client.NumericControl txtPesoSucata;
    private org.openswing.swing.client.NumericControl txtPesoSucataOrdem;
    private org.openswing.swing.client.NumericControl txtRentabilidade;
    private org.openswing.swing.client.TextControl txtSucata;
    // End of variables declaration//GEN-END:variables
}
