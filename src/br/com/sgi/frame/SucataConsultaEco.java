/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Filial;
import br.com.sgi.bean.SucataEco;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.SucataEcoDao;
import br.com.sgi.dao.UsuarioERPDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.AbrangeciaSapiens;

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
import static br.com.sgi.util.ConversaoHoras.converterMinutosHora;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author jairosilva
 */
public final class SucataConsultaEco extends InternalFrame {

    private SucataEco sucataEco;
    private Usuario usuario;

    private List<Filial> listFilial = new ArrayList<Filial>();
    private List<SucataEco> listSucataEco = new ArrayList<SucataEco>();
    private List<Usuario> listAbragencia = new ArrayList<Usuario>();

    private FilialDAO filialDAO;
    private UsuarioERPDAO usuarioERPDAO;
    private SucataEcoDao sucataEcoDao;
    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;

    private static final Color COR_CON = new Color(102, 255, 102);
    private static final Color COR_CON_PRO = new Color(255, 0, 0);
    private static final Color COR_CON_PEN = new Color(51, 51, 255);

    private String PESQUISA;
    private String PESQUISA_POR;

    private String PROCESSO;
    private String COMPLEMENTO;
    private String codigoFilial;
    private String FanFilial;
    private boolean AddRegEtq;
    private String codigolancamento;

    private String sCodEmp;
    private String sCodFil;
    private String sCodSnf;
    private String sNumNfv;
    private String sNumOcp;
    private String sPesado;
    private Integer sUsuario;
    private String abreFilial;
    private String PesquisaFilial;
    private Integer nContEntrada = 0;
    private String sqlCheckBox;
    private String sqlData;
    private String botaoPesagem = "";
    FormatarNumeros formatarNumeros = new FormatarNumeros();

