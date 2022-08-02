/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.BaseEstado;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Representante;
import br.com.sgi.dao.ClienteDAO;
import br.com.sgi.dao.FilialDAO;
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
public final class CRMClientes extends InternalFrame {

    private Cliente cliente;
    private ClienteDAO clienteDAO;
    private List<Cliente> lstCliente = new ArrayList<Cliente>();

    private UtilDatas utilDatas;
    // private Sucatas veioCampo;
    private String SITUACAO;
    private String AUTOMOTO;

    public CRMClientes() {
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

    private void getClientes(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (!PESQUISA.isEmpty()) {
            getSelecaoPlanta();

            if (this.PESQUISAR_POR.equals("MOV")) {
                lstCliente = this.clienteDAO.getClientesMovimento(this.PESQUISAR_POR, PESQUISA);
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
        ImageIcon CreIcon = getImage("/images/sitBom.png");
        ImageIcon InaIcon = getImage("/images/sitRuim.png");
        lblTotal.setText("TOT: ");
        lblAtivo.setText("ATV: ");
        lblInativo.setText("INA: ");
        for (Cliente cli : lstCliente) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableCad.getColumnModel();
            CRMClientes.JTableRenderer renderers = new CRMClientes.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = CreIcon;
            if (cli.getSituacao().equals("INATIVO")) {
                inativo++;
                linha[0] = InaIcon;
            } else {
                ativo++;
            }
            linha[1] = cli.getCodigo_cliente();
            linha[2] = cli.getNome();
            linha[3] = cli.getEstado();
            linha[4] = cli.getSituacao();
            linha[5] = cli.getOrigem();
            linha[6] = cli.getData_ultimo_faturamentoS();
            linha[7] = cli.getDias_ultimo_faturamento();
            linha[8] = cli.getCadVendedor().getNome();
            linha[9] = cli.getCadVendedor().getCodigo();

            modeloCarga.addRow(linha);
        }
        int totalCliente = ativo + inativo;
        lblTotal.setText("TOT: " + totalCliente);
        lblAtivo.setText("ATV: " + ativo);
        lblInativo.setText("INA: " + inativo);

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
        if (chkFab.isSelected()) {
            PESQUISAR_POR = " not in (10,11,12,13,14,90,91,92,93,94,94,95,96,97,98,99)";
        } else {
            if (chkCd.isSelected()) {
                PESQUISAR_POR = "  in (10,11,12,13,14,90,91,92,93,94,94,95,96,97,98,99)";
                String filial = txtFilial.getSelectedItem().toString();
                int index = filial.indexOf("-");
                String filialSelecionada = filial.substring(0, index).trim();
                if (!filialSelecionada.equals("0")) {
                    PESQUISAR_POR = " in (" + filialSelecionada + ")";
                }
            } else if (chkMovimentacao.isSelected()) {
                PESQUISAR_POR = "MOV";

            }
        }

    }

    public void preencherComboFilial(Integer id) throws SQLException, Exception {
        FilialDAO filialDAO = new FilialDAO();
        List<Filial> listFilial = new ArrayList<Filial>();
        String cod;
        String des;
        String desger;
        txtFilial.removeAllItems();
        txtFilial.addItem("0 - SELECIONE");
        listFilial = filialDAO.getFilias("", " and codemp = 1 and codfil in (10, 11, 12, 13, 14, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99)");
        if (listFilial != null) {
            for (Filial filial : listFilial) {
                cod = filial.getFilial().toString();
                des = filial.getRazao_social();
                desger = cod + " - " + des;
                txtFilial.addItem(desger);
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
        jLabel12 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtNome = new org.openswing.swing.client.TextControl();
        txtEstado = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        btnSucCli1 = new javax.swing.JButton();
        txtCliente = new org.openswing.swing.client.TextControl();
        barra = new javax.swing.JProgressBar();
        lblInativo = new javax.swing.JLabel();
        lblAtivo = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnMoto = new javax.swing.JButton();
        btnAuto = new javax.swing.JButton();
        btnTodos = new javax.swing.JButton();
        chkFab = new javax.swing.JRadioButton();
        chkCd = new javax.swing.JRadioButton();
        txtFilial = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtVendedor = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        chkMovimentacao = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Clientes");
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
                "#", "Código", "Nome", "Estado", "Situação", "Linha", "Ultimo Fat", "Dias Fat", "Nome Vendedor", "Vendedor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, true, true, true
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
            jTableCad.getColumnModel().getColumn(8).setMinWidth(150);
            jTableCad.getColumnModel().getColumn(8).setPreferredWidth(150);
            jTableCad.getColumnModel().getColumn(8).setMaxWidth(150);
            jTableCad.getColumnModel().getColumn(9).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(9).setMaxWidth(100);
        }

        jLabel12.setText("Cliente");

        jLabel1.setText("Nome");

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));

        jLabel2.setText("Estado");

        btnSucCli1.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        btnSucCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnSucCli1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSucCli1ActionPerformed(evt);
            }
        });

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

        btnMoto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnMoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        btnMoto.setText("Moto");
        btnMoto.setFocusCycleRoot(true);
        btnMoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMotoActionPerformed(evt);
            }
        });

        btnAuto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnAuto.setText("Auto");
        btnAuto.setFocusCycleRoot(true);
        btnAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutoActionPerformed(evt);
            }
        });

        btnTodos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnTodos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder.png"))); // NOI18N
        btnTodos.setText("CARTEIRA");
        btnTodos.setFocusCycleRoot(true);
        btnTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTodosActionPerformed(evt);
            }
        });

        buttonGroup1.add(chkFab);
        chkFab.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkFab.setForeground(new java.awt.Color(0, 153, 0));
        chkFab.setSelected(true);
        chkFab.setText("Fabrica");
        chkFab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFabActionPerformed(evt);
            }
        });

        buttonGroup1.add(chkCd);
        chkCd.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkCd.setForeground(new java.awt.Color(255, 51, 51));
        chkCd.setText("Centro distribuição");
        chkCd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCdActionPerformed(evt);
            }
        });

        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtFilial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 - SELECIONE" }));

        jLabel3.setText("Vendedor");

        txtVendedor.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtVendedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user_suit.png"))); // NOI18N
        jButton1.setText("Filtrar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Centro de distribuição");

        buttonGroup1.add(chkMovimentacao);
        chkMovimentacao.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkMovimentacao.setText("Sem compra");

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
                        .addComponent(lblInativo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoto, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(145, 145, 145))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkFab, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkCd, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkMovimentacao, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtFilial, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(txtVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButton1)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnSucCli1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel2)))))))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(chkMovimentacao, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkCd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkFab, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(lblAtivo, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(lblInativo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTodos, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMoto, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfirmar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addGap(2, 2, 2))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSucCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAuto, btnCancelar, btnConfirmar, btnMoto, btnTodos, lblAtivo, lblInativo, lblTotal});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnSucCli1, txtCliente, txtEstado, txtFilial, txtNome});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, txtVendedor});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed

        // VERIFICA SE O CLIENTE ESTA INADIMPLENTE
        Integer retorno = 0;
