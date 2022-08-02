/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.CargaRegistro;
import br.com.sgi.dao.CargaAberturaDAO;
import br.com.sgi.util.Mensagem;

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
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;
import br.com.sgi.util.UtilDatas;

/**
 *
 * @author jairosilva
 */
public final class IntegrarPesos extends InternalFrame {

    private CargaRegistro cargaRegistro;
    private List<CargaRegistro> listaCargaAbertura = new ArrayList<CargaRegistro>();
    private CargaAberturaDAO cargaAberturaDAO;

    private UtilDatas utilDatas;

    private static final Color COR_CON = new Color(102, 255, 102);
    private static final Color COR_CON_PRO = new Color(255, 0, 0);
    private static final Color COR_CON_PEN = new Color(51, 51, 255);

    private String datIni;
    private String datFim;
    private String PESQUISA;
    private String PESQUISA_POR;

    private String PROCESSO;

    public IntegrarPesos() {
        try {

            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Lançamento de Peso "));

            if (this.cargaAberturaDAO == null) {
                this.cargaAberturaDAO = new CargaAberturaDAO();
            }
            if (utilDatas == null) {
                utilDatas = new UtilDatas();
            }

            pegarData();
            getCargas(this.PESQUISA_POR, this.PESQUISA);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }

    }

    private void pegarData() throws ParseException {
        txtDatIni.setDate(new Date());
        txtDatFim.setDate(new Date());
        datIni = utilDatas.converterDateToStr(new Date());
        datFim = utilDatas.converterDateToStr(new Date());
        this.PESQUISA_POR = "DATA";
        this.PESQUISA = " and usu_datent >= '" + datIni + "' and usu_datent <='" + datFim + "'";

    }

    private void pesquisarCarga() throws Exception {
        datIni = utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = utilDatas.converterDateToStr(txtDatFim.getDate());

        if (optAbe.isSelected()) {
            this.PESQUISA_POR = "SITUACAO";
            this.PESQUISA = " and usu_sitcar = 'A' and usu_datent >= '" + datIni + "' and usu_datent <='" + datFim + "'";

        }
        if (optFec.isSelected()) {
            this.PESQUISA_POR = "SITUACAO";
            this.PESQUISA = "and usu_sitcar = 'F' and usu_datent >= '" + datIni + "' and usu_datent <='" + datFim + "'";
        }

        if (optDat.isSelected()) {

            this.PESQUISA_POR = "DATA";
            this.PESQUISA = " and usu_datent >= '" + datIni + "' and usu_datent <='" + datFim + "'";
        }

        if (optExc.isSelected()) {

            this.PESQUISA_POR = "SITUACAO";
            this.PESQUISA = " and usu_sitcar = 'R' and usu_datent >= '" + datIni + "' and usu_datent <='" + datFim + "'";
        }
        getCargas(PESQUISA_POR, PESQUISA);
    }

    private void getCargas(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listaCargaAbertura = this.cargaAberturaDAO.getCargasRegistro(PESQUISA_POR, PESQUISA);
        if (listaCargaAbertura != null) {
            carregarTabela();
        }
    }

