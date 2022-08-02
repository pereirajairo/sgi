/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.CargaItens;
import br.com.sgi.bean.CargaItensImpureza;
import br.com.sgi.bean.Impureza;
import br.com.sgi.dao.CargaItensImpurezaDAO;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public class IntegrarPesosImpureza extends InternalFrame {

    private boolean addNewReg;
    private boolean showMsgErros;

    private IntegrarPesosRegistrar veioCampo;

    private CargaItens cargaItens;
    private CargaItensImpureza cargaImpureza;

    public IntegrarPesosImpureza() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Impurezas do produto"));
            this.setSize(800, 500);
            carregarImpureza();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    /*-------------------------------------------------------------------------*/
    private void carregarImpureza() throws Exception {
        List<Impureza> lista = new ArrayList<Impureza>();
        Impureza imp = new Impureza();
        imp.setCodigo(1);
        imp.setDescricao("Agua");
        imp.setPercentualImpureza(0.0);
        imp.setTipo("Pesagem");
        lista.add(imp);
        imp = new Impureza();
        imp.setCodigo(2);
        imp.setDescricao("Ferro");
        imp.setPercentualImpureza(0.0);
        imp.setTipo("Pesagem");
        lista.add(imp);
        imp = new Impureza();
        imp.setCodigo(3);
        imp.setDescricao("Plástico");
        imp.setPercentualImpureza(0.0);
        imp.setTipo("Pesagem");
        lista.add(imp);
        imp = new Impureza();
        imp.setCodigo(4);
        imp.setDescricao("Outros");
        imp.setPercentualImpureza(0.0);
        imp.setTipo("Refugo Interno");
        lista.add(imp);

        carregarTabela(lista);
    }

    /*-------------------------------------------------------------------------*/
    private void gravar() throws SQLException, Exception {
        CargaItensImpurezaDAO dao = new CargaItensImpurezaDAO();

        cargaImpureza.setEmpresa(cargaItens.getEmpresa());
        cargaImpureza.setFilial(cargaImpureza.getFilial());
        cargaImpureza.setFornecedor(cargaItens.getFornecedor());
        cargaImpureza.setNumeroCarga(cargaItens.getNumerocarga());
        cargaImpureza.setSequenciacarga(cargaItens.getSequenciacarga());
        cargaImpureza.setSequenciaCadastro(dao.proxCodCad());

        cargaImpureza.setProduto(cargaItens.getProduto());

        cargaImpureza.setDataCadastro(new Date());
        cargaImpureza.setUsusarioCadastro(1);
        cargaImpureza.setHorarioCadastro(1);

        cargaImpureza.setPesoImpureza(txtPesoDescontar.getDouble());
        cargaImpureza.setPesoTotalCarga(txtPesoCarga.getDouble() - txtPesoTotalCargaLancado.getDouble());
        cargaImpureza.setQuantidade(txtQuantidadeEmalagemImpureza.getDouble()); // quantidade pallete
        cargaImpureza.setPercentualDesconto(txtPercentual.getDouble()); // percentual desconto

        cargaImpureza.setCodigoImpureza(Integer.valueOf(txtCodigoImpureza.getText().trim()));
        cargaImpureza.setQuantidadePeso(txtPesoTotalImpureza.getDouble());

        dao.remover(cargaImpureza);

        if (!dao.inserir(cargaImpureza)) {

        } else {
            getImpurezasRegistradas();
            atualizarLista();

        }

    }

    private void atualizarLista() {
        if (veioCampo != null) {
            try {
                veioCampo.retonarDescontos(txtPesoTotalCargaLancado.getDouble());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {

            }
        }
    }

    private void sair() {
        if (veioCampo != null) {
            try {
                //     veioCampo.retonarDescontos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {

                this.dispose();
            }

        }
    }

    public void setRecebePalavra(IntegrarPesosRegistrar veioInput, CargaItens cargaItens, Double pesoLiquidoCarga) throws Exception {
        this.veioCampo = veioInput;
        this.cargaItens = cargaItens;
        if (cargaItens != null) {
            if (cargaItens.getSequenciacarga() > 0) {
                txtProduto.setText(cargaItens.getProduto());
                txtDescricao.setText(cargaItens.getDescricao());
                carregarImpureza();

                txtPesoCarga.setValue(pesoLiquidoCarga);
                txtQuantidadeEmbalagem.setValue(cargaItens.getPesoItem());

                txtTotalPesoEmbalagem.setValue(pesoLiquidoCarga / cargaItens.getPesoItem());

                txtPercentual.setValue(0);

                cargaImpureza = new CargaItensImpureza();
                getImpurezasRegistradas();
            }
        }

    }

    private void calcularDesconto() {
      
        double pesoEmb = txtTotalPesoEmbalagem.getDouble();
        double qtdyEmb = txtQuantidadeEmalagemImpureza.getDouble();
        double descEmb = pesoEmb * qtdyEmb;
        txtPesoTotalImpureza.setValue(descEmb);

        double perDes = (txtPercentual.getDouble());

        double totDes = descEmb * (perDes / 100);
        txtPesoDescontar.setValue(totDes);
        txtPercentual.setEnabled(false);
        if (txtCodigoImpureza.getText().equals("1")) {
            txtPercentual.setEnabled(true);
            txtPercentual.requestFocus();
        }else{
            txtPesoDescontar.setValue(txtPesoTotalImpureza.getValue());
            txtPercentual.setValue(100);
        }

    }
    private List<CargaItensImpureza> lista = new ArrayList<CargaItensImpureza>();

    private void getImpurezasRegistradas() throws SQLException, Exception {
        CargaItensImpurezaDAO dao = new CargaItensImpurezaDAO();
        lista = dao.getCargaItensImpurezas("CARGA", " and usu_nrocar = " + cargaItens.getNumerocarga() + ""
                + " and usu_seqcar = " + cargaItens.getSequenciacarga());
        carregarTabelaLancamento();
        totalizarPesosDesconto();
    }

    private void totalizarPesosDesconto() {
        double peso = 0.0;
        txtPesoTotalCargaLancado.setValue(peso);
        for (CargaItensImpureza prg : lista) {
            peso += prg.getPesoImpureza();
        }
        txtPesoTotalCargaLancado.setValue(peso);
    }

    public void carregarTabelaLancamento() throws Exception {
        addNewReg = true;
        ImageIcon sitok = getImage("/images/sitBom.png");
        redColunastab();
        int linhas = 0;
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableLancamento.getModel();
        modeloCarga.setNumRows(linhas);
        Object[] linha = new Object[15];
        for (CargaItensImpureza prg : lista) {
            TableColumnModel columnModel = jTableLancamento.getColumnModel();
            IntegrarPesosImpureza.JTableRenderer renderers = new IntegrarPesosImpureza.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = sitok;
            linha[1] = prg.getCodigoImpureza();
            if (prg.getCodigoImpureza() == 1) {
                linha[2] = "AGUA";
            } else {
                if (prg.getCodigoImpureza() == 2) {
                    linha[2] = "FERRO";
                } else if (prg.getCodigoImpureza() == 3) {
                    linha[2] = "PLASTICO";
                }
            }
            linha[3] = prg.getPesoImpureza();
            modeloCarga.addRow(linha);

        }

    }

    public void carregarTabela(List<Impureza> lista) throws Exception {
        addNewReg = true;
        ImageIcon sitok = getImage("/images/sitBom.png");
        redColunastab();
        int linhas = 0;
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableLista.getModel();
        modeloCarga.setNumRows(linhas);
        Object[] linha = new Object[15];
        for (Impureza prg : lista) {
            TableColumnModel columnModel = jTableLista.getColumnModel();
            IntegrarPesosImpureza.JTableRenderer renderers = new IntegrarPesosImpureza.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = sitok;
            linha[1] = prg.getCodigo();
            linha[2] = prg.getDescricao();
            linha[3] = prg.getTipo();
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

        jTableLista.setRowHeight(32);
        jTableLista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTableLancamento.setRowHeight(32);
        jTableLancamento.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grp = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btnGravar = new javax.swing.JButton();
        txtProduto = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        txtDescricao = new org.openswing.swing.client.TextControl();
        btnCalcular = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableLista = new javax.swing.JTable();
        txtCodigoImpureza = new org.openswing.swing.client.TextControl();
        txtDescricaoImpureza = new org.openswing.swing.client.TextControl();
        jLabel12 = new javax.swing.JLabel();
        txtPesoCarga = new org.openswing.swing.client.NumericControl();
        jLabel5 = new javax.swing.JLabel();
        txtQuantidadeEmbalagem = new org.openswing.swing.client.NumericControl();
        txtTotalPesoEmbalagem = new org.openswing.swing.client.NumericControl();
        txtPesoTotalImpureza = new org.openswing.swing.client.NumericControl();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtQuantidadeEmalagemImpureza = new org.openswing.swing.client.NumericControl();
        txtPercentual = new org.openswing.swing.client.NumericControl();
        txtPesoDescontar = new org.openswing.swing.client.NumericControl();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtPesoTotalCargaLancado = new org.openswing.swing.client.NumericControl();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableLancamento = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pequisar clientes");
        setPreferredSize(new java.awt.Dimension(599, 188));

        btnGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravar.setText("Gravar");
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        txtProduto.setEnabled(false);
        txtProduto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProdutoActionPerformed(evt);
            }
        });

        jLabel1.setText("Produto");

        txtDescricao.setEnabled(false);
        txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnCalcular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        btnCalcular.setText("Calcular");
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        btnSair.setText("Fechar");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        jTableLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "#", "Codigo", "Descricão", "Peso"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableListaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableLista);
        if (jTableLista.getColumnModel().getColumnCount() > 0) {
            jTableLista.getColumnModel().getColumn(0).setMinWidth(50);
            jTableLista.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableLista.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableLista.getColumnModel().getColumn(1).setMinWidth(50);
            jTableLista.getColumnModel().getColumn(1).setPreferredWidth(50);
            jTableLista.getColumnModel().getColumn(1).setMaxWidth(50);
            jTableLista.getColumnModel().getColumn(3).setMinWidth(200);
            jTableLista.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTableLista.getColumnModel().getColumn(3).setMaxWidth(200);
        }

        txtCodigoImpureza.setEnabled(false);
        txtCodigoImpureza.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtDescricaoImpureza.setEnabled(false);
        txtDescricaoImpureza.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel12.setText("Peso Carga");

        txtPesoCarga.setDecimals(2);
        txtPesoCarga.setEnabled(false);
        txtPesoCarga.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel5.setText("Qtdy Embalagem");

        txtQuantidadeEmbalagem.setDecimals(2);
        txtQuantidadeEmbalagem.setEnabled(false);
        txtQuantidadeEmbalagem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtTotalPesoEmbalagem.setDecimals(2);
        txtTotalPesoEmbalagem.setEnabled(false);
        txtTotalPesoEmbalagem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTotalPesoEmbalagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalPesoEmbalagemActionPerformed(evt);
            }
        });

        txtPesoTotalImpureza.setDecimals(2);
        txtPesoTotalImpureza.setEnabled(false);
        txtPesoTotalImpureza.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel6.setText("Peso por Embalagem");

        jLabel4.setText("Total peso com Impureza");

        jLabel7.setText("Qtdy Pallet Ruim");

        txtQuantidadeEmalagemImpureza.setDecimals(2);
        txtQuantidadeEmalagemImpureza.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtQuantidadeEmalagemImpureza.setRequired(true);
        txtQuantidadeEmalagemImpureza.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantidadeEmalagemImpurezaActionPerformed(evt);
            }
        });

        txtPercentual.setDecimals(2);
        txtPercentual.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPercentual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPercentualActionPerformed(evt);
            }
        });

        txtPesoDescontar.setDecimals(2);
        txtPesoDescontar.setEnabled(false);
        txtPesoDescontar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel8.setText("% Percentual");

        jLabel9.setText("Total Peso Impureza");

        jLabel10.setText("Peso com  Desconto");

        txtPesoTotalCargaLancado.setDecimals(2);
        txtPesoTotalCargaLancado.setEnabled(false);
        txtPesoTotalCargaLancado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPesoTotalCargaLancado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoTotalCargaLancadoActionPerformed(evt);
            }
        });

        jLabel2.setText("Codigo");

        jLabel3.setText("Descrição");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addComponent(txtQuantidadeEmalagemImpureza, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(2, 2, 2))
                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPesoCarga, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel7))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtQuantidadeEmbalagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPercentual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTotalPesoEmbalagem, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(jLabel6)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPesoDescontar, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPesoTotalImpureza, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtPesoTotalCargaLancado, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCodigoImpureza, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtDescricaoImpureza, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodigoImpureza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescricaoImpureza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesoCarga, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtQuantidadeEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalPesoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPesoTotalImpureza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtQuantidadeEmalagemImpureza, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPercentual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPesoDescontar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPesoTotalCargaLancado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Impurezas", jPanel2);

        jTableLancamento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "#", "Código", "Descrição", "Peso", "Id"
            }
        ));
        jScrollPane2.setViewportView(jTableLancamento);
        if (jTableLancamento.getColumnModel().getColumnCount() > 0) {
            jTableLancamento.getColumnModel().getColumn(0).setMinWidth(50);
            jTableLancamento.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableLancamento.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableLancamento.getColumnModel().getColumn(1).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(3).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(4).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(4).setMaxWidth(100);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Lançamento", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSair)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCalcular))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(2, 2, 2))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCalcular, btnSair});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSair)
                    .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCalcular, btnGravar, btnSair});

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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        try {
            gravar();
        } catch (SQLException ex) {
            Logger.getLogger(IntegrarPesosImpureza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesosImpureza.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGravarActionPerformed

    private void txtProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProdutoActionPerformed

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        calcularDesconto();
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void jTableListaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableListaMouseClicked
        int linhaSelSit = jTableLista.getSelectedRow();
        int colunaSelSit = jTableLista.getSelectedColumn();
        txtCodigoImpureza.setText(jTableLista.getValueAt(linhaSelSit, 1).toString());
        txtDescricaoImpureza.setText(jTableLista.getValueAt(linhaSelSit, 2).toString());

    }//GEN-LAST:event_jTableListaMouseClicked

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        sair();
    }//GEN-LAST:event_btnSairActionPerformed

    private void txtTotalPesoEmbalagemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalPesoEmbalagemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalPesoEmbalagemActionPerformed

    private void txtQuantidadeEmalagemImpurezaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantidadeEmalagemImpurezaActionPerformed
        calcularDesconto();
    }//GEN-LAST:event_txtQuantidadeEmalagemImpurezaActionPerformed

    private void txtPesoTotalCargaLancadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoTotalCargaLancadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesoTotalCargaLancadoActionPerformed

    private void txtPercentualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPercentualActionPerformed
        calcularDesconto();
    }//GEN-LAST:event_txtPercentualActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnSair;
    private javax.swing.ButtonGroup grp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableLancamento;
    private javax.swing.JTable jTableLista;
    private org.openswing.swing.client.TextControl txtCodigoImpureza;
    private org.openswing.swing.client.TextControl txtDescricao;
    private org.openswing.swing.client.TextControl txtDescricaoImpureza;
    private org.openswing.swing.client.NumericControl txtPercentual;
    private org.openswing.swing.client.NumericControl txtPesoCarga;
    private org.openswing.swing.client.NumericControl txtPesoDescontar;
    private org.openswing.swing.client.NumericControl txtPesoTotalCargaLancado;
    private org.openswing.swing.client.NumericControl txtPesoTotalImpureza;
    private org.openswing.swing.client.TextControl txtProduto;
    private org.openswing.swing.client.NumericControl txtQuantidadeEmalagemImpureza;
    private org.openswing.swing.client.NumericControl txtQuantidadeEmbalagem;
    private org.openswing.swing.client.NumericControl txtTotalPesoEmbalagem;
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
