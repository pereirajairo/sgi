/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.OrdemCompra;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Produto;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.dao.OrdemCompraDAO;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.UtilDatas;
import java.awt.Dimension;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public class IntegrarOrdemCompraGeral extends InternalFrame {

    private boolean addNewReg;
    private boolean showMsgErros;
    private IntegrarPesosRegistrar veioCampo;
    private OrdemCompraDAO ordemcompraDAO;
    private List<OrdemCompra> lstOrdemCompra = new ArrayList<OrdemCompra>();
    private UtilDatas utilDatas;
    private String datacorte;

    private SucataMovimento sucataMovimento;
    private List<SucataMovimento> listSucataMovimento = new ArrayList<SucataMovimento>();
    private SucataMovimentoDAO sucataMovimentoDAO;

    private List<Pedido> listPedido = new ArrayList<Pedido>();
    private PedidoDAO pedidoDAO;
    private Pedido pedido;

    private String processo;
    private String complemento;

    public IntegrarOrdemCompraGeral() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Ordem de Compra"));
            this.setSize(800, 500);
            this.ordemcompraDAO = new OrdemCompraDAO();
            this.utilDatas = new UtilDatas();
            Date data = utilDatas.getDataRetorno(-60);
            txtDataCorte.setValue(data);
            //   getListar("", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String retornarData() throws ParseException {

        datacorte = utilDatas.converterDateToStr(txtDataCorte.getDate());
        return datacorte;
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void setRecebePalavra(IntegrarPesosRegistrar veioInput, String tipo) {
        btnPesquisar.setEnabled(false);
        this.veioCampo = veioInput;
        btnAut.setEnabled(true);
        btnInd.setEnabled(true);
        if (tipo.equals("INDUSTRIALIZAÇÃO")) {
            btnInd.setEnabled(true);
            try {
                getListar("ORDEMCOMPRA", " and ocp.tnspro = '90419'");
            } catch (Exception ex) {
                Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (tipo.equals("ECO")) {
            btnAut.setEnabled(true);
            try {
                getListar("ORDEMCOMPRA", " and ocp.tnspro = '90417'");
            } catch (Exception ex) {
                Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return;
    }

    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        retornarData();
        lstOrdemCompra = this.ordemcompraDAO.getOrdemCompras(this.datacorte, PESQUISA);
        if (lstOrdemCompra != null) {
            carregarTabela();
        }

    }

    public void carregarTabela() throws Exception {
        jTableInfo.setRowHeight(32);
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableInfo.getModel();
        modeloCarga.setNumRows(0);

        for (OrdemCompra oc : lstOrdemCompra) {
            Object[] linha = new Object[15];

            // popula as colunas
            linha[0] = oc.getNumeroOrdemCompra();
            linha[1] = oc.getCodigoFornecedor();
            linha[2] = oc.getNomeFornecedor();
            linha[3] = oc.getDataemissao();
            linha[4] = oc.getEmpresa();
            linha[5] = oc.getFilial();
            linha[6] = oc.getCnpj();
            linha[8] = oc.getQuantidade();
            linha[9] = oc.getCidade();
            linha[10] = oc.getEstado();
            linha[11] = " ";
            linha[12] = " ";

            modeloCarga.addRow(linha);
        }

    }

    public void getListarRegistroSucata(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        this.sucataMovimentoDAO = new SucataMovimentoDAO();
        PESQUISA += "\nand usu_debcre  in ('3 - DEBITO')  and usu_numocp > 0";
        listSucataMovimento = this.sucataMovimentoDAO.getSucatasMovimentoOcp(PESQUISA_POR, PESQUISA);
        if (listSucataMovimento != null) {
            carregarTabelaSucataRegistro();
        }

    }

    public void carregarTabelaSucataRegistro() throws Exception {

        DefaultTableModel modeloCarga = (DefaultTableModel) jTableInfo.getModel();
        modeloCarga.setNumRows(0);

        for (SucataMovimento oc : listSucataMovimento) {
            Object[] linha = new Object[15];

            linha[0] = oc.getOrdemcompra();
            linha[1] = oc.getCliente();
            linha[2] = oc.getCadCliente().getNome();
            linha[3] = oc.getDatageracaoS();
            linha[4] = oc.getEmpresa();
            linha[5] = oc.getFilial();
            linha[6] = oc.getCadCliente().getCpfcnpj();
            linha[8] = oc.getPesoordemcompra();
            linha[9] = oc.getCadCliente().getCidade();
            linha[10] = oc.getCadCliente().getEstado();
            linha[11] = " ";
            linha[12] = " ";

            modeloCarga.addRow(linha);
        }

    }

    public void getListarPedidos(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        this.pedidoDAO = new PedidoDAO();
        PESQUISA += "";
        listPedido = this.pedidoDAO.getPedidosIndustrializacao(PESQUISA_POR, PESQUISA);
        if (listPedido != null) {
            carregarTabelaPedidos();
        }

    }

    public void carregarTabelaPedidos() throws Exception {
        jTableInfo.setRowHeight(40);
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableInfo.getModel();
        modeloCarga.setNumRows(0);

        for (Pedido pe : listPedido) {
            Object[] linha = new Object[20];

            linha[0] = pe.getPedido();
            linha[1] = pe.getCliente();
            linha[2] = pe.getCadCliente().getNome();
            linha[3] = this.utilDatas.converterDateToStr(pe.getEmissao());
            linha[4] = pe.getEmpresa();
            linha[5] = pe.getFilial();
            linha[6] = pe.getCadCliente().getCpfcnpj();
            linha[8] = pe.getQuantidade();
            linha[9] = pe.getCadCliente().getCidade();
            linha[10] = pe.getCadCliente().getEstado();
            //  linha[11] = oc.getObservacaomovimento();
            linha[11] = pe.getCadProduto().getCodigoproduto();
            linha[12] = pe.getCadProduto().getDescricaoproduto();

            modeloCarga.addRow(linha);
        }

    }

    private void limparTela() {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableInfo.getModel();
        modeloCarga.setNumRows(0);
        txtPesquisar.setText("");
        txtPesquisar.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grp = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableInfo = new javax.swing.JTable();
        txtPesquisar = new org.openswing.swing.client.TextControl();
        txtOrdemCompra = new org.openswing.swing.client.TextControl();
        txtNomeFornecedor = new org.openswing.swing.client.TextControl();
        btnOc = new javax.swing.JRadioButton();
        opcCnpj = new javax.swing.JRadioButton();
        txtFilial = new org.openswing.swing.client.TextControl();
        txtEmpresa = new org.openswing.swing.client.TextControl();
        opcNome = new javax.swing.JRadioButton();
        opcCod = new javax.swing.JRadioButton();
        txtDataCorte = new org.openswing.swing.client.DateControl();
        jLabel2 = new javax.swing.JLabel();
        btnAut = new javax.swing.JButton();
        btnInd = new javax.swing.JButton();
        optFan1 = new javax.swing.JRadioButton();
        optPedidos = new javax.swing.JRadioButton();
        txtCodigo = new org.openswing.swing.client.TextControl();
        optPedidosMetais = new javax.swing.JRadioButton();
        optFan2 = new javax.swing.JRadioButton();
        btnPesquisar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnPesquisar1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximizable(false);
        setTitle("Ordem de Compras");
        setPreferredSize(new java.awt.Dimension(599, 188));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableInfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ordem compra", "Codigo", "Descrição", "Data ", "Empresa", "Filial", "Cnpj", "Peso", "Quantidade", "Cidade", "Estado", "Pro", "Des"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableInfoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableInfo);
        if (jTableInfo.getColumnModel().getColumnCount() > 0) {
            jTableInfo.getColumnModel().getColumn(0).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(1).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(3).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(4).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(4).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(4).setMaxWidth(0);
            jTableInfo.getColumnModel().getColumn(5).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(5).setMaxWidth(0);
            jTableInfo.getColumnModel().getColumn(6).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(7).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(7).setMaxWidth(0);
            jTableInfo.getColumnModel().getColumn(8).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(9).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(10).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(10).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(11).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(11).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(11).setMaxWidth(0);
            jTableInfo.getColumnModel().getColumn(12).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(12).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(12).setMaxWidth(0);
        }

        txtPesquisar.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        txtPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarActionPerformed(evt);
            }
        });
        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyTyped(evt);
            }
        });

        txtOrdemCompra.setEnabled(false);
        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtOrdemCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOrdemCompraActionPerformed(evt);
            }
        });

        txtNomeFornecedor.setEnabled(false);
        txtNomeFornecedor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        buttonGroup1.add(btnOc);
        btnOc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnOc.setSelected(true);
        btnOc.setText("Ordem");
        btnOc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOcActionPerformed(evt);
            }
        });

        buttonGroup1.add(opcCnpj);
        opcCnpj.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        opcCnpj.setText("Por Cnpj");
        opcCnpj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcCnpjActionPerformed(evt);
            }
        });

        txtFilial.setEnabled(false);
        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtFilial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialActionPerformed(evt);
            }
        });

        txtEmpresa.setEnabled(false);
        txtEmpresa.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmpresaActionPerformed(evt);
            }
        });

        buttonGroup1.add(opcNome);
        opcNome.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        opcNome.setText("Por Nome");
        opcNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcNomeActionPerformed(evt);
            }
        });

        buttonGroup1.add(opcCod);
        opcCod.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        opcCod.setText("Por fornecedor");
        opcCod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcCodActionPerformed(evt);
            }
        });

        txtDataCorte.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        txtDataCorte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataCorteActionPerformed(evt);
            }
        });

        jLabel2.setText("Data de Corte");

        btnAut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/carros.png"))); // NOI18N
        btnAut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutActionPerformed(evt);
            }
        });

        btnInd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        btnInd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIndActionPerformed(evt);
            }
        });

        buttonGroup1.add(optFan1);
        optFan1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        optFan1.setText("OC ECO");
        optFan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optFan1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(optPedidos);
        optPedidos.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        optPedidos.setText("PEDIDOS IND METAIS");
        optPedidos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optPedidosActionPerformed(evt);
            }
        });

        txtCodigo.setEnabled(false);
        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        buttonGroup1.add(optPedidosMetais);
        optPedidosMetais.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        optPedidosMetais.setText("PEDIDOS VENDA METAIS");
        optPedidosMetais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optPedidosMetaisActionPerformed(evt);
            }
        });

        buttonGroup1.add(optFan2);
        optFan2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        optFan2.setText("OC METAIS");
        optFan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optFan2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNomeFornecedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInd))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnOc)
                        .addGap(8, 8, 8)
                        .addComponent(opcCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(opcNome, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(opcCod, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(optFan1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(optFan2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(optPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(optPedidosMetais, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDataCorte, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(opcCnpj, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(opcNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(opcCod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(optFan1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(optPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(optPedidosMetais, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(optFan2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnInd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDataCorte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomeFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnOc, opcCnpj, opcCod, opcNome});

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAut, btnInd, txtPesquisar});

        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesquisar.setText("Confirmar");
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        jLabel1.setText("Ordens de Compra");

        btnPesquisar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruby_delete.png"))); // NOI18N
        btnPesquisar1.setText("Sair");
        btnPesquisar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisar1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPesquisar1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
private String codigoOrdemCompra;
    private String nomeFornecedor;
    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        if (veioCampo != null) {
            try {
                String msg = "Confirma ordem de compra";
                processo = "ENTRADA";
                if (optPedidos.isSelected()) {
                      this.complemento = "INDUSTRIALIZAÇÃO";
                    msg = "Confirma pedido de venda";
                    processo = "SAIDA";
                }
                if (optPedidosMetais.isSelected()) {
                    this.complemento = "VENDA_METAIS";
                    msg = "Confirma pedido de venda";
                    processo = "SAIDA";
                }
                if (ManipularRegistros.gravarRegistros(msg)) {
                    veioCampo.retornarOrdemCompra(codigoOrdemCompra, nomeFornecedor, txtEmpresa.getText(), txtFilial.getText(), this.processo, this.pedido, this.complemento);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {
                this.dispose();
            }

        }

    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void jTableInfoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableInfoMouseClicked

        int linhaSelSit = jTableInfo.getSelectedRow();
        int colunaSelSit = jTableInfo.getSelectedColumn();
        this.codigoOrdemCompra = jTableInfo.getValueAt(linhaSelSit, 0).toString();
        txtCodigo.setText(jTableInfo.getValueAt(linhaSelSit, 1).toString());
        this.nomeFornecedor = jTableInfo.getValueAt(linhaSelSit, 2).toString();

        txtEmpresa.setText(jTableInfo.getValueAt(linhaSelSit, 4).toString());
        txtFilial.setText(jTableInfo.getValueAt(linhaSelSit, 5).toString());
        txtOrdemCompra.setText(codigoOrdemCompra);
        txtNomeFornecedor.setText(nomeFornecedor);

        pedido = new Pedido();
        pedido.setEmpresa(1);
        pedido.setFilial(1);
        pedido.setCliente(Integer.valueOf(txtCodigo.getText()));
        pedido.setPedido(Integer.valueOf(txtOrdemCompra.getText()));
        pedido.setQuantidade(Double.valueOf(jTableInfo.getValueAt(linhaSelSit, 8).toString()));
        Cliente cli = new Cliente();
        cli.setCodigo(Integer.valueOf(txtCodigo.getText()));
        cli.setNome(txtNomeFornecedor.getText());
        pedido.setCadCliente(cli);
        pedido.setProduto(jTableInfo.getValueAt(linhaSelSit, 11).toString());
        Produto pro = new Produto();
        pro.setCodigoproduto(jTableInfo.getValueAt(linhaSelSit, 11).toString());
        pro.setDescricaoproduto(jTableInfo.getValueAt(linhaSelSit, 12).toString());
        pedido.setCadProduto(pro);

        if (evt.getClickCount() == 2) {
            if (!codigoOrdemCompra.isEmpty()) {
                btnPesquisar.setEnabled(true);
            } else {
                btnPesquisar.setEnabled(false);
            }

        }


    }//GEN-LAST:event_jTableInfoMouseClicked

    private void txtPesquisarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyPressed
//
    }//GEN-LAST:event_txtPesquisarKeyPressed

    private void txtPesquisarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyTyped
//
    }//GEN-LAST:event_txtPesquisarKeyTyped

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed

        try {
            if (btnOc.isSelected()) {
                getListar("ORDEMCOMPRA", " and ocp.numocp = '" + txtPesquisar.getText() + "'");
            }
            if (opcCnpj.isSelected()) {
                getListar("ORDEMCOMPRA", " and forn.cgccpf  like '%" + txtPesquisar.getText() + "%'");
            }
            if (opcNome.isSelected()) {
                getListar("ORDEMCOMPRA", " and (forn.apefor LIKE '%" + txtPesquisar.getText() + "%' OR forn.nomfor like  '%" + txtPesquisar.getText() + "%')");
            }
            if (opcCod.isSelected()) {
                getListar("ORDEMCOMPRA", " and ocp.codfor  = '" + txtPesquisar.getText() + "'");
            }

        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtPesquisarActionPerformed

    private void txtOrdemCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOrdemCompraActionPerformed
        try {
            getListar("PRODUTO", " and codpro = '" + txtOrdemCompra.getText() + "'");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtOrdemCompraActionPerformed

    private void btnPesquisar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisar1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnPesquisar1ActionPerformed

    private void txtFilialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilialActionPerformed

    private void txtEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmpresaActionPerformed

    private void opcCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcCodActionPerformed
        processo = "ENTRADA";
        limparTela();
    }//GEN-LAST:event_opcCodActionPerformed

    private void btnOcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOcActionPerformed
        processo = "ENTRADA";
        limparTela();
    }//GEN-LAST:event_btnOcActionPerformed

    private void opcCnpjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcCnpjActionPerformed
        processo = "ENTRADA";
        limparTela();
    }//GEN-LAST:event_opcCnpjActionPerformed

    private void opcNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcNomeActionPerformed
        processo = "ENTRADA";
        limparTela();
    }//GEN-LAST:event_opcNomeActionPerformed

    private void txtDataCorteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataCorteActionPerformed
        try {
            if (btnOc.isSelected()) {
                getListar("ORDEMCOMPRA", " and ocp.numocp = '" + txtPesquisar.getText() + "'");
            }
            if (opcCnpj.isSelected()) {
                getListar("ORDEMCOMPRA", " and forn.cgccpf  like '%" + txtPesquisar.getText() + "%'");
            }
            if (opcNome.isSelected()) {
                getListar("ORDEMCOMPRA", " and forn.nomfor  like '%" + txtPesquisar.getText() + "%'");
            }
            if (opcCod.isSelected()) {
                getListar("ORDEMCOMPRA", " and ocp.codfor  = '" + txtPesquisar.getText() + "'");
            }

        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtDataCorteActionPerformed

    private void btnAutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutActionPerformed
        try {
            complemento = "ECO";
            processo = "ENTRADA";
            getListar("ORDEMCOMPRA", " and ocp.tnspro = '90417'");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAutActionPerformed

    private void btnIndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIndActionPerformed
        try {
            complemento = "INDUSTRIALIZAÇÃO";
            processo = "ENTRADA";
            getListar("ORDEMCOMPRA", " and ocp.tnspro = '90419'");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnIndActionPerformed

    private void optFan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optFan1ActionPerformed
        try {
            complemento = "ECO";
            processo = "ENTRADA";
            getListarRegistroSucata("SUC", "");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optFan1ActionPerformed

    private void optPedidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optPedidosActionPerformed
        try {
            complemento = "INDUSTRIALIZAÇÃO";
            processo = "SAIDA";

            getListarPedidos("", " and ped.tnspro in ('90124') ");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optPedidosActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void optPedidosMetaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optPedidosMetaisActionPerformed
        try {
            complemento = "INDUSTRIALIZAÇÃO";
            processo = "SAIDA";
            getListarPedidos("", " and ped.tnspro in ('90127') ");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_optPedidosMetaisActionPerformed

    private void optFan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optFan2ActionPerformed
        try {
            complemento = "INDUSTRIALIZAÇÃO";
            processo = "ENTRADA";
            getListar("ORDEMCOMPRA", " and ocp.tnspro = '90419'");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompraGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optFan2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAut;
    private javax.swing.JButton btnInd;
    private javax.swing.JRadioButton btnOc;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnPesquisar1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup grp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableInfo;
    private javax.swing.JRadioButton opcCnpj;
    private javax.swing.JRadioButton opcCod;
    private javax.swing.JRadioButton opcNome;
    private javax.swing.JRadioButton optFan1;
    private javax.swing.JRadioButton optFan2;
    private javax.swing.JRadioButton optPedidos;
    private javax.swing.JRadioButton optPedidosMetais;
    private org.openswing.swing.client.TextControl txtCodigo;
    private org.openswing.swing.client.DateControl txtDataCorte;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.TextControl txtFilial;
    private org.openswing.swing.client.TextControl txtNomeFornecedor;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private org.openswing.swing.client.TextControl txtPesquisar;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the pessoa
     */
    /**
     * @return the addNewReg
     */
    public boolean isAddNewReg() {
        return addNewReg;
    }

    /**
     * @param addNewReg the addNewReg to set
     */
    public void setAddNewReg(boolean addNewReg) {
        this.addNewReg = addNewReg;
    }

    /**
     * @return the showMsgErros
     */
    public boolean isShowMsgErros() {
        return showMsgErros;
    }

    /**
     * @param showMsgErros the showMsgErros to set
     */
    public void setShowMsgErros(boolean showMsgErros) {
        this.showMsgErros = showMsgErros;
    }
}
