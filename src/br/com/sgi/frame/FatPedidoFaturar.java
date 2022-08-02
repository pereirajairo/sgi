/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.BaseEstado;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.Pedido;
import br.com.sgi.dao.MinutaDAO;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.frame.faturamento.pnPedidoPadrao;
import br.com.sgi.util.FormatarPeso;
import static br.com.sgi.util.FormatarPeso.PORCENTAGEM;
import static br.com.sgi.util.FormatarPeso.PORCENTAGEM_QTDY;
import br.com.sgi.util.UtilDatas;
import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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

/**
 *
 * @author jairosilva
 */
public final class FatPedidoFaturar extends InternalFrame {
    
    private Minuta minuta;
    private List<Minuta> listMinuta = new ArrayList<Minuta>();
    private List<Minuta> listMinutaDetalhada = new ArrayList<Minuta>();
    private List<Pedido> listPedidoExpedicao = new ArrayList<Pedido>();
    private MinutaDAO minutaDAO;
    
    private UtilDatas utilDatas;
    private String datIni;
    private String datFim;
    private pnPedidoPadrao[][] mesa;
    
    public FatPedidoFaturar() {
        try {
            initComponents();
            
            this.setSize(800, 500);
            
            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            
            if (minutaDAO == null) {
                this.minutaDAO = new MinutaDAO();
            }
            
            txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
            txtDatFim.setDate(new Date());
            pegarDataDigitada();
            pesquisarPorData("", "");
            executarAplicacao();
            LoadEstados();
            //  iniciarBarra("", "");
            new Thread() {
                public void run() {
                    
                    while (true) {
                        lblMensagem.setVisible(!lblMensagem.isVisible());
                        try {
                            sleep(1000);
                        } catch (Exception e) {
                        }
                    }
                }
            }.start();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }
    
    public void executarAplicacao() {
        iniciaCronometro();
        final long time = 600000; //  10 min 
        Timer timer = new Timer();
        TimerTask tarefa = new TimerTask() {
            public void run() {
                try {
                    iniciarBarra("", "");
                } catch (Exception ex) {
                    Logger.getLogger(FatPedidoFaturar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        timer.scheduleAtFixedRate(tarefa, time, time);
        
    }
    
    public void iniciaCronometro() {
        Timer timer = null;
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        if (timer == null) {
            timer = new Timer();
            TimerTask tarefa = new TimerTask() {
                public void run() {
                    try {
                        lblHora.setText(format.format(new Date().getTime()));
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            //600000
            lblHora1.setText(format.format(new Date().getTime() + 600000));
            timer.scheduleAtFixedRate(tarefa, 0, 1000);
            
        }
    }
    
      public void retornarMinuta() {
        pegarMinutasPedidos();
    }
    
    private void pegarMinutasPedidos() {
        try {
            final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            lblHora1.setText(format.format(new Date().getTime() + 600000));
            txtColuna.setEnabled(false);
            colunas = Integer.valueOf(txtColuna.getText().trim());
            getMinutasDetalhada("pedido", "\n and min.usu_orimin in ('EXP','MET','HUB','MG')"
                    + "\n  and min.usu_sitmin = 'FATURAR' ");
            
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoFaturar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void iniciarBarra(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Buscando Pedidos");
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                pegarMinutasPedidos();
                return null;
            }
            
            @Override
            protected void done() {
                barra.setIndeterminate(false);
                //    barra.setVisible(false);
                // barra.setString("Filtro carregado");
            }
        };
        worker.execute();
        
    }
    
    private void getPedidos(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        PedidoDAO dao = new PedidoDAO();
        listPedidoExpedicao = dao.getPedidosExpedicaoGeral(PESQUISA_POR, PESQUISA);
        if (listPedidoExpedicao != null) {
            carregarTabela();
        }
    }
    
    public void carregarTabela() throws Exception {
        
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon MedIcon = getImage("/images/cadeado.png");
        ImageIcon ReaIcon = getImage("/images/sitAnd.png");
        
        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon GarIcon = getImage("/images/ruby_delete.png");
        ImageIcon MktIcon = getImage("/images/bateriaindu.png");
        ImageIcon NotIcon = getImage("/images/Nota.png");
        ImageIcon RomIcon = getImage("/images/NotaRomaneio.png");
        ImageIcon NotGarIcon = getImage("/images/NotaGarantia.png");
        
        double peso = 0;
        double qtdy = 0;
        double qtdy_atr = 0;
        double qtdy_com = 0;
        
        double qtdped = 0;
        double qtdpreF = 0;
        double qtdpreP = 0;
        int contador = 0;
        
        String liberarMinuta = "N";
        
        for (Minuta m : listMinutaDetalhada) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new FatPedido.ColorirRenderer();
            FatPedidoFaturar.JTableRenderer renderers = new FatPedidoFaturar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            
            linha[0] = BomIcon;
            linha[1] = m.getMinutaPedido().getUsu_numped();
            linha[2] = m.getMinutaPedido().getUsu_codcli();
            linha[3] = m.getMinutaPedido().getCadCliente().getNome();
            
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
        
        jTableCarga.setRowHeight(40);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoCreateRowSorter(true);
        jTableCarga.setAutoResizeMode(0);
        
    }
    
    private void LoadEstados() {
        BaseEstado estado = new BaseEstado();
        Map<String, String> mapas = estado.getEstados();
        for (String uf : mapas.keySet()) {
            txtEstado.addItem(mapas.get(uf));
        }
    }
    
    private void pegarDataDigitada() throws ParseException {
        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
    }
    
    private void pesquisarPorData(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        pegarDataDigitada();
        PESQUISA_POR = "DATA";
        PESQUISA = " and usu_datemi >= '" + datIni + "' and usu_datemi <='" + datFim + "' and usu_orimin = 'EXP'";
        // getMinutas(PESQUISA_POR, PESQUISA);
    }
    
    public void retonar() throws ParseException, Exception {
        pegarDataDigitada();
        getMinutasDetalhada("pedido", " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "' and min.usu_orimin = 'EXP'");
        
    }
    
    private void pesquisarOpcao() {
        try {
            pegarDataDigitada();
            String sql = " and min.usu_sitmin not in ('CANCELADA')";
            if (txtPesquisar.getText().isEmpty()) {
                getMinutasDetalhada("pedido", sql + " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "'");
                
            } else {
                
                if (txtSelecionar.getSelectedItem().equals("Pedido")) {
                    getMinutasDetalhada("pedido", sql + " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "' and minI.usu_numped = " + txtPesquisar.getText());
                    
                }
                if (txtSelecionar.getSelectedItem().equals("Cliente")) {
                    getMinutasDetalhada("pedido", sql + " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "'  and minI.usu_codcli = " + txtPesquisar.getText());
                }
                
                if (txtSelecionar.getSelectedItem().equals("Minuta")) {
                    getMinutasDetalhada("pedido", sql + " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "' and minI.usu_codlan = " + txtPesquisar.getText());
                }
                
                if (txtSelecionar.getSelectedItem().equals("Pré Fatura")) {
                    getMinutasDetalhada("pedido", sql + " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "' and minI.usu_numnfa = " + txtPesquisar.getText());
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int colunas = 0;
    
    private void getColunas() throws SQLException {
        colunas = Integer.valueOf(txtColuna.getText());
        String coluna;
        StringBuilder mensagem = new StringBuilder();
        
        coluna = JOptionPane.showInputDialog("Informer a quantidade de coluna :");
        // mensagem.append("Colunas Informada ").append(coluna).append("!");
        // JOptionPane.showMessageDialog(null, mensagem);
        colunas = Integer.valueOf(coluna);
        
    }
    
    private void getMinutas(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listMinuta = this.minutaDAO.getMinutas(PESQUISA_POR, PESQUISA, "N");
        if (listMinuta != null) {
            organizaMesas(listMinuta.size());
        }
    }
    
    public void organizaMesas(int qtdminuta) throws SQLException {
        //Limpa o painel
        pnMesas.removeAll();
        pnMesas.repaint();
        
        double pesos = 0;
        double qtd = 0;

        // minuta
        int minuta = 0;
        Minuta minutas = new Minuta();

        //declara as variaveis utilizadas
        int altura, largura, qtdh, qtdv, aux = 0;

        //pega altura e largura do painel
        altura = pnMesas.getHeight();
        largura = pnMesas.getWidth();

        //quantidade de mesas por linha e coluna
//        qtdv = (int) Math.floor(altura / 110);
//        qtdh = (int) Math.floor(largura / 155);
        qtdh = this.colunas;
        qtdv = qtdminuta;

        //define numero de mesas para o array
        if (mesa == null) {
            mesa = new pnPedidoPadrao[qtdh][qtdv];
        }

        //laço para organizar as mesas dentro do painel
        for (int x = 0; x < qtdv; x++) {
            for (int y = 0; y < qtdh; y++) {
                if (aux <= listMinuta.size() - 1) {
                    if (mesa[y][x] == null) {
                        mesa[y][x] = new pnPedidoPadrao();
                    }
                    pnMesas.add(mesa[y][x]);
                    if (x == 0 && y == 0) {
                        mesa[y][x].setBounds(10, 10, 145, 100);
                    } else if (x == 0 && y > 0) {
                        mesa[y][x].setBounds(y * 155 + 10, 10, 145, 100);
                    } else {
                        mesa[y][x].setBounds(y * 155 + 10, x * 110 + 10, 145, 100);
                    }
                    
                    for (int m = 0; m < listMinuta.size(); m++) {
                        // System.out.println(aux);
                        minuta = listMinuta.get(aux).getUsu_codlan();
                        minutas.setUsu_codlan(listMinuta.get(aux).getUsu_codlan());
                        minutas.setUsu_qtdvol(listMinuta.get(aux).getUsu_qtdvol());
                        minutas.setUsu_sitmin(listMinuta.get(aux).getUsu_sitmin());
                        minutas.setUsu_libmot(listMinuta.get(aux).getUsu_libmot());
                        minutas.setUsu_pesfat(listMinuta.get(aux).getUsu_pesfat());
                        minutas.setUsu_qtdfat(listMinuta.get(aux).getUsu_qtdfat());
                        minutas.setUsu_codemb(listMinuta.get(aux).getUsu_codemb());
                        minutas.setTransacao(listMinuta.get(aux).getTransacao());
                        
                        pesos += minutas.getUsu_pesfat();
                        qtd += minutas.getUsu_qtdfat();
                        MinutaPedido mp = new MinutaPedido();
                        mp.setUsu_numped(listMinuta.get(aux).getMinutaPedido().getUsu_numped());
                        mp.setUsu_sitmin(listMinuta.get(aux).getMinutaPedido().getUsu_sitmin());
                        minutas.setMinutaPedido(mp);

                        // m++;
                        break;
                    }
                    mesa[y][x].modificaNum(minutas);
                }
                aux++;
            }
        }
        pnMesas.revalidate();
        
        lblPeso.setText(FormatarPeso.mascaraPorcentagem(pesos, PORCENTAGEM));
        lblQtdy.setText(FormatarPeso.mascaraPorcentagem(qtd, PORCENTAGEM_QTDY));
        lblPedidos.setText(FormatarPeso.mascaraPorcentagem(qtdminuta, PORCENTAGEM_QTDY));
        
        lblMensagem.setText("Pedidos à Faturar--: " + qtdminuta);
        
    }
    
    public void getMinutasDetalhada(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        
        if (listMinutaDetalhada != null) {
            if (listMinutaDetalhada.size() > 0) {
                listMinutaDetalhada.clear();
                listMinuta.clear();
                organizaMesas(listMinuta.size());
                carregarTabela();
            }
        }
        
        listMinutaDetalhada = this.minutaDAO.getMinutasDetalhada(PESQUISA_POR, PESQUISA, "S");
        if (listMinutaDetalhada != null) {
            if (listMinutaDetalhada.size() > 0) {
                this.listMinuta = listMinutaDetalhada;
                organizaMesas(listMinuta.size());
                carregarTabela();
                btnAuto.setEnabled(true);
                btnMoto.setEnabled(true);
                btnMetais.setEnabled(true);
                btnHub.setEnabled(true);
            }
            
        }
    }
    
    public static void getMinutaSelecionada(String minuta) throws Exception {
        lblMinuta.setText("Minuta :" + minuta + "");
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

        jInternalFrame1 = new javax.swing.JInternalFrame();
        btnAuto = new javax.swing.JButton();
        btnMoto = new javax.swing.JButton();
        btnMetais = new javax.swing.JButton();
        btnHub = new javax.swing.JButton();
        lblHora = new javax.swing.JLabel();
        lblHora1 = new javax.swing.JLabel();
        btDelivery4 = new javax.swing.JButton();
        jInternalFrame2 = new javax.swing.JInternalFrame();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnMesas = new javax.swing.JPanel();
        jInternalFrame3 = new javax.swing.JInternalFrame();
        lblPeso = new javax.swing.JLabel();
        lblQtdy = new javax.swing.JLabel();
        lblPedidos = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        textControl1 = new org.openswing.swing.client.TextControl();
        lblMinuta = new javax.swing.JLabel();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnFiltrar = new javax.swing.JButton();
        txtSelecionar = new javax.swing.JComboBox<>();
        txtPesquisar = new org.openswing.swing.client.TextControl();
        btnFiltrar1 = new javax.swing.JButton();
        txtEstado = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        lblMensagem = new javax.swing.JLabel();
        barra = new javax.swing.JProgressBar();
        txtColuna = new javax.swing.JTextField();
        btnFiltrarMinuta = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Clientes");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jInternalFrame1.setTitle("Canal de Faturamento");
        jInternalFrame1.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Novo.png"))); // NOI18N
        jInternalFrame1.setVisible(true);

        btnAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/auto.png"))); // NOI18N
        btnAuto.setText("AUTO");
        btnAuto.setEnabled(false);
        btnAuto.setPreferredSize(new java.awt.Dimension(120, 120));
        btnAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutoActionPerformed(evt);
            }
        });

        btnMoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png"))); // NOI18N
        btnMoto.setText("MOTO");
        btnMoto.setEnabled(false);
        btnMoto.setPreferredSize(new java.awt.Dimension(120, 120));
        btnMoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMotoActionPerformed(evt);
            }
        });

        btnMetais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        btnMetais.setText("METAIS");
        btnMetais.setEnabled(false);
        btnMetais.setPreferredSize(new java.awt.Dimension(120, 120));
        btnMetais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMetaisActionPerformed(evt);
            }
        });

        btnHub.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/truck_red.png"))); // NOI18N
        btnHub.setText("HUB");
        btnHub.setEnabled(false);
        btnHub.setPreferredSize(new java.awt.Dimension(120, 120));
        btnHub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHubActionPerformed(evt);
            }
        });

        lblHora.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblHora.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHora.setText("00:00:00");
        lblHora.setBorder(javax.swing.BorderFactory.createTitledBorder("Execução"));

        lblHora1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblHora1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHora1.setText("00:00:00");
        lblHora1.setBorder(javax.swing.BorderFactory.createTitledBorder("Atualizar Minutas"));

        btDelivery4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/megaphonefat.png"))); // NOI18N
        btDelivery4.setText("Faturadas");
        btDelivery4.setPreferredSize(new java.awt.Dimension(120, 120));
        btDelivery4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDelivery4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnAuto, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
            .addComponent(btnMoto, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(btnMetais, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(btnHub, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(lblHora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblHora1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btDelivery4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addComponent(btnAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMoto, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMetais, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHub, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btDelivery4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHora)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHora1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jInternalFrame1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblHora, lblHora1});

        jInternalFrame2.setTitle("Minutas");
        jInternalFrame2.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        jInternalFrame2.setVisible(true);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnMesas.setBackground(new java.awt.Color(170, 169, 149));
        pnMesas.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnMesas.setPreferredSize(new java.awt.Dimension(865, 3000));

        javax.swing.GroupLayout pnMesasLayout = new javax.swing.GroupLayout(pnMesas);
        pnMesas.setLayout(pnMesasLayout);
        pnMesasLayout.setHorizontalGroup(
            pnMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 863, Short.MAX_VALUE)
        );
        pnMesasLayout.setVerticalGroup(
            pnMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2998, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(pnMesas);

        javax.swing.GroupLayout jInternalFrame2Layout = new javax.swing.GroupLayout(jInternalFrame2.getContentPane());
        jInternalFrame2.getContentPane().setLayout(jInternalFrame2Layout);
        jInternalFrame2Layout.setHorizontalGroup(
            jInternalFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jInternalFrame2Layout.setVerticalGroup(
            jInternalFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jInternalFrame3.setTitle("Resumo");
        jInternalFrame3.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-caixa-cheia-16x16.png"))); // NOI18N
        jInternalFrame3.setVisible(true);

        lblPeso.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPeso.setForeground(new java.awt.Color(51, 102, 255));
        lblPeso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPeso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio.png"))); // NOI18N
        lblPeso.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso"));
        lblPeso.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblPeso.setOpaque(true);
        lblPeso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPesoMouseClicked(evt);
            }
        });

        lblQtdy.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdy.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio.png"))); // NOI18N
        lblQtdy.setBorder(javax.swing.BorderFactory.createTitledBorder("Quantidade Baterias"));
        lblQtdy.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdy.setOpaque(true);

        lblPedidos.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPedidos.setForeground(new java.awt.Color(0, 102, 0));
        lblPedidos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPedidos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio_cancelar.png"))); // NOI18N
        lblPedidos.setBorder(javax.swing.BorderFactory.createTitledBorder("Pedidos"));
        lblPedidos.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblPedidos.setOpaque(true);

        jTableCarga.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "#", "Pedido", "Cliente", "Nome"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(300);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(300);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(300);
        }

        textControl1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout jInternalFrame3Layout = new javax.swing.GroupLayout(jInternalFrame3.getContentPane());
        jInternalFrame3.getContentPane().setLayout(jInternalFrame3Layout);
        jInternalFrame3Layout.setHorizontalGroup(
            jInternalFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jInternalFrame3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(lblPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(textControl1, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
        );
        jInternalFrame3Layout.setVerticalGroup(
            jInternalFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrame3Layout.createSequentialGroup()
                .addComponent(lblPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQtdy, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblPedidos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textControl1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        jInternalFrame3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblPedidos, lblPeso, lblQtdy});

        lblMinuta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblMinuta.setForeground(new java.awt.Color(0, 51, 204));
        lblMinuta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        lblMinuta.setText("minuta ");

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        txtSelecionar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSelecionar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cliente", "Pedido", "Minuta", "Pré Fatura" }));

        txtPesquisar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarActionPerformed(evt);
            }
        });

        btnFiltrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar1ActionPerformed(evt);
            }
        });

        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstadoActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        lblMensagem.setBackground(new java.awt.Color(0, 0, 0));
        lblMensagem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblMensagem.setForeground(new java.awt.Color(51, 255, 51));
        lblMensagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMensagem.setText("PEDIDOS PARA FATURAR");
        lblMensagem.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMensagem, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMensagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        txtColuna.setText("5");
        txtColuna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtColunaActionPerformed(evt);
            }
        });