    public void carregarTabela() throws Exception {

        ImageIcon sitSep = getImage("/images/bricks.png");
        ImageIcon sitEnt = getImage("/images/sitRuim.png");
        ImageIcon sitSai = getImage("/images/sitBom.png");

        int linhas = 0;
        redColunastab();

        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(linhas);
        Object[] linha = new Object[20];
        for (CargaRegistro prg : listaCargaAbertura) {
            TableColumnModel columnModel = jTableCarga.getColumnModel();
            IntegrarPesos.JTableRenderer renderers = new IntegrarPesos.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            columnModel.getColumn(6).setCellRenderer(renderers);
            columnModel.getColumn(10).setCellRenderer(renderers);
            linha[0] = sitSai;
            if (prg.getSituacaoCarga().equals("A")) {
                linha[0] = sitEnt;
            }
            if (prg.getSituacaoCarga().equals("R")) {
                linha[0] = sitSep;
            }
            linha[1] = prg.getNumerocarga();
            linha[2] = prg.getPlaca();
            linha[3] = prg.getDataEntradaS();
            linha[4] = prg.getHoraEntradaS();
            linha[5] = prg.getPesoEntrada();
            linha[6] = sitSep;
            linha[7] = prg.getDataSaidaS();
            linha[8] = prg.getHoraSaidaS();
            linha[9] = prg.getPesoSaida();
            linha[10] = sitSep;
            linha[11] = prg.getPesoLiquidoCarga();
            linha[12] = prg.getSituacaoCarga();
            linha[13] = prg.getTipoCarga();
            linha[14] = prg.getNomeMotorista();
            linha[15] = prg.getTransportadora().getCodigoTransportadora() + " - " + prg.getTransportadora().getNomeTransportadora();

            modeloCarga.addRow(linha);
        }

    }

    private void getCargaAbertura(String numeroCarga) throws SQLException, PropertyVetoException {
        cargaRegistro = new CargaRegistro();
        cargaRegistro = cargaAberturaDAO.getCargaRegistro("PLACA", " and car.usu_nrocar = '" + numeroCarga + "'");

        if (cargaRegistro != null) {
            btnFinalizar.setEnabled(true);
            btnFinalizar.setText("Finalizar Peso " + cargaRegistro.getNumerocarga());
            btnExcluir.setText("Exluir Peso ");
            if (!cargaRegistro.getSituacaoCarga().equals("F")) {
                btnExcluir.setText("Exluir Peso " + cargaRegistro.getNumerocarga());
                btnExcluir.setEnabled(true);
            }
            if (cargaRegistro.getSituacaoCarga().equals("R")) {
                btnFinalizar.setText("Finalizar Peso ");
                btnFinalizar.setEnabled(false);
            }
        } else {

        }
    }

    public void ExcluirPeso(String retorno, String motivo) throws SQLException, Exception {
        if (retorno.equals("ERRO")) {
            Mensagem.mensagemRegistros(retorno, "Operação cancelada pelo usuário");
        }
        if (this.cargaRegistro.getNumerocarga() > 0) {
            cargaRegistro.setSituacaoCarga("R");
            cargaRegistro.setObservacaoCarga(cargaRegistro.getNumerocarga() + " Excluído. Motivo:" + motivo);
            if (!cargaAberturaDAO.remover(cargaRegistro)) {
            } else {
                getCargas(PESQUISA_POR, PESQUISA);
            }
        }
        btnFinalizar.setText("Finalizar Peso ");
        btnExcluir.setText("Exluir Peso ");
    }

    public void retornarPeso() throws Exception {

        getCargas(PESQUISA_POR, PESQUISA);
        btnFinalizar.setEnabled(false);
        btnFinalizar.setText("Finalizar Peso ");
    }

    private void novoRegistro(String PROCESSO, String COMPLEMENTO) throws PropertyVetoException, Exception {
        IntegrarPesosRegistrar sol = new IntegrarPesosRegistrar();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 
        CargaRegistro car = new CargaRegistro();
        sol.setRecebePalavra(this, "ENTRADA", PROCESSO, car, COMPLEMENTO);
    }