//        if (chkMovimentacao.isSelected()) {
//            try {
//                retorno = this.clienteDAO.QuatidadeTituloVencido("", "and CODCLI =" + cliente.getCodigo_cliente());
//            } catch (SQLException ex) {
//                Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        try {
            retorno = this.clienteDAO.QuatidadeTituloVencido("", "and CODCLI =" + cliente.getCodigo_cliente());
        } catch (SQLException ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (retorno == 0) {
            try {
                CRMClientesAtendimento sol = new CRMClientesAtendimento();
                MDIFrame.add(sol, true);
                sol.setMaximum(true); // executa maximizado 
                //  sol.setSize(800, 500);
                sol.setPosicao();
                sol.setRecebePalavra(this, this.cliente, "", SITUACAO, AUTOMOTO);

            } catch (PropertyVetoException ex) {
                Logger.getLogger(CRMClientes.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
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

            if (chkMovimentacao.isSelected()) {
                this.cliente = clienteDAO.getClienteMovimento(PESQUISAR_POR, "  and codcli = " + Integer.valueOf(jTableCad.getValueAt(linhaSelSit, 1).toString()));

            } else {
                this.cliente = clienteDAO.getCliente(PESQUISAR_POR, " and codori in ('BA','BM') and codcli = " + Integer.valueOf(jTableCad.getValueAt(linhaSelSit, 1).toString()));

            }

            this.SITUACAO = jTableCad.getValueAt(linhaSelSit, 4).toString();
            this.AUTOMOTO = jTableCad.getValueAt(linhaSelSit, 5).toString();
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
                    txtEstado.setSelectedItem(cliente.getEstado());
                    btnConfirmar.setEnabled(true);
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jTableCadMouseClicked

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        try {
            //   getClientes("", " and nomcli like '%" + txtNome.getText().trim() + "%'");
            if (!txtNome.getText().isEmpty()) {
                if (chkMovimentacao.isSelected()) {
                    iniciarBarra("", "\n and nomcli like '%" + txtNome.getText().trim() + "%'");
                } else {
                    iniciarBarra("", "\nand codori in ('BA','BM') \n  and nomcli like '%" + txtNome.getText().trim() + "%'");
                }

                //iniciarBarra("", " \nand codori in ('BA','BM')  \nand nomcli like '%" + txtNome.getText().trim() + "%'");
            }

        } catch (SQLException ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtNomeActionPerformed

    private void btnSucCli1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSucCli1ActionPerformed
        try {

            if (chkMovimentacao.isSelected()) {
                iniciarBarra("", " \nand sigufs like '%" + txtEstado.getSelectedItem().toString() + "%'");
            } else {
                iniciarBarra("", " \nand codori in ('BA','BM') \nand sigufs like '%" + txtEstado.getSelectedItem().toString() + "%'");

            }

        } catch (SQLException ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSucCli1ActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        try {

            if (!txtCliente.getText().isEmpty()) {
                if (chkMovimentacao.isSelected()) {
                  //  iniciarBarra("", "\n and codcli like '%" + txtCliente.getText().trim() + "%'");
                    iniciarBarra("", "\n and codcli = '" + txtCliente.getText().trim() + "'");
                } else {
                   // iniciarBarra("", "\nand codori in ('BA','BM') \n  "
                    //        + "and codcli like '%" + txtCliente.getText().trim() + "%'");
                    
                     iniciarBarra("", "\nand codori in ('BA','BM') \n  "
                            + "and codcli = '" + txtCliente.getText().trim() + "'");
                    
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnMotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMotoActionPerformed
        try {
            if (chkFab.isSelected()) {
                getSelecaoPlanta();
                iniciarBarra("", " AND codori in ('BM')");
            }
            if (chkCd.isSelected()) {
                getSelecaoPlanta();
                iniciarBarra("", " AND codori in ('BM')");
            }
            if (chkMovimentacao.isSelected()) {
                JOptionPane.showMessageDialog(null, "Opção não liberada para cadastro(s) sem compras.  ",
                        "Erro: ", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnMotoActionPerformed

    private void btnAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutoActionPerformed
        try {
            if (chkFab.isSelected()) {
                getSelecaoPlanta();
                iniciarBarra("", " AND codori in ('BA')");
            }
            if (chkCd.isSelected()) {
                getSelecaoPlanta();
                iniciarBarra("", " AND codori in ('BA')");
            }
            if (chkMovimentacao.isSelected()) {
                JOptionPane.showMessageDialog(null, "Opção não liberada para cadastro(s) sem compras.  ",
                        "Erro: ", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAutoActionPerformed

    private void btnTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTodosActionPerformed
        try {
            if (chkMovimentacao.isSelected()) {
                JOptionPane.showMessageDialog(null, "Opção não liberada para cadastro(s) sem compras.  ",
                        "Erro: ", JOptionPane.ERROR_MESSAGE);
            } else {
                iniciarBarra(PESQUISAR_POR, "AND codori in ('BA', 'BM')");
            }

        } catch (Exception ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnTodosActionPerformed

    private void chkCdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCdActionPerformed
        try {
            preencherComboFilial(1);
        } catch (Exception ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_chkCdActionPerformed

    private void chkFabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFabActionPerformed
        txtFilial.removeAllItems();
        txtFilial.addItem("0 - SELECIONE");
    }//GEN-LAST:event_chkFabActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            if (chkMovimentacao.isSelected()) {
                JOptionPane.showMessageDialog(null, "Opção não liberada para cadastro(s) sem compras.  ",
                        "Erro: ", JOptionPane.ERROR_MESSAGE);
            } else {
                String cod = txtVendedor.getSelectedItem().toString();
                int index = cod.indexOf("-");
                String codcon = cod.substring(0, index);
                if (!codcon.isEmpty()) {
                    iniciarBarra("", " \nand codori in ('BA','BM')  \nand codven  =" + codcon);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CRMClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnAuto;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnMoto;
    private javax.swing.JButton btnSucCli1;
    private javax.swing.JButton btnTodos;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton chkCd;
    private javax.swing.JRadioButton chkFab;
    private javax.swing.JRadioButton chkMovimentacao;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableCad;
    private javax.swing.JLabel lblAtivo;
    private javax.swing.JLabel lblInativo;
    private javax.swing.JLabel lblTotal;
    private org.openswing.swing.client.TextControl txtCliente;
    private javax.swing.JComboBox<String> txtEstado;
    private javax.swing.JComboBox<String> txtFilial;
    private org.openswing.swing.client.TextControl txtNome;
    private javax.swing.JComboBox<String> txtVendedor;
    // End of variables declaration//GEN-END:variables
}
