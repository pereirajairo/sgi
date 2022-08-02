/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.ws.WSEnviarEmail;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.ClienteGrupo;
import br.com.sgi.bean.ComissaoTitulos;
import br.com.sgi.bean.ComissaoTitulosMovimentos;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.OrdemCompraComissao;
import br.com.sgi.bean.Representante;
import br.com.sgi.dao.ClienteDAO;
import br.com.sgi.dao.ClienteGrupoDAO;
import br.com.sgi.dao.ComissaoTitulosDAO;
import br.com.sgi.dao.ComissaoTitulosMovimentosDAO;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.OrdemCompraComissaoDAO;
import br.com.sgi.dao.RepresentanteDAO;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSRelatorio;
import br.com.sgi.ws.WsOrdemDeCompra;

import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.xml.soap.SOAPException;

import org.openswing.swing.mdi.client.InternalFrame;

/**
 *
 * @author jairosilva
 */
public final class Comissao extends InternalFrame {

    private Cliente cliente;
    private ClienteDAO clienteDAO;
    private List<Cliente> lstCliente = new ArrayList<Cliente>();

    private ComissaoTitulos titulos;
    private ComissaoTitulosDAO comissaoTitulosDAO;
    private List<ComissaoTitulos> lstTitulos = new ArrayList<ComissaoTitulos>();

    private ComissaoTitulosMovimentos titulosMovimentos;
    private ComissaoTitulosMovimentosDAO comissaoTitulosMovimentosDAO;
    private OrdemCompraComissao ordemCompraComissao;
    private OrdemCompraComissaoDAO ordemCompraComissaoDAO;
    private List<ComissaoTitulosMovimentos> lstTitulosMov = new ArrayList<ComissaoTitulosMovimentos>();
    private List<ComissaoTitulosMovimentos> lstTitulosEmp = new ArrayList<ComissaoTitulosMovimentos>();
    private List<ComissaoTitulosMovimentos> lstTitulosOcs = new ArrayList<ComissaoTitulosMovimentos>();
    private List<ComissaoTitulosMovimentos> lstTitulosOcsItens = new ArrayList<ComissaoTitulosMovimentos>();
    private List<ComissaoTitulosMovimentos> lstTitulosTipo = new ArrayList<ComissaoTitulosMovimentos>();
    private List<ComissaoTitulosMovimentos> lstTitulosPerc = new ArrayList<ComissaoTitulosMovimentos>();
    private List<OrdemCompraComissao> lstOrdensCompra = new ArrayList<OrdemCompraComissao>();
    private RepresentanteDAO representanteDAO;
    private Representante representante;

    private UtilDatas utilDatas;
    private String pagIni;
    private String pagFim;
    private String dataInicial;
    private String dataFinal;
    private String dataInicialVen;
    private String dataFinalVen;
    String repAnt = null;
    String abrir = "N";
    String baseDiferente = "N";
    String arquivo = "";