    private void fecharRegistro(String PROCESSO) throws PropertyVetoException, Exception {
        IntegrarPesosRegistrar sol = new IntegrarPesosRegistrar();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 
        sol.setRecebePalavraSaida(this, "SAIDA", PROCESSO, this.cargaRegistro);

    }

    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            //  System.out.println(" value " + value.toString());
            return this;
        }
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableCarga.setRowHeight(40);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);

        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(8).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(9).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(10).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(11).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);

        jTableCarga.setAutoResizeMode(0);
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

    public class ColorirRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable jTableCarga, Object value, boolean selected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(jTableCarga, value, selected, hasFocus, row, col);

            if (value.equals("CON")) {
                setBackground(COR_CON);
            } else if (value.equals("CON_PEN")) {
                setBackground(COR_CON_PEN);
            } else if (value.equals("CON_PRO")) {
                setBackground(COR_CON_PRO);
            } else if (value.equals("EST")) {
                setBackground(COR_CON);
            } else {
                setBackground(COR_CON_PRO);
            }

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

        buttonGroup2 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        optAbe = new javax.swing.JRadioButton();
        optFec = new javax.swing.JRadioButton();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnFiltrar = new javax.swing.JButton();
        btnFiltrar1 = new javax.swing.JButton();
        optDat = new javax.swing.JRadioButton();
        btnFiltrar2 = new javax.swing.JButton();
        btnFiltrar3 = new javax.swing.JButton();
        btnFinalizar = new javax.swing.JButton();
        btnFiltrar4 = new javax.swing.JButton();
        btnSacataIndustrializacao = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        optExc = new javax.swing.JRadioButton();
        btnSucataAuto = new javax.swing.JButton();
        btnSucataAuto1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Integrar Ordens");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações"));
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "TICKET", "PLACA", "ENTRADA", "HORA", "PESO ENTRADA", "#", "SAÍDA", "HORA", "PESO SAÍDA", "#", "PESO LIQUIDO", "SITUAÇÃO", "TIPO", "MOTORISTA", "TRANSPORTADORA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(13).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(13).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(13).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(150);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(150);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(150);
            jTableCarga.getColumnModel().getColumn(15).setMinWidth(300);
            jTableCarga.getColumnModel().getColumn(15).setPreferredWidth(300);
            jTableCarga.getColumnModel().getColumn(15).setMaxWidth(300);
        }

        buttonGroup2.add(optAbe);
        optAbe.setText("Abertas");
        optAbe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optAbeActionPerformed(evt);
            }
        });

        buttonGroup2.add(optFec);
        optFec.setText("Fechadas");
        optFec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optFecActionPerformed(evt);
            }
        });

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnFiltrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnFiltrar1.setText("Coleta Moto");
        btnFiltrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar1ActionPerformed(evt);
            }
        });

        buttonGroup2.add(optDat);
        optDat.setText("Data");
        optDat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optDatActionPerformed(evt);
            }
        });

        btnFiltrar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        btnFiltrar2.setText("Descarga");
        btnFiltrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar2ActionPerformed(evt);
            }
        });

        btnFiltrar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder.png"))); // NOI18N
        btnFiltrar3.setText("Pesagem Interna");
        btnFiltrar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar3ActionPerformed(evt);
            }
        });

        btnFinalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/book.png"))); // NOI18N
        btnFinalizar.setText("Finalizar");
        btnFinalizar.setEnabled(false);
        btnFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarActionPerformed(evt);
            }
        });

        btnFiltrar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        btnFiltrar4.setText("Coleta Auto");
        btnFiltrar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar4ActionPerformed(evt);
            }
        });

        btnSacataIndustrializacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bug_link.png"))); // NOI18N
        btnSacataIndustrializacao.setText(" Sucata Industrialização");
        btnSacataIndustrializacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSacataIndustrializacaoActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.setEnabled(false);
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        buttonGroup2.add(optExc);
        optExc.setText("Excluidos");
        optExc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optExcActionPerformed(evt);
            }
        });

        btnSucataAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/table_add.png"))); // NOI18N
        btnSucataAuto.setText(" Sucata Eco");
        btnSucataAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSucataAutoActionPerformed(evt);
            }
        });

        btnSucataAuto1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/truck_red.png"))); // NOI18N
        btnSucataAuto1.setText("Coleta Pedidos");
        btnSucataAuto1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSucataAuto1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(optAbe)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optFec)
                .addGap(2, 2, 2)
                .addComponent(optExc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optDat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(btnFiltrar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnExcluir))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnFiltrar2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnFiltrar1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrar4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSucataAuto1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrar3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSacataIndustrializacao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSucataAuto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addComponent(btnFinalizar))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnFiltrar1, btnFiltrar2, btnFiltrar3, btnFiltrar4, btnSacataIndustrializacao});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(optAbe)
                        .addComponent(optFec)
                        .addComponent(optDat)
                        .addComponent(optExc))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFiltrar)
                        .addComponent(btnExcluir))
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFiltrar2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFiltrar3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSacataIndustrializacao, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSucataAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSucataAuto1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnExcluir, btnFiltrar});

        jTabbedPane1.addTab("Pesagem", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1290, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
        //registrarAcoesDoTeclado(jPanelGeral);
    }//GEN-LAST:event_formKeyTyped

    private String numeroCarga;
    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();
        numeroCarga = jTableCarga.getValueAt(linhaSelSit, 1).toString();
        PROCESSO = jTableCarga.getValueAt(linhaSelSit, 13).toString();
        btnExcluir.setEnabled(false);
        btnFinalizar.setEnabled(false);

        if (evt.getClickCount() == 2) {
            try {
                getCargaAbertura(numeroCarga);
            } catch (SQLException ex) {
                Logger.getLogger(IntegrarPesos.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (PropertyVetoException ex) {
                Logger.getLogger(IntegrarPesos.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnFiltrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar1ActionPerformed
        try {
            PROCESSO = "CM";
            novoRegistro(PROCESSO, "");

        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar1ActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        try {
            pesquisarCarga();

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void optAbeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optAbeActionPerformed
        try {
            pesquisarCarga();

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optAbeActionPerformed

    private void optFecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optFecActionPerformed
        try {
            pesquisarCarga();

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optFecActionPerformed

    private void optDatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optDatActionPerformed
        try {
            pesquisarCarga();

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optDatActionPerformed

    private void btnFiltrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar2ActionPerformed
        try {
            PROCESSO = "D";
            novoRegistro(PROCESSO, "");

        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar2ActionPerformed

    private void btnFiltrar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar3ActionPerformed
        try {
            PROCESSO = "CI";
            novoRegistro(PROCESSO, "");

        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnFiltrar3ActionPerformed

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        try {
            fecharRegistro(PROCESSO);

        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFinalizarActionPerformed

    private void btnFiltrar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar4ActionPerformed
        try {
            PROCESSO = "CA";
            novoRegistro(PROCESSO, "");

        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar4ActionPerformed

    private void btnSacataIndustrializacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSacataIndustrializacaoActionPerformed
        try {
            PROCESSO = "DS";
            novoRegistro(PROCESSO, "INDUSTRIALIZAÇÃO");

        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSacataIndustrializacaoActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed

        try {
            ManutencaoPeso sol = new ManutencaoPeso();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado 
            sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavra(this, "ENTRADA", PROCESSO, this.cargaRegistro);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_btnExcluirActionPerformed

    private void optExcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optExcActionPerformed
        try {
            pesquisarCarga();

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optExcActionPerformed

    private void btnSucataAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSucataAutoActionPerformed
        try {
            PROCESSO = "D";
            novoRegistro(PROCESSO, "ECO");

        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSucataAutoActionPerformed

    private void btnSucataAuto1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSucataAuto1ActionPerformed
        try {
            PROCESSO = "CP";
            novoRegistro(PROCESSO, "");

        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSucataAuto1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar1;
    private javax.swing.JButton btnFiltrar2;
    private javax.swing.JButton btnFiltrar3;
    private javax.swing.JButton btnFiltrar4;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JButton btnSacataIndustrializacao;
    private javax.swing.JButton btnSucataAuto;
    private javax.swing.JButton btnSucataAuto1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JRadioButton optAbe;
    private javax.swing.JRadioButton optDat;
    private javax.swing.JRadioButton optExc;
    private javax.swing.JRadioButton optFec;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    // End of variables declaration//GEN-END:variables
}
