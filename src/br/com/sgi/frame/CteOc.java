/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cte;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Fornecedor;
import br.com.sgi.bean.OrdemCompra;
import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.dao.CteDAO;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.FornecedorDAO;
import br.com.sgi.dao.OrdemCompraDAO;
import br.com.sgi.dao.SucataEcoDao;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSFecharOrdem;
import br.com.sgi.ws.WsOrdemDeCompra;
import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.sql.SQLException;
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
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class CteOc extends InternalFrame {

    private Cte cte;
    private CteDAO cteDAO;
    private List<Cte> lstCtes = new ArrayList<Cte>();
    private List<Cte> lstCtesAgrupado = new ArrayList<Cte>();
    private UtilDatas utilDatas;
    private CteXml veioCampo;

    public CteOc() {
        try {
            initComponents();

            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }

            if (cteDAO == null) {
                this.cteDAO = new CteDAO();
            }
            preencherComboFilial(0);
            txtDataOrdem.setDate(new Date());
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
        txtFilialDestino.removeAllItems();

        if (id == 0) {
            listFilial = filialDAO.getFilias("", " and e070fil.codemp = 1");
        }

        if (listFilial != null) {
            for (Filial filial : listFilial) {
                cod = filial.getFilial().toString();
                des = filial.getRazao_social();
                desger = cod + " - " + des;
                txtFilialDestino.addItem(desger);
            }
        }
    }

    private void getCtesAgrupado(String pesquisar_por, String pesquisar) throws SQLException, Exception {
        btnGerarOc.setEnabled(false);

        lstCtesAgrupado = this.cteDAO.getCtesAgrupado("", " and cte.usu_gerocp = 'S'");
        if (lstCtesAgrupado != null) {
            carregarTabelaAgrupado(0);
            if (lstCtesAgrupado.size() > 0) {
            }
        }

    }

    public void carregarTabelaAgrupado(Integer ordemcompra) throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableOc.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        jTableOc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon OkIcon = getImage("/images/sitBom.png");
        ImageIcon ErrorIcon = getImage("/images/sitRuim.png");

        double valorFrete = 0;
        double pesoFrete = 0;
        for (Cte cte : lstCtesAgrupado) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableOc.getColumnModel();
            CteOc.JTableRenderer renderers = new CteOc.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            if (cte.getUsu_codfor() == 0) {
                linha[0] = ErrorIcon;
            } else {
                linha[0] = OkIcon;
            }

            linha[1] = cte.getUsu_codtra();
            linha[2] = cte.getCadTransportadora().getNomeTransportadora();
            linha[3] = cte.getTotalcte();
            linha[4] = cte.getUsu_valcte();
            linha[5] = cte.getUsu_pesnfv();
            linha[6] = cte.getUsu_valnfv();
            linha[7] = cte.getUsu_codfor();
            linha[8] = cte.getUsu_ctafin();
            linha[9] = cte.getUsu_codlan();
            valorFrete += cte.getUsu_valcte();
            pesoFrete += cte.getUsu_pesnfv();

            modeloCarga.addRow(linha);
        }
        valorFrete = FormatarNumeros.converterDoubleDoisDecimais(valorFrete);
        pesoFrete = FormatarNumeros.converterDoubleDoisDecimais(pesoFrete);

        lblValorFrete.setText(FormatarNumeros.converterDoubleString(valorFrete));
        lblPesoFrete.setText(FormatarNumeros.converterDoubleString(pesoFrete));
    }

    private void getCtes(String pesquisar_por, String pesquisar) throws SQLException, Exception {
        btnGerarOc.setEnabled(false);
        lstCtes = this.cteDAO.getCtes("", " and usu_codfor = " + txtCodigo.getText() + " and usu_gerocp = 'S'");
        if (lstCtes != null) {
            carregarTabela(0);
            if (lstCtes.size() > 0) {
                jTabPanel.setSelectedIndex(1);
                btnGerarOc.setEnabled(true);
            }
        }

    }

    public void carregarTabela(Integer ordemcompra) throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCad.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        jTableCad.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon OkIcon = getImage("/images/sitBom.png");
        ImageIcon ErrorIcon = getImage("/images/sitRuim.png");

        double valorFrete = 0;
        double pesoFrete = 0;
        for (Cte cte : lstCtes) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableCad.getColumnModel();
            CteOc.JTableRenderer renderers = new CteOc.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            if (cte.getUsu_numocp() > 0) {
                ordemcompra = cte.getUsu_numocp();
            }

            if (ordemcompra == 0) {
                linha[0] = ErrorIcon;
            } else {
                linha[0] = OkIcon;
            }

            linha[1] = cte.getUsu_numcte();
            linha[2] = cte.getUsu_valcte();
            linha[3] = cte.getUsu_pesnfv();
            linha[4] = cte.getUsu_numnfv();
            linha[5] = cte.getUsu_cplocp();
            linha[6] = cte.getUsu_valnfv();
            linha[7] = cte.getUsu_tipfre();
            linha[8] = cte.getUsu_codser();
            linha[9] = cte.getUsu_chacte();
            linha[10] = ordemcompra;
            linha[11] = cte.getUsu_codlan();

            valorFrete += cte.getUsu_valcte();
            pesoFrete += cte.getUsu_pesnfv();

            modeloCarga.addRow(linha);
        }
        valorFrete = FormatarNumeros.converterDoubleDoisDecimais(valorFrete);
        pesoFrete = FormatarNumeros.converterDoubleDoisDecimais(pesoFrete);

        lblValorFrete.setText(FormatarNumeros.converterDoubleString(valorFrete));
        lblPesoFrete.setText(FormatarNumeros.converterDoubleString(pesoFrete));
    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCad.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCad.setAutoCreateRowSorter(true);
        jTableCad.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCad.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableCad.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableCad.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableCad.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCad.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableCad.setRowHeight(40);

        jTableOc.setRowHeight(40);
        jTableOc.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableOc.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableOc.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableOc.getColumnModel().getColumn(7).setCellRenderer(direita);

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void setRecebePalavra(CteXml veioInput, String fornecedor) throws Exception {
        this.veioCampo = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Gerar ordens de compra"));
        if (veioInput != null) {

            getCtesAgrupado("", "");

        }
    }
    private Integer orderCompra = 0;

    private void gerarOrdem() throws SQLException, Exception {
        String empresa = "1";
        String re = "";
        String sNumOcp = "";
        String resultado = "";
        String erro = "";
        SucataEcoParametros sucataEcoParametros = new SucataEcoParametros();
        SucataEcoDao sucataEcoDao = new SucataEcoDao();
        sucataEcoParametros = sucataEcoDao.getSucataEcoParamentros("1", empresa);
        sucataEcoParametros.setTransacao("90408");
        sucataEcoParametros.setCentroCustos("65");
        sucataEcoParametros.setCondicao(txtCodPgt.getSelectedItem().toString());
        sucataEcoParametros.setFornecedor(txtCodigo.getText());

        sucataEcoParametros.setObservacao("FRETE SOBRE VENDA ");

        sucataEcoParametros.setContaFinanceira(1024);
        sucataEcoParametros.setValorSucata(0.0);

        if (this.utilDatas == null) {
            this.utilDatas = new UtilDatas();
        }
        String data = this.utilDatas.converterDateToStr(txtDataOrdem.getDate());

        re = WsOrdemDeCompra.ordemDeCompraTransporteSapiens(sucataEcoParametros, this.lstCtes, txtDataOrdem.getDate(), txtEmpresaDestino.getSelectedItem().toString(), txtFilialSugestao.getText());
        int tam = re.length();
        if (tam > 0) {
            int intretorno = re.indexOf("<mensagemRetorno>");
            int intFinalRetorno = re.indexOf("</mensagemRetorno>");
            resultado = re.substring(intretorno + 17, intFinalRetorno);
            int retornoNumOcp = re.indexOf("<numOcp>");
            int retornoNumOcpFim = re.indexOf("</numOcp>");
            int retornoErro = re.indexOf("<retorno>");
            int retornoErroFim = re.indexOf("</retorno>");
            sNumOcp = re.substring(retornoNumOcp + 8, retornoNumOcpFim);
            erro = re.substring(retornoErro + 9, retornoErroFim);
            OrdemCompra oc = new OrdemCompra();
            if (resultado.equals("Processado com sucesso.")) {
                txtObservacao.setText("ORDEM DE COMPRA GERADA COM SUCESSO: " + sNumOcp + "\n" + re);
                oc.setEmpresa(1);
                oc.setFilial(1);
                oc.setCodigoFornecedor(Integer.valueOf(txtCodigo.getText()));
                oc.setNumeroOrdemCompra(Integer.valueOf(sNumOcp));
                txtOrdemCompra.setText(sNumOcp);
                this.orderCompra = Integer.valueOf(sNumOcp);
               // if (orderCompra > 0) {
                    System.out.println(" gerando oc ");
                    lblSituacao.setText("FECHANDO ORDEM DE COMPRA");
                    WSFecharOrdem wsFecharOrdem = new WSFecharOrdem();
                    wsFecharOrdem.executar("procauto", "3n3rg14");
                    lblSituacao.setText("OC FECHADA");
               // }

                try {
                    if (!atualizarOc(oc, "F")) {
                    }
                    btnGerarOc.setEnabled(false);
                } catch (Exception e) {
                    Mensagem.mensagem("ERROR", e.getMessage());
                }

            } else if (resultado.equals("Ocorreram erros.")) {
                txtObservacao.setText(re);
                if (!atualizarOc(oc, "E")) {
                }
                btnGerarOc.setEnabled(false);

            }

        } else {
            txtObservacao.setText(re);
        }

    }

    public boolean removerCte() throws SQLException {
        boolean retorno = false;
        if (this.cte != null) {
            if (this.cte.getUsu_codlan() > 0) {
                CteDAO dao = new CteDAO();
                retorno = dao.remover(cte);
            }
        }

        return retorno;
    }

    public boolean atualizarOc(OrdemCompra oc, String situacao) throws SQLException, Exception {
        boolean retorno = false;
        if (oc != null) {
            if (oc.getNumeroOrdemCompra() != 0) {
                OrdemCompraDAO dao = new OrdemCompraDAO();
                if (!dao.alterar(oc)) {

                } else {
                    CteDAO cteDao = new CteDAO();
                    int qtdreg = lstCtes.size();
                    int contador = 0;
                    for (Cte cte : lstCtes) {
                        if (cte.getUsu_codlan() > 0) {
                            cte.setUsu_numocp(this.orderCompra);
                            cte.setUsu_sitocp(situacao);
                            contador++;
                            if (!cteDao.atualizarOC(cte, qtdreg, contador)) {

                            } else {
                            }
                        }
                    }

                }

            }
        }
        return retorno;
    }

    private void validarOrdens(String retorno) {
        if (veioCampo != null) {
            try {
                veioCampo.retornarOrdem(String.valueOf(orderCompra));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {
                this.dispose();
            }
        }
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

        jLabel12 = new javax.swing.JLabel();
        txtCodigo = new org.openswing.swing.client.TextControl();
        txtNome = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        btnGerarOc = new javax.swing.JButton();
        btnSucCli2 = new javax.swing.JButton();
        lblValorFrete = new javax.swing.JLabel();
        lblPesoFrete = new javax.swing.JLabel();
        txtCodPgt = new javax.swing.JComboBox<>();
        jTabPanel = new javax.swing.JTabbedPane();
        jPoc = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableOc = new javax.swing.JTable();
        jIte = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCad = new javax.swing.JTable();
        jInfo = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        txtOrdemCompra = new javax.swing.JLabel();
        txtQtdCte = new javax.swing.JLabel();
        txtDataOrdem = new org.openswing.swing.client.DateControl();
        jLabel2 = new javax.swing.JLabel();
        txtEmpresaDestino = new javax.swing.JComboBox<>();
        txtFilialDestino = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtFilialSugestao = new org.openswing.swing.client.TextControl();
        lblSituacao = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Peso"));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Clientes");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jLabel12.setText("Fornecedor");

        txtCodigo.setEnabled(false);
        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        txtNome.setEnabled(false);
        txtNome.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        jLabel1.setText("Nome");

        btnGerarOc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        btnGerarOc.setBorder(javax.swing.BorderFactory.createTitledBorder("Confirmar"));
        btnGerarOc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarOcActionPerformed(evt);
            }
        });

        btnSucCli2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnSucCli2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSucCli2ActionPerformed(evt);
            }
        });

        lblValorFrete.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblValorFrete.setBorder(javax.swing.BorderFactory.createTitledBorder("Frete"));

        lblPesoFrete.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblPesoFrete.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso"));

        txtCodPgt.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCodPgt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "15" }));

        jTabPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabPanelMouseClicked(evt);
            }
        });

        jTableOc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "Código", "Transportadora", "Cte(s)", "Valor Frete", "Peso Frete", "Valor Nota", "Fornecedor", "Filial OC", "id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false, false, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableOc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableOcMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableOc);
        if (jTableOc.getColumnModel().getColumnCount() > 0) {
            jTableOc.getColumnModel().getColumn(0).setMinWidth(100);
            jTableOc.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableOc.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableOc.getColumnModel().getColumn(1).setMinWidth(100);
            jTableOc.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableOc.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableOc.getColumnModel().getColumn(3).setMinWidth(100);
            jTableOc.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableOc.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableOc.getColumnModel().getColumn(4).setMinWidth(100);
            jTableOc.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableOc.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableOc.getColumnModel().getColumn(5).setMinWidth(100);
            jTableOc.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableOc.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableOc.getColumnModel().getColumn(6).setMinWidth(100);
            jTableOc.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableOc.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableOc.getColumnModel().getColumn(7).setMinWidth(100);
            jTableOc.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableOc.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableOc.getColumnModel().getColumn(8).setMinWidth(100);
            jTableOc.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableOc.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableOc.getColumnModel().getColumn(9).setMinWidth(100);
            jTableOc.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableOc.getColumnModel().getColumn(9).setMaxWidth(100);
        }

        javax.swing.GroupLayout jPocLayout = new javax.swing.GroupLayout(jPoc);
        jPoc.setLayout(jPocLayout);
        jPocLayout.setHorizontalGroup(
            jPocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1113, Short.MAX_VALUE)
        );
        jPocLayout.setVerticalGroup(
            jPocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPocLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
        );

        jTabPanel.addTab("Oc", jPoc);

        jTableCad.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "Cte", "Valor Frete", "Peso Frete", "Nota", "Cliente", "Valor Nota", "Gerar", "Código", "Chave Nota", "Ordem Compra", "ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, false, false, true, true, true, false
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
            jTableCad.getColumnModel().getColumn(2).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(3).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(4).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(8).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(9).setMinWidth(0);
            jTableCad.getColumnModel().getColumn(9).setPreferredWidth(0);
            jTableCad.getColumnModel().getColumn(9).setMaxWidth(0);
            jTableCad.getColumnModel().getColumn(10).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(10).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(11).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(11).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(11).setMaxWidth(100);
        }

        javax.swing.GroupLayout jIteLayout = new javax.swing.GroupLayout(jIte);
        jIte.setLayout(jIteLayout);
        jIteLayout.setHorizontalGroup(
            jIteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1113, Short.MAX_VALUE)
        );
        jIteLayout.setVerticalGroup(
            jIteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jIteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
        );

        jTabPanel.addTab("Itens", jIte);

        txtObservacao.setColumns(20);
        txtObservacao.setRows(5);
        jScrollPane2.setViewportView(txtObservacao);

        javax.swing.GroupLayout jInfoLayout = new javax.swing.GroupLayout(jInfo);
        jInfo.setLayout(jInfoLayout);
        jInfoLayout.setHorizontalGroup(
            jInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1113, Short.MAX_VALUE)
            .addGroup(jInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1113, Short.MAX_VALUE))
        );
        jInfoLayout.setVerticalGroup(
            jInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 334, Short.MAX_VALUE)
            .addGroup(jInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
        );

        jTabPanel.addTab("Info", jInfo);

        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtOrdemCompra.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordem de Compra"));

        txtQtdCte.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtQtdCte.setBorder(javax.swing.BorderFactory.createTitledBorder("Qtdy Cte"));

        txtDataOrdem.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel2.setText("Data Ordem");

        txtEmpresaDestino.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtEmpresaDestino.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2" }));

        txtFilialDestino.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtFilialDestino.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "15" }));
        txtFilialDestino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialDestinoActionPerformed(evt);
            }
        });

        jLabel3.setText("Condição");

        jLabel4.setText("Empresa");

        jLabel5.setText("Filial");

        txtFilialSugestao.setEnabled(false);
        txtFilialSugestao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtFilialSugestao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialSugestaoActionPerformed(evt);
            }
        });

        lblSituacao.setBorder(javax.swing.BorderFactory.createTitledBorder("Situação"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabPanel)
                        .addGap(4, 4, 4))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblValorFrete, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(lblPesoFrete, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtQtdCte, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSituacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnGerarOc, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(165, 165, 165)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCodPgt, 0, 77, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(33, 33, 33)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmpresaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtFilialSugestao, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFilialDestino, 0, 50, Short.MAX_VALUE)
                                .addGap(4, 4, 4))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtDataOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSucCli2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addGap(10, 10, 10))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSucCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCodPgt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtEmpresaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtFilialDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtFilialSugestao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabPanel))
                    .addComponent(txtDataOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblValorFrete, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(lblPesoFrete, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtQtdCte, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnGerarOc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSituacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGerarOc, lblPesoFrete, lblValorFrete, txtOrdemCompra, txtQtdCte});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        if (!txtCodigo.getText().isEmpty()) {
            try {
                getCtes("", "");
            } catch (Exception ex) {
                Logger.getLogger(CteOc.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        if (!txtNome.getText().isEmpty()) {
            //
        }
    }//GEN-LAST:event_txtNomeActionPerformed

    private void jTableCadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCadMouseClicked
        int linhaSelSit = jTableCad.getSelectedRow();
        int colunaSelSit = jTableCad.getSelectedColumn();

    }//GEN-LAST:event_jTableCadMouseClicked

    private void btnGerarOcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarOcActionPerformed
        if (ManipularRegistros.gravarRegistros(" Gerar ordem de compra ")) {
            try {
                gerarOrdem();
                carregarTabela(orderCompra);
                getCtesAgrupado("", "");
            } catch (SQLException ex) {
                Logger.getLogger(CRMContaManutencao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CteOc.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnGerarOcActionPerformed

    private void btnSucCli2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSucCli2ActionPerformed
        validarOrdens("");
    }//GEN-LAST:event_btnSucCli2ActionPerformed

    private void jTableOcMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableOcMouseClicked
        int linhaSelSit = jTableOc.getSelectedRow();
        int colunaSelSit = jTableOc.getSelectedColumn();
        txtQtdCte.setText(jTableOc.getValueAt(linhaSelSit, 3).toString());
        txtCodigo.setText(jTableOc.getValueAt(linhaSelSit, 7).toString());

        if (jTableOc.getValueAt(linhaSelSit, 8).toString().equals("0")) {
            txtFilialSugestao.setText("1");
        } else {
            txtFilialSugestao.setText(jTableOc.getValueAt(linhaSelSit, 8).toString());

        }

        jTabPanel.setSelectedIndex(0);
        if (evt.getClickCount() == 2) {
            txtCodigo.setEnabled(true);
            if (!txtCodigo.getText().isEmpty()) {

                try {
                    Fornecedor fo = new Fornecedor();
                    FornecedorDAO dao = new FornecedorDAO();
                    fo = dao.getFornecedor("REP", " and codfor = " + txtCodigo.getText());
                    if (fo != null) {
                        if (fo.getCodfor() > 0) {
                            txtCodigo.setEnabled(false);
                            txtNome.setText(fo.getNomfor());
                            getCtes("por_codigo", txtCodigo.getText());
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CteOc.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(CteOc.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

    }//GEN-LAST:event_jTableOcMouseClicked

    private void jTabPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabPanelMouseClicked
        int abaselecionada = jTabPanel.getSelectedIndex();
        if (abaselecionada == 0) {
            jTabPanel.setSelectedIndex(0);
            try {

                getCtesAgrupado("", "");
                DefaultTableModel modeloCarga = (DefaultTableModel) jTableCad.getModel();
                modeloCarga.setNumRows(0);
            } catch (Exception ex) {
                Logger.getLogger(CteOc.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_jTabPanelMouseClicked

    private void txtFilialSugestaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialSugestaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilialSugestaoActionPerformed

    private void txtFilialDestinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialDestinoActionPerformed
        if (txtFilialDestino.getSelectedIndex() != -1) {
            if (!txtFilialDestino.getSelectedItem().toString().equals("TODOS")) {
                String cod = txtFilialDestino.getSelectedItem().toString();
                int index = cod.indexOf("-");
                String codcon = cod.substring(0, index);
                txtFilialSugestao.setText(codcon);
            }
        }
    }//GEN-LAST:event_txtFilialDestinoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGerarOc;
    private javax.swing.JButton btnSucCli2;
    private javax.swing.JPanel jInfo;
    private javax.swing.JPanel jIte;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPoc;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabPanel;
    private javax.swing.JTable jTableCad;
    private javax.swing.JTable jTableOc;
    private javax.swing.JLabel lblPesoFrete;
    private javax.swing.JLabel lblSituacao;
    private javax.swing.JLabel lblValorFrete;
    private javax.swing.JComboBox<String> txtCodPgt;
    private org.openswing.swing.client.TextControl txtCodigo;
    private org.openswing.swing.client.DateControl txtDataOrdem;
    private javax.swing.JComboBox<String> txtEmpresaDestino;
    private javax.swing.JComboBox<String> txtFilialDestino;
    private org.openswing.swing.client.TextControl txtFilialSugestao;
    private org.openswing.swing.client.TextControl txtNome;
    private javax.swing.JTextArea txtObservacao;
    private javax.swing.JLabel txtOrdemCompra;
    private javax.swing.JLabel txtQtdCte;
    // End of variables declaration//GEN-END:variables
}