    public Comissao() {
        try {
            initComponents();

            this.setSize(800, 500);

            btnImprimir.setText("Validar");

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }

            if (clienteDAO == null) {
                this.clienteDAO = new ClienteDAO();
            }

            if (comissaoTitulosDAO == null) {
                this.comissaoTitulosDAO = new ComissaoTitulosDAO();
            }

            if (comissaoTitulosMovimentosDAO == null) {
                this.comissaoTitulosMovimentosDAO = new ComissaoTitulosMovimentosDAO();
            }

            if (ordemCompraComissaoDAO == null) {
                this.ordemCompraComissaoDAO = new OrdemCompraComissaoDAO();
            }

            if (representanteDAO == null) {
                this.representanteDAO = new RepresentanteDAO();
            }

            if (representante == null) {
                this.representante = new Representante();
            }

            jDChooserDataBase.setCalendar(Calendar.getInstance());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void MontarData(Date data) {
        dataInicial = this.utilDatas.dataInicioMes(data);
        dataFinal = this.utilDatas.dataFimMes(data);
        dataInicialVen = this.utilDatas.dataInicioMesVen(data);
        dataFinalVen = this.utilDatas.dataFimMesVen(data);
    }

    private void getClientes(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (!PESQUISA.isEmpty()) {
            getSelecaoPlanta();
            lstCliente = this.clienteDAO.getClientes(this.PESQUISAR_POR, PESQUISA);
            if (lstCliente != null) {
                carregarTabela();
            }
        }
    }

    private void getTitulos(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (!PESQUISA.isEmpty()) {
            //getSelecaoPlanta();
            lstTitulos = this.comissaoTitulosDAO.getTitulos(PESQUISA_POR, PESQUISA);
            if (lstTitulos != null) {
                carregarTabela();
            }
            lstTitulosMov = this.comissaoTitulosMovimentosDAO.getTitulos(PESQUISA_POR, PESQUISA);
            if (lstTitulosMov != null) {
                carregarMovimentos();
            }
            lstTitulosEmp = this.comissaoTitulosMovimentosDAO.getTitulosEmpresa(PESQUISA_POR, PESQUISA);
            if (lstTitulosEmp != null) {
                carregarTotaisEmpresa();
            }
            lstTitulosTipo = this.comissaoTitulosMovimentosDAO.getTitulosTipo(PESQUISA_POR, PESQUISA);
            if (lstTitulosTipo != null) {
                carregarTotaisTipo();
            }
            lstTitulosPerc = this.comissaoTitulosMovimentosDAO.getTitulosPercentual(PESQUISA_POR, PESQUISA);
            if (lstTitulosPerc != null) {
                carregarTotaisPerc();
            }
            lstTitulosOcs = this.comissaoTitulosMovimentosDAO.getTitulosEmpresaOC(dataInicial, PESQUISA);
            if (lstTitulosOcs != null) {
                carregarTotaisEmpresaOC();
            }
            lstOrdensCompra = this.ordemCompraComissaoDAO.getOrdemComprasComissao(PESQUISA_POR, pesquisarOC());
            if (lstOrdensCompra != null) {
                carregarOrdensGeradas();
            }
        }
    }

    private void getItensOcs(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (!PESQUISA.isEmpty()) {
            lstTitulosOcsItens = this.comissaoTitulosMovimentosDAO.getTitulosEmpresaOcItens(PESQUISA_POR, PESQUISA);
            if (lstTitulosOcsItens != null) {
                carregarTotaisEmpresaOcItens();
            }
        }
    }

////    
//    lstOrdensCompra = this.ordemCompraComissaoDAO.getOrdemComprasComissao(PESQUISA_POR, pesquisarOC());
    private void buscarGrupo() throws SQLException {
        ClienteGrupo grupo = new ClienteGrupo();
        ClienteGrupoDAO dao = new ClienteGrupoDAO();

        if ((!txtCodGrupo.getText().trim().isEmpty()) && (dao != null)) {
            grupo = dao.getGrupo("", "and E069GRE.codgre = " + txtCodGrupo.getText());
            txtGrupo.setText(grupo.getNome());
        }
        if ((!txtGrupo.getText().trim().isEmpty()) && (dao != null)) {
            grupo = dao.getGrupo("", "and UPPER(E069GRE.nomgre) like UPPER('%" + txtGrupo.getText() + "%')");
            txtCodGrupo.setText(grupo.getCodgrp());
            txtGrupo.setText(grupo.getNome());
        }

    }

    public void limparTabela() throws Exception {
        DefaultTableModel modeloCarga1 = (DefaultTableModel) jTableTitulos.getModel();
        modeloCarga1.setNumRows(0);
        DefaultTableModel modeloCarga2 = (DefaultTableModel) jTableTitulosMov.getModel();
        modeloCarga2.setNumRows(0);
        DefaultTableModel modeloCarga3 = (DefaultTableModel) jTableTitulosMovEmpresa.getModel();
        modeloCarga3.setNumRows(0);
        DefaultTableModel modeloCarga4 = (DefaultTableModel) jTableTitulosMovPerc.getModel();
        modeloCarga4.setNumRows(0);
        DefaultTableModel modeloCarga5 = (DefaultTableModel) jTableTitulosMovTipo.getModel();
        modeloCarga5.setNumRows(0);
        DefaultTableModel modeloCarga6 = (DefaultTableModel) jTableTitulosMovEmpresaOC.getModel();
        modeloCarga6.setNumRows(0);
        DefaultTableModel modeloCarga7 = (DefaultTableModel) jTableTitulosMovEmpresaOcItens.getModel();
        modeloCarga7.setNumRows(0);
        DefaultTableModel modeloCarga8 = (DefaultTableModel) jTableOrdensGeradas.getModel();
        modeloCarga8.setNumRows(0);
    }

    public void validarRegistrosGrid() throws Exception {
        try {
            int i = 0;
            btnImprimir.setText("Imprimir");
            btnReabilitar.setEnabled(true);
            ImageIcon ImpIcon = getImage("/images/printer.png");
            btnImprimir.setIcon(ImpIcon);
            if (jTableTitulosMov.getRowCount() > 0) {
                titulosMovimentos = new ComissaoTitulosMovimentos();
                while (i < jTableTitulosMov.getRowCount()) {
                    titulosMovimentos.setComissaoValidada("S");
                    titulosMovimentos.setEmpresa(1);
                    titulosMovimentos.setFilial((Integer) jTableTitulosMov.getValueAt(i, 0));
                    titulosMovimentos.setNumeroTitulo(jTableTitulosMov.getValueAt(i, 2).toString());
                    titulosMovimentos.setTipoTitulo(jTableTitulosMov.getValueAt(i, 1).toString());
                    titulosMovimentos.setSequencia((Integer) jTableTitulosMov.getValueAt(i, 3));
                    i++;
                    if (!comissaoTitulosMovimentosDAO.alterar(titulosMovimentos)) {

                    } else {

                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void reabilitarRegistrosGrid() throws Exception {
        try {
            int i = 0;
            btnImprimir.setText("Validar");
            btnReabilitar.setEnabled(false);
            ImageIcon VadIcon = getImage("/images/accept.png");
            btnImprimir.setIcon(VadIcon);
            if (jTableTitulosMov.getRowCount() > 0) {
                titulosMovimentos = new ComissaoTitulosMovimentos();
                while (i < jTableTitulosMov.getRowCount()) {
                    titulosMovimentos.setComissaoValidada("N");
                    titulosMovimentos.setEmpresa(1);
                    titulosMovimentos.setFilial((Integer) jTableTitulosMov.getValueAt(i, 0));
                    titulosMovimentos.setNumeroTitulo(jTableTitulosMov.getValueAt(i, 2).toString());
                    titulosMovimentos.setTipoTitulo(jTableTitulosMov.getValueAt(i, 1).toString());
                    titulosMovimentos.setSequencia((Integer) jTableTitulosMov.getValueAt(i, 3));
                    i++;
                    if (!comissaoTitulosMovimentosDAO.alterar(titulosMovimentos)) {

                    } else {

                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void carregarTabela() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableTitulos.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        Double valorPago = 0.0;

        int aberto = 0;
        int liquidado = 0;
        int parcial = 0;
        ImageIcon CreIcon = getImage("/images/sitBom.png");
        ImageIcon ParIcon = getImage("/images/sitMedio.png");
        ImageIcon InaIcon = getImage("/images/sitRuim.png");

        FormatarNumeros format = new FormatarNumeros();

        for (ComissaoTitulos tit : lstTitulos) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTableTitulos.getColumnModel();
            Comissao.JTableRenderer renderers = new Comissao.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

//            linha[0] = CreIcon;
//            if (((0 == tit.getValorAberto()) && (tit.getSituacao().equals("LS"))) || tit.getSituacao().equals("LQ")) {
//                liquidado++;
//                linha[0] = InaIcon;
//            } else if (tit.getSituacao().equals("LS") && (tit.getValorAberto() > 0)) {
//                parcial++;
//                linha[0] = ParIcon;
//            } else {
//                aberto++;
//                linha[0] = CreIcon;
//            }
            linha[1] = tit.getSituacao();
            linha[2] = tit.getFilial();
            linha[3] = tit.getCliente();
            linha[4] = tit.getCadCliente().getNome();
            linha[5] = tit.getRepresentante();
            linha[6] = tit.getCadRepresentante().getNome();
            linha[7] = tit.getNumeroTitulo();
            linha[8] = tit.getTipoTitulo();
            linha[9] = tit.getNumeroNota();
            linha[10] = tit.getSerieNota();
            linha[11] = tit.getPedido();
            linha[12] = format.converterToString(tit.getValorOriginal());
            linha[13] = format.converterToString(tit.getBaseComissao());
            linha[14] = format.converterToString(tit.getPercentualComissao()) + " %";
            linha[15] = format.converterToString(tit.getValorComissao());
            linha[17] = tit.getCadClienteGrupo().getNome();

//            valorPago += tit.getValorOriginal();
            modeloCarga.addRow(linha);
        }

        barra.setString("Registro encontrados " + lstTitulos.size());
        if (jTableTitulos.getRowCount() > 0) {
            lblqtdTitulos.setText(String.valueOf(jTableTitulos.getRowCount()));
//            lblvalorPago.setText(format.converterToString(valorPago));
        }
    }

    public void carregarMovimentos() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableTitulosMov.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        int imp = 0;
        Double valorBase = 0.0;
        Double valorCom = 0.0;
        Double valorPago = 0.0;
        FormatarNumeros format = new FormatarNumeros();

        for (ComissaoTitulosMovimentos mov : lstTitulosMov) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableTitulosMov.getColumnModel();
            Comissao.JTableRenderer renderers = new Comissao.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = mov.getFilial();
            linha[1] = mov.getTipoTitulo();
            linha[2] = mov.getNumeroTitulo();
            linha[3] = mov.getSequencia();
            linha[4] = mov.getTransacaoMov();
            linha[5] = mov.getDataEmissaoS();
            linha[6] = mov.getVencimentoAtualS();
            linha[7] = format.converterToString(mov.getValorAberto());
            linha[8] = mov.getDataPagamentoS();
            linha[9] = mov.getFormaPag();
            linha[10] = format.converterToString(mov.getValorMov());
            linha[12] = format.converterToString(mov.getValorOutros());
            linha[13] = format.converterToString(mov.getJuros());
            linha[14] = format.converterToString(mov.getEncargos());
            linha[16] = format.converterToString(mov.getValorLiquido());
            linha[15] = format.converterToString(mov.getValorBase());
            linha[16] = format.converterToString(mov.getPercentualComissao()) + " %";
            linha[17] = format.converterToString(mov.getValorComissao());
            linha[18] = format.converterToString(mov.getValorDesc());
            linha[19] = format.converterToString(mov.getMultas());
            linha[20] = mov.getNotaFiscal();
            linha[21] = mov.getPedido();
            linha[22] = mov.getDiferencaBase();
            linha[23] = mov.getLinha();
            jTableTitulosMov.getColumnModel().getColumn(20).setCellRenderer(new RegistroZeradoTableRenderer());
            jTableTitulosMov.getColumnModel().getColumn(21).setCellRenderer(new RegistroZeradoTableRenderer());
            jTableTitulosMov.getColumnModel().getColumn(22).setCellRenderer(new DiferencaTableRenderer());
            valorBase += mov.getValorBase();
            valorCom += mov.getValorComissao();
            valorPago += mov.getValorMov();

            if ((mov.getComissaoValidada() == null) || (mov.getComissaoValidada().equals("N"))) {
                btnImprimir.setText("Validar");
                btnReabilitar.setEnabled(false);
                ImageIcon VadIcon = getImage("/images/accept.png");
                btnImprimir.setIcon(VadIcon);
                imp++;
            }
            modeloCarga.addRow(linha);
        }

        if (imp == 0) {
            btnImprimir.setText("Imprimir");
            btnReabilitar.setEnabled(true);
            ImageIcon ImpIcon = getImage("/images/printer.png");
            btnImprimir.setIcon(ImpIcon);
        }

        barra.setString("Registro encontrados " + lstTitulosMov.size());

        if (jTableTitulosMov.getRowCount() > 0) {
            lblqtdTitulosMov.setText(String.valueOf(jTableTitulosMov.getRowCount()));
            lblvalorBase.setText(format.converterToString(valorBase));
            lblcomissao.setText(format.converterToString(valorCom));
            lblvalorPago.setText(format.converterToString(valorPago));
        }

    }

    public final void gerarRelatatorio() throws SQLException, Exception {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            try {
                String Data = "";
//                String arquivo = ""; //txtRep.getText();
                String relatorio = "FPMI200";
                if (chkVendedor.isSelected()) {
                    arquivo = dataFinalVen;
                    arquivo = dataFinalVen.substring(3, 5);
                    arquivo = txtRep.getText() + "_" + arquivo + dataFinalVen.substring(6, 10);
                    MontarData(jDChooserDataBase.getDate());
                    Data = dataInicialVen + "-" + dataFinalVen;
                }
                if (chkRepresentante.isSelected()) {
                    arquivo = dataInicial;
                    arquivo = dataInicial.substring(3, 5);
                    arquivo = txtRep.getText() + "_" + arquivo + dataInicial.substring(6, 10);
                    MontarData(jDChooserDataBase.getDate());
                    Data = dataInicial + "-" + dataFinal;
                }
                String entrada = "<EAbrDatBas=" + Data + "><EAbrCodRep=" + txtRep.getText() + "><ERes12a=N>";
                String diretorio = "\\\\SRV-SPNS01\\Senior_ERP\\Sapiens\\Relatorios\\Comissao\\";
                String formato = "tsfNormal";
                WSRelatorio.chamarMetodoWsXmlHttpSapiens(arquivo, relatorio, entrada, diretorio, formato);

            } catch (Exception ex) {
                abrir = "N";
                Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado");
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, e, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
        }
//        return abrir;
    }

    public final void salvarRelatatorio() throws SQLException, Exception {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            try {
                String Data = "";
                String relatorio = "FPMI200";
                if (chkVendedor.isSelected()) {
                    arquivo = dataFinalVen;
                    arquivo = dataFinalVen.substring(3, 5);
                    arquivo = txtRep.getText() + "_" + arquivo + dataFinalVen.substring(6, 10);
                    MontarData(jDChooserDataBase.getDate());
                    Data = dataInicialVen + "-" + dataFinalVen;
                }
                if (chkRepresentante.isSelected()) {
                    arquivo = dataInicial;
                    arquivo = dataInicial.substring(3, 5);
                    arquivo = txtRep.getText() + "_" + arquivo + dataInicial.substring(6, 10);
                    MontarData(jDChooserDataBase.getDate());
                    Data = dataInicial + "-" + dataFinal;
                }
                String entrada = "<EAbrDatBas=" + Data + "><EAbrCodRep=" + txtRep.getText() + "><ERes12a=N>";
                String diretorio = "\\\\SRV-SPNS01\\Senior_ERP\\Sapiens\\Relatorios\\Comissao\\";
                String formato = "tsfPDF";
                WSRelatorio.chamarMetodoWsXmlHttpSapiens(arquivo, relatorio, entrada, diretorio, formato);

            } catch (Exception ex) {
                abrir = "N";
                Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado");
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, e, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public final void abrirRelatatorio() throws SQLException, Exception {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        try {
//            String arquivo = txtRep.getText();
            String diretorio = "\\\\SRV-SPNS01\\Senior_ERP\\Sapiens\\Relatorios\\Comissao\\";
            desktop.open(new File(diretorio + arquivo + ".IMP"));
        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado");
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, e, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void carregarTotaisEmpresa() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableTitulosMovEmpresa.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        int qtd = 0;
        Double valorLiq = 0.0;
        Double valorBase = 0.0;
        Double valorCom = 0.0;

        FormatarNumeros format = new FormatarNumeros();
        Object[] linha = new Object[6];
        for (ComissaoTitulosMovimentos mov : lstTitulosEmp) {
            TableColumnModel columnModel = jTableTitulosMovEmpresa.getColumnModel();
            Comissao.JTableRenderer renderers = new Comissao.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            if (mov.getEmpresaComissao() == 1) {
                linha[0] = "17.645.625/0001-03";
            }
            if (mov.getEmpresaComissao() == 2) {
                linha[0] = "07.564.769/0001-81";
            }
            if (mov.getEmpresaComissao() == 3) {
                linha[0] = "05.730.180/0001-80";
            }
            if (mov.getEmpresaComissao() == 99) {
                linha[0] = "GERENCIAL";
            }

            linha[1] = mov.getTipoTitulo();
            linha[2] = mov.getQuantidade();
            qtd += mov.getQuantidade();
            linha[3] = format.converterToString(mov.getValorLiquido());
            valorLiq += mov.getValorLiquido();
            linha[4] = format.converterToString(mov.getValorBase());
            valorBase += mov.getValorBase();
            linha[5] = format.converterToString(mov.getValorComissao());
            valorCom += mov.getValorComissao();

            modeloCarga.addRow(linha);
        }
        linha[0] = "TOTAL";
        linha[1] = " ";
        linha[2] = qtd;
        linha[3] = format.converterToString(valorLiq);
        linha[4] = format.converterToString(valorBase);
        linha[5] = format.converterToString(valorCom);
        modeloCarga.addRow(linha);
    }

    public void carregarOrdensGeradas() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableOrdensGeradas.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        Double valorCom = 0.0;
        FormatarNumeros format = new FormatarNumeros();
        Object[] linha = new Object[8];
        for (OrdemCompraComissao mov : lstOrdensCompra) {
            TableColumnModel columnModel = jTableOrdensGeradas.getColumnModel();
            Comissao.JTableRenderer renderers = new Comissao.JTableRenderer();
//            columnModel.getColumn(0).setCellRenderer(renderers);

            if (mov.getEmpresa() == 1) {
                linha[0] = "17.645.625/0001-03";
            }
            if (mov.getEmpresa() == 2) {
                linha[0] = "07.564.769/0001-81";
            }
            if (mov.getEmpresa() == 3) {
                linha[0] = "05.730.180/0001-80";
            }
            if (mov.getEmpresa() == 99) {
                linha[0] = "GERENCIAL";
            }

            linha[1] = mov.getNumeroOrdemCompra();
            linha[2] = mov.getRepresentante();
            linha[3] = mov.getCodigoFornecedor();
            if (mov.getEmpresa() != 99) {
                linha[4] = format.converterToString(mov.getValorIrf());
            }
            if (mov.getEmpresa() == 99) {
                linha[4] = 0;
            }
            linha[5] = format.converterToString(mov.getValorLiquido());
            linha[6] = mov.getEmailRep();

            modeloCarga.addRow(linha);
        }
    }

    public void carregarTotaisEmpresaOC() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableTitulosMovEmpresaOC.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        Double valorCom = 0.0;
        Double valorTot = 0.0;
//        Boolean chk = false;
        FormatarNumeros format = new FormatarNumeros();
        Object[] linha = new Object[14];
        for (ComissaoTitulosMovimentos mov : lstTitulosOcs) {
            TableColumnModel columnModel = jTableTitulosMovEmpresaOC.getColumnModel();
            Comissao.JTableRenderer renderers = new Comissao.JTableRenderer();
//            columnModel.getColumn(0).setCellRenderer(renderers);

            if (mov.getEmpresaComissao() == 1) {
                linha[1] = "17.645.625/0001-03";
            }
            if (mov.getEmpresaComissao() == 2) {
                linha[1] = "07.564.769/0001-81";
            }
            if (mov.getEmpresaComissao() == 3) {
                linha[1] = "05.730.180/0001-80";
            }
            if (mov.getEmpresaComissao() == 99) {
                linha[1] = "GERENCIAL";
            }

//            linha[0] = false;
            linha[2] = "Gerar OC";
            linha[3] = mov.getFornecedor();
            linha[4] = mov.getCondPagOc();
            linha[5] = mov.getDescCondPagOc();
            linha[6] = mov.getTipoTitulo();
            linha[7] = format.converterToString(mov.getValorComissao());
            valorCom += mov.getValorComissao();
            if (mov.getEmpresaComissao() != 99) {
                linha[8] = format.converterToString(mov.getValorIrf());
            }
            if (mov.getEmpresaComissao() == 99) {
                linha[8] = 0.00;
            }
            if (mov.getEmpresaComissao() != 99) {
                linha[9] = format.converterToString(mov.getValorLiq());
            }
            if (mov.getEmpresaComissao() == 99) {
                valorTot = mov.getValorLiq() + mov.getValorIrf();
                linha[9] = format.converterToString(valorTot);
            }
            linha[10] = mov.getRegimeTrib();
            linha[11] = mov.getEmailRep();
            linha[12] = mov.getRepOc();
            linha[13] = mov.getIdeExt();

            modeloCarga.addRow(linha);
        }
        if (modeloCarga.getRowCount() != 0) {
            linha[1] = "TOTAL";
            linha[2] = " ";
            linha[3] = " ";
            linha[4] = " ";
            linha[5] = " ";
            linha[6] = " ";
            linha[7] = format.converterToString(valorCom);
            linha[8] = " ";
            linha[9] = " ";
            linha[10] = " ";
            linha[11] = " ";
            linha[12] = " ";
            linha[13] = " ";
            modeloCarga.addRow(linha);
        }

    }

    public void carregarTotaisEmpresaOcItens() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableTitulosMovEmpresaOcItens.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        int i = 0;
        Double valorCom = 0.0;
        FormatarNumeros format = new FormatarNumeros();
        Object[] linha = new Object[8];
        for (ComissaoTitulosMovimentos mov : lstTitulosOcsItens) {
            TableColumnModel columnModel = jTableTitulosMovEmpresaOcItens.getColumnModel();
            Comissao.JTableRenderer renderers = new Comissao.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            i++;

            if (mov.getEmpresaComissao() == 1) {
                linha[0] = "17.645.625/0001-03";
            }
            if (mov.getEmpresaComissao() == 2) {
                linha[0] = "07.564.769/0001-81";
            }
            if (mov.getEmpresaComissao() == 3) {
                linha[0] = "05.730.180/0001-80";
            }
            if (mov.getEmpresaComissao() == 99) {
                linha[0] = "GERENCIAL";
            }
            //linha[0] = mov.getEmpresaComissao();             
            linha[1] = "OC";
            linha[2] = i;
            linha[3] = mov.getServico();
            linha[4] = mov.getComplementoServ();
            linha[5] = mov.getLinha();
            linha[6] = mov.getTipoTitulo();
            linha[7] = format.converterToString(mov.getValorComissaoItem());
            valorCom += mov.getValorComissaoItem();

            modeloCarga.addRow(linha);
        }

        linha[0] = "TOTAL";
        linha[1] = " ";
        linha[2] = " ";
        linha[3] = " ";
        linha[4] = " ";
        linha[5] = " ";
        linha[6] = " ";
        linha[7] = valorCom;
        modeloCarga.addRow(linha);
    }

    public void removerTotaisEmpresaOcItens(String empresaSelItens) throws Exception {
        int i = 0;

        while (i < lstTitulosOcsItens.size()) {
            if (jTableTitulosMovEmpresaOcItens.getValueAt(i, 0).toString() == empresaSelItens);
            jTableTitulosMovEmpresaOcItens.removeRowSelectionInterval(i, 0);
            lstTitulosOcsItens.remove(i);
            i++;
        }

//        if (mov.getEmpresaComissao() == 1) {
//            linha[0] = "17.645.625/0001-03";
//        }
//        if (mov.getEmpresaComissao() == 2) {
//            linha[0] = "07.564.769/0001-81";
//        }
//        if (mov.getEmpresaComissao() == 3) {
//            linha[0] = "05.730.180/0001-80";
//        }
//        if (mov.getEmpresaComissao() == 99) {
//            linha[0] = "GERENCIAL";
//        }
//
//        modeloCarga.addRow(linha);
    }

    public void carregarTotaisTipo() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableTitulosMovTipo.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        int qtd = 0;
        int qtdsub = 0;
        Double valorLiq = 0.0;
        Double valorBase = 0.0;
        Double valorCom = 0.0;
        Double valorSubLiq = 0.0;
        Double valorSubBase = 0.0;
        Double valorSubCom = 0.0;
        FormatarNumeros format = new FormatarNumeros();
        Object[] linha = new Object[6];
        for (ComissaoTitulosMovimentos mov : lstTitulosTipo) {
            TableColumnModel columnModel = jTableTitulosMovTipo.getColumnModel();
            Comissao.JTableRenderer renderers = new Comissao.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = mov.getTipoTitulo();
            linha[1] = mov.getLinha();
            linha[2] = mov.getQuantidade();
            qtd += mov.getQuantidade();
            linha[3] = format.converterToString(mov.getValorLiquido());
            valorLiq += mov.getValorLiquido();
            linha[4] = format.converterToString(mov.getValorBase());
            valorBase += mov.getValorBase();
            linha[5] = format.converterToString(mov.getValorComissao());
            valorCom += mov.getValorComissao();

            modeloCarga.addRow(linha);

            if ((mov.getTipoTitulo().equals("CHP")) || (mov.getTipoTitulo().equals("CHQ")) || (mov.getTipoTitulo().equals("DPG"))) {
                qtdsub += mov.getQuantidade();
                valorSubLiq += mov.getValorLiquido();
                valorSubBase += mov.getValorBase();
                valorSubCom += mov.getValorComissao();

            }
        }
        /*
        linha[0] = "SUBTOTAL";
        linha[1] = "CHP-CHQ-DPG";
        linha[2] = qtdsub;
        linha[3] = format.converterToString(valorSubLiq);
        linha[4] = format.converterToString(valorSubBase);
        linha[5] = format.converterToString(valorSubCom);
        modeloCarga.addRow(linha);
         */
        linha[0] = "TOTAL";
        linha[1] = "-";
        linha[2] = qtd;
        linha[3] = format.converterToString(valorLiq);
        linha[4] = format.converterToString(valorBase);
        linha[5] = format.converterToString(valorCom);
        modeloCarga.addRow(linha);

    }

    public void carregarTotaisPerc() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableTitulosMovPerc.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        int qtd = 0;
        Double valorLiq = 0.0;
        Double valorBase = 0.0;
        Double valorCom = 0.0;
        FormatarNumeros format = new FormatarNumeros();
        Object[] linha = new Object[6];
        for (ComissaoTitulosMovimentos mov : lstTitulosPerc) {
            TableColumnModel columnModel = jTableTitulosMovPerc.getColumnModel();
            Comissao.JTableRenderer renderers = new Comissao.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = mov.getLinha();
            linha[1] = mov.getQuantidade();
            qtd += mov.getQuantidade();
            linha[2] = format.converterToString(mov.getPercentualComissao());
            linha[3] = format.converterToString(mov.getValorLiquido());
            valorLiq += mov.getValorLiquido();
            linha[4] = format.converterToString(mov.getValorBase());
            valorBase += mov.getValorBase();
            linha[5] = format.converterToString(mov.getValorComissao());
            valorCom += mov.getValorComissao();

            modeloCarga.addRow(linha);
        }

        linha[0] = "TOTAL";
        linha[1] = qtd;
        linha[2] = "-";
        linha[3] = format.converterToString(valorLiq);
        linha[4] = format.converterToString(valorBase);
        linha[5] = format.converterToString(valorCom);
        modeloCarga.addRow(linha);

    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableTitulos.setRowHeight(20);
        jTableTitulosMov.setRowHeight(20);
        jTableTitulosMovTipo.setRowHeight(20);
        jTableTitulosMovPerc.setRowHeight(20);
        jTableTitulosMovEmpresa.setRowHeight(20);
        jTableTitulosMovEmpresaOC.setRowHeight(20);
        jTableTitulosMovEmpresaOcItens.setRowHeight(20);
        jTableTitulos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableTitulos.setIntercellSpacing(new Dimension(1, 2));
        jTableTitulos.setAutoCreateRowSorter(true);
        jTableTitulosMov.setAutoCreateRowSorter(true);
        jTableTitulosMovTipo.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableTitulosMovTipo.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableTitulosMovTipo.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableTitulosMovPerc.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableTitulosMovPerc.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableTitulosMovPerc.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableTitulosMovEmpresa.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableTitulosMovEmpresa.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableTitulosMovEmpresa.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableTitulosMovEmpresaOC.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableTitulosMovEmpresaOC.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableTitulosMovEmpresaOC.getColumnModel().getColumn(4).setCellRenderer(direita);
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

    public String iniciarBarraRelatorio() throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Carregando Relatório");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                for (int count = 1; count <= 101; count++) {
                    try {
                        sleep(100);
                        barra.setValue(count);
                        if (barra.getValue() <= 99) {
                            barra.setString("Carregando ..." + String.valueOf(count));
                        } else {
                            barra.setString(" ");
                        }
                    } catch (InterruptedException ex) {

                    }
                }
                return abrir = "S";
            }

            @Override

            protected void done() {
                barra.setIndeterminate(false);
            }
        };
        worker.execute();
        return null;
    }

    public void iniciarBarra(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Filtrando registros");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getTitulos(PESQUISA_POR, PESQUISA);
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
    private String PESQUISAR_CD;

    private void getSelecaoPlanta() {
        if (chkFab.isSelected()) {
            PESQUISAR_POR = " not in (10, 11, 12, 13, 14, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99)";
        } else {
            if (chkCd.isSelected()) {
                PESQUISAR_POR = "  in (10, 11, 12, 13, 14, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99)";
                String filial = txtFilial.getSelectedItem().toString();
                int index = filial.indexOf("-");
                String filialSelecionada = filial.substring(0, index).trim();
                if (!filialSelecionada.equals("0")) {
                    PESQUISAR_POR = " in (" + filialSelecionada + ")";
                }
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

    public void preencherComboRepresentante(Integer id) throws SQLException, Exception {
        RepresentanteDAO repDAO = new RepresentanteDAO();
        List<Representante> lstRep = new ArrayList<Representante>();
        String desger;
        txtRepresentante.removeAllItems();
        txtRepresentante.addItem("SELECIONE");

        lstRep = repDAO.getRepresentantes("REP", " and USU_CatRep='REP' and SitRep='A' and USU_CalCom='S'");
        if (lstRep != null) {
            for (Representante rep : lstRep) {
                desger = rep.getNome();
                txtRepresentante.addItem(desger);
            }
        }
    }

    public void preencherComboVendedor(Integer id) throws SQLException, Exception {
        RepresentanteDAO repDAO = new RepresentanteDAO();
        List<Representante> lstRep = new ArrayList<Representante>();
        String desger;
        txtRepresentante.removeAllItems();
        txtRepresentante.addItem("SELECIONE");
        lstRep = repDAO.getRepresentantes("REP", " and USU_CatRep='VEN' and SitRep='A' and USU_CalCom='S'");
        if (lstRep != null) {
            for (Representante rep : lstRep) {
                desger = rep.getNome();
                txtRepresentante.addItem(desger);
            }
        }
    }

    private void Limpar() {
        try {
            buttonGroup1.clearSelection();
            buttonGroup2.clearSelection();
            buttonGroup3.clearSelection();
            buttonGroup4.clearSelection();
            txtperc.setText("");
            txtGrupo.setText("");
            txtCodGrupo.setText("");
            txtRep.setText("");
            txtCliente.setText("");
            txtRepresentante.removeAllItems();
            txtNome.setText("");
            txtTitulo.setText("");
            txtTipoTitulo.setText("");
            txtPedido.setText("");
            txtNota.setText("");
            txtSerie.setText("");
            dcPagIni.setDate(null);
            dcPagFim.setDate(null);
            lblqtdTitulos.setText("");
            lblvalorPago.setText("");
            lblvalorBase.setText("");
            lblcomissao.setText("");
            limparTabela();

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void LimparPesquisa() {
        try {
            txtRep.setText("");
            txtCliente.setText("");
            txtNome.setText("");
            txtTitulo.setText("");
            txtTipoTitulo.setText("");
            txtPedido.setText("");
            txtNota.setText("");
            txtSerie.setText("");
            dcPagIni.setDate(null);
            dcPagFim.setDate(null);
            limparTabela();

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String buscarRepVen(String busca) throws ParseException, SQLException {
        String desger;

        representante = representanteDAO.getRepresentante("REP", busca);
        desger = representante.getNome();
        txtRepresentante.setSelectedItem(desger);
        return desger;
    }

    private void pegarDataDigitada() throws ParseException {
        if (dcPagIni.getDate() != null) {
            pagIni = this.utilDatas.converterDateToStr(dcPagIni.getDate());
        }

        if (dcPagFim.getDate() != null) {
            pagFim = this.utilDatas.converterDateToStr(dcPagFim.getDate());
        }
    }

    private String pesquisarOC() throws ParseException, SQLException {
        String sqlOC = "";

        if (jDChooserDataBase.getDate() == null) {
            Mensagem.mensagem("OK", "É Obrigatório preenchimento da Compentência");
        } else {
            sqlOC = "AND USU_DatBas='" + dataInicial + "'";

            if (txtRepresentante.getSelectedIndex() != -1) {
                txtRep.setText("");
                if (!txtRepresentante.getSelectedItem().toString().equalsIgnoreCase("SELECIONE")) {
                    representante = representanteDAO.getRepresentante("REP", " and rep.nomrep like '%" + txtRepresentante.getSelectedItem().toString() + "%'");
                    String Codigo = representante.getCodigo().toString();
                    txtRep.setText(Codigo);
                    sqlOC += "AND USU_CodRep=" + Codigo;
                }
                if (!txtRep.getText().trim().isEmpty()) {
                    sqlOC += "AND USU_CodRep=" + txtRep.getText().trim();
                }
            } else {
                sqlOC += "AND USU_CodRep=" + txtRep.getText().trim();
            }
        }
        return sqlOC;
    }

    private String montarPesquisa() throws ParseException, SQLException {
        String sql = "";

        if (jDChooserDataBase.getDate() == null) {
            Mensagem.mensagem("OK", "É Obrigatório preenchimento da Compentência");
        } else {
            if (chkVendedor.isSelected()) {
                MontarData(jDChooserDataBase.getDate());
                sql = " and E301MCR.DatPgt>='" + dataInicialVen + "' and E301MCR.DatPgt<='" + dataFinalVen + "'";
                sql = sql + " and E090REP.USU_CatRep='VEN'";
            }
            if (chkRepresentante.isSelected()) {
                MontarData(jDChooserDataBase.getDate());
                sql = " and E301MCR.DatPgt>='" + dataInicial + "' and E301MCR.DatPgt<='" + dataFinal + "'";
                sql += " and E090REP.USU_CatRep='REP'";
            }
            if (!txtCliente.getText().trim().isEmpty()) {
                sql += " and E301TCR.CodCli = '" + txtCliente.getText().trim() + "'";
            }
            if (txtRepresentante.getSelectedIndex() != -1) {
                txtRep.setText("");
                if (!txtRepresentante.getSelectedItem().toString().equalsIgnoreCase("SELECIONE")) {
                    representante = representanteDAO.getRepresentante("REP", " and rep.nomrep like '%" + txtRepresentante.getSelectedItem().toString() + "%'");
                    String Codigo = representante.getCodigo().toString();
                    txtRep.setText(Codigo);
                    sql += " and E301TCR.codrep = '" + Codigo + "'\n";
                }
                if (!txtRep.getText().trim().isEmpty()) {
                    sql += " and E301TCR.CodRep = '" + txtRep.getText().trim() + "'";
                }
            } else {
                sql += " and E301TCR.CodRep = '" + txtRep.getText().trim() + "'";
            }
            if (dcPagIni.getDate() != null) {
                pegarDataDigitada();
                System.out.print(pagIni);
                sql += " and E301MCR.DatPgt>='" + pagIni + "'";
            }
            if (dcPagFim.getDate() != null) {
                pegarDataDigitada();
                System.out.print(pagFim);
                sql += " and E301MCR.DatPgt<='" + pagFim + "'";
            }
            if (!txtCodGrupo.getText().isEmpty()) {
                sql += " and E085CLI.CodGre like '%" + txtCodGrupo.getText().trim() + "%'";
                if (txtGrupo.getText().isEmpty()) {
                    String Nome = titulos.getCadClienteGrupo().getNome();
                    txtGrupo.setText(Nome);
                }
            }
            if (!txtNome.getText().isEmpty()) {
                sql += " and UPPER(E085CLI.NomCli) like UPPER('%" + txtNome.getText().trim() + "%')";
                if (!txtCliente.getText().trim().isEmpty()) {
                    txtCliente.setText(cliente.getCodigo().toString());
                }
            }
            if (chkAuto.isSelected()) {
                sql += " and E301TCR.CodCrp='AUT'";
            }
            if (chkMoto.isSelected()) {
                sql += " and E301TCR.CodCrp='MOT'";
            }
            if (!txtTitulo.getText().isEmpty()) {
                sql += " and  E301TCR.NumTit='" + txtTitulo.getText().trim() + "'";
            }
            if (!txtTipoTitulo.getText().isEmpty()) {
                sql += " and  E301TCR.CodTpt='" + txtTipoTitulo.getText().trim() + "'";
            }
            if (!txtPedido.getText().isEmpty()) {
                sql += " and  E301TCR.NumPed='" + txtPedido.getText().trim() + "'";
            }
            if (!txtNota.getText().isEmpty()) {
                sql += " and  E301TCR.NumNfv='" + txtNota.getText().trim() + "'";
            }
            if (!txtSerie.getText().isEmpty()) {
                sql += " and  E301TCR.CodSnf='" + txtSerie.getText().trim() + "'";
            }
            if (chkSemComissao.isSelected()) {
                sql += " and E301TCR.PerCom=0";
            }
            if (chkpercentual.isSelected()) {
                if (txtperc.getText().trim().isEmpty()) {
                    sql += " and E301TCR.PerCom<>0";
                } else {
                    sql += " and E301TCR.PerCom=" + txtperc.getText().trim();
                }
            }
            lblqtdTitulos.setText(String.valueOf(jTableTitulos.getRowCount()));
            getSelecaoPlanta();

        }
        return sql;
    }

    private void filtrarTituloMov(String titulo) throws SQLException, Exception {
        if (!titulo.isEmpty()) {
            lstTitulosMov = comissaoTitulosMovimentosDAO.getTitulos("", " and  E301TCR.NumTit='" + txtTitulo.getText().trim() + "'");

            if (lstTitulosMov != null) {
                carregarMovimentos();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Titulo não selecionado ");

        }
    }

    private ComissaoTitulosMovimentos gravarParametrosOC(ComissaoTitulosMovimentos ocs) throws SQLException, ParseException {
        try {
            FormatarNumeros format = new FormatarNumeros();
            jTableTitulosMovEmpresaOC.getCellSelectionEnabled();
            int linhaSelSit = jTableTitulosMovEmpresaOC.getSelectedRow();
            int colunaSelSit = jTableTitulosMovEmpresaOC.getSelectedColumn();
            String empresaSel = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 1).toString();
            String fornecedorSel = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 3).toString();
            String condPag = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 4).toString();
            String valorComissao = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 7).toString();
            String valorIrf = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 8).toString();
            String valorLiq = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 9).toString();
            String email = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 11).toString();
            String rep = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 12).toString();
            String idOC = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 13).toString();
            ocs.setFornecedor(Integer.valueOf(fornecedorSel));
            ocs.setCondPagOc(condPag);
            ocs.setValorComissao(format.converterToDouble(valorComissao));
            ocs.setValorLiq(format.converterToDouble(valorLiq));
            ocs.setValorIrf(format.converterToDouble(valorIrf));
            ocs.setEmailRep(email);
            ocs.setRepOc(Integer.valueOf(rep));
            int ideExt = Integer.valueOf(idOC) + 1;
            ocs.setIdeExt(ideExt);
            int i = 0;
            while (i < lstTitulosOcsItens.size()) {
                String empresaSelItens = jTableTitulosMovEmpresaOcItens.getValueAt(i, 0).toString();
                String servicoItens = jTableTitulosMovEmpresaOcItens.getValueAt(i, 3).toString();
                String compItens = jTableTitulosMovEmpresaOcItens.getValueAt(i, 4).toString();
                String comissaoItens = jTableTitulosMovEmpresaOcItens.getValueAt(i, 7).toString();
                ocs.setServico(servicoItens);
                ocs.setComplementoServ(compItens);
                ocs.setValorComissaoItem(format.converterToDouble(comissaoItens));
                System.out.print(lstTitulosOcsItens.get(i) + " ");
                i++;
            }
            i = 0;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
            System.out.println("" + e);
        }
        return ocs;

    }

    private void salvarOC(ComissaoTitulosMovimentos ocs) throws SQLException, ParseException {
        try {
            OrdemCompraComissao oc = new OrdemCompraComissao();

            if (ordemCompraComissaoDAO == null) {
                this.ordemCompraComissaoDAO = new OrdemCompraComissaoDAO();
            }

            oc.setEmpresa(1); //USU_CODEMP,
            oc.setFilial(1);  //USU_CODFIL,
            oc.setRepresentante(ocs.getRepOc()); //USU_CODREP,
            oc.setDataBase(dataInicial); //USU_DATBAS,
            oc.setNumeroOrdemCompra(ocs.getNumeroOC()); //USU_NUMOCP,
            oc.setCodigoFornecedor(ocs.getFornecedor()); //USU_CODFOR,
            oc.setValorLiquido(ocs.getValorLiq()); //USU_VLRLIQ,
            oc.setValorIrf(ocs.getValorIrf());  //USU_VLRIRF,
//            oc.setEmpresaDestino(ocs.getEmpresaComissao()); //USU_EMPDES,
            oc.setEmpresaDestino(1);
            oc.setFilialDestino(1);  //USU_FILDES,
            oc.setEmailRep(ocs.getEmailRep());  //USU_INTNET

            if (!ordemCompraComissaoDAO.inserir(oc)) {

            } else {

            }

        } catch (SQLException ex) {

        } finally {

        }
    }

    private void gerarOc() throws SQLException, ParseException {
        String re = " ";
        String sNumOcp = " ";
        String resultado = " ";
        String erro = " ";
        ComissaoTitulosMovimentos ocs = new ComissaoTitulosMovimentos();

        try {
            gravarParametrosOC(ocs);
            WsOrdemDeCompra wsOrdemDeCompra = new WsOrdemDeCompra();

            re = wsOrdemDeCompra.ordemDeCompraSapiensComissao(ocs, this.lstTitulosOcsItens, ocs.getFornecedor().toString(), ocs.getValorLiq());

            int intretorno = re.indexOf("<mensagemRetorno>");
            int intFinalRetorno = re.indexOf("</mensagemRetorno>");
            resultado = re.substring(intretorno + 17, intFinalRetorno);
            int retornoNumOcp = re.indexOf("<numOcp>");
            int retornoNumOcpFim = re.indexOf("</numOcp>");
            int retornoErro = re.indexOf("<retorno>");
            int retornoErroFim = re.indexOf("</retorno>");
            sNumOcp = re.substring(retornoNumOcp + 8, retornoNumOcpFim);
            ocs.setNumeroOC(Integer.valueOf(sNumOcp));
            erro = re.substring(retornoErro + 9, retornoErroFim);
        } catch (Exception ex) {
            System.out.println("br.com.sgi.frame.Comissao.gerarOc()" + ex);
        }
        if (resultado.equals("Processado com sucesso.")) {
            // Gravar OC na Tabela USU_T420OCP de histórico            
            salvarOC(ocs);

        } else if (resultado.equals("Ocorreram erros.")) {
            JOptionPane.showMessageDialog(null, erro);
        }

    }

    private void excluirOC() throws Exception {
        try {
            OrdemCompraComissao oc = new OrdemCompraComissao();
            jTableTitulosMovEmpresaOC.getCellSelectionEnabled();
            int linhaSelSit = jTableOrdensGeradas.getSelectedRow();
            int colunaSelSit = jTableOrdensGeradas.getSelectedColumn();
            String empresa = jTableOrdensGeradas.getValueAt(linhaSelSit, 0).toString();
            String OC = jTableOrdensGeradas.getValueAt(linhaSelSit, 1).toString();
            String fornecedor = jTableOrdensGeradas.getValueAt(linhaSelSit, 3).toString();
            oc.setEmpresa(1);
            oc.setFilial(1);
            oc.setNumeroOrdemCompra(Integer.valueOf(OC));
            oc.setCodigoFornecedor(Integer.valueOf(fornecedor));
            if (!ordemCompraComissaoDAO.removerItens(oc)) {

            }
            if (!ordemCompraComissaoDAO.remover(oc)) {

            }
            if (!ordemCompraComissaoDAO.removerHistorico(oc)) {

            } else {

            }
        } catch (Exception ex) {
            System.out.println("br.com.sgi.frame.Comissao.gerarOc()" + ex);
        }
    }

    private void alterarOC() throws Exception {
        try {
            if (txtCodForNovo.getText().isEmpty()) {
                Mensagem.mensagem("ERROR", "É necessário preencher o campo com o novo fornecedor para alteração");
            } else {
                OrdemCompraComissao oc = new OrdemCompraComissao();
                jTableTitulosMovEmpresaOC.getCellSelectionEnabled();
                int linhaSelSit = jTableOrdensGeradas.getSelectedRow();
                int colunaSelSit = jTableOrdensGeradas.getSelectedColumn();
                String OC = jTableOrdensGeradas.getValueAt(linhaSelSit, 1).toString();
                String fornecedor = jTableOrdensGeradas.getValueAt(linhaSelSit, 3).toString();
                oc.setEmpresa(1);
                oc.setFilial(1);
                oc.setNumeroOrdemCompra(Integer.valueOf(OC));
                oc.setCodigoFornecedor(Integer.valueOf(txtCodForNovo.getText()));
                if (!ordemCompraComissaoDAO.alterar(oc)) {

                }
                if (!ordemCompraComissaoDAO.alterarHistorico(oc)) {

                } else {

                }
            }
        } catch (Exception ex) {
            System.out.println("br.com.sgi.frame.Comissao.gerarOc()" + ex);
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

    public class RegistroZeradoTableRenderer extends DefaultTableCellRenderer {

        Color defaultBackground, defaultForeground;

        public RegistroZeradoTableRenderer() {
            this.defaultBackground = getBackground();
            this.defaultForeground = getForeground();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Integer saldo = (int) value;

            if (saldo == 0) {
                c.setBackground(Color.red);
                c.setForeground(Color.white);
            } else {
                c.setBackground(defaultBackground);
                c.setForeground(defaultForeground);
            }
            setText(saldo.toString());

            return c;
        }

    }

    public class DiferencaTableRenderer extends DefaultTableCellRenderer {

        Color defaultBackground, defaultForeground;

        public DiferencaTableRenderer() {
            this.defaultBackground = getBackground();
            this.defaultForeground = getForeground();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Double saldo = (Double) value;

            c.setBackground(defaultBackground);
            c.setForeground(defaultForeground);

            if (saldo > 0.1) {
                c.setBackground(Color.red);
                c.setForeground(Color.black);
            }
            if (saldo < -0.1) {
                c.setBackground(Color.yellow);
                c.setForeground(Color.black);
            }
            if (saldo == 0) {
                c.setBackground(defaultBackground);
                c.setForeground(defaultForeground);
            }
            setText(saldo.toString());

            return c;
        }

    }

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
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        btnGerarOC = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        txtNome = new org.openswing.swing.client.TextControl();
        txtCliente = new org.openswing.swing.client.TextControl();
        barra = new javax.swing.JProgressBar();
        chkFab = new javax.swing.JRadioButton();
        chkCd = new javax.swing.JRadioButton();
        txtFilial = new javax.swing.JComboBox<>();
        btnPesquisar = new javax.swing.JButton();
        jTabTitulos = new javax.swing.JTabbedPane();
        jPMovimento = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableTitulosMov = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableTitulosMovTipo = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableTitulosMovEmpresa = new javax.swing.JTable();
        jPTitulo = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTitulos = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableTitulosMovPerc = new javax.swing.JTable();
        jPOCs = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTableTitulosMovEmpresaOC = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTableTitulosMovEmpresaOcItens = new javax.swing.JTable();
        jLabel31 = new javax.swing.JLabel();
        txtMail = new org.openswing.swing.client.TextControl();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTableOrdensGeradas = new javax.swing.JTable();
        txtCodForNovo = new org.openswing.swing.client.TextControl();
        jLabel32 = new javax.swing.JLabel();
        btnFechar1 = new javax.swing.JButton();
        btnEnviarEmail = new javax.swing.JButton();
        btnFechar3 = new javax.swing.JButton();
        chkVendedor = new javax.swing.JRadioButton();
        chkRepresentante = new javax.swing.JRadioButton();
        txtRep = new org.openswing.swing.client.TextControl();
        jLabel14 = new javax.swing.JLabel();
        chkMoto = new javax.swing.JRadioButton();
        chkAuto = new javax.swing.JRadioButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtTitulo = new org.openswing.swing.client.TextControl();
        txtTipoTitulo = new org.openswing.swing.client.TextControl();
        jLabel23 = new javax.swing.JLabel();
        txtPedido = new org.openswing.swing.client.TextControl();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtSerie = new org.openswing.swing.client.TextControl();
        txtNota = new org.openswing.swing.client.TextControl();
        jLabel26 = new javax.swing.JLabel();
        btnLimpar = new javax.swing.JButton();
        chkSemComissao = new javax.swing.JRadioButton();
        chkpercentual = new javax.swing.JRadioButton();
        txtperc = new org.openswing.swing.client.TextControl();
        jLabel27 = new javax.swing.JLabel();
        jDChooserDataBase = new com.toedter.calendar.JDateChooser();
        lblqtdTitulos = new javax.swing.JLabel();
        lblcomissao = new javax.swing.JLabel();
        lblvalorBase = new javax.swing.JLabel();
        lblvalorPago = new javax.swing.JLabel();
        txtRepresentante = new javax.swing.JComboBox<>();
        dcPagIni = new org.openswing.swing.client.DateControl();
        dcPagFim = new org.openswing.swing.client.DateControl();
        labelControl1 = new org.openswing.swing.client.LabelControl();
        jLabel28 = new javax.swing.JLabel();
        txtGrupo = new org.openswing.swing.client.TextControl();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtCodGrupo = new org.openswing.swing.client.TextControl();
        btnReabilitar = new javax.swing.JButton();
        lblqtdTitulosMov = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Comissões");
        setToolTipText("");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        btnGerarOC.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnGerarOC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnGerarOC.setText("Gerar OC");
        btnGerarOC.setToolTipText("");
        btnGerarOC.setFocusCycleRoot(true);
        btnGerarOC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarOCActionPerformed(evt);
            }
        });

        btnImprimir.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        btnImprimir.setText("Imprimir");
        btnImprimir.setToolTipText("");
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        buttonGroup3.add(chkFab);
        chkFab.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkFab.setForeground(new java.awt.Color(0, 153, 0));
        chkFab.setText("Fabrica");
        chkFab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFabActionPerformed(evt);
            }
        });

        buttonGroup3.add(chkCd);
        chkCd.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkCd.setForeground(new java.awt.Color(255, 51, 51));
        chkCd.setText("CDs");
        chkCd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCdActionPerformed(evt);
            }
        });

        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtFilial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 - SELECIONE" }));
        txtFilial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialActionPerformed(evt);
            }
        });

        btnPesquisar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesquisar.setText("Mostrar");
        btnPesquisar.setToolTipText("");
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        jTabTitulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabTitulosMouseClicked(evt);
            }
        });
        jTabTitulos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTabTitulosKeyPressed(evt);
            }
        });

        jTableTitulosMov.setAutoCreateRowSorter(true);
        jTableTitulosMov.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTableTitulosMov.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Filial", "Tipo", "Titulo", "Seq.", "Transação", "Emissão Nota", "Vencimento", "Valor Aberto", "Data Pagamento", "Forma de Pagamento", "Valor Movimento", "Valor Outros", "Encargos", "Multas", "Valor Liquido", "Valor Base Comissão", "% Comissão", "Valor Comissão", "Descontos", "Juros", "Nota Fiscal", "Pedido", "Diferença", "Linha"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTitulosMov.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableTitulosMovMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableTitulosMov);
        if (jTableTitulosMov.getColumnModel().getColumnCount() > 0) {
            jTableTitulosMov.getColumnModel().getColumn(0).setMinWidth(30);
            jTableTitulosMov.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTableTitulosMov.getColumnModel().getColumn(0).setMaxWidth(30);
            jTableTitulosMov.getColumnModel().getColumn(1).setMinWidth(30);
            jTableTitulosMov.getColumnModel().getColumn(1).setPreferredWidth(30);
            jTableTitulosMov.getColumnModel().getColumn(1).setMaxWidth(30);
            jTableTitulosMov.getColumnModel().getColumn(2).setMinWidth(100);
            jTableTitulosMov.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableTitulosMov.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableTitulosMov.getColumnModel().getColumn(3).setMinWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(3).setPreferredWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(3).setMaxWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(4).setMinWidth(60);
            jTableTitulosMov.getColumnModel().getColumn(4).setPreferredWidth(60);
            jTableTitulosMov.getColumnModel().getColumn(4).setMaxWidth(60);
            jTableTitulosMov.getColumnModel().getColumn(7).setMinWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(7).setMaxWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(9).setMinWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(9).setPreferredWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(9).setMaxWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(11).setMinWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(11).setPreferredWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(11).setMaxWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(12).setMinWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(12).setPreferredWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(12).setMaxWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(13).setMinWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(13).setPreferredWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(13).setMaxWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(14).setMinWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(14).setPreferredWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(14).setMaxWidth(0);
            jTableTitulosMov.getColumnModel().getColumn(15).setMinWidth(120);
            jTableTitulosMov.getColumnModel().getColumn(15).setPreferredWidth(120);
            jTableTitulosMov.getColumnModel().getColumn(15).setMaxWidth(120);
        }

        jTableTitulosMovTipo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tipo", "Linha", "Quantidade", "Valor Liquido", "Valor Base Comissão", "Valor Comissão"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTitulosMovTipo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableTitulosMovTipoMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTableTitulosMovTipo);
        if (jTableTitulosMovTipo.getColumnModel().getColumnCount() > 0) {
            jTableTitulosMovTipo.getColumnModel().getColumn(0).setMinWidth(40);
            jTableTitulosMovTipo.getColumnModel().getColumn(0).setPreferredWidth(40);
            jTableTitulosMovTipo.getColumnModel().getColumn(1).setMinWidth(40);
            jTableTitulosMovTipo.getColumnModel().getColumn(1).setPreferredWidth(40);
            jTableTitulosMovTipo.getColumnModel().getColumn(2).setMinWidth(70);
            jTableTitulosMovTipo.getColumnModel().getColumn(2).setPreferredWidth(70);
            jTableTitulosMovTipo.getColumnModel().getColumn(3).setMinWidth(80);
            jTableTitulosMovTipo.getColumnModel().getColumn(3).setPreferredWidth(80);
            jTableTitulosMovTipo.getColumnModel().getColumn(4).setMinWidth(120);
            jTableTitulosMovTipo.getColumnModel().getColumn(4).setPreferredWidth(120);
            jTableTitulosMovTipo.getColumnModel().getColumn(5).setMinWidth(100);
            jTableTitulosMovTipo.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        jTableTitulosMovEmpresa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Empresa", "Tipo", "Quantidade", "Valor Liquido", "Valor Base Comissão", "Valor Comissão"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTitulosMovEmpresa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableTitulosMovEmpresaMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jTableTitulosMovEmpresa);
        if (jTableTitulosMovEmpresa.getColumnModel().getColumnCount() > 0) {
            jTableTitulosMovEmpresa.getColumnModel().getColumn(0).setMinWidth(60);
            jTableTitulosMovEmpresa.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTableTitulosMovEmpresa.getColumnModel().getColumn(2).setMinWidth(70);
            jTableTitulosMovEmpresa.getColumnModel().getColumn(2).setPreferredWidth(70);
            jTableTitulosMovEmpresa.getColumnModel().getColumn(4).setMinWidth(120);
            jTableTitulosMovEmpresa.getColumnModel().getColumn(4).setPreferredWidth(120);
        }

        javax.swing.GroupLayout jPMovimentoLayout = new javax.swing.GroupLayout(jPMovimento);
        jPMovimento.setLayout(jPMovimentoLayout);
        jPMovimentoLayout.setHorizontalGroup(
            jPMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPMovimentoLayout.createSequentialGroup()
                .addGroup(jPMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPMovimentoLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addGap(0, 0, 0))
        );
        jPMovimentoLayout.setVerticalGroup(
            jPMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPMovimentoLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addGroup(jPMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(1, 1, 1))
        );

        jTabTitulos.addTab("Movimentos/Baixa", jPMovimento);

        jTableTitulos.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTableTitulos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Situação", "Filial", "Cliente", "Nome", "Representante", "", "Titulo", "Tipo", "Nota", "Série", "Pedido", "Valor", "Base Comissão", "%Comissão", "Comissão", "Valor Pago", "Valor Aberto", "Grupo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true, false, true, true, true, true, true, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTitulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableTitulosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableTitulos);
        if (jTableTitulos.getColumnModel().getColumnCount() > 0) {
            jTableTitulos.getColumnModel().getColumn(0).setMinWidth(0);
            jTableTitulos.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTableTitulos.getColumnModel().getColumn(0).setMaxWidth(0);
            jTableTitulos.getColumnModel().getColumn(1).setMinWidth(60);
            jTableTitulos.getColumnModel().getColumn(1).setPreferredWidth(60);
            jTableTitulos.getColumnModel().getColumn(1).setMaxWidth(60);
            jTableTitulos.getColumnModel().getColumn(3).setMinWidth(100);
            jTableTitulos.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableTitulos.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableTitulos.getColumnModel().getColumn(4).setMinWidth(300);
            jTableTitulos.getColumnModel().getColumn(4).setPreferredWidth(300);
            jTableTitulos.getColumnModel().getColumn(4).setMaxWidth(300);
            jTableTitulos.getColumnModel().getColumn(6).setMinWidth(300);
            jTableTitulos.getColumnModel().getColumn(6).setPreferredWidth(300);
            jTableTitulos.getColumnModel().getColumn(6).setMaxWidth(300);
            jTableTitulos.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableTitulos.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableTitulos.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableTitulos.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableTitulos.getColumnModel().getColumn(13).setMinWidth(0);
            jTableTitulos.getColumnModel().getColumn(13).setPreferredWidth(0);
            jTableTitulos.getColumnModel().getColumn(13).setMaxWidth(0);
            jTableTitulos.getColumnModel().getColumn(15).setMinWidth(0);
            jTableTitulos.getColumnModel().getColumn(15).setPreferredWidth(0);
            jTableTitulos.getColumnModel().getColumn(15).setMaxWidth(0);
            jTableTitulos.getColumnModel().getColumn(16).setMinWidth(0);
            jTableTitulos.getColumnModel().getColumn(16).setPreferredWidth(0);
            jTableTitulos.getColumnModel().getColumn(16).setMaxWidth(0);
            jTableTitulos.getColumnModel().getColumn(17).setMinWidth(0);
            jTableTitulos.getColumnModel().getColumn(17).setPreferredWidth(0);
            jTableTitulos.getColumnModel().getColumn(17).setMaxWidth(0);
        }

        javax.swing.GroupLayout jPTituloLayout = new javax.swing.GroupLayout(jPTitulo);
        jPTitulo.setLayout(jPTituloLayout);
        jPTituloLayout.setHorizontalGroup(
            jPTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPTituloLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1163, Short.MAX_VALUE))
        );
        jPTituloLayout.setVerticalGroup(
            jPTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
        );

        jTabTitulos.addTab("Titulos", jPTitulo);

        jTableTitulosMovPerc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Linha", "Quantidade", "%Comisão", "Valor Liquido", "Valor Base Comissão", "Valor Comissão"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTitulosMovPerc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableTitulosMovPercMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableTitulosMovPerc);
        if (jTableTitulosMovPerc.getColumnModel().getColumnCount() > 0) {
            jTableTitulosMovPerc.getColumnModel().getColumn(0).setMinWidth(40);
            jTableTitulosMovPerc.getColumnModel().getColumn(0).setPreferredWidth(40);
            jTableTitulosMovPerc.getColumnModel().getColumn(1).setMinWidth(70);
            jTableTitulosMovPerc.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTableTitulosMovPerc.getColumnModel().getColumn(2).setMinWidth(65);
            jTableTitulosMovPerc.getColumnModel().getColumn(2).setPreferredWidth(65);
            jTableTitulosMovPerc.getColumnModel().getColumn(3).setMinWidth(70);
            jTableTitulosMovPerc.getColumnModel().getColumn(3).setPreferredWidth(70);
            jTableTitulosMovPerc.getColumnModel().getColumn(4).setMinWidth(110);
            jTableTitulosMovPerc.getColumnModel().getColumn(4).setPreferredWidth(110);
            jTableTitulosMovPerc.getColumnModel().getColumn(5).setMinWidth(100);
            jTableTitulosMovPerc.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        jTabTitulos.addTab("Total por %", jScrollPane3);

        jScrollPane7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jScrollPane7KeyPressed(evt);
            }
        });

        jTableTitulosMovEmpresaOC.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Empresa", "OC", "Fornecedor", "Condição de Pagamento", "Descrição", "Tipo de Titulo", "Valor Comissão", "Valor IR", "Valor Liquido", "Regime Tributário", "E-mail", "Rep", "IdeExt"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, false, false, false, false, false, true, true, true, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTitulosMovEmpresaOC.setMinimumSize(new java.awt.Dimension(350, 0));
        jTableTitulosMovEmpresaOC.setRowHeight(10);
        jTableTitulosMovEmpresaOC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableTitulosMovEmpresaOCMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableTitulosMovEmpresaOCMousePressed(evt);
            }
        });
        jTableTitulosMovEmpresaOC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableTitulosMovEmpresaOCKeyPressed(evt);
            }
        });
        jScrollPane7.setViewportView(jTableTitulosMovEmpresaOC);
        if (jTableTitulosMovEmpresaOC.getColumnModel().getColumnCount() > 0) {
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(0).setMinWidth(0);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(0).setMaxWidth(0);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(1).setMinWidth(60);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(1).setPreferredWidth(60);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(4).setMinWidth(70);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(4).setPreferredWidth(70);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(6).setMinWidth(120);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(6).setPreferredWidth(120);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(13).setMinWidth(0);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(13).setPreferredWidth(0);
            jTableTitulosMovEmpresaOC.getColumnModel().getColumn(13).setMaxWidth(0);
        }

        jScrollPane8.setAutoscrolls(true);

        jTableTitulosMovEmpresaOcItens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Empresa", "OC", "Seq.", "Serviço", "Complemento", "Linha", "Tipo de Titulo", "Valor Comissão"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTitulosMovEmpresaOcItens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableTitulosMovEmpresaOcItensMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(jTableTitulosMovEmpresaOcItens);
        if (jTableTitulosMovEmpresaOcItens.getColumnModel().getColumnCount() > 0) {
            jTableTitulosMovEmpresaOcItens.getColumnModel().getColumn(0).setMinWidth(60);
            jTableTitulosMovEmpresaOcItens.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTableTitulosMovEmpresaOcItens.getColumnModel().getColumn(1).setMinWidth(0);
            jTableTitulosMovEmpresaOcItens.getColumnModel().getColumn(1).setPreferredWidth(0);
            jTableTitulosMovEmpresaOcItens.getColumnModel().getColumn(1).setMaxWidth(0);
            jTableTitulosMovEmpresaOcItens.getColumnModel().getColumn(4).setMinWidth(70);
            jTableTitulosMovEmpresaOcItens.getColumnModel().getColumn(4).setPreferredWidth(70);
            jTableTitulosMovEmpresaOcItens.getColumnModel().getColumn(6).setMinWidth(120);
            jTableTitulosMovEmpresaOcItens.getColumnModel().getColumn(6).setPreferredWidth(120);
        }

        jLabel31.setText("E-mail (Opcional)");

        txtMail.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMailActionPerformed(evt);
            }
        });

        jScrollPane9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jScrollPane9KeyPressed(evt);
            }
        });

        jTableOrdensGeradas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Empresa", "OC", "Rep", "Fornecedor", "Valor IR", "Valor Liquido", "E-mail"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableOrdensGeradas.setMinimumSize(new java.awt.Dimension(350, 0));
        jTableOrdensGeradas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableOrdensGeradasMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableOrdensGeradasMousePressed(evt);
            }
        });
        jTableOrdensGeradas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableOrdensGeradasKeyPressed(evt);
            }
        });
        jScrollPane9.setViewportView(jTableOrdensGeradas);
        if (jTableOrdensGeradas.getColumnModel().getColumnCount() > 0) {
            jTableOrdensGeradas.getColumnModel().getColumn(0).setMinWidth(60);
            jTableOrdensGeradas.getColumnModel().getColumn(0).setPreferredWidth(60);
        }

        txtCodForNovo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodForNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodForNovoActionPerformed(evt);
            }
        });

        jLabel32.setText("Fornecedor (Novo para a OC)");

        btnFechar1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnFechar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/table_add.png"))); // NOI18N
        btnFechar1.setText("Alterar");
        btnFechar1.setToolTipText("");
        btnFechar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFechar1ActionPerformed(evt);
            }
        });

        btnEnviarEmail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnEnviarEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/email.png"))); // NOI18N
        btnEnviarEmail.setText("Enviar E-Mail");
        btnEnviarEmail.setToolTipText("");
        btnEnviarEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarEmailActionPerformed(evt);
            }
        });

        btnFechar3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnFechar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnFechar3.setText("Excluir OC");
        btnFechar3.setToolTipText("");
        btnFechar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFechar3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPOCsLayout = new javax.swing.GroupLayout(jPOCs);
        jPOCs.setLayout(jPOCsLayout);
        jPOCsLayout.setHorizontalGroup(
            jPOCsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1163, Short.MAX_VALUE)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 1163, Short.MAX_VALUE)
            .addComponent(jScrollPane9)
            .addGroup(jPOCsLayout.createSequentialGroup()
                .addGroup(jPOCsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPOCsLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel31))
                    .addGroup(jPOCsLayout.createSequentialGroup()
                        .addComponent(txtMail, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEnviarEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15)
                .addGroup(jPOCsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPOCsLayout.createSequentialGroup()
                        .addComponent(txtCodForNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFechar1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFechar3, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel32))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPOCsLayout.setVerticalGroup(
            jPOCsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPOCsLayout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addGroup(jPOCsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPOCsLayout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(0, 0, 0)
                        .addComponent(txtCodForNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPOCsLayout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addGap(0, 0, 0)
                        .addComponent(txtMail, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnEnviarEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPOCsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFechar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFechar3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
        );

        jTabTitulos.addTab("Geração de OCs", jPOCs);

        buttonGroup1.add(chkVendedor);
        chkVendedor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkVendedor.setForeground(new java.awt.Color(0, 153, 0));
        chkVendedor.setText("Vendedor");
        chkVendedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVendedorActionPerformed(evt);
            }
        });

        buttonGroup1.add(chkRepresentante);
        chkRepresentante.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkRepresentante.setForeground(new java.awt.Color(255, 51, 51));
        chkRepresentante.setText("Representante");
        chkRepresentante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRepresentanteActionPerformed(evt);
            }
        });

        txtRep.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtRep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRepActionPerformed(evt);
            }
        });

        jLabel14.setText("Competência");

        buttonGroup2.add(chkMoto);
        chkMoto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkMoto.setForeground(new java.awt.Color(255, 51, 51));
        chkMoto.setText("Moto");
        chkMoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMotoActionPerformed(evt);
            }
        });

        buttonGroup2.add(chkAuto);
        chkAuto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkAuto.setForeground(new java.awt.Color(0, 153, 0));
        chkAuto.setText("Auto");
        chkAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAutoActionPerformed(evt);
            }
        });

        jLabel15.setText("Linha");

        jLabel16.setText("Localização");

        jLabel17.setText("Código");

        jLabel18.setText("Representante");

        jLabel19.setText("Representante/Vendedor");

        jLabel20.setText("Código");

        jLabel21.setText("Cliente");

        jLabel22.setText("Titulo");

        txtTitulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTitulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTituloActionPerformed(evt);
            }
        });

        txtTipoTitulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTipoTitulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTipoTituloActionPerformed(evt);
            }
        });

        jLabel23.setText("Tipo");

        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPedidoActionPerformed(evt);
            }
        });

        jLabel24.setText("Nota");

        jLabel25.setText("Pedido");

        txtSerie.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSerie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSerieActionPerformed(evt);
            }
        });

        txtNota.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNotaActionPerformed(evt);
            }
        });

        jLabel26.setText("Serie");

        btnLimpar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-vassoura-16.png"))); // NOI18N
        btnLimpar.setText("Limpar");
        btnLimpar.setToolTipText("");
        btnLimpar.setDisabledIcon(null);
        btnLimpar.setFocusCycleRoot(true);
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        buttonGroup4.add(chkSemComissao);
        chkSemComissao.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkSemComissao.setForeground(new java.awt.Color(255, 51, 51));
        chkSemComissao.setText("Sem Comissão");
        chkSemComissao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSemComissaoActionPerformed(evt);
            }
        });

        buttonGroup4.add(chkpercentual);
        chkpercentual.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkpercentual.setForeground(new java.awt.Color(0, 153, 0));
        chkpercentual.setSelected(true);
        chkpercentual.setText("%");
        chkpercentual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkpercentualActionPerformed(evt);
            }
        });

        txtperc.setEnabled(false);
        txtperc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtperc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpercActionPerformed(evt);
            }
        });

        jLabel27.setText("%Comissão");

        jDChooserDataBase.setDateFormatString("MM/yyyy");
        jDChooserDataBase.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jDChooserDataBase.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jDChooserDataBaseMouseClicked(evt);
            }
        });
        jDChooserDataBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jDChooserDataBaseKeyPressed(evt);
            }
        });

        lblqtdTitulos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblqtdTitulos.setForeground(new java.awt.Color(51, 102, 255));
        lblqtdTitulos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblqtdTitulos.setBorder(javax.swing.BorderFactory.createTitledBorder("Quantidade de Titulos"));
        lblqtdTitulos.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblqtdTitulos.setName(""); // NOI18N
        lblqtdTitulos.setOpaque(true);
        lblqtdTitulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblqtdTitulosMouseClicked(evt);
            }
        });

        lblcomissao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblcomissao.setForeground(new java.awt.Color(51, 102, 255));
        lblcomissao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblcomissao.setBorder(javax.swing.BorderFactory.createTitledBorder("Valor Comissão"));
        lblcomissao.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblcomissao.setName(""); // NOI18N
        lblcomissao.setOpaque(true);
        lblcomissao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblcomissaoMouseClicked(evt);
            }
        });

        lblvalorBase.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblvalorBase.setForeground(new java.awt.Color(51, 102, 255));
        lblvalorBase.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblvalorBase.setBorder(javax.swing.BorderFactory.createTitledBorder("Valor Base Comissão"));
        lblvalorBase.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblvalorBase.setName(""); // NOI18N
        lblvalorBase.setOpaque(true);
        lblvalorBase.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblvalorBaseMouseClicked(evt);
            }
        });

        lblvalorPago.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblvalorPago.setForeground(new java.awt.Color(51, 102, 255));
        lblvalorPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblvalorPago.setBorder(javax.swing.BorderFactory.createTitledBorder("Valor Pago Cliente"));
        lblvalorPago.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblvalorPago.setName(""); // NOI18N
        lblvalorPago.setOpaque(true);
        lblvalorPago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblvalorPagoMouseClicked(evt);
            }
        });

        txtRepresentante.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtRepresentante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE" }));
        txtRepresentante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRepresentanteActionPerformed(evt);
            }
        });

        dcPagIni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dcPagIni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dcPagIniActionPerformed(evt);
            }
        });

        dcPagFim.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dcPagFim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dcPagFimActionPerformed(evt);
            }
        });

        labelControl1.setText("Pagamento Final");

        jLabel28.setText("Pagamento Inicial");

        txtGrupo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGrupoActionPerformed(evt);
            }
        });

        jLabel29.setText("Código");

        jLabel30.setText("Grupo");

        txtCodGrupo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodGrupoActionPerformed(evt);
            }
        });

        btnReabilitar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnReabilitar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/reabilitar.png"))); // NOI18N
        btnReabilitar.setText("Reabilitar");
        btnReabilitar.setToolTipText("");
        btnReabilitar.setEnabled(false);
        btnReabilitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReabilitarActionPerformed(evt);
            }
        });

        lblqtdTitulosMov.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblqtdTitulosMov.setForeground(new java.awt.Color(51, 102, 255));
        lblqtdTitulosMov.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblqtdTitulosMov.setBorder(javax.swing.BorderFactory.createTitledBorder("Quantidade de Movimentos"));
        lblqtdTitulosMov.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblqtdTitulosMov.setName(""); // NOI18N
        lblqtdTitulosMov.setOpaque(true);
        lblqtdTitulosMov.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblqtdTitulosMovMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTabTitulos, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtRep, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(txtRepresentante, 0, 0, Short.MAX_VALUE)
                                        .addGap(5, 5, 5)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel20)
                                                .addGap(30, 30, 30)
                                                .addComponent(jLabel21)
                                                .addGap(222, 222, 222)
                                                .addComponent(jLabel29)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel30)
                                                .addGap(238, 238, 238)
                                                .addComponent(jLabel23)
                                                .addGap(28, 28, 28)
                                                .addComponent(jLabel22)
                                                .addGap(59, 59, 59)
                                                .addComponent(jLabel24)
                                                .addGap(37, 37, 37)
                                                .addComponent(jLabel26))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(txtCodGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(txtGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(txtTipoTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(1, 1, 1)
                                                .addComponent(jLabel17)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel18))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jDChooserDataBase, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel14))
                                                .addGap(5, 5, 5)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel19)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(chkVendedor)
                                                        .addGap(0, 0, 0)
                                                        .addComponent(chkRepresentante)))))
                                        .addGap(5, 5, 5)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel15)
                                                .addGap(88, 88, 88)
                                                .addComponent(jLabel16))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(chkAuto)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(chkMoto)
                                                .addGap(5, 5, 5)
                                                .addComponent(chkFab)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkCd)
                                        .addGap(5, 5, 5)
                                        .addComponent(txtFilial, 0, 1, Short.MAX_VALUE)
                                        .addGap(5, 5, 5)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(chkSemComissao)
                                                .addGap(0, 0, 0)
                                                .addComponent(chkpercentual)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtperc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel27))
                                        .addGap(5, 5, 5)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(dcPagIni, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel28))
                                        .addGap(5, 5, 5)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(labelControl1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(28, 28, 28))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(dcPagFim, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel25)
                                            .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnGerarOC, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnReabilitar, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnLimpar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(lblqtdTitulos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(lblqtdTitulosMov, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(lblvalorPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(lblvalorBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(lblcomissao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGerarOC, btnImprimir, btnLimpar, btnPesquisar, btnReabilitar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel19))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jDChooserDataBase, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkVendedor)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(chkRepresentante)
                                        .addComponent(chkAuto)
                                        .addComponent(chkMoto))))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel16)
                                .addComponent(jLabel15))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(chkSemComissao)
                                    .addComponent(chkpercentual)
                                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkCd)
                                    .addComponent(chkFab)
                                    .addComponent(txtperc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel28)
                                    .addComponent(labelControl1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel25))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(dcPagFim, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(dcPagIni, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel22)
                                .addComponent(jLabel23)
                                .addComponent(jLabel24))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel29)
                                .addComponent(jLabel30))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel17)
                                .addComponent(jLabel18))
                            .addComponent(jLabel21))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtSerie, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtRepresentante)
                                .addComponent(txtGrupo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTipoTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRep, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtCodGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnGerarOC, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnReabilitar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0)
                .addComponent(jTabTitulos, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblvalorPago, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblvalorBase, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblcomissao, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblqtdTitulosMov, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblqtdTitulos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCliente, txtCodGrupo, txtGrupo, txtNome, txtNota, txtRep, txtSerie, txtTipoTitulo, txtTitulo});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtPedido, txtperc});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGerarOC, btnImprimir, btnLimpar, btnPesquisar, btnReabilitar});

        getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGerarOCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarOCActionPerformed
        try {
            if (!txtRep.getText().isEmpty()) {
                if (btnImprimir.getText().equals("Imprimir")) {
                    gerarOc();
                    iniciarBarra("", montarPesquisa());

                } else {
                    Mensagem.mensagem("ERROR", "É Obrigatório Validar antes de gerar a OC");
                }
            } else {
                Mensagem.mensagem("ERROR", "É Obrigatório preenchimento do Representante ou Vendedor");

            }
        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnGerarOCActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtClienteActionPerformed

    private void chkCdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCdActionPerformed
        try {
            preencherComboFilial(1);

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_chkCdActionPerformed

    private void chkFabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFabActionPerformed
        txtFilial.removeAllItems();
        txtFilial.addItem("0 - SELECIONE");

    }//GEN-LAST:event_chkFabActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void chkVendedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVendedorActionPerformed
        try {
            preencherComboVendedor(1);
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
    }//GEN-LAST:event_chkVendedorActionPerformed

    private void chkRepresentanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRepresentanteActionPerformed
        try {
            preencherComboRepresentante(1);
            //  montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
    }//GEN-LAST:event_chkRepresentanteActionPerformed

    private void txtRepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRepActionPerformed
        try {
            buscarRepVen("and rep.CodRep = '" + txtRep.getText().trim() + "'");
//            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtRepActionPerformed

    private void chkMotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMotoActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_chkMotoActionPerformed

    private void chkAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAutoActionPerformed
        try {
//            montarPesquisa();
            getTitulos("DATA", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
    }//GEN-LAST:event_chkAutoActionPerformed

    private void txtTituloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTituloActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtTituloActionPerformed

    private void txtTipoTituloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTipoTituloActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtTipoTituloActionPerformed

    private void txtPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPedidoActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtPedidoActionPerformed

    private void txtSerieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSerieActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtSerieActionPerformed

    private void txtNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotaActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtNotaActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        Limpar();
    }//GEN-LAST:event_btnLimparActionPerformed

    private void chkSemComissaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSemComissaoActionPerformed
        try {
            if (chkSemComissao.isSelected()) {
                txtperc.setEnabled(false);
            }
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_chkSemComissaoActionPerformed

    private void chkpercentualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkpercentualActionPerformed
        try {
            if (chkpercentual.isSelected()) {
                txtperc.setEnabled(true);
            }
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_chkpercentualActionPerformed

    private void txtpercActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpercActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtpercActionPerformed

    private void lblqtdTitulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblqtdTitulosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lblqtdTitulosMouseClicked

    private void lblcomissaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblcomissaoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lblcomissaoMouseClicked

    private void lblvalorBaseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblvalorBaseMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lblvalorBaseMouseClicked

    private void lblvalorPagoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblvalorPagoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lblvalorPagoMouseClicked

    private void jDChooserDataBaseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDChooserDataBaseMouseClicked

    }//GEN-LAST:event_jDChooserDataBaseMouseClicked

    private void jDChooserDataBaseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jDChooserDataBaseKeyPressed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
    }//GEN-LAST:event_jDChooserDataBaseKeyPressed

    private void txtFilialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilialActionPerformed

    private void txtRepresentanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRepresentanteActionPerformed
        try {
            if (txtRepresentante.getSelectedIndex() != -1) {
                if (!txtRepresentante.getSelectedItem().toString().equals("SELECIONE")) {
                    iniciarBarra("", montarPesquisa());

                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
    }//GEN-LAST:event_txtRepresentanteActionPerformed

    private void dcPagIniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dcPagIniActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
    }//GEN-LAST:event_dcPagIniActionPerformed

    private void dcPagFimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dcPagFimActionPerformed
        try {
//            montarPesquisa();
            iniciarBarra("", montarPesquisa());

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
    }//GEN-LAST:event_dcPagFimActionPerformed

    private void txtGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGrupoActionPerformed
        try {
            buscarGrupo();
            iniciarBarra("", montarPesquisa());

        } catch (ParseException ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (SQLException ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtGrupoActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        try {
            if (!txtRep.getText().isEmpty()) {

                if (btnImprimir.getText().equals("Validar")) {
                    validarRegistrosGrid();
                } else {
                    iniciarBarraRelatorio();
                    gerarRelatatorio();
                    salvarRelatatorio();
                    abrirRelatatorio();
                }

//                if (abrir.equals("S")) {
//                    abrirRelatatorio();
//                }
            } else {
                Mensagem.mensagem("ERROR", "É Obrigatório preenchimento do Representante ou Vendedor");

            }

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void txtCodGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodGrupoActionPerformed
        try {
            buscarGrupo();
            iniciarBarra("", montarPesquisa());

        } catch (ParseException ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (SQLException ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtCodGrupoActionPerformed

    private void lblqtdTitulosMovMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblqtdTitulosMovMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lblqtdTitulosMovMouseClicked

    private void jTableTitulosMovEmpresaOCMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTitulosMovEmpresaOCMouseClicked
        try {
            int linhaSelSit = jTableTitulosMovEmpresaOC.getSelectedRow();
            int colunaSelSit = jTableTitulosMovEmpresaOC.getSelectedColumn();
            String rep = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 12).toString();
            String empresaSel = jTableTitulosMovEmpresaOC.getValueAt(linhaSelSit, 1).toString();
            String Sql = "AND E301TCR.CODREP='" + rep + "'";
//            buscarRepVen("and rep.CodRep = '" + rep + "'");

            if (chkVendedor.isSelected()) {
                MontarData(jDChooserDataBase.getDate());
                Sql = Sql + " and E301MCR.DatPgt>='" + dataInicialVen + "' and E301MCR.DatPgt<='" + dataFinalVen + "'";
            }
            if (chkRepresentante.isSelected()) {
                MontarData(jDChooserDataBase.getDate());
                Sql = Sql + " and E301MCR.DatPgt>='" + dataInicial + "' and E301MCR.DatPgt<='" + dataFinal + "'";
            }
            if (empresaSel == "17.645.625/0001-03") {
                Sql = Sql + "and USU_EMPCOM = '1'";
            }
            if (empresaSel == "07.564.769/0001-81") {
                Sql = Sql + "and USU_EMPCOM = '2'";
            }
            if (empresaSel == "05.730.180/0001-80") {
                Sql = Sql + "and USU_EMPCOM = '3'";
            }
            if (empresaSel == "GERENCIAL") {
                Sql = Sql + "and USU_EMPCOM = '99'";
            }

            getItensOcs(PESQUISAR_POR, Sql);
//            jTableTitulosMovEmpresaOC.setSelectionMode(linhaSelSit);
        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(" " + ex);
        }

        // TODO add your handling code here:
        // TODO add your handling code here:
        // TODO add your handling code here:
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableTitulosMovEmpresaOCMouseClicked

    private void jTableTitulosMovPercMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTitulosMovPercMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableTitulosMovPercMouseClicked

    private void jTableTitulosMovEmpresaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTitulosMovEmpresaMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableTitulosMovEmpresaMouseClicked

    private void jTableTitulosMovTipoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTitulosMovTipoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableTitulosMovTipoMouseClicked

    private void jTableTitulosMovMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTitulosMovMouseClicked
        try {
            int linhaSelSit = jTableTitulosMov.getSelectedRow();
            int colunaSelSit = jTableTitulosMov.getSelectedColumn();
            txtTitulo.setText(jTableTitulosMov.getValueAt(linhaSelSit, 2).toString());
            txtTipoTitulo.setText(jTableTitulosMov.getValueAt(linhaSelSit, 1).toString());

            if (evt.getClickCount() == 2) {
                filtrarTituloMov(txtTitulo.getText());
                iniciarBarra("", montarPesquisa());
                jTabTitulos.setSelectedIndex(0);

            }
        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTableTitulosMovMouseClicked

    private void jTableTitulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTitulosMouseClicked
        try {
            int linhaSelSit = jTableTitulos.getSelectedRow();
            int colunaSelSit = jTableTitulos.getSelectedColumn();
            txtTitulo.setText(jTableTitulos.getValueAt(linhaSelSit, 7).toString());
            txtTipoTitulo.setText(jTableTitulos.getValueAt(linhaSelSit, 8).toString());

            if (evt.getClickCount() == 2) {
                filtrarTituloMov(txtTitulo.getText());
                jTabTitulos.setSelectedIndex(1);
                //                montarPesquisa();
                iniciarBarra("", montarPesquisa());

            }
        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTableTitulosMouseClicked

    private void jTableTitulosMovEmpresaOcItensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTitulosMovEmpresaOcItensMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableTitulosMovEmpresaOcItensMouseClicked

    private void txtMailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMailActionPerformed

    private void jTabTitulosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTabTitulosKeyPressed

    }//GEN-LAST:event_jTabTitulosKeyPressed

    private void jScrollPane7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane7KeyPressed

    }//GEN-LAST:event_jScrollPane7KeyPressed

    private void jTabTitulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabTitulosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTabTitulosMouseClicked

    private void jTableTitulosMovEmpresaOCKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableTitulosMovEmpresaOCKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableTitulosMovEmpresaOCKeyPressed

    private void jTableTitulosMovEmpresaOCMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTitulosMovEmpresaOCMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableTitulosMovEmpresaOCMousePressed

    private void jTableOrdensGeradasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableOrdensGeradasMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableOrdensGeradasMouseClicked

    private void jTableOrdensGeradasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableOrdensGeradasMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableOrdensGeradasMousePressed

    private void jTableOrdensGeradasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableOrdensGeradasKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableOrdensGeradasKeyPressed

    private void jScrollPane9KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane9KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane9KeyPressed

    private void txtCodForNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodForNovoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodForNovoActionPerformed

    private void btnFechar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechar1ActionPerformed
        try {
            alterarOC();
            iniciarBarra("", montarPesquisa());
        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFechar1ActionPerformed

    private void btnEnviarEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarEmailActionPerformed
        JOptionPane.showMessageDialog(null, "Em desenvolvimento");
    }//GEN-LAST:event_btnEnviarEmailActionPerformed

    private void btnFechar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechar3ActionPerformed
        try {
            excluirOC();
            iniciarBarra("", montarPesquisa());
        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFechar3ActionPerformed

    private void btnReabilitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReabilitarActionPerformed
        try {
            if (!txtRep.getText().isEmpty()) {

                if (btnImprimir.getText().equals("Imprimir")) {
                    reabilitarRegistrosGrid();
                }
            } else {
                Mensagem.mensagem("ERROR", "É Obrigatório preenchimento do Representante ou Vendedor");

            }

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnReabilitarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private static javax.swing.JButton btnEnviarEmail;
    private javax.swing.JButton btnFechar1;
    private javax.swing.JButton btnFechar3;
    private javax.swing.JButton btnGerarOC;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnReabilitar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JRadioButton chkAuto;
    private javax.swing.JRadioButton chkCd;
    private javax.swing.JRadioButton chkFab;
    private javax.swing.JRadioButton chkMoto;
    private javax.swing.JRadioButton chkRepresentante;
    private javax.swing.JRadioButton chkSemComissao;
    private javax.swing.JRadioButton chkVendedor;
    private javax.swing.JRadioButton chkpercentual;
    private org.openswing.swing.client.DateControl dcPagFim;
    private org.openswing.swing.client.DateControl dcPagIni;
    private com.toedter.calendar.JDateChooser jDChooserDataBase;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
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
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JPanel jPMovimento;
    private javax.swing.JPanel jPOCs;
    private javax.swing.JPanel jPTitulo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabTitulos;
    private javax.swing.JTable jTableOrdensGeradas;
    private javax.swing.JTable jTableTitulos;
    private javax.swing.JTable jTableTitulosMov;
    private javax.swing.JTable jTableTitulosMovEmpresa;
    private javax.swing.JTable jTableTitulosMovEmpresaOC;
    private javax.swing.JTable jTableTitulosMovEmpresaOcItens;
    private javax.swing.JTable jTableTitulosMovPerc;
    private javax.swing.JTable jTableTitulosMovTipo;
    private org.openswing.swing.client.LabelControl labelControl1;
    private javax.swing.JLabel lblcomissao;
    private javax.swing.JLabel lblqtdTitulos;
    private javax.swing.JLabel lblqtdTitulosMov;
    private javax.swing.JLabel lblvalorBase;
    private javax.swing.JLabel lblvalorPago;
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.TextControl txtCodForNovo;
    private org.openswing.swing.client.TextControl txtCodGrupo;
    private javax.swing.JComboBox<String> txtFilial;
    private org.openswing.swing.client.TextControl txtGrupo;
    private org.openswing.swing.client.TextControl txtMail;
    private org.openswing.swing.client.TextControl txtNome;
    private org.openswing.swing.client.TextControl txtNota;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.TextControl txtRep;
    private javax.swing.JComboBox<String> txtRepresentante;
    private org.openswing.swing.client.TextControl txtSerie;
    private org.openswing.swing.client.TextControl txtTipoTitulo;
    private org.openswing.swing.client.TextControl txtTitulo;
    private org.openswing.swing.client.TextControl txtperc;
    // End of variables declaration//GEN-END:variables
}
