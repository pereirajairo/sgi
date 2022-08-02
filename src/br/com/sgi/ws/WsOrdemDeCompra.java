/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.ws;

import br.com.sgi.bean.ComissaoTitulosMovimentos;
import br.com.sgi.bean.Cte;
import br.com.sgi.bean.NotaEntrada;
import br.com.sgi.bean.NotaEntradaItem;
import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.bean.UsuarioWs;
import br.com.sgi.bean.WebServiceParametro;
import br.com.sgi.dao.WebServiceParametroDAO;
import br.com.sgi.frame.SucataEcoLancamento;
import br.com.sgi.main.Menu;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author matheus.luiz
 */
public class WsOrdemDeCompra {

    private static List<SucataEcoParametros> listSucataEcoParametros = new ArrayList<SucataEcoParametros>();
    private static List<SucataEcoLancamento> listSucataEcoParametro = new ArrayList<SucataEcoLancamento>();
    private static WebServiceParametro webServiceParametro = new WebServiceParametro();
    private static WebServiceParametroDAO webServiceParametroDAO = new WebServiceParametroDAO();
    private static SucataEcoParametros sucataEcoParametros = new SucataEcoParametros();

    private static String ip = "";
    private static String porta = "";
    private static String wsUsuario;
    private static String wsSenha;

    public UsuarioWs getuUsuarioWs(UsuarioWs usuarioWs) {
        int x = 1;
        return usuarioWs;
    }

    private static void CarregaParametroWebService() throws SQLException {
        UsuarioWs user = Menu.getUsuarioWs();

        webServiceParametro = webServiceParametroDAO.getWebServiceParametro();
        ip = webServiceParametro.getIpWebService();
        porta = webServiceParametro.getPortaWebService();
        wsUsuario = user.getNome();
        wsSenha = user.getSenha();

    }

    public static String ordemDeCompraCancelaSaldoSucataEcoSapiens(NotaEntrada notaEntrada, NotaEntradaItem notaEntradaItem) throws Exception {
        CarregaParametroWebService();

        String requestSoap;
        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ser:GravarOrdensCompra_4>\n"
                + "         <user>" + wsUsuario + "</user>\n"
                + "         <password>" + wsSenha + "</password>\n"
                + "         <encryption>0</encryption>\n"
                + "         <parameters>\n"
                + "            <dadosGerais>     \n"
                + "               <codEmp>" + notaEntrada.getEmpresa() + "</codEmp>\n"
                + "               <codFil>" + notaEntradaItem.getFilial_oc() + "</codFil>\n"
                + "               <codFor>" + notaEntrada.getFornecedor() + "</codFor>\n"
                + "               <numOcp>" + notaEntradaItem.getOrdem_compra() + "</numOcp>\n"
                + "               <tipoProcessamento>3</tipoProcessamento>              \n"
                + "               <salCan>S</salCan>        \n"
                + "            </dadosGerais>\n"
                + "            <tipoProcessamento>3</tipoProcessamento>\n"
                + "          <identificadorSistema>ERBS</identificadorSistema>\n"
                + "         </parameters>\n"
                + "      </ser:GravarOrdensCompra_4>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        System.out.println(requestSoap);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        String url = "http://" + ip + ":" + porta + "/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_cpr_ordemcompra";
        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml");
        System.out.println(url);

        //fim da regiao a ser excluida caso o webservice nao possua a proprieade SOAPAction
        MessageFactory messageFactory = MessageFactory.newInstance();

        SOAPMessage msg = messageFactory.createMessage(headers, (new ByteArrayInputStream(requestSoap.getBytes())));

        SOAPMessage soapResponse = soapConnection.call(msg, url);
        Document xmlRespostaARequisicao = soapResponse.getSOAPBody().getOwnerDocument();
        System.out.println(passarXMLParaStringSapiens(xmlRespostaARequisicao, 4));//imprime na tela o xml de retorno.
        String x = passarXMLParaStringSapiens(xmlRespostaARequisicao, 4);
        int indexArroba = x.indexOf("<mensagemRetorno>");
        int indexEComercial = x.indexOf("</mensagemRetorno>");
        String resultado = x.substring(indexArroba + 17, indexEComercial);
        System.out.println(resultado);
        return resultado;

    }

