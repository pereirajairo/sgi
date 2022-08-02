/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Imagem;
import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.ClienteDAO;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.ImagemDAO;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSRelatorio;

import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;

/**
 *
 * @author jairosilva
 */
public final class SucataContaCorrente extends InternalFrame {
    
    private List<SucataMovimento> listContaCorrente = new ArrayList<SucataMovimento>();
    private List<SucataMovimento> listSucataMovimento = new ArrayList<SucataMovimento>();
    private SucataMovimentoDAO sucataMovimentoDAO;
    private SucataMovimento sucataMovimento;
    private Sucata sucata;
    private String datIni;
    private String datFim;
    private UtilDatas utilDatas;
    private Integer sUsuario;
    
    private Cliente cliente;
    private String codigoEmpresa;
    private String codigoFilial;
    private static Color COR_ESTOQUE_HFF = new Color(66, 111, 66);
    private String tipoControle = "";
    
    private List<Imagem> listImagem = new ArrayList<Imagem>();
    private ImagemDAO imagemDAO;
    private Imagem imagem;
    
    private Filial filial;
    private Filial filialFaturamento;
    
    public SucataContaCorrente() {
        try {
            initComponents();
            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            
            if (sucataMovimentoDAO == null) {
                sucataMovimentoDAO = new SucataMovimentoDAO();
            }
            if (imagemDAO == null) {
                imagemDAO = new ImagemDAO();
            }

            //   this.setSize(800, 500);
            txtDatIni.setDate(utilDatas.retornaDataIniSucata(new Date()));
            txtDatFim.setDate(utilDatas.retornaDataFim(new Date()));
            
            txtCliente.setText("0");
            
            getUsuarioLogado();
            
            preencherComboFilial(0);
            pegarDataDigitada();
            
            tipoLancamento = "NORMAL";
            txtPedido.setText("0");
            
            getListarMovimento("DATA", " and usu_autmot in ('AUT','MOT') and usu_datger >= '" + datIni + "' and usu_datger <='" + datFim + "'");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }
    
    private void getImagens(Integer pedido) throws SQLException, Exception {
        
        if (pedido == 0) {
            listImagem = imagemDAO.getImagens("pedido", " and hub.cliente = " + txtCliente.getText());
        } else {
            listImagem = imagemDAO.getImagens("pedido", " and hub.pedido = " + pedido);
        }
        
        if (listImagem != null) {
            carregarTabelaImagens();
        }
    }
    
    public void carregarTabelaImagens() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableImagens.getModel();
        modeloCarga.setNumRows(0);
        jTableImagens.setRowHeight(40);
        jTableImagens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon CreIcon = getImage("/images/user_suit.png");
        
        for (Imagem ima : listImagem) {
            Object[] linha = new Object[4];
            TableColumnModel columnModel = jTableImagens.getColumnModel();
            SucataContaCorrente.JTableRenderer renderers = new SucataContaCorrente.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            
            linha[0] = ima.getId();
            linha[1] = ima.getNomeImagem();
            modeloCarga.addRow(linha);
        }
        
    }
    
