/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.ws;

import br.com.sgi.bean.NotaEntrada;
import br.com.sgi.bean.NotaEntradaItem;
import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.bean.Usuario;
import br.com.sgi.bean.UsuarioWs;
import br.com.sgi.bean.WebServiceParametro;
import br.com.sgi.dao.WebServiceParametroDAO;
import br.com.sgi.frame.SucataEcoLancamento;
import br.com.sgi.main.Menu;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
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
public class WsNotaEntrada {

    private static List<SucataEcoLancamento> listSucataEcoParametro = new ArrayList<SucataEcoLancamento>();
    private static WebServiceParametro webServiceParametro = new WebServiceParametro();
    private static WebServiceParametroDAO webServiceParametroDAO = new WebServiceParametroDAO();

    private static String ip = " ";
    private static String porta = " ";
    private static String wsUsuario;
    private static String wsSenha;

    public UsuarioWs getuUsuarioWs(UsuarioWs usuarioWs) {
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

    public static String NotaEntradaSucataEcoSapiens(NotaEntrada notaEntrada, NotaEntradaItem notaEntradaItem) throws Exception {
        CarregaParametroWebService();

        String requestSoap;
        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ser:GravarNotasFiscaisEntrada>\n"
                + "         <user>" + wsUsuario + "</user>\n"
                + "         <password>" + wsSenha + "</password>\n "
                + "         <encryption>0</encryption>\n "
                + "         <parameters>\n "
                + "             <dadosGerais> \n"
                + "               <codEmp>" + notaEntrada.getEmpresa() + "</codEmp>\n"
                + "               <codFil>" + notaEntrada.getFilial() + "</codFil>\n"
                + "               <codFor>" + notaEntrada.getFornecedor() + "</codFor>\n"
                + "               <codSnf>" + notaEntrada.getSerie() + "</codSnf>\n"
                + "               <numNfc>" + notaEntrada.getNota() + "</numNfc>\n"
                + "               <tipNfe>1</tipNfe>\n"
                + "               <tnsPro>" + notaEntrada.getTrancacao() + "</tnsPro>\n"
                + "               <vlrInf>" + notaEntrada.getValorNota() + "</vlrInf>\n"
                + "               <produtos>\n"
                + "                  <codPro>" + notaEntradaItem.getProduto() + "</codPro>\n"
                + "                  <tnsPro>" + notaEntradaItem.getTrancacao() + "</tnsPro> \n"
                + "                  <qtdRec>" + notaEntradaItem.getQuantidade_recebida() + "</qtdRec> \n"
                + "                  <preUni>" + notaEntradaItem.getValorSucata() + "</preUni> \n"
                + "                  <filOcp>" + notaEntradaItem.getFilial_oc() + "</filOcp> \n"
                + "                  <numOcp>" + notaEntradaItem.getOrdem_compra() + "</numOcp>\n"
                + "                  <seqIpo>" + notaEntradaItem.getSeqIpo() + "</seqIpo> \n"
                + "                  <codDep>" + notaEntradaItem.getDeposito() + "</codDep>\n"
                + "                  <codCcu>" + notaEntradaItem.getCentroCusto() + "</codCcu>\n"
                + "                  <ctaFin>" + notaEntradaItem.getContaFinanceira() + "</ctaFin>\n"
                + "               </produtos>\n"
                + "             </dadosGerais>\n"
                + "             <tipoProcessamento>1</tipoProcessamento>\n"
                + "         </parameters>\n"
                + "      </ser:GravarNotasFiscaisEntrada>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        System.out.println(requestSoap);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        String url = "http://" + ip + ":" + porta + "/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_cpr_notafiscal";//url do webservice nao e a url do wsdl do webservice, repare que isto foi copia da parte vermelha da figura 1
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
        int indexArroba = x.indexOf("<retorno>");
        int indexEComercial = x.indexOf("</retorno>");
        String resultado = x.substring(indexArroba + 9, indexEComercial);
        System.out.println(resultado);
        return resultado;

    }

    public static String NotaEntradaExcluir(NotaEntrada notaEntrada) throws SOAPException, IOException, SQLException {
        CarregaParametroWebService();
        String requestSoap;
        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ser:ReabilitaCancelaNota>\n"
                + "         <user>" + wsUsuario + "</user>\n"
                + "         <password>" + wsSenha + "</password>\n"
                + "         <encryption>0</encryption>\n"
                + "         <parameters>\n"
                + "            <notaFiscal>\n"
                + "               <codEmp>" + notaEntrada.getEmpresa() + "</codEmp>\n"
                + "               <codFil>" + notaEntrada.getFilial() + "</codFil>\n"
                + "               <codFor>" + notaEntrada.getFornecedor() + "</codFor>\n"
                + "               <codSnf>SUC</codSnf>\n"
                + "               <numNfc>" + notaEntrada.getNota() + "</numNfc>\n"
                + "               <tipoOperacao>C</tipoOperacao>\n"
                + "            </notaFiscal>\n"
                + "         </parameters>\n"
                + "      </ser:ReabilitaCancelaNota>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        System.out.println(requestSoap);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        String url = "http://" + ip + ":" + porta + "/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_cpr_notafiscal";//url do webservice nao e a url do wsdl do webservice, repare que isto foi copia da parte vermelha da figura 1
        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml");

        //fim da regiao a ser excluida caso o webservice nao possua a proprieade SOAPAction
        MessageFactory messageFactory = MessageFactory.newInstance();

        SOAPMessage msg = messageFactory.createMessage(headers, (new ByteArrayInputStream(requestSoap.getBytes())));

        SOAPMessage soapResponse = soapConnection.call(msg, url);
        Document xmlRespostaARequisicao = soapResponse.getSOAPBody().getOwnerDocument();
        System.out.println(passarXMLParaStringSapiens(xmlRespostaARequisicao, 4));//imprime na tela o xml de retorno.
        String x = passarXMLParaStringSapiens(xmlRespostaARequisicao, 4);
        int indexArroba = x.indexOf("<msgRet>");
        int indexEComercial = x.indexOf("</msgRet>");
        String resultado = x.substring(indexArroba + 8, indexEComercial);
        System.out.println(resultado);
        return resultado;
    }

    public static String passarXMLParaStringSapiens(Document xml, int espacosIdentacao) {
        try {
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            transfac.setAttribute("indent-number", new Integer(espacosIdentacao));
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

}