    public static String ordemDeCompraSucataEcoSapiens(SucataEcoParametros sucataEcoParametros, String fornecedor, Double peso) throws Exception {
        CarregaParametroWebService();

        String requestSoap;
        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ser:GravarOrdensCompra_4>\n"
                + "         <user>" + wsUsuario + "</user>\n"
                + "         <password>" + wsSenha + "</password>\n"
                + "         <encryption>0</encryption>\n"
                + "         <parameters>\n"
                + "            <dadosGerais>\n"
                + "              <camposUsuarioDadosGerais>        \n"
                + "                  <cmpUsu>USU_FILDES</cmpUsu>\n"
                + "                  <vlrUsu>" + sucataEcoParametros.getFilial() + "</vlrUsu>    \n"
                + "               </camposUsuarioDadosGerais>\n"
                + "               <camposUsuarioDadosGerais>                \n"
                + "                  <cmpUsu>USU_EMPDES</cmpUsu>\n"
                + "                  <vlrUsu>" + sucataEcoParametros.getEmpresa() + "</vlrUsu>    \n"
                + "              </camposUsuarioDadosGerais>\n"
                + "              <camposUsuarioDadosGerais>           \n"
                + "                  <cmpUsu>USU_CODNEG</cmpUsu>\n"
                + "                  <vlrUsu>0010</vlrUsu> \n"
                + "               </camposUsuarioDadosGerais>\n"
                + "               <camposUsuarioDadosGerais>           \n"
                + "                  <cmpUsu>USU_OCPDUP</cmpUsu>\n"
                + "                  <vlrUsu>1</vlrUsu>\n"
                + "               </camposUsuarioDadosGerais>                      \n"
                + "               <codCpg>FAV</codCpg>\n"
                + "               <codEmp>" + sucataEcoParametros.getEmpresa() + "</codEmp>\n"
                + "               <codFil>" + sucataEcoParametros.getFilial() + "</codFil>\n"
                + "               <codFor>" + fornecedor + "</codFor>\n"
                + "               <codTra>204</codTra>\n"
                + "               <codFpg>0</codFpg>\n"
                + "               <codMoe>01</codMoe>\n"
                + "               <ideExt>" + sucataEcoParametros.getIdeExt() + "</ideExt>       \n"
                + "               <produtos>\n"
                + "                  <codCcu>" + sucataEcoParametros.getCentroCustos() + "</codCcu>\n"
                + "                  <codDep>" + sucataEcoParametros.getDeposito() + "</codDep>\n"
                + "                  <codPro>" + sucataEcoParametros.getProduto() + "</codPro>\n"
                + "                  <ctaFin>" + sucataEcoParametros.getContaFinanceira() + "</ctaFin>\n"
                + "                  <qtdPed>" + peso + "</qtdPed>\n"
                + "                  <tnsPro>" + sucataEcoParametros.getTransacao() + "</tnsPro>\n"
                + "                  <uniMed>KG</uniMed>\n"
                + "                  <preUni>" + sucataEcoParametros.getValorSucata() + "</preUni>\n"
                + "               </produtos>\n"
                + "               <tnsPro>" + sucataEcoParametros.getTransacao() + "</tnsPro>\n"
                + "            </dadosGerais>\n"
                + "            <fechaOC>1</fechaOC>\n"
                + "            <identificadorSistema>ERBS</identificadorSistema>\n"
                + "            <tipoProcessamento>1</tipoProcessamento>\n"
                + "         </parameters>\n"
                + "      </ser:GravarOrdensCompra_4>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        System.out.println(requestSoap);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
//        
//        ip = "192.168.2.105";
//        porta = "28080";

        String url = "http://" + ip + ":" + porta + "/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_cpr_ordemcompra";
        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml");
        System.out.println(url);

        //fim da regiao a ser excluida caso o webservice nao possua a proprieade SOAPAction
        MessageFactory messageFactory = MessageFactory.newInstance();

        SOAPMessage msg = messageFactory.createMessage(headers, (new ByteArrayInputStream(requestSoap.getBytes())));

        SOAPMessage soapResponse = soapConnection.call(msg, url);
        Document xmlRespostaARequisicao = soapResponse.getSOAPBody().getOwnerDocument();
        System.out.println(passarXMLParaStringSapiens(xmlRespostaARequisicao, 4));//imprime na tela o xml de retorno.
        String x = passarXMLParaStringSapiens(xmlRespostaARequisicao, 4);
        // int indexArroba = x.indexOf("<mensagemRetorno>");
        //int indexEComercial = x.indexOf("</mensagemRetorno>");
        // String resultado = x.substring(indexArroba + 17, indexEComercial);
        System.out.println(x);
        return x;

    }

