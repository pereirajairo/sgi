/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Atendimento;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.AtendimentoDAO;
import br.com.sgi.dao.ClienteDAO;
import br.com.sgi.dao.UsuarioERPDAO;
import br.com.sgi.main.Menu;
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

/**
 *
 * @author jairosilva
 */
public final class CRMAgendas extends InternalFrame {

    private Cliente cliente;
    private ClienteDAO clienteDAO;
    private List<Cliente> lstCliente = new ArrayList<Cliente>();

    private UtilDatas utilDatas;
    // private Sucatas veioCampo;

    private Atendimento atendimento;
    private AtendimentoDAO atendimentoDAO;

    private String dataI;
    private String dataF;

    public CRMAgendas() {
        try {
            initComponents();

            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }

            if (clienteDAO == null) {
                this.clienteDAO = new ClienteDAO();
            }
            if (atendimentoDAO == null) {
                this.atendimentoDAO = new AtendimentoDAO();
            }
            txtDataInicial.setDate(new Date());
            txtDataFinal.setDate(this.utilDatas.retornaDataFim(new Date()));

            dataI = this.utilDatas.converterDateToStr(new Date());

            dataF = this.utilDatas.converterDateToStr(txtDataFinal.getDate());
            getUsuarioLogado();
            getLancamento(PESQUISAR_POR, "");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }
    private Usuario usuario;

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        UsuarioERPDAO dao = new UsuarioERPDAO();