    public void exibirFoto(String id) throws SQLException, IOException {
        
        byte[] foto = null;
        
        imagem = new Imagem();
        
        BufferedImage img = null;
        imagem = imagemDAO.getImagem(Long.valueOf(id));
        
        try {
            img = ImageIO.read(new ByteArrayInputStream(imagem.getFileBlob()));
            //  lblImagem.setIcon(new ImageIcon(img));
            lblImagem.setIcon(new ImageIcon(new ImageIcon(img).getImage().getScaledInstance(lblImagem.getWidth(), lblImagem.getHeight(), Image.SCALE_DEFAULT)));
            // ImageIO.write(img, "PNG", new File("C:/Downloads/" + id + ".png"));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void getListarContaCorrente(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        
        if (tipoSucata.equals("AUTO") || tipoSucata.equals("AUT") || tipoSucata.equals("MOTO") || tipoSucata.equals("MOT")) {
            PESQUISA += " and usu_debcre not in ('0 - REMOVIDO','7 - CANCELADO')"
                    + "  and usu_autmot in ('AUT','MOT')";
            
        } else {
            PESQUISA += " and usu_debcre not in ('0 - REMOVIDO') and usu_autmot = '" + tipoSucata + "'";
        }
        
        if (tipoControle.equals("HUB")) {
            if (!txtCliente.getText().equals("84359") && !txtCliente.getText().equals("10469") && !txtCliente.getText().equals("17231")) {
                //PESQUISA += "  and usu_codcli =" + this.cliente.getCodigo();
            } else {
                PESQUISA += " and usu_tnspro in ('901HB','902HB')";
            }
        } else {
            if (cliente.getCodigo() > 0) {
                PESQUISA += "  and (usu_tnspro not in ('901HB')   or usu_tnspro is null or usu_tnspro='') and usu_codcli =" + this.cliente.getCodigo();
            }
            
        }
        
        listContaCorrente = this.sucataMovimentoDAO.getContaCorrentesAuto(PESQUISA_POR, PESQUISA);
        
        if (listContaCorrente != null) {
            btnAdd.setEnabled(true);
            carregarTabelaContaCorrente(false);
        }
    }
    
    public void carregarTabelaContaCorrente(boolean selecionar) throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableEnvio.getModel();
        modeloCarga.setNumRows(0);
        
        ImageIcon ProIcon = getImage("/images/money_add.png");
        ImageIcon gerIcon = getImage("/images/sitMedio.png");
        ImageIcon ManIcon = getImage("/images/overlays.png");
        ImageIcon Retorno = getImage("/images/veiculo_retorno.png");
        ImageIcon RetornoManual = getImage("/images/veiculo_retorno_manual.png");
        ImageIcon Envio = getImage("/images/veiculo_envio.png");
        ImageIcon Envio_manual = getImage("/images/veiculo_envio_manual.png");
        ImageIcon RetornoCancelado = getImage("/images/veiculo_envio_cancelar.png");
        ImageIcon Branco = getImage("/images/veiculo_envio_branco.png");
        
        double peso_sucata_cre = 0;
        double peso_sucata_cre_manual = 0;
        double peso_sucata_deb = 0;
        double peso_sucata_deb_manual = 0;
        double peso_sucata_sld = 0;
        
        double peso_produto_cre = 0;
        double peso_produto_cre_manual = 0;
        double peso_produto_deb = 0;
        double peso_produto_deb_manual = 0;
        double peso_produto_sld = 0;
        
        double peso_nota_complementar = 0;
        
        for (SucataMovimento suc : listContaCorrente) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableEnvio.getColumnModel();
            SucataContaCorrente.JTableRenderer renderers = new SucataContaCorrente.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            
            switch (suc.getDebitocredito()) {
                case "1 - GERADO":
                    linha[0] = gerIcon;
                    if (suc.getSituacao().equals("MANUAL")) {
                        linha[0] = ManIcon;
                    }
                    break;
                case "2 - PROVISIONADO":
                    linha[0] = ProIcon;
                    if (suc.getSituacao().equals("MANUAL")) {
                        linha[0] = ManIcon;
                    }
                    break;
                case "3 - DEBITO":
                    if (suc.getPesomovimento() < 0) {
                        suc.setPesomovimento(suc.getPesomovimento() * -1);
                    }
                    
                    linha[0] = Retorno;
                    if (suc.getSituacao().equals("MANUAL")) {
                        linha[0] = RetornoManual;
                        peso_produto_deb_manual += suc.getPesomovimento();
                        peso_sucata_deb_manual = suc.getPesomovimento() / (suc.getPercentualrendimento() / 100);
                        if (!suc.getAutomoto().equals("IND")) {
                            peso_sucata_deb_manual = suc.getPesomovimento();
                            System.out.println(" peso manual " + peso_sucata_deb_manual);
                        }
                        
                    }
                    
                    break;
                case "4 - CREDITO":
                    linha[0] = Envio;
                    if (suc.getSituacao().equals("MANUAL")) {
                        linha[0] = Envio_manual;
                        //   peso_sucata_cre_manual += suc.getPesoajustado();

                        peso_sucata_cre_manual += suc.getPesosucata();
                        if (suc.getPercentualrendimento() > 0) {
                            double peso = suc.getPesosucata() * (suc.getPercentualrendimento() / 100);
                            peso_produto_cre_manual = peso_produto_cre_manual + peso;
                        }
                        if (suc.getNotasaida() > 0) {
                            peso_nota_complementar += suc.getPesomovimento();
                        }
                    }
                    break;
                case "5 - REABILITADO":
                    linha[0] = gerIcon;
                    break;
                case "6 - ORDEM MANUAL":
                    linha[0] = gerIcon;
                    break;
                
                case "7 - CANCELADO":
                    linha[0] = RetornoCancelado;
                    break;
                default:
                    break;
            }
            
            linha[1] = suc.getSituacaoPeso();
            linha[2] = suc.getPedido();
            linha[3] = suc.getPesooriginal();
            linha[4] = suc.getPesoenviado();
            if (suc.getSituacao().equals("MANUAL")) {
                linha[4] = "";
            }
            linha[5] = "";
            if (suc.getSituacao().equals("MANUAL") && suc.getDebitocredito().equals("3 - DEBITO")) {
                linha[5] = suc.getPesomovimento();
            }
            if (suc.getSituacao().equals("MANUAL") && suc.getDebitocredito().equals("4 - CREDITO")) {
                linha[5] = suc.getPesosucata();
            }
            linha[6] = suc.getPercentualrendimento();
            linha[7] = suc.getSucata();
            linha[8] = suc.getOrdemcompra();
            linha[9] = suc.getPesoordemcompra();
            linha[10] = suc.getNotasaida();
            linha[11] = suc.getPesofaturado();
            linha[12] = suc.getNotaentrada();
            linha[13] = suc.getPesorecebido();
            TableCellRenderer renderer = new SucataContaCorrente.ColorirRenderer();
            jTableEnvio.getColumnModel().getColumn(14).setCellRenderer(renderer);
            
            linha[14] = suc.getDebitocredito();
            linha[15] = suc.getCliente() + " - " + suc.getCadCliente().getNome();
            linha[16] = suc.getDatageracaoS();
            linha[17] = suc.getCodigolancamento();
            linha[18] = suc.getSequencia();
            linha[19] = suc.getSituacao();
            linha[20] = suc.getEmpresa();
            linha[21] = suc.getFilial();
            linha[22] = suc.getObservacaomovimento();
            linha[23] = suc.getCliente();
            linha[24] = suc.getFilialsucata();
            peso_sucata_cre += suc.getPeso_sucata_cre();
            
            System.out.println(" Sucata " + suc.getPeso_sucata_cre());
            System.out.println(" Sucata manual " + peso_sucata_cre_manual);
            System.out.println(" Total " + peso_sucata_cre);
            
            peso_sucata_deb += suc.getPeso_sucata_deb() + peso_sucata_deb_manual;
            peso_sucata_deb_manual = 0.0;

            // peso_sucata_deb += suc.getPeso_sucata_deb();
            peso_produto_cre += suc.getPeso_produto_cre();
            if (suc.getAutomoto().equals("IND")) {
                peso_produto_deb += suc.getPesofaturado();
            } else {
                peso_produto_deb = 0;
                peso_produto_deb_manual = 0;
            }
            
            modeloCarga.addRow(linha);
        }
        //  txtPesoNotaComplementar.setText(String.valueOf(peso_nota_complementar));
        peso_sucata_cre = peso_sucata_cre + peso_sucata_cre_manual;
        peso_sucata_sld = peso_sucata_cre - peso_sucata_deb;
        peso_produto_sld = (peso_produto_cre + peso_produto_cre_manual) - (peso_produto_deb + peso_produto_deb_manual);
        
        formatarCampoPeso(peso_sucata_cre, "CS");
        formatarCampoPeso(peso_sucata_deb, "DS");
        formatarCampoPeso(peso_sucata_sld, "SC");
        
