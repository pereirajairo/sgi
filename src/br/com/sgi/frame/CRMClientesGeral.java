/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.BaseEstado;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.ClienteGrupo;
import br.com.sgi.bean.Representante;
import br.com.sgi.dao.ClienteDAO;
import br.com.sgi.dao.ClienteGrupoDAO;
import br.com.sgi.dao.RepresentanteDAO;
import br.com.sgi.util.UtilDatas;
import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;

/**
 *
 * @author jairosilva
 */
public final class CRMClientesGeral extends InternalFrame {

    private Cliente cliente;
    private ClienteDAO clienteDAO;
    private List<Cliente> lstCliente = new ArrayList<Cliente>();

    private UtilDatas utilDatas;
    // private Sucatas veioCampo;
    private String SITUACAO;
    private String AUTOMOTO;

    private String vendedorSelecionado = "0-SELECIONE";
    private String statusSelecionado = "0-SELECIONE";
    private String seguimentoSelecionado = "0-SELECIONE";

    public CRMClientesGeral() {
        try {
            initComponents();

            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }

            if (clienteDAO == null) {
                this.clienteDAO = new ClienteDAO();
            }
            LoadEstados();
            getRepresentantes("");
            getGrupos("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void LoadEstados() {
        BaseEstado estado = new BaseEstado();
        Map<String, String> mapas = estado.getEstados();
        for (String uf : mapas.keySet()) {
            txtEstado.addItem(mapas.get(uf));
        }
    }

    private void getRepresentantes(String acao) throws SQLException {
        RepresentanteDAO dao = new RepresentanteDAO();
        List<Representante> lstRepresentante = new ArrayList<Representante>();

        lstRepresentante = dao.getRepresentantes("VEN", "");

        for (Representante rep : lstRepresentante) {
            if (rep.getCodigo() > 0) {
                txtVendedor.addItem(rep.getCodigo() + "-" + rep.getNome());
            }
        }
    }

    private void getGrupos(String acao) throws SQLException {
        ClienteGrupoDAO dao = new ClienteGrupoDAO();
        List<ClienteGrupo> lstClienteGrupo = new ArrayList<ClienteGrupo>();

        lstClienteGrupo = dao.getClienteGrupos(" ", " ");

        for (ClienteGrupo grp : lstClienteGrupo) {
            if (!grp.getCodgrp().isEmpty()) {
                txtGrupo.addItem(grp.getCodgrp() + "-" + grp.getNome());
            }
        }
    }

    private void getClientes(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (!PESQUISA_POR.isEmpty()) {
            if (PESQUISA_POR.equals("VENDEDOR")) {
                lstCliente = this.clienteDAO.getClientesGeral(PESQUISA_POR, PESQUISA);
            } else {
                lstCliente = this.clienteDAO.getClientes(this.PESQUISAR_POR, PESQUISA);
            }

            if (lstCliente != null) {
                carregarTabela(lstCliente);
            }

        }
    }

    public void carregarTabela(List<Cliente> lstCliente) throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCad.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        int ativo = 0;
        int inativo = 0;
        int pre_inativo = 0;
        int totalCliente = 0;
        ImageIcon CreIcon = getImage("/images/sitBom.png");
        ImageIcon PreIcon = getImage("/images/sitMedio.png");
        ImageIcon InaIcon = getImage("/images/sitRuim.png");
        lblTotal.setText("TOT: ");
        lblAtivo.setText("ATV: ");
        lblInativo.setText("INA: ");
        for (Cliente cli : lstCliente) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTableCad.getColumnModel();
            CRMClientesGeral.JTableRenderer renderers = new CRMClientesGeral.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = CreIcon;
            totalCliente++;
            if (cli.getSituacao().equals("INATIVO")) {
                inativo++;
                linha[0] = InaIcon;
            } else {
                if (cli.getSituacao().equals("ATIVO")) {
                    ativo++;
                    linha[0] = CreIcon;
                } else {
                    pre_inativo++;
                    linha[0] = PreIcon;
                }

            }
            linha[1] = cli.getCodigo_cliente();
            linha[2] = cli.getNome();
            linha[3] = cli.getEstado();
            linha[4] = cli.getSituacao();
            linha[5] = cli.getOrigem();
            linha[6] = cli.getData_ultimo_faturamentoS();
            linha[7] = cli.getDias_ultimo_faturamento();
            linha[8] = cli.getQuatidade_fat_ano();
            linha[9] = cli.getPedido();
            linha[10] = cli.getCadVendedor().getNome();
            linha[11] = cli.getCadVendedor().getCodigo();
            linha[12] = cli.getCadClienteGrupo().getCodgrp() + "-" + cli.getCadClienteGrupo().getNome();

            modeloCarga.addRow(linha);
        }
        // int totalCliente = ativo + inativo + pre_inativo;
        lblTotal.setText("TOT: " + totalCliente);
        lblAtivo.setText("ATV: " + ativo);
        lblInativo.setText("INA: " + inativo);
        lblPreInativo.setText("PRE: " + pre_inativo);

