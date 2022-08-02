/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.ws;

import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.bean.UsuarioWs;
import br.com.sgi.bean.WebServiceParametro;
import br.com.sgi.dao.WebServiceParametroDAO;
import br.com.sgi.main.Menu;
import static br.com.sgi.util.FormatarNumeros.converterDoubleString;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.sql.SQLException;
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
public class WsEstoque {

    private static WebServiceParametro webServiceParametro = new WebServiceParametro();
    private static WebServiceParametroDAO webServiceParametroDAO = new WebServiceParametroDAO();

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

    public static String estoqueMovimentoSucataPDVSapiens(SucataEcoParametros sucataEcoParametros, Double peso, String nota, String serie) throws Exception {
        CarregaParametroWebService();

        double valorMovimento = peso * sucataEcoParametros.getValorSucata();
        String valorFormat = converterDoubleString(valorMovimento);
        String valor = valorFormat.replace(".", ",");

        String pesoFormat = converterDoubleString(peso);
        String peso2 = pesoFormat.replace(".", ",");

        String requestSoap;
        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ser:MovimentarEstoque>\n"
                + "         <user>" + wsUsuario + "</user>\n"
                + "         <password>" + wsSenha + "</password>\n"
                + "         <encryption>0</encryption>\n"
                + "         <parameters>\n"
                + "            <dadosGerais>\n"
                + "               <codDep>" + sucataEcoParametros.getDeposito() + "</codDep>\n"
                + "               <codDer></codDer>\n"
                + "               <codEmp>" + sucataEcoParametros.getEmpresa() + "</codEmp>\n"
                + "               <codFil>" + sucataEcoParametros.getFilial() + "</codFil>\n"
                + "               <codPro>" + sucataEcoParametros.getProduto() + "</codPro>\n"
                + "               <codTns>90237</codTns>\n"
                + "               <ctaFin>0</ctaFin>\n"
                + "               <motMvp>Movimento Entrada sucata ECO do PDV NFC-e " + nota + " </motMvp>\n"
                + "               <numDoc>" + nota + "</numDoc>\n"
                + "               <qtdMov>" + peso2+ "</qtdMov>\n"
                + "               <vlrMov>" + valor + "</vlrMov>\n"
                + "               <codSnf>"+serie+"</codSnf>\n" 
                + "               <numNfv>"+nota+"</numNfv>\n" 
              //  + "               <sqlIpv>1</sqlIpv>\n" 
                + "            </dadosGerais>\n"
                + "         </parameters>\n"
                + "      </ser:MovimentarEstoque>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        System.out.println(requestSoap);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        String url = "http://" + ip + ":" + porta + "/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_est_estoques";
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
        int indexOK = x.indexOf("<mensagemRetorno>");
        int indexOKFim = x.indexOf("</mensagemRetorno>");
        String resultado = " ";
        if (indexOK > 0) {
            resultado = x.substring(indexOK + 17, indexOKFim);
        }

        int indexErro = x.indexOf("<erroExecucao>");
        int indexErroFim = x.indexOf("</erroExecucao>");
        if (indexErro > 0) {
            resultado = x.substring(indexErro + 14, indexErroFim);
        }
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
