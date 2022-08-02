package br.com.sgi.main;

import br.com.sgi.frame.BalancinhaConsulta;
import br.com.sgi.frame.CRMAgendas;
import br.com.sgi.frame.CRMClientes;
import br.com.sgi.frame.CRMClientesGeral;
import br.com.sgi.frame.CRMConta;
import br.com.sgi.frame.CadastroCaixa;
import br.com.sgi.frame.CadastroFuncionario;
import br.com.sgi.frame.CadastroTela;
import br.com.sgi.frame.Comissao;
import br.com.sgi.frame.FatPedido;
import br.com.sgi.frame.SucataConsultaEco;
import br.com.sgi.frame.SucataConsultaPDV;
import br.com.sgi.frame.IntegrarPesos;
import br.com.sgi.frame.frmMinutas;
import br.com.sgi.frame.LogPedido;
import br.com.sgi.frame.Parametros;
import br.com.sgi.frame.SucataTriagem;
import br.com.sgi.frame.SucataContaCorrente;
import br.com.sgi.frame.SucataContaCorrenteIndustrializacao;
import br.com.sgi.frame.Sucatas;
import br.com.sgi.frame.VinculoGrupoTela;
import br.com.sgi.frame.CteXml;
import br.com.sgi.frame.FatPedidoExpedicao;
import br.com.sgi.frame.FatPedidoExpedicaoMetais;
import br.com.sgi.frame.FatPedidoFaturar;
import br.com.sgi.frame.FatPedidoHub;
import br.com.sgi.frame.FatPedidoReport;
import br.com.sgi.frame.FrmGarantiaNovo;
import br.com.sgi.frame.FrmHubTransferencia;
import br.com.sgi.frame.SucataTriagemEntrada;
import br.com.sgi.frame.SucataTriagemMinuta;
import br.com.sgi.frame.frmMinutasExpedicao;
import br.com.sgi.frame.frmMinutasHubGerar;
import java.beans.PropertyVetoException;
import org.openswing.swing.mdi.client.*;
import java.sql.Connection;

public class ClienteFachada implements ClientFacade {

    private Connection conn = null;

    public ClienteFachada(Connection conn) {
        this.conn = conn;
    }

    public void getGarantia() throws PropertyVetoException, Exception {
        FrmGarantiaNovo sol = new FrmGarantiaNovo();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true);

    }
    
       public void getHubTransferencia() throws PropertyVetoException, Exception {
        FrmHubTransferencia sol = new FrmHubTransferencia();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true);

    }


    public void getComissao() throws PropertyVetoException, Exception {
        Comissao sol = new Comissao();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true);

    }

    public void getParamentros() throws PropertyVetoException, Exception {
        Parametros sol = new Parametros();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true);

    }

    public void getXmlCte() throws PropertyVetoException, Exception {
        CteXml sol = new CteXml();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true);

    }

    public void getAgendas() throws PropertyVetoException, Exception {
        CRMAgendas sol = new CRMAgendas();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 
    }

    public void getCadastroCaixa() throws PropertyVetoException, Exception {
        CadastroCaixa sol = new CadastroCaixa();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getCadastroFuncionario() throws PropertyVetoException, Exception {
        CadastroFuncionario sol = new CadastroFuncionario();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getVinculoTelaGrupo() throws PropertyVetoException, Exception {
        VinculoGrupoTela sol = new VinculoGrupoTela();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getCadastroTela() throws PropertyVetoException, Exception {
        CadastroTela sol = new CadastroTela();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 
        //JOptionPane.showMessageDialog(null, "Em desenvolvimento");

    }

    public void getLancementoSucataEco() throws PropertyVetoException, Exception {
        SucataConsultaEco sol = new SucataConsultaEco();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 
        //JOptionPane.showMessageDialog(null, "Em desenvolvimento");

    }

    public void getContas() throws PropertyVetoException, Exception {
        CRMConta sol = new CRMConta();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getClientesGeral() throws PropertyVetoException, Exception {
        CRMClientesGeral sol = new CRMClientesGeral();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getClientes() throws PropertyVetoException, Exception {
        CRMClientes sol = new CRMClientes();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getSucata() throws PropertyVetoException, Exception {
        Sucatas sol = new Sucatas();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getSucataTriagemMinuta() throws PropertyVetoException, Exception {
        SucataTriagemMinuta sol = new SucataTriagemMinuta();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getSucataAnalises() throws PropertyVetoException, Exception {
        SucataTriagem sol = new SucataTriagem();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getSucataAnalisesEntrada() throws PropertyVetoException, Exception {
        SucataTriagemEntrada sol = new SucataTriagemEntrada();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getSucataContaCorrente() throws PropertyVetoException, Exception {
        SucataContaCorrente sol = new SucataContaCorrente();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getSucataContaCorrenteIndustrializacao() throws PropertyVetoException, Exception {
        SucataContaCorrenteIndustrializacao sol = new SucataContaCorrenteIndustrializacao();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getBalancinha() throws PropertyVetoException, Exception {
        BalancinhaConsulta sol = new BalancinhaConsulta();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getMinuta() throws PropertyVetoException, Exception {
        frmMinutas sol = new frmMinutas();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getMinutaHub() throws PropertyVetoException, Exception {
        frmMinutasHubGerar sol = new frmMinutasHubGerar();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getPedidoReport() throws PropertyVetoException, Exception {
        FatPedidoReport sol = new FatPedidoReport();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getPedido() throws PropertyVetoException, Exception {
        LogPedido sol = new LogPedido();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getPedidoFaturamentoHub() throws PropertyVetoException, Exception {
        FatPedidoHub sol = new FatPedidoHub();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getFatPedidoExpedicao() throws PropertyVetoException, Exception {
        FatPedidoExpedicao sol = new FatPedidoExpedicao();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getFatPedidoFaturar() throws PropertyVetoException, Exception {
        FatPedidoFaturar sol = new FatPedidoFaturar();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getFrmMinutasExpedicao() throws PropertyVetoException, Exception {
        frmMinutasExpedicao sol = new frmMinutasExpedicao();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getFrmMinutasExpedicaoMetais() throws PropertyVetoException, Exception {
        FatPedidoExpedicaoMetais sol = new FatPedidoExpedicaoMetais();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getPedidoFaturamento() throws PropertyVetoException, Exception {
        FatPedido sol = new FatPedido();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getIntegrarPesos() throws PropertyVetoException, Exception {
        IntegrarPesos sol = new IntegrarPesos();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }

    public void getConsultaSucataPDV() throws PropertyVetoException, Exception {
        SucataConsultaPDV sol = new SucataConsultaPDV();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 

    }
}