        usuario = dao.getUsuario(Menu.username.toLowerCase());
        return usuario;
    }

    private void getLancamento(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        List<Atendimento> lst = new ArrayList<Atendimento>();
        if (optMinhasAgenda.isSelected()) {
            PESQUISA = "\nand obs.datprx >='" + dataI + "' and obs.datprx <= '" + dataF + "'";
            PESQUISA += " \nand obs.obsusu =" + usuario.getId();
        }
        if (optMinhasLigacoes.isSelected()) {
            PESQUISA = "\nand obs.obsdat >='" + dataI + "' and obs.obsdat <= '" + dataF + "'";
            PESQUISA += " \nand obs.obsusu =" + usuario.getId();
        }

        if (optTodas.isSelected()) {

            PESQUISA = "\nand obs.obsdat >='" + dataI + "' and obs.obsdat <= '" + dataF + "'";

        }
        lst = atendimentoDAO.getAtendimentos(PESQUISA_POR, PESQUISA);
        if (lst != null) {
            carregarTabelaAtendimento(lst);
        }

    }

    public void carregarTabelaAtendimento(List<Atendimento> lst) throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableLancamento.getModel();
        modeloCarga.setNumRows(0);
        jTableLancamento.setRowHeight(40);
        jTableLancamento.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon CreIcon = getImage("/images/sitRuim.png");
        ImageIcon sitSai = getImage("/images/sitBom.png");
        String situacao = "";
        for (Atendimento cli : lst) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTableLancamento.getColumnModel();
            CRMAgendas.JTableRenderer renderers = new CRMAgendas.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            if (!cli.getSituacaoobservacao().equals("R")) {
                linha[0] = CreIcon;
                situacao = "SEM SOLUÇÃO";
            } else {
                linha[0] = sitSai;
                situacao = "OK";
            }
            linha[1] = cli.getCodigocliente();
            linha[2] = cli.getCliente().getNome();
            linha[3] = cli.getSequencialancamento();
            linha[4] = cli.getMotivoobservacao() + " - " + cli.getMotivo().getDescricao();
            linha[5] = cli.getSituacaoobservacao();
            linha[6] = cli.getDatalancamento();
            linha[7] = cli.getDatavista();
            linha[8] = situacao;
            modeloCarga.addRow(linha);
        }
        lblTotal.setText("TOT: " + jTableLancamento.getRowCount());

    }

    private void getClientes(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (!PESQUISA.isEmpty()) {

            lstCliente = this.clienteDAO.getClientes(this.PESQUISAR_POR, PESQUISA);
            if (lstCliente != null) {
                carregarTabela();
            }

        }
    }

    public void carregarTabela() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableLancamento.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        int ativo = 0;
        int inativo = 0;
        ImageIcon CreIcon = getImage("/images/sitBom.png");
        ImageIcon InaIcon = getImage("/images/sitRuim.png");
        lblTotal.setText("TOT: ");

        for (Cliente cli : lstCliente) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableLancamento.getColumnModel();
            CRMAgendas.JTableRenderer renderers = new CRMAgendas.JTableRenderer();
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
            modeloCarga.addRow(linha);
        }
        int totalCliente = ativo + inativo;
        lblTotal.setText("TOT: " + totalCliente);

    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableLancamento.setRowHeight(40);
        jTableLancamento.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableLancamento.setIntercellSpacing(new Dimension(1, 2));
        jTableLancamento.setAutoCreateRowSorter(true);

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

    private String PESQUISAR_POR;

    private void getBuscarInformacoes() {
        try {
            dataI = this.utilDatas.converterDateToStr(txtDataInicial.getDate());
            dataF = this.utilDatas.converterDateToStr(txtDataFinal.getDate());

            getLancamento(PESQUISAR_POR, " \nand obs.datprx >='" + dataI + "' and obs.datprx <= '" + dataF + "'");

        } catch (ParseException ex) {
            Logger.getLogger(CRMAgendas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CRMAgendas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getAlterarLancamento() {

        try {
            CRMClientesAtendimento sol = new CRMClientesAtendimento();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado 
            //  sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavraAgendas(this, cliente, title);

        } catch (PropertyVetoException ex) {
            Logger.getLogger(CRMAgendas.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CRMAgendas.class.getName()).log(Level.SEVERE, null, ex);
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
        jTableLancamento = new javax.swing.JTable();
        lblTotal = new javax.swing.JLabel();
        txtDataInicial = new org.openswing.swing.client.DateControl();
        txtDataFinal = new org.openswing.swing.client.DateControl();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnSucCli = new javax.swing.JButton();
        optMinhasAgenda = new javax.swing.JRadioButton();
        optMinhasLigacoes = new javax.swing.JRadioButton();
        optTodas = new javax.swing.JRadioButton();
        txtVendedor = new javax.swing.JComboBox<>();
        txtSeguimento = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Agendas");
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

        jTableLancamento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Código", "Nome", "Sequencia", "Motivo", "Situação", "Lançamento", "Visita", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLancamento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableLancamentoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableLancamento);
        if (jTableLancamento.getColumnModel().getColumnCount() > 0) {
            jTableLancamento.getColumnModel().getColumn(0).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(1).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(3).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(5).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(6).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(7).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(8).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(8).setMaxWidth(100);
        }

        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(51, 51, 255));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        lblTotal.setText("TOT:");
        lblTotal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel1.setText("Inicio");

        jLabel4.setText("Fim");

        btnSucCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnSucCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSucCliActionPerformed(evt);
            }
        });

        buttonGroup1.add(optMinhasAgenda);
        optMinhasAgenda.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optMinhasAgenda.setSelected(true);
        optMinhasAgenda.setText("Minhas agendas");
        optMinhasAgenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optMinhasAgendaActionPerformed(evt);
            }
        });

        buttonGroup1.add(optMinhasLigacoes);
        optMinhasLigacoes.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optMinhasLigacoes.setText("Minhas ligações");
        optMinhasLigacoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optMinhasLigacoesActionPerformed(evt);
            }
        });

        buttonGroup1.add(optTodas);
        optTodas.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optTodas.setText("Todas");
        optTodas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optTodasActionPerformed(evt);
            }
        });

        txtVendedor.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtVendedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));

        txtSeguimento.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtSeguimento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE", "AUTO", "MOTO", "METAIS" }));

        jLabel2.setText("Vendedor");

        jLabel3.setText("Seguimento");

        txtStatus.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE", "1-ATIVO", "2-PRÉ-INATIVO", "3-INATICO" }));

        jLabel5.setText("Status");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConfirmar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar)
                .addGap(2, 2, 2))
            .addGroup(layout.createSequentialGroup()
                .addComponent(optMinhasAgenda, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(optMinhasLigacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(optTodas, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSucCli, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVendedor, 0, 1, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSeguimento, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(optMinhasAgenda)
                            .addComponent(optMinhasLigacoes)
                            .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSucCli, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(optTodas, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                                .addComponent(btnConfirmar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)))
                        .addGap(2, 2, 2))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSeguimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCancelar, btnConfirmar, lblTotal});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnSucCli, txtDataFinal, txtDataInicial});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {optMinhasAgenda, optMinhasLigacoes, optTodas});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed

        getAlterarLancamento();

    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void jTableLancamentoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableLancamentoMouseClicked
        if (evt.getClickCount() == 2) {
            int linhaSelSit = jTableLancamento.getSelectedRow();
            int colunaSelSit = jTableLancamento.getSelectedColumn();
            this.cliente = new Cliente();
            try {
                this.PESQUISAR_POR = " >0 ";
                this.cliente = clienteDAO.getCliente(PESQUISAR_POR, " and codori in ('BA','BM') and codcli = " + Integer.valueOf(jTableLancamento.getValueAt(linhaSelSit, 1).toString()));
                if (cliente == null) {

                    this.cliente = clienteDAO.getClienteMovimento(PESQUISAR_POR, " and codcli = " + Integer.valueOf(jTableLancamento.getValueAt(linhaSelSit, 1).toString()) + "");

                }

                btnConfirmar.setEnabled(false);

                if (cliente != null) {
                    if (cliente.getCodigo_cliente() > 0) {
                        btnConfirmar.setText("Confirmar " + cliente.getCodigo_cliente());

                        btnConfirmar.setEnabled(true);
                     //   getAlterarLancamento();
                    }

                }
            } catch (SQLException ex) {
                Logger.getLogger(CRMAgendas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }//GEN-LAST:event_jTableLancamentoMouseClicked

    private void btnSucCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSucCliActionPerformed
        getBuscarInformacoes();


    }//GEN-LAST:event_btnSucCliActionPerformed

    private void optMinhasAgendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optMinhasAgendaActionPerformed
        getBuscarInformacoes();
    }//GEN-LAST:event_optMinhasAgendaActionPerformed

    private void optMinhasLigacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optMinhasLigacoesActionPerformed
        getBuscarInformacoes();

    }//GEN-LAST:event_optMinhasLigacoesActionPerformed

    private void optTodasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optTodasActionPerformed
        getBuscarInformacoes();

    }//GEN-LAST:event_optTodasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnSucCli;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableLancamento;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JRadioButton optMinhasAgenda;
    private javax.swing.JRadioButton optMinhasLigacoes;
    private javax.swing.JRadioButton optTodas;
    private org.openswing.swing.client.DateControl txtDataFinal;
    private org.openswing.swing.client.DateControl txtDataInicial;
    private javax.swing.JComboBox<String> txtSeguimento;
    private javax.swing.JComboBox<String> txtStatus;
    private javax.swing.JComboBox<String> txtVendedor;
    // End of variables declaration//GEN-END:variables
}