        formatarCampoPeso(peso_produto_cre + peso_produto_cre_manual, "CP");
        formatarCampoPeso(peso_produto_deb + peso_produto_deb_manual, "DP");
        formatarCampoPeso(peso_produto_sld, "SP");
        
    }
    
    private void formatarCampoPeso(double peso, String tipo) {
        
        String pesoS = FormatarPeso.mascaraPorcentagem(peso, FormatarPeso.PORCENTAGEM);
        if (tipo.equals("CS")) {
            lblCreditoSucata.setText(pesoS);
        }
        if (tipo.equals("DS")) {
            lblDebitoSucata.setText(pesoS);
        }
        if (tipo.equals("SC")) {
            lblSaldoSucata.setText(pesoS);
            
        }
        
    }
    
    private void getFilial() throws SQLException {
        if (!codigoEmpresa.equals("0") && !codigoFilial.equals("0")) {
            FilialDAO dao = new FilialDAO();
            filial = new Filial();
            filial = dao.getFilia("", ""
                    + " and e070fil.codemp  = " + codigoEmpresa + " "
                    + " and e070fil.codfil = " + codigoFilial);
        }
    }
    
    private void getFilialFaturamento() throws SQLException {
        if (!txtFilialFaturamento.getText().isEmpty()) {
            FilialDAO dao = new FilialDAO();
            filialFaturamento = new Filial();
            filialFaturamento = dao.getFilia("", " and e070fil.codemp  = 1 "
                    + " and e070fil.codfil = " + txtFilialFaturamento.getText().trim());
        }
    }
    
    public void preencherComboFilial(Integer id) throws SQLException, Exception {
        FilialDAO filialDAO = new FilialDAO();
        List<Filial> listFilial = new ArrayList<Filial>();
        String cod;
        String des;
        String desger;
        txtFiliaOple.removeAllItems();
        
        if (id == 0) {
            listFilial = filialDAO.getFilias("", " and e070fil.codemp = 1  ");
        } else {
            listFilial = filialDAO.getFilias("", " and e070fil.codemp = 1  and codfil = " + id);
            
        }
        
        if (listFilial != null) {
            for (Filial filial : listFilial) {
                cod = filial.getFilial().toString();
                des = filial.getRazao_social();
                desger = cod + " - " + des;
                txtFiliaOple.addItem(desger);
            }
        }
    }
    
    private void pesquisarRegistro(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        jTabSucata.setSelectedIndex(0);
        ((DefaultTableModel) jTableEnvio.getModel()).setRowCount(0);
        
        lblCreditoSucata.setText("0");
        lblDebitoSucata.setText("0");
        lblSaldoSucata.setText("0");
        if (!txtPesReg.getText().isEmpty()) {
            ClienteDAO dao = new ClienteDAO();
            this.cliente = dao.getClienteSucata("CLI", " and codcli = " + txtPesReg.getText().trim());
            if (this.cliente != null) {
                if (this.cliente.getCodigo() > 0) {
                    txtCliente.setText(String.valueOf(cliente.getCodigo()));
                    txtNomeCliente.setText(cliente.getNome());
                    txtGrupo.setText(cliente.getGrupocodigo() + " - " + cliente.getCadClienteGrupo().getNome());
                    
                    pegarDataDigitada();
                    getListarMovimento(PESQUISA_POR, PESQUISA);
                    btnAdd.setEnabled(false);
                    if (listSucataMovimento != null) {
                        if (listSucataMovimento.size() == 0) {
                            btnAdd.setEnabled(true);
                        }
                    }
                }
            }
        }
        
    }
    
    private void pesquisarRegistroGeral(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        jTabSucata.setSelectedIndex(0);
        ((DefaultTableModel) jTableEnvio.getModel()).setRowCount(0);
        this.cliente = new Cliente();
        this.cliente.setCodigo(0);
        lblCreditoSucata.setText("0");
        lblDebitoSucata.setText("0");
        lblSaldoSucata.setText("0");
        pegarDataDigitada();
        
        getListarMovimento(PESQUISA_POR, PESQUISA);
        btnAdd.setEnabled(false);
        if (listSucataMovimento != null) {
            if (listSucataMovimento.size() == 0) {
                btnAdd.setEnabled(true);
            }
        }
        
    }
    
    private String tipoLancamento;
    
    public void getListarMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (tipoLancamento.equals("NORMAL")) {
            PESQUISA += " and usu_debcre not in ('0 - REMOVIDO')";
            listSucataMovimento = this.sucataMovimentoDAO.getSucatasMovimentoAgrupado(PESQUISA_POR, PESQUISA);
        } else {
            listSucataMovimento = this.sucataMovimentoDAO.getSucatasMovimentoAgrupadoGerado(PESQUISA_POR, PESQUISA);
        }
        
        if (listSucataMovimento != null) {
            carregarTabelaMovimento(false);
        }
        
    }
    
    public void carregarTabelaMovimento(boolean selecionar) throws Exception {
        redColunastabCarga();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon IndIcon = getImage("/images/bateriaindu.png");
        ImageIcon gerIcon = getImage("/images/sitMedio.png");
        ImageIcon proIcon = getImage("/images/money_add.png");
        String situacao = "";
        
        double pesoP = 0.0;
        for (SucataMovimento suc : listSucataMovimento) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTableCarga.getColumnModel();
            SucataContaCorrente.JTableRenderer renderers = new SucataContaCorrente.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            this.tipoSucata = suc.getAutomoto();
            switch (suc.getAutomoto()) {
                case "IND":
                    linha[0] = IndIcon;
                    situacao = "SUCATA DE INDUSTRIALIZAÇÃO";
                    break;
                case "AUT":
                    linha[0] = AutIcon;
                    situacao = "SUCATA DE AUTO";
                    this.tipoSucata = "AUT";
                    break;
                case "MOT":
                    linha[0] = MotIcon;
                    situacao = "SUCATA DE MOTO";
                    this.tipoSucata = "MOT";
                    break;
                
                default:
                    break;
            }
            if (tipoLancamento.equals("GERADO")) {
                if (suc.getDebitocredito().equals("1 - GERADO")) {
                    linha[0] = gerIcon;
                    pesoP += suc.getPesopedido();
                }
                if (suc.getDebitocredito().equals("2 - PROVISIONADO")) {
                    linha[0] = proIcon;
                    pesoP += suc.getPesopedido();
                }
            }
            linha[1] = suc.getCodigolancamento();
            linha[2] = suc.getCliente();
            linha[3] = suc.getCadCliente().getNome();
            linha[4] = suc.getMes();
            linha[5] = suc.getAno();
            linha[6] = suc.getDatageracaoS();
            
            linha[7] = suc.getFilial();
            linha[8] = situacao;
            linha[9] = suc.getSequencia();
            linha[10] = suc.getPedido();
            linha[11] = suc.getFilialsucata();
            linha[12] = suc.getAutomoto();
            
            modeloCarga.addRow(linha);
        }