    public static String ordemDeCompraTransporteSapiens(SucataEcoParametros sucataEcoParametros, List<Cte> lista, Date data, String empresaDes, String filialDes) throws Exception {
        CarregaParametroWebService();

        String tnspro = "";

        String requestSoap;
        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ser:GravarOrdensCompra_4>\n"
                + "         <user>" + wsUsuario + "</user>\n"
                + "         <password>" + wsSenha + "</password>\n"
                + "         <encryption>0</encryption>\n"
                + "         <parameters>\n"
                + "            <dadosGerais>\n"
                + "              <camposUsuarioDadosGerais>        \n"
                + "                  <cmpUsu>USU_FILDES</cmpUsu>\n"
                + "                  <vlrUsu>" + filialDes + "</vlrUsu>    \n"
                + "               </camposUsuarioDadosGerais>\n"
                + "               <camposUsuarioDadosGerais>                \n"
                + "                  <cmpUsu>USU_EMPDES</cmpUsu>\n"
                + "                  <vlrUsu>" + empresaDes + "</vlrUsu>    \n"
                + "              </camposUsuarioDadosGerais>\n"
                + "              <camposUsuarioDadosGerais>           \n"
                + "                  <cmpUsu>USU_CODNEG</cmpUsu>\n"
                + "                  <vlrUsu>0010</vlrUsu> \n"
                + "               </camposUsuarioDadosGerais>\n"
                + "               <camposUsuarioDadosGerais>           \n"
                + "                  <cmpUsu>USU_OCPDUP</cmpUsu>\n"
                + "                  <vlrUsu>1</vlrUsu>\n"
                + "               </camposUsuarioDadosGerais>                      \n"
                + "               <codCpg>" + sucataEcoParametros.getCondicao() + "</codCpg>\n"
                + "               <codEmp>1</codEmp>\n"
                + "               <codFil>1</codFil>\n"
                + "               <codFor>" + sucataEcoParametros.getFornecedor() + "</codFor>\n"
                + "               <tnsSer>" + sucataEcoParametros.getTransacao() + "</tnsSer>\n"
                + "               <tnsPro>" + tnspro + "</tnsPro>\n"
                + "               <obsOcp>" + sucataEcoParametros.getObservacao() + "</obsOcp>\n"
                + "               <datemi>" + data + "</datemi>\n"
                + "               <TemPar>S</TemPar>\n"
                + "               <codFpg>0</codFpg>\n"
                + "               <codMoe>01</codMoe>\n"
                + "               <ideExt>" + sucataEcoParametros.getIdeExt() + "</ideExt>       \n";

        for (Cte cte : lista) {
            requestSoap += "<servicos>\n"
                    + "                  <codCcu>" + sucataEcoParametros.getCentroCustos() + "</codCcu>\n"
                    + "                  <codSer>" + cte.getUsu_codser() + "</codSer>\n"
                    + "                  <cplIso>" + cte.getUsu_cplocp() + "</cplIso>\n"
                    + "                  <ctaFin>" + sucataEcoParametros.getContaFinanceira() + "</ctaFin>\n"
                    + "                  <qtdPed>1</qtdPed>\n"
                    + "                  <tnsSer>" + sucataEcoParametros.getTransacao() + "</tnsSer>\n"
                    + "                  <tnsPro>" + tnspro + "</tnsPro>\n"
                    + "                  <preUni>" + cte.getUsu_valcte() + "</preUni>\n"
                    + "                  <datent>" + data + "</datent>\n"
                    + "               </servicos>\n";
        }

        requestSoap += "       <parcelas>\n"
                + "                <seqPar>1</seqPar>\n"
                + "                <codFcr>01</codFcr>\n"
                //  + "                <datFcr>DateTime</datFcr>\n"
                + "                <diaPar>15</diaPar>\n"
                //  + "                <vctPar>DateTime</vctPar>\n"
                + "                <perPar>100</perPar>\n"
                + "                <vlrPar>0</vlrPar>\n"
                + "                <dscPar>0</dscPar>\n"
                //  + "                <obsPar>String</obsPar>\n"
                //   + "                <indPag>String</indPag>\n"
                + "           </parcelas>"
                + "           </dadosGerais>\n"
                + "            <fechaOC>2</fechaOC>\n"
                + "            <identificadorSistema>ERBS</identificadorSistema>\n"
                + "            <tipoProcessamento>1</tipoProcessamento>\n"
                + "         </parameters>\n"
                + "      </ser:GravarOrdensCompra_4>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        System.out.println(requestSoap);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
//        ip = "192.168.2.106";
        //  ip = "192.168.2.106";
        //  porta = "18080";
        String url = "http://" + ip + ":" + porta + "/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_cpr_ordemcompra";
        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml");
        System.out.println(url);

        //fim da regiao a ser excluida caso o webservice nao possua a proprieade SOAPAction
        MessageFactory messageFactory = MessageFactory.newInstance();

        SOAPMessage msg = messageFactory.createMessage(headers, (new ByteArrayInputStream(requestSoap.getBytes())));

        SOAPMessage soapResponse = soapConnection.call(msg, url);
        Document xmlRespostaARequisicao = soapResponse.getSOAPBody().getOwnerDocument();
        System.out.println(passarXMLParaStringSapiens(xmlRespostaARequisicao, 4));//imprime na tela o xml de retorno.
        String x = passarXMLParaStringSapiens(xmlRespostaARequisicao, 4);
        // int indexArroba = x.indexOf("<mensagemRetorno>");
        //int indexEComercial = x.indexOf("</mensagemRetorno>");
        // String resultado = x.substring(indexArroba + 17, indexEComercial);
        System.out.println(x);
        return x;

    }

