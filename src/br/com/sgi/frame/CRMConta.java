/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Contas;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.ContasDAO;
import br.com.sgi.dao.UsuarioERPDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.ManipularRegistros;
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
import javax.swing.SwingWorker;
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
public final class CRMConta extends InternalFrame {

    private Contas contas;
    private List<Contas> listContas = new ArrayList<Contas>();
    private ContasDAO contasDAO;

    private UtilDatas utilDatas;

    private String dataI;
    private String dataF;

    public CRMConta() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Portal |  Contas"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (contasDAO == null) {
                contasDAO = new ContasDAO();
            }

            txtDataInicial.setDate(new Date());
            txtDataFinal.setDate(this.utilDatas.retornaDataFim(new Date()));

            dataI = this.utilDatas.converterDateToStr(new Date());

            dataF = this.utilDatas.converterDateToStr(txtDataFinal.getDate());
            this.PESQUISAR_POR = "";
            getUsuarioLogado();
            getContass("", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void pesquisarPorData(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        getContass(PESQUISA_POR, PESQUISA);
    }

    private void getContas(String codigocontas) throws SQLException {
        btnAlterar.setEnabled(false);
        btnAlterar.setText("Alterar");
        btnDuplicar.setEnabled(false);
        contas = new Contas();
        contas = contasDAO.getConta("", "and usu_codcli = " + codigocontas);
        if (contas != null) {
            if (contas.getUsu_codcli() > 0) {
                btnAlterar.setText("Alterar Registro" + contas.getUsu_codcli());
                btnAlterar.setEnabled(true);
                btnDuplicar.setEnabled(true);
              
            }
        }
    }

    private Usuario usuario;

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        UsuarioERPDAO dao = new UsuarioERPDAO();

        usuario = dao.getUsuario(Menu.username.toLowerCase());
        return usuario;
    }

    private void getContass(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        btnAlterar.setEnabled(false);
        btnAlterar.setText("Alterar");
        if (optMinhasContas.isSelected()) {
            PESQUISA = " and cli.usu_datcad >='" + dataI + "' and cli.usu_datcad <= '" + dataF + "'";
            PESQUISA += " \n and cli.usu_codusu =" + usuario.getId();
        }

        if (optTodas.isSelected()) {
            PESQUISA = "\nand cli.usu_datcad >='" + dataI + "' and cli.usu_datcad <= '" + dataF + "'";
        }
        if (!this.PESQUISAR_POR.isEmpty()) {
            PESQUISA += this.PESQUISAR_POR;
        }

        listContas = this.contasDAO.getContas(PESQUISA_POR, PESQUISA);
        if (listContas != null) {
            carregarTabela();
        }
    }

    private void getContasRetorno(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        btnAlterar.setEnabled(false);
        btnAlterar.setText("Alterar");
        if (optMinhasContas.isSelected()) {
            PESQUISA = " and cli.usu_datdis >='" + dataI + "' and cli.usu_datdis <= '" + dataF + "'";
            PESQUISA += " \n and cli.usu_codusu =" + usuario.getId();
        }

        if (optTodas.isSelected()) {
            PESQUISA = "\nand cli.usu_datdis >='" + dataI + "' and cli.usu_datdis <= '" + dataF + "'";
        }
        if (!this.PESQUISAR_POR.isEmpty()) {
            PESQUISA += this.PESQUISAR_POR;
        }

        listContas = this.contasDAO.getContas(PESQUISA_POR, PESQUISA);
        if (listContas != null) {
            carregarTabela();
        }
    }

    public void carregarTabela() throws Exception {

        int contador = 0;
        int qtdregistros = listContas.size();

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon CancIcon = getImage("/images/ruby_delete.png");

        lblTotal.setText("0");

        for (Contas mi : listContas) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new ContasEmbarque.ColorirRenderer();
            CRMConta.JTableRenderer renderers = new CRMConta.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = RuimIcon;
            if (mi.getUsu_sitcon().equals("RESOLVIDO")) {
                linha[0] = BomIcon;
            }
            if (mi.getUsu_sitcon().equals("CANCELADO")) {
                linha[0] = CancIcon;
            }
            linha[1] = mi.getUsu_codcli();
            linha[2] = mi.getUsu_nomcli();
            linha[3] = mi.getUsu_cidcli();
            linha[4] = mi.getUsu_concli();
            linha[5] = mi.getUsu_sitcon();
            linha[6] = mi.getUsu_datcadS();
            linha[7] = mi.getUsu_datdisS();
            linha[8] = mi.getUsu_codven() + "-" + mi.getCadRepresentante().getNome();
            contador++;
            modeloCarga.addRow(linha);
        }

        lblTotal.setText(String.valueOf(listContas.size()));
    }

    public void retornarContas(String PESQUISAR_POR, String PESQUISA) {
        try {
            btnAlterar.setEnabled(false);
            btnDuplicar.setEnabled(false);
            btnAlterar.setText("Alterar");
            getContass("", "");

        } catch (Exception ex) {

        }
    }
    private boolean AddRegEtq;

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
  
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
                jTableCarga.setRowHeight(40);
        jTableCarga.setIntercellSpacing(new Dimension(1, 2));
        jTableCarga.setAutoCreateRowSorter(true);
        

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

    private void novoRegistro(Contas contas, boolean acao) throws PropertyVetoException, Exception {
        CRMContaManutencao sol = new CRMContaManutencao();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

        sol.setRecebePalavra(this, contas, acao);
    }

    private String PESQUISAR_POR;

    private void getBuscarInformacoes() {
        this.PESQUISAR_POR = "";
        try {
            dataI = this.utilDatas.converterDateToStr(txtDataInicial.getDate());
            dataF = this.utilDatas.converterDateToStr(txtDataFinal.getDate());
            getContass(PESQUISAR_POR, PESQUISAR_POR);

        } catch (ParseException ex) {
            Logger.getLogger(CRMAgendas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CRMAgendas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getBuscarInformacoesRetorno() {
        this.PESQUISAR_POR = "";
        try {
            dataI = this.utilDatas.converterDateToStr(txtDataInicial.getDate());
            dataF = this.utilDatas.converterDateToStr(txtDataFinal.getDate());
            getContasRetorno(PESQUISAR_POR, PESQUISAR_POR);

        } catch (ParseException ex) {
            Logger.getLogger(CRMAgendas.class.getName()).log(Level.SEVERE, null, ex);
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
        buttonGroup2 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        btnFiltrar = new javax.swing.JButton();
        txtCodigo = new org.openswing.swing.client.TextControl();
        txtNome = new org.openswing.swing.client.TextControl();
        jButton1 = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        optMinhasContas = new javax.swing.JRadioButton();
        optTodas = new javax.swing.JRadioButton();
        txtDataInicial = new org.openswing.swing.client.DateControl();
        txtDataFinal = new org.openswing.swing.client.DateControl();
        btnCadastro = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        btnRetorno = new javax.swing.JButton();
        btnDuplicar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações"));
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Código", "Nome", "Cidade", "Contato", "Situacao", "Cadastro", "Retorno", "Vendedor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(150);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(150);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(200);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(200);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(200);
        }

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        jButton1.setText("Novo");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnAlterar.setText("Alterar");
        btnAlterar.setEnabled(false);
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        buttonGroup1.add(optMinhasContas);
        optMinhasContas.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optMinhasContas.setSelected(true);
        optMinhasContas.setText("Minhas contas");
        optMinhasContas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optMinhasContasActionPerformed(evt);
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

        txtDataInicial.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        txtDataFinal.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        btnCadastro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnCadastro.setText("Filtrar Cadastro");
        btnCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastroActionPerformed(evt);
            }
        });

        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(102, 102, 255));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user_suit.png"))); // NOI18N
        lblTotal.setText("0");
        lblTotal.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        lblTotal.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        btnRetorno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        btnRetorno.setText("Filtrar Retorno");
        btnRetorno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRetornoActionPerformed(evt);
            }
        });

        btnDuplicar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-e-fechar16x16.png"))); // NOI18N
        btnDuplicar.setText("Duplicar");
        btnDuplicar.setEnabled(false);
        btnDuplicar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDuplicarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(optMinhasContas, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optTodas, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCadastro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDuplicar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAlterar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane3)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(optMinhasContas)
                        .addComponent(optTodas, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDuplicar)
                    .addComponent(jButton1)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAlterar)))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal)
                    .addComponent(btnFiltrar)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAlterar, btnCadastro, btnFiltrar, jButton1, optMinhasContas, optTodas, txtDataFinal, txtDataInicial});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblTotal, txtNome});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnDuplicar, btnRetorno});

        jTabbedPane1.addTab("Contas", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1109, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String codigolancamento;
    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();
        codigolancamento = jTableCarga.getValueAt(linhaSelSit, 1).toString();

        //  btnManutencao.setEnabled(false);
        if (evt.getClickCount() == 2) {
            try {
                getContas(codigolancamento);
            } catch (SQLException ex) {
                Logger.getLogger(CRMConta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        try {

            getContass("", "");

        } catch (Exception ex) {

        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            Contas contas = new Contas();
            novoRegistro(contas, false);
        } catch (Exception ex) {
            Logger.getLogger(CRMConta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        try {
            novoRegistro(contas, false);
        } catch (Exception ex) {
            Logger.getLogger(CRMConta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        if (!txtCodigo.getText().isEmpty()) {
            try {
                this.PESQUISAR_POR = "\nand usu_codcli = " + txtCodigo.getText();
                getContass(PESQUISAR_POR, PESQUISAR_POR);

            } catch (Exception ex) {
                Logger.getLogger(CRMConta.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "ERRO: Informe o código ",
                    "Atenção", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        if (!txtNome.getText().isEmpty()) {
            try {

                this.PESQUISAR_POR = "\nand usu_nomcli like ('%" + txtNome.getText() + "%')";
                getContass(PESQUISAR_POR, PESQUISAR_POR);

            } catch (Exception ex) {
                Logger.getLogger(CRMConta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
              JOptionPane.showMessageDialog(null, "ERRO: Informe o nome " ,
                    "Atenção", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtNomeActionPerformed

    private void optMinhasContasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optMinhasContasActionPerformed
        getBuscarInformacoes();
    }//GEN-LAST:event_optMinhasContasActionPerformed

    private void optTodasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optTodasActionPerformed
        getBuscarInformacoes();
    }//GEN-LAST:event_optTodasActionPerformed

    private void btnCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastroActionPerformed
        getBuscarInformacoes();

    }//GEN-LAST:event_btnCadastroActionPerformed

    private void btnRetornoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRetornoActionPerformed
        getBuscarInformacoesRetorno();
    }//GEN-LAST:event_btnRetornoActionPerformed

    private void btnDuplicarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDuplicarActionPerformed
        if (ManipularRegistros.pesos(" Deseja duplicar esse registro ")) {
            try {
                novoRegistro(contas, true);
            } catch (Exception ex) {
                Logger.getLogger(CRMConta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnDuplicarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnCadastro;
    private javax.swing.JButton btnDuplicar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnRetorno;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JRadioButton optMinhasContas;
    private javax.swing.JRadioButton optTodas;
    private org.openswing.swing.client.TextControl txtCodigo;
    private org.openswing.swing.client.DateControl txtDataFinal;
    private org.openswing.swing.client.DateControl txtDataInicial;
    private org.openswing.swing.client.TextControl txtNome;
    // End of variables declaration//GEN-END:variables
}
