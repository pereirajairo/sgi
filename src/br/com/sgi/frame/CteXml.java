/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 /*
 * Banco.java
 *
 * Created on 02/05/2009, 04:32:48
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cte;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.bean.XmlArquivos;
import br.com.sgi.bean.XmlCabecalho;
import br.com.sgi.bean.XmlItens;
import br.com.sgi.dao.CteDAO;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.NotaFiscalDAO;
import br.com.sgi.dao.TransportadoraDAO;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;

/**
 *
 * @author Albert Eije
 */
public final class CteXml extends InternalFrame implements MouseListener, ActionListener {

    NotaFiscal nf = new NotaFiscal();

    private static Color COR_ESTOQUE_HFF = new Color(66, 111, 66);
    private Component RdiGrid;
    // Leitura de arquivo
    private XmlArquivos ediArquivos;
    private List<XmlArquivos> lstEdiArquivos;
    //
    private XmlCabecalho xmlCabecalho;
    private XmlItens xmlItens;
    List<XmlItens> listCtes = new ArrayList<XmlItens>();
    //    
    private String pathxml;
    private String dirXml;
    private String diretorio = "\\\\SRV-SPNS01\\XMLs\\17645625000103\\Xml\\17645625000103\\Cte Recebimento\\";
    private String diretorioPrincipal;
    private String diretorioPadrao;
    private String filename = "";
    private Date datini;
    private Date datfim;

    private UtilDatas utilDatas;
    private String arquivo;

    private Cte cte;

    private boolean gravar = true;
    private boolean addreg;

    private Filial filial;

    public CteXml() throws SQLException, Exception {
        initComponents();

        diretorio = "\\\\SRV-SPNS01\\XMLs\\17645625000456\\Xml\\17645625000456\\Cte Recebimento" + UtilDatas.retornarEstruturaPasta(new Date());
        diretorio = "C:\\xml\\cte\\integrar\\";
        txtDirFil.setText(diretorio);

        montarEstruturaData();
        pegarFilial();

        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);

    }

    private void montarEstruturaData() {
        utilDatas = new UtilDatas();

        int ano = utilDatas.retornaAno(new Date());
        int mes = utilDatas.retornaMes(new Date());
        int dia = utilDatas.retornaDia(new Date());
        txtDia.setSelectedItem(String.valueOf(dia));
        txtMes.setValue(mes);
        txtAno.setValue(ano);

    }

    private void montarEstruturaPasta() throws Exception {
        diretorio = "";
        String dir = this.diretorioPadrao;
        System.out.println("DIR" + dir);
        String datas = txtAno.getText() + "\\" + txtMes.getText() + "\\" + txtDia.getSelectedItem() + "\\";
        System.out.println("DATAS" + datas);
        dir += datas;

        diretorioPrincipal = dir;
        diretorio = diretorioPrincipal;
        System.out.println("DIRE" + diretorio);

        txtDirFil.setText(diretorio);
        carregarTabelaFiles();
        nTabProcesso.setSelectedIndex(1);
    }

    private void pegarFilial() throws SQLException, Exception {
        List<Filial> lstFiliais = new ArrayList<Filial>();
        FilialDAO dao = new FilialDAO();
        lstFiliais = dao.getFilias("empresa", " and codemp = 1");
        if (lstFiliais != null) {
            if (lstFiliais.size() > 0) {
                carregarTabelaAgrupado(lstFiliais);
            }
        }

    }

    public void carregarTabelaAgrupado(List<Filial> lstFiliais) throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableFilial.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        jTableFilial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon OkIcon = getImage("/images/sitBom.png");
        ImageIcon ErrorIcon = getImage("/images/sitRuim.png");

        for (Filial fil : lstFiliais) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableFilial.getColumnModel();
            CteXml.JTableRenderer renderers = new CteXml.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = OkIcon;

            linha[1] = fil.getFilial();
            linha[2] = fil.getRazao_social();
            linha[3] = fil.getDiretorio() + txtAno.getText() + "\\" + txtMes.getText() + "\\" + txtDia.getSelectedItem() + "\\";
            linha[4] = fil.getCnpj();
            linha[5] = fil.getDiretorio();
            modeloCarga.addRow(linha);
        }

    }

    public Cte buscarCteCadastrada(String pesquisar_por, String pesquisa) throws SQLException {
        this.cte = new Cte();
        CteDAO dao = new CteDAO();
        this.cte = dao.getCte(pesquisar_por, pesquisa);
        return this.cte;

    }

    public boolean gravarCte() throws SQLException, Exception {
        boolean retorno = false;
        Cte cte = new Cte();
        CteDAO dao = new CteDAO();
        cte.setUsu_codlan(dao.proxCodCad(0));
        if (cte.getUsu_codlan() == 0) {
            cte.setUsu_codlan(1);
        }
        cte.setUsu_codemp(1);
        cte.setUsu_codfil(nf.getFilial());
        cte.setUsu_codfil(1);

        cte.setUsu_chacte(txtChvDoe.getText());
        cte.setUsu_codtra(this.transportadora.getCodigoTransportadora());
        cte.setUsu_datlan(new Date());

        cte.setUsu_datval(null);
        cte.setUsu_estdes(txtEstMunFim.getText());
        cte.setUsu_numcte(txtCte.getText());
        cte.setUsu_numocp(0);
        cte.setUsu_perfrefat(0);
        cte.setUsu_pesnfv(txtPesCar.getDouble());
        cte.setUsu_pesfre(0.0);

        cte.setUsu_valcte(txtValRec1.getDouble());
        cte.setUsu_valfrepes(0);
        cte.setUsu_valnfv(txtValCar.getDouble());

        cte.setUsu_tnspro(nf.getTransacao());
        cte.setUsu_tipfre(lblTipo.getText());
        cte.setUsu_gerocp("S");
        cte.setUsu_codsnf(nf.getSerie());
        cte.setUsu_tnspro(nf.getTransacao());
        cte.setUsu_numnfv(nf.getNotafiscal());
        cte.setUsu_codcli(nf.getCodigocliente());
        cte.setUsu_qtdpro(nf.getQuantidade());
        cte.setUsu_linpro(nf.getLinhaProduto());
        cte.setUsu_valnfv(nf.getValorLiquido());
        cte.setUsu_pesfat(nf.getPesoLiquido());

        cte.setUsu_codfor(Integer.valueOf(txtCodFor.getText()));

        cte.setUsu_codccu("65");
        cte.setUsu_ctafin(txtFilialOc.getText());
        cte.setUsu_cplocp(txtInfo.getText());
        
        String servico = txtCodSer.getSelectedItem().toString();
        int index = servico.indexOf("-");
        String servicoSelecionada = servico.substring(0, index);
        cte.setUsu_codser(servicoSelecionada.trim());

        cte.setUsu_codpro("");
        retorno = dao.inserir(cte);
        if (retorno) {
            this.cte = new Cte();
            this.cte = cte;
            moverArquivoLido();
        }
        return retorno;
    }

    public void moverArquivoLido() throws Exception {
        // arquivo a ser movido
        String origem = diretorio + this.filename;
        File fl = new File(origem);

        // diretorio de destino
        String destino = "\\\\SRV-SPNS01\\XMLs\\17645625000103\\Xml\\17645625000103\\Cte Recebimento\\processados\\" + this.filename;
        destino = "C:\\xml\\cte\\processados\\" + this.filename;
        try {
            fl.renameTo(new File(destino));//aki vc renomiea para outro diretorio  

//            if (JOptionPane.showConfirmDialog(this, "Deseja Arquivar EDI ? " + this.filename, "Programação EDI ",
//                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                fl.delete();
//            } else {
//
//            }
            fl.delete();
            fl = new File(destino);
            fl.createNewFile();
            System.out.println("\nNovo Nome :\t" + "  " + fl.getPath());
            System.out.println("Nome do arquivo :" + fl.getName());
            System.out.println("Propriedades de :\t" + fl.getPath());
            System.out.println("Leitura permitida :\t" + fl.canRead());
            System.out.println("Escrita permitida :\t" + fl.canWrite());
            System.out.println("Diretorio ? :\t\t" + fl.isDirectory());
            System.out.println("Arquivo ? :\t\t" + fl.isFile());
        } catch (IOException ioex) {
            System.out.println("Erro ocorrido !" + ioex);
        } catch (Error ioex) {
            System.out.println("Erro ocorrido !" + ioex);
        } catch (Exception ioex) {
            System.out.println("Erro ocorrido !" + ioex);
        } finally {
            carregarTabelaFiles();
        }
    }

    public void limparTela(int val) {
        txtValAve.setValue(val);
        txtValAdm.setValue(val);
        txtValBCs.setValue(val);
        txtValCar.setValue(val);
        txtValCol.setValue(val);
        txtValCst.setValue(val);
        txtValFPe.setValue(val);
        txtValGri.setValue(val);
        txtValIcm.setValue(val);
        txtValPed.setValue(val);
        txtValQuim.setValue(val);
        txtValRec.setValue(val);
        txtValRec1.setValue(val);
        txtValRep.setValue(val);
        txtValSer.setValue(val);
        txtValTri.setValue(val);
        txtChvDoe.setText("");
        txtNumPrt.setText("");
        txtChvNot.setText("");
        txtCodFor.setText("");
        txtNumNfv.setText("");
        txtCodCli.setText("");
        txtCidCli.setText("");
        txtSigUfs.setText("");
        txtNomCli.setText("");
        txtTransacao.setText("");
        txtInfo.setText("");

        btnGravar.setEnabled(false);
        btnLerCte.setEnabled(false);
        txtOrdemCompra.setText("");

    }