        btnFiltrarMinuta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-caixa-cheia-16x16.png"))); // NOI18N
        btnFiltrarMinuta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarMinutaActionPerformed(evt);
            }
        });

        jLabel1.setText("Data Inicial");

        jLabel2.setText("Data Final");

        jLabel3.setText("Opção");

        jLabel4.setText("Pesquisar");

        jLabel5.setText("Estado");

        jLabel6.setText("Atenção");

        jLabel7.setText("Coluna");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblMinuta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jInternalFrame2))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(txtColuna, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFiltrarMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFiltrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jInternalFrame3)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6)))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jInternalFrame3, jPanel1});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDatFim, txtDatIni});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtSelecionar)
                        .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFiltrar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtEstado))
                    .addComponent(txtColuna, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrarMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jInternalFrame2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jInternalFrame1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jInternalFrame3))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblMinuta, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                    .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar, btnFiltrar1, txtDatFim, txtDatIni, txtPesquisar, txtSelecionar});

        try {
            jInternalFrame1.setIcon(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        try {
            jInternalFrame2.setIcon(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        try {
            jInternalFrame3.setIcon(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMetaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMetaisActionPerformed
        try {
            final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            lblHora1.setText(format.format(new Date().getTime() + 600000));
            txtColuna.setEnabled(false);
            colunas = Integer.valueOf(txtColuna.getText().trim());
            getMinutasDetalhada("pedido", ""
                    + "\nand min.usu_orimin = 'MET'\n"
                    + "\n and min.usu_sitmin = 'FATURAR' ");
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoFaturar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnMetaisActionPerformed

    private void btnMotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMotoActionPerformed
        
        try {
            final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            lblHora1.setText(format.format(new Date().getTime() + 600000));
            txtColuna.setEnabled(false);
            colunas = Integer.valueOf(txtColuna.getText().trim());
            getMinutasDetalhada("pedido", ""
                    + "\nand min.usu_orimin IN ('EXP','HUB')\n"
                    + "\nand usu_libmot = 'M'"
                    + "\nand min.usu_sitmin = 'FATURAR' ");
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoFaturar.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnMotoActionPerformed

    private void btnAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutoActionPerformed
        try {
            final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            lblHora1.setText(format.format(new Date().getTime() + 600000));
            txtColuna.setEnabled(false);
            colunas = Integer.valueOf(txtColuna.getText().trim());
            getMinutasDetalhada("pedido", ""
                    + "\nand min.usu_orimin IN ('EXP','HUB')\n"
                    + "\nand usu_libmot = 'A'"
                    + "\nand min.usu_sitmin = 'FATURAR' ");
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoFaturar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAutoActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        try {
            pegarDataDigitada();
            getMinutasDetalhada("pedido", " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "' and min.usu_orimin = 'EXP'");
            
        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnFiltrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar1ActionPerformed
        
        try {
            pesquisarOpcao();
            
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoFaturar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar1ActionPerformed

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed
        pesquisarOpcao();
    }//GEN-LAST:event_txtPesquisarActionPerformed

    private void lblPesoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPesoMouseClicked

    }//GEN-LAST:event_lblPesoMouseClicked

    private void btnHubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHubActionPerformed
        try {
            final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            lblHora1.setText(format.format(new Date().getTime() + 600000));
            txtColuna.setEnabled(false);
            colunas = Integer.valueOf(txtColuna.getText().trim());
            getMinutasDetalhada("pedido", ""
                    + "\nand min.usu_orimin = 'HUB'\n"
                    + "\n and min.usu_sitmin = 'FATURAR' ");
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoFaturar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnHubActionPerformed

    private void txtEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstadoActionPerformed
        if (txtEstado.getSelectedIndex() != -1) {
            if (!txtEstado.getSelectedItem().equals("TODOS")) {
                try {
                    
                    getMinutasDetalhada("pedido", " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "' "
                            + "and min.usu_orimin = 'EXP' "
                            + "and cli.sigufs = '" + txtEstado.getSelectedItem().toString() + "'");
                    
                } catch (Exception ex) {
                    Logger.getLogger(IntegrarPesos.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                //  Mensagem.mensagemRegistros("ERRO", "Selecione Estado");
            }
        }
    }//GEN-LAST:event_txtEstadoActionPerformed

    private void btnFiltrarMinutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarMinutaActionPerformed
        pegarMinutasPedidos();

    }//GEN-LAST:event_btnFiltrarMinutaActionPerformed

    private void txtColunaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtColunaActionPerformed
        pegarMinutasPedidos();
    }//GEN-LAST:event_txtColunaActionPerformed

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();
        
        txtPesquisar.setText(jTableCarga.getValueAt(linhaSelSit, 2).toString());

    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btDelivery4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDelivery4ActionPerformed
        try {
            final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            lblHora1.setText(format.format(new Date().getTime() + 600000));
            
            txtColuna.setEnabled(false);
            colunas = Integer.valueOf(txtColuna.getText().trim());
            getMinutasDetalhada("pedido", " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "' and min.usu_orimin = 'EXP' and min.usu_numnfv >0 ");
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoFaturar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btDelivery4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btDelivery4;
    private javax.swing.JButton btnAuto;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar1;
    private javax.swing.JButton btnFiltrarMinuta;
    private javax.swing.JButton btnHub;
    private javax.swing.JButton btnMetais;
    private javax.swing.JButton btnMoto;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JInternalFrame jInternalFrame2;
    private javax.swing.JInternalFrame jInternalFrame3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JLabel lblHora;
    private javax.swing.JLabel lblHora1;
    private javax.swing.JLabel lblMensagem;
    public static javax.swing.JLabel lblMinuta;
    private javax.swing.JLabel lblPedidos;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblQtdy;
    private javax.swing.JPanel pnMesas;
    private org.openswing.swing.client.TextControl textControl1;
    private javax.swing.JTextField txtColuna;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private javax.swing.JComboBox<String> txtEstado;
    private org.openswing.swing.client.TextControl txtPesquisar;
    private javax.swing.JComboBox<String> txtSelecionar;
    // End of variables declaration//GEN-END:variables
}