        barra.setString("Registro encontrados " + lstCliente.size());
    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCad.setRowHeight(40);
        jTableCad.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCad.setIntercellSpacing(new Dimension(1, 2));
        jTableCad.setAutoCreateRowSorter(true);
        // jTableCad.setAutoResizeMode(0);

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

    public void iniciarBarra(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Filtrando contas");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getClientes(PESQUISA_POR, PESQUISA);
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                // barra.setString("Filtro carregado");
            }
        };
        worker.execute();
    }
    private String PESQUISAR_POR;
    private String PESQUISAR;

    private void getSelecaoPlanta() {
        //

    }

    public void preencherComboFilial(Integer id) throws SQLException, Exception {
        //
    }

    private void pesquisarVendedor() {
        try {
            if (txtVendedor.getSelectedIndex() != -1) {
                if (!txtVendedor.getSelectedItem().toString().equals("0-SELECIONE")) {
                    txtSeguimento.setSelectedItem("0-SELECIONE");
                    txtStatus.setSelectedItem("0-SELECIONE");
                    txtEstado.setSelectedItem("0-SELECIONE");

                    this.statusSelecionado = "0-SELECIONE";
                    this.seguimentoSelecionado = "0-SELECIONE";

                    String cod = txtVendedor.getSelectedItem().toString();
                    int index = cod.indexOf("-");
                    String codcon = cod.substring(0, index);

                    if (!codcon.isEmpty()) {
                        this.vendedorSelecionado = codcon;
                        iniciarBarra("VENDEDOR", " \nand usu_codori in ('BA','BM')  \nand usu_codven  =" + codcon);
                    }

                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    private void pesquisarStatus() {

        try {
            if (!vendedorSelecionado.equals("0-SELECIONE")) {
                if (txtStatus.getSelectedIndex() != -1) {
                    if (!txtStatus.getSelectedItem().toString().equals("0-SELECIONE")) {
                        String sta = txtStatus.getSelectedItem().toString();
                        int index = sta.indexOf("-");
                        String status = sta.substring(0, index);
                        if (!status.isEmpty()) {
                            this.statusSelecionado = status;
                            iniciarBarra("VENDEDOR", " \nand usu_codori in ('BA','BM')  \nand usu_codven  =" + vendedorSelecionado + " and usu_sitcli ='" + status + "'");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    private void pesquisarSeguimento() {
        try {
            if (!vendedorSelecionado.equals("0-SELECIONE") || (this.statusSelecionado.equals("0-SELECIONE"))) {

                if (txtSeguimento.getSelectedIndex() != -1) {
                    if (!txtSeguimento.getSelectedItem().toString().equals("0-SELECIONE")) {
                        String seguimento = txtSeguimento.getSelectedItem().toString();

                        if (seguimento.equals("AUTO")) {
                            seguimento = "BA";
                        }
                        if (seguimento.equals("MOTO")) {
                            seguimento = "BM";
                        }
                        if (!seguimento.isEmpty()) {
                            this.seguimentoSelecionado = seguimento;
                            iniciarBarra("VENDEDOR", " \nand usu_codori in ('" + seguimentoSelecionado + "')  \nand usu_codven  =" + vendedorSelecionado + " and usu_sitcli ='" + statusSelecionado + "'");
                        }
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    private void pesquisarEstado() {
        try {

            if (txtEstado.getSelectedIndex() != -1) {
                if (!txtEstado.getSelectedItem().toString().equals("0-SELECIONE")) {
                    txtVendedor.setSelectedItem("0-SELECIONE");
                    txtSeguimento.setSelectedItem("0-SELECIONE");
                    txtStatus.setSelectedItem("0-SELECIONE");

                    this.vendedorSelecionado = "0-SELECIONE";
                    this.statusSelecionado = "0-SELECIONE";
                    this.seguimentoSelecionado = "0-SELECIONE";

                    String estado = txtEstado.getSelectedItem().toString();

                    if (!estado.isEmpty()) {

                        iniciarBarra("VENDEDOR", " \nand cli.sigufs in ('" + estado + "') and usu_codori in ('BA','BM')");
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    private void pesquisarGrupo() {
        try {

            if (txtGrupo.getSelectedIndex() != -1) {
                if (!txtGrupo.getSelectedItem().toString().equals("0-SELECIONE")) {
                    String gru = txtGrupo.getSelectedItem().toString();
                    int index = gru.indexOf("-");
                    String grupo = gru.substring(0, index);
                    if (!grupo.isEmpty()) {
                        iniciarBarra("VENDEDOR", " \nand cli.codgre in ('" + grupo.trim() + "') and usu_codori in ('BA','BM')");
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);

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
        btnConfirmar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCad = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtNome = new org.openswing.swing.client.TextControl();
        txtEstado = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtCliente = new org.openswing.swing.client.TextControl();
        barra = new javax.swing.JProgressBar();
        lblInativo = new javax.swing.JLabel();
        lblAtivo = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnTodos = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtVendedor = new javax.swing.JComboBox<>();
        txtStatus = new javax.swing.JComboBox<>();
        txtSeguimento = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblPreInativo = new javax.swing.JLabel();
        txtGrupo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carteira de Clientes");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        btnConfirmar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnConfirmar.setText("Atendimentos");
        btnConfirmar.setEnabled(false);
        btnConfirmar.setFocusCycleRoot(true);
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnCancelar.setText("Fechar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jTableCad.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Código", "Nome", "Estado", "Situação", "Linha", "Ultimo Fat", "Dias Fat", "Qtdy Ano", "Pedido", "Nome Vendedor", "Vendedor", "Grupo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false, false, false, false, false, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCadMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableCad);
        if (jTableCad.getColumnModel().getColumnCount() > 0) {
            jTableCad.getColumnModel().getColumn(0).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(1).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(3).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(4).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(5).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(8).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(9).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(10).setMinWidth(200);
            jTableCad.getColumnModel().getColumn(10).setPreferredWidth(200);
            jTableCad.getColumnModel().getColumn(10).setMaxWidth(200);
            jTableCad.getColumnModel().getColumn(11).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(11).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(11).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(12).setMinWidth(250);
            jTableCad.getColumnModel().getColumn(12).setPreferredWidth(250);
            jTableCad.getColumnModel().getColumn(12).setMaxWidth(250);
        }

        jLabel1.setText("Nome");

        txtNome.setEnabled(false);
        txtNome.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));
        txtEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstadoActionPerformed(evt);
            }
        });

        jLabel2.setText("Estado");

        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        lblInativo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblInativo.setForeground(new java.awt.Color(255, 51, 51));
        lblInativo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInativo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruby_delete.png"))); // NOI18N
        lblInativo.setText("INA: ");
        lblInativo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblAtivo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblAtivo.setForeground(new java.awt.Color(0, 204, 51));
        lblAtivo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAtivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        lblAtivo.setText("ATV:");
        lblAtivo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(51, 51, 255));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        lblTotal.setText("TOT:");
        lblTotal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnTodos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnTodos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder.png"))); // NOI18N
        btnTodos.setText("CARTEIRA");
        btnTodos.setFocusCycleRoot(true);
        btnTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTodosActionPerformed(evt);
            }
        });

        jLabel3.setText("Vendedor");

        txtVendedor.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtVendedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));
        txtVendedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVendedorActionPerformed(evt);
            }
        });

        txtStatus.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE", "A-ATIVO", "P-PRÉ-INATIVO", "I-INATICO" }));
        txtStatus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtStatusMouseClicked(evt);
            }
        });
        txtStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStatusActionPerformed(evt);
            }
        });

        txtSeguimento.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtSeguimento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE", "AUTO", "MOTO", "METAIS" }));
        txtSeguimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSeguimentoActionPerformed(evt);
            }
        });

        jLabel4.setText("Status");

        jLabel5.setText("Seguimento");

        jLabel6.setText("Cliente");

        lblPreInativo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPreInativo.setForeground(new java.awt.Color(51, 51, 51));
        lblPreInativo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPreInativo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        lblPreInativo.setText("PRE:");
        lblPreInativo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtGrupo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtGrupo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));
        txtGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGrupoActionPerformed(evt);
            }
        });

        jLabel7.setText("Grupo");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAtivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPreInativo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)
                        .addComponent(lblInativo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)
                        .addComponent(btnTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btnConfirmar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(barra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(2, 2, 2))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(txtGrupo, 0, 137, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVendedor, 0, 218, Short.MAX_VALUE)
                            .addComponent(jLabel3))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSeguimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(4, 4, 4)))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSeguimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(lblAtivo, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(lblInativo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTodos, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnConfirmar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(lblPreInativo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCancelar, btnConfirmar, btnTodos, lblAtivo, lblInativo, lblTotal});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCliente, txtEstado, txtNome});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed

        // VERIFICA SE O CLIENTE ESTA INADIMPLENTE
        Integer retorno = 0;
        try {
            retorno = this.clienteDAO.QuatidadeTituloVencido("", "and CODCLI =" + cliente.getCodigo_cliente());
        } catch (SQLException ex) {
            Logger.getLogger(CRMClientesGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (retorno == 0) {
            try {
                CRMClientesAtendimento sol = new CRMClientesAtendimento();
                MDIFrame.add(sol, true);
                sol.setMaximum(true); // executa maximizado 
                //  sol.setSize(800, 500);
                sol.setPosicao();
                sol.setRecebePalavraGeral(this, this.cliente, "", SITUACAO, AUTOMOTO);

            } catch (PropertyVetoException ex) {
                Logger.getLogger(CRMClientesGeral.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CRMClientesGeral.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Erro Cliente Inadimplente. [ " + retorno + " ] Titulos vencidos.",
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            btnConfirmar.setEnabled(false);
        }
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void jTableCadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCadMouseClicked
        int linhaSelSit = jTableCad.getSelectedRow();
        int colunaSelSit = jTableCad.getSelectedColumn();
        this.cliente = new Cliente();
        try {
            this.PESQUISAR_POR = " >0 ";
            this.cliente = clienteDAO.getClienteGeral("VENDEDOR", " and usu_codori in ('BA','BM') "
                    + "and usu_codcli = " + Integer.valueOf(jTableCad.getValueAt(linhaSelSit, 1).toString()));
            this.SITUACAO = jTableCad.getValueAt(linhaSelSit, 4).toString();
            this.AUTOMOTO = jTableCad.getValueAt(linhaSelSit, 5).toString();
            
            txtGrupo.setSelectedItem(jTableCad.getValueAt(linhaSelSit, 12).toString());
            
            if (this.AUTOMOTO.equals("BA")) {
                this.AUTOMOTO = "AUTO";
            }
            if (this.AUTOMOTO.equals("BM")) {
                this.AUTOMOTO = "MOTO";
            }
            btnConfirmar.setEnabled(false);
            if (cliente != null) {
                if (cliente.getCodigo_cliente() > 0) {
                    btnConfirmar.setText("Confirmar " + cliente.getCodigo_cliente());
                    txtCliente.setText(cliente.getCodigo_cliente().toString());
                    txtNome.setText(cliente.getNome());
                    btnConfirmar.setEnabled(true);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRMClientesGeral.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jTableCadMouseClicked

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
//
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        try {
            txtVendedor.setSelectedItem("0-SELECIONE");
            txtSeguimento.setSelectedItem("0-SELECIONE");
            txtStatus.setSelectedItem("0-SELECIONE");
            txtEstado.setSelectedItem("0-SELECIONE");

            this.vendedorSelecionado = "0-SELECIONE";
            this.statusSelecionado = "0-SELECIONE";
            this.seguimentoSelecionado = "0-SELECIONE";
            iniciarBarra("VENDEDOR", " \nand usu_codcli = " + txtCliente.getText());

        } catch (Exception ex) {
            Logger.getLogger(CRMClientesGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTodosActionPerformed
        try {
            txtVendedor.setSelectedItem("0-SELECIONE");
            txtSeguimento.setSelectedItem("0-SELECIONE");
            txtStatus.setSelectedItem("0-SELECIONE");
            txtEstado.setSelectedItem("0-SELECIONE");

            this.vendedorSelecionado = "0-SELECIONE";
            this.statusSelecionado = "0-SELECIONE";
            this.seguimentoSelecionado = "0-SELECIONE";
            iniciarBarra("VENDEDOR", " \nand usu_codori in ('BA','BM')");

        } catch (Exception ex) {
            Logger.getLogger(CRMClientesGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnTodosActionPerformed

    private void txtVendedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVendedorActionPerformed
        pesquisarVendedor();
    }//GEN-LAST:event_txtVendedorActionPerformed

    private void txtStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtStatusMouseClicked
        //
    }//GEN-LAST:event_txtStatusMouseClicked

    private void txtStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStatusActionPerformed
        pesquisarStatus();
    }//GEN-LAST:event_txtStatusActionPerformed

    private void txtSeguimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSeguimentoActionPerformed
        pesquisarSeguimento();
    }//GEN-LAST:event_txtSeguimentoActionPerformed

    private void txtEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstadoActionPerformed
        pesquisarEstado();
    }//GEN-LAST:event_txtEstadoActionPerformed

    private void txtGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGrupoActionPerformed
        //
    }//GEN-LAST:event_txtGrupoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       pesquisarGrupo();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnTodos;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableCad;
    private javax.swing.JLabel lblAtivo;
    private javax.swing.JLabel lblInativo;
    private javax.swing.JLabel lblPreInativo;
    private javax.swing.JLabel lblTotal;
    private org.openswing.swing.client.TextControl txtCliente;
    private javax.swing.JComboBox<String> txtEstado;
    private javax.swing.JComboBox<String> txtGrupo;
    private org.openswing.swing.client.TextControl txtNome;
    private javax.swing.JComboBox<String> txtSeguimento;
    private javax.swing.JComboBox<String> txtStatus;
    private javax.swing.JComboBox<String> txtVendedor;
    // End of variables declaration//GEN-END:variables
}