    public static String passarXMLParaStringSapiens(Document xml, int espacosIdentacao) {
        try {
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            //   transfac.setAttribute("indent-number", new Integer(espacosIdentacao));
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource((Node) xml);
            trans.transform(source, result);
            String xmlString = sw.toString();
            return xmlString;
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public static String ordemDeCompraSapiensComissao(ComissaoTitulosMovimentos comissaoTitulosMovimentos, List<ComissaoTitulosMovimentos> lstTitulosOcsItens, String fornecedor, Double valor) throws Exception {
        CarregaParametroWebService();

        wsUsuario = "ProcAuto";
        wsSenha = "3n3rg14";
        String wtnsser = "90408";

        String requestSoap;
        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ser:GravarOrdensCompra_4>\n"
                + "         <user>" + wsUsuario + "</user>\n"
                + "         <password>" + wsSenha + "</password>\n"
                + "         <encryption>0</encryption>\n"
                + "         <parameters>\n"
                + "            <dadosGerais>\n"
                + "              <camposUsuarioDadosGerais>        \n"
                + "                  <cmpUsu>USU_FILDES</cmpUsu>\n"
                + "                  <vlrUsu>1</vlrUsu>    \n"
                + "               </camposUsuarioDadosGerais>\n"
                + "               <camposUsuarioDadosGerais>                \n"
                + "                  <cmpUsu>USU_EMPDES</cmpUsu>\n"
                + "                  <vlrUsu>1</vlrUsu>    \n"
                + "              </camposUsuarioDadosGerais>\n"
                + "              <camposUsuarioDadosGerais>           \n"
                + "                  <cmpUsu>USU_CODNEG</cmpUsu>\n"
                + "                  <vlrUsu>0010</vlrUsu> \n"
                + "               </camposUsuarioDadosGerais>\n"
                + "               <camposUsuarioDadosGerais>           \n"
                + "                  <cmpUsu>USU_OCPDUP</cmpUsu>\n"
                + "                  <vlrUsu>1</vlrUsu>\n"
                + "               </camposUsuarioDadosGerais>                      \n"
                + "               <codCpg>" + comissaoTitulosMovimentos.getCondPagOc() + "</codCpg>\n"
                + "               <codEmp>1</codEmp>\n"
                + "               <codFil>1</codFil>\n"
                + "               <codFor>" + comissaoTitulosMovimentos.getFornecedor() + "</codFor>\n"
                + "               <tnsSer>" + wtnsser + "</tnsSer>\n"
                + "               <obsOcp>Pagamento de Comissao</obsOcp>\n"
                + "               <codFpg>1</codFpg>\n"
                + "               <codMoe>01</codMoe>\n"
                + "               <ideExt>" + comissaoTitulosMovimentos.getIdeExt() + "</ideExt> \n";

        for (ComissaoTitulosMovimentos ser : lstTitulosOcsItens) {
            requestSoap += "<servicos>\n"
                    + "                  <codCcu>68</codCcu>\n"
                    + "                  <codSer>" + ser.getServico() + "</codSer>\n"
                    + "                  <cplIso>" + ser.getComplementoServ() + "</cplIso>\n"
                    + "                  <ctaFin>1456</ctaFin>\n"
                    + "                  <qtdPed>1</qtdPed>\n"
                    + "                  <tnsSer>" + wtnsser + "</tnsSer>\n"
                    + "                  <preUni>" + ser.getValorComissaoItem() + "</preUni>\n"
                    + "               </servicos>\n";
        }
        requestSoap += "            </dadosGerais>\n"
                + "            <fechaOC>2</fechaOC>\n"
                + "            <identificadorSistema>ERBS</identificadorSistema>\n"
                + "            <tipoProcessamento>1</tipoProcessamento>\n"
                + "         </parameters>\n"
                + "      </ser:GravarOrdensCompra_4>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        System.out.println(requestSoap);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        ip = "192.168.2.193";
        porta = "18080";
        String url = "http://" + ip + ":" + porta + "/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_cpr_ordemcompra";
        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml");
        System.out.println(url);

        //fim da regiao a ser excluida caso o webservice nao possua a proprieade SOAPAction
        MessageFactory messageFactory = MessageFactory.newInstance();

        SOAPMessage msg = messageFactory.createMessage(headers, (new ByteArrayInputStream(requestSoap.getBytes())));

        SOAPMessage soapResponse = soapConnection.call(msg, url);
        Document xmlRespostaARequisicao = soapResponse.getSOAPBody().getOwnerDocument();
        System.out.println(passarXMLParaStringSapiens(xmlRespostaARequisicao, 4));//imprime na tela o xml de retorno.
        String x = passarXMLParaStringSapiens(xmlRespostaARequisicao, 4);
        System.out.println(x);
        return x;
    }

    public static String testeSapiens(SucataEcoParametros sucataEcoParametros, String fornecedor, Double peso) throws Exception {
        CarregaParametroWebService();

        String requestSoap;
        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ser:GravarOrdensCompra_4>\n"
                + "         <user>ProcAuto</user>\n"
                + "         <password>3n3rg14</password>\n"
                + "         <encryption>0</encryption>\n"
                + "         <parameters>\n"
                + "            <dadosGerais>\n"
                + "              <camposUsuarioDadosGerais>        \n"
                + "                  <cmpUsu>USU_FILDES</cmpUsu>\n"
                + "                  <vlrUsu>1</vlrUsu>    \n"
                + "               </camposUsuarioDadosGerais>\n"
                + "               <camposUsuarioDadosGerais>                \n"
                + "                  <cmpUsu>USU_EMPDES</cmpUsu>\n"
                + "                  <vlrUsu>1</vlrUsu>    \n"
                + "              </camposUsuarioDadosGerais>\n"
                + "              <camposUsuarioDadosGerais>           \n"
                + "                  <cmpUsu>USU_CODNEG</cmpUsu>\n"
                + "                  <vlrUsu>0010</vlrUsu> \n"
                + "               </camposUsuarioDadosGerais>\n"
                + "               <camposUsuarioDadosGerais>           \n"
                + "                  <cmpUsu>USU_OCPDUP</cmpUsu>\n"
                + "                  <vlrUsu>1</vlrUsu>\n"
                + "               </camposUsuarioDadosGerais>                      \n"
                + "               <codCpg>D15</codCpg>\n"
                + "               <codEmp>1</codEmp>\n"
                + "               <codFil>1</codFil>\n"
                + "               <codFor>5005</codFor>\n"
                + "               <tnsSer>90408</tnsSer>\n"
                + "               <obsOcp>Pagamento de Comissao</obsOcp>\n"
                + "               <codFpg>0</codFpg>\n"
                + "               <codMoe>01</codMoe>\n"
                + "               <ideExt>111141</ideExt> \n"
                + "               <servicos>\n"
                + "                  <codCcu>68</codCcu>\n"
                + "                  <codSer>SVSVE0001</codSer>\n"
                + "                  <cplIso>COMISSAO SOBRE VENDAS - MOTO</cplIso>\n"
                + "                  <ctaFin>1456</ctaFin>\n"
                + "                  <qtdPed>1</qtdPed>\n"
                + "                  <tnsSer>90408</tnsSer>\n"
                + "                  <preUni>5719.2</preUni>\n"
                + "               </servicos>\n"
                + "            </dadosGerais>\n"
                + "            <fechaOC>2</fechaOC>\n"
                + "            <identificadorSistema>ERBS</identificadorSistema>\n"
                + "            <tipoProcessamento>1</tipoProcessamento>\n"
                + "         </parameters>\n"
                + "      </ser:GravarOrdensCompra_4>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        System.out.println(requestSoap);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
//        
//        ip = "192.168.2.105";
//       porta = "28080";

        String url = "http://" + ip + ":" + porta + "/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_cpr_ordemcompra";
        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml");
        System.out.println(url);

        //fim da regiao a ser excluida caso o webservice nao possua a proprieade SOAPAction
        MessageFactory messageFactory = MessageFactory.newInstance();

        SOAPMessage msg = messageFactory.createMessage(headers, (new ByteArrayInputStream(requestSoap.getBytes())));

        SOAPMessage soapResponse = soapConnection.call(msg, url);
        Document xmlRespostaARequisicao = soapResponse.getSOAPBody().getOwnerDocument();
        System.out.println(passarXMLParaStringSapiens(xmlRespostaARequisicao, 4));//imprime na tela o xml de retorno.
        String x = passarXMLParaStringSapiens(xmlRespostaARequisicao, 4);
        // int indexArroba = x.indexOf("<mensagemRetorno>");
        //int indexEComercial = x.indexOf("</mensagemRetorno>");
        // String resultado = x.substring(indexArroba + 17, indexEComercial);
        System.out.println(x);
        return x;

    }
}