// [lista os arquivos do diretório
    public List<XmlArquivos> listar() throws SQLException, Exception {
        List<XmlArquivos> lstEdiFile = new ArrayList<XmlArquivos>();
        DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");

        File arquivos[];
        File diretorio = new File(txtDirFil.getText());
        arquivos = diretorio.listFiles();

        String dataIni = null;

        if (diretorio.isDirectory()) {
            for (int i = 0; i < arquivos.length; i++) {

                dataIni = new SimpleDateFormat("yyyyMMdd").format(new Date(arquivos[i].lastModified()));
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                Date data = new Date(format.parse(dataIni).getTime());
                ediArquivos = new XmlArquivos();
                ediArquivos.setCaminho(arquivos[i].getAbsolutePath());
                ediArquivos.setFilename(arquivos[i].getName());
                System.out.println(" " + ediArquivos.getFilename());
                String dt_ateracao = formatData.format(new Date(arquivos[i].lastModified()));
                ediArquivos.setLastmodified(dt_ateracao);
                ediArquivos.setLastdate(new Date(arquivos[i].lastModified()));
                ediArquivos.setFileread("Arquivo processado no dia ");
                if (!ediArquivos.getFilename().startsWith("Can") && !ediArquivos.getFilename().startsWith("Inu")) {

                    String finalAqr = ediArquivos.getFilename().substring(ediArquivos.getFilename().lastIndexOf(".") - 3);
//                    if (finalAqr.equals("CTe.xml")) {
//                        lstEdiFile.add(ediArquivos);
//                    }
                    lstEdiFile.add(ediArquivos);
                }

            }
        }

        return lstEdiFile;
    }

    public void carregarTabelaFiles() throws SQLException, Exception {
        redColunastab();
        lstEdiArquivos = listar();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableXml.getModel();
        modeloCarga.setNumRows(0);
        for (int i = 0; i < lstEdiArquivos.size(); i++) {
            Object[] linha = new Object[5];
            linha[0] = i + 1;
            linha[1] = lstEdiArquivos.get(i).getFilename();
            linha[2] = lstEdiArquivos.get(i).getLastmodified();
            linha[3] = lstEdiArquivos.get(i).getFileread();
            linha[4] = lstEdiArquivos.get(i).getArqimp();
            modeloCarga.addRow(linha);
        }
    }

    public void iniciarBarra() {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Processo iniciado");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                lerarq();
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                barra.setString("Processando registro...");
            }
        };
        worker.execute();
    }

    private void lerarq() throws Exception {

        //Aqui você informa o nome do arquivo XML.  
        File f = new File(pathxml);

        //Criamos uma classe SAXBuilder que vai processar o XML  
        SAXBuilder sb = new SAXBuilder();

        //Este documento agora possui toda a estrutura do arquivo.  
        Document d;
        try {
            d = sb.build(f);
            //Recuperamos o elemento root  
            Element nfe = d.getRootElement();

            //Recuperamos os atributos filhos (Attributes)  
            List atributes = nfe.getAttributes();
            Iterator i_atr = atributes.iterator();

            //Iteramos com os atributos filhos  
            while (i_atr.hasNext()) {
                Attribute atrib = (Attribute) i_atr.next();
                System.out.println("\nattribute de (" + nfe.getName() + "):" + atrib.getName() + " - valor: " + atrib.getValue());
            }
            //Recuperamos os elementos filhos (children)  
            List elements = nfe.getChildren();
            Iterator i = elements.iterator();

            //Iteramos com os elementos filhos, e filhos do dos filhos  
            while (i.hasNext()) {
                Element element = (Element) i.next();
                System.out.println("element:" + element.getName());
                trataElement(element);
            }

            this.cte = new Cte();
            this.cte = buscarCteCadastrada("por_chave", "and usu_chacte='" + txtChvDoe.getText() + "'");
            if (this.cte != null) {
                if (this.cte.getUsu_codlan() > 0) {
                    btnGravar.setEnabled(false);
                    txtOrdemCompra.setText(String.valueOf(cte.getUsu_numocp()));
                    Mensagem.mensagem("OK", "Cte lançado na OC " + cte.getUsu_numocp());

                }
            }

            lblTipo.setText("INFORMAÇÃO NÃO ENCONTRADA");
            if (!txtChvNot.getText().isEmpty()) {
                try {
                    NotaFiscalDAO dao = new NotaFiscalDAO();

                    nf = dao.getNotaFiscalCte(" ide ", " and ide.chvdoe = '" + txtChvNot.getText() + "'", new Date());
                    if (nf != null) {
                        if (nf.getNotafiscal() > 0) {
                            txtNumNfv.setText(String.valueOf(nf.getNotafiscal()));
                            txtCodCli.setText(String.valueOf(nf.getCliente().getCodigo()));
                            txtNomCli.setText(nf.getCliente().getNome());
                            txtCidCli.setText(nf.getCliente().getCidade());
                            txtSigUfs.setText(nf.getCliente().getEstado());
                            txtSerieNota.setText(nf.getSerie());
                            txtEmpresaNota.setText(nf.getEmpresa().toString());
                            txtFilialNota.setText(nf.getFilial().toString());

                            txtInfo.setText("FRETE DA NOTA " + txtNumNfv.getText() + " CLIENTE " + txtCodCli.getText() + " - " + txtNomCli.getText());
                            txtTransacao.setText(nf.getTransacao());

                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CteXml.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }

            if (!txtRemCnpj.getText().isEmpty()) {
                try {
                    Filial fil = new Filial();
                    FilialDAO filDAO = new FilialDAO();
                    fil = filDAO.getFilia("cnpj", " and codemp = 1 and \n numcgc = " + txtRemCnpj.getText());
                    if (fil == null) {
                        lblTipo.setText("GARANTIA");
                        txtInfo.setText("CTE DE GARANTIA ");
                    }
                    if (fil != null) {
                        if (fil.getFilial() > 0) {
                            lblTipo.setText("VENDA");
                            txtEmpresaOc.setText(fil.getEmpresa_id().toString());
                            txtFilialOc.setText(fil.getFilial().toString());

                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CteXml.class
                            .getName()).log(Level.SEVERE, null, ex);

                }

            }

        } catch (JDOMException ex) {
            Logger.getLogger(CteXml.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(CteXml.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        if (!txtEmiCnpj.getText().isEmpty()) {
            buscarTransportadora(txtEmiCnpj.getText());

        }
    }

    private String tag;

    private void trataElement(Element element) throws Exception {
        FormatarNumeros formatar = new FormatarNumeros();
        //Recuperamos os atributos filhos (Attributes)  
        List atributes = element.getAttributes();
        Iterator i_atr = atributes.iterator();

        //Iteramos com os atributos filhos  
        while (i_atr.hasNext()) {
            Attribute atrib = (Attribute) i_atr.next();
            tag = "";
            System.out.println("\nattribute de (" + element.getName() + "):" + atrib.getName() + " - valor: " + atrib.getValue());

        }
        //Recuperamos os elementos filhos (children)  
        List elements = element.getChildren();
        Iterator it = elements.iterator();
        int cont = 0;
        //Iteramos com os elementos filhos, e filhos do dos filhos  
        while (it.hasNext()) {

            Element el = (Element) it.next();

            System.out.println("element(" + element.getName() + "):" + el.getName() + " - Valor: " + el.getText());
            if (el.getText().equals("PESO REAL")) {
                tag = el.getText();
                System.out.println(el.getText());
            }

            //  aqui voce pode escolher qual(is) campo(s) quer manipular  
            if (el.getName().equals("cUF")) {
                //  System.out.println("encontrou cOrgao . . . com valor: " + el.getText());
                txtUf.setText(el.getText());
            }
            if (el.getName().equals("cCT")) {
                txtCte.setText(el.getText());
            }
            if (el.getName().equals("CFOP")) {
                txtCfop.setText(el.getText());
            }
            if (el.getName().equals("natOp")) {
                txtNatOpe.setText(el.getText());
            }
            if (el.getName().equals("serie")) {
                txtSerie.setText(el.getText());
            }
            if (el.getName().equals("nCT")) {
                txtNumCtee.setText(el.getText());
            }
            if (el.getName().equals("dhEmi")) {
                txtEmissao.setText(el.getText());
            }

            if (el.getName().equals("cMunEnv")) {
                txtCodMunEnv.setText(el.getText());
            }
            if (el.getName().equals("xMunEnv")) {
                txtNomMunEvi.setText(el.getText());
            }

            if (el.getName().equals("UFEnv")) {
                txtEstMunEnv.setText(el.getText());
            }

            if (el.getName().equals("cMunIni")) {
                txtCodMunIni.setText(el.getText());
            }
            if (el.getName().equals("xMunIni")) {
                txtNomMunIni.setText(el.getText());
            }

            if (el.getName().equals("UFIni")) {
                txtEstMunIni.setText(el.getText());
            }

            if (el.getName().equals("cMunFim")) {
                txtCodMunFim.setText(el.getText());
            }
            if (el.getName().equals("xMunFim")) {
                txtNomMunFim.setText(el.getText());
            }

            if (el.getName().equals("UFFim")) {
                txtEstMunFim.setText(el.getText());
            }

            if (el.getName().equals("CNPJ") && element.getName().equals("emit")) {
                txtEmiCnpj.setText(el.getText());

            }
            if (el.getName().equals("IE") && element.getName().equals("emit")) {
                txtEmiIns.setText(el.getText());

            }

            if (el.getName().equals("xNome") && element.getName().equals("emit")) {
                txtEmiNom.setText(el.getText());

            }

            if (el.getName().equals("CNPJ") && element.getName().equals("rem")) {
                txtRemCnpj.setText(el.getText());

            }
            if (el.getName().equals("IE") && element.getName().equals("rem")) {
                txtRemIns.setText(el.getText());

            }

            if (el.getName().equals("xNome") && element.getName().equals("rem")) {
                txtRemNom.setText(el.getText());

            }

            if (el.getName().equals("xNome") && element.getName().equals("emit")) {
                txtEmiNom.setText(el.getText());

            }

            if (el.getName().equals("CNPJ") && element.getName().equals("dest")) {
                txtDesCnpj.setText(el.getText());

            }
            if (el.getName().equals("IE") && element.getName().equals("dest")) {
                txtDesIns.setText(el.getText());

            }

            if (el.getName().equals("xNome") && element.getName().equals("dest")) {
                txtDesNom.setText(el.getText());

            }

            
            
            if (el.getName().equals("vTPrest") && element.getName().equals("vPrest")) {
                txtValSer.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

            }
            if (el.getName().equals("vRec") && element.getName().equals("vPrest")) {
                txtValRec.setValue(FormatarNumeros.converterStringToDouble(el.getText()));
                txtValRec1.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

            }
            if (el.getName().equals("xNome") && element.getName().equals("Comp")) {
//                System.out.println(el.getName());
//                System.out.println(element.getName());
//                System.out.println(el.getText());

                if (el.getText().equals("FRETE PESO")) {
                    cont = 1;
                }

                if (el.getText().equals("FRETE VALOR")) {
                    cont = 2;
                }

                if (el.getText().equals("GRIS")) {
                    cont = 3;
                }

                if (el.getText().equals("SECCAT")) {
                    cont = 4;
                }

                if (el.getText().equals("PEDAGIO")) {
                    cont = 5;
                }

                if (el.getText().equals("TX-ADM")) {
                    cont = 6;
                }

                if (el.getText().equals("Adicional Quim")) {
                    cont = 7;
                }

                if (el.getText().equals("IMP REPASSADO")) {
                    cont = 8;
                }
            }
            if (cont == 1) {
                if (el.getName().equals("vComp") && element.getName().equals("Comp")) {
                    txtValFPe.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

                }
            }
            if (cont == 2) {
                if (el.getName().equals("vComp") && element.getName().equals("Comp")) {
                    txtValFre.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

                }
            }
            if (cont == 3) {
                if (el.getName().equals("vComp") && element.getName().equals("Comp")) {
                    txtValGri.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

                }
            }
            if (cont == 4) {
                if (el.getName().equals("vComp") && element.getName().equals("Comp")) {
                    txtValCol.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

                }
            }

            if (cont == 5) {
                if (el.getName().equals("vComp") && element.getName().equals("Comp")) {
                    txtValPed.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

                }
            }
            if (cont == 6) {
                if (el.getName().equals("vComp") && element.getName().equals("Comp")) {
                    txtValAdm.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

                }
            }
            if (cont == 7) {
                if (el.getName().equals("vComp") && element.getName().equals("Comp")) {
                    txtValQuim.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

                }
            }
            if (cont == 8) {
                if (el.getName().equals("vComp") && element.getName().equals("Comp")) {
                    txtValRep.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

                }
            }

            if (el.getName().equals("vCarga") && element.getName().equals("infCarga")) {
                txtValCar.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

            }

            if (el.getName().equals("CST") && element.getName().equals("ICMS00")) {
                txtValCst.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

            }

            if (el.getName().equals("vBC") && element.getName().equals("ICMS00")) {
                txtValBCs.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

            }

            if (el.getName().equals("pICMS") && element.getName().equals("ICMS00")) {
                txtPerIcm.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

            }

            if (el.getName().equals("vICMS") && element.getName().equals("ICMS00")) {
                txtValIcm.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

            }
            if (el.getName().equals("vTotTrib") && element.getName().equals("imp")) {
                txtValTri.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

            }

            if (el.getName().equals("qCarga") && element.getName().equals("infQ")) {
                txtPesCar.setValue(FormatarNumeros.converterStringToDouble(el.getText()));
                if (tag.equals("PESO REAL")) {
                    txtPesCar.setValue(FormatarNumeros.converterStringToDouble(el.getText()));
                }
                tag = "";
            }

            if (el.getName().equals("vCargaAverb") && element.getName().equals("infCarga")) {
                txtValAve.setValue(FormatarNumeros.converterStringToDouble(el.getText()));

            }

            if (el.getName().equals("chCTe") && element.getName().equals("infProt")) {
                txtChvDoe.setText(el.getText());

            }
            if (el.getName().equals("nProt") && element.getName().equals("infProt")) {
                txtNumPrt.setText(el.getText());

            }
            if (el.getName().equals("chave") && element.getName().equals("infNFe")) {
                txtChvNot.setText(el.getText());
            }

            trataElement(el);
        }

    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        //empresa
        jTableXml.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTableXml.setRowHeight(42);
        jTableXml.setIntercellSpacing(new Dimension(1, 1));
        jTableXml.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableXml.setAutoCreateRowSorter(true);

        jTableFilial.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTableFilial.setRowHeight(42);
        jTableFilial.setIntercellSpacing(new Dimension(1, 1));
        jTableFilial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableFilial.setAutoCreateRowSorter(true);

    }

    void retornarOrdem(String ordem) {
        txtOrdemCompra.setText(ordem);
    }

    private Transportadora transportadora;

    private void buscarTransportadora(String cnpj) throws SQLException {
        if (!cnpj.isEmpty()) {
            TransportadoraDAO dao = new TransportadoraDAO();
            transportadora = new Transportadora();
            transportadora = dao.getTransportadora("cnpj", " and cgccpf = " + cnpj);
            btnGravar.setEnabled(false);
            if (transportadora != null) {
                if (transportadora.getCodigoTransportadora() > 0) {
                    txtCodFor.setText(transportadora.getFornecedor());
                    txtCodTra.setText(transportadora.getCodigoTransportadora().toString());
                    if (txtOrdemCompra.getText().isEmpty()) {
                        btnGravar.setEnabled(true);
                    }

                }
            }
        }
    }

    public class ColorirRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable jTableCarga, Object value, boolean selected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(jTableCarga, value, selected, hasFocus, row, col);
            setBackground(Color.WHITE);
            String str = (String) value;
            if (null != str) {
                switch (str) {
                    case "ERRO":
                        setForeground(Color.RED);
                        break;
                    case "INSERIR":
                        setForeground(Color.WHITE);
                        setBackground(COR_ESTOQUE_HFF);
                        break;
                    case "ALTERAR":
                        setForeground(Color.WHITE);
                        setBackground(Color.BLUE);
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

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);

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

    /**
     * Creates new form GridFrame2
     */
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

    class RenderCheckBox extends JCheckBox implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected,
                boolean hasFocus, int row,
                int column) {

            setSelected(isSelected);
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
        barra = new javax.swing.JProgressBar();
        jTabbedEdi = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtUf = new org.openswing.swing.client.TextControl();
        txtCte = new org.openswing.swing.client.TextControl();
        jLabel7 = new javax.swing.JLabel();
        txtCfop = new org.openswing.swing.client.TextControl();
        jLabel8 = new javax.swing.JLabel();
        txtNatOpe = new org.openswing.swing.client.TextControl();
        jLabel9 = new javax.swing.JLabel();
        txtSerie = new org.openswing.swing.client.TextControl();
        txtNumCtee = new org.openswing.swing.client.TextControl();
        txtEmissao = new org.openswing.swing.client.TextControl();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtCodMunFim = new org.openswing.swing.client.TextControl();
        txtNomMunFim = new org.openswing.swing.client.TextControl();
        txtEstMunFim = new org.openswing.swing.client.TextControl();
        jPanel8 = new javax.swing.JPanel();
        txtCodMunEnv = new org.openswing.swing.client.TextControl();
        txtNomMunEvi = new org.openswing.swing.client.TextControl();
        txtEstMunEnv = new org.openswing.swing.client.TextControl();
        jPanel9 = new javax.swing.JPanel();
        txtCodMunIni = new org.openswing.swing.client.TextControl();
        txtNomMunIni = new org.openswing.swing.client.TextControl();
        txtEstMunIni = new org.openswing.swing.client.TextControl();
        jPanel10 = new javax.swing.JPanel();
        txtEmiCnpj = new org.openswing.swing.client.TextControl();
        txtEmiNom = new org.openswing.swing.client.TextControl();
        txtEmiIns = new org.openswing.swing.client.TextControl();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        txtRemCnpj = new org.openswing.swing.client.TextControl();
        txtRemNom = new org.openswing.swing.client.TextControl();
        txtRemIns = new org.openswing.swing.client.TextControl();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        txtDesCnpj = new org.openswing.swing.client.TextControl();
        txtDesNom = new org.openswing.swing.client.TextControl();
        txtDesIns = new org.openswing.swing.client.TextControl();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        nTabProcesso = new javax.swing.JTabbedPane();
        jPnlXml = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        txtValRec1 = new org.openswing.swing.client.NumericControl();
        jLabel50 = new javax.swing.JLabel();
        txtCodSer = new javax.swing.JComboBox<>();
        txtCodPgt = new javax.swing.JComboBox<>();
        lblFileSelection = new org.openswing.swing.client.TextControl();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        btnGravar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtInfo = new javax.swing.JTextArea();
        txtOrdemCompra = new org.openswing.swing.client.TextControl();
        jLabel4 = new javax.swing.JLabel();
        lblTipo = new javax.swing.JLabel();
        btnGerarOc = new javax.swing.JButton();
        txtCodTra = new org.openswing.swing.client.TextControl();
        btnLerCte = new javax.swing.JButton();
        txtCodFor = new org.openswing.swing.client.TextControl();
        txtEmpresaOc = new org.openswing.swing.client.TextControl();
        txtFilialOc = new org.openswing.swing.client.TextControl();
        jLabel35 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableXml = new javax.swing.JTable();
        jPnlFrete = new javax.swing.JPanel();
        txtValSer = new org.openswing.swing.client.NumericControl();
        txtValRec = new org.openswing.swing.client.NumericControl();
        txtValFPe = new org.openswing.swing.client.NumericControl();
        txtValFre = new org.openswing.swing.client.NumericControl();
        txtValGri = new org.openswing.swing.client.NumericControl();
        txtValCol = new org.openswing.swing.client.NumericControl();
        txtValPed = new org.openswing.swing.client.NumericControl();
        txtValAdm = new org.openswing.swing.client.NumericControl();
        txtValQuim = new org.openswing.swing.client.NumericControl();
        jLabel22 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtValRep = new org.openswing.swing.client.NumericControl();
        jLabel37 = new javax.swing.JLabel();
        txtValCst = new org.openswing.swing.client.NumericControl();
        jLabel38 = new javax.swing.JLabel();
        txtValBCs = new org.openswing.swing.client.NumericControl();
        jLabel39 = new javax.swing.JLabel();
        txtPerIcm = new org.openswing.swing.client.NumericControl();
        jLabel40 = new javax.swing.JLabel();
        txtValIcm = new org.openswing.swing.client.NumericControl();
        jLabel41 = new javax.swing.JLabel();
        txtValTri = new org.openswing.swing.client.NumericControl();
        jLabel42 = new javax.swing.JLabel();
        txtValCar = new org.openswing.swing.client.NumericControl();
        jLabel43 = new javax.swing.JLabel();
        txtPesCar = new org.openswing.swing.client.NumericControl();
        jLabel44 = new javax.swing.JLabel();
        txtValAve = new org.openswing.swing.client.NumericControl();
        jLabel45 = new javax.swing.JLabel();
        txtNumNfv = new org.openswing.swing.client.TextControl();
        txtCodCli = new org.openswing.swing.client.TextControl();
        txtNomCli = new org.openswing.swing.client.TextControl();
        txtCidCli = new org.openswing.swing.client.TextControl();
        txtSigUfs = new org.openswing.swing.client.TextControl();
        txtTransacao = new org.openswing.swing.client.TextControl();
        jLabel5 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtChvDoe = new javax.swing.JTextField();
        txtSerieNota = new org.openswing.swing.client.TextControl();
        txtEmpresaNota = new org.openswing.swing.client.TextControl();
        txtFilialNota = new org.openswing.swing.client.TextControl();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jPnlFilial = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableFilial = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtNumPrt = new javax.swing.JTextField();
        txtChvNot = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        txtDirFil = new org.openswing.swing.client.TextControl();
        btnDirFil = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnDirFil1 = new javax.swing.JButton();
        txtAno = new org.openswing.swing.client.TextControl();
        txtMes = new org.openswing.swing.client.TextControl();
        txtDia = new javax.swing.JComboBox<>();

        setTitle("CTE - Conhecimento de Transporte Eletrônico");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Orgão");

        txtUf.setEnabled(false);
        txtUf.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtCte.setEnabled(false);
        txtCte.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel7.setText("Cte");

        txtCfop.setEnabled(false);
        txtCfop.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel8.setText("Cfop");

        txtNatOpe.setEnabled(false);
        txtNatOpe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel9.setText("Natureza da Operação");

        txtSerie.setEnabled(false);
        txtSerie.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtNumCtee.setEnabled(false);
        txtNumCtee.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtEmissao.setEnabled(false);
        txtEmissao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel10.setText("Serie");

        jLabel11.setText("Nr Cte");

        jLabel12.setText("Emissão");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Municipio Fim"));

        txtCodMunFim.setEnabled(false);
        txtCodMunFim.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtNomMunFim.setEnabled(false);
        txtNomMunFim.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtEstMunFim.setEnabled(false);
        txtEstMunFim.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(txtCodMunFim, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNomMunFim, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(13, 13, 13)
                .addComponent(txtEstMunFim, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEstMunFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodMunFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomMunFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Municipio Inicio"));

        txtCodMunEnv.setEnabled(false);
        txtCodMunEnv.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtNomMunEvi.setEnabled(false);
        txtNomMunEvi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtEstMunEnv.setEnabled(false);
        txtEstMunEnv.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(txtCodMunEnv, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNomMunEvi, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEstMunEnv, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodMunEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomMunEvi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEstMunEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Municipio Envio"));

        txtCodMunIni.setEnabled(false);
        txtCodMunIni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtNomMunIni.setEnabled(false);
        txtNomMunIni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtEstMunIni.setEnabled(false);
        txtEstMunIni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(txtCodMunIni, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNomMunIni, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEstMunIni, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEstMunIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodMunIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomMunIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Emitente"));

        txtEmiCnpj.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEmiCnpj.addActionListener(this);

        txtEmiNom.setEnabled(false);
        txtEmiNom.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtEmiIns.setEnabled(false);
        txtEmiIns.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel13.setText("Cnpj");

        jLabel14.setText("IE");

        jLabel15.setText("Razão Social");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel15)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEmiNom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmiCnpj, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtEmiIns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(6, 6, 6))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtEmiCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmiIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmiNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        jPanel10Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtEmiCnpj, txtEmiIns, txtEmiNom});

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Remetente"));

        txtRemCnpj.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtRemNom.setEnabled(false);
        txtRemNom.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtRemIns.setEnabled(false);
        txtRemIns.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel16.setText("Cnpj");

        jLabel17.setText("IE");

        jLabel18.setText("Razão Social");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(txtRemNom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel18)
                    .addComponent(txtRemCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(txtRemIns, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(4, 4, 4))))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtRemCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRemIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRemNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Destinatário"));

        txtDesCnpj.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtDesNom.setEnabled(false);
        txtDesNom.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtDesIns.setEnabled(false);
        txtDesIns.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel19.setText("Cnpj");

        jLabel20.setText("IE");

        jLabel21.setText("Razão Social");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel21)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(txtDesNom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDesCnpj, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(txtDesIns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDesCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDesIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDesNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtValRec1.setDecimals(2);
        txtValRec1.setEnabled(false);
        txtValRec1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel50.setText("Valor do frete");

        txtCodSer.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodSer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SVFRE0001 - Frete S/ Venda e S/Compras com crédito", "SVFRE0003 - Frete S/Venda realizada pelas OPLES - Exceto Filial RS", "SVFRE0004 - Frete S/Compras sem credito de ICMS", "SVFRE0005 - Frete S/ Transferencia para OPLES", "SVFRE0007 - Frete S/ Garantia e s/ Compra sem crédito", "SVFRE0012 - Frete S/Vendas realizada Exlusivamente pelas OPLES Tijucas", "SVFRE0013 - Frete S/Vendas realizada Exlusivamente pelas OPLES RS", " " }));

        txtCodPgt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodPgt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "15" }));

        lblFileSelection.setEnabled(false);
        lblFileSelection.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel51.setText("Serviço");

        jLabel52.setText("Condição");

        jLabel53.setText("Arquivo");

        jLabel54.setText("Fornecedor");

        btnGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravar.setText("Gravar");
        btnGravar.setEnabled(false);
        btnGravar.addActionListener(this);

        txtInfo.setColumns(20);
        txtInfo.setRows(5);
        jScrollPane3.setViewportView(txtInfo);

        txtOrdemCompra.setEnabled(false);
        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel4.setText("OC");

        lblTipo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnGerarOc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-caixa-cheia-16x16.png"))); // NOI18N
        btnGerarOc.setText("Gerar OC");
        btnGerarOc.addActionListener(this);

        txtCodTra.setEnabled(false);
        txtCodTra.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnLerCte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder.png"))); // NOI18N
        btnLerCte.setText("Ler Cte");
        btnLerCte.setEnabled(false);
        btnLerCte.addActionListener(this);

        txtCodFor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtEmpresaOc.setEnabled(false);
        txtEmpresaOc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtFilialOc.setEnabled(false);
        txtFilialOc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel35.setText("Emp");

        jLabel47.setText("Fil");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblFileSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtValRec1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel50))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(txtCodSer, 0, 383, Short.MAX_VALUE)
                                                .addGap(4, 4, 4))
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(jLabel51)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtEmpresaOc, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel35))
                                        .addGap(4, 4, 4)
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtFilialOc, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel53)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCodPgt, 0, 211, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(txtCodFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(4, 4, 4)
                                        .addComponent(txtCodTra, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel54)
                                    .addComponent(jLabel52))
                                .addGap(6, 6, 6)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addComponent(lblTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLerCte, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btnGerarOc, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)))
                .addGap(2, 2, 2))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(jLabel51)
                    .addComponent(jLabel52)
                    .addComponent(jLabel35)
                    .addComponent(jLabel47))
                .addGap(1, 1, 1)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtValRec1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCodSer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCodPgt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtEmpresaOc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilialOc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel53)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel54)
                        .addComponent(jLabel4)))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFileSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodTra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLerCte, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGerarOc, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGerarOc, btnGravar, btnLerCte, lblTipo});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableXml.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sequencia", "Arquivo", "Data envio", "Situação", "Arquivo Lido"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableXml.addMouseListener(this);
        jScrollPane2.setViewportView(jTableXml);
        if (jTableXml.getColumnModel().getColumnCount() > 0) {
            jTableXml.getColumnModel().getColumn(0).setMinWidth(100);
            jTableXml.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableXml.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableXml.getColumnModel().getColumn(2).setMinWidth(100);
            jTableXml.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableXml.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableXml.getColumnModel().getColumn(3).setMinWidth(0);
            jTableXml.getColumnModel().getColumn(3).setPreferredWidth(0);
            jTableXml.getColumnModel().getColumn(3).setMaxWidth(0);
            jTableXml.getColumnModel().getColumn(4).setMinWidth(0);
            jTableXml.getColumnModel().getColumn(4).setPreferredWidth(0);
            jTableXml.getColumnModel().getColumn(4).setMaxWidth(0);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPnlXmlLayout = new javax.swing.GroupLayout(jPnlXml);
        jPnlXml.setLayout(jPnlXmlLayout);
        jPnlXmlLayout.setHorizontalGroup(
            jPnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlXmlLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPnlXmlLayout.setVerticalGroup(
            jPnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        nTabProcesso.addTab("Xml", jPnlXml);

        txtValSer.setDecimals(2);
        txtValSer.setEnabled(false);
        txtValSer.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtValRec.setDecimals(2);
        txtValRec.setEnabled(false);
        txtValRec.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtValFPe.setColumns(2);
        txtValFPe.setDecimals(2);
        txtValFPe.setEnabled(false);
        txtValFPe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtValFre.setDecimals(2);
        txtValFre.setEnabled(false);
        txtValFre.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtValGri.setDecimals(2);
        txtValGri.setEnabled(false);
        txtValGri.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtValCol.setDecimals(2);
        txtValCol.setEnabled(false);
        txtValCol.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtValPed.setDecimals(2);
        txtValPed.setEnabled(false);
        txtValPed.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtValAdm.setDecimals(2);
        txtValAdm.setEnabled(false);
        txtValAdm.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtValQuim.setDecimals(2);
        txtValQuim.setEnabled(false);
        txtValQuim.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel22.setText("Serviço");

        jLabel31.setText("A Receber");

        jLabel32.setText("Frete Peso");

        jLabel25.setText("Frete Valor");

        jLabel26.setText("GRIS");

        jLabel27.setText("Serviço Coleta");

        jLabel28.setText("Pedágio");

        jLabel29.setText("TX Adm");

        jLabel30.setText("Adm Quim");

        txtValRep.setDecimals(2);
        txtValRep.setEnabled(false);
        txtValRep.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel37.setText("Imp. Repasse");

        txtValCst.setDecimals(2);
        txtValCst.setEnabled(false);
        txtValCst.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel38.setText("CST");

        txtValBCs.setDecimals(2);
        txtValBCs.setEnabled(false);
        txtValBCs.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel39.setText("Base CST");

        txtPerIcm.setDecimals(2);
        txtPerIcm.setEnabled(false);
        txtPerIcm.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel40.setText("%Icms");

        txtValIcm.setDecimals(2);
        txtValIcm.setEnabled(false);
        txtValIcm.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValIcm.addActionListener(this);

        jLabel41.setText("Valor ICMS");

        txtValTri.setDecimals(2);
        txtValTri.setEnabled(false);
        txtValTri.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValTri.addActionListener(this);

        jLabel42.setText("Total Tributo");

        txtValCar.setDecimals(2);
        txtValCar.setEnabled(false);
        txtValCar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValCar.addActionListener(this);

        jLabel43.setText("Valor Carga");

        txtPesCar.setDecimals(2);
        txtPesCar.setEnabled(false);
        txtPesCar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPesCar.addActionListener(this);

        jLabel44.setText("Peso");

        txtValAve.setDecimals(2);
        txtValAve.setEnabled(false);
        txtValAve.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValAve.addActionListener(this);

        jLabel45.setText("Valor Averbado");

        txtNumNfv.setEnabled(false);
        txtNumNfv.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtCodCli.setEnabled(false);
        txtCodCli.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtNomCli.setEnabled(false);
        txtNomCli.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtCidCli.setEnabled(false);
        txtCidCli.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtSigUfs.setEnabled(false);
        txtSigUfs.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtTransacao.setEnabled(false);
        txtTransacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel5.setText("Nota");

        jLabel36.setText("Cliente");

        jLabel48.setText("Nome");

        jLabel49.setText("Cidade");

        jLabel55.setText("Estado");

        jLabel2.setText("Transação");

        txtChvDoe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtChvDoe.setEnabled(false);

        txtSerieNota.setEnabled(false);
        txtSerieNota.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtEmpresaNota.setEnabled(false);
        txtEmpresaNota.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtFilialNota.setEnabled(false);
        txtFilialNota.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel23.setText("Chave Cte");

        jLabel24.setText("Serie Nota");

        jLabel33.setText("Empresa");

        jLabel34.setText("Filial");

        javax.swing.GroupLayout jPnlFreteLayout = new javax.swing.GroupLayout(jPnlFrete);
        jPnlFrete.setLayout(jPnlFreteLayout);
        jPnlFreteLayout.setHorizontalGroup(
            jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlFreteLayout.createSequentialGroup()
                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPnlFreteLayout.createSequentialGroup()
                                    .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtValSer, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel22)
                                        .addComponent(txtValRep, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                    .addGap(6, 6, 6))
                                .addGroup(jPnlFreteLayout.createSequentialGroup()
                                    .addComponent(jLabel37)
                                    .addGap(36, 36, 36))
                                .addGroup(jPnlFreteLayout.createSequentialGroup()
                                    .addComponent(txtNumNfv, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(86, 86, 86)))
                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtValRec, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                            .addComponent(jLabel31)
                                            .addComponent(txtValCst, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                        .addGap(6, 6, 6))
                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                        .addComponent(jLabel38)
                                        .addGap(83, 83, 83)))
                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtValFPe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addComponent(jLabel32)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                            .addComponent(txtValBCs, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                        .addGap(2, 2, 2))
                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                        .addComponent(jLabel39)
                                        .addGap(48, 48, 48)))
                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                        .addComponent(jLabel40)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtValFre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel25)
                                            .addComponent(txtPerIcm, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addGap(7, 7, 7)
                                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtValGri, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jLabel26)))
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                        .addComponent(jLabel41)
                                                        .addGap(0, 0, Short.MAX_VALUE))
                                                    .addComponent(txtValIcm, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addGap(7, 7, 7)
                                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtValCol, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                                    .addComponent(jLabel27)))
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                        .addComponent(jLabel42)
                                                        .addGap(0, 0, Short.MAX_VALUE))
                                                    .addComponent(txtValTri, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addGap(7, 7, 7)
                                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtValPed, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                                    .addComponent(jLabel28)))
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                        .addComponent(jLabel43)
                                                        .addGap(0, 0, Short.MAX_VALUE))
                                                    .addComponent(txtValCar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addGap(7, 7, 7)
                                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtValAdm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jLabel29)))
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                        .addComponent(jLabel44)
                                                        .addGap(0, 0, Short.MAX_VALUE))
                                                    .addComponent(txtPesCar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addGap(7, 7, 7)
                                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel30)
                                                    .addComponent(txtValQuim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtValAve, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                                        .addComponent(jLabel45)
                                                        .addGap(0, 0, Short.MAX_VALUE))))))))
                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCodCli, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel36))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                        .addComponent(txtNomCli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                        .addComponent(jLabel48)
                                        .addGap(292, 292, 292)))
                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                        .addComponent(jLabel49)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                                        .addComponent(txtCidCli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSigUfs, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel55))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(txtTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPnlFreteLayout.createSequentialGroup()
                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                .addComponent(txtChvDoe)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(632, 632, 632)))
                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSerieNota, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmpresaNota, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34)
                            .addComponent(txtFilialNota, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPnlFreteLayout.setVerticalGroup(
            jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlFreteLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32))
                .addGap(3, 3, 3)
                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtValSer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValRec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValFPe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValFre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValGri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValCol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValPed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValAdm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValQuim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel37)
                        .addComponent(jLabel38)
                        .addComponent(jLabel39)
                        .addComponent(jLabel40))
                    .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel41)
                        .addComponent(jLabel42))
                    .addComponent(jLabel43)
                    .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel44)
                        .addComponent(jLabel45)))
                .addGap(4, 4, 4)
                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPnlFreteLayout.createSequentialGroup()
                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtValRep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValCst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValBCs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPerIcm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValIcm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValTri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValCar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPesCar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValAve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel5))
                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel36)
                                    .addComponent(jLabel48)
                                    .addComponent(jLabel49)))
                            .addGroup(jPnlFreteLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel55))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNumNfv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNomCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCidCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSigUfs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(jLabel24)
                            .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPnlFreteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtChvDoe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSerieNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmpresaNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilialNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        nTabProcesso.addTab("Frete", jPnlFrete);

        jTableFilial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "#", "Filial", "Nome", "Diretorio", "Cnpj", "Diretorio Principal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableFilial.addMouseListener(this);
        jScrollPane1.setViewportView(jTableFilial);
        if (jTableFilial.getColumnModel().getColumnCount() > 0) {
            jTableFilial.getColumnModel().getColumn(0).setMinWidth(100);
            jTableFilial.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableFilial.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableFilial.getColumnModel().getColumn(1).setMinWidth(100);
            jTableFilial.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableFilial.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableFilial.getColumnModel().getColumn(4).setMinWidth(150);
            jTableFilial.getColumnModel().getColumn(4).setPreferredWidth(150);
            jTableFilial.getColumnModel().getColumn(4).setMaxWidth(150);
        }

        javax.swing.GroupLayout jPnlFilialLayout = new javax.swing.GroupLayout(jPnlFilial);
        jPnlFilial.setLayout(jPnlFilialLayout);
        jPnlFilialLayout.setHorizontalGroup(
            jPnlFilialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlFilialLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1205, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );
        jPnlFilialLayout.setVerticalGroup(
            jPnlFilialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPnlFilialLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        nTabProcesso.addTab("Filial", jPnlFilial);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtUf, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtCte, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(51, 51, 51)
                                        .addComponent(jLabel7)))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(txtCfop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNatOpe, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNumCtee, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(jLabel12)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addComponent(nTabProcesso))
                .addGap(4, 4, 4))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8)
                        .addComponent(jLabel9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(jLabel11)
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNumCtee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCfop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNatOpe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(nTabProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel3, jPanel8, jPanel9});

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jTabbedEdi.addTab("Cte", jPanel6);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("XML"));

        jLabel3.setText("Protocolo");

        txtNumPrt.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNumPrt.setEnabled(false);

        txtChvNot.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel46.setText("Chave Nota");

        txtDirFil.setEnabled(false);
        txtDirFil.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnDirFil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnDirFil.addActionListener(this);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        jButton1.addActionListener(this);

        jLabel1.setText("Diretório");

        btnDirFil1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/table_add.png"))); // NOI18N
        btnDirFil1.addActionListener(this);

        txtAno.setEnabled(false);
        txtAno.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtMes.setEnabled(false);
        txtMes.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtDia.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtDia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "4", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtDirFil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(txtDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(txtMes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(txtAno, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDirFil1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDirFil, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtNumPrt, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel46)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtChvNot))
                .addGap(2, 2, 2))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel46))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDirFil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDirFil1)
                    .addComponent(btnDirFil)
                    .addComponent(jButton1)
                    .addComponent(txtNumPrt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtChvNot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jTabbedEdi))
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedEdi, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        pack();
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == txtEmiCnpj) {
            CteXml.this.txtEmiCnpjActionPerformed(evt);
        }
        else if (evt.getSource() == btnGravar) {
            CteXml.this.btnGravarActionPerformed(evt);
        }
        else if (evt.getSource() == btnGerarOc) {
            CteXml.this.btnGerarOcActionPerformed(evt);
        }
        else if (evt.getSource() == btnLerCte) {
            CteXml.this.btnLerCteActionPerformed(evt);
        }
        else if (evt.getSource() == txtValIcm) {
            CteXml.this.txtValIcmActionPerformed(evt);
        }
        else if (evt.getSource() == txtValTri) {
            CteXml.this.txtValTriActionPerformed(evt);
        }
        else if (evt.getSource() == txtValCar) {
            CteXml.this.txtValCarActionPerformed(evt);
        }
        else if (evt.getSource() == txtPesCar) {
            CteXml.this.txtPesCarActionPerformed(evt);
        }
        else if (evt.getSource() == txtValAve) {
            CteXml.this.txtValAveActionPerformed(evt);
        }
        else if (evt.getSource() == btnDirFil) {
            CteXml.this.btnDirFilActionPerformed(evt);
        }
        else if (evt.getSource() == jButton1) {
            CteXml.this.jButton1ActionPerformed(evt);
        }
        else if (evt.getSource() == btnDirFil1) {
            CteXml.this.btnDirFil1ActionPerformed(evt);
        }
    }

    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == jTableXml) {
            CteXml.this.jTableXmlMouseClicked(evt);
        }
        else if (evt.getSource() == jTableFilial) {
            CteXml.this.jTableFilialMouseClicked(evt);
        }
    }

    public void mouseEntered(java.awt.event.MouseEvent evt) {
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
    }

    public void mousePressed(java.awt.event.MouseEvent evt) {
    }

    public void mouseReleased(java.awt.event.MouseEvent evt) {
    }// </editor-fold>//GEN-END:initComponents

    private void btnDirFilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDirFilActionPerformed
        JFileChooser fc = new JFileChooser();
        int option = fc.showOpenDialog(RdiGrid);
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = fc.getSelectedFile().getName();
            String path = fc.getSelectedFile().getParentFile().getPath();
            txtDirFil.setText(path);
            this.diretorio = path;

        }
    }//GEN-LAST:event_btnDirFilActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            carregarTabelaFiles();

        } catch (ParseException ex) {
            Mensagem.mensagem("ERROR", ex.toString());

        } catch (Exception ex) {
            Mensagem.mensagem("ERROR", ex.toString());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTableXmlMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableXmlMouseClicked
        limparTela(0);
        int linhaSelSit = jTableXml.getSelectedRow();
        int colunaSelSit = jTableXml.getSelectedColumn();
        String nomeXml = jTableXml.getValueAt(linhaSelSit, 1).toString();
        this.dirXml = diretorio;
        this.pathxml = this.dirXml + nomeXml;
        filename = jTableXml.getValueAt(linhaSelSit, 1).toString();
        lblFileSelection.setText(nomeXml);
        if (evt.getClickCount() == 2) {
            btnLerCte.setEnabled(true);
            iniciarBarra();
            if (!txtOrdemCompra.getText().isEmpty()) {
                btnGravar.setEnabled(true);

            }

        }

    }//GEN-LAST:event_jTableXmlMouseClicked

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed

        if (txtCodFor.getText().equals("0")) {
            JOptionPane.showMessageDialog(null, "Não localizado o código do fornecedor",
                    "Aviso", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        } else if (lblFileSelection.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecione um arquivo para processar",
                    "Aviso", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;

        } else if (txtInfo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Informe a descrição da ordemm",
                    "Aviso", javax.swing.JOptionPane.ERROR_MESSAGE);
            txtInfo.requestFocus();
            return;

        } else {

            try {
                btnGravar.setEnabled(false);
                this.cte = buscarCteCadastrada("por_chave", "and usu_chacte='" + txtChvDoe.getText() + "'");

                if (this.cte == null) {
                    this.cte = new Cte();
                    this.cte.setUsu_codlan(0);
                }
                if (this.cte != null) {

                    if (this.cte.getUsu_codlan() == 0) {
                        if (gravarCte()) {

                        }
                    } else {
                        this.utilDatas = new UtilDatas();
                        Mensagem.mensagem("ERROR", "Cte ja foi lançado Ordem Compra " + cte.getUsu_numocp() + " - " + this.utilDatas.converterDateToStr(cte.getUsu_datlan()));
                        if (cte.getUsu_numocp() == 0) {

                            Mensagem.mensagem("ERROR", "Cte sem ordem compra ");

                        }
                    }
                }

            } catch (SQLException ex) {
                Mensagem.mensagem("ERROR", ex.toString());

            } catch (Exception ex) {
                Mensagem.mensagem("ERROR", ex.toString());
            }

        }
        limparTela(0);
    }//GEN-LAST:event_btnGravarActionPerformed

    private void txtValIcmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValIcmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValIcmActionPerformed

    private void txtValTriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValTriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValTriActionPerformed

    private void txtValCarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValCarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValCarActionPerformed

    private void txtPesCarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesCarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesCarActionPerformed

    private void txtValAveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValAveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValAveActionPerformed

    private void btnGerarOcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarOcActionPerformed

        try {
            CteOc sol = new CteOc();
            MDIFrame.add(sol, true);
            //sol.setSize(600, 400);
            sol.setMaximum(true);
            sol.setPosicao();
            sol.setRecebePalavra(this, txtCodFor.getText());
        } catch (Exception ex) {
            Logger.getLogger(CteXml.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnGerarOcActionPerformed

    private void btnLerCteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLerCteActionPerformed
        iniciarBarra();
    }//GEN-LAST:event_btnLerCteActionPerformed

    private void btnDirFil1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDirFil1ActionPerformed
        try {
            montarEstruturaPasta();
        } catch (Exception ex) {
            Logger.getLogger(CteXml.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnDirFil1ActionPerformed


    private void jTableFilialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFilialMouseClicked
        int linhaSelSit = jTableFilial.getSelectedRow();
        int colunaSelSit = jTableFilial.getSelectedColumn();
        txtDirFil.setText(jTableFilial.getValueAt(linhaSelSit, 3).toString());

        diretorioPadrao = jTableFilial.getValueAt(linhaSelSit, 5).toString();
        diretorio = txtDirFil.getText();
        if (evt.getClickCount() == 2) {
            try {
                carregarTabelaFiles();
                nTabProcesso.setSelectedIndex(1);

            } catch (ParseException ex) {
                Mensagem.mensagem("ERROR", ex.toString());

            } catch (Exception ex) {
                Mensagem.mensagem("ERROR", ex.toString());
            }
        }


    }//GEN-LAST:event_jTableFilialMouseClicked

    private void txtEmiCnpjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmiCnpjActionPerformed
        txtDia.requestFocus();
    }//GEN-LAST:event_txtEmiCnpjActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnDirFil;
    private javax.swing.JButton btnDirFil1;
    private javax.swing.JButton btnGerarOc;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnLerCte;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPnlFilial;
    private javax.swing.JPanel jPnlFrete;
    private javax.swing.JPanel jPnlXml;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedEdi;
    private javax.swing.JTable jTableFilial;
    private javax.swing.JTable jTableXml;
    private org.openswing.swing.client.TextControl lblFileSelection;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JTabbedPane nTabProcesso;
    private org.openswing.swing.client.TextControl txtAno;
    private org.openswing.swing.client.TextControl txtCfop;
    private javax.swing.JTextField txtChvDoe;
    private javax.swing.JTextField txtChvNot;
    private org.openswing.swing.client.TextControl txtCidCli;
    private org.openswing.swing.client.TextControl txtCodCli;
    private org.openswing.swing.client.TextControl txtCodFor;
    private org.openswing.swing.client.TextControl txtCodMunEnv;
    private org.openswing.swing.client.TextControl txtCodMunFim;
    private org.openswing.swing.client.TextControl txtCodMunIni;
    private javax.swing.JComboBox<String> txtCodPgt;
    private javax.swing.JComboBox<String> txtCodSer;
    private org.openswing.swing.client.TextControl txtCodTra;
    private org.openswing.swing.client.TextControl txtCte;
    private org.openswing.swing.client.TextControl txtDesCnpj;
    private org.openswing.swing.client.TextControl txtDesIns;
    private org.openswing.swing.client.TextControl txtDesNom;
    private javax.swing.JComboBox<String> txtDia;
    private org.openswing.swing.client.TextControl txtDirFil;
    private org.openswing.swing.client.TextControl txtEmiCnpj;
    private org.openswing.swing.client.TextControl txtEmiIns;
    private org.openswing.swing.client.TextControl txtEmiNom;
    private org.openswing.swing.client.TextControl txtEmissao;
    private org.openswing.swing.client.TextControl txtEmpresaNota;
    private org.openswing.swing.client.TextControl txtEmpresaOc;
    private org.openswing.swing.client.TextControl txtEstMunEnv;
    private org.openswing.swing.client.TextControl txtEstMunFim;
    private org.openswing.swing.client.TextControl txtEstMunIni;
    private org.openswing.swing.client.TextControl txtFilialNota;
    private org.openswing.swing.client.TextControl txtFilialOc;
    private javax.swing.JTextArea txtInfo;
    private org.openswing.swing.client.TextControl txtMes;
    private org.openswing.swing.client.TextControl txtNatOpe;
    private org.openswing.swing.client.TextControl txtNomCli;
    private org.openswing.swing.client.TextControl txtNomMunEvi;
    private org.openswing.swing.client.TextControl txtNomMunFim;
    private org.openswing.swing.client.TextControl txtNomMunIni;
    private org.openswing.swing.client.TextControl txtNumCtee;
    private org.openswing.swing.client.TextControl txtNumNfv;
    private javax.swing.JTextField txtNumPrt;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private org.openswing.swing.client.NumericControl txtPerIcm;
    private org.openswing.swing.client.NumericControl txtPesCar;
    private org.openswing.swing.client.TextControl txtRemCnpj;
    private org.openswing.swing.client.TextControl txtRemIns;
    private org.openswing.swing.client.TextControl txtRemNom;
    private org.openswing.swing.client.TextControl txtSerie;
    private org.openswing.swing.client.TextControl txtSerieNota;
    private org.openswing.swing.client.TextControl txtSigUfs;
    private org.openswing.swing.client.TextControl txtTransacao;
    private org.openswing.swing.client.TextControl txtUf;
    private org.openswing.swing.client.NumericControl txtValAdm;
    private org.openswing.swing.client.NumericControl txtValAve;
    private org.openswing.swing.client.NumericControl txtValBCs;
    private org.openswing.swing.client.NumericControl txtValCar;
    private org.openswing.swing.client.NumericControl txtValCol;
    private org.openswing.swing.client.NumericControl txtValCst;
    private org.openswing.swing.client.NumericControl txtValFPe;
    private org.openswing.swing.client.NumericControl txtValFre;
    private org.openswing.swing.client.NumericControl txtValGri;
    private org.openswing.swing.client.NumericControl txtValIcm;
    private org.openswing.swing.client.NumericControl txtValPed;
    private org.openswing.swing.client.NumericControl txtValQuim;
    private org.openswing.swing.client.NumericControl txtValRec;
    private org.openswing.swing.client.NumericControl txtValRec1;
    private org.openswing.swing.client.NumericControl txtValRep;
    private org.openswing.swing.client.NumericControl txtValSer;
    private org.openswing.swing.client.NumericControl txtValTri;
    // End of variables declaration//GEN-END:variables
}
