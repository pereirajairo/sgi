/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Filial;
import br.com.sgi.bean.SucataPDV;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.SucataPDVDao;
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
import br.com.sgi.util.UtilDatas;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author jairosilva
 */
//testematheus 
public final class SucataConsultaPDV extends InternalFrame {

    private SucataPDV sucataPDV;
    private Usuario usuario;

    private List<Filial> listFilial = new ArrayList<Filial>();
    private List<SucataPDV> listSucataPDV = new ArrayList<SucataPDV>();
    private List<Usuario> listAbragencia = new ArrayList<Usuario>();

    private FilialDAO filialDAO;
    private UsuarioERPDAO usuarioERPDAO;
    private SucataPDVDao sucataPDVDao;
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
    private String sPesado;
    private Integer sUsuario;
    private String abreFilial;
    private String PesquisaFilial;
    private Integer nContEntrada = 0;
    private Integer filial = 0;
    FormatarNumeros formatarNumeros = new FormatarNumeros();

    public SucataConsultaPDV() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Gestão de Pesos do PDV"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (filialDAO == null) {
                filialDAO = new FilialDAO();
            }
            if (sucataPDVDao == null) {
                sucataPDVDao = new SucataPDVDao();
            }
            if (usuarioERPDAO == null) {
                usuarioERPDAO = new UsuarioERPDAO();
            }

            txtDatIni.setDate(this.utilDatas.retornaDataIni(new Date()));
            txtDatFim.setDate(new Date());
            pegarDataDigitada();
            preencherComboBalanca(0);
            // getListar("", "");
            verificaCheckbox();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void RetornarCampoAtualiza() throws Exception {

        verificaCheckbox();
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
                sql = " and sitnfv =2 AND NOT EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                        + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV ) ";

            } else if (optPesados.isSelected()) {
                sql = " AND EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                        + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV AND USU_TPDVSUC.USU_INDSUC='S') ";

            } else if (optPaga.isSelected()) {
                sql = " AND EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                        + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV AND USU_TPDVSUC.USU_INDSUC='N') ";

            } else if (optTodos.isSelected()) {
                sql = " AND sitnfv =2 AND EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                        + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV)";
            }

            getListar("", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void novoRegistro(String PROCESSO, String string) throws PropertyVetoException, Exception {
        SucataPDVLancamento sol = new SucataPDVLancamento();
        MDIFrame.add(sol, true);

        sol.setMaximum(false); // executa maximizado 
        sol.setSize(600, 500);
        sol.setPosicao();
        sol.setRecebePalavra(this, this.sucataPDV);
    }

    private void getNota(String PESQUISA_POR, String PESQUISA) throws SQLException {
        sucataPDV = new SucataPDV();

        sucataPDV = sucataPDVDao.getSucataPDV(PESQUISA_POR, PESQUISA);
        btnLiberar.setEnabled(false);
        if (sucataPDV != null) {
            if (sucataPDV.getNota() > 0) {
                if (optNaoPesados.isSelected()) {
                    btnLiberar.setEnabled(true);
                } else {
                    //JOptionPane.showMessageDialog(null, "Nota já vinculada com a sucata.",
                    //     "Atenção", JOptionPane.WARNING_MESSAGE);
                    btnLiberar.setEnabled(false);

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
        listFilial = filialDAO.getFilias("", " AND CODEMP = 1 AND codfil in (" + PesquisaFilial + ")");

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
    }

    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        pegarDataDigitada();
        String codigo = comboBalanca.getSelectedItem().toString();

        if (!codigo.equals("")) {
            int index = codigo.indexOf("-");
            String codigoSelecao = codigo.substring(0, index);

            filial = Integer.valueOf(codigoSelecao.trim());
            if (filial > 0) {

                PESQUISA += "  and CODEMP =1 AND CODFIL = " + codigoSelecao.trim() + " \n"
                        + " AND DATEMI BETWEEN '" + datIni + "' AND '" + datFim + "'  \n"
                        //  + "AND CODSNF IN ( SELECT codsnf FROM E020snf WHERE CODEMP = 1 AND CODFIL = " + codigoSelecao.trim() + " AND  disaut = 6) ";
                        + "AND CODSNF IN (SELECT SNFNFC FROM E070VAR WHERE CODINT =3 AND CODEMP = 1 AND CODFIL =  " + codigoSelecao.trim() + "\n"
                        + "UNION ALL\n"
                        + "SELECT SERNCE FROM E070VAR WHERE CODINT =3 AND CODEMP = 1 AND CODFIL = " + codigoSelecao.trim() + "\n"
                        + "UNION ALL\n"
                        + "SELECT SNFMAN FROM E070VAR WHERE CODINT =3 AND CODEMP = 1 AND CODFIL =  " + codigoSelecao.trim() + " )";

                if (nContEntrada == 0) {
                    nContEntrada = 1;
                    PESQUISA += " AND NOT EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                            + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV ) ";
                }

                listSucataPDV = this.sucataPDVDao.getSucataPDVs(PESQUISA_POR, PESQUISA);

                if (listSucataPDV != null) {
                    carregarTabela();

                }
            }

        }

    }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/icons8-peso-kg-32-pesado.png");
        ImageIcon RuimIcon = getImage("/images/icons8-peso-kg-32-naopesado.png");
        ImageIcon PagoIcon = getImage("/images/icons8-peso-kg-32-pago.png");
        ImageIcon ProvIcon = getImage("/images/sitMedio.png");
        String data = null;
        String mostrarPeso = "";
        for (SucataPDV mi : listSucataPDV) {
            Object[] linha = new Object[13];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            TableCellRenderer renderer = new SucataConsultaPDV.ColorirRenderer();
            SucataConsultaPDV.JTableRenderer renderers = new SucataConsultaPDV.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            mostrarPeso = mi.getPesado();

            if (mostrarPeso.equals("PESADO")) {
                linha[0] = BomIcon;
            }
            if (mostrarPeso.equals("NAOPESADO")) {
                linha[0] = RuimIcon;
            }
            if (mostrarPeso.equals("PAGA")) {
                linha[0] = PagoIcon;
            }
            linha[1] = mi.getEmpresa();
            linha[2] = mi.getFilial();
            linha[3] = mi.getSerie();
            linha[4] = mi.getNota();
            linha[5] = mi.getCliente() + " - " + mi.getNomeCliente();
            if (mostrarPeso.equals("PESADO")) {
                linha[6] = mi.getPesoNota() + " KG ";
            } else {
                linha[6] = "0.0 KG ";
            }
            if (optPaga.isSelected()) {
                linha[6] = mi.getPrecoSucata();
                jTableCarga.getColumnModel().getColumn(6).setHeaderValue("PREÇO UNITÁRIO");
            } else {
                jTableCarga.getColumnModel().getColumn(6).setHeaderValue("PESO NOTA");
            }
            linha[7] = mi.getPesoSucata() + " KG ";
            linha[8] = mi.getPrecoTotalSucata();
            linha[9] = this.utilDatas.converterDateToStr(mi.getDataSaida());
            linha[10] = converterMinutosHora(mi.getHoraSaida());
            if ((optPesados.isSelected()) || (optTodos.isSelected())) {
                linha[11] = this.utilDatas.converterDateToStr(mi.getDataLancamento());
                linha[12] = converterMinutosHora(mi.getHoraLancamento());
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
        optTodos = new javax.swing.JRadioButton();
        optPesados = new javax.swing.JRadioButton();
        optNaoPesados = new javax.swing.JRadioButton();
        btnLiberar = new javax.swing.JButton();
        optPaga = new javax.swing.JRadioButton();

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
                "#", "EMPRESA", "FILIAL", "SERIE", "NOTA", "CLIENTE", "PESO NOTA", "PESO SUCATA", "VALOR PAGO", "DATA NOTA", "HORA NOTA", "DATA SUCATA", "HORA SUCATA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
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
            jTableCarga.getColumnModel().getColumn(0).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(500);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(130);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(130);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(130);
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

        buttonGroup1.add(optTodos);
        optTodos.setText("Todos");
        optTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optTodosActionPerformed(evt);
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

        btnLiberar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-balança-industrial-16.png"))); // NOI18N
        btnLiberar.setText("Pesar Nota");
        btnLiberar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnLiberar.setEnabled(false);
        btnLiberar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLiberarActionPerformed(evt);
            }
        });

        buttonGroup1.add(optPaga);
        optPaga.setText("Paga");
        optPaga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optPagaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnLiberar, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFinalizar))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(comboBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optNaoPesados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(optPesados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optPaga)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optTodos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnFiltrar)
                        .addGap(0, 199, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(optNaoPesados)
                            .addComponent(optPesados)
                            .addComponent(btnFiltrar)
                            .addComponent(optPaga)
                            .addComponent(optTodos))
                        .addGap(21, 21, 21))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)))
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLiberar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Sucata PDV", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1191, Short.MAX_VALUE)
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
            verificaCheckbox();
            // getListar("", "");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_comboBalancaActionPerformed

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnFinalizarActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        try {
            String sql = "";
            if (optNaoPesados.isSelected()) {
                sql = " and sitnfv =2  AND NOT EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                        + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV ) ";

            } else if (optPesados.isSelected()) {
                sql = " AND EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                        + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV and USU_TPDVSUC.USU_IndSuc='S') ";

            } else if (optPaga.isSelected()) {
                sql = " AND EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                        + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV and USU_TPDVSUC.USU_IndSuc='N') ";

            } else if (optTodos.isSelected()) {
                sql = "AND EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                        + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV)";
            } else {

            }
            getListar("", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();

        sPesado = jTableCarga.getValueAt(linhaSelSit, 0).toString();
        // Fazer um subString
        int tam = sPesado.length();
        String substring = sPesado.substring(tam - 28, tam);
        if (substring.equals("icons8-peso-kg-32-pesado.png")) {
            sPesado = "PESADO";
        }

        sCodEmp = jTableCarga.getValueAt(linhaSelSit, 1).toString();
        sCodFil = jTableCarga.getValueAt(linhaSelSit, 2).toString();
        sCodSnf = jTableCarga.getValueAt(linhaSelSit, 3).toString();
        sNumNfv = jTableCarga.getValueAt(linhaSelSit, 4).toString();

        btnLiberar.setEnabled(false);
        if (evt.getClickCount() == 1) {
            PESQUISA = " AND  CODEMP =" + sCodEmp.trim() + " AND CODFIL =" + sCodFil.trim() + " AND CODSNF = '" + sCodSnf.trim() + "' AND NUMNFV = " + sNumNfv.trim() + " ";

            try {
                getNota("", PESQUISA);
            } catch (SQLException ex) {
                Logger.getLogger(SucataConsultaPDV.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (optNaoPesados.isSelected()) {
                btnLiberar.setEnabled(true);
            } else {
                // JOptionPane.showMessageDialog(null, "Nota já vinculada com a sucata.",
                //           "Atenção", JOptionPane.WARNING_MESSAGE);     
            }

        }
        if (evt.getClickCount() == 2) {
            PESQUISA = " AND  CODEMP =" + sCodEmp.trim() + " AND CODFIL =" + sCodFil.trim() + " AND CODSNF = '" + sCodSnf.trim() + "' AND NUMNFV = " + sNumNfv.trim() + " ";

            try {
                getNota("", PESQUISA);
            } catch (SQLException ex) {
                Logger.getLogger(SucataConsultaPDV.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (optNaoPesados.isSelected()) {
                btnLiberar.setEnabled(true);
                try {
                    novoRegistro("", "");
                } catch (Exception ex) {
                    Logger.getLogger(SucataConsultaPDV.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nota já vinculada com a sucata.",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        }

    }//GEN-LAST:event_jTableCargaMouseClicked


    private void optTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optTodosActionPerformed
        try {
            pegarDataDigitada();
            getListar("SIT", " AND sitnfv =2 AND EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                    + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV)");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optTodosActionPerformed

    private void optPesadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optPesadosActionPerformed
        try {
            pegarDataDigitada();
            String sql = " AND EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                    + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV AND USU_TPDVSUC.USU_INDSUC='S') ";
            getListar("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optPesadosActionPerformed

    private void optNaoPesadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optNaoPesadosActionPerformed
        try {
            pegarDataDigitada();
            String sql = " and sitnfv =2  AND NOT EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                    + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV) ";
            getListar("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optNaoPesadosActionPerformed

    private void btnLiberarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLiberarActionPerformed
        try {
            novoRegistro("", "");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLiberarActionPerformed

    private void optPagaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optPagaActionPerformed
        try {
            pegarDataDigitada();
            String sql = " AND EXISTS (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                    + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV and USU_TPDVSUC.USU_IndSuc='N') ";
            getListar("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_optPagaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JButton btnLiberar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<String> comboBalanca;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JRadioButton optNaoPesados;
    private javax.swing.JRadioButton optPaga;
    private javax.swing.JRadioButton optPesados;
    private javax.swing.JRadioButton optTodos;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    // End of variables declaration//GEN-END:variables
}