    public SucataConsultaEco() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Gestão de Pesos da Fabrica"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (filialDAO == null) {
                filialDAO = new FilialDAO();
            }
            if (sucataEcoDao == null) {
                sucataEcoDao = new SucataEcoDao();
            }
            if (usuarioERPDAO == null) {
                usuarioERPDAO = new UsuarioERPDAO();
            }

            txtDatIni.setDate(this.utilDatas.retornaDataIni(new Date()));
            txtDatFim.setDate(new Date());
            pegarDataDigitada();
            preencherComboBalanca(0);
            btnPesar.setEnabled(false);

            verificaCheckbox();
            getListar("", "");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }

    }

    public void RetornarCampoAtualiza() throws Exception {
        botaoPesagem =" ";
        verificaCheckbox();
        pegarDataDigitada();
        getListar("", "");
    }

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        UsuarioERPDAO dao = new UsuarioERPDAO();

        usuario = dao.getUsuario(Menu.username.toLowerCase());
        sUsuario = usuario.getId();
        return usuario;
    }

    private void verificaCheckbox() {
        try {
            String sql = "";
            if (optNaoPesados.isSelected()) {
                sql = "AND USU_TSUCMOV.USU_SITPED IS NULL   AND USU_TSUCMOV.USU_DEBCRE ='3 - DEBITO'\n"
                        + " AND USU_TSUCMOV.USU_AUTMOT NOT IN('IND') AND  USU_TSUCMOV.USU_SITSUC NOT IN ('MANUAL') ";

                sqlCheckBox = " ";
                sqlCheckBox = sql;

            } else if (optPesados.isSelected()) {
                sql = " AND USU_DEBCRE ='4 - CREDITO' AND USU_AUTMOT NOT IN('IND') AND  USU_SITSUC NOT IN ('MANUAL')  ";
                sqlCheckBox = " ";
                sqlCheckBox = sql;
            } else {

            }
            //  getListar("", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void novoRegistro(String PROCESSO, String string) throws PropertyVetoException, Exception {
        SucataEcoLancamento sol = new SucataEcoLancamento();
        MDIFrame.add(sol, true);

        sol.setMaximum(false); // executa maximizado 
        sol.setSize(400, 600);
        sol.setPosicao();
        sol.setRecebePalavra(this, this.sucataEco,this.botaoPesagem);
    }

    private void getNota(String PESQUISA_POR, String PESQUISA) throws SQLException {
        sucataEco = new SucataEco();

        sucataEco = sucataEcoDao.getSucataEco(PESQUISA_POR, PESQUISA);
        btnPesar.setEnabled(false);
        if (sucataEco != null) {
            if (sucataEco.getNota() > 0) {
                if (!sPesado.equals("PESADO")) {
                    // btnPesar.setEnabled(true);
                } else {
                    //JOptionPane.showMessageDialog(null, "Nota já vinculada com a sucata.",
                    //     "Atenção", JOptionPane.WARNING_MESSAGE);
                    //  btnPesar.setEnabled(false);

                }
            }
        }
    }

    public void preencherComboBalanca(Integer id) throws SQLException {

        List<Filial> listFilial = new ArrayList<Filial>();
        String desger;
        comboBalanca.removeAllItems();
        getUsuarioLogado();
        PesquisaFilial = AbrangeciaSapiens.AbrangeciaSapiensFilial(sUsuario.toString());
        listFilial = filialDAO.getFilias("", " AND CODEMP = 1 AND codfil in (" + PesquisaFilial + ") AND usu_consuc ='S'");

        if (listFilial != null) {
            for (Filial filial : listFilial) {
                codigoFilial = filial.getFilial().toString();
                FanFilial = filial.getRazao_social();
                desger = codigoFilial + " - " + FanFilial;
                comboBalanca.addItem(desger);
            }
        }

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    private void pegarDataDigitada() throws ParseException {
        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
        sqlData = " ";
        sqlData = " and USU_DATGER between '" + datIni + "' and '" + datFim + "' ";
    }

    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        pegarDataDigitada();
        String codigo = comboBalanca.getSelectedItem().toString();

        String sql = " ";
        if (txtCliente.getText().isEmpty()) {
            sql += " ";
        } else {
            sql = " and USU_TSUCMOV.usu_codcli = '" + txtCliente.getText() + "' ";
        }
        if (txtPedido.getText().isEmpty()) {
            sql += " ";
        } else {
            sql = " and USU_TSUCMOV.usu_numped = '" + txtPedido.getText() + "' ";
        }
        if (txtNota.getText().isEmpty()) {
            sql += " ";
        } else {
            sql += " and USU_TSUCMOV.usu_numnfv = '" + txtNota.getText() + "' ";
        }
        if (txtOC.getText().isEmpty()) {
            sql += " ";
        } else {
            sql += " and USU_TSUCMOV.usu_numocp = '" + txtOC.getText() + "' ";
        }

        if (!codigo.equals("")) {
            int index = codigo.indexOf("-");
            String codigoSelecao = codigo.substring(0, index);
            verificaCheckbox();
            PESQUISA += " AND USU_CODEMP = 1 AND USU_CODFILSUC =  " + codigoSelecao.trim() + " AND USU_NUMNFV > 0 \n " + sqlCheckBox + " " + sqlData + sql;

            if (optNaoPesados.isSelected()) {
                PESQUISA += sqlData + sql;
            }

            listSucataEco = this.sucataEcoDao.getSucataEcos(PESQUISA_POR, PESQUISA);
            if (listSucataEco != null) {
                carregarTabela();

            }
        }

    }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/icons8-peso-kg-32-pesado.png");
        ImageIcon RuimIcon = getImage("/images/icons8-peso-kg-32-naopesado.png");
        ImageIcon ProvIcon = getImage("/images/sitMedio.png");
        String data = null;
        String mostrarPeso = "";
        for (SucataEco mi : listSucataEco) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            TableCellRenderer renderer = new SucataConsultaEco.ColorirRenderer();
            SucataConsultaEco.JTableRenderer renderers = new SucataConsultaEco.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            mostrarPeso = mi.getPesado();

            if (mostrarPeso.equals("PESADO")) {
                linha[0] = BomIcon;
            } else {
                linha[0] = RuimIcon;
            }

            linha[1] = mi.getEmpresa();
            linha[2] = mi.getFilial();
            linha[3] = mi.getSerie();
            linha[4] = mi.getNota();
            linha[5] = mi.getNumeroOC();
            linha[6] = mi.getPedidoSucata();
            linha[7] = mi.getCliente() + " - " + mi.getNomeCliente();
            if (mostrarPeso.equals("PESADO")) {
                linha[8] = mi.getPesoNota() + " KG ";
            } else {
                linha[8] = "0.0 KG ";
            }
            linha[9] = mi.getPesoSucata() + " KG ";
            linha[10] = this.utilDatas.converterDateToStr(mi.getDataSaida());
            linha[11] = converterMinutosHora(mi.getHoraSaida());
            if (optPesados.isSelected()) {
                linha[12] = this.utilDatas.converterDateToStr(mi.getDataLancamento());
                linha[13] = converterMinutosHora(mi.getHoraLancamento());
            } else {
                linha[12] = " ";
                linha[13] = " ";
            }

            modeloCarga.addRow(linha);
        }
    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(3).setCellRenderer(direita);
        //  jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.setRowHeight(40);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //  jTableCarga.setAutoResizeMode(0);

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
            if ("ABERTA".equals(str)) {
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnFiltrar = new javax.swing.JButton();
        btnFinalizar = new javax.swing.JButton();
        comboBalanca = new javax.swing.JComboBox<>();
        optPesados = new javax.swing.JRadioButton();
        optNaoPesados = new javax.swing.JRadioButton();
        btnPesar = new javax.swing.JButton();
        txtCliente = new org.openswing.swing.client.TextControl();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNota = new org.openswing.swing.client.TextControl();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtPedido = new org.openswing.swing.client.TextControl();
        jLabel8 = new javax.swing.JLabel();
        txtOC = new org.openswing.swing.client.TextControl();
        btnPesarAvulso = new javax.swing.JButton();
        btnCCAuto = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações"));
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jTableCarga.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "EMPRESA", "FILIAL", "SERIE", "NOTA", "OC", "PEDIDO", "CLIENTE", "PESO NOTA", "PESO SUCATA", "DATA NOTA", "HORA NOTA", "DATA SUCATA", "HORA SUCATA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
            jTableCarga.getColumnModel().getColumn(0).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(0).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(500);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMinWidth(130);
            jTableCarga.getColumnModel().getColumn(13).setPreferredWidth(130);
            jTableCarga.getColumnModel().getColumn(13).setMaxWidth(130);
        }

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.setText("Mostrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnFinalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnFinalizar.setText("Finalizar");
        btnFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarActionPerformed(evt);
            }
        });

        comboBalanca.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 - Selecionar Balança", "" }));
        comboBalanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBalancaActionPerformed(evt);
            }
        });

        buttonGroup1.add(optPesados);
        optPesados.setText("Pesado");
        optPesados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optPesadosActionPerformed(evt);
            }
        });

        buttonGroup1.add(optNaoPesados);
        optNaoPesados.setSelected(true);
        optNaoPesados.setText("Não Pesado");
        optNaoPesados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optNaoPesadosActionPerformed(evt);
            }
        });

        btnPesar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-balança-industrial-16.png"))); // NOI18N
        btnPesar.setText("Pesar Sucata ECO");
        btnPesar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnPesar.setEnabled(false);
        btnPesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesarActionPerformed(evt);
            }
        });

        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        jLabel2.setText("Cliente");

        jLabel3.setText("Filial");

        jLabel4.setText("Data Nota Fiscal");

        jLabel5.setText("Status");

        txtNota.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNotaActionPerformed(evt);
            }
        });

        jLabel6.setText("Nota");

        jLabel7.setText("Pedido");

        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPedidoActionPerformed(evt);
            }
        });

        jLabel8.setText("Ordem de compra");

        txtOC.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtOC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOCActionPerformed(evt);
            }
        });

        btnPesarAvulso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-balança-industrial-16.png"))); // NOI18N
        btnPesarAvulso.setText("Pesar Avulso ECO");
        btnPesarAvulso.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnPesarAvulso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesarAvulsoActionPerformed(evt);
            }
        });

        btnCCAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        btnCCAuto.setText("Conta Corrente  Auto");
        btnCCAuto.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnCCAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCCAutoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnPesar, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnPesarAvulso, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCCAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnFinalizar)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(optNaoPesados))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(optPesados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtOC, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnFiltrar))
                            .addComponent(jLabel8))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(optPesados)
                            .addComponent(optNaoPesados)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboBalanca)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnFiltrar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCCAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnPesar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPesarAvulso, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Sucata PDV", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1418, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comboBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBalancaActionPerformed
        try {
            //verificaCheckbox();
            getListar("", "");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_comboBalancaActionPerformed

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnFinalizarActionPerformed

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        if (optNaoPesados.isSelected()) {
            botaoPesagem = "sucataEcoNota";
            int linhaSelSit = jTableCarga.getSelectedRow();
            int colunaSelSit = jTableCarga.getSelectedColumn();

            sPesado = jTableCarga.getValueAt(linhaSelSit, 0).toString();
            // Fazer um subString
            int tam = sPesado.length();
            String substring = sPesado.substring(tam - 10, tam);
            if (substring.equals("sitBom.png")) {
                sPesado = "PESADO";
            }

            sCodEmp = jTableCarga.getValueAt(linhaSelSit, 1).toString().trim();
            sCodFil = jTableCarga.getValueAt(linhaSelSit, 2).toString().trim();
            sCodSnf = jTableCarga.getValueAt(linhaSelSit, 3).toString().trim();
            sNumNfv = jTableCarga.getValueAt(linhaSelSit, 4).toString().trim();
            sNumOcp = jTableCarga.getValueAt(linhaSelSit, 5).toString().trim();

            btnPesar.setEnabled(false);
            if (evt.getClickCount() == 1) {
                PESQUISA = " AND  USU_TSUCMOV.USU_CODEMP =" + sCodEmp.trim() + " AND USU_TSUCMOV.USU_CODFILSUC =" + sCodFil.trim() + " AND USU_TSUCMOV.USU_CODSNF = '" + sCodSnf.trim() + "' AND USU_TSUCMOV.USU_NUMNFV = " + sNumNfv.trim() + " \n"
                        + "AND USU_TSUCMOV.USU_CODSNF IN ( SELECT codsnf FROM E020snf WHERE E020snf.CODEMP =" + sCodEmp.trim() + " AND E020snf.CODFIL =" + sCodFil.trim() + " AND  E020snf.disaut = 6)";
                try {
                    getNota("", PESQUISA);
                } catch (SQLException ex) {
                    Logger.getLogger(SucataConsultaEco.class.getName()).log(Level.SEVERE, null, ex);
                }
                if ((!sPesado.equals("PESADO")) && (!sNumOcp.equals("0"))) {
                    btnPesar.setEnabled(true);
                } else {
                    // JOptionPane.showMessageDialog(null, "Nota já vinculada com a sucata.",
                    //           "Atenção", JOptionPane.WARNING_MESSAGE);     
                }

            }
            if (evt.getClickCount() == 2) {
                PESQUISA = " AND  USU_TSUCMOV.USU_CODEMP =" + sCodEmp.trim() + " AND USU_TSUCMOV.USU_CODFILSUC =" + sCodFil.trim() + " AND USU_TSUCMOV.USU_CODSNF = '" + sCodSnf.trim() + "' AND USU_TSUCMOV.USU_NUMNFV = " + sNumNfv.trim() + " \n"
                        + "AND USU_TSUCMOV.USU_CODSNF IN ( SELECT codsnf FROM E020snf WHERE E020snf.CODEMP =" + sCodEmp.trim() + " AND E020snf.CODFIL =" + sCodFil.trim() + " AND  E020snf.disaut = 6)";
                try {
                    getNota("", PESQUISA);
                } catch (SQLException ex) {
                    Logger.getLogger(SucataConsultaEco.class.getName()).log(Level.SEVERE, null, ex);
                }
                if ((!sPesado.equals("PESADO")) && (!sNumOcp.equals("0"))) {
                    btnPesar.setEnabled(true);
                    try {
                        novoRegistro("", "");
                    } catch (Exception ex) {
                        Logger.getLogger(SucataConsultaEco.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    if (!sNumOcp.equals("0")) {
                        JOptionPane.showMessageDialog(null, "Nota já vinculada com a sucata.",
                                "Atenção", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Nota sem Ordem de Compra de Sucata ECO.",
                                "Atenção", JOptionPane.WARNING_MESSAGE);

                    }
                }
            }
        }
    }//GEN-LAST:event_jTableCargaMouseClicked


    private void optPesadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optPesadosActionPerformed
        try {
            jLabel4.setText("Data Pesagem da Sucata");
            pegarDataDigitada();
            String sql = " AND  USU_DEBCRE ='4 - CREDITO' AND USU_AUTMOT NOT IN('IND') AND  USU_SITSUC NOT IN ('MANUAL') ";
            getListar("SIT", sql);

            btnPesar.setEnabled(false);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optPesadosActionPerformed

    private void optNaoPesadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optNaoPesadosActionPerformed
        try {
            jLabel4.setText("Data Nota Fiscal");
            pegarDataDigitada();
            String sql = " AND USU_TSUCMOV.USU_SITPED IS NULL   AND USU_TSUCMOV.USU_DEBCRE ='3 - DEBITO'\n"
                    + " AND USU_TSUCMOV.USU_AUTMOT NOT IN('IND') AND  USU_TSUCMOV.USU_SITSUC NOT IN ('MANUAL') \n  ";

            getListar("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optNaoPesadosActionPerformed

    private void btnPesarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesarActionPerformed
        botaoPesagem = "sucataEcoNota";
        try {
            novoRegistro("", "");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPesarActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        verificaCheckbox();
         try {
            getListar("", "");
        } catch (Exception ex) {
            Logger.getLogger(SucataConsultaEco.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void txtNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotaActionPerformed

        if (txtNota.getText().isEmpty()) {
            verificaCheckbox();
            try {

            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
            String sql = " " + sqlCheckBox;
            try {
                getListar("SIT", sql);
            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {

                String sql = " and USU_TSUCMOV.usu_numnfv = '" + txtNota.getText() + "' " + sqlCheckBox;
                getListar("SIT", sql);
            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtNotaActionPerformed

    private void txtPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPedidoActionPerformed
        if (txtPedido.getText().isEmpty()) {
            // Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
            verificaCheckbox();
            try {

            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
            String sql = " " + sqlCheckBox;
            try {
                getListar("SIT", sql);
            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {

                String sql = " and USU_TSUCMOV.usu_numped = '" + txtPedido.getText() + "' " + sqlCheckBox;
                getListar("SIT", sql);
            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtPedidoActionPerformed

    private void txtOCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOCActionPerformed
        if (txtOC.getText().isEmpty()) {
            // Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
            verificaCheckbox();
            try {

            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
            String sql = " " + sqlCheckBox;
            try {
                getListar("SIT", sql);
            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {

                String sql = " and USU_TSUCMOV.usu_numocp = '" + txtOC.getText() + "' " + sqlCheckBox;
                getListar("SIT", sql);
            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtOCActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed

        if (txtCliente.getText().isEmpty()) {
            // Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
            verificaCheckbox();
            try {

            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
            String sql = " " + sqlCheckBox;
            try {
                getListar("SIT", sql);
            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {

                String sql = " and USU_TSUCMOV.usu_codcli = '" + txtCliente.getText() + "' " + sqlCheckBox;
                getListar("SIT", sql);
            } catch (Exception ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnPesarAvulsoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesarAvulsoActionPerformed
        // TODO add your handling code here:
        botaoPesagem = "avulso";
        
         sucataEco = new SucataEco();
         sucataEco.setEmpresa(1);
         sucataEco.setFilial(10);
         sucataEco.setSerie("SUC");
         
         try {
            novoRegistro("", "");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPesarAvulsoActionPerformed

    private void btnCCAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCCAutoActionPerformed
        SucataContaCorrente sol = new SucataContaCorrente();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        try {
            sol.setMaximum(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SucataConsultaEco.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCCAutoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCCAuto;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JButton btnPesar;
    private javax.swing.JButton btnPesarAvulso;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<String> comboBalanca;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JRadioButton optNaoPesados;
    private javax.swing.JRadioButton optPesados;
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private org.openswing.swing.client.TextControl txtNota;
    private org.openswing.swing.client.TextControl txtOC;
    private org.openswing.swing.client.TextControl txtPedido;
    // End of variables declaration//GEN-END:variables
}