//        lblMsg.setText("PESO PEDIDO PROVISIONADO: " + FormatarPeso.mascaraPorcentagem(pesoP, FormatarPeso.PORCENTAGEM));
    }
    
    private void redColunastabCarga() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        
        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(9).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(10).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(11).setCellRenderer(direita);
        jTableCarga.setRowHeight(40);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoCreateRowSorter(true);
        // jTableCarga.setAutoResizeMode(0);
    }
    
    private void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        
        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        
        jTableEnvio.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(8).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(9).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(10).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(11).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(12).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(13).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(14).setCellRenderer(centralizado);
        jTableEnvio.getColumnModel().getColumn(16).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(17).setCellRenderer(direita);
        jTableEnvio.setRowHeight(40);
        jTableEnvio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        jTableEnvio.setAutoCreateRowSorter(true);
        jTableEnvio.setAutoResizeMode(0);
        
    }
    
    public void retornarCliente(String PESQUISA_POR, String PESQUISA, Cliente cliente) throws Exception {
        this.cliente = new Cliente();
        this.cliente = cliente;
        txtCliente.setText(cliente.getCodigo().toString());
        txtPesReg.setText(cliente.getCodigo().toString());
        txtNomeCliente.setText(cliente.getNome());
        txtGrupo.setText(cliente.getGrupocodigo() + " - " + cliente.getGruponome());
        
        codigoFilial = "0";
        pegarFilial("1");
        
        pegarDataDigitada();
        pesquisarRegistro("DATA", " and usu_codcli = " + cliente.getCodigo() + "");
        
        getListarContaCorrente("", " and usu_codcli = " + txtCliente.getText());
    }
    
    public void retornarGrupo(String PESQUISA_POR, String PESQUISA, String grupo) throws Exception {
        txtGrupo.setText(grupo);
        if (!txtGrupo.getText().isEmpty()) {
            try {
                pegarDataDigitada();
                String pes = txtGrupo.getText();
                int index = pes.indexOf("-");
                String pesquisar = pes.substring(0, index);
                pesquisarRegistroGeral("CLI", " and usu_debcre not in ('IND') and usu_datger >= '" + datIni + "\n"
                        + "' and usu_datger <='" + datFim + "'\n"
                        + "  and cli.codgre = " + pesquisar + "");
                
                getListarContaCorrente("", "and usu_debcre not in ('IND')  and cli.codgre = " + pesquisar);
                
            } catch (Exception ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

//    private String sqlRel = "";
    private void getCliente() throws SQLException, Exception {
        
        ClienteDAO dao = new ClienteDAO();
        this.cliente = dao.getClienteSucata("CLI", " and codcli = " + txtCliente.getText());
        if (cliente != null) {
            if (cliente.getCodigo() > 0) {
                txtNomeCliente.setText(cliente.getCodigo() + " - " + cliente.getNome());
                if ((cliente.getGrupocodigo() == null) || (cliente.getGrupocodigo().equals("0"))) {
                    cliente.setGruponome("Não informado");
                    cliente.setGrupocodigo("0");
                    
                }
                txtGrupo.setText(cliente.getGrupocodigo() + " - " + cliente.getGruponome());
                getListarContaCorrente("", " and usu_codcli = " + txtCliente.getText());
                btnNfv.setEnabled(false);
                btnAddMovimento.setEnabled(true);
            }
        }
    }
    
    void retornarSucata() throws ParseException, Exception {
        try {
            getListarContaCorrente("", " and usu_datger >= '" + datIni + "'"
                    + " and usu_datger <= '" + datFim + "'"
                    + "  and usu_codcli = " + txtCliente.getText());
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }
    
    private void pegarDataDigitada() throws ParseException {
        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
    }
    
    public void iniciarBarra(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Gerando relatório de conta corrente de sucata");
        
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                gerarRelatatorio("");
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
    
    public final void gerarRelatatorio(String sql) throws SQLException, Exception {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            try {
                String Data = "";
                String arquivo = txtCliente.getText();
                String relatorio = "DSGE908";
                
                String entrada = "<ECodCli=" + txtCliente.getText() + "><EAnaCre=A>";
                String diretorio = "\\\\SRV-SPNS01\\Senior_ERP\\Sapiens\\Relatorios\\Sucata\\";
                WSRelatorio.chamarMetodoWsXmlHttpSapiens(arquivo, relatorio, entrada, diretorio, "tsfNormal");
                
                desktop.open(new File(diretorio + arquivo + ".IMP"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "ERRO " + ex);
            }
            
        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado");
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, e, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
        }
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
    
    private Usuario usuario;
    private String filtroFilial;
    
    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        usuario.setId(Menu.getUsuario().getId());
        sUsuario = usuario.getId();
        //  String PesquisaFilial = AbrangeciaSapiens.AbrangeciaSapiensFilial(sUsuario.toString().trim());
        //  filtroFilial = " and  codfil in (" + PesquisaFilial + ")";
        //  filtroFilialSucata = " and  usu_codfil in (" + PesquisaFilial + ")";
        return usuario;
    }
    
    private void pegarFilial(String codfil) throws SQLException, Exception {
        FilialDAO dao = new FilialDAO();
        this.filial = dao.getFilia("", "and codemp = 1 and codfil = " + codfil);
        preencherComboFilial(Integer.valueOf(codigoFilial));
    }
    
    private String selecionarFilial() {
        String filial = txtFiliaOple.getSelectedItem().toString();
        int index = filial.indexOf("-");
        String filialSelecionada = filial.substring(0, index);
        return filialSelecionada;
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
    
    class EvenOddRenderer implements TableCellRenderer {
        
        public DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
        
        public Component getTableCellRendererComponent(JTable jTableCarga, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(
                    jTableCarga, value, isSelected, hasFocus, row, column);
            Color foreground, background;
            if (isSelected) {
                foreground = Color.yellow;
                background = Color.black;
            } else {
                if (row % 2 == 0) {
                    foreground = Color.blue;
                    background = Color.white;
                } else {
                    foreground = Color.white;
                    background = Color.blue;
                }
            }
            renderer.setForeground(foreground);
            renderer.setBackground(background);
            return renderer;
        }
    }
    // private static Color COR_ESTOQUE_HFF = new Color(66, 111, 66);

    public class ColorirRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(
                JTable jTableCarga, Object value, boolean selected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(jTableCarga, value, selected, hasFocus, row, col);
            setBackground(Color.WHITE);
            String str = (String) value;
            if (null == str) {
                setForeground(Color.BLACK);
            } else {
                switch (str) {
                    case "3 - DEBITO":
                        setForeground(Color.RED);
                        break;
                    case "4 - CREDITO":
                        setForeground(Color.WHITE);
                        setBackground(COR_ESTOQUE_HFF);
                        break;
                    default:
                        setForeground(Color.BLACK);
                        break;
                }
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

        jPanel1 = new javax.swing.JPanel();
        txtCliente = new org.openswing.swing.client.TextControl();
        txtNomeCliente = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtGrupo = new org.openswing.swing.client.TextControl();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnPesPer = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnPesquisarGrupo = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblCreditoSucata = new javax.swing.JLabel();
        lblDebitoSucata = new javax.swing.JLabel();
        lblSaldoSucata = new javax.swing.JLabel();
        jTabSucata = new javax.swing.JTabbedPane();
        jPanelSucatas = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        txtPesReg = new org.openswing.swing.client.TextControl();
        btnPes = new javax.swing.JButton();
        btnPes1 = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        jPanelMovimento = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableEnvio = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btnHoras = new javax.swing.JButton();
        btnNotaEntrada = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        btnHoras1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtLancamento = new org.openswing.swing.client.NumericControl();
        jLabel5 = new javax.swing.JLabel();
        txtSequencia = new org.openswing.swing.client.NumericControl();
        jLabel6 = new javax.swing.JLabel();
        btnNfv = new javax.swing.JButton();
        btnPesquisaConta = new javax.swing.JButton();
        txtPedido = new org.openswing.swing.client.TextControl();
        txtOrdemCompra = new org.openswing.swing.client.TextControl();
        txtNotaEntrada = new org.openswing.swing.client.TextControl();
        txtNotaSaida = new org.openswing.swing.client.TextControl();
        btnImprimir = new javax.swing.JButton();
        btnHubMov = new javax.swing.JButton();
        btnAddMovimento = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableImagens = new javax.swing.JTable();
        lblImagem = new javax.swing.JLabel();
        btnVerImagens = new javax.swing.JButton();
        btnFiltrar1 = new javax.swing.JButton();
        btnFiltrar2 = new javax.swing.JButton();
        btnFiltrar4 = new javax.swing.JButton();
        txtFiliaOple = new javax.swing.JComboBox<>();
        btnFiltrar8 = new javax.swing.JButton();
        btnFiltrar9 = new javax.swing.JButton();
        btnFiltrarPro = new javax.swing.JButton();
        btnHub = new javax.swing.JButton();
        barra = new javax.swing.JProgressBar();
        txtFilialFaturamento = new org.openswing.swing.client.TextControl();
        txtHub = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sucata ECO");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente"));

        txtCliente.setEnabled(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNomeCliente.setEnabled(false);
        txtNomeCliente.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel1.setText("ID");

        jLabel11.setText("Nome");

        txtGrupo.setEnabled(false);
        txtGrupo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnPesPer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesPer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesPerActionPerformed(evt);
            }
        });

        jLabel3.setText("Grupo");

        jLabel8.setText("Data Inicial");

        jLabel12.setText("Data Final");

        btnPesquisarGrupo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesquisarGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarGrupoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(78, 78, 78))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(txtNomeCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtGrupo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPesquisarGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(btnPesPer, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel12)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jLabel3)
                        .addComponent(jLabel8)
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesPer, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisarGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnPesPer, txtDatFim, txtDatIni});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCliente, txtNomeCliente});

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblCreditoSucata.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblCreditoSucata.setForeground(new java.awt.Color(51, 102, 255));
        lblCreditoSucata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCreditoSucata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio.png"))); // NOI18N
        lblCreditoSucata.setBorder(javax.swing.BorderFactory.createTitledBorder("Credito Sucata"));
        lblCreditoSucata.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblCreditoSucata.setOpaque(true);
        lblCreditoSucata.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCreditoSucataMouseClicked(evt);
            }
        });

        lblDebitoSucata.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblDebitoSucata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDebitoSucata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_retorno.png"))); // NOI18N
        lblDebitoSucata.setBorder(javax.swing.BorderFactory.createTitledBorder("Débito Sucata"));
        lblDebitoSucata.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblDebitoSucata.setOpaque(true);
        lblDebitoSucata.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDebitoSucataMouseClicked(evt);
            }
        });

        lblSaldoSucata.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblSaldoSucata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSaldoSucata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/indu.png"))); // NOI18N
        lblSaldoSucata.setBorder(javax.swing.BorderFactory.createTitledBorder("Saldo Sucata"));
        lblSaldoSucata.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblSaldoSucata.setOpaque(true);
        lblSaldoSucata.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSaldoSucataMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(lblCreditoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(lblDebitoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(lblSaldoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCreditoSucata)
                    .addComponent(lblDebitoSucata, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSaldoSucata))
                .addGap(2, 2, 2))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblCreditoSucata, lblDebitoSucata, lblSaldoSucata});

        jTabSucata.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabSucataMouseClicked(evt);
            }
        });

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Id", "Cliente", "Nome", "Mês", "Ano", "Ult. Movimento", "Filial Origem", "Situação", "Mov", "Pedido", "Filial Destino", "Tipo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, false, false, false, false, false, true, false, false
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
        jScrollPane5.setViewportView(jTableCarga);
        if (jTableCarga.getColumnModel().getColumnCount() > 0) {
            jTableCarga.getColumnModel().getColumn(0).setMinWidth(30);
            jTableCarga.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTableCarga.getColumnModel().getColumn(0).setMaxWidth(30);
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(200);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(200);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(200);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(0);
        }

        txtPesReg.setColumns(15);
        txtPesReg.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesReg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesRegActionPerformed(evt);
            }
        });

        btnPes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user_suit.png"))); // NOI18N
        btnPes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesActionPerformed(evt);
            }
        });

        btnPes1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPes1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPes1ActionPerformed(evt);
            }
        });

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-sinal-de-reciclagem-16.png"))); // NOI18N
        btnAdd.setText("ADD");
        btnAdd.setEnabled(false);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSucatasLayout = new javax.swing.GroupLayout(jPanelSucatas);
        jPanelSucatas.setLayout(jPanelSucatasLayout);
        jPanelSucatasLayout.setHorizontalGroup(
            jPanelSucatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1228, Short.MAX_VALUE)
            .addGroup(jPanelSucatasLayout.createSequentialGroup()
                .addComponent(txtPesReg, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPes1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnPes, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 648, Short.MAX_VALUE)
                .addComponent(btnAdd))
        );
        jPanelSucatasLayout.setVerticalGroup(
            jPanelSucatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSucatasLayout.createSequentialGroup()
                .addGroup(jPanelSucatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPesReg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPes1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdd))
                .addGap(3, 3, 3)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        jPanelSucatasLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnPes, txtPesReg});

        jTabSucata.addTab("1 - Sucatas", jPanelSucatas);

        jPanelMovimento.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jTableEnvio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Processo", "Pedido", "Peso Pedido", "Peso", "Manual", "% Rent.", "Sucata", "O.C", "Peso", "Nota Saída", "Peso", "Nota Entrada", "Peso", "Movimento", "#", "Emissão", "Mov", "Seq", "Situação", "Empresa", "Filial", "Observação", "Cliente", "FilialDestino"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, false, false, false, true, false, true, false, true, false, false, false, false, true, false, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableEnvio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableEnvioMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTableEnvio);
        if (jTableEnvio.getColumnModel().getColumnCount() > 0) {
            jTableEnvio.getColumnModel().getColumn(0).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(0).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(1).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(2).setMinWidth(50);
            jTableEnvio.getColumnModel().getColumn(2).setPreferredWidth(50);
            jTableEnvio.getColumnModel().getColumn(2).setMaxWidth(50);
            jTableEnvio.getColumnModel().getColumn(3).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(3).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(3).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(4).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(5).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(5).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(5).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(6).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(6).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(7).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(8).setMinWidth(50);
            jTableEnvio.getColumnModel().getColumn(8).setPreferredWidth(50);
            jTableEnvio.getColumnModel().getColumn(8).setMaxWidth(50);
            jTableEnvio.getColumnModel().getColumn(9).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(9).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(9).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(10).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(10).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(10).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(11).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(11).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(11).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(12).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(12).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(12).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(13).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(13).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(13).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(14).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(14).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(15).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(15).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(15).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(16).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(16).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(16).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(17).setMinWidth(50);
            jTableEnvio.getColumnModel().getColumn(17).setPreferredWidth(50);
            jTableEnvio.getColumnModel().getColumn(17).setMaxWidth(50);
            jTableEnvio.getColumnModel().getColumn(18).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(18).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(18).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(19).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(19).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(19).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(20).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(20).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(20).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(21).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(21).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(21).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(22).setMinWidth(500);
            jTableEnvio.getColumnModel().getColumn(22).setPreferredWidth(500);
            jTableEnvio.getColumnModel().getColumn(22).setMaxWidth(500);
            jTableEnvio.getColumnModel().getColumn(23).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(23).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(23).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(24).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(24).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(24).setMaxWidth(100);
        }

        jLabel7.setText("Pedido");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setText("OC");

        btnHoras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        btnHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHorasActionPerformed(evt);
            }
        });

        btnNotaEntrada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        btnNotaEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNotaEntradaActionPerformed(evt);
            }
        });

        jLabel13.setText("Nota Entrada");

        btnHoras1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        btnHoras1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoras1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Nota Saída");

        txtLancamento.setEnabled(false);
        txtLancamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel5.setText("Lancamento");

        txtSequencia.setEnabled(false);
        txtSequencia.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel6.setText("Seq");

        btnNfv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Nota.png"))); // NOI18N
        btnNfv.setText("Nfv");
        btnNfv.setEnabled(false);
        btnNfv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNfvActionPerformed(evt);
            }
        });

        btnPesquisaConta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-vassoura-16.png"))); // NOI18N
        btnPesquisaConta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisaContaActionPerformed(evt);
            }
        });

        txtPedido.setEnabled(false);
        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtOrdemCompra.setEnabled(false);
        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtNotaEntrada.setEnabled(false);
        txtNotaEntrada.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtNotaSaida.setEnabled(false);
        txtNotaSaida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnHubMov.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/posvenda.png"))); // NOI18N
        btnHubMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHubMovActionPerformed(evt);
            }
        });

        btnAddMovimento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        btnAddMovimento.setText("Add");
        btnAddMovimento.setEnabled(false);
        btnAddMovimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMovimentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMovimentoLayout = new javax.swing.GroupLayout(jPanelMovimento);
        jPanelMovimento.setLayout(jPanelMovimentoLayout);
        jPanelMovimentoLayout.setHorizontalGroup(
            jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel7))
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btnHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtNotaEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btnNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtNotaSaida, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btnHoras1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txtLancamento, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtSequencia, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnPesquisaConta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHubMov)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnImprimir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddMovimento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNfv)
                        .addGap(2, 2, 2))
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(jScrollPane4)
        );
        jPanelMovimentoLayout.setVerticalGroup(
            jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel2)
                    .addComponent(jLabel13)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(2, 2, 2)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNotaEntrada)
                    .addComponent(btnPesquisaConta, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHubMov, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddMovimento, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNfv, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHoras1)
                    .addComponent(txtNotaSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHoras)
                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelMovimentoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAddMovimento, btnHubMov, btnImprimir, btnNfv, btnPesquisaConta, txtSequencia});

        jPanelMovimentoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnHoras, btnHoras1, btnNotaEntrada, jButton3, txtNotaSaida, txtOrdemCompra, txtPedido});

        jTabSucata.addTab("2 - Movimentos", jPanelMovimento);

        jTableImagens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Seq.", "Imagem"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableImagens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableImagensMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableImagens);
        if (jTableImagens.getColumnModel().getColumnCount() > 0) {
            jTableImagens.getColumnModel().getColumn(0).setMinWidth(50);
            jTableImagens.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableImagens.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        lblImagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/work.png"))); // NOI18N

        btnVerImagens.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Importar.png"))); // NOI18N
        btnVerImagens.setText("Ver Imagens");
        btnVerImagens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerImagensActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerImagens, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblImagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnVerImagens)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblImagem, javax.swing.GroupLayout.PREFERRED_SIZE, 366, Short.MAX_VALUE)))
        );

        jTabSucata.addTab("Imagens", jPanel2);

        btnFiltrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/carros.png"))); // NOI18N
        btnFiltrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar1ActionPerformed(evt);
            }
        });

        btnFiltrar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png"))); // NOI18N
        btnFiltrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar2ActionPerformed(evt);
            }
        });

        btnFiltrar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/chart_organisation.png"))); // NOI18N
        btnFiltrar4.setText("ERBS");
        btnFiltrar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar4ActionPerformed(evt);
            }
        });

        txtFiliaOple.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtFiliaOple.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "9 - SELECIONE A OPLE" }));
        txtFiliaOple.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFiliaOpleMouseClicked(evt);
            }
        });
        txtFiliaOple.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFiliaOpleActionPerformed(evt);
            }
        });

        btnFiltrar8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar8ActionPerformed(evt);
            }
        });

        btnFiltrar9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sitMedio.png"))); // NOI18N
        btnFiltrar9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar9ActionPerformed(evt);
            }
        });

        btnFiltrarPro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        btnFiltrarPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarProActionPerformed(evt);
            }
        });

        btnHub.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/posvenda.png"))); // NOI18N
        btnHub.setText("HUB");
        btnHub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHubActionPerformed(evt);
            }
        });

        txtFilialFaturamento.setEnabled(false);
        txtFilialFaturamento.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        txtHub.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtHub.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "9 - SELECIONE O HUB", "HUB_PR_BROS_CLIENTE", "HUB_PR_BROS_FILIAL", "HUB_SP_ANDRANDE_CLIENTE", "HUB_SP_ANDRANDE_FILIAL" }));
        txtHub.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtHubMouseClicked(evt);
            }
        });
        txtHub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHubActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnFiltrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnFiltrar2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnFiltrar9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrarPro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrar4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHub, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHub)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFiliaOple, 0, 120, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrar8, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFilialFaturamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
            .addComponent(jTabSucata, javax.swing.GroupLayout.DEFAULT_SIZE, 1128, Short.MAX_VALUE)
            .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnFiltrar1, btnFiltrar2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFiltrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar9, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrarPro, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHub, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHub, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFiliaOple, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilialFaturamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabSucata, javax.swing.GroupLayout.PREFERRED_SIZE, 370, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar1, btnFiltrar2, btnFiltrar4, btnFiltrar8, btnFiltrar9, btnFiltrarPro, btnHub, txtFiliaOple, txtFilialFaturamento, txtHub});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesActionPerformed
        try {
            jTabSucata.setSelectedIndex(0);
            Clientes sol = new Clientes();
            MDIFrame.add(sol, true);
            tipoControle = "";
            sol.setPosicao();
            sol.setMaximum(true); // executa maximizado
            sol.setRecebePalavra(this, "");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPesActionPerformed

    private void btnPesPerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesPerActionPerformed
        try {
            tipoControle = "";
            pegarDataDigitada();
            datIni = utilDatas.converterDateToStr(txtDatIni.getDate());
            datFim = utilDatas.converterDateToStr(txtDatFim.getDate());
            preencherComboFilial(0);
            tipoLancamento = "NORMAL";
            tipoSucata = "MOT";
            pesquisarRegistroGeral("TIPO",
                    " and usu_autmot not in 'IND' "
                    + " and usu_datger >= '" + datIni + "'"
                    + " and usu_datger <='" + datFim + "'");
            
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnPesPerActionPerformed

    private void txtFiliaOpleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFiliaOpleMouseClicked
        //
    }//GEN-LAST:event_txtFiliaOpleMouseClicked

    private void txtFiliaOpleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFiliaOpleActionPerformed
        //
    }//GEN-LAST:event_txtFiliaOpleActionPerformed

    private void btnHoras1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoras1ActionPerformed
        try {
            getListarContaCorrente("", "and usu_numnfv = " + txtNotaSaida.getText());
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnHoras1ActionPerformed

    private void btnHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHorasActionPerformed
        try {
            getListarContaCorrente("", "and usu_numocp = " + txtOrdemCompra.getText());
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnHorasActionPerformed
    
    private String tipoSucata;

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            getListarContaCorrente("", "and usu_numped = " + txtPedido.getText());
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnNotaEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNotaEntradaActionPerformed
        try {
            getListarContaCorrente("", "and usu_numnfc = " + txtNotaEntrada.getText());
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnNotaEntradaActionPerformed

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        try {
            int linhaSelSit = jTableCarga.getSelectedRow();
            int colunaSelSit = jTableCarga.getSelectedColumn();
            txtCliente.setText(jTableCarga.getValueAt(linhaSelSit, 2).toString());
            txtPesReg.setText(jTableCarga.getValueAt(linhaSelSit, 2).toString());
            codigoFilial = jTableCarga.getValueAt(linhaSelSit, 11).toString().trim();
            pegarFilial(codigoFilial);
            tipoSucata = jTableCarga.getValueAt(linhaSelSit, 12).toString().trim();
            codigoEmpresa = "1";
            
            if (!txtCliente.getText().isEmpty()) {
                try {
                    getCliente();
                } catch (SQLException ex) {
                    Mensagem.mensagemRegistros("ERRO", ex.getMessage());
                } catch (Exception ex) {
                    Mensagem.mensagemRegistros("ERRO", ex.getMessage());
                }
                
            }
            if (evt.getClickCount() == 2) {
                jTabSucata.setSelectedIndex(1);
            }
        } catch (SQLException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(SucataContaCorrente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jTableCargaMouseClicked

    private void jTableEnvioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEnvioMouseClicked
        try {
            
            btnNfv.setEnabled(false);
            btnAddMovimento.setEnabled(true);
            int linhaSelSit = jTableEnvio.getSelectedRow();
            int colunaSelSit = jTableEnvio.getSelectedColumn();
            
            String movimento = jTableEnvio.getValueAt(linhaSelSit, 14).toString();
            txtLancamento.setText(jTableEnvio.getValueAt(linhaSelSit, 17).toString());
            txtSequencia.setText(jTableEnvio.getValueAt(linhaSelSit, 18).toString());
            String situcao = jTableEnvio.getValueAt(linhaSelSit, 19).toString();
            txtFilialFaturamento.setText(jTableEnvio.getValueAt(linhaSelSit, 24).toString());
            
            this.sucataMovimento = new SucataMovimento();
            this.sucataMovimento = sucataMovimentoDAO.getContaCorrente("LANCAMENTO", " and usu_codlan = " + txtLancamento.getText() + " "
                    + "\nand usu_seqmov = " + txtSequencia.getText());
            if (this.sucataMovimento != null) {
                if (this.sucataMovimento.getCodigolancamento() > 0 && movimento.equals("3 - DEBITO") && situcao.equals("AUTOMATICO")) {
                    btnNfv.setEnabled(false);
                    
                    txtPedido.setText(String.valueOf(sucataMovimento.getPedido()));
                    txtOrdemCompra.setText(String.valueOf(sucataMovimento.getOrdemcompra()));
                    txtNotaSaida.setText(String.valueOf(sucataMovimento.getNotasaida()));
                    txtNotaEntrada.setText(String.valueOf(sucataMovimento.getNotaentrada()));
                    getFilialFaturamento();
                    
                }

            }
            
        } catch (Exception ex) {
            Mensagem.mensagem("ERROR", ex.toString());
        }
    }//GEN-LAST:event_jTableEnvioMouseClicked

    private void lblCreditoSucataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCreditoSucataMouseClicked
        if (!txtCliente.getText().isEmpty()) {
            try {
                
                getListarContaCorrente("", "  and usu_codcli = " + txtCliente.getText() + "\n"
                        + " and usu_debcre ='4 - CREDITO' ");
                
            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
                
            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_lblCreditoSucataMouseClicked

    private void lblSaldoSucataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSaldoSucataMouseClicked
        //
    }//GEN-LAST:event_lblSaldoSucataMouseClicked

    private void btnFiltrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar1ActionPerformed
        
        try {
            preencherComboFilial(0);
            tipoLancamento = "NORMAL";
            tipoSucata = "AUT";
            tipoControle = "";
            pesquisarRegistroGeral("TIPO",
                    " and usu_autmot = 'AUT' "
                    + "and usu_datger >= '" + datIni + "'"
                    + " and usu_datger <='" + datFim + "'");
        } catch (ParseException ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar1ActionPerformed

    private void btnFiltrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar2ActionPerformed
        
        try {
            preencherComboFilial(0);
            tipoLancamento = "NORMAL";
            tipoSucata = "MOT";
            pesquisarRegistroGeral("TIPO",
                    " and usu_autmot = 'MOT' "
                    + "and usu_datger >= '" + datIni + "'"
                    + " and usu_datger <='" + datFim + "'");
        } catch (ParseException ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar2ActionPerformed

    private void btnFiltrar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar4ActionPerformed
        
        try {
            pegarDataDigitada();
            
            pesquisarRegistroGeral("CLI", " and usu_debcre not in ('IND') \n"
                    + "  and cli.codgre = 158 ");
            
            getListarContaCorrente("", " and usu_debcre not in ('IND')  and cli.codgre =158 ");
            
        } catch (Exception ex) {
            Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar4ActionPerformed

    private void btnFiltrar8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar8ActionPerformed
        if (!txtFiliaOple.getSelectedItem().toString().isEmpty() || txtFiliaOple.getSelectedItem().toString() != null) {
            try {
                tipoLancamento = "NORMAL";
                tipoControle = "";
                pegarDataDigitada();
                String filial = txtFiliaOple.getSelectedItem().toString();
                int index = filial.indexOf("-");
                String filialSelecionada = filial.substring(0, index);
                
                pesquisarRegistroGeral("FILIAL",
                        "  and usu_codfilsuc = '" + filialSelecionada + "' "
                        + "and usu_datger >= '" + datIni + "'"
                        + " and usu_datger <='" + datFim + "'");
            } catch (ParseException ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnFiltrar8ActionPerformed

    private void txtPesRegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesRegActionPerformed
        if (txtPesReg.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {
                
                tipoLancamento = "NORMAL";
                tipoControle = "";
                pesquisarRegistro("TIPO",
                        " and usu_codcli = '" + txtPesReg.getText() + "'");
                
            } catch (ParseException ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtPesRegActionPerformed

    private void btnNfvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNfvActionPerformed
        try {
            
            SucataManualAuto sol = new SucataManualAuto();
            MDIFrame.add(sol, true);
            sol.setPosicao();
            if (this.sucataMovimento == null) {
                this.sucataMovimento = new SucataMovimento();
            }
            if (this.filialFaturamento == null) {
                this.filialFaturamento = new Filial();
            }
            sol.setMaximum(true); // executa maximizado
            sol.setRecebePalavraConta(this, sucataMovimento, this.filial, sucata, cliente, this.filialFaturamento, "NOTA");
            
        } catch (Exception e) {
        }
    }//GEN-LAST:event_btnNfvActionPerformed

    private void btnFiltrar9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar9ActionPerformed
        try {
            preencherComboFilial(0);
            tipoControle = "";
            tipoLancamento = "GERADO";
            pesquisarRegistroGeral("TIPO", "and usu_debcre in ('1 - GERADO') and usu_autmot not in ('IND')");
        } catch (Exception ex) {
            Logger.getLogger(SucataContaCorrente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar9ActionPerformed

    private void btnPesquisaContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisaContaActionPerformed
        try {
            pegarDataDigitada();
            getListarContaCorrente("", "and usu_codcli = " + txtCliente.getText() + " \n"
                    + " and usu_datger >= '" + datIni + "'"
                    + " and usu_datger <= '" + datFim + "'");
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnPesquisaContaActionPerformed

    private void btnFiltrarProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarProActionPerformed
        try {
            tipoControle = "";
            tipoLancamento = "GERADO";
            pesquisarRegistroGeral("TIPO", "and usu_debcre in ('2 - PROVISIONADO') and usu_autmot not in ('IND') ");
        } catch (Exception ex) {
            Logger.getLogger(SucataContaCorrente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrarProActionPerformed

    private void btnPesquisarGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarGrupoActionPerformed
        if (!txtGrupo.getText().isEmpty()) {
            try {
                pegarDataDigitada();
                String pes = txtGrupo.getText();
                int index = pes.indexOf("-");
                String pesquisar = pes.substring(0, index);
                pesquisarRegistroGeral("CLI", " and usu_debcre not in ('IND') and usu_datger >= '" + datIni + "\n"
                        + "' and usu_datger <='" + datFim + "'\n"
                        + "  and cli.codgre = " + pesquisar + "");
                
                getListarContaCorrente("", "and usu_debcre not in ('IND')  and cli.codgre = " + pesquisar);
                
            } catch (Exception ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        

    }//GEN-LAST:event_btnPesquisarGrupoActionPerformed

    private void btnPes1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPes1ActionPerformed
        
        if (txtPesReg.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {
                tipoLancamento = "NORMAL";
                tipoControle = "";
                pesquisarRegistro("TIPO",
                        " and usu_codcli = '" + txtPesReg.getText() + "'");
            } catch (ParseException ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnPes1ActionPerformed

    private void lblDebitoSucataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDebitoSucataMouseClicked
        if (!txtCliente.getText().isEmpty()) {
            try {
                
                getListarContaCorrente("", "  and usu_codcli = " + txtCliente.getText() + "\n"
                        + " and usu_debcre ='3 - DEBITO' ");
                
            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
                
            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_lblDebitoSucataMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if ((cliente != null)) {
            if ((cliente.getCodigo() == 0)) {
                Mensagem.mensagem("ERROR", "Informe o cliente");
            } else {
                jTabSucata.setSelectedIndex(1);
                btnAdd.setEnabled(false);
                try {
                    
                    SucataManualAuto sol = new SucataManualAuto();
                    MDIFrame.add(sol, true);
                    sol.setPosicao();
                    if (this.sucataMovimento == null) {
                        this.sucataMovimento = new SucataMovimento();
                        
                    }
                    if (this.filialFaturamento == null) {
                        this.filialFaturamento = new Filial();
                        this.filialFaturamento.setFilial(1);
                    }
                    if (this.filial == null) {
                        this.filial = new Filial();
                        this.filial.setFilial(1);
                    }
                    
                    sol.setMaximum(true); // executa maximizado
                    sol.setRecebePalavraConta(this, sucataMovimento, this.filial, sucata, cliente, this.filialFaturamento,
                            "MANUTENCAO");
                    
                } catch (Exception e) {
                } finally {
                    
                }
                
            }
        } else {
            Mensagem.mensagem("ERROR", "Informe o cliente e a filial");
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnHubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHubActionPerformed
        if (txtHub.getSelectedItem().equals("9 - SELECIONE O HUB")) {
            Mensagem.mensagem("ERROR", "Selecione o Hub");
        } else {
            try {
                String transacao = "";
                if (txtHub.getSelectedItem().equals("HUB_PR_BROS_CLIENTE")) {
                    transacao = " usu_tnspro  in ('902HB') and usu_codhub = 46 ";
                    
                } else {
                    if (txtHub.getSelectedItem().equals("HUB_PR_BROS_FILIAL")) {
                        transacao = " usu_tnspro  in ('901HB') and codcli in (10469,17231)";
                        filtroFilial = " AND CODFIL IN (29)";
                    } else if (txtHub.getSelectedItem().equals("HUB_SP_ANDRANDE_CLIENTE")) {
                        transacao = " usu_tnspro  in ('902HB') and usu_codhub = 47 ";
                        filtroFilial = " AND CODFIL IN (39)";
                    } else if (txtHub.getSelectedItem().equals("HUB_SP_ANDRANDE_FILIAL")) {
                        transacao = " usu_tnspro  in '901HB' and codcli in (16654)";
                        filtroFilial = " AND CODFIL IN (39)";
                    }
                }
                
                this.cliente = new Cliente();
                this.cliente.setCodigo(0);
                
                preencherComboFilial(0);
                tipoLancamento = "NORMAL";
                pesquisarRegistroGeral("TIPO",
                        "  and " + transacao + " "
                        + " and usu_datger >= '" + datIni + "'"
                        + " and usu_datger <='" + datFim + "'"
                );
                
                getListarContaCorrente("", "and usu_debcre not in ('IND') and  " + transacao + "  and usu_datger >= '" + datIni + "'"
                        + " and usu_datger <='" + datFim + "' ");
                
                tipoControle = "HUB";
                filtroFilial = "";
            } catch (ParseException ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Sucatas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnHubActionPerformed

    private void jTableImagensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableImagensMouseClicked
        int linhaSelSit = jTableImagens.getSelectedRow();
        int colunaSelSit = jTableImagens.getSelectedColumn();
        String id = jTableImagens.getValueAt(linhaSelSit, 0).toString();
        if (!id.isEmpty()) {
            try {
                exibirFoto(id);
            } catch (SQLException ex) {
                Mensagem.mensagem("ERROR", ex.toString());
            } catch (IOException ex) {
                Mensagem.mensagem("ERROR", ex.toString());
            }
        }
    }//GEN-LAST:event_jTableImagensMouseClicked

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        try {
            iniciarBarra("", "");
        } catch (Exception ex) {
            Logger.getLogger(SucataContaCorrente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnHubMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHubMovActionPerformed
        
        try {
            listContaCorrente = this.sucataMovimentoDAO.getContaCorrentesAuto(" HUB ", " \nand usu_debcre not in ('0 - REMOVIDO','7 - CANCELADO')"
                    + "  and usu_autmot in ('AUT','MOT') and usu_tnspro in ('901HB','902HB') and usu_codcli =" + this.cliente.getCodigo());
            if (listContaCorrente != null) {
                carregarTabelaContaCorrente(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SucataContaCorrente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SucataContaCorrente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnHubMovActionPerformed

    private void jTabSucataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabSucataMouseClicked
        lblImagem.setIcon(null);
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableImagens.getModel();
        modeloCarga.setNumRows(0);
    }//GEN-LAST:event_jTabSucataMouseClicked

    private void btnVerImagensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerImagensActionPerformed
        try {
            
            if (txtPedido.getText() != null) {
                getImagens(Integer.valueOf(txtPedido.getText()));
            } else {
                getImagens(0);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(SucataContaCorrente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnVerImagensActionPerformed

    private void btnAddMovimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMovimentoActionPerformed
        try {
            
            SucataManualAuto sol = new SucataManualAuto();
            MDIFrame.add(sol, true);
            sol.setPosicao();
            if (this.sucataMovimento == null) {
                this.sucataMovimento = new SucataMovimento();
            }
            if (this.filialFaturamento == null) {
                this.filialFaturamento = new Filial();
            }
            sol.setMaximum(true); // executa maximizado
            sol.setRecebePalavraConta(this, sucataMovimento, this.filial, sucata, cliente, this.filialFaturamento,
                    "MANUTENCAO");
            
        } catch (Exception e) {
        } finally {
            
        }
    }//GEN-LAST:event_btnAddMovimentoActionPerformed

    private void txtHubMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtHubMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHubMouseClicked

    private void txtHubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHubActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHubActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddMovimento;
    private javax.swing.JButton btnFiltrar1;
    private javax.swing.JButton btnFiltrar2;
    private javax.swing.JButton btnFiltrar4;
    private javax.swing.JButton btnFiltrar8;
    private javax.swing.JButton btnFiltrar9;
    private javax.swing.JButton btnFiltrarPro;
    private javax.swing.JButton btnHoras;
    private javax.swing.JButton btnHoras1;
    private javax.swing.JButton btnHub;
    private javax.swing.JButton btnHubMov;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnNfv;
    private javax.swing.JButton btnNotaEntrada;
    private javax.swing.JButton btnPes;
    private javax.swing.JButton btnPes1;
    private javax.swing.JButton btnPesPer;
    private javax.swing.JButton btnPesquisaConta;
    private javax.swing.JButton btnPesquisarGrupo;
    private javax.swing.JButton btnVerImagens;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelMovimento;
    private javax.swing.JPanel jPanelSucatas;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabSucata;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JTable jTableEnvio;
    private javax.swing.JTable jTableImagens;
    private javax.swing.JLabel lblCreditoSucata;
    private javax.swing.JLabel lblDebitoSucata;
    private javax.swing.JLabel lblImagem;
    private javax.swing.JLabel lblSaldoSucata;
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private javax.swing.JComboBox<String> txtFiliaOple;
    private org.openswing.swing.client.TextControl txtFilialFaturamento;
    private org.openswing.swing.client.TextControl txtGrupo;
    private javax.swing.JComboBox<String> txtHub;
    private org.openswing.swing.client.NumericControl txtLancamento;
    private org.openswing.swing.client.TextControl txtNomeCliente;
    private org.openswing.swing.client.TextControl txtNotaEntrada;
    private org.openswing.swing.client.TextControl txtNotaSaida;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.TextControl txtPesReg;
    private org.openswing.swing.client.NumericControl txtSequencia;
    // End of variables declaration//GEN-END:variables
}
