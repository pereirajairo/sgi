/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.dao.ClienteDAO;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.SucataDAO;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.UtilDatas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class Sucatas extends InternalFrame {

    private Sucata sucata;
    private List<Sucata> listSucata = new ArrayList<Sucata>();

    private List<SucataMovimento> listSucataMovimento = new ArrayList<SucataMovimento>();
    private SucataMovimentoDAO sucataMovimentoDAO;

    private SucataDAO sucataDAO;

    private Cliente cliente;
    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;

    public Sucatas() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Gestão de sucatas"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (sucataDAO == null) {
                sucataDAO = new SucataDAO();
            }

            if (sucataMovimentoDAO == null) {
                sucataMovimentoDAO = new SucataMovimentoDAO();
            }

            txtDatIni.setDate(utilDatas.retornaDataIni(new Date()));
            txtDatFim.setDate(utilDatas.retornaDataFim(new Date()));

            pegarDataDigitada();
            // pesquisarRegistro("DATA", " and usu_datger >= '" + datIni + "' and usu_datger <='" + datFim + "'");
            getListarMovimento("DATA", " and usu_datger >= '" + datIni + "' and usu_datger <='" + datFim + "'");
            preencherComboFilial(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void preencherComboFilial(Integer id) throws SQLException, Exception {
        FilialDAO filialDAO = new FilialDAO();
        List<Filial> listFilial = new ArrayList<Filial>();
        String cod;
        String des;
        String desger;
        txtFilial.removeAllItems();

        listFilial = filialDAO.getFilias("", " and codemp = 1 ");
        if (listFilial != null) {
            for (Filial filial : listFilial) {
                cod = filial.getFilial().toString();
                des = filial.getRazao_social();
                desger = cod + " - " + des;
                txtFilial.addItem(desger);
            }
        }
    }

    private void pegarDataDigitada() throws ParseException {

        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());

        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
    }

    private void limparTela() {
        txtCliente.setText("0");
        txtNomeCliente.setText("");
        txtGrupo.setText("");
    }

    public void getListarMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        PESQUISA += " and usu_debcre not in '0 - REMOVIDO'";
        listSucataMovimento = this.sucataMovimentoDAO.getSucatasMovimentoAgrupado(PESQUISA_POR, PESQUISA);
        if (listSucataMovimento != null) {
            carregarTabelaMovimento(false);

        }
    }

    public void carregarTabelaMovimento(boolean selecionar) throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon IndIcon = getImage("/images/bateriaindu.png");

        String situacao = "";
        double pesoG = 0.0;
        double pesoD = 0.0;
        double pesoC = 0.0;
        double pesoP = 0.0;
        for (SucataMovimento suc : listSucataMovimento) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTableCarga.getColumnModel();
            Sucatas.JTableRenderer renderers = new Sucatas.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            switch (suc.getAutomoto()) {
                case "IND":
                    pesoG += suc.getPesosucata();
                    linha[0] = IndIcon;
                    situacao = "SUCATA DE INDUSTRIALIZAÇÃO";
                    break;
                case "AUT":
                    pesoP += suc.getPesosucata();
                    linha[0] = AutIcon;
                    situacao = "SUCATA DE AUTO";
                    break;
                case "MOT":
                    pesoD += suc.getPesosucata();
                    linha[0] = MotIcon;
                    situacao = "SUCATA DE MOTO";
                    break;

                default:
                    break;
            }
            linha[1] = suc.getCodigolancamento();
            linha[2] = suc.getCliente();
            linha[3] = suc.getCadCliente().getNome();
            linha[4] = suc.getMes();
            linha[5] = suc.getAno();
            linha[6] = suc.getDatageracaoS();

            linha[7] = suc.getFilial();
            linha[8] = situacao;
            linha[9] = suc.getSequencia();
            linha[10] = suc.getPedido();
            linha[11] = suc.getFilialsucata();
            modeloCarga.addRow(linha);
        }

        formatarCampoPeso(pesoG, "G");
        formatarCampoPeso(pesoD, "D");
        formatarCampoPeso(pesoC, "C");
        formatarCampoPeso(pesoP + pesoG, "P");

    }

    private void formatarCampoPeso(double peso, String tipo) {

        String pesoS = FormatarPeso.mascaraPorcentagem(peso, FormatarPeso.PORCENTAGEM);

    }

    private void pesquisarRegistro(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {

        getListarMovimento(PESQUISA_POR, PESQUISA);
    }

    public void retornarSucata() {
        try {

            getListarMovimento("DATA", " and usu_datger >= '" + datIni + "' and usu_datger <='" + datFim + "'");
            limparTela();
        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retornarCliente(String PESQUISA_POR, String PESQUISA, Cliente cliente) throws Exception {
        this.cliente = new Cliente();
        this.cliente = cliente;
        txtCliente.setText(cliente.getCodigo().toString());
        txtNomeCliente.setText(cliente.getNome());
        txtGrupo.setText(cliente.getGrupocodigo() + " - " + cliente.getGruponome());
        getCliente();
        pegarDataDigitada();
        pesquisarRegistro("DATA", " and usu_datger >= '" + datIni + "\n"
                + "' and usu_datger <='" + datFim + "'\n"
                + " and usu_codcli = " + cliente.getCodigo() + "");

    }

    private boolean AddRegEtq;

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(9).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(10).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(11).setCellRenderer(direita);
        jTableCarga.setRowHeight(40);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTableCarga.setAutoCreateRowSorter(true);
        //jTableCarga.setAutoResizeMode(0);

    }

    private void getCliente() throws SQLException {
        ClienteDAO dao = new ClienteDAO();
        btnManutencao.setEnabled(false);

        this.cliente = dao.getClienteSucata("CLI", " and codcli = " + txtCliente.getText());
        if (cliente != null) {
            if (cliente.getCodigo() > 0) {
                txtNomeCliente.setText(cliente.getNome());
                if ((cliente.getGrupocodigo() == null) || (cliente.getGrupocodigo().equals("0"))) {
                    cliente.setGruponome("Não informado");
                    cliente.setGrupocodigo("0");
                }
                btnManutencao.setEnabled(true);

                txtGrupo.setText(cliente.getGrupocodigo() + " - " + cliente.getGruponome());

            }
        }

    }

    private void getSucataCliente(String acao) {
        if (!txtCliente.getText().isEmpty()) {
            try {
                getCliente();
                if (cliente != null) {
                    if (cliente.getCodigo() > 0) {
                        pegarDataDigitada();
                        pesquisarRegistro("CLI", " and usu_datger >= '" + datIni + "\n"
                                + "' and usu_datger <='" + datFim + "'\n"
                                + " and usu_codcli = " + txtCliente.getText() + "");
                        btnManutencao.setEnabled(true);

                    } else {
                        JOptionPane.showMessageDialog(null, "Cliente não encontrado");
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
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
    private static Color COR_DEBITO = new Color(255, 0, 0);
    private static Color COR_CREDITO = new Color(66, 111, 66);

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
            if ("ABERTA".equals(str)) {
                setForeground(Color.RED);
            } else {
                setForeground(Color.WHITE);
                setBackground(COR_CREDITO);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnFiltrar = new javax.swing.JButton();
        btnFiltrar1 = new javax.swing.JButton();
        btnFiltrar2 = new javax.swing.JButton();
        btnFiltrar3 = new javax.swing.JButton();
        txtFilial = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        txtNomeCliente = new org.openswing.swing.client.TextControl();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtGrupo = new org.openswing.swing.client.TextControl();
        jLabel7 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        txtCliente = new org.openswing.swing.client.NumericControl();
        btnSucCli = new javax.swing.JButton();
        btnManutencao = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Id", "Cliente", "Nome", "Mês", "Ano", "Ult. Movimento", "Filial Origem", "Situação", "Mov", "Ult. Pedido", "Filial Destino"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCarga.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCargaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableCarga);
        if (jTableCarga.getColumnModel().getColumnCount() > 0) {
            jTableCarga.getColumnModel().getColumn(0).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(120);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(120);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(120);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(100);
        }

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnFiltrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/carros.png"))); // NOI18N
        btnFiltrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar1ActionPerformed(evt);
            }
        });

        btnFiltrar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png"))); // NOI18N
        btnFiltrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar2ActionPerformed(evt);
            }
        });

        btnFiltrar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        btnFiltrar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar3ActionPerformed(evt);
            }
        });

        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtFilial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "9 - SELECIONE A FILIAL" }));
        txtFilial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFilialMouseClicked(evt);
            }
        });
        txtFilial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialActionPerformed(evt);
            }
        });

        jLabel1.setText("Filial");

        txtNomeCliente.setEnabled(false);
        txtNomeCliente.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Cliente");

        jLabel4.setText("Razão Social");

        jLabel5.setText("Data Inicial");

        jLabel6.setText("Data Final");

        txtGrupo.setEnabled(false);
        txtGrupo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel7.setText("Grupo");

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/chart_organisation.png"))); // NOI18N
        jButton2.setText("Filtrar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        jButton3.setText("Origem");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        btnSucCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnSucCli.setText("Filtrar");
        btnSucCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSucCliActionPerformed(evt);
            }
        });

        btnManutencao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-e-fechar16x16.png"))); // NOI18N
        btnManutencao.setText("Sucata");
        btnManutencao.setEnabled(false);
        btnManutencao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManutencaoActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jButton4.setText("Destino");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnFiltrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnFiltrar2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnFiltrar3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton1)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnSucCli)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(txtNomeCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(txtFilial, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6)))
                    .addComponent(jLabel7)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnFiltrar1, btnFiltrar2, btnFiltrar3});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(5, 5, 5)
                            .addComponent(jLabel6))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel1)))
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(4, 4, 4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(btnSucCli)
                    .addComponent(txtNomeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFiltrar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnFiltrar3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar))
                .addContainerGap(385, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar1, btnFiltrar2, btnFiltrar3, btnManutencao});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar, btnSucCli, jButton1, jButton2, jButton3, jButton4, txtFilial, txtGrupo});

        jTabbedPane1.addTab("Sucata", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 899, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String codigoFilial;

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();
        txtCliente.setText(jTableCarga.getValueAt(linhaSelSit, 2).toString());
        codigoFilial = jTableCarga.getValueAt(linhaSelSit, 7).toString().trim();

        if (!txtCliente.getText().isEmpty()) {
            try {
                getCliente();
                // if (evt.getClickCount() == 2) {
                //getSucataCliente("S");
                //  }
            } catch (SQLException ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            }

        }


    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        try {
            pegarDataDigitada();
            pesquisarRegistro("DATA", " and usu_datger >= '" + datIni + "' and usu_datger <='" + datFim + "'");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnFiltrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar1ActionPerformed
        limparTela();
        try {

            pegarDataDigitada();

            pesquisarRegistro("TIPO",
                    " and usu_autmot = 'AUT' "
                    + "and usu_datger >= '" + datIni + "'"
                    + " and usu_datger <='" + datFim + "'");
        } catch (ParseException ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnFiltrar1ActionPerformed

    private void btnFiltrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar2ActionPerformed
        limparTela();
        try {
            pegarDataDigitada();

            pesquisarRegistro("TIPO",
                    " and usu_autmot = 'MOT' "
                    + "and usu_datger >= '" + datIni + "'"
                    + " and usu_datger <='" + datFim + "'");
        } catch (ParseException ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnFiltrar2ActionPerformed

    private void btnFiltrar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar3ActionPerformed
        limparTela();
        try {
            pegarDataDigitada();

            pesquisarRegistro("TIPO",
                    " and usu_autmot = 'IND' "
                    + "and usu_datger >= '" + datIni + "'"
                    + " and usu_datger <='" + datFim + "'");
        } catch (ParseException ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnFiltrar3ActionPerformed

    private void txtFilialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialActionPerformed
//

    }//GEN-LAST:event_txtFilialActionPerformed

    private void txtFilialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFilialMouseClicked
        //
    }//GEN-LAST:event_txtFilialMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (!txtFilial.getSelectedItem().toString().isEmpty() || txtFilial.getSelectedItem().toString() != null) {
            try {
                pegarDataDigitada();
                String filial = txtFilial.getSelectedItem().toString();
                int index = filial.indexOf("-");
                String filialSelecionada = filial.substring(0, index);

                pesquisarRegistro("FILIAL",
                        " and usu_codfil = '" + filialSelecionada + "' "
                        + "and usu_datger >= '" + datIni + "'"
                        + " and usu_datger <='" + datFim + "'");
            } catch (ParseException ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
//        try {
//            Clientes sol = new Clientes();
//            MDIFrame.add(sol, true);
//
//            //  sol.setSize(400, 200);
//            sol.setPosicao();
//            sol.setMaximum(true); // executa maximizado
//            sol.setRecebePalavra(this, "");
//        } catch (Exception ex) {
//            Logger.getLogger(IntegrarPesosRegistrar.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (cliente != null) {
            if (!cliente.getGrupocodigo().isEmpty()) {
                try {
                    pegarDataDigitada();
                    pesquisarRegistro("CLI", " and usu_datger >= '" + datIni + "\n"
                            + "' and usu_datger <='" + datFim + "'\n"
                            + " and cli.codgre = " + cliente.getGrupocodigo() + "");
                } catch (Exception ex) {
                    Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        getSucataCliente("S");
    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnSucCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSucCliActionPerformed
        getSucataCliente("S");
    }//GEN-LAST:event_btnSucCliActionPerformed

    private void btnManutencaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManutencaoActionPerformed

        if (this.cliente != null) {
            if (this.cliente.getCodigo() > 0) {
                try {
                    SucatasManutencao sol = new SucatasManutencao();
                    MDIFrame.add(sol, true);
                    sol.setPosicao();
                    sol.setMaximum(true); // executa maximizado 
                    sol.setRecebePalavra(this, this.codigoFilial, this.cliente);
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnManutencaoActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       if (!txtFilial.getSelectedItem().toString().isEmpty() || txtFilial.getSelectedItem().toString() != null) {
            try {
                pegarDataDigitada();
                String filial = txtFilial.getSelectedItem().toString();
                int index = filial.indexOf("-");
                String filialSelecionada = filial.substring(0, index);

                pesquisarRegistro("FILIAL",
                        " and usu_codfilsuc = '" + filialSelecionada + "' "
                        + "and usu_datger >= '" + datIni + "'"
                        + " and usu_datger <='" + datFim + "'");
            } catch (ParseException ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar1;
    private javax.swing.JButton btnFiltrar2;
    private javax.swing.JButton btnFiltrar3;
    private javax.swing.JButton btnManutencao;
    private javax.swing.JButton btnSucCli;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private org.openswing.swing.client.NumericControl txtCliente;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private javax.swing.JComboBox<String> txtFilial;
    private org.openswing.swing.client.TextControl txtGrupo;
    private org.openswing.swing.client.TextControl txtNomeCliente;
    // End of variables declaration//GEN-END:variables
}
